import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Day12 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));

        var sum = 0L;
        for (var line : lines) {
            var springs = line.split(" ")[0];
            var broken = Arrays.stream(line.split(" ")[1].split(",")).map(Integer::parseInt).toList();

            if (!part1) {
                var foldedSprings = springs;
                var foldedBroken = new ArrayList<>(broken);
                for (var i = 0; i < 4; i++) {
                    foldedSprings += "?" + springs;
                    foldedBroken.addAll(broken);
                }
                springs = foldedSprings;
                broken = foldedBroken;
            }

            var b = new int[broken.size()];
            for (var i = 0; i < b.length; i++) {
                b[i] = broken.get(i);
            }

            var memo = new HashMap<State, Long>();
            sum += backtrack(memo, new State(springs.toCharArray(), 0, b, 0));
        }
        System.out.println(sum);
    }

    private static long backtrack(HashMap<State, Long> memo, State state) {
        if (state.springsIndex == state.springs.length) {
            return state.brokenIndex == state.broken.length || state.broken[state.broken.length - 1] == 0 ? 1 : 0;
        }

        var res = memo.get(state);
        if (res != null) {
            return res;
        }

        if (state.current == '.') {
            res = doOk(memo, state);
        } else if (state.current == '#') {
            res = doBroken(memo, state);
        } else {
            var springsCopy = Arrays.copyOf(state.springs, state.springs.length);
            springsCopy[state.springsIndex] = '.';
            res = doOk(memo, new State(springsCopy, state.springsIndex, state.broken, state.brokenIndex));
            springsCopy[state.springsIndex] = '#';
            res += doBroken(memo, new State(springsCopy, state.springsIndex, state.broken, state.brokenIndex));
        }

        memo.put(state, res);
        return res;
    }

    private static long doOk(HashMap<State, Long> memo, State state) {
        var prevBroken = state.prev == '#';
        if (prevBroken && state.broken[state.brokenIndex] != 0) {
            return 0;
        }
        return backtrack(memo, new State(state.springs, state.springsIndex + 1,
                state.broken, state.brokenIndex + (prevBroken ? 1 : 0)));
    }

    private static long doBroken(HashMap<State, Long> memo, State state) {
        if (state.brokenIndex == state.broken.length) {
            // too many groups
            return 0;
        }

        var numBroken = state.broken[state.brokenIndex];
        if (numBroken == 0) {
            // not enough in current group
            return 0;
        }

        var brokenCopy = Arrays.copyOf(state.broken, state.broken.length);
        brokenCopy[state.brokenIndex]--;
        return backtrack(memo, new State(state.springs, state.springsIndex + 1,
                brokenCopy, state.brokenIndex));
    }

    private static class State {

        final char[] springs;
        final char current;
        final char prev;
        final int springsIndex;
        final int[] broken;
        final int brokenIndex;

        State(char[] springs, int springsIndex, int[] broken, int brokenIndex) {
            this.springs = Arrays.copyOf(springs, springs.length);
            this.current = springsIndex < springs.length ? springs[springsIndex] : '.';
            this.prev = springsIndex > 0 ? springs[springsIndex - 1] : '.';
            this.springsIndex = springsIndex;
            this.broken = Arrays.copyOf(broken, broken.length);
            this.brokenIndex = brokenIndex;
        }

        @Override
        public boolean equals(Object o) {
            var state = (State) o;
            return current == state.current
                    && prev == state.prev
                    && springsIndex == state.springsIndex
                    && Arrays.equals(broken, state.broken)
                    && brokenIndex == state.brokenIndex;
        }

        @Override
        public int hashCode() {
            return Objects.hash(current, prev, springsIndex, Arrays.hashCode(broken), brokenIndex);
        }
    }
}
