package consulo.php.impl.xdebug;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.Application;
import consulo.execution.debug.breakpoint.XBreakpointType;
import consulo.execution.debug.breakpoint.XLineBreakpointType;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@ExtensionImpl
public class PhpLineBreakpointType extends XLineBreakpointType<PhpLineBreakpointProperties> {
    public static final String ID = "php-line-breakpoint";

    public PhpLineBreakpointType() {
        super(ID, "PHP Line Breakpoints");
    }

    @Nonnull
    public static PhpLineBreakpointType getInstance() {
        return Application.get().getExtensionPoint(XBreakpointType.class).findExtensionOrFail(PhpLineBreakpointType.class);
    }

    @Nullable
    @Override
    public PhpLineBreakpointProperties createBreakpointProperties(@Nonnull VirtualFile file, int line) {
        return new PhpLineBreakpointProperties();
    }
}
