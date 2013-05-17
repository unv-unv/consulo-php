package net.jay.plugins.php.lang.documentation.phpdoc.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.peer.PeerFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IChameleonElementType;
import net.jay.plugins.php.lang.PHPFileType;
import net.jay.plugins.php.lang.documentation.phpdoc.lexer.PhpDocLexer;
import net.jay.plugins.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes;
import net.jay.plugins.php.lang.documentation.phpdoc.psi.PhpDocElementType;
import org.jetbrains.annotations.NotNull;

/**
 * @author jay
 * @date Jun 26, 2008 10:12:07 PM
 */
public interface PhpDocElementTypes extends PhpDocTokenTypes {

  final public IChameleonElementType DOC_COMMENT = new IChameleonElementType("PhpDocComment") {
    @NotNull
    public Language getLanguage() {
      return PHPFileType.PHP.getLanguage();
    }

    public ASTNode parseContents(ASTNode chameleon) {
      final PeerFactory factory = PeerFactory.getInstance();
      final PsiElement parentElement = chameleon.getTreeParent().getPsi();

      final PsiBuilder builder = factory.createBuilder(
        chameleon,
        new PhpDocLexer(),
        getLanguage(),
        chameleon.getText(),
        parentElement.getProject());
      final PsiParser parser = new PhpDocParser();
      return parser.parse(this, builder).getFirstChildNode();
    }
  };

  final public PhpDocElementType phpDocText = new PhpDocElementType("PhpDocText");
  final public PhpDocElementType phpDocTag = new PhpDocElementType("PhpDocTag");
  final public PhpDocElementType phpDocInlineTag = new PhpDocElementType("PhpDocInlineTag");
  final public PhpDocElementType phpDocTagValue = new PhpDocElementType("PhpDocTagValue");
  final public PhpDocElementType phpDocType = new PhpDocElementType("PhpDocType");
  final public PhpDocElementType phpDocVariable = new PhpDocElementType("PhpDocVariable");

}
