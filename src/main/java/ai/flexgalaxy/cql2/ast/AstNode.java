package ai.flexgalaxy.cql2.ast;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AstNode {
    private String op;
    private AstNodeType type;
    private List<AstNode> args;
    private Object value;

    public <T> T getValue() {
        return (T) value;
    }

    public AstNode(String op, AstNodeType type, List<AstNode> args) {
        this.op = op;
        this.type = type;
        this.args = args;
    }

    public AstNode(AstNodeType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public boolean isLiteral() {
        return type.toString().endsWith("Literal");
    }
}
