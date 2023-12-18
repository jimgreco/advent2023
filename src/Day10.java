import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: Path traversal.
 *         The loop is traversed all the way around.
 *         The farthest distance is half the number of segments traversed.
 *     <li>Part 2: Shoelace theorem.
 *         The loop is traversed and the pipe is marked to form the boundary of a polygon.
 *         Each row is then scanned for the presence of the pipe.
 *         The shoelace theorem is applied to mark the points inside and outside the polygon made up by the polygon.
 *         Note: There's a bug in here somewhere due to the discrete nature of the boundary.
 * </ul>
 */
public class Day10 {

    private static final int[][] DIRECTIONS = new int[][] {{ -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }};

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));

        var grid = new char[lines.size()][];
        Coordinate start = null;
        for (var i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            if (start == null) {
                var col = line.indexOf('S');
                if (col != -1) {
                    start = new Coordinate(i, col);
                }
            }
            grid[i] = line.toCharArray();
        }

        if (part1) {
            var distance = doPart1(grid, start);
            System.out.println(distance);
        } else {
            var inside = doPart2(grid, start);
            System.out.println(inside);
        }
    }

    private static int doPart1(char[][] grid, Coordinate start) {
        Coordinate current = findStartPipe(grid, start);
        var last = start;

        var steps = 1;
        while (grid[current.row][current.col] != 'S') {
            steps++;
            var newCurrent = traversePipe(grid, last, current);
            last = current;
            current = newCurrent;
        }

        return steps / 2 + ((steps % 2 == 1) ? 1 : 0);
    }

    private static int doPart2(char[][] grid, Coordinate start) {
         var current = findStartPipe(grid, start);
        var first = current;
        var last = start;
        while (grid[current.row][current.col] != 'S') {
            if (grid[last.row][last.col] == 'F' || grid[last.row][last.col] == 'L' || grid[last.row][last.col] == '|') {
                grid[last.row][last.col] = 'P';
            }
            var newCurrent = traversePipe(grid, last, current);
            last = current;
            current = newCurrent;
        }
        if (first.row != last.row && first.col == last.col) {
            grid[start.row][start.col] = 'P';
        } else if (first.row == last.row - 1 && first.col == last.col + 1) {
            grid[start.row][start.col] = 'P';
        }else if (first.row == last.row + 1 && first.col == last.col + 1) {
            grid[start.row][start.col] = 'P';
        }

        var inside = false;
        var insideCount = 0;
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                var isPipe = grid[i][j];
                if (isPipe == 'P') {
                    inside = !inside;
                } else if (inside) {
                    insideCount++;
                }
            }

        }
        return insideCount;
    }

    private static Coordinate findStartPipe(char[][] grid, Coordinate start) {
        Coordinate current = null;
        for (var path : DIRECTIONS) {
            current = findStartPipe(grid, start, path[0], path[1]);
            if (current != null) {
                break;
            }
        }
        return current;
    }

    private static Coordinate findStartPipe(char[][] grid, Coordinate start, int i, int j) {
        var row = start.row + i;
        var col = start.col + j;
        if (row >= 0 && row < grid.length && col >= 0 && col < grid[row].length) {
            var c = grid[row][col];
            if ((col < start.col && (c == 'L' || c == '-' || c == 'F'))           // west
                    || (col > start.col && (c == 'J' || c == '-' || c == '7'))    // east
                    || (row < start.row && (c == '7' || c == '|' || c == 'F'))    // north
                    || (row > start.row && (c == 'J' || c == '|' || c == 'L'))) { // south
                return new Coordinate(row, col);
            }
        }
        return null;
    }

    private static Coordinate traversePipe(char[][] grid, Coordinate last, Coordinate current) {
        var c = grid[current.row][current.col];

        var diffRow = current.row - last.row;
        var diffCol = current.col - last.col;

        if (c == '-' || c == '|') { // move east/west
            return new Coordinate(current.row + diffRow, current.col + diffCol);
        } else if (c == '7' || c == 'L') { // move south/east
            return new Coordinate(current.row + diffCol, current.col + diffRow);
        } else if (c == 'J' || c == 'F') {
            return new Coordinate(current.row - diffCol, current.col - diffRow);
        }
        throw new IllegalArgumentException();
    }

    private record Coordinate(int row, int col) {
    }
}
