package consulo.php.impl.xdebug;

import consulo.execution.debug.frame.XCompositeNode;
import consulo.execution.debug.frame.XValueChildrenList;
import consulo.execution.debug.frame.XValueGroup;
import consulo.php.impl.xdebug.connection.DbgpCommandSender;
import jakarta.annotation.Nonnull;
import org.w3c.dom.Element;

import java.util.List;

public class PhpValueGroup extends XValueGroup {
    private final DbgpCommandSender myCommandSender;
    private final List<Element> myProperties;
    private final int myStackLevel;
    private final int myContextId;

    public PhpValueGroup(@Nonnull String name,
                         @Nonnull DbgpCommandSender commandSender,
                         @Nonnull List<Element> properties,
                         int stackLevel,
                         int contextId) {
        super(name);
        myCommandSender = commandSender;
        myProperties = properties;
        myStackLevel = stackLevel;
        myContextId = contextId;
    }

    @Override
    public void computeChildren(@Nonnull XCompositeNode node) {
        XValueChildrenList children = new XValueChildrenList();
        for (Element prop : myProperties) {
            String name = prop.getAttribute("name");
            children.add(name != null ? name : "?", new PhpValue(myCommandSender, prop, myStackLevel, myContextId));
        }
        node.addChildren(children, true);
    }
}
