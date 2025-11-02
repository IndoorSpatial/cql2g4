package io.github.IndoorSpatial.cql2.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.locationtech.jts.geom.Geometry;

import io.github.IndoorSpatial.cql2.converter.CustomGeometrySerializer;

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

    public String ToJsonString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule module = new SimpleModule();
        module.addSerializer(Geometry.class, new CustomGeometrySerializer());
        objectMapper.registerModule(module);
        return objectMapper.writeValueAsString(this);
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
