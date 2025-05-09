package consulo.php.impl.lang.psi.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nonnull;

import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.ImplementsList;
import consulo.php.impl.lang.psi.visitors.PhpElementVisitor;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;

/**
 * @author jay
 * @date Jun 24, 2008 9:21:14 PM
 */
public class PhpImplementsListImpl extends PhpElementImpl implements ImplementsList
{
	public PhpImplementsListImpl(ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@Nonnull PhpElementVisitor visitor)
	{
		visitor.visitPhpElement(this);
	}

	@Override
	@Nonnull
	public List<PhpClass> getInterfaces()
	{
		List<PhpClass> result = new ArrayList<PhpClass>();

		final PsiElement[] children = getChildren();
		for(PsiElement child : children)
		{
			if(child instanceof ClassReference)
			{
				//noinspection ConstantConditions
				final PsiElement element = child.getReference().resolve();
				if(element instanceof PhpClass)
				{
					result.add((PhpClass) element);
				}
			}
		}

		return result;
	}
}
