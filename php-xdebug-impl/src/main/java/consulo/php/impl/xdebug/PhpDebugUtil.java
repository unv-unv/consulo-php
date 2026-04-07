package consulo.php.impl.xdebug;

import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class PhpDebugUtil {
    private PhpDebugUtil() {
    }

    @Nonnull
    public static String toFileUri(@Nonnull VirtualFile file) {
        return "file://" + file.getPath();
    }

    @Nonnull
    public static String toFileUri(@Nonnull String path) {
        return "file://" + path;
    }

    @Nullable
    public static VirtualFile toVirtualFile(@Nonnull String fileUri) {
        try {
            String path;
            if (fileUri.startsWith("file://")) {
                URI uri = new URI(fileUri);
                path = uri.getPath();
            }
            else {
                path = fileUri;
            }
            return LocalFileSystem.getInstance().findFileByPath(path);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static String decodeBase64(@Nullable String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return null;
        }
        try {
            return new String(Base64.getDecoder().decode(encoded.trim()), StandardCharsets.UTF_8);
        }
        catch (IllegalArgumentException e) {
            return encoded;
        }
    }

    @Nonnull
    public static String makeBreakpointKey(@Nonnull String fileUri, int line) {
        return fileUri + ":" + line;
    }
}
