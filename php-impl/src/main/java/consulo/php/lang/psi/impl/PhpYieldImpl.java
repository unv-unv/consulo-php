package consulo.php.lang.psi.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpYield;
import consulo.php.lang.psi.visitors.PhpElementVisitor;

/**
 * @author VISTALL
 * @since 2019-04-23
 */
public class PhpYieldImpl extends PhpElementImpl implements PhpYield
{
	public PhpYieldImpl(ASTNode node)
	{
		super(node);
	}

	@Nullable
	@Override
	public PsiElement getArgument()
	{
		return null;
	}

	@Override
	public void accept(@Nonnull PhpElementVisitor visitor)
	{
		visitor.visitPhpYield(this);
	}
}
