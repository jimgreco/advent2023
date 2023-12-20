import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1 and 2: Shoelace algo.
 *         The input is parsed to extract the vertices of the boundary that defines the polygon.
 *         The shoelace algo is then applied to get the area of the polygon from the vertices.
 *         The area of the polygon plus the length of boundary is the total area.
 * </ul>
 */
public class Day18 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(java.nio.file.Path.of(args[1]));

        var vertices = new ArrayList<Coord>();
        vertices.add(new Coord(0, 0));
        var row = 0L;
        var col = 0L;

        for (var line : lines) {
            var split = line.split(" ");
            String dir;
            long num;

            if (part1) {
                dir = split[0];
                num = Integer.parseInt(split[1]);

            } else {
                var colorHex = split[2].substring(2, 8);
                num = 0;
                for (var i = 0; i < 5; i++) {
                    num *= 16;

                    var c = colorHex.charAt(i);
                    if (c >= '0' && c <= '9') {
                        num += c - '0';
                    } else {
                        num += c - 'a' + 10;
                    }
                }

                var c = colorHex.charAt(5);
                if (c == '0') {
                    dir = "R";
                } else if (c == '1') {
                    dir = "D";
                } else if (c == '2') {
                    dir = "L";
                } else {
                    dir = "U";
                }
            }

            if (dir.equals("L")) {
                vertices.add(new Coord(row, col - num));
                col -= num;
            } else if (dir.equals("R")) {
                vertices.add(new Coord(row, col + num));
                col += num;
            } else if (dir.equals("U")) {
                vertices.add(new Coord(row - num, col));
                row -= num;
            } else if (dir.equals("D")) {
                vertices.add(new Coord(row + num, col));
                row += num;
            }
        }

        var border = 0L;
        var one = 0L;
        var two = 0L;
        for (var i = 0; i < vertices.size() - 1; i++) {
            var curr = vertices.get(i);
            var next = vertices.get(i + 1);
            one += curr.col * next.row;
            two += curr.row * next.col;
            border += Math.abs(next.row - curr.row);
            border += Math.abs(next.col - curr.col);
        }
        var inside = Math.abs(one - two);

        var totalArea = (inside + border) / 2 + 1;
        System.out.println(totalArea);
    }

    private record Coord(long row, long col) { }
}
