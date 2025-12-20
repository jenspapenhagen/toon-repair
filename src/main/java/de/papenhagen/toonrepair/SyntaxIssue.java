
package de.papenhagen.toonrepair;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;

import java.util.List;

/**
 * Represents a syntax issue found in a TOON file that can be automatically repaired.
 * This is a sealed interface with known implementations for specific issue types.
 */
public sealed interface SyntaxIssue permits MissingColonIssue, ArrayHeaderIssue, UnexpectedTokenIssue, NoOpIssue {

    /**
     * Applies the repair for this syntax issue to the given source string.
     *
     * @param source the original source string
     * @return the repaired source string
     */
    String apply(String source);

    /**
     * @return the start index of the issue in the source string
     */
    int getStartIndex();

    /**
     * Factory method to create a specific SyntaxIssue based on the parser state and token.
     *
     * @param parser the parser that encountered the issue
     * @param token  the token associated with the issue
     * @return a specific implementation of SyntaxIssue
     */
    static SyntaxIssue from(final Parser parser, final Token token) {
        final String context = parser.getContext().getText();
        final List<String> stack = parser.getRuleInvocationStack();
        final String ruleName = stack.isEmpty() ? "" : stack.get(0);

        // Array header issues
        if (context.contains("[") || context.contains("]") ||
            ruleName.contains("tabularHeader") || ruleName.contains("bracket")) {
            return new ArrayHeaderIssue(token);
        }

        // Extraneous tokens that can be safely removed
        if (token.getType() == ToonParser.TAB) {
            return new UnexpectedTokenIssue(token);
        }

        // Missing colon issue
        final int index = token.getTokenIndex();
        if (index > 0) {
            final Token prev = parser.getTokenStream().get(index - 1);
            // Only suggest missing colon if it looks like a key followed by something else
            if (prev.getType() == ToonParser.IDENT || prev.getType() == ToonParser.QUOTED_STRING || prev.getType() == ToonParser.UNQUOTED_VALUE) {
                // BUT only if NOT inside brackets!
                return new MissingColonIssue(prev);
            }
        }

        // If it's a known problematic fuzzer-injected token, remove it
        if (token.getType() == ToonParser.TAB) {
            return new UnexpectedTokenIssue(token);
        }

        // Default: Do nothing or return a dummy issue that doesn't change anything
        return new NoOpIssue();
    }
}
