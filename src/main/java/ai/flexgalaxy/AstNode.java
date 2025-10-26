package ai.flexgalaxy;

import java.util.List;

public class AstNode {
    private String op;
    private String type;
    private List<AstNode> args;

    public AstNode(String op, String type, List<AstNode> args) {
        this.op = op;
        this.type = type;
        this.args = args;
    }

    boolean isLiteral() { return false; }
}
