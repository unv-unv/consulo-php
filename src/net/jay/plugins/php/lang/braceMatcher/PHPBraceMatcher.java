package net.jay.plugins.php.lang.braceMatcher;

import com.intellij.lang.PairedBraceMatcher;
import com.intellij.lang.BracePair;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.jay.plugins.php.lang.lexer.PHPTokenTypes;

/**
 * @author jay
 * @time 21.12.2007 18:36:34
 */
public class PHPBraceMatcher implements PairedBraceMatcher, PHPTokenTypes {
	public BracePair[] getPairs() {
		return new BracePair[]{
			new BracePair(chLBRACE, chRBRACE, true),
			new BracePair(chLBRACKET, chRBRACKET, false),
			new BracePair(chLPAREN, chRPAREN, false)
		};
	}

	public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
		return true;
	}

    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return 0;
    }
}
