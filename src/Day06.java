import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: Binary search.
 *         The distance the boat goes in the race is y(x) = x * (c - x) = -x^2 + cx, where x is the time spent winding
 *         the boat and c is the length of the race.
 *         Solve for the two values of x to find the lower and upper bound on the time wound.
 *         A discrete solution can be found using two binary searches from the midpoint of c since the derivative of
 *         the function is y'(x) = -2x + c and solving for y'(x) = 0 results in x = c / 2.
 *     <li>Part 2: Binary search.
 *         No changes required to the algorithm.
 * </ul>
 */
public class Day06 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));

        List<Long> times;
        List<Long> distances;
        if (part1) {
            times = Arrays.stream(lines.getFirst().split(":")[1].trim().split("\\s+"))
                    .map(Long::parseLong).toList();
            distances = Arrays.stream(lines.get(1).split(":")[1].trim().split("\\s+"))
                    .map(Long::parseLong).toList();
        } else {
            times = List.of(Long.parseLong(
                    String.join("", lines.getFirst().split(":")[1].trim().split("\\s+"))));
            distances = List.of(Long.parseLong(
                    String.join("", lines.get(1).split(":")[1].trim().split("\\s+"))));
        }

        var product = 1L;
        for (var i = 0; i < times.size(); i++) {
            var raceTime = times.get(i);
            var recordDistance = distances.get(i);

            var lowerBound = 0L;
            var higherBound = 0L;

            var low = 0L;
            var high = raceTime / 2; // round down
            while (low <= high) {
                var timeHeld = low + (high - low) / 2; // mid
                var timeMove = raceTime - timeHeld;
                var distanceTraveled = timeHeld * timeMove;
                if (distanceTraveled >= recordDistance) {
                    lowerBound = timeHeld;
                    high = timeHeld - 1;
                } else {
                    low = timeHeld + 1;
                }
            }

            low = raceTime / 2 + (raceTime % 2 == 1 ? 1 : 0); // round up
            high = raceTime;
            while (low <= high) {
                var timeHeld = low + (high - low) / 2; // mid
                var timeMove = raceTime - timeHeld;
                var distanceTraveled = timeHeld * timeMove;
                if (distanceTraveled >= recordDistance) {
                    higherBound = timeHeld;
                    low = timeHeld + 1;
                } else {
                    high = timeHeld - 1;
                }
            }

            product *= higherBound - lowerBound + 1;
        }

        System.out.println(product);
    }
}
