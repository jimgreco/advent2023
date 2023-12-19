import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: Implementation.
 *         The grid is parsed into a matrix and the north tilt function is implemented.
 *     <li>Part 2: Cycle detection.
 *         The matrix is tilted north, west, south, and east and a copy of the matrix is stored in a linked list.
 *         The linked list is then searched for a cycle: a previous matrix is equal to the current matrix.
 *         The "spin cycle" iteration is then fast-forwarded to the end based on the cycle length, avoid repetitive
 *         "spin cycle" iterations.
 * </ul>
 */
public class Day14 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));

        var grid = new char[lines.size()][];
        for (var i = 0; i < lines.size(); i++) {
            grid[i] = lines.get(i).toCharArray();
        }

        if (part1) {
            tiltNorth(grid);
        } else {
            var pastGrids = new LinkedList<char[][]>();
            for (var i = 0; i < 1000000000; i++) {
                var copy = new char[grid.length][];
                for (var k = 0; k < grid.length; k++) {
                    copy[k] = Arrays.copyOf(grid[k], grid[k].length);
                }
                pastGrids.addFirst(copy);

                tiltNorth(grid);
                tiltWest(grid);
                tiltSouth(grid);
                tiltEast(grid);

                for (var k = 0; k < pastGrids.size(); k++) {
                    var pastGrid = pastGrids.get(k);
                    if (isSame(grid, pastGrid)) {
                        var diffLength = k + 1;
                        while (i < 1000000000 - diffLength) {
                            i += diffLength;
                        }
                        pastGrids.clear();
                    }
                }
            }
        }

        var sum = 0;
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'O') {
                    sum += grid.length - i;
                }
            }
        }

        System.out.println(sum);
    }

    private static boolean isSame(char[][] grid1, char[][] grid2) {
        for (var i = 0; i < grid1.length; i++) {
            for (var j = 0; j < grid1[i].length; j++) {
                if (grid1[i][j] != grid2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void tiltNorth(char[][] grid) {
        for (var j = 0; j < grid[0].length; j++) {
            var row = 0;
            for (var i = 0; i < grid.length; i++) {
                if (grid[i][j] == '#') {
                    row = i + 1;
                } else if (grid[i][j] == 'O') {
                    if (i != row) {
                        grid[row][j] = 'O';
                        grid[i][j] = '.';
                    }
                    row++;
                }
            }
        }
    }

    private static void tiltWest(char[][] grid) {
        for (var i = 0; i < grid.length; i++) {
            var col = 0;
            for (var j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == '#') {
                    col = j + 1;
                } else if (grid[i][j] == 'O') {
                    if (j != col) {
                        grid[i][col] = 'O';
                        grid[i][j] = '.';
                    }
                    col++;
                }
            }
        }
    }

    private static void tiltSouth(char[][] grid) {
        for (var j = 0; j < grid[0].length; j++) {
            var row = grid.length - 1;
            for (var i = grid.length - 1; i >= 0; i--) {
                if (grid[i][j] == '#') {
                    row = i - 1;
                } else if (grid[i][j] == 'O') {
                    if (i != row) {
                        grid[row][j] = 'O';
                        grid[i][j] = '.';
                    }
                    row--;
                }
            }
        }
    }

    private static void tiltEast(char[][] grid) {
        for (var i = 0; i < grid.length; i++) {
            var col = grid[i].length - 1;
            for (var j = grid[i].length - 1; j >= 0; j--) {
                if (grid[i][j] == '#') {
                    col = j - 1;
                } else if (grid[i][j] == 'O') {
                    if (j != col) {
                        grid[i][col] = 'O';
                        grid[i][j] = '.';
                    }
                    col--;
                }
            }
        }
    }
}
