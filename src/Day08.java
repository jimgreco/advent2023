import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: Binary tree traversal.
 *         Parse the input into a binary tree.
 *         Traverse the tree from node "AAA" to node "ZZZ".
 *     <li>Part 2: Binary tree traversal, least common multiple.
 *         Traverse each node that ends with an 'A' to a node that ends with a 'Z'.
 *         Compute the least common multiple of the number of steps for each traversal.
 * </ul>
 */
public class Day08 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));

        var nodes = new HashMap<String, Node>();
        var directions = lines.getFirst();
        for (var i = 2; i < lines.size(); i++) {
            var initial = lines.get(i).split("=");
            var label = initial[0].trim();
            var leftRight = initial[1].trim().split(",");
            var left = leftRight[0].substring(1);
            var right = leftRight[1].trim().substring(0, 3);
            nodes.put(label, new Node(label, left, right));
        }

        if (part1) {
            var steps = doPart1(nodes, directions);
            System.out.println(steps);
        } else {
            var lcm = doPart2(nodes, directions);
            System.out.println(lcm);
        }
    }

    private static int doPart1(HashMap<String, Node> nodes, String directions) {
        var steps = 0;
        var current = nodes.get("AAA");
        var last = nodes.get("ZZZ");
        while (current != last) {
            var dir = directions.charAt(steps++ % directions.length());
            current = nodes.get(dir == 'L' ? current.left : current.right);
        }
        return steps;
    }

    private static long doPart2(HashMap<String, Node> nodes, String directions) {
        var start = nodes.values().stream().filter(x -> x.start).toList();
        var steps = new long[start.size()];
        for (var i = 0; i < start.size(); i++) {
            var current = start.get(i);
            while (!current.end) {
                var dir = directions.charAt((int) (steps[i]++ % directions.length()));
                current = nodes.get(dir == 'L' ? current.left : current.right);
            }
        }

        // LCM
        var result = 1L;
        for (var value : steps) {
            result = lcm(result, value);
        }
        return result;
    }

    private static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            var temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    private static class Node {

        final boolean start;
        final boolean end;
        final String left;
        final String right;
        final String label;

        private Node(String label, String left, String right) {
            this.label = label;
            this.start = label.endsWith("A");
            this.end = label.endsWith("Z");
            this.left = left;
            this.right = right;
        }
    }
}
