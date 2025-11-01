package ai.flexgalaxy.cql2.converter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;

import ai.flexgalaxy.cql2.Cql2Parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

public class ParseTreeToJsonNode extends ai.flexgalaxy.cql2.Cql2ParserBaseVisitor<JsonNode> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeoJsonWriter writer = new GeoJsonWriter();
    private final WKTReader reader = new WKTReader();
    private final CommonTokenStream tokens;
    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .appendLiteral("Z")
            .toFormatter(Locale.ROOT);

    public ParseTreeToJsonNode(CommonTokenStream tokens) {
        this.tokens = tokens;
        writer.setEncodeCRS(false);
    }

    private JsonNode Not(JsonNode node) {
        ObjectNode not = objectMapper.createObjectNode();
        not.put("op", "not");
        ArrayNode args = objectMapper.createArrayNode();
        args.add(node);
        not.set("args", args);
        return not;
    }

    private JsonNode OpArgs(String op, List<? extends ParserRuleContext> ctxs) {
        ObjectNode n = objectMapper.createObjectNode();
        n.put("op", op);
        ArrayNode args = objectMapper.createArrayNode();
        ctxs.forEach(ctx -> args.add(visit(ctx)));
        n.set("args", args);
        return n;
    }

    private JsonNode OpArgs(String op, JsonNode node, List<? extends ParserRuleContext> ctxs) {
        ObjectNode n = objectMapper.createObjectNode();
        n.put("op", op);
        ArrayNode args = objectMapper.createArrayNode();
        args.add(node);
        ctxs.forEach(ctx -> args.add(visit(ctx)));
        n.set("args", args);
        return n;
    }

    private JsonNode OpArgs(String op, ParserRuleContext... ctxs) {
        List<ParserRuleContext> ctxsList = Arrays.asList(ctxs);
        return OpArgs(op, ctxsList);
    }

    private JsonNode OpArgs(String op, JsonNode node, ParserRuleContext... ctxs) {
        List<ParserRuleContext> ctxsList = Arrays.asList(ctxs);
        return OpArgs(op, node, ctxsList);
    }

    private JsonNode Args(List<? extends ParserRuleContext> ctxs) {
        ArrayNode args = objectMapper.createArrayNode();
        ctxs.forEach(ctx -> args.add(visit(ctx)));
        return args;
    }

    private static String trim(String str, Character character) {
        if (str == null || str.length() < 2)
            return str;
        if (str.charAt(0) == character && str.charAt(str.length() - 1) == character)
            return str.substring(1, str.length() - 1);
        return str;
    }

    private static String toLowerCase(String str) {
        String lowerCase = str.toLowerCase();
        if (lowerCase.endsWith("by")) {
            char[] chars = lowerCase.toCharArray();
            chars[lowerCase.length() - 2] = 'B';
            return new String(chars);
        } else {
            return lowerCase;
        }
    }

    private JsonNode parseNumber(String str) {
        if (str.contains(String.valueOf('.')))
            return objectMapper.valueToTree(Double.parseDouble(str));
        else
            return objectMapper.valueToTree(Integer.parseInt(str));
    }

    @Override
    public JsonNode visitBooleanExpression(Cql2Parser.BooleanExpressionContext ctx) {
        if (ctx.booleanTerm().size() > 1)
            return OpArgs("or", ctx.booleanTerm());
        else
            return visit(ctx.booleanTerm().getFirst());
    }

    @Override
    public JsonNode visitBooleanTerm(Cql2Parser.BooleanTermContext ctx) {
        if (ctx.booleanFactor().size() > 1)
            return OpArgs("and", ctx.booleanFactor());
        else
            return visit(ctx.booleanFactor().getFirst());
    }

    @Override
    public JsonNode visitBooleanFactor(Cql2Parser.BooleanFactorContext ctx) {
        JsonNode n = visit(ctx.booleanPrimary());
        return ctx.NOT() == null ? n : Not(n);
    }

    @Override
    public JsonNode visitBooleanPrimary(Cql2Parser.BooleanPrimaryContext ctx) {
        if (ctx.booleanLiteral() != null) return visit((ctx.booleanLiteral()));
        if (ctx.booleanExpression() != null) return visit(ctx.booleanExpression());
        if (ctx.function() != null) return visit(ctx.function());
        if (ctx.predicate() != null) return visit(ctx.predicate());
        return null;
    }

    @Override
    public JsonNode visitPredicate(Cql2Parser.PredicateContext ctx) {
        if (ctx.comparisonPredicate() != null) return visit(ctx.comparisonPredicate());
        if (ctx.spatialPredicate() != null) return visit(ctx.spatialPredicate());
        if (ctx.temporalPredicate() != null) return visit(ctx.temporalPredicate());
        if (ctx.arrayPredicate() != null) return visit(ctx.arrayPredicate());
        return null;
    }

    @Override
    public JsonNode visitComparisonPredicate(Cql2Parser.ComparisonPredicateContext ctx) {
        if (ctx.binaryComparisonPredicate() != null) return visit(ctx.binaryComparisonPredicate());
        if (ctx.isLikePredicate() != null) return visit(ctx.isLikePredicate());
        if (ctx.isBetweenPredicate() != null) return visit(ctx.isBetweenPredicate());
        if (ctx.isInListPredicate() != null) return visit(ctx.isInListPredicate());
        if (ctx.isNullPredicate() != null) return visit(ctx.isNullPredicate());
        return null;
    }

    @Override
    public JsonNode visitBinaryComparisonPredicate(Cql2Parser.BinaryComparisonPredicateContext ctx) {
        return OpArgs(ctx.COMP_OP().getText(), ctx.scalarExpression());
    }

    @Override
    public JsonNode visitScalarExpression(Cql2Parser.ScalarExpressionContext ctx) {
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        if (ctx.characterClause() != null) return visit(ctx.characterClause());
        if (ctx.numericLiteral() != null) return visit(ctx.numericLiteral());
        if (ctx.instantInstance() != null) return visit(ctx.instantInstance());
        if (ctx.booleanLiteral() != null) return visit(ctx.booleanLiteral());
        if (ctx.arithmeticExpression() != null) return visit(ctx.arithmeticExpression());
        return null;
    }

    @Override
    public JsonNode visitIsLikePredicate(Cql2Parser.IsLikePredicateContext ctx) {
        JsonNode n = OpArgs("like", ctx.characterExpression(), ctx.patternExpression());
        return ctx.NOT() == null ? n : Not(n);
    }

    @Override
    public JsonNode visitPatternExpression(Cql2Parser.PatternExpressionContext ctx) {
        if (ctx.CASEI() != null) {
            return OpArgs("casei", ctx.patternExpression());
        } else if (ctx.ACCENTI() != null) {
            return OpArgs("accenti", ctx.patternExpression());
        } else {
            String charactorLiteral = ctx.CharacterLiteral().getText();
            return objectMapper.valueToTree(trim(charactorLiteral, '\''));
        }
    }

    @Override
    public JsonNode visitIsBetweenPredicate(Cql2Parser.IsBetweenPredicateContext ctx) {
        JsonNode n = OpArgs("between", ctx.numericExpression());
        return ctx.NOT() == null ? n : Not(n);
    }

    @Override
    public JsonNode visitNumericExpression(Cql2Parser.NumericExpressionContext ctx) {
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.arithmeticExpression() != null) return visit(ctx.arithmeticExpression());
        if (ctx.numericLiteral() != null) return visit(ctx.numericLiteral());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public JsonNode visitIsInListPredicate(Cql2Parser.IsInListPredicateContext ctx) {
        JsonNode n = OpArgs("in", ctx.scalarExpression(), ctx.inList());
        return ctx.NOT() == null ? n : Not(n);
    }

    @Override
    public JsonNode visitInList(Cql2Parser.InListContext ctx) {
        return Args(ctx.scalarExpression());
    }

    @Override
    public JsonNode visitIsNullPredicate(Cql2Parser.IsNullPredicateContext ctx) {
        JsonNode n = OpArgs("isNull", ctx.isNullOperand());
        return ctx.NOT() == null ? n : Not(n);
    }

    @Override
    public JsonNode visitIsNullOperand(Cql2Parser.IsNullOperandContext ctx) {
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.characterClause() != null) return visit(ctx.characterClause());
        if (ctx.numericLiteral() != null) return visit(ctx.numericLiteral());
        if (ctx.temporalInstance() != null) return visit(ctx.temporalInstance());
        if (ctx.spatialInstance() != null) return visit(ctx.spatialInstance());
        if (ctx.arithmeticExpression() != null) return visit(ctx.arithmeticExpression());
        if (ctx.booleanExpression() != null) return visit(ctx.booleanExpression());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public JsonNode visitSpatialPredicate(Cql2Parser.SpatialPredicateContext ctx) {
        return OpArgs(ctx.SPATIAL_FUNC().getText().toLowerCase(), ctx.geomExpression());
    }

    @Override
    public JsonNode visitGeomExpression(Cql2Parser.GeomExpressionContext ctx) {
        if (ctx.spatialInstance() != null) return visit(ctx.spatialInstance());
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public JsonNode visitTemporalPredicate(Cql2Parser.TemporalPredicateContext ctx) {
        return OpArgs(toLowerCase(ctx.TEMPORAL_FUNC().getText()), ctx.temporalExpression());
    }

    @Override
    public JsonNode visitTemporalExpression(Cql2Parser.TemporalExpressionContext ctx) {
        if (ctx.temporalInstance() != null) return visit(ctx.temporalInstance());
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public JsonNode visitArrayPredicate(Cql2Parser.ArrayPredicateContext ctx) {
        return OpArgs(toLowerCase(ctx.ARRAY_FUNC().getText()), ctx.arrayExpression());
    }

    @Override
    public JsonNode visitArrayExpression(Cql2Parser.ArrayExpressionContext ctx) {
        if (ctx.array() != null) return visit(ctx.array());
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public JsonNode visitArray(Cql2Parser.ArrayContext ctx) {
        return Args(ctx.arrayElement());
    }

    @Override
    public JsonNode visitArrayElement(Cql2Parser.ArrayElementContext ctx) {
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.characterClause() != null) return visit(ctx.characterClause());
        if (ctx.numericLiteral() != null) return visit(ctx.numericLiteral());
        if (ctx.temporalInstance() != null) return visit(ctx.temporalInstance());
        if (ctx.spatialInstance() != null) return visit(ctx.spatialInstance());
        if (ctx.array() != null) return visit(ctx.array());
        if (ctx.arithmeticExpression() != null) return visit(ctx.arithmeticExpression());
        if (ctx.booleanExpression() != null) return visit(ctx.booleanExpression());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public JsonNode visitArithmeticExpression(Cql2Parser.ArithmeticExpressionContext ctx) {
        JsonNode n = visit(ctx.arithmeticTerm().getFirst());
        for (int i = 0; i < ctx.Sign().size(); i++)
            n = OpArgs(ctx.Sign(i).getText(), n, ctx.arithmeticTerm(i + 1));
        return n;
    }

    @Override
    public JsonNode visitArithmeticTerm(Cql2Parser.ArithmeticTermContext ctx) {
        JsonNode n = visit(ctx.powerTerm().getFirst());
        for (int i = 0; i < ctx.ArithmeticOperatorMultDiv().size(); i++)
            n = OpArgs(ctx.ArithmeticOperatorMultDiv(i).getText(), n, ctx.powerTerm(i + 1));
        return n;
    }

    @Override
    public JsonNode visitPowerTerm(Cql2Parser.PowerTermContext ctx) {
        if (ctx.POWER() != null)
            return OpArgs(ctx.POWER().getText(), ctx.arithmeticFactor());
        else
            return visit(ctx.arithmeticFactor().getFirst());
    }

    @Override
    public JsonNode visitArithmeticFactor(Cql2Parser.ArithmeticFactorContext ctx) {
        if (ctx.arithmeticExpression() != null) return visit(ctx.arithmeticExpression());
        if (ctx.Sign() != null) return OpArgs(ctx.Sign().getText(), ctx.arithmeticOperand());
        else return visit(ctx.arithmeticOperand());
    }

    @Override
    public JsonNode visitArithmeticOperand(Cql2Parser.ArithmeticOperandContext ctx) {
        if (ctx.numericLiteral() != null) return visit(ctx.numericLiteral());
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public JsonNode visitPropertyName(Cql2Parser.PropertyNameContext ctx) {
        return objectMapper.createObjectNode().put("property", ctx.Identifier().getText());
    }

    @Override
    public JsonNode visitFunction(Cql2Parser.FunctionContext ctx) {
        return OpArgs(ctx.Identifier().getText(), ctx.argumentList().argument());
    }

    @Override
    public JsonNode visitArgument(Cql2Parser.ArgumentContext ctx) {
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.characterClause() != null) return visit(ctx.characterClause());
        if (ctx.numericLiteral() != null) return visit(ctx.numericLiteral());
        if (ctx.temporalInstance() != null) return visit(ctx.temporalInstance());
        if (ctx.spatialInstance() != null) return visit(ctx.spatialInstance());
        if (ctx.array() != null) return visit(ctx.array());
        if (ctx.arithmeticExpression() != null) return visit(ctx.arithmeticExpression());
        if (ctx.booleanExpression() != null) return visit(ctx.booleanExpression());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public JsonNode visitCharacterExpression(Cql2Parser.CharacterExpressionContext ctx) {
        if (ctx.characterClause() != null) return visit(ctx.characterClause());
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public JsonNode visitCharacterClause(Cql2Parser.CharacterClauseContext ctx) {
        if (ctx.CASEI() != null) {
            return OpArgs("casei", ctx.characterExpression());
        } else if (ctx.ACCENTI() != null) {
            return OpArgs("accenti", ctx.characterExpression());
        } else {
            String charactorLiteral = ctx.CharacterLiteral().getText();
            return objectMapper.valueToTree(trim(charactorLiteral, '\''));
        }
    }

    @Override
    public JsonNode visitNumericLiteral(Cql2Parser.NumericLiteralContext ctx) {
        return parseNumber(ctx.getText());
    }

    @Override
    public JsonNode visitBooleanLiteral(Cql2Parser.BooleanLiteralContext ctx) {
        return objectMapper.valueToTree(ctx.BOOL().getText().equalsIgnoreCase("TRUE"));
    }

    @Override
    public JsonNode visitSpatialInstance(Cql2Parser.SpatialInstanceContext ctx) {
        if (ctx.geometryLiteral() != null) return visit(ctx.geometryLiteral());
        if (ctx.geometryCollectionTaggedText() != null) return visit(ctx.geometryCollectionTaggedText());
        if (ctx.bboxTaggedText() != null) return visit(ctx.bboxTaggedText());
        return null;
    }

    @Override
    public JsonNode visitGeometryLiteral(Cql2Parser.GeometryLiteralContext ctx) {
        try {
            String originalText = tokens.getText(ctx.getSourceInterval());
            return objectMapper.readTree(writer.write(reader.read(originalText)));
        } catch (ParseException | JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public JsonNode visitGeometryCollectionTaggedText(Cql2Parser.GeometryCollectionTaggedTextContext ctx) {
        try {
            String originalText = tokens.getText(ctx.getSourceInterval());
            return objectMapper.readTree(writer.write(reader.read(originalText)));
        } catch (ParseException | JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public JsonNode visitBboxTaggedText(Cql2Parser.BboxTaggedTextContext ctx) {
        return objectMapper.createObjectNode().set("bbox", visit(ctx.bboxText()));
    }

    @Override
    public JsonNode visitBboxText(Cql2Parser.BboxTextContext ctx) {
        ArrayNode array = objectMapper.createArrayNode();
        array.add(parseNumber(ctx.westBoundLon().getText()));
        array.add(parseNumber(ctx.southBoundLat().getText()));
        if (ctx.minElev() != null)
            array.add(parseNumber(ctx.minElev().getText()));
        array.add(parseNumber(ctx.eastBoundLon().getText()));
        array.add(parseNumber(ctx.northBoundLat().getText()));
        if (ctx.maxElev() != null)
            array.add(parseNumber(ctx.maxElev().getText()));
        return array;
    }

    @Override
    public JsonNode visitTemporalInstance(Cql2Parser.TemporalInstanceContext ctx) {
        if (ctx.instantInstance() != null) return visit(ctx.instantInstance());
        if (ctx.intervalInstance() != null) return visit(ctx.intervalInstance());
        return null;
    }

    @Override
    public JsonNode visitInstantInstance(Cql2Parser.InstantInstanceContext ctx) {
        if (ctx.dateInstant() != null) return visit(ctx.dateInstant());
        if (ctx.timestampInstant() != null) return visit(ctx.timestampInstant());
        return null;
    }

    @Override
    public JsonNode visitDateInstant(Cql2Parser.DateInstantContext ctx) {
        return objectMapper.createObjectNode().put("date", trim(ctx.CharacterLiteral().getText(), '\''));
    }

    @Override
    public JsonNode visitTimestampInstant(Cql2Parser.TimestampInstantContext ctx) {
        String trimed = trim(ctx.CharacterLiteral().getText(), '\'');
        OffsetDateTime odt = OffsetDateTime.parse(trimed, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return objectMapper.createObjectNode().put("timestamp", odt.format(formatter));
    }

    @Override
    public JsonNode visitIntervalInstance(Cql2Parser.IntervalInstanceContext ctx) {
        ObjectNode n = objectMapper.createObjectNode();
        n.set("interval", Args(ctx.instantParameter()));
        return n;
    }

    @Override
    public JsonNode visitInstantParameter(Cql2Parser.InstantParameterContext ctx) {
        if (ctx.CharacterLiteral() != null) {
            String trimed = trim(ctx.CharacterLiteral().getText(), '\'');
            if (trimed.equals("..") || !trimed.contains("T")) {
                return objectMapper.valueToTree(trimed);
            } else {
                OffsetDateTime odt = OffsetDateTime.parse(trimed, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                return objectMapper.valueToTree(odt.format(formatter));
            }
        }

        if (ctx.dateInstant() != null) return visit(ctx.dateInstant());
        if (ctx.timestampInstant() != null) return visit(ctx.dateInstant());
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        if (ctx.DDOT() != null) return objectMapper.valueToTree(ctx.DDOT().getText());
        return null;
    }
}
