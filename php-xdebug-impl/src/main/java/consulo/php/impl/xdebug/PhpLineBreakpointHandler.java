package consulo.php.impl.xdebug;

import consulo.execution.debug.breakpoint.XBreakpointHandler;
import consulo.execution.debug.breakpoint.XLineBreakpoint;
import jakarta.annotation.Nonnull;

public class PhpLineBreakpointHandler extends XBreakpointHandler<XLineBreakpoint<PhpLineBreakpointProperties>> {
    private final PhpBreakpointListener myListener;

    @SuppressWarnings("unchecked")
    public PhpLineBreakpointHandler(@Nonnull PhpBreakpointListener listener) {
        super(PhpLineBreakpointType.class);
        myListener = listener;
    }

    @Override
    public void registerBreakpoint(@Nonnull XLineBreakpoint<PhpLineBreakpointProperties> breakpoint) {
        myListener.addBreakpoint(breakpoint);
    }

    @Override
    public void unregisterBreakpoint(@Nonnull XLineBreakpoint<PhpLineBreakpointProperties> breakpoint, boolean temporary) {
        myListener.removeBreakpoint(breakpoint);
    }
}
