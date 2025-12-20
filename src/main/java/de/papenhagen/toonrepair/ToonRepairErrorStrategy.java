
package de.papenhagen.toonrepair;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

public final class ToonRepairErrorStrategy extends DefaultErrorStrategy {

    private final ToonRepairEngine repairEngine;

    public ToonRepairErrorStrategy(final ToonRepairEngine repairEngine) {
        this.repairEngine = repairEngine;
    }

    @Override
    public void reportError(final Parser recognizer, final RecognitionException e) {
        repairEngine.registerSyntaxError(recognizer, e.getOffendingToken(), e);
        super.reportError(recognizer, e);
    }

    @Override
    public Token recoverInline(final Parser recognizer) {
        final Token token = recognizer.getCurrentToken();
        repairEngine.registerInlineRecovery(recognizer, token);
        return super.recoverInline(recognizer);
    }

    @Override
    public void sync(Parser recognizer) {
        // use default sync
        super.sync(recognizer);
    }
}
