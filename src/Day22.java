import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: 280 too low, 1105 too high
 * </ul>
 */
public class Day22 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(java.nio.file.Path.of(args[1]));

        var bricks = new ArrayList<Brick>();
        var brickMap = new HashMap<Integer, Brick>();
        for (var i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            var split = line.split("~");
            var low = Arrays.stream(split[0].split(",")).map(Integer::parseInt).toList();
            var high = Arrays.stream(split[1].split(",")).map(Integer::parseInt).toList();
            var brick = new Brick(low, high, i + 1);
            bricks.add(brick);
            brickMap.put(brick.id, brick);
        }
        bricks.sort(Comparator.comparingInt(b -> b.low[2]));

        var maxZs = new int[10][10];
        for (var brick : bricks) {
            var maxZ = 0;
            for (var x = brick.low[0]; x <= brick.high[0]; x++) {
                for (var y = brick.low[1]; y <= brick.high[1]; y++) {
                    maxZ = Math.max(maxZ, maxZs[x][y]);
                }
            }

            var zDiff = brick.high[2] - brick.low[2];
            brick.low[2] = maxZ + 1;
            brick.high[2] = maxZ + 1 + zDiff;

            for (var x = brick.low[0]; x <= brick.high[0]; x++) {
                for (var y = brick.low[1]; y <= brick.high[1]; y++) {
                    maxZs[x][y] = maxZ + 1 + zDiff;
                }
            }
        }

        var maxZ = bricks.stream().max(Comparator.comparingInt(x -> x.high[2])).orElseThrow().high[2];
        var space = new int[10][10][maxZ + 2];
        for (var brick : bricks) {
            for (var x = brick.low[0]; x <= brick.high[0]; x++) {
                for (var y = brick.low[1]; y <= brick.high[1]; y++) {
                    for (var z = brick.low[2]; z <= brick.high[2]; z++) {
                        space[x][y][z] = brick.id;
                    }
                }
            }
        }

        var bricksRemoved = 0;
        for (var brick : bricks) {
            var nextZ = brick.high[2] + 1;

            var brickNumToArea = new HashMap<Integer, Integer>();
            for (var x = brick.low[0]; x <= brick.high[0]; x++) {
                for (var y = brick.low[1]; y <= brick.high[1]; y++) {
                    var brickId = space[x][y][nextZ];
                    if (brickId != 0) {
                        var area = brickNumToArea.getOrDefault(brickId, 0);
                        brickNumToArea.put(brickId, area + 1);
                    }
                }
            }

            var removed = true;
            for (var entry : brickNumToArea.entrySet()) {
                var brickId = entry.getKey();
                var brickArea = brickMap.get(brickId).area;
                var nextBrickArea = brickNumToArea.getOrDefault(brickId, 0);
                if (nextBrickArea == brickArea) {
                    removed = false;
                    break;
                }
            }
            if (removed) {
                bricksRemoved++;
            }
        }

        System.out.println(bricksRemoved);
    }

    private static class Brick {

        final int id;
        final Integer[] low;
        final Integer[] high;
        final int area;

        Brick(List<Integer> low, List<Integer> high, int id) {
            this.low = low.toArray(new Integer[0]);
            this.high = high.toArray(new Integer[0]);
            this.id = id;
            area = (high.get(0) - low.get(0) + 1) * (high.get(1) - low.get(1) + 1);
        }
    }
}
