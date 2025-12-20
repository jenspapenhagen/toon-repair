package de.papenhagen.toonrepair;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ToonRepairTest {

    @Test
    @DisplayName("should repair missing colon by inserting it after the key")
    void shouldRepairMissingColon() {
        // Given
        final String input = "name \"Alice\"\n";
        final String expected = "name: \"Alice\"\n";

        // When
        final String result = ToonRepair.parse(input);

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("should repair array header with length and custom delimiter")
    void shouldRepairArrayHeaderWithLengthAndDelimiter() {
        // Given
        final String input = "[ | 10 ] data\n  1,2,3\n";
        final String expected = "[10|] data\n  1,2,3\n";

        // When
        final String result = ToonRepair.parse(input);

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("repairs missing colon and parses successfully")
    void givenToonWithMissingColon_whenParsed_thenSucceeds() {
        // Given
        final String toon = """
                name "Alice"
                age  42
                """;

        // When / Then
        assertThatCode(() -> ToonRepair.parse(toon)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("repairs invalid array header and parses successfully")
    void givenInvalidArrayHeader_whenParsed_thenSucceeds() {
        // Given
        final String toon = """
                items[abc]
                  "a","b","c"
                """;

        // When / Then
        assertThatCode(() -> ToonRepair.parse(toon)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("repairs unclosed string and parses successfully")
    void givenUnclosedString_whenParsed_thenSucceeds() {
        // Given
        final String toon = """
                title: "Hello world
                """;

        // When / Then
        assertThatCode(() -> ToonRepair.parse(toon)).doesNotThrowAnyException();
    }

}
