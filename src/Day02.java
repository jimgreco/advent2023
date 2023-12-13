import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Day02 {

    private static final int RED = 12;
    private static final int GREEN = 13;
    private static final int BLUE = 14;

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));

        var sum = 0;
        for (var line : lines) {
            sum += part1 ? doPart1(line) : doPart2(line);
        }
        System.out.println(sum);
    }

    private static int doPart1(String line) {
        var sets = line.split(":")[1].split(";");
        for (var set : sets) {
            var rolls = set.split(",");
            for (var roll : rolls) {
                var value = Integer.parseInt(roll.trim().split(" ")[0]);
                if (roll.contains("red") && value > RED
                        || roll.contains("blue") && value > BLUE
                        || roll.contains("green") && value > GREEN) {
                    return 0;
                }
            }
        }
        return Integer.parseInt(line.split(":")[0].split(" ")[1]);
    }

    private static int doPart2(String line) {
        var minRed = 0;
        var minBlue = 0;
        var minGreen = 0;

        var sets = line.split(":")[1].split(";");
        for (var set : sets) {
            var rolls = set.split(",");
            for (var roll : rolls) {
                var value = Integer.parseInt(roll.trim().split(" ")[0]);
                if (roll.contains("red")) {
                    minRed = Math.max(minRed, value);
                } else if (roll.contains("blue")) {
                    minBlue = Math.max(minBlue, value);
                } else if (roll.contains("green")) {
                    minGreen = Math.max(minGreen, value);
                }
            }
        }

        return minRed * minBlue * minGreen;
    }
}
