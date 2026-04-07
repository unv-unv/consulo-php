package consulo.php.impl.xdebug;

import consulo.execution.debug.frame.XExecutionStack;
import consulo.execution.debug.frame.XSuspendContext;
import consulo.php.impl.xdebug.connection.DbgpCommandSender;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class PhpSuspendContext extends XSuspendContext {
    private final PhpExecutionStack myExecutionStack;

    public PhpSuspendContext(@Nonnull DbgpCommandSender commandSender) {
        myExecutionStack = new PhpExecutionStack(commandSender);
    }

    @Nullable
    @Override
    public XExecutionStack getActiveExecutionStack() {
        return myExecutionStack;
    }

    @Nonnull
    @Override
    public XExecutionStack[] getExecutionStacks() {
        return new XExecutionStack[]{myExecutionStack};
    }
}
