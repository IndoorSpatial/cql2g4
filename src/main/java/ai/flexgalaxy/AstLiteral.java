package ai.flexgalaxy;

import java.util.ArrayList;

public class AstLiteral extends AstNode {
    LiteralType literalType;
    Object value;

    public AstLiteral(LiteralType literalType, Object value) {
        super("", literalType.name(), new ArrayList<>());
        this.literalType = literalType;
        this.value = value;
    }

    @Override
    boolean isLiteral() { return true; }
}
