import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1 and 2: Path traversal.
 *         Find the Manhattan distance between each pair of galaxies.
 *         The distance between two points, one row or one column apart, is 1 unless the entire row or entire column
 *         does not have a galaxy in which case the distance is 2 (100,000 in part 2).
 * </ul>
 */
public class Day11 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));

        var rowEdges = new int[lines.size()];
        var colEdges = new int[lines.getFirst().length()];
        for (var i = 0; i < lines.size(); i++) {
            var row = lines.get(i);
            for (var j = 0; j < row.length(); j++) {
                var isGalaxy = row.charAt(j) == '#';
                rowEdges[i] += isGalaxy ? 1 : 0;
                colEdges[j] += isGalaxy ? 1 : 0;
            }
        }

        for (var i = 0; i < rowEdges.length; i++) {
            if (rowEdges[i] == 0) {
                rowEdges[i] = part1 ? 2 : 1000000;
            } else {
                rowEdges[i] = 1;
            }
        }
        for (var i = 0; i < colEdges.length; i++) {
            if (colEdges[i] == 0) {
                colEdges[i] = part1 ? 2 : 1000000;
            } else {
                colEdges[i] = 1;
            }
        }

        var galaxyCoords = new ArrayList<Coordinate>();
        var numGalaxies = 0;
        for (var i = 0; i < lines.size(); i++) {
            var row = lines.get(i);
            for (var j = 0; j < row.length(); j++) {
                var isGalaxy = row.charAt(j) == '#';
                if (isGalaxy) {
                    galaxyCoords.add(new Coordinate(i, j, 0));
                    numGalaxies++;
                }
            }
        }

        var sum = 0L;
        for (var startGalaxy = 1; startGalaxy < numGalaxies; startGalaxy++) {
            var start = galaxyCoords.get(startGalaxy - 1);

            for (var destGalaxy = startGalaxy + 1; destGalaxy <= numGalaxies; destGalaxy++) {
                var finish = galaxyCoords.get(destGalaxy - 1);

                var firstRow = Math.min(start.row, finish.row) + 1;
                var lastRow = Math.max(start.row, finish.row);
                var firstCol = Math.min(start.col, finish.col) + 1;
                var lastCol = Math.max(start.col, finish.col);
                for (var i = firstRow; i <= lastRow; i++) {
                    sum += rowEdges[i];
                }
                for (var i = firstCol; i <= lastCol; i++) {
                    sum += colEdges[i];
                }
            }
        }
        System.out.println(sum);
    }

    private record Coordinate(int row, int col, int steps) {
    }
}
