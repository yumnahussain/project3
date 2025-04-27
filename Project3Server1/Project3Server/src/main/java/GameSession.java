public class GameSession {
    private ClientThread player1;
    private ClientThread player2;
    private GameBoard board;
    private ClientThread currentPlayer;

    public GameSession(ClientThread player1, ClientThread player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new GameBoard();
        this.currentPlayer = player1;
    }

    public void handleMove(Message message, ClientThread player) {
        if (player != currentPlayer) {
            player.sendMessage(new Message(Message.MessageType.ERROR, "Not your turn", "Server"));
            return;
        }

        int column = message.getColumn();
        if (board.makeMove(column)) {
            // Broadcast move to both players
            player1.sendMessage(message);
            player2.sendMessage(message);

            // Check for win
            int row = findRowForMove(column);
            if (board.checkWin(row, column)) {
                Message winMessage = new Message(Message.MessageType.WIN, "Player " + currentPlayer.getClientName() + " wins!", "Server");
                player1.sendMessage(winMessage);
                player2.sendMessage(winMessage);
                return;
            }

            // Check for draw
            if (board.isBoardFull()) {
                Message drawMessage = new Message(Message.MessageType.DRAW, "Game is a draw!", "Server");
                player1.sendMessage(drawMessage);
                player2.sendMessage(drawMessage);
                return;
            }

            // Switch turns
            switchTurn();
        } else {
            player.sendMessage(new Message(Message.MessageType.ERROR, "Invalid move", "Server"));
        }
    }

    private int findRowForMove(int column) {
        int[][] gameBoard = board.getBoard();
        for (int row = GameBoard.getRows() - 1; row >= 0; row--) {
            if (gameBoard[row][column] != 0) {
                return row;
            }
        }
        return -1;
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        Message turnMessage = new Message(Message.MessageType.UPDATE, "Your turn", currentPlayer.getClientName());
        currentPlayer.sendMessage(turnMessage);
    }

    public boolean hasPlayer(ClientThread player) {
        return player == player1 || player == player2;
    }
}