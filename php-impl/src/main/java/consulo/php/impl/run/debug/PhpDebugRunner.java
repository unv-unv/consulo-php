package consulo.php.impl.run.debug;

import consulo.annotation.component.ExtensionImpl;
import consulo.execution.configuration.RunProfile;
import consulo.execution.configuration.RunProfileState;
import consulo.execution.debug.DefaultDebugExecutor;
import consulo.execution.debug.XDebugSession;
import consulo.execution.debug.XDebuggerManager;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.runner.GenericProgramRunner;
import consulo.execution.ui.RunContentDescriptor;
import consulo.php.impl.run.script.PhpScriptConfiguration;
import consulo.process.ExecutionException;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@ExtensionImpl
public class PhpDebugRunner extends GenericProgramRunner {
    private static final String RUNNER_ID = "PhpDebugRunner";

    @Nonnull
    @Override
    public String getRunnerId() {
        return RUNNER_ID;
    }

    @Override
    public boolean canRun(@Nonnull String executorId, @Nonnull RunProfile profile) {
        return DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) && profile instanceof PhpScriptConfiguration;
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@Nonnull RunProfileState state, @Nonnull ExecutionEnvironment environment) throws ExecutionException {
        XDebugSession session = XDebuggerManager.getInstance(environment.getProject()).startSession(
            environment,
            session1 -> new PhpXDebugProcess(session1, environment)
        );
        return session.getRunContentDescriptor();
    }
}
