package ai.flexgalaxy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AstLiteral extends AstNode {
    Object value;

    public AstLiteral(LiteralType literalType, Object value) {
        super(null, literalType.name(), null);
        this.value = value;
    }

    @Override
    boolean isLiteral() { return true; }
}
