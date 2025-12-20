package de.papenhagen.toonrepair;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExperimentTest {

    /**
     * Tests repair of malformed array header
     */
    @Test
    void testRepairArrayHeader() {
        String input = "data[ | 10 ]\n  1,2,3\n";
        ToonLexer lexer = new ToonLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ToonParser parser = new ToonParser(tokens);
        ToonRepairEngine repairEngine = new ToonRepairEngine();
        parser.setErrorHandler(new ToonRepairErrorStrategy(repairEngine));

        StringBuilder stringBuilder = new StringBuilder();
        try {
            parser.toonFile();
            stringBuilder.append("Parse finished\n");
        } catch (Exception e) {
            // Reports parse failure with exception details
            stringBuilder.append("Parse failed: ").append(e.getClass().getSimpleName()).append(": ").append(e.getMessage()).append("\n");
        }

        if (repairEngine.hasIssues()) {
            stringBuilder.append("Issues found. Repaired: [").append(repairEngine.repair(input)).append("]");
        } else {
            stringBuilder.append("No issues found");
        }
        String string = stringBuilder.toString();
        assertThat(string).contains("Issues found. Repaired: [data:[10|]" );
    }
}
