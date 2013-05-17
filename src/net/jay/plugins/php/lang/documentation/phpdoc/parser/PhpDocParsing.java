package net.jay.plugins.php.lang.documentation.phpdoc.parser;

import net.jay.plugins.php.lang.documentation.phpdoc.parser.tags.PhpDocTagParserRegistry;
import net.jay.plugins.php.lang.parser.util.PHPPsiBuilder;

/**
 * @author jay
 * @date Jun 28, 2008 4:39:52 PM
 */
public class PhpDocParsing implements PhpDocElementTypes {

  public void parse(PHPPsiBuilder builder) {
    builder.match(DOC_COMMENT_START);
    while (!builder.compare(DOC_COMMENT_END) && !builder.eof()) {
      if (builder.compare(DOC_TAG_NAME)) {
        PhpDocTagParserRegistry.parse(builder);
      } else {
        builder.advanceLexer();
      }
    }
    builder.match(DOC_COMMENT_END);
  }
  
}
