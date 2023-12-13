import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Day04 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");

        var lines = Files.readAllLines(Path.of(args[1]));
        var cardWins = new int[lines.size()];

        for (var i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            var raw = line.split(":")[1].split("\\|");
            var winners = Arrays.stream(raw[0].trim().split("\\s+"))
                    .map(x -> Integer.parseInt(x.trim()))
                    .collect(Collectors.toSet());
            var numbers = Arrays.stream(raw[1].trim().split("\\s+"))
                    .map(x -> Integer.parseInt(x.trim()))
                    .toList();
            var win = 0;
            for (var number : numbers) {
                if (winners.contains(number)) {
                    win++;
                }
            }
            cardWins[i] = win;
        }

        var sum = 0;
        if (part1) {
            for (var win : cardWins) {
                sum += win == 0 ? 0 : (1 << (win - 1));
            }
        } else {
            var memo = new HashMap<Integer, Integer>();
            for (var i = 0; i < cardWins.length; i++) {
                sum += recursive(cardWins, i, memo);
            }
        }

        System.out.println(sum);
    }

    private static int recursive(int[] cardWins, int card, Map<Integer, Integer> memo) {
        if (memo.containsKey(card)) {
            return memo.get(card);
        }

        var cards = 1;
        for (var i = 0; i < cardWins[card]; i++) {
            cards += recursive(cardWins, card + 1 + i, memo);
        }

        memo.put(card, cards);
        return cards;
    }
}
