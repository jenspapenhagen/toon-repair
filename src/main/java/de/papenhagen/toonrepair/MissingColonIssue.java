
package de.papenhagen.toonrepair;

import org.antlr.v4.runtime.Token;

/**
 * Handles missing colon issues in TOON files.
 * This class attempts to fix syntax errors where a colon (:) is expected but missing.
 */
final class MissingColonIssue implements SyntaxIssue {

    private final Token token;

    /**
     * Creates a new MissingColonIssue.
     *
     * @param token the token where the colon is missing
     */
    MissingColonIssue(final Token token) {
        this.token = token;
    }

    /**
     * Inserts a missing colon into the source string at the token position.
     *
     * @param source the original source string
     * @return the repaired source string
     */
    @Override
    public String apply(final String source) {
        final int pos = token.getStopIndex();
        if (pos < 0 || pos >= source.length()) {
            return source;
        }

        // Avoid adding redundant colon if it's already there
        if (pos + 1 < source.length() && source.charAt(pos + 1) == ':') {
            return source;
        }

        // Check if there is already a colon or if we are at the end of the line
        // We want to insert it immediately after the token text in the original source
        return source.substring(0, pos + 1) + ':' + source.substring(pos + 1);
    }

    @Override
    public int getStartIndex() {
        return token.getStartIndex();
    }
}
