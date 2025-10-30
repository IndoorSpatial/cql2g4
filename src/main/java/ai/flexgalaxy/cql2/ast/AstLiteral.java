package ai.flexgalaxy.cql2.ast;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AstLiteral extends AstNode {
    Object value;
    LiteralType literalType;

    public AstLiteral(LiteralType literalType, Object value) {
        super(null, literalType.name(), null);
        this.literalType = literalType;
        this.value = value;
    }

    @Override
    public boolean isLiteral() { return true; }
}
