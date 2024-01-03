import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: Graph traversal.
 *         The module file is interpreted as a graph and the graph is traversed 1,000 times.
 *     <li>Part 2: Graph traversal, least common multiple.
 *         "rx" will receive a low pulse if it's parent, a conjunction module, receive a high pulse from all of its
 *         children.
 *         Each grandchild is measured to see when it first sends a high pulse.
 *         The least common multiple of the grandchildren is the total number of iterations required for the parent to
 *         send a low pulse.
 * </ul>
 */
public class Day20 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(java.nio.file.Path.of(args[1]));

        var mods = new HashMap<String, Mod>();
        for (var line : lines) {
            var split = line.split(" -> ");
            var name = split[0];
            var type = name.charAt(0);
            var destinations = split[1].split(", ");

            if (type == '%') {
                mods.put(name.substring(1), new FlipMod(name.substring(1), destinations));
            } else if (type == '&') {
                mods.put(name.substring(1), new ConjMod(name.substring(1), destinations));
            } else {
                mods.put(name, new Mod(name, destinations));
            }
        }
        mods.put("button", new Mod("button", new String[]{ "broadcaster" })); // start
        mods.put("rx", new Mod("rx", new String[]{}));                        // finish

        for (var mod : mods.values()) {
            for (var dest : mod.dests) {
                var destMod = mods.get(dest);
                if (!destMod.srcs.contains(mod.name)) {
                    destMod.srcs.add(mod.name);
                }
                if (destMod instanceof ConjMod) {
                    ((ConjMod) destMod).srcPulses.put(mod.name, false);
                }
            }
        }

        var result = part1 ? doPart1(mods) : doPart2(mods);
        System.out.println(result);
    }

    private static long doPart1(Map<String, Mod> mods) {
        var totalLow = 0L;
        var totalHigh = 0L;
        for (var k = 0; k < 1000; k++) {
            var results = pressButton(mods);
            totalLow += results[0];
            totalHigh += results[1];
        }
        return totalLow * totalHigh;
    }

    private static long doPart2(Map<String, Mod> mods) {
        var rx = mods.get("rx");
        var parent = mods.get(rx.srcs.getFirst());
        var grandparent = parent.srcs.stream().map(mods::get).toList();
        var grandparentFirstPress = new HashMap<String, Long>();
        for (var grandchild : grandparent) {
            grandparentFirstPress.put(grandchild.name, 0L);
        }

        var presses = 0L;
        while (grandparentFirstPress.values().stream().anyMatch(x -> x == 0)) {
            grandparent.forEach(x -> x.sentHighPulses = 0);
            pressButton(mods);
            presses++;

            for (var grandchild : grandparent) {
                if (grandchild.sentHighPulses == 1 && grandparentFirstPress.get(grandchild.name) == 0) {
                    grandparentFirstPress.put(grandchild.name, presses);
                }
            }
        }

        var product = 1L;
        for (var value : grandparentFirstPress.values()) {
            product *= value;
        }
        return product;
    }

    private static long[] pressButton(Map<String, Mod> mods) {
        var lowPulses = 0L;
        var highPulses = 0L;
        Queue<Pulse> queue = new LinkedList<>();
        queue.add(new Pulse("button", "broadcaster", false));

        while (!queue.isEmpty()) {
            var size = queue.size();
            for (var i = 0; i < size; i++) {
                var pulse = queue.remove();
                if (pulse.high) {
                    highPulses++;
                } else {
                    lowPulses++;
                }

                var mod = mods.get(pulse.dest);
                if (mod instanceof FlipMod flip) {
                    if (!pulse.high) {
                        flip.on = !flip.on;
                        for (var dest : flip.dests) {
                            queue.add(new Pulse(pulse.dest, dest, flip.on));
                            if (flip.on) {
                                mod.sentHighPulses++;
                            }
                        }
                    }
                } else if (mod instanceof ConjMod conj) {
                    conj.srcPulses.put(pulse.src, pulse.high);
                    for (var dest : conj.dests) {
                        var sendHighPulse = !conj.isAllHigh();
                        queue.add(new Pulse(pulse.dest, dest, sendHighPulse));
                        if (sendHighPulse) {
                            mod.sentHighPulses++;
                        }
                    }
                } else {
                    for (var dest : mod.dests) {
                        queue.add(new Pulse(pulse.dest, dest, pulse.high));
                        if (pulse.high) {
                            mod.sentHighPulses++;
                        }
                    }
                }
            }
        }
        return new long[] { lowPulses, highPulses };
    }

    private record Pulse(String src, String dest, boolean high) {}

    private static class Mod {

        String name;
        List<String> srcs;
        List<String> dests;
        int sentHighPulses;

        public Mod(String name, String[] dests) {
            this.name = name;
            this.dests = Arrays.stream(dests).toList();
            srcs = new ArrayList<>();
        }
    }

    private static class FlipMod extends Mod {

        boolean on;

        FlipMod(String name, String[] destinations) {
            super(name, destinations);
        }
    }

    private static class ConjMod extends Mod {

        final Map<String, Boolean> srcPulses;

        ConjMod(String name, String[] destinations) {
            super(name, destinations);
            srcPulses = new HashMap<>();
        }

        boolean isAllHigh() {
            var high = true;
            for (var src : srcPulses.values()) {
                high &= src;
            }
            return high;
        }
    }
}
