package consulo.php.impl.xdebug;

import com.jetbrains.php.lang.PhpFileType;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.execution.debug.breakpoint.XLineBreakpointType;
import consulo.execution.debug.breakpoint.XLineBreakpointTypeResolver;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@ExtensionImpl
public class PhpLineBreakpointTypeResolver implements XLineBreakpointTypeResolver {
    @Nonnull
    @Override
    public FileType getFileType() {
        return PhpFileType.INSTANCE;
    }

    @Nullable
    @Override
    @RequiredReadAction
    public XLineBreakpointType<?> resolveBreakpointType(@Nonnull Project project, @Nonnull VirtualFile virtualFile, int line) {
        return PhpLineBreakpointType.getInstance();
    }
}
