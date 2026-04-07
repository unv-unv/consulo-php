package consulo.php.impl.xdebug;

import consulo.execution.debug.breakpoint.XBreakpointProperties;
import jakarta.annotation.Nullable;

public class PhpLineBreakpointProperties extends XBreakpointProperties<PhpLineBreakpointProperties> {
    @Nullable
    @Override
    public PhpLineBreakpointProperties getState() {
        return this;
    }

    @Override
    public void loadState(PhpLineBreakpointProperties state) {
    }
}
