import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: Path traversal.
 *         The loop is traversed all the way around.
 *         The farthest distance is half the number of segments traversed.
 *     <li>Part 2: Shoelace algo.
 *         The input is parsed to extract the vertices of the boundary that defines the polygon.
 *         The shoelace algo is then applied to get the area of the polygon from the vertices.
 *         The area of the polygon minus the length of boundary is the total area.
 * </ul>
 */
public class Day10 {

    private static final int[][] DIRECTIONS = new int[][] {{ -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }};

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));

        var grid = new char[lines.size()][];
        Coord start = null;
        for (var i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            if (start == null) {
                var col = line.indexOf('S');
                if (col != -1) {
                    start = new Coord(i, col);
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

    private static int doPart1(char[][] grid, Coord start) {
        Coord current = findStartPipe(grid, start);
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

    private static int doPart2(char[][] grid, Coord start) {
        var current = findStartPipe(grid, start);
        var last = start;

        var vertices = new ArrayList<Coord>();
        vertices.add(start);
        while (grid[current.row][current.col] != 'S') {
            vertices.add(current);
            var newCurrent = traversePipe(grid, last, current);
            last = current;
            current = newCurrent;
        }
        vertices.add(current);

        var border = 0;
        var one = 0;
        var two = 0;
        for (var i = 0; i < vertices.size() - 1; i++) {
            var curr = vertices.get(i);
            var next = vertices.get(i + 1);
            one += curr.col * next.row;
            two += curr.row * next.col;
            border += Math.abs(next.row - curr.row);
            border += Math.abs(next.col - curr.col);
        }
        var inside = Math.abs(one - two);

        var totalArea = inside / 2 + 1 - border / 2;
        return totalArea;
    }

    private static Coord findStartPipe(char[][] grid, Coord start) {
        Coord current = null;
        for (var path : DIRECTIONS) {
            current = findStartPipe(grid, start, path[0], path[1]);
            if (current != null) {
                break;
            }
        }
        return current;
    }

    private static Coord findStartPipe(char[][] grid, Coord start, int i, int j) {
        var row = start.row + i;
        var col = start.col + j;
        if (row >= 0 && row < grid.length && col >= 0 && col < grid[row].length) {
            var c = grid[row][col];
            if ((col < start.col && (c == 'L' || c == '-' || c == 'F'))           // west
                    || (col > start.col && (c == 'J' || c == '-' || c == '7'))    // east
                    || (row < start.row && (c == '7' || c == '|' || c == 'F'))    // north
                    || (row > start.row && (c == 'J' || c == '|' || c == 'L'))) { // south
                return new Coord(row, col);
            }
        }
        return null;
    }

    private static Coord traversePipe(char[][] grid, Coord last, Coord current) {
        var c = grid[current.row][current.col];

        var diffRow = current.row - last.row;
        var diffCol = current.col - last.col;

        if (c == '-' || c == '|') { // move east/west
            return new Coord(current.row + diffRow, current.col + diffCol);
        } else if (c == '7' || c == 'L') { // move south/east
            return new Coord(current.row + diffCol, current.col + diffRow);
        } else if (c == 'J' || c == 'F') {
            return new Coord(current.row - diffCol, current.col - diffRow);
        }
        throw new IllegalArgumentException();
    }

    private record Coord(int row, int col) {
    }
}
