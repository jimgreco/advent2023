import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Solution Description:
 * <ul>
 *     <li>Part 1: Conditional tree traversal
 *         The workflows are parsed into a sequence of rules.
 *         Each workflow can be thought of as a node in a tree with a sequence of rules.
 *         Each data set is passed to the root node, "in".
 *         For each node, the rules are evaluated in order, with the current data, and can result in a branch to another
 *         node or termination.
 *     <li>Part 2: DFS
 *         A depth-first search is done on the "in" workflow beginning with a range of all possible values (1 to 4000
 *         for each input).
 *         The range is then narrowed as nodes are traversed.
 *         For conditions, each branch is taken with a different range of values for the successful evaluation of the
 *         condition and the failure evaluation of the condition.
 * </ul>
 */
public class Day19 {

    private static final int[] XMAS = new int[128];

    static {
        XMAS['x'] = 0;
        XMAS['m'] = 1;
        XMAS['a'] = 2;
        XMAS['s'] = 3;
    }

    public static void main(String[] args) throws IOException {
        var part1 = args[0].equals("1");
        var lines = Files.readAllLines(java.nio.file.Path.of(args[1]));

        var workflows = new HashMap<String, String[]>();

        int i;
        for (i = 0; i < lines.size(); i++) {
            if (lines.get(i).isEmpty()) {
                i++;
                break;
            }
            var split = lines.get(i).split("\\{");
            var rules = split[1].substring(0, split[1].length() - 1).split(",");
            workflows.put(split[0], rules);
        }

        if (part1) {
            var accepted = 0;
            for (; i < lines.size(); i++) {
                var line = lines.get(i);
                var values = Arrays.stream(line.substring(1, line.length() - 1).split(","))
                        .map(x -> Integer.parseInt(x.split("=")[1]))
                        .toList();
                accepted += doWorkflows1(workflows, "in", values);
            }
            System.out.println(accepted);
        } else {
            var state = new State();
            List<State> acceptedStates = new LinkedList<State>();
            doWorkflows2(acceptedStates, workflows, "in", state);

            var sum = 0L;
            for (var accepted : acceptedStates) {
                long states = accepted.max[0] - accepted.min[0] + 1;
                for (var j = 1; j < accepted.min.length; j++) {
                    states *= accepted.max[j] - accepted.min[j] + 1;
                }
                sum += states;
            }

            System.out.println(sum);
        }
    }

    private static int doWorkflows1(Map<String, String[]> workflows, String workflowName, List<Integer> values) {
        var workflow = workflows.get(workflowName);
        for (var rule : workflow) {
            var dest = rule;

            if (rule.contains("<") || rule.contains(">")) {
                dest = rule.split(":")[1];
                var myValue = values.get(XMAS[rule.charAt(0)]);
                var ruleValue = Integer.parseInt(rule.split(":")[0].substring(2));
                if (rule.contains("<") && myValue >= ruleValue || rule.contains(">") && myValue <= ruleValue) {
                    continue;
                }
            }

            if (dest.equals("A")) {
                return values.stream().reduce(Integer::sum).orElseThrow();
            } else if (dest.equals("R")) {
                return 0;
            } else {
                return doWorkflows1(workflows, dest, values);
            }
        }
        throw new IllegalArgumentException();
    }

    private static void doWorkflows2(
            List<State> accepted, HashMap<String, String[]> workflows, String workflowName, State state) {
        var workflow = workflows.get(workflowName);
        for (var rule : workflow) {
            if (rule.contains("<")) {
                var dest = rule.split(":")[1];
                var ruleValue = Integer.parseInt(rule.split(":")[0].substring(2));
                var letter = XMAS[rule.charAt(0)];

                var success = state.copy();
                success.max[letter] = Math.min(state.max[letter], ruleValue - 1);

                if (dest.equals("A")) {
                    accepted.add(success);
                } else if (!dest.equals("R")) {
                    doWorkflows2(accepted, workflows, dest, success);
                }

                // failure state for next rule
                state.min[letter] = Math.max(state.min[letter], ruleValue);
            } else if (rule.contains(">")) {
                var dest = rule.split(":")[1];
                var ruleValue = Integer.parseInt(rule.split(":")[0].substring(2));
                var letter = XMAS[rule.charAt(0)];

                var success = state.copy();
                success.min[letter] = Math.max(state.min[letter], ruleValue + 1);

                if (dest.equals("A")) {
                    accepted.add(success);
                } else if (!dest.equals("R")) {
                    doWorkflows2(accepted, workflows, dest, success);
                }

                // failure state for next rule
                state.max[letter] = Math.min(state.max[letter], ruleValue);
            } else if (rule.equals("A")) {
                accepted.add(state);
            } else if (!rule.equals("R")) {
                doWorkflows2(accepted, workflows, rule, state);
            }
        }
    }

    private static class State {

        int[] min = { 1, 1, 1, 1 };
        int[] max = { 4000, 4000, 4000, 4000 };

        State copy() {
            var state = new State();
            state.min = Arrays.copyOf(min, min.length);
            state.max = Arrays.copyOf(max, max.length);
            return state;
        }
    }
}
