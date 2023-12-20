import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1 and 2: Dijkstras algorithm.
 *         The grid is modeled as a weighted graph with each cell representing a node and the value of the cell
 *         representing the weight of the edges into the node.
 *         The graph is traversed using Dijstras algorithm to find the shortest path from the node represented by the
 *         top left cell to the node represented by the bottom right cell.
 *         The only difference between part 1 and part 2 is the minimum number of movements before allowing a turn
 *         (1 and 4) and the maximum number of movements before a turn is required (3 and 10).
 * </ul>
 */
public class Day17 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(java.nio.file.Path.of(args[1]));

        var minDirCount = part1 ? 1 : 4;
        var maxDirCount = part1 ? 3 : 10;

        var rows = lines.size();
        var cols = lines.getFirst().length();
        var graph = new int[rows][cols];
        for (var i = 0; i < rows; i++) {
            for (var j = 0; j < cols; j++) {
                graph[i][j] = lines.get(i).charAt(j) - '0';
            }
        }

        var queue = new PriorityQueue<Path>(Comparator.comparingInt(a -> a.weight));
        var start1 = new Path(new State(0, 1, Direction.RIGHT, 1), graph[0][1]);
        var start2 = new Path(new State(1, 0, Direction.DOWN, 1), graph[1][0]);
        queue.add(start1);
        queue.add(start2);
        var stateToLowestHeat = new HashMap<State, Integer>();

        while (!queue.isEmpty()) {
            var path = queue.remove();
            var state = path.state;
            var row = state.row;
            var col = state.col;
            var dirCount = state.dirCount;
            if (row < 0 || row == rows || col < 0 || col == cols) {
                continue;
            }

            var weight = path.weight;
            var value = stateToLowestHeat.getOrDefault(state, Integer.MAX_VALUE);
            if (weight < value) {
                stateToLowestHeat.put(state, weight);

                var up = row - 1 >= 0 ? graph[row - 1][col] : 0;
                var down = row + 1 < graph.length ? graph[row + 1][col] : 0;
                var left = col - 1 >= 0 ? graph[row][col - 1] : 0;
                var right = col + 1 < graph.length ? graph[row][col + 1] : 0;

                var dir = state.dir;

                if (dirCount + 1 <= maxDirCount) {
                    switch (dir) {
                        case UP -> queue.add(new Path(new State(row - 1, col, dir, dirCount + 1), weight + up));
                        case RIGHT -> queue.add(new Path(new State(row, col + 1, dir, dirCount + 1), weight + right));
                        case DOWN -> queue.add(new Path(new State(row + 1, col, dir, dirCount + 1), weight + down));
                        case LEFT -> queue.add(new Path(new State(row, col - 1, dir, dirCount + 1), weight + left));
                    }
                }

                if (dirCount >= minDirCount) {
                    switch (dir) {
                        case DOWN, UP -> {
                            queue.add(new Path(new State(row, col - 1, Direction.LEFT, 1), weight + left));
                            queue.add(new Path(new State(row, col + 1, Direction.RIGHT, 1), weight + right));
                        }
                        case LEFT, RIGHT -> {
                            queue.add(new Path(new State(row - 1, col, Direction.UP, 1), weight + up));
                            queue.add(new Path(new State(row + 1, col, Direction.DOWN, 1), weight + down));
                        }
                    }
                }
            }
        }

        var lowest = Integer.MAX_VALUE;
        for (var entry : stateToLowestHeat.entrySet()) {
            var state = entry.getKey();
            if (state.row == rows - 1 && state.col == cols - 1 && state.dirCount >= minDirCount) {
                lowest = Math.min(entry.getValue(), lowest);
            }
        }

        System.out.println(lowest);
    }

    private static class Path {

        final State state;
        final int weight;

        private Path(State state, int weight) {
            this.state = state;
            this.weight = weight;
        }
    }

    private static class State {

        final int row;
        final int col;
        final Direction dir;
        final int dirCount;

        private State(int row, int col, Direction dir, int dirCount) {
            this.row = row;
            this.col = col;
            this.dir = dir;
            this.dirCount = dirCount;
        }

        @Override
        public boolean equals(Object o) {
            var state = (State) o;
            return row == state.row && col == state.col && dir == state.dir && dirCount == state.dirCount;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col, dir, dirCount);
        }
    }

    private enum Direction { UP, RIGHT, DOWN, LEFT };
}
