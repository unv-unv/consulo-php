package net.jay.plugins.php.lang.parser.parsing.statements;

import com.intellij.psi.tree.IElementType;
import com.intellij.lang.PsiBuilder;
import net.jay.plugins.php.lang.lexer.PHPTokenTypes;
import net.jay.plugins.php.lang.parser.PHPElementTypes;
import net.jay.plugins.php.lang.parser.parsing.expressions.Expression;
import net.jay.plugins.php.lang.parser.util.PHPPsiBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: markov
 * Date: 03.11.2007
 */
public class BreakStatement implements PHPTokenTypes {

	//	kwBREAK ';'
	//	| kwBREAK expr ';'
	public static IElementType parse(PHPPsiBuilder builder) {
		if (!builder.compare(kwBREAK)) {
			return PHPElementTypes.EMPTY_INPUT;
		}
		PsiBuilder.Marker statement = builder.mark();
		builder.advanceLexer();
		if (!builder.compareAndEat(opSEMICOLON)) {
			Expression.parse(builder);
      if (!builder.compare(PHP_CLOSING_TAG)) {
        builder.match(opSEMICOLON);
      }
    }
		statement.done(PHPElementTypes.BREAK);
		return PHPElementTypes.BREAK;
	}
}
