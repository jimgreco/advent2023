import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1 and 2: Path traversal with early escape
 *         The input is converted into a matrix and the beam path is traversed.
 *         An optimization is made to escape the traversal early if the same position in the matrix has been previosuly
 *         visited from the same direction.
 * </ul>
 */
public class Day16 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));

        var matrix = new char[lines.size()][];
        var rows = matrix.length;
        for (var i = 0; i < rows; i++) {
            matrix[i] = lines.get(i).toCharArray();
        }
        var cols = matrix[0].length;

        if (part1) {
            var size = followBeams(new Beam(new Coord(0, 0), Direction.RIGHT), matrix);
            System.out.println(size);
        } else {
            var max = 0;
            for (var i = 0; i < rows; i++) { // left side
                max = Math.max(max, followBeams(new Beam(new Coord(i, 0), Direction.RIGHT), matrix));
            }
            for (var j = 0; j < cols; j++) { // top side
                max = Math.max(max, followBeams(new Beam(new Coord(0, j), Direction.DOWN), matrix));
            }
            for (var i = 0; i < rows; i++) { // right side
                max = Math.max(max, followBeams(new Beam(new Coord(i, cols - 1), Direction.LEFT), matrix));
            }
            for (var j = 0; j < cols; j++) { // bottom side
                max = Math.max(max, followBeams(new Beam(new Coord(rows - 1, j), Direction.UP), matrix));
            }
            System.out.println(max);
        }
    }

    private static int followBeams(Beam start, char[][] matrix) {
        var visited = new HashSet<Coord>();
        var states = new HashSet<Beam>();

        var queue = new LinkedList<Beam>();
        queue.add(start);

        while (!queue.isEmpty()) {
            var size = queue.size();
            for (var i = 0; i < size; i++) {
                var beam = queue.remove();
                if (states.contains(beam)) {
                    continue;
                }
                var dir = beam.direction;
                var row = beam.coord.row;
                var col = beam.coord.col;
                if (row < 0 || row == matrix.length || col < 0 || col == matrix[0].length) {
                    continue;
                }

                visited.add(beam.coord);
                states.add(beam);

                var c = matrix[row][col];
                if (c == '.') {
                    var newRow = switch (dir) {
                        case LEFT, RIGHT -> row;
                        case UP -> row - 1;
                        case DOWN -> row + 1;
                    };
                    var newCol = switch (dir) {
                        case UP, DOWN -> col;
                        case LEFT -> col - 1;
                        case RIGHT -> col + 1;
                    };
                    queue.add(new Beam(new Coord(newRow, newCol), dir));
                } else if (c == '\\') {
                    var newRow = switch (dir) {
                        case UP, DOWN -> row;
                        case LEFT -> row - 1;
                        case RIGHT -> row + 1;
                    };
                    var newCol = switch (dir) {
                        case LEFT, RIGHT -> col;
                        case UP -> col - 1;
                        case DOWN -> col + 1;
                    };
                    var newDir = switch (dir) {
                        case UP -> Direction.LEFT;
                        case DOWN -> Direction.RIGHT;
                        case LEFT -> Direction.UP;
                        case RIGHT -> Direction.DOWN;
                    };
                    queue.add(new Beam(new Coord(newRow, newCol), newDir));
                } else if (c == '/') {
                    var newRow = switch (dir) {
                        case UP, DOWN -> row;
                        case LEFT -> row + 1;
                        case RIGHT -> row - 1;
                    };
                    var newCol = switch (dir) {
                        case LEFT, RIGHT -> col;
                        case UP -> col + 1;
                        case DOWN -> col - 1;
                    };
                    var newDir = switch (dir) {
                        case UP -> Direction.RIGHT;
                        case DOWN -> Direction.LEFT;
                        case LEFT -> Direction.DOWN;
                        case RIGHT -> Direction.UP;
                    };
                    queue.add(new Beam(new Coord(newRow, newCol), newDir));
                } else if (c == '-') {
                    if (dir != Direction.RIGHT) {
                        queue.add(new Beam(new Coord(row, col - 1), Direction.LEFT));
                    }
                    if (dir != Direction.LEFT) {
                        queue.add(new Beam(new Coord(row, col + 1), Direction.RIGHT));
                    }
                } else if (c == '|') {
                    if (dir != Direction.DOWN) {
                        queue.add(new Beam(new Coord(row - 1, col), Direction.UP));
                    }
                    if (dir != Direction.UP) {
                        queue.add(new Beam(new Coord(row + 1, col), Direction.DOWN));
                    }
                }
            }
        }

        return visited.size();
    }

    private record Coord(int row, int col) { }

    private record Beam(Coord coord, Direction direction) { }

    private enum Direction { RIGHT, DOWN, LEFT, UP }
}
