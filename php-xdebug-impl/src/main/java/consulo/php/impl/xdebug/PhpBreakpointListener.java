package consulo.php.impl.xdebug;

import consulo.execution.debug.breakpoint.XLineBreakpoint;
import jakarta.annotation.Nonnull;

public interface PhpBreakpointListener {
    void addBreakpoint(@Nonnull XLineBreakpoint<PhpLineBreakpointProperties> breakpoint);

    void removeBreakpoint(@Nonnull XLineBreakpoint<PhpLineBreakpointProperties> breakpoint);
}
