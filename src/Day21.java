import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: Traversal.
 *         The grid is traversed for 64 iterations from the starting location.
 *     <li>Part 2: Quadratic formula.
 *         The starting location is at the center of the grid and there is a clear path in all four cardinal directions
 *         from the starting location to the edge of the grid.
 *         As such, a quadratic equation, f(x) = a + b * x + c * x^2, can be iterpolated from the first 3 points where
 *         the end of the grid is reached, i.e., x = rows / 2, 3 * rows / 2, and 5 * rows / 2,
 * </ul>
 */
public class Day21 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(java.nio.file.Path.of(args[1]));

        Coord start = null;
        var grid = new char[lines.size()][];
        for (var i = 0; i < lines.size(); i++) {
            var str = lines.get(i);
            grid[i] = str.toCharArray();
            if (str.contains("S")) {
                start = new Coord(i, str.indexOf('S'));
            }
        }

        var count = part1 ? doPart1(grid, start) : doPart2(grid, start, 26501365);
        System.out.println(count);
    }

    private static long doPart1(char[][] grid, Coord start) {;
        Queue<Coord> queue = new LinkedList<>();
        queue.add(start);

        for (var k = 0; k < 64; k++) {
            var size = queue.size();
            var visited = new HashSet<Coord>();
            for (var i = 0; i < size; i++) {
                var coord = queue.remove();

                if (coord.row < 0 || coord.row == grid.length || coord.col < 0 || coord.col == grid.length
                        || grid[coord.row][coord.col] == '#') {
                    continue;
                }
                if (!visited.add(coord)) {
                    continue;
                }

                queue.add(new Coord(coord.row - 1, coord.col));
                queue.add(new Coord(coord.row + 1, coord.col));
                queue.add(new Coord(coord.row, coord.col - 1));
                queue.add(new Coord(coord.row, coord.col + 1));
            }
        }

        return queue.stream().filter(
                x -> x.row >= 0 && x.row < grid.length
                        && x.col >= 0 && x.col < grid[0].length
                        && grid[x.row][x.col] != '#').distinct().count();
    }

    private static long doPart2(char[][] grid, Coord start, long n) {
        Queue<Coord> queue = new LinkedList<>();
        queue.add(start);

        var rows = grid.length;
        var cols = grid[0].length;
        var f0 = 0L;
        var f1 = 0L;
        var f2 = 0L;

        var iterations = 0;
        while (f2 == 0) {
            var size = queue.size();
            var visited = new HashSet<Coord>();

            for (var i = 0; i < size; i++) {
                var coord = queue.remove();

                var row = coord.row % rows;
                if (row < 0) {
                    row += rows;
                }
                var col = coord.col % cols;
                if (col < 0) {
                    col += cols;
                }
                if (grid[row][col] == '#') {
                    continue;
                }
                if (!visited.add(coord)) {
                    continue;
                }

                queue.add(new Coord(coord.row - 1, coord.col));
                queue.add(new Coord(coord.row + 1, coord.col));
                queue.add(new Coord(coord.row, coord.col - 1));
                queue.add(new Coord(coord.row, coord.col + 1));
            }

            if (iterations == start.row) {
                f0 = visited.size();
            } else if (iterations == start.row + rows) {
                f1 = visited.size();
            } else if (iterations == start.row + 2 * rows) {
                f2 = visited.size();
            }
            iterations++;
        }

        n /= rows;
        var b0 = f0;
        var b1 = f1 - f0;
        var b2 = f2 - f1;
        return b0 + b1 * n + (b2 - b1) * (n * (n - 1) / 2);
    }

    private record Coord(int row, int col) {}
}
