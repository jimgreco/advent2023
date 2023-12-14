import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: Sorting
 *         Each hand is parsed and added to a list.
 *         Each card in the hand is assigned a value of 2 - 14 (2, 3, ..., Q, K).
 *         Each card is counted and the hand is then ranked from 1 - 7 depending on the strength of the hand: high card,
 *         pair, ..., four of a kind, five of a kind.
 *         The list is then sorted by rank and then the value of each card in order.
 *     <li>Part 2: Sorting
 *         The same general strategy for part 1 is done.
 *         The introduction of jokers changes the assigned value of 'J' to 1.
 *         Computing the rank of the hand is changed to incorporate the possibility of jokers.
 * </ul>
 */
public class Day07 {

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(Path.of(args[1]));

        var hands = new ArrayList<Hand>();
        for (var line : lines) {
            var cards = line.split(" ")[0];
            var bid = Integer.parseInt(line.split(" ")[1]);
            hands.add(part1 ? Hand.createHandForPart1(cards, bid) : Hand.createHandForPart2(cards, bid));
        }

        hands.sort((o1, o2) -> {
            var c = Integer.compare(o1.rank, o2.rank);
            if (c != 0) {
                return c;
            }
            for (var i = 0; i < o1.cardValues.length; i++) {
                c = Integer.compare(o1.cardValues[i], o2.cardValues[i]);
                if (c != 0) {
                    return c;
                }
            }
            return 0;
        });

        var sum = 0L;
        for (var i = 0 ; i < hands.size(); i++) {
            sum += (long) (i + 1) * hands.get(i).bid;
        }
        System.out.println(sum);
    }

    private static class Hand {

        final int bid;
        final int[] cardValues;
        final int rank;

        private Hand(int bid, int[] cardValues, int rank) {
            this.bid = bid;
            this.cardValues = cardValues;
            this.rank = rank;
        }

        static Hand createHandForPart1(String cards, int bid) {
            var cardValues = new int[cards.length()];
            for (var i = 0; i < cardValues.length; i++) {
                var c = cards.charAt(i);
                if (c >= '2' && c <= '9') {
                    cardValues[i] = c - '0';
                } else if (c == 'T') {
                    cardValues[i] = 10;
                } else if (c == 'J') {
                    cardValues[i] = 11;
                } else if (c == 'Q') {
                    cardValues[i] = 12;
                } else if (c == 'K') {
                    cardValues[i] = 13;
                } else if (c == 'A') {
                    cardValues[i] = 14;
                }
            }

            var cardCount = new int[15];
            for (var card : cardValues) {
                cardCount[card]++;
            }
            Arrays.sort(cardCount);
            var firstValue = cardCount[cardCount.length - 1];
            var secondValue = cardCount[cardCount.length - 2];
            var rank = 1;
            if (firstValue == 5) {
                rank = 7;
            } else if (firstValue == 4) {
                rank = 6;
            } else if (firstValue == 3 && secondValue == 2) {
                rank = 5;
            } else if (firstValue == 3) {
                rank = 4;
            } else if (firstValue == 2 && secondValue == 2) {
                rank = 3;
            } else if (firstValue == 2) {
                rank = 2;
            }

            return new Hand(bid, cardValues, rank);
        }

        static Hand createHandForPart2(String cards, int bid) {
            var cardValues = new int[cards.length()];
            for (var i = 0; i < cardValues.length; i++) {
                var c = cards.charAt(i);
                if (c == 'J') {
                    cardValues[i] = 1;
                } else if (c >= '2' && c <= '9') {
                    cardValues[i] = c - '0';
                } else if (c == 'T') {
                    cardValues[i] = 10;
                } else if (c == 'Q') {
                    cardValues[i] = 12;
                } else if (c == 'K') {
                    cardValues[i] = 13;
                } else if (c == 'A') {
                    cardValues[i] = 14;
                }
            }

            var cardCount = new int[15];
            for (var card : cardValues) {
                cardCount[card]++;
            }

            var rank = 1;
            var jokers = cardCount[1];
            cardCount[1] = 0;
            Arrays.sort(cardCount);

            if (jokers == 5) {
                // corner case for all jokers
                rank = 7;
            } else {
                var firstValue = cardCount[cardCount.length - 1];
                var secondValue = cardCount[cardCount.length - 2];

                if (firstValue + jokers == 5) {
                    rank = 7;
                } else if (firstValue + jokers == 4) {
                    rank = 6;
                } else if (firstValue + jokers == 3 && secondValue == 2
                        || firstValue == 3 && secondValue + jokers == 2) {
                    rank = 5;
                } else if (firstValue + jokers == 3) {
                    rank = 4;
                } else if (firstValue + jokers == 2 && secondValue == 2
                        || firstValue == 2 && secondValue + jokers == 2) {
                    rank = 3;
                } else if (firstValue + jokers == 2) {
                    rank = 2;
                }
            }

            return new Hand(bid, cardValues, rank);
        }
    }
}
