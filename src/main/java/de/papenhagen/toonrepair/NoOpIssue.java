
package de.papenhagen.toonrepair;

/**
 * A no-op repair that returns the source unchanged.
 */
final class NoOpIssue implements SyntaxIssue {
    @Override
    public String apply(String source) {
        return source;
    }

    @Override
    public int getStartIndex() {
        return -1;
    }
}
