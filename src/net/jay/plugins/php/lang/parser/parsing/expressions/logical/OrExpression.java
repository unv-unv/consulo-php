package net.jay.plugins.php.lang.parser.parsing.expressions.logical;

import net.jay.plugins.php.lang.lexer.PHPTokenTypes;
import net.jay.plugins.php.lang.parser.util.PHPPsiBuilder;
import net.jay.plugins.php.lang.parser.util.PHPParserErrors;
import net.jay.plugins.php.lang.parser.PHPElementTypes;
import net.jay.plugins.php.lang.parser.parsing.expressions.AssignmentExpression;
import net.jay.plugins.php.lang.psi.PHPElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.PsiBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: markov
 * Date: 15.12.2007
 */
public class OrExpression implements PHPTokenTypes {

	public static IElementType parse(PHPPsiBuilder builder) {
		PsiBuilder.Marker marker = builder.mark();
		IElementType result = AndExpression.parse(builder);
		if (result != PHPElementTypes.EMPTY_INPUT) {
			if (builder.compareAndEat(opOR)) {
				result = AssignmentExpression.parseWithoutPriority(builder);
				if (result == PHPElementTypes.EMPTY_INPUT) {
					result = AndExpression.parse(builder);
				}
				if (result == PHPElementTypes.EMPTY_INPUT) {
					builder.error(PHPParserErrors.expected("expression"));
				}
				PsiBuilder.Marker newMarker = marker.precede();
				marker.done(PHPElementTypes.LOGICAL_EXPRESSION);
				result = PHPElementTypes.LOGICAL_EXPRESSION;
				if (builder.compareAndEat(opOR)) {
					subParse(builder, newMarker);
				} else {
					newMarker.drop();
				}
			} else {
				marker.drop();
			}
		} else {
			marker.drop();
		}
		return result;
	}

	private static IElementType subParse(PHPPsiBuilder builder, PsiBuilder.Marker marker) {
		IElementType result = AssignmentExpression.parseWithoutPriority(builder);
		if (result == PHPElementTypes.EMPTY_INPUT) {
			result = AndExpression.parse(builder);
		}
		if (result == PHPElementTypes.EMPTY_INPUT) {
			builder.error(PHPParserErrors.expected("expression"));
		}
		PsiBuilder.Marker newMarker = marker.precede();
		marker.done(PHPElementTypes.LOGICAL_EXPRESSION);
		if (builder.compareAndEat(opOR)) {
			subParse(builder, newMarker);
		} else {
			newMarker.drop();
		}
		return PHPElementTypes.LOGICAL_EXPRESSION;
	}
}
