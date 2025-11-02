package io.github.IndoorSpatial.cql2.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    public boolean isLiteral() {
        return type.toString().endsWith("Literal");
    }

    public String ToString() {
        return String.join("\n", ToIndentExpression("    "));
    }

    private List<String> ToIndentExpression(String indent) {
        List<String> result = new LinkedList<>();
        if (isLiteral()) {
            result.add("\"" + value + "\" (" + type + ")");
            return result;
        } else {
            result.add((op == null ? "" : ("\"" + op + "\" ")) + "(" + type + ")");
            if (args != null && !args.isEmpty()) {
                List<String> argsResult = args.stream().filter(Objects::nonNull)
                        .map(arg -> arg.ToIndentExpression(indent)).flatMap(List::stream).toList();
                argsResult.forEach(arg -> result.add(indent + arg));
            }
            return result;
        }
    }
}
