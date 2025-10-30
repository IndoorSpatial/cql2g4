package ai.flexgalaxy.cql2.ast;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AstNode {
    private String op;
    private String type;
    private List<AstNode> args;

    public AstNode(String op, String type, List<AstNode> args) {
        this.op = op;
        this.type = type;
        this.args = args;
    }

    public boolean isLiteral() { return false; }
}
