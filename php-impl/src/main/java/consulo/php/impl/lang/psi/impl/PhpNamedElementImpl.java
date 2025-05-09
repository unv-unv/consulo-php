package consulo.php.impl.lang.psi.impl;

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.psi.elements.ConstantReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.access.RequiredWriteAction;
import consulo.php.lang.lexer.PhpTokenTypes;
import consulo.php.impl.lang.psi.PhpPsiElementFactory;
import consulo.language.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author jay
 * @date Jun 4, 2008 11:38:51 AM
 */
public abstract class PhpNamedElementImpl extends PhpElementImpl implements PhpNamedElement
{
	public PhpNamedElementImpl(ASTNode node)
	{
		super(node);
	}

	@RequiredWriteAction
	@Override
	public PsiElement setName(@NonNls @Nonnull String name) throws IncorrectOperationException
	{
		PsiElement nameIdentifier = getNameIdentifier();
		//noinspection ConstantConditions
		if(nameIdentifier != null && !getName().equals(name))
		{
			final ConstantReference constantReference = PhpPsiElementFactory.createConstantReference(getProject(), name);
			nameIdentifier.replace(constantReference.getNameIdentifier());
		}
		return this;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return findChildByType(PhpTokenTypes.IDENTIFIER);
	}

	@Nonnull
	@Override
	public String getName()
	{
		PsiElement nameNode = getNameIdentifier();
		if(nameNode != null)
		{
			return nameNode.getText();
		}
		return null;
	}

	@RequiredReadAction
	@Override
	public int getTextOffset()
	{
		PsiElement nameNode = getNameIdentifier();
		if(nameNode != null)
		{
			return nameNode.getNode().getStartOffset();
		}
		return super.getTextOffset();
	}

	@Override
	public PhpDocComment getDocComment()
	{
		final PhpPsiElement element = getPrevPsiSibling();
		if(element instanceof PhpDocComment)
		{
			return (PhpDocComment) element;
		}
		return null;
	}

	@Override
	@Nonnull
	public PhpType getType()
	{
		return PhpType.EMPTY;
	}
}
