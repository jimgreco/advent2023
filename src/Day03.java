import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: Inline integer string parsing and area search.
 *         The text is turned into a matrix and each row of the matrix is parsed, inline, for numbers.
 *         For each character of a number, the surrounding 8 boxes are search for symbols.
 *     <li>Part 2: Inline integer string parsing and area search.
 *         Similar parse and search methods are used as Part 1.
 *         The number of times each gear (*) is associated with a number is recorded in a map.
 *         That map is then searched for gears that have exactly two associated associated numbers.
 * </ul>
 */
public class Day03 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));
        var chars = new char[lines.size()][];
        for (var i = 0; i < lines.size(); i++) {
            chars[i] = lines.get(i).toCharArray();
        }
        var sum = part1 ? doPart1(chars) : doPart2(chars);
        System.out.println(sum);
    }

    private static int doPart1(char[][] chars) {
        var sum = 0;
        for (var i = 0; i < chars.length; i++) {
            var inNumber = false;
            var foundSymbol = false;
            var num = 0;

            for (var j = 0; j < chars[i].length; j++) {
                var charIsNumber = chars[i][j] >= '0' && chars[i][j] <= '9';
                if (charIsNumber) {
                    num *= 10;
                    num += chars[i][j] - '0';
                    inNumber = true;
                    if (!foundSymbol) {
                        foundSymbol = isSymbol(chars, i, j);
                    }
                }

                if (inNumber && (!charIsNumber || j == chars[i].length - 1)) {
                    if (foundSymbol) {
                        sum += num;
                    }
                    num = 0;
                    inNumber = false;
                    foundSymbol = false;
                }
            }
        }
        return sum;
    }

    private static boolean isSymbol(char[][] chars, int row, int col) {
        var symbol = false;
        for (var i = row - 1; i <= row + 1; i++) {
            for (var j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < chars.length && j >= 0 && j < chars[i].length
                        && !(chars[i][j] >= '0' && chars[i][j] <= '9' || chars[i][j] == '.')) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int doPart2(char[][] chars) {
        var gearPartNumbers = new HashMap<RowCol, List<Integer>>();

        for (var i = 0; i < chars.length; i++) {
            var inNumber = false;
            RowCol gearCoords = null;
            var num = 0;

            for (var j = 0; j < chars[i].length; j++) {
                var charIsNumber = chars[i][j] >= '0' && chars[i][j] <= '9';
                if (charIsNumber) {
                    num *= 10;
                    num += chars[i][j] - '0';
                    inNumber = true;

                    if (gearCoords == null) {
                        gearCoords = getGearCoords(chars, i, j);
                    }
                }

                if (inNumber && (!charIsNumber || j == chars[i].length - 1)) {
                    if (gearCoords != null) {
                        var partNumbers = gearPartNumbers.computeIfAbsent(gearCoords, k -> new ArrayList<>());
                        partNumbers.add(num);
                    }
                    num = 0;
                    inNumber = false;
                    gearCoords = null;
                }
            }
        }

        var sum = 0;
        for (var entry : gearPartNumbers.entrySet()) {
            var partNumbers = entry.getValue();
            if (partNumbers.size() == 2) {
                sum += partNumbers.get(0) * partNumbers.get(1);
            }
        }
        return sum;
    }

    private static RowCol getGearCoords(char[][] chars, int row, int col) {
        for (var i = row - 1; i <= row + 1; i++) {
            for (var j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < chars.length && j >= 0 && j < chars[i].length && chars[i][j] == '*') {
                    return new RowCol(i, j);
                }
            }
        }
        return null;
    }

    private static final class RowCol {

        private final int row;
        private final int col;

        private RowCol(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            var rowCol = (RowCol) o;
            return row == rowCol.row && col == rowCol.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }
}
