/**
 * @author VISTALL
 * @since 2025-03-16
 */
module consulo.php.xdebug.impl {
    requires consulo.execution.debug.api;
    requires consulo.code.editor.api;
    requires consulo.ui.ex.api;

    requires consulo.php.api;

    exports consulo.php.impl.xdebug;
    exports consulo.php.impl.xdebug.connection;
}
