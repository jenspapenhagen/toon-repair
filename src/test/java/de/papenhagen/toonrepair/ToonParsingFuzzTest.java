package de.papenhagen.toonrepair;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThatCode;

class ToonParsingFuzzTest {
    private static final int NUMBER_OF_FUZZ_TEST_CASES = 1_000; // number of fuzzed TOONs
    private static final Random RANDOM = new Random(0xC0FFEE);

    private static final String VALID_TOON = """
        name: "Alice"
        age: 42
        tags[3,]:
          "a","b","c"
        """;

    @RepeatedTest(NUMBER_OF_FUZZ_TEST_CASES)
    @DisplayName("fuzzed TOON input never crashes parser")
    void givenFuzzedToon_whenParsed_thenNeverCrashes() {
        // Given
        final String fuzzed = fuzz();
        System.out.println("[DEBUG_LOG] Fuzzed input: " + fuzzed.replace("\t", "\\t").replace("\n", "\\n"));

        // When / Then
        assertThatCode(() -> ToonRepair.parse(fuzzed)).doesNotThrowAnyException();
    }

    /**
     * Mutates input via random operations to produce a fuzzed string
     */
    private static String fuzz() {
        final StringBuilder stringBuilder = new StringBuilder(VALID_TOON);

        final int operations = RANDOM.nextInt(5) + 1;
        for (int i = 0; i < operations; i++) {
            applyRandomMutation(stringBuilder);
        }

        return stringBuilder.toString();
    }

    private static void applyRandomMutation(StringBuilder stringBuilder) {
        if (stringBuilder.isEmpty()) {
            return;
        }

        final int choice = RANDOM.nextInt(6);
        final int pos = RANDOM.nextInt(stringBuilder.length());

        switch (choice) {
            case 0 -> // remove character
                stringBuilder.deleteCharAt(pos);

            case 1 -> // duplicate character
                stringBuilder.insert(pos, stringBuilder.charAt(pos));

            case 2 -> // remove colon
                removeFirst(stringBuilder, ':');

            case 3 -> // break array header
                replaceFirst(stringBuilder, "[", "[" + randomGarbage());

            case 4 -> // truncate string
                truncateAfterQuote(stringBuilder);

            case 5 -> // inject delimiter
                stringBuilder.insert(pos, randomDelimiter());
            default -> throw new IllegalStateException("Unexpected value: " + choice);
        }
    }

    private static void removeFirst(StringBuilder stringBuilder, char c) {
        final int idx = stringBuilder.indexOf(String.valueOf(c));
        if (idx >= 0) {
            stringBuilder.deleteCharAt(idx);
        }
    }

    private static void replaceFirst(StringBuilder stringBuilder, String target, String replacement) {
        final int idx = stringBuilder.indexOf(target);
        if (idx >= 0) {
            stringBuilder.replace(idx, idx + target.length(), replacement);
        }
    }

    private static void truncateAfterQuote(StringBuilder stringBuilder) {
        final int idx = stringBuilder.indexOf("\"");
        // Truncates string after the first quote if present
        if (idx >= 0 && idx + 1 < stringBuilder.length()) {
            stringBuilder.delete(idx + 1, stringBuilder.length());
        }
    }

    private static char randomDelimiter() {
        return switch (RANDOM.nextInt(3)) {
            case 0 -> ',';
            case 1 -> '|';
            default -> '\t';
        };
    }

    private static String randomGarbage() {
        return switch (RANDOM.nextInt(3)) {
            case 0 -> "abc";
            case 1 -> ";;;";
            default -> "";
        };
    }
}
