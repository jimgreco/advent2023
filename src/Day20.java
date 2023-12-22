import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Day20 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(java.nio.file.Path.of(args[1]));

        var flipFlops = new HashMap<String, FlipFlopModule>();
        var conjunctions = new HashMap<String, ConjunctionModule>();
        String[] broadcaster = null;

        for (var line : lines) {
            var split = line.split(" -> ");
            var module = split[0];
            var type = module.charAt(0);
            var destinations = split[1].split(", ");

            var name = module.substring(1);
            if (type == '%') {
                flipFlops.put(name, new FlipFlopModule(name, destinations));
            } else if (type == '&') {
                conjunctions.put(name, new ConjunctionModule(name, destinations));
            } else {
                broadcaster = destinations;
            }
        }

        var rx = new FlipFlopModule("rx", new String[]{});
        flipFlops.put("rx", rx);
        for (var entry : flipFlops.entrySet()) {
            addSrcDest(entry.getKey(), entry.getValue(), flipFlops, conjunctions);
        }
        for (var entry : conjunctions.entrySet()) {
            addSrcDest(entry.getKey(), entry.getValue(), flipFlops, conjunctions);
        }

        var result = part1 ? doPart1(flipFlops, conjunctions, broadcaster)
                : doPart2(flipFlops, conjunctions, broadcaster);
        System.out.println(result);
    }

    private static void addSrcDest(
            String moduleName,
            Module module,
            HashMap<String, FlipFlopModule> flipFlops,
            HashMap<String, ConjunctionModule> conjunctions) {
        for (var dest : module.destinations) {
            List<String> sources = null;
            if (flipFlops.containsKey(dest)) {
                sources = flipFlops.get(dest).sources;
            } else if (conjunctions.containsKey(dest)) {
                sources = conjunctions.get(dest).sources;
                conjunctions.get(dest).sourcePulses.put(moduleName, false);
            }
            if (!sources.contains(moduleName)) {
                sources.add(moduleName);
            }
        }
    }

    private static long doPart1(
            HashMap<String, FlipFlopModule> flipFlops,
            HashMap<String, ConjunctionModule> conjunctions,
            String[] broadcaster) {
        var totalLow = 0L;
        var totalHigh = 0L;
        for (var k = 0; k < 1000; k++) {
            var results = pressButton(flipFlops, conjunctions, broadcaster);
            totalLow += results[0];
            totalHigh += results[1];
        }
        return totalLow * totalHigh;
    }

    private static long doPart2(
            HashMap<String, FlipFlopModule> flipFlops,
            HashMap<String, ConjunctionModule> conjunctions,
            String[] broadcaster) {
        var rx = flipFlops.get("rx");

        var presses = 0L;
        while (true) {
            rx.lowPulses = 0;
            pressButton(flipFlops, conjunctions, broadcaster);
            presses++;
            if (conjunctions.get("tj").sourcePulses.values().stream().anyMatch(x -> x)) {
                System.out.println(presses + ":" + conjunctions.get("tj").sourcePulses.values().stream().filter(x -> x).count());
            }
            if (rx.lowPulses == 1) {
                return presses;
            }
        }
    }

    private static long[] pressButton(
            HashMap<String, FlipFlopModule> flipFlops,
            HashMap<String, ConjunctionModule> conjunctions,
            String[] broadcaster) {
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

                if (flipFlops.containsKey(pulse.destination)) {
                    var module = flipFlops.get(pulse.destination);
                    if (!pulse.high) {
                        module.lowPulses++;
                        module.on = !module.on;
                        for (var dest : module.destinations) {
                            queue.add(new Pulse(pulse.destination, dest, module.on));
                        }
                    }
                } else if (conjunctions.containsKey(pulse.destination)) {
                    var module = conjunctions.get(pulse.destination);
                    module.sourcePulses.put(pulse.source, pulse.high);
                    for (var dest : module.destinations) {
                        queue.add(new Pulse(pulse.destination, dest, !module.isAllHigh()));
                    }
                } else if (pulse.destination.equals("broadcaster")) {
                    for (var dest : broadcaster) {
                        queue.add(new Pulse(pulse.destination, dest, pulse.high));
                    }
                }
            }
        }
        return new long[] { lowPulses, highPulses };
    }

    private record Pulse(String source, String destination, boolean high) {}

    private static class Module {

        String name;
        List<String> sources;
        List<String> destinations;
        int lowPulses;

        public Module(String name, String[] destinations) {
            this.name = name;
            this.destinations = Arrays.stream(destinations).toList();
            sources = new ArrayList<>();
        }
    }

    private static class FlipFlopModule extends Module {

        boolean on;

        FlipFlopModule(String name, String[] destinations) {
            super(name, destinations);
        }
    }

    private static class ConjunctionModule extends Module {

        final Map<String, Boolean> sourcePulses;

        ConjunctionModule(String name, String[] destinations) {
            super(name, destinations);
            sourcePulses = new HashMap<>();
        }

        boolean isAllHigh() {
            var high = true;
            for (var src : sourcePulses.values()) {
                high &= src;
            }
            return high;
        }
    }
}
