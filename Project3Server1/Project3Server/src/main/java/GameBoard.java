public class GameBoard {
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private int[][] board;
    private int currentPlayer;

    public GameBoard() {
        board = new int[ROWS][COLS];
        currentPlayer = 1;
    }

    public boolean makeMove(int column) {
        if (column < 0 || column >= COLS) return false;

        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == 0) {
                board[row][column] = currentPlayer;
                return true;
            }
        }
        return false;
    }

    public boolean checkWin(int row, int col) {
        int player = board[row][col];

        // Check horizontal
        int count = 0;
        for (int c = 0; c < COLS; c++) {
            if (board[row][c] == player) count++;
            else count = 0;
            if (count >= 4) return true;
        }

        // Check vertical
        count = 0;
        for (int r = 0; r < ROWS; r++) {
            if (board[r][col] == player) count++;
            else count = 0;
            if (count >= 4) return true;
        }

        // Check diagonal (top-left to bottom-right)
        int startRow = row - Math.min(row, col);
        int startCol = col - Math.min(row, col);
        count = 0;
        while (startRow < ROWS && startCol < COLS) {
            if (board[startRow][startCol] == player) count++;
            else count = 0;
            if (count >= 4) return true;
            startRow++;
            startCol++;
        }

        // Check diagonal (top-right to bottom-left)
        startRow = row - Math.min(row, COLS - 1 - col);
        startCol = col + Math.min(row, COLS - 1 - col);
        count = 0;
        while (startRow < ROWS && startCol >= 0) {
            if (board[startRow][startCol] == player) count++;
            else count = 0;
            if (count >= 4) return true;
            startRow++;
            startCol--;
        }

        return false;
    }

    public boolean isBoardFull() {
        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == 0) return false;
        }
        return true;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int[][] getBoard() {
        return board;
    }

    public static int getRows() {
        return ROWS;
    }

    public static int getCols() {
        return COLS;
    }
}