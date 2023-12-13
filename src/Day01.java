import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day01 {

    private static final List<String> NUMBERS = List.of(
            "zero", "one", "two", "three", "four",
            "five", "six", "seven", "eight", "nine");

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
        var num = 0;
        for (var i = 0; i < line.length(); i++) {
            var c = line.charAt(i);
            if (c >= '0' && c <= '9') {
                num = 10 * (c - '0');
                break;
            }
        }

        for (var i = line.length() - 1; i >= 0; i--) {
            var c = line.charAt(i);
            if (c >= '0' && c <= '9') {
                num += c - '0';
                break;
            }
        }
        return num;
    }

    private static int doPart2(String line) {
        var num = 0;

        for (var i = 0; i < line.length(); i++) {
            var c = line.charAt(i);
            if (c >= '0' && c <= '9') {
                num = 10 * (c - '0');
                break;
            } else {
                var last5 = i >= 4 ? line.substring(i - 4, i + 1) : "";
                var last4 = i >= 3 ? line.substring(i - 3, i + 1) : "";
                var last3 = i >= 2 ? line.substring(i - 2, i + 1) : "";

                if (NUMBERS.contains(last5)) {
                    num = 10 * NUMBERS.indexOf(last5);
                    break;
                } else if (NUMBERS.contains(last4)) {
                    num = 10 * NUMBERS.indexOf(last4);
                    break;
                } else if (NUMBERS.contains(last3)) {
                    num = 10 * NUMBERS.indexOf(last3);
                    break;
                }
            }
        }

        var len = line.length();
        for (var i = len - 1; i >= 0; i--) {
            var c = line.charAt(i);
            if (c >= '0' && c <= '9') {
                num += c - '0';
                break;
            } else {
                var last5 = i <= len - 5 ? line.substring(i, i + 5) : "";
                var last4 = i <= len - 4 ? line.substring(i, i + 4) : "";
                var last3 = i <= len - 3 ? line.substring(i, i + 3) : "";

                if (NUMBERS.contains(last5)) {
                    num += NUMBERS.indexOf(last5);
                    break;
                } else if (NUMBERS.contains(last4)) {
                    num += NUMBERS.indexOf(last4);
                    break;
                } else if (NUMBERS.contains(last3)) {
                    num += NUMBERS.indexOf(last3);
                    break;
                }
            }
        }
        return num;
    }
}