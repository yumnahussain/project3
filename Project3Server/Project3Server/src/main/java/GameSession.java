//import Server;
//package project3.Project3Server.Project3Server.src.main.java.Server.ClientThread;
//package project3server;

//import Server.ClientThread;

public class GameSession {
    private final GameBoard board = new GameBoard();
//    private final Server server;
    private final ClientThread player1, player2;   // player2 may wrap an AI proxy
    private       int        currentPlayer = 1;

    public GameSession(ClientThread p1, ClientThread p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    /** Called by your ClientThread when it receives a MOVE message. */
    public synchronized void handleMove(int column, ClientThread from) {
//        if ((from != player1 && from != player2) ||
//                board.getGrid()[0][column] != 0 ||
//                !board.makeMove(column, currentPlayer)) {
//            from.sendInvalid();
//            return;
//        }

        // broadcast updated board
//        Message update = Message.update(board.getGrid(), currentPlayer);
//        player1.send(update);
//        player2.send(update);

        // check for win/draw
        if (board.checkWin()) {
//            Message win = Message.win(currentPlayer);
//            player1.send(win);
//            player2.send(win);
            return;
        }

        // switch turn
        currentPlayer = 3 - currentPlayer;
    }
}