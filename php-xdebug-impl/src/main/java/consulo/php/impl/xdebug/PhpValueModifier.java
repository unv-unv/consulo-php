package consulo.php.impl.xdebug;

import consulo.execution.debug.frame.XValueModifier;
import consulo.logging.Logger;
import consulo.php.impl.xdebug.connection.DbgpCommandSender;
import consulo.php.impl.xdebug.connection.DbgpResponse;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class PhpValueModifier extends XValueModifier {
    private static final Logger LOG = Logger.getInstance(PhpValueModifier.class);

    private final DbgpCommandSender myCommandSender;
    private final String myFullName;
    private final int myStackDepth;

    public PhpValueModifier(@Nonnull DbgpCommandSender commandSender, @Nonnull String fullName, int stackDepth) {
        myCommandSender = commandSender;
        myFullName = fullName;
        myStackDepth = stackDepth;
    }

    @Override
    public void setValue(@Nonnull String expression, @Nonnull XModificationCallback callback) {
        try {
            DbgpResponse response = myCommandSender.propertySet(myFullName, myStackDepth, "string", expression).get();
            if (response.hasError()) {
                callback.errorOccurred("Failed to set value: " + response.getErrorMessage());
            }
            else {
                callback.valueModified();
            }
        }
        catch (Exception e) {
            LOG.warn("Failed to set value for " + myFullName, e);
            callback.errorOccurred("Failed to set value: " + e.getMessage());
        }
    }

    @Nullable
    @Override
    public String getInitialValueEditorText() {
        return null;
    }
}
