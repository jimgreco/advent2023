import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1 and 2: Recursion.
 *         Recursively calculate the difference between sequential pairs of numbers until the difference between numbers
 *         is all zeros.
 *         Each recursion function call then returns the new interpolated value (part 1: 1st value, part 2: last value).
 * </ul>
 */
public class Day09 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));

        var sum = 0L;
        for (var line : lines) {
            var sequence = Arrays.stream(line.split(" ")).map(Long::parseLong).toList();
            var diff = getLastDiff(sequence, part1);
            if (part1) {
                var lastValue = sequence.getLast();
                sum += lastValue + diff;
            } else {
                var firstValue = sequence.getFirst();
                sum += firstValue - diff;
            }
        }
        System.out.println(sum);
    }

    private static long getLastDiff(List<Long> sequence, boolean part1) {
        var diffSequence = new ArrayList<Long>();
        var done = true;
        for (var i = 1; i < sequence.size(); i++) {
            var diff = sequence.get(i) - sequence.get(i - 1);
            diffSequence.add(diff);
            if (diff != 0) {
                done = false;
            }
        }

        if (done) {
            return 0;
        } else {
            var diff = getLastDiff(diffSequence, part1);
            if (part1) {
                var lastValue = diffSequence.getLast();
                return lastValue + diff;
            } else {
                var firstValue = diffSequence.getFirst();
                return firstValue - diff;
            }
        }
    }
}
