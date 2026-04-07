package consulo.php.impl.xdebug;

import consulo.execution.debug.frame.XExecutionStack;
import consulo.execution.debug.frame.XStackFrame;
import consulo.logging.Logger;
import consulo.php.impl.xdebug.connection.DbgpCommandSender;
import consulo.php.impl.xdebug.connection.DbgpResponse;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class PhpExecutionStack extends XExecutionStack {
    private static final Logger LOG = Logger.getInstance(PhpExecutionStack.class);

    private final DbgpCommandSender myCommandSender;
    private volatile List<PhpStackFrame> myFrames;

    protected PhpExecutionStack(@Nonnull DbgpCommandSender commandSender) {
        super("PHP");
        myCommandSender = commandSender;
    }

    @Nullable
    @Override
    public XStackFrame getTopFrame() {
        List<PhpStackFrame> frames = getOrLoadFrames();
        return frames != null && !frames.isEmpty() ? frames.get(0) : null;
    }

    @Override
    public void computeStackFrames(@Nonnull XStackFrameContainer container) {
        List<PhpStackFrame> frames = getOrLoadFrames();
        if (frames == null) {
            container.errorOccurred("Failed to load stack frames");
            return;
        }

        container.addStackFrames(frames, true);
    }

    @Nullable
    private List<PhpStackFrame> getOrLoadFrames() {
        if (myFrames != null) {
            return myFrames;
        }

        try {
            DbgpResponse response = myCommandSender.stackGet().get();
            if (response.hasError()) {
                LOG.warn("stack_get error: " + response.getErrorMessage());
                return null;
            }

            List<Element> stackElements = response.getStackElements();
            List<PhpStackFrame> frames = new ArrayList<>();
            for (Element element : stackElements) {
                String filename = element.getAttribute("filename");
                String linenoStr = element.getAttribute("lineno");
                String where = element.getAttribute("where");
                String levelStr = element.getAttribute("level");

                int lineno = linenoStr != null && !linenoStr.isEmpty() ? Integer.parseInt(linenoStr) : 0;
                int level = levelStr != null && !levelStr.isEmpty() ? Integer.parseInt(levelStr) : 0;

                frames.add(new PhpStackFrame(myCommandSender, filename, lineno, where, level));
            }

            myFrames = frames;
            return frames;
        }
        catch (Exception e) {
            LOG.warn("Failed to load stack frames", e);
            return null;
        }
    }
}
