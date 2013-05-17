package net.jay.plugins.php.lang.parser.util;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * @author markov
 * @date 13.10.2007
 */
public class PHPPsiBuilder {

	private PsiBuilder psiBuilder;

	public PHPPsiBuilder(@NotNull PsiBuilder builder) {
		psiBuilder = builder;
	}

	public boolean compare(final IElementType type) {
		return getTokenType() == type;
	}

	public boolean compare(final TokenSet types) {
		return types.contains(getTokenType());
	}

	public boolean compareAndEat(final IElementType type) {
		boolean found = compare(type);
		if (found) {
			advanceLexer();
		}
		return found;
	}

	public boolean compareAndEat(final TokenSet types) {
		boolean found = compare(types);
		if (found) {
			advanceLexer();
		}
		return found;
	}

	public void match(final IElementType token) {
		match(token, PHPParserErrors.expected(token));
	}

	public void match(final IElementType token, final String errorMessage) {
		if (!compareAndEat(token)) {
			error(errorMessage);
		}
	}

	public void match(final TokenSet tokens) {
		match(tokens, PHPParserErrors.expected(tokens));
	}

	public void match(final TokenSet tokens, final String errorMessage) {
		if (!compareAndEat(tokens)) {
			error(errorMessage);
		}
	}


	// CORE PsiBuilder FEATURES
	public void advanceLexer() {
		psiBuilder.advanceLexer();
	}

	public PsiBuilder.Marker mark() {
		return psiBuilder.mark();
	}

	public void error(String errorMessage) {
		psiBuilder.error(errorMessage);
	}

	public IElementType getTokenType() {
		return psiBuilder.getTokenType();
	}

  public String getTokenText() {
    return psiBuilder.getTokenText();
  }

  public boolean eof() {
		return psiBuilder.eof();
	}

	public ASTNode getTreeBuilt() {
		return psiBuilder.getTreeBuilt();
	}

	public int getCurrentOffset() {
		return psiBuilder.getCurrentOffset();
	}
}
