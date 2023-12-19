import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1 and 2: Search.
 *         A search is conducted for mirrored rows, followed by mirrored columns by comparing pairs of rows/columns.
 *         Part 1 looks for an exact match.
 *         Part 2 looks for a match with one error.
 *         Note: You could do tricks with bitmasks to make the comparison faster, but the problem is heavily constrained
 *               in size,
 * </ul>
 */
public class Day13 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));
        var maxErrors = part1 ? 0 : 1;

        var rowPattern = new ArrayList<char[]>();
        var sum = 0L;
        for (var line : lines) {
            if (line.isEmpty()) {
                sum += getValue(rowPattern, maxErrors);
                rowPattern = new ArrayList<>();
            } else {
                rowPattern.add(line.toCharArray());
            }
        }
        // last one
        sum += getValue(rowPattern, maxErrors);
        System.out.println(sum);
    }

    private static int getValue(ArrayList<char[]> pattern, int maxErrors) {
        var row = getRow(pattern, maxErrors);
        if (row != 0) {
            return 100 * row;
        }
        var col = getCol(pattern, maxErrors);
        return col;
    }

    private static int getRow(ArrayList<char[]> pattern, int maxErrors) {
        for (var i = 1; i < pattern.size(); i++) {
            var rowsToCheck = Math.min(i, pattern.size() - i);
            var errors = 0;

            for (var k = 1; errors <= maxErrors && k <= rowsToCheck; k++) {
                var row1 = pattern.get(i - k);
                var row2 = pattern.get(i + k - 1);

                for (var j = 0; j < row1.length; j++) {
                    if (row1[j] != row2[j]) {
                        errors++;
                    }
                }
            }

            if (errors == maxErrors) {
                return i;
            }
        }
        return 0;
    }

    private static int getCol(ArrayList<char[]> pattern, int maxErrors) {
        var cols = pattern.getFirst().length;
        for (var j = 1; j < cols; j++) {
            var errors = 0;
            var colsToCheck = Math.min(j, cols - j);

            for (var k = 1; errors <= maxErrors && k <= colsToCheck; k++) {
                for (var i = 0; i < pattern.size(); i++) {
                    var row = pattern.get(i);
                    var col1 = row[j - k];
                    var col2 = row[j + k - 1];

                    if (col1 != col2) {
                        errors++;
                    }
                }
            }

            if (errors == maxErrors) {
                return j;
            }
        }
        return 0;
    }
}
