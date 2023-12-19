import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: Hashing.
 *         The hash function for each step is summed.
 *     <li>Part 2: Linked hash map.
 *         The lens labels/focal lengths are stored in a linked hash map for each "box" which preserves the order
 *         for insertion, replacement, and deletion.
 * </ul>
 */
public class Day15 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));

        var steps = lines.getFirst().split(",");
        var sum = part1 ? doPart1(steps) : doPart2(steps);
        System.out.println(sum);
    }

    private static int doPart1(String[] steps) {
        var sum = 0;
        for (var sequence : steps) {
            var hash = 0;
            for (var i = 0; i < sequence.length(); i++) {
                var c = sequence.charAt(i);
                hash += c;
                hash *= 17;
                hash %= 256;
            }
            sum += hash;
        }
        return sum;
    }

    private static int doPart2(String[] steps) {
        var boxes = new HashMap<Integer, Map<String, Integer>>();

        for (var sequence : steps) {
            var hash = 0;

            for (var i = 0; i < sequence.length(); i++) {
                var c = sequence.charAt(i);

                if (c == '-' || c == '=') {
                    var label = sequence.substring(0, i);
                    var box = boxes.computeIfAbsent(hash, k -> new LinkedHashMap<>());
                    if (c == '-') {
                        box.remove(label);
                    } else {
                        var focalLength = sequence.charAt(i + 1);
                        box.put(label, focalLength - '0');
                    }
                    break;
                }

                hash += c;
                hash *= 17;
                hash %= 256;
            }
        }

        var sum = 0;
        for (var entry : boxes.entrySet()) {
            var boxNum = 1 + entry.getKey();
            var slot = 1;
            for (var focalLength : entry.getValue().values()) {
                sum += boxNum * slot * focalLength;
                slot++;
            }
        }
        return sum;
    }
}
