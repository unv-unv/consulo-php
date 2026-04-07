package consulo.php.impl.xdebug;

import consulo.execution.debug.XSourcePosition;
import consulo.execution.debug.evaluation.XDebuggerEvaluator;
import consulo.logging.Logger;
import consulo.php.impl.xdebug.connection.DbgpCommandSender;
import consulo.php.impl.xdebug.connection.DbgpResponse;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.w3c.dom.Element;

public class PhpDebuggerEvaluator extends XDebuggerEvaluator {
    private static final Logger LOG = Logger.getInstance(PhpDebuggerEvaluator.class);

    private final DbgpCommandSender myCommandSender;

    public PhpDebuggerEvaluator(@Nonnull DbgpCommandSender commandSender) {
        myCommandSender = commandSender;
    }

    @Override
    public void evaluate(@Nonnull String expression, @Nonnull XEvaluationCallback callback, @Nullable XSourcePosition expressionPosition) {
        try {
            DbgpResponse response = myCommandSender.eval(expression).get();
            if (response.hasError()) {
                callback.errorOccurred("Eval error: " + response.getErrorMessage());
                return;
            }

            Element property = response.getFirstChildElement("property");
            if (property != null) {
                callback.evaluated(new PhpValue(myCommandSender, property, 0, 0));
            }
            else {
                callback.errorOccurred("No result returned");
            }
        }
        catch (Exception e) {
            LOG.warn("Eval failed: " + expression, e);
            callback.errorOccurred("Eval failed: " + e.getMessage());
        }
    }
}
