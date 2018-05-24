package seng302.Utilities.Web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import seng302.Client;
import seng302.Utilities.Enums.Gender;

import com.google.api.client.util.Key;

/**
 * Handles parsing the response from a drug interactions API request and determining which symptoms apply to a given
 * client.
 */
public class DrugInteractionsResponse {

    private static final Pattern AGE_RANGE_PATTERN
            = Pattern.compile("^(?:(?<nan>nan)|(?<singlePlus>\\d+)\\+|(?<lowerBound>\\d+)-(?<upperBound>\\d+))$");

    private static final Pattern DURATION_RANGE_PATTERN
            = Pattern.compile("^(?:(?:<\\s+(?<lessMonth>\\d+)\\s+months?)|"
            + "(?:<\\s+(?<lessYear>\\d+)\\s+years?)|"
            + "(?:(?<greaterMonth>\\d+)\\+ months?)|"
            + "(?:(?<greaterYear>\\d+)\\+ years?)|"
            + "(?:(?<lowMonth>\\d+)\\s+-\\s+(?<highMonth>\\d+)\\s+months?)|"
            + "(?:(?<lowYear>\\d+)\\s+-\\s+(?<highYear>\\d+)\\s+years?))$");
    public static final DrugInteractionsResponse EMPTY = new DrugInteractionsResponse();

    @Key("co_existing_conditions")
    private Map<String, Integer> coexistingConditions = new HashMap<>();

    @Key("reports")
    private Map<String, Integer> reports = new HashMap<>();

    @Key("age_interaction")
    private Map<String, List<String>> ageInteraction = new HashMap<>();

    @Key("duration_interaction")
    private Map<String, List<String>> durationInteraction = new HashMap<>();

    @Key("gender_interaction")
    private Map<String, List<String>> genderInteraction = new HashMap<>();

    /**
     * Calculates a list of possible drug interactions given a client.
     */
    public List<String> calculateClientInteractions(Client client) {
        int clientAge = client.getAge();
        Set<String> ageInteractions = calculateAgeInteractions(clientAge);

        Gender clientGender = client.getGender();
        Set<String> genderInteractions = calculateGenderInteractions(clientGender);

        // Get the intersection
        ageInteractions.retainAll(genderInteractions);

        Map<String, List<String>> interactions = new HashMap<>();
        for (String interaction : ageInteractions) {
            interactions.put(interaction, new ArrayList<>());
        }

        calculateDurations(interactions);

        return prettifyDurations(interactions);
    }

    /**
     * Converts a list of durations into a human readable format.
     */
    private static List<String> prettifyDurations(Map<String, List<String>> interactions) {
        List<String> result = new ArrayList<>();

        for (Map.Entry<String, List<String>> interactionEntry : interactions.entrySet()) {
            String interaction = interactionEntry.getKey();
            List<String> durations = interactionEntry.getValue();

            if (durations.isEmpty() || (durations.size() == 1 && "not specified".equals(durations.get(0)))) {
                result.add(interaction);
            } else {
                result.add(String.format("%s (%s)", interaction, prettifyDuration(durations)));
            }
        }

        return result;
    }

    /**
     * Given a list of durations, in the format x - y years/months, returns a human readable string.
     */
    private static String prettifyDuration(Iterable<String> durations) {
        List<IntPair> intDurations = new ArrayList<>();

        for (String duration : durations) {
            if ("not specified".equals(duration)) {
                continue;
            }

            intDurations.add(parseDurationRange(duration));
        }

        intDurations.sort(Comparator.comparingInt(lhs -> lhs.value1));

        // Merge adjacent pairs into one
        for (int i = 0; i < intDurations.size() - 1; i++) {
            IntPair lhs = intDurations.get(i);
            IntPair rhs = intDurations.get(i + 1);

            if (lhs.value2 == rhs.value1) {
                IntPair newPair = new IntPair(lhs.value1, rhs.value2);
                intDurations.set(i, newPair);
                intDurations.remove(i + 1);
                i--;
            }
        }

        return intDurations.stream()
                .map(DrugInteractionsResponse::durationRangeToString)
                .collect(Collectors.joining(", "));
    }

    private static String durationRangeToString(IntPair duration) {
        if (duration.value1 == 0) {
            if (duration.value2 < 12 || duration.value2 % 12 != 0) {
                return String.format("< %d month%s", duration.value2, duration.value2 == 1 ? "" : "s");
            }

            return String.format("< %d year%s", duration.value2 / 12, duration.value2 == 12 ? "" : "s");
        }

        if (duration.value2 == Integer.MAX_VALUE) {
            if (duration.value1 < 12 || duration.value1 % 12 != 0) {
                return String.format("%d+ months", duration.value1);
            }

            return String.format("%d+ years", duration.value1 / 12);
        }

        if (duration.value1 < 12 && duration.value2 > 24 && duration.value2 % 12 == 0) {
            return String.format("%d %s - %d years",
                    duration.value1,
                    duration.value1 == 1 ? "month" : "months",
                    duration.value2 / 12);
        }

        // Converts month range to year range
        if (duration.value1 >= 12 && duration.value1 % 12 == 0 && duration.value2 % 12 == 0) {
            return String.format("%d - %d years", duration.value1 / 12, duration.value2 / 12);
        }

        return String.format("%d - %d months", duration.value1, duration.value2);
    }

