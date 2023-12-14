import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: Tree traversal.
 *         The file is read as a list of root nodes ("seeds") and a list of source and destination nodes ("mappings").
 *         For each seed, the graph is traversed from the root node to the leaf node.
 *     <li>Part 2: Reversing a tree, tree traversal.
 *         The tree built in part 1 is reversed (source nodes become destination nodes and vice versa).
 *         The tree is then traversed, starting from the lowest value, from each root node (the leaf nodes in Part 1).
 *         The first leaf node that corresponds to the seed ranges is the answer.
 * </ul>
 */
public class Day05 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));
        var seeds = Arrays.stream(lines.getFirst().split(":")[1].trim().split(" "))
                .map(Long::parseLong).toList();
        var lowest = part1 ? doPart1(seeds, lines) : doPart2(seeds, lines);
        System.out.println(lowest);
    }

    private static long doPart1(List<Long> seeds, List<String> lines) {
        var maps = getMaps1(lines);
        var lowest = Long.MAX_VALUE;
        for (var seed : seeds) {
            var location = traverse(maps, seed);
            lowest = Math.min(lowest, location);
        }
        return lowest;
    }

    private static long doPart2(List<Long> rawSeeds, List<String> lines) {
        var seeds = new ArrayList<Entry>();
        for (var i = 0; i < rawSeeds.size(); i += 2) {
            seeds.add(new Entry(-1, rawSeeds.get(i), rawSeeds.get(i + 1)));
        }
        seeds.sort(Comparator.comparingLong(o -> o.dest));

        var maps = getMaps2(lines);
        var locationMaps = maps.removeFirst();

        for (var startingLocation : locationMaps) {
            for (var current = startingLocation.dest; current < startingLocation.dest + startingLocation.range; current++) {
                var potentialSeed = traverse(maps, current);
                for (var seed : seeds) {
                    if (potentialSeed < seed.dest) {
                        break;
                    } else if (potentialSeed < seed.dest + seed.range) {
                        var offset = current - startingLocation.dest;
                        return startingLocation.src + offset;
                    }
                }
            }
        }

        return -1;
    }

    private static Long traverse(List<List<Entry>> maps, Long seed) {
        var current = seed;
        for (var map : maps) {
            for (var mapping : map) {
                if (current < mapping.src) {
                    break;
                } else if (current < mapping.src + mapping.range) {
                    var offset = current - mapping.src;
                    current = mapping.dest + offset;
                    break;
                }
            }
        }
        return current;
    }

    private static List<List<Entry>> getMaps1(List<String> lines) {
        List<List<Entry>> maps = new ArrayList<>();
        var currentMap = new ArrayList<Entry>();
        for (var i = 1; i < lines.size(); i++) {
            var line = lines.get(i);

            if (line.isEmpty()) {
                currentMap = new ArrayList<>();
                maps.add(currentMap);
            } else if (line.charAt(0) >= '0' && line.charAt(0) <= '9') {
                var split = line.split(" ");
                var dest = Long.parseLong(split[0]);
                var src = Long.parseLong(split[1]);
                var range = Long.parseLong(split[2]);
                var entry = new Entry(src, dest, range);
                currentMap.add(entry);
            }
        }

        for (var map : maps) {
            map.sort(Comparator.comparingLong(o -> o.src));
        }
        return maps;
    }

    private static List<List<Entry>> getMaps2(List<String> lines) {
        List<List<Entry>> maps = new ArrayList<>();
        var currentMap = new ArrayList<Entry>();
        for (var i = 1; i < lines.size(); i++) {
            var line = lines.get(i);

            if (line.isEmpty()) {
                currentMap = new ArrayList<>();
                maps.addFirst(currentMap);
            } else if (line.charAt(0) >= '0' && line.charAt(0) <= '9') {
                var split = line.split(" ");
                var src = Long.parseLong(split[0]);
                var dest = Long.parseLong(split[1]);
                var range = Long.parseLong(split[2]);
                var entry = new Entry(src, dest, range);
                currentMap.add(entry);
            }
        }

        for (var map : maps) {
            map.sort(Comparator.comparingLong(o -> o.src));
        }
        return maps;
    }

    private static class Entry {

        final long src;
        final long dest;
        final long range;

        public Entry(long src, long dest, long range) {
            this.src = src;
            this.dest = dest;
            this.range = range;
        }
    }
}
