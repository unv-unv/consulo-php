package net.jay.plugins.php.lang.highlighter;

import com.intellij.lang.StdLanguages;
import com.intellij.lexer.*;
import com.intellij.psi.JavaDocTokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import net.jay.plugins.php.lang.lexer.PHPFlexLexer;
import net.jay.plugins.php.lang.lexer.PHPTokenTypes;
import net.jay.plugins.php.lang.lexer.PHPStringLiteralLexer;

/**
 * Created by IntelliJ IDEA.
 * User: jay
 * Date: 26.02.2007
 *
 * @author jay
 */
public class PHPHighlightingLexer extends LayeredLexer {

    public PHPHighlightingLexer() {
        super(new FlexAdapter(new PHPFlexLexer(true)));
        LayeredLexer docLexer = new LayeredLexer(new JavaDocLexer(false));
        registerSelfStoppingLayer(docLexer,
                new IElementType[]{PHPTokenTypes.DOC_COMMENT},
                new IElementType[]{JavaDocTokenType.DOC_COMMENT_END});

        Lexer lexer = getHtmlHighlightingLexer();
        docLexer.registerLayer(lexer, new IElementType[]{JavaDocTokenType.DOC_COMMENT_DATA});
        // @todo do it!
        registerLayer(new PHPStringLiteralLexer(PHPStringLiteralLexer.NO_QUOTE_CHAR, PHPTokenTypes.STRING_LITERAL, PHPStringLiteralLexer.TYPE_DOUBLE_QUOTE),
                new IElementType[]{PHPTokenTypes.STRING_LITERAL});
        registerLayer(new PHPStringLiteralLexer(PHPStringLiteralLexer.NO_QUOTE_CHAR, PHPTokenTypes.STRING_LITERAL_SINGLE_QUOTE, PHPStringLiteralLexer.TYPE_SINGLE_QUOTE),
                new IElementType[]{PHPTokenTypes.STRING_LITERAL_SINGLE_QUOTE});
    }

    private static Lexer getHtmlHighlightingLexer() {
        return SyntaxHighlighterFactory.getSyntaxHighlighter(StdLanguages.HTML, null, null).getHighlightingLexer();
    }

}