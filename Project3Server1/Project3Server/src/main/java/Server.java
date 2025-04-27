import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.function.Consumer;

public class Server {
	private int count = 1;
	private final List<ClientThread> clients = new ArrayList<>();
	private final HashMap<String, String> userDatabase = new HashMap<>();
	private final Consumer<Message> callback;

	public Server(Consumer<Message> callback) {
		this.callback = callback;
		new TheServer().start();
	}

	private class TheServer extends Thread {
		@Override
		public void run() {
			try (ServerSocket serverSocket = new ServerSocket(5555)) {
				while (true) {
					Socket clientSocket = serverSocket.accept();
					ClientThread thread = new ClientThread(clientSocket);
					thread.start();
					count++;
				}
			} catch (Exception e) {
				callback.accept(new Message(Message.MessageType.TEXT, "Server failed to start: " + e.getMessage()));
			}
		}
	}

	private class ClientThread extends Thread {
		private final Socket socket;
		private ObjectInputStream in;
		private ObjectOutputStream out;
		private String username;

		ClientThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				socket.setTcpNoDelay(true);
				clients.add(this);
				while (true) {
					Message msg = (Message) in.readObject();
					switch (msg.type) {
						case SIGNUP:
							handleSignup(msg);
							break;
						case LOGIN:
							handleLogin(msg);
							break;
						case TEXT:
							forwardText(msg);
							break;
						case DISCONNECT:
							handleDisconnect();
							return;
						default:
							break;
					}
				}
			} catch (Exception e) {
				handleDisconnect();
			}
		}

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
			List<String> names = new ArrayList<>();
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