    /**
     * Calculates the durations of the drug interactions, storing them in a list.
     */
    private void calculateDurations(Map<String, List<String>> interactions) {
        for (Map.Entry<String, List<String>> durationEntry : durationInteraction.entrySet()) {
            for (Entry<String, List<String>> interactionEntries : interactions.entrySet()) {
                if (durationEntry.getValue().contains(interactionEntries.getKey())) {
                    interactionEntries.getValue().add(durationEntry.getKey());
                }
            }
        }
    }

    /**
     * Calculates the intersection of the drug interactions for a given age.
     */
    private Set<String> calculateAgeInteractions(int age) {
        Set<String> result = new HashSet<>();

        for (Map.Entry<String, List<String>> pair : ageInteraction.entrySet()) {
            IntPair range = parseAgeRange(pair.getKey());
            if (range.value1 <= age && range.value2 >= age) {
                result.addAll(pair.getValue());
            }
        }

        return result;
    }

    /**
     * Calculates the drug interactions for the given gender.
     * Will calculate the intersection of male and female if the given gender is non-binary or unspecified.
     */
    private Set<String> calculateGenderInteractions(Gender gender) {
        Set<String> results = new HashSet<>();

        switch (gender) {
            case MALE:
                results.addAll(genderInteraction.get("male"));
                break;
            case FEMALE:
                results.addAll(genderInteraction.get("female"));
                break;
            default:
                results.addAll(genderInteraction.get("male"));
                results.addAll(genderInteraction.get("female"));
                break;
        }

        return results;
    }

    /**
     * Converts a string in the format 10-20 into a range tuple.
     * Will convert nan to 0-0x7FFFFFFF and 30+ into 30-0x7FFFFFFF.
     * @param range A string describing a number range, in the format 10-20, 30+, or nan/
     */
    private static IntPair parseAgeRange(String range) {
        Matcher matcher = AGE_RANGE_PATTERN.matcher(range);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Range is not an expected value");
        }

        String nan = matcher.group("nan");

        if (nan != null) {
            return new IntPair(0, Integer.MAX_VALUE);
        }

        String singlePlus = matcher.group("singlePlus");
        if (singlePlus != null) {
            return new IntPair(Integer.parseInt(singlePlus), Integer.MAX_VALUE);
        }

        String lowerBound = matcher.group("lowerBound");
        String upperBound = matcher.group("upperBound");

        return new IntPair(Integer.parseInt(lowerBound), Integer.parseInt(upperBound));
    }

    /**
     * Converts a duration to a range tuple.
     */
    private static IntPair parseDurationRange(String duration) {
        Matcher matcher = DURATION_RANGE_PATTERN.matcher(duration);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Duration is not an expected value");
        }

        String lessMonth = matcher.group("lessMonth");
        if (lessMonth != null) {
            return new IntPair(0, Integer.parseInt(lessMonth));
        }

        String lessYear = matcher.group("lessYear");
        if (lessYear != null) {
            return new IntPair(0, Integer.parseInt(lessYear) * 12);
        }

        String greaterMonth = matcher.group("greaterMonth");
        if (greaterMonth != null) {
            return new IntPair(Integer.parseInt(greaterMonth), Integer.MAX_VALUE);
        }

        String greaterYear = matcher.group("greaterYear");
        if (greaterYear != null) {
            return new IntPair(Integer.parseInt(greaterYear) * 12, Integer.MAX_VALUE);
        }

        String lowMonth = matcher.group("lowMonth");
        String highMonth = matcher.group("highMonth");
        if (lowMonth != null && highMonth != null) {
            return new IntPair(Integer.parseInt(lowMonth), Integer.parseInt(highMonth));
        }

        String lowYear = matcher.group("lowYear");
        String highYear = matcher.group("highYear");
        if (lowYear != null && highYear != null) {
            return new IntPair(Integer.parseInt(lowYear) * 12, Integer.parseInt(highYear) * 12);
        }

        throw new IllegalStateException("Should never get here");
    }

    /**
     * A tuple of ints.
     */
    private static class IntPair {

        public final int value1;
        public final int value2;

        public IntPair(int value1, int value2) {
            this.value1 = value1;
            this.value2 = value2;
        }
    }
}
