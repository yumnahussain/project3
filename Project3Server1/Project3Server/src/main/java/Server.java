import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	private int count = 1;
	private final ArrayList<ClientThread> clients = new ArrayList<>();
	private final HashMap<String, String> userDatabase = new HashMap<>();
	private final Consumer<Message> callback;
	private final GameMaster gameMaster = new GameMaster();


	Server(Consumer<Message> call){

		callback = call;
		new TheServer().start();
	}


	private class TheServer extends Thread{

	public void run() {

			try(ServerSocket mysocket = new ServerSocket(5555);){
				System.out.println("Server is waiting for a client!");


				while(true) {

					Socket clientSocket = mysocket.accept();
					ClientThread c = new ClientThread(clientSocket);
					gameMaster.addWaitingPlayer(c);
					c.start();

					count++;

				}
			}//end of try
			catch(Exception e) {
				callback.accept(new Message(Message.MessageType.TEXT, "Server failed to start: " + e.getMessage()));
			}
		}//end of while
	}


	private class GameBoard {
		public static final int COLUMNS = 7;
		public static final int ROWS = 6;
		private final int[][] grid = new int[ROWS][COLUMNS];

		public boolean makeMove(int col, int player) {
			if (col < 0 || col >= COLUMNS) return false;
			for (int row = ROWS - 1; row >= 0; row--) {
				if (grid[row][col] == 0) {
					grid[row][col] = player;
					return true;
				}
			}
			return false;
		}

		public boolean checkWin() {
			// Check horizontal
			for (int row = 0; row < ROWS; row++) {
				for (int col = 0; col < COLUMNS - 3; col++) {
					int player = grid[row][col];
					if (player != 0 && player == grid[row][col + 1] && 
						player == grid[row][col + 2] && player == grid[row][col + 3]) {
						return true;
					}
				}
			}
			// Check vertical
			for (int row = 0; row < ROWS - 3; row++) {
				for (int col = 0; col < COLUMNS; col++) {
					int player = grid[row][col];
					if (player != 0 && player == grid[row + 1][col] && 
						player == grid[row + 2][col] && player == grid[row + 3][col]) {
						return true;
					}
				}
			}
			// Check diagonal (down-right)
			for (int row = 0; row < ROWS - 3; row++) {
				for (int col = 0; col < COLUMNS - 3; col++) {
					int player = grid[row][col];
					if (player != 0 && player == grid[row + 1][col + 1] && 
						player == grid[row + 2][col + 2] && player == grid[row + 3][col + 3]) {
						return true;
					}
				}
			}
			// Check diagonal (up-right)
			for (int row = 3; row < ROWS; row++) {
				for (int col = 0; col < COLUMNS - 3; col++) {
					int player = grid[row][col];
					if (player != 0 && player == grid[row - 1][col + 1] && 
						player == grid[row - 2][col + 2] && player == grid[row - 3][col + 3]) {
						return true;
					}
				}
			}
			return false;
		}

		public int[][] getGrid() {
			return grid;
		}
	}

	private class GameSession {
		private final GameBoard board = new GameBoard();
		private final ClientThread player1, player2;
		private int currentPlayer = 1;
		private final HashMap<ClientThread, Integer> playerNumbers = new HashMap<>();

		public GameSession(ClientThread p1, ClientThread p2) {
			this.player1 = p1;
			this.player2 = p2;
			playerNumbers.put(p1, 1);
			playerNumbers.put(p2, 2);
		}

		public synchronized void handleMove(int column, ClientThread from) throws Exception {
			int playerNum = playerNumbers.get(from);
			System.out.println("Player " + playerNum + " making move in column " + column);
			
			if (playerNum != currentPlayer || !board.makeMove(column, playerNum)) {
				System.out.println("Invalid move attempted");
				from.out.writeObject(new Message(Message.MessageType.INVALID_MOVE, "Invalid move"));
				return;
			}

			// Get the current board state
			int[][] grid = board.getGrid();
			System.out.println("Current board state:");
			for (int row = 0; row < 6; row++) {
				for (int col = 0; col < 7; col++) {
					System.out.print(grid[row][col] + " ");
				}
				System.out.println();
			}

			// Create and send update message to both players
			Message update = new Message(Message.MessageType.GAME_UPDATE, grid, currentPlayer);
			
			// Send to player 1
			try {
				player1.out.reset(); // Clear any cached objects
				player1.out.writeObject(update);
				System.out.println("Sent update to player 1: " + player1.username);
			} catch (Exception e) {
				System.out.println("Error sending to player 1: " + e.getMessage());
			}

			// Send to player 2
			try {
				player2.out.reset(); // Clear any cached objects
				player2.out.writeObject(update);
				System.out.println("Sent update to player 2: " + player2.username);
			} catch (Exception e) {
				System.out.println("Error sending to player 2: " + e.getMessage());
			}

			// Check for win
			if (board.checkWin()) {
				Message win = new Message(Message.MessageType.GAME_OVER, currentPlayer);
				// Send to player 1
				try {
					player1.out.reset(); // Clear any cached objects
					player1.out.writeObject(win);
					System.out.println("Sent win update to player 1: " + player1.username);
				} catch (Exception e) {
					System.out.println("Error sending to player 1: " + e.getMessage());
				}

				// Send to player 2
				try {
					player2.out.reset(); // Clear any cached objects
					player2.out.writeObject(win);
					System.out.println("Sent win update to player 2: " + player2.username);
				} catch (Exception e) {
					System.out.println("Error sending to player 2: " + e.getMessage());
				}

				return;
			}

			// Switch turn
			currentPlayer = 3 - currentPlayer;
			System.out.println("Switching to player " + currentPlayer);
		}
	}

	private class GameMaster {
		private final ArrayList<GameSession> activeGames = new ArrayList<>();
		private final Queue<ClientThread> waitingQueue = new LinkedList<>();

		public synchronized void addWaitingPlayer(ClientThread newPlayer) {
			// Remove player from any existing game sessions
			for (GameSession game : activeGames) {
				if (game.player1 == newPlayer || game.player2 == newPlayer) {
					activeGames.remove(game);
					break;
				}
			}
			
			// Add player to waiting queue
			waitingQueue.add(newPlayer);
			System.out.println("Player " + newPlayer.username + " added to waiting queue");
			pairPlayers();
		}

		private void pairPlayers() {
			while (waitingQueue.size() >= 2) {
				ClientThread p1 = waitingQueue.poll();
				ClientThread p2 = waitingQueue.poll();
				GameSession session = new GameSession(p1, p2);
				activeGames.add(session);
				p1.currentGame = session;
				p2.currentGame = session;

				try {
					p1.out.reset();
					p1.out.writeObject(new Message(Message.MessageType.GAME_START, 1));
					System.out.println("Sent start update to player 1: " + p1.username);
				} catch (Exception e) {
					System.out.println("Error sending to player 1: " + e.getMessage());
				}

				try {
					p2.out.reset();
					p2.out.writeObject(new Message(Message.MessageType.GAME_START, 2));
					System.out.println("Sent start update to player 2: " + p2.username);
				} catch (Exception e) {
					System.out.println("Error sending to player 2: " + e.getMessage());
				}
			}
		}
	}

	private class ClientThread extends Thread{

		private final Socket socket;
		private ObjectInputStream in;
		private ObjectOutputStream out;
		private String username;
		private GameSession currentGame;

		ClientThread(Socket s){
			this.socket = s;
		}

		public void run(){

			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				socket.setTcpNoDelay(true);
				clients.add(this);
				while(true) {
					Message data = (Message) in.readObject();
					switch(data.type){
						case SIGNUP:
							handleSignup(data);
							break;
						case LOGIN:
							handleLogin(data);
							break;
						case TEXT:
							forwardText(data);
							break;
						case DISCONNECT:
							handleDisconnect();
							return;
						case GAME_MOVE:
							if (currentGame != null) {
								currentGame.handleMove(data.column, this);
							}
							break;
						case REQUEST_GAME:
							gameMaster.addWaitingPlayer(this);
							break;
						case GAME_START:
							// This is handled by the GameMaster when pairing players
							break;
						case GAME_UPDATE:
							// This is handled by the GameSession when making moves
							break;
						case GAME_OVER:
							// This is handled by the GameSession when checking for wins
							break;
						case INVALID_MOVE:
							// This is handled by the GameSession when moves are invalid
							break;
						case UPDATEUSERS:
							broadcastUpdateUsers();
							break;
						case NEWUSER:
							callback.accept(new Message(Message.MessageType.NEWUSER, username));
							broadcastUpdateUsers();
							break;
						case SIGNUP_RESPONSE:
							// This is handled by the handleSignup method
							break;
						case LOGIN_RESPONSE:
							// This is handled by the handleLogin method
							break;
						default:
							break;
					}
				}
			}
			catch(Exception e) {
				handleDisconnect();
			}
		}//end of run

		private void handleSignup(Message msg) throws Exception {
			boolean success;
			synchronized (userDatabase) {
				success = !userDatabase.containsKey(msg.username);
				if (success) {
					userDatabase.put(msg.username, msg.password);
				}
			}
			if (success) {
				out.writeObject(new Message(Message.MessageType.SIGNUP_RESPONSE, "SUCCESS"));
			} else {
				out.writeObject(new Message(Message.MessageType.SIGNUP_RESPONSE, "USERNAME_TAKEN"));
			}
		}

		private void handleLogin(Message msg) throws Exception {
			boolean valid;
			synchronized (userDatabase) {
				valid = userDatabase.containsKey(msg.username)
						&& userDatabase.get(msg.username).equals(msg.password);
			}
			if (valid) {
				out.writeObject(new Message(Message.MessageType.LOGIN_RESPONSE, "SUCCESS"));
			} else {
				out.writeObject(new Message(Message.MessageType.LOGIN_RESPONSE, "INVALID_CREDENTIALS"));
			}

			 if (valid) {
			 	username = msg.username;
			 	broadcastUpdateUsers();
			 	callback.accept(new Message(Message.MessageType.NEWUSER, username));
			 }
		}

		private void forwardText(Message msg) throws Exception {
			callback.accept(msg);
			for (ClientThread ct : clients) {
				if (ct.username != null && ct.username.equals(msg.recipientUsername)) {
					ct.out.writeObject(new Message(msg.username, msg.recipientUsername, msg.message));
				}
			}
		}

		private void handleDisconnect() {
			clients.remove(this);
			if (username != null) {
				callback.accept(new Message(Message.MessageType.DISCONNECT, username));
				try {
					broadcastUpdateUsers();
				} catch (Exception ignored) {}
			}
			try { socket.close(); } catch (Exception ignored) {}
		}

		private void broadcastUpdateUsers() throws Exception {
			ArrayList<String> names = new ArrayList<>();
			for (ClientThread ct : clients) {
				if (ct.username != null) {
					names.add(ct.username);
				}
			}
			Message update = new Message(names);
			for (ClientThread ct : clients) {
				ct.out.writeObject(update);
			}
		}
	}
}