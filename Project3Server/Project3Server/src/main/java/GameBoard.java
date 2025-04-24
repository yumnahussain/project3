import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

// --- the board itself: no threading here! ---
public class GameBoard {
    public static final int COLUMNS = 7;
    public static final int ROWS    = 6;
    private int[][] grid = new int[ROWS][COLUMNS];

    // factory for drawing
    public Shape makeGrid(int tileSize) {
        return new Rectangle(COLUMNS * tileSize, ROWS * tileSize);
    }

    // attempt a move; returns true if valid
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
        // TODO: implement 4-in-a-row checking on grid[][]
        return false;
    }

    public int[][] getGrid() {
        return grid;
    }
}



