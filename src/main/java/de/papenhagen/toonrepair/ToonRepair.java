package de.papenhagen.toonrepair;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * Service for parsing TOON files with automatic error recovery and repair.
 */
public class ToonRepair {

    /**
     * Private constructor to prevent instantiation.
     */
    private ToonRepair() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private static final String QUOTED_STRING_REGEX = "^\"(?:[^\"\\\\\\x00-\\x1F]|\\\\[\"\\\\/bfnrt])*\"$";

    /**
     * Parses TOON input with automatic repair-on-error.
     * This method attempts to parse the input multiple times, applying repairs
     * between attempts if syntax errors are detected.
     *
     * @param input the TOON content to parse
     * @throws IllegalStateException if repair does not converge within the allowed number of attempts
     */
    public static String parse(final String input) {
        String current = input.replaceAll(QUOTED_STRING_REGEX, "");

        // Attempts parse with repair until convergence or limit
        for (int attempt = 0; attempt < 3; attempt++) {
            final ToonLexer lexer = new ToonLexer(CharStreams.fromString(current));
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final ToonParser parser = new ToonParser(tokens);

            final ToonRepairEngine repairEngine = new ToonRepairEngine();
            parser.setErrorHandler(new ToonRepairErrorStrategy(repairEngine));

            // Attempts parse and repair until convergence or failure
            try {
                parser.toonFile();
                if (repairEngine.hasIssues()) {
                    current = repairEngine.repair(current);
                    continue;
                }
                return current;
            } catch (RuntimeException ex) {
                if (!repairEngine.hasIssues()) {
                    throw ex;
                }
                current = repairEngine.repair(current);
            }
        }
        return current;
    }
}
