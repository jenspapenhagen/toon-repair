
package de.papenhagen.toonrepair;

import org.antlr.v4.runtime.Token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles issues with array headers in TOON files.
 * An array header is expected to be in the format {@code [length delimiter]}.
 * This class attempts to normalize headers that deviate from this format.
 */
final class ArrayHeaderIssue implements SyntaxIssue {

    private static final Pattern HEADER = Pattern.compile("\\[(.*?)]");
    private final Token token;

    /**
     * Creates a new ArrayHeaderIssue.
     *
     * @param token the token identifying the malformed array header
     */
    ArrayHeaderIssue(final Token token) {
        this.token = token;
    }

    /**
     * Replaces an invalid array header with its normalized form.
     * The normalized form is {@code [length delimiter]}.
     *
     * @param source the original source string
     * @return the repaired source string
     */
    @Override
    public String apply(final String source) {
        int start = token.getStartIndex();
        int end = token.getStopIndex();
        if (start < 0) {
            return source;
        }
        // Ensure end is within bounds
        end = Math.min(end, source.length() - 1);

        // Expand to find the brackets [ ]
        // We look both ways because the token might be inside or after the brackets
        int actualStart = start;
        while (actualStart > 0 && source.charAt(actualStart) != '[') {
            actualStart--;
        }

        int actualEnd = (actualStart >= 0 && source.charAt(actualStart) == '[') ? actualStart : start;
        while (actualEnd < source.length() - 1 && source.charAt(actualEnd) != ']') {
            actualEnd++;
        }

        if (actualStart < 0 ||
                actualEnd >= source.length() ||
                source.charAt(actualStart) != '[' ||
                source.charAt(actualEnd) != ']') {
            return source;
        }

        final String fragment = source.substring(actualStart, actualEnd + 1);
        final Matcher matcher = HEADER.matcher(fragment);
        if (!matcher.find()) {
            return source;
        }

        final String content = matcher.group(1);
        String trimmedContent = content.trim();
        final int length = extractLength(trimmedContent);
        final char delimiter = extractDelimiter(trimmedContent);

        final String fixed = "[" + length + delimiter + "]";
        return source.substring(0, actualStart) + fixed.replaceAll("\\s+", "") + source.substring(actualEnd + 1);
    }

    @Override
    public int getStartIndex() {
        return token.getStartIndex();
    }

    /**
     * Extracts the length component from the array header content.
     *
     * @param content the raw content inside the brackets
     * @return the extracted length, or 0 if not found
     */
    private int extractLength(final String content) {
        for (final String part : content.split("[,|\\t ]")) {
            if (!part.isEmpty() && part.chars().allMatch(Character::isDigit)) {
                return Integer.parseInt(part);
            }
        }
        return 0;
    }

    /**
     * Extracts the delimiter character from the array header content.
     * Defaults to ',' if no recognized delimiter is found.
     *
     * @param content the raw content inside the brackets
     * @return the extracted delimiter character
     */
    private char extractDelimiter(final String content) {
        if (content.contains("|")) {
            return '|';
        }
        if (content.contains("\t")) {
            return '\t';
        }
        return ',';
    }
}
