
package de.papenhagen.toonrepair;

import org.antlr.v4.runtime.Token;

/**
 * Handles unexpected or extraneous tokens by removing them from the source.
 */
final class UnexpectedTokenIssue implements SyntaxIssue {

    private final Token token;

    UnexpectedTokenIssue(final Token token) {
        this.token = token;
    }

    @Override
    public String apply(final String source) {
        final int start = token.getStartIndex();
        final int end = token.getStopIndex();
        if (start < 0 || end >= source.length()) {
            return source;
        }

        return source.substring(0, start) + source.substring(end + 1);
    }

    @Override
    public int getStartIndex() {
        return token.getStartIndex();
    }
}
