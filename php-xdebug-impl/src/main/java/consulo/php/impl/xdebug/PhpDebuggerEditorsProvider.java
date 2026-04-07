package consulo.php.impl.xdebug;

import com.jetbrains.php.lang.PhpFileType;
import consulo.annotation.access.RequiredReadAction;
import consulo.codeEditor.EditorFactory;
import consulo.document.Document;
import consulo.document.FileDocumentManager;
import consulo.execution.debug.XSourcePosition;
import consulo.execution.debug.evaluation.EvaluationMode;
import consulo.execution.debug.evaluation.XDebuggerEditorsProvider;
import consulo.language.file.light.LightVirtualFile;
import consulo.project.Project;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class PhpDebuggerEditorsProvider extends XDebuggerEditorsProvider {
    @Nonnull
    @Override
    public FileType getFileType() {
        return PhpFileType.INSTANCE;
    }

    @RequiredReadAction
    @Nonnull
    @Override
    public Document createDocument(@Nonnull Project project, @Nonnull String text, @Nullable XSourcePosition sourcePosition, @Nonnull EvaluationMode mode) {
        LightVirtualFile file = new LightVirtualFile("php-eval.php", PhpFileType.INSTANCE, text);
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document != null) {
            return document;
        }
        return EditorFactory.getInstance().createDocument(text);
    }
}
