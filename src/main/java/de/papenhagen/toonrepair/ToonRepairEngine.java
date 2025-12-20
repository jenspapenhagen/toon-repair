
package de.papenhagen.toonrepair;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Engine responsible for collecting syntax issues and applying repairs to TOON source code.
 */
public final class ToonRepairEngine {

    private final List<SyntaxIssue> issues = new ArrayList<>();

    /**
     * Registers a syntax error found during parsing.
     *
     * @param parser the parser that encountered the error
     * @param token  the token where the error occurred
     * @param ex     the recognition exception
     */
    public void registerSyntaxError(final Parser parser, final Token token, final RecognitionException ex) {
        issues.add(SyntaxIssue.from(parser, token));
    }

    /**
     * Registers an inline recovery event.
     *
     * @param parser the parser that performed the recovery
     * @param token  the token where recovery happened
     */
    public void registerInlineRecovery(final Parser parser, final Token token) {
        issues.add(SyntaxIssue.from(parser, token));
    }

    /**
     * Checks if any syntax issues have been registered.
     *
     * @return {@code true} if there are issues, {@code false} otherwise
     */
    public boolean hasIssues() {
        return !issues.isEmpty();
    }

    /**
     * Applies all registered repairs to the provided source string.
     * Repairs are applied from back to front to maintain index validity.
     *
     * @param source the original source code
     * @return the repaired source code
     */
    public String repair(final String source) {
        final List<SyntaxIssue> sortedIssues = new ArrayList<>(issues);
        // Remove duplicates or overlapping issues to prevent double-applying repairs at same start index
        final List<SyntaxIssue> uniqueIssues = new ArrayList<>();
        int lastStart = -1;
        
        sortedIssues.sort(Comparator.comparingInt(SyntaxIssue::getStartIndex).reversed());
        
        for (final SyntaxIssue issue : sortedIssues) {
            // Only add if it doesn't overlap with the last added issue's range
            if (issue.getStartIndex() != lastStart) {
                // Heuristic: Prefer more specific issues if they start at the same position
                uniqueIssues.add(issue);
                lastStart = issue.getStartIndex();
            }
        }

        String result = source;
        for (final SyntaxIssue issue : uniqueIssues) {
            result = issue.apply(result);
        }
        issues.clear(); // Clear issues after applying
        return result;
    }
}
