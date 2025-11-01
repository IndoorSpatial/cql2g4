package ai.flexgalaxy.cql2.converter;

import ai.flexgalaxy.Cql2g4.Cql2Parser;
import ai.flexgalaxy.cql2.ast.AstNode;
import ai.flexgalaxy.cql2.ast.AstNodeType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ParseTreeToAst extends ai.flexgalaxy.Cql2g4.Cql2ParserBaseVisitor<AstNode> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeoJsonWriter writer = new GeoJsonWriter();
    private final WKTReader reader = new WKTReader();
    private final CommonTokenStream tokens;
    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .appendLiteral("Z")
            .toFormatter(Locale.ROOT);

    public ParseTreeToAst(CommonTokenStream tokens) {
        this.tokens = tokens;
        writer.setEncodeCRS(false);
    }

    private AstNode Not(AstNode node) {
        return new AstNode("not", AstNodeType.NotExpression, new LinkedList<>() {{ add(node); }});
    }

    private AstNode OpArgs(String op, AstNodeType nodeType, List<? extends ParserRuleContext> ctxs) {
        List<AstNode> args = new LinkedList<>();
        ctxs.forEach(ctx -> args.add(visit(ctx)));
        return new AstNode(op, nodeType, args);
    }

    private AstNode OpArgs(String op, AstNodeType nodeType, AstNode node, List<? extends ParserRuleContext> ctxs) {
        List<AstNode> args = new LinkedList<>();
        args.add(node);
        ctxs.forEach(ctx -> args.add(visit(ctx)));
        return new AstNode(op, nodeType, args);
    }

    private AstNode OpArgs(String op, AstNodeType nodeType, ParserRuleContext... ctxs) {
        List<ParserRuleContext> ctxsList = Arrays.asList(ctxs);
        return OpArgs(op, nodeType, ctxsList);
    }

    private AstNode OpArgs(String op, AstNodeType nodeType, AstNode node, ParserRuleContext... ctxs) {
        List<ParserRuleContext> ctxsList = Arrays.asList(ctxs);
        return OpArgs(op, nodeType, node, ctxsList);
    }

    private List<AstNode> Args(List<? extends ParserRuleContext> ctxs) {
        List<AstNode> args = new LinkedList<>();
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

    private AstNode parseNumber(String str) {
        if (str.contains(String.valueOf('.')))
            return new AstNode(AstNodeType.DoubleLiteral, Double.parseDouble(str));
        else
            return new AstNode(AstNodeType.IntegerLiteral, Integer.parseInt(str));
    }

    @Override
    public AstNode visitBooleanExpression(Cql2Parser.BooleanExpressionContext ctx) {
        if (ctx.booleanTerm().size() > 1)
            return OpArgs("or", AstNodeType.AndOrExpression, ctx.booleanTerm());
        else
            return visit(ctx.booleanTerm().getFirst());
    }

    @Override
    public AstNode visitBooleanTerm(Cql2Parser.BooleanTermContext ctx) {
        if (ctx.booleanFactor().size() > 1)
            return OpArgs("and", AstNodeType.AndOrExpression, ctx.booleanFactor());
        else
            return visit(ctx.booleanFactor().getFirst());
    }

    @Override
    public AstNode visitBooleanFactor(Cql2Parser.BooleanFactorContext ctx) {
        AstNode n = visit(ctx.booleanPrimary());
        return ctx.NOT() == null ? n : Not(n);
    }

    @Override
    public AstNode visitBooleanPrimary(Cql2Parser.BooleanPrimaryContext ctx) {
        if (ctx.booleanLiteral() != null) return visit((ctx.booleanLiteral()));
        if (ctx.booleanExpression() != null) return visit(ctx.booleanExpression());
        if (ctx.function() != null) return visit(ctx.function());
        if (ctx.predicate() != null) return visit(ctx.predicate());
        return null;
    }

    @Override
    public AstNode visitPredicate(Cql2Parser.PredicateContext ctx) {
        if (ctx.comparisonPredicate() != null) return visit(ctx.comparisonPredicate());
        if (ctx.spatialPredicate() != null) return visit(ctx.spatialPredicate());
        if (ctx.temporalPredicate() != null) return visit(ctx.temporalPredicate());
        if (ctx.arrayPredicate() != null) return visit(ctx.arrayPredicate());
        return null;
    }

    @Override
    public AstNode visitComparisonPredicate(Cql2Parser.ComparisonPredicateContext ctx) {
        if (ctx.binaryComparisonPredicate() != null) return visit(ctx.binaryComparisonPredicate());
        if (ctx.isLikePredicate() != null) return visit(ctx.isLikePredicate());
        if (ctx.isBetweenPredicate() != null) return visit(ctx.isBetweenPredicate());
        if (ctx.isInListPredicate() != null) return visit(ctx.isInListPredicate());
        if (ctx.isNullPredicate() != null) return visit(ctx.isNullPredicate());
        return null;
    }

    @Override
    public AstNode visitBinaryComparisonPredicate(Cql2Parser.BinaryComparisonPredicateContext ctx) {
        return OpArgs(ctx.COMP_OP().getText(), AstNodeType.BinaryComparisonPredicate, ctx.scalarExpression());
    }

    @Override
    public AstNode visitScalarExpression(Cql2Parser.ScalarExpressionContext ctx) {
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
    public AstNode visitIsLikePredicate(Cql2Parser.IsLikePredicateContext ctx) {
        AstNode n = OpArgs("like", AstNodeType.IsLikePredicate, ctx.characterExpression(), ctx.patternExpression());
        return ctx.NOT() == null ? n : Not(n);
    }

    @Override
    public AstNode visitPatternExpression(Cql2Parser.PatternExpressionContext ctx) {
        if (ctx.CASEI() != null) {
            return OpArgs("casei", AstNodeType.CharacterClause, ctx.patternExpression());
        } else if (ctx.ACCENTI() != null) {
            return OpArgs("accenti", AstNodeType.CharacterClause, ctx.patternExpression());
        }
        return null;
    }

    @Override
    public AstNode visitIsBetweenPredicate(Cql2Parser.IsBetweenPredicateContext ctx) {
        AstNode n = OpArgs("between", AstNodeType.IsBetweenPredicate, ctx.numericExpression());
        return ctx.NOT() == null ? n : Not(n);
    }

    @Override
    public AstNode visitNumericExpression(Cql2Parser.NumericExpressionContext ctx) {
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.arithmeticExpression() != null) return visit(ctx.arithmeticExpression());
        if (ctx.numericLiteral() != null) return visit(ctx.numericLiteral());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public AstNode visitIsInListPredicate(Cql2Parser.IsInListPredicateContext ctx) {
        AstNode n = OpArgs("in", AstNodeType.IsInListPredicate, ctx.scalarExpression(), ctx.inList());
        return ctx.NOT() == null ? n : Not(n);
    }

    @Override
    public AstNode visitInList(Cql2Parser.InListContext ctx) {
        return new AstNode(null, AstNodeType.InListOperands, Args(ctx.scalarExpression()));
    }

    @Override
    public AstNode visitIsNullPredicate(Cql2Parser.IsNullPredicateContext ctx) {
        AstNode n = OpArgs("isNull", AstNodeType.IsNullPredicate, ctx.isNullOperand());
        return ctx.NOT() == null ? n : Not(n);
    }

    @Override
    public AstNode visitIsNullOperand(Cql2Parser.IsNullOperandContext ctx) {
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
    public AstNode visitSpatialPredicate(Cql2Parser.SpatialPredicateContext ctx) {
        return OpArgs(ctx.SPATIAL_FUNC().getText().toLowerCase(), AstNodeType.SpatialPredicate, ctx.geomExpression());
    }

    @Override
    public AstNode visitGeomExpression(Cql2Parser.GeomExpressionContext ctx) {
        if (ctx.spatialInstance() != null) return visit(ctx.spatialInstance());
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public AstNode visitTemporalPredicate(Cql2Parser.TemporalPredicateContext ctx) {
        return OpArgs(toLowerCase(ctx.TEMPORAL_FUNC().getText()), AstNodeType.TemporalPredicate, ctx.temporalExpression());
    }

    @Override
    public AstNode visitTemporalExpression(Cql2Parser.TemporalExpressionContext ctx) {
        if (ctx.temporalInstance() != null) return visit(ctx.temporalInstance());
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public AstNode visitArrayPredicate(Cql2Parser.ArrayPredicateContext ctx) {
        return OpArgs(toLowerCase(ctx.ARRAY_FUNC().getText()), AstNodeType.ArrayPredicate, ctx.arrayExpression());
    }

    @Override
    public AstNode visitArrayExpression(Cql2Parser.ArrayExpressionContext ctx) {
        if (ctx.array() != null) return visit(ctx.array());
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public AstNode visitArray(Cql2Parser.ArrayContext ctx) {
        return new AstNode(null, AstNodeType.ArrayExpression, Args(ctx.arrayElement()));
    }

    @Override
    public AstNode visitArrayElement(Cql2Parser.ArrayElementContext ctx) {
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
    public AstNode visitArithmeticExpression(Cql2Parser.ArithmeticExpressionContext ctx) {
        AstNode n = visit(ctx.arithmeticTerm().getFirst());
        for (int i = 0; i < ctx.Sign().size(); i++)
            n = OpArgs(ctx.Sign(i).getText(), AstNodeType.ArithmeticExpression, n, ctx.arithmeticTerm(i + 1));
        return n;
    }

    @Override
    public AstNode visitArithmeticTerm(Cql2Parser.ArithmeticTermContext ctx) {
        AstNode n = visit(ctx.powerTerm().getFirst());
        for (int i = 0; i < ctx.ArithmeticOperatorMultDiv().size(); i++)
            n = OpArgs(ctx.ArithmeticOperatorMultDiv(i).getText(), AstNodeType.ArithmeticExpression, n, ctx.powerTerm(i + 1));
        return n;
    }

    @Override
    public AstNode visitPowerTerm(Cql2Parser.PowerTermContext ctx) {
        if (ctx.POWER() != null)
            return OpArgs(ctx.POWER().getText(), AstNodeType.ArithmeticExpression, ctx.arithmeticFactor());
        else
            return visit(ctx.arithmeticFactor().getFirst());
    }

    @Override
    public AstNode visitArithmeticFactor(Cql2Parser.ArithmeticFactorContext ctx) {
        if (ctx.arithmeticExpression() != null) return visit(ctx.arithmeticExpression());
        if (ctx.Sign() != null) return OpArgs(ctx.Sign().getText(), AstNodeType.ArithmeticExpression, ctx.arithmeticOperand());
        else return visit(ctx.arithmeticOperand());
    }

    @Override
    public AstNode visitArithmeticOperand(Cql2Parser.ArithmeticOperandContext ctx) {
        if (ctx.numericLiteral() != null) return visit(ctx.numericLiteral());
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public AstNode visitPropertyName(Cql2Parser.PropertyNameContext ctx) {
        return new AstNode(AstNodeType.PropertyLiteral, ctx.Identifier().getText());
    }

    @Override
    public AstNode visitFunction(Cql2Parser.FunctionContext ctx) {
        return OpArgs(ctx.Identifier().getText(), AstNodeType.FunctionRef, ctx.argumentList().argument());
    }

    @Override
    public AstNode visitArgument(Cql2Parser.ArgumentContext ctx) {
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
    public AstNode visitCharacterExpression(Cql2Parser.CharacterExpressionContext ctx) {
        if (ctx.characterClause() != null) return visit(ctx.characterClause());
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        return null;
    }

    @Override
    public AstNode visitCharacterClause(Cql2Parser.CharacterClauseContext ctx) {
        if (ctx.CASEI() != null) {
            return OpArgs("casei", AstNodeType.CharacterClause, ctx.characterExpression());
        } else if (ctx.ACCENTI() != null) {
            return OpArgs("accenti", AstNodeType.CharacterClause, ctx.characterExpression());
        }
        return new AstNode(AstNodeType.StringLiteral, ctx.CharacterLiteral().getText());
    }

    @Override
    public AstNode visitNumericLiteral(Cql2Parser.NumericLiteralContext ctx) {
        return parseNumber(ctx.getText());
    }

    @Override
    public AstNode visitBooleanLiteral(Cql2Parser.BooleanLiteralContext ctx) {
        return new AstNode(AstNodeType.BooleanLiteral, ctx.BOOL().getText().equalsIgnoreCase("TRUE"));
    }

    @Override
    public AstNode visitSpatialInstance(Cql2Parser.SpatialInstanceContext ctx) {
        if (ctx.geometryLiteral() != null) return visit(ctx.geometryLiteral());
        if (ctx.geometryCollectionTaggedText() != null) return visit(ctx.geometryCollectionTaggedText());
        if (ctx.bboxTaggedText() != null) return visit(ctx.bboxTaggedText());
        return null;
    }

    @Override
    public AstNode visitGeometryLiteral(Cql2Parser.GeometryLiteralContext ctx) {
        try {
            String originalText = tokens.getText(ctx.getSourceInterval());
            return new AstNode(AstNodeType.GeometryLiteral, reader.read(originalText));
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public AstNode visitGeometryCollectionTaggedText(Cql2Parser.GeometryCollectionTaggedTextContext ctx) {
        try {
            String originalText = tokens.getText(ctx.getSourceInterval());
            return new AstNode(AstNodeType.GeometryLiteral, reader.read(originalText));
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public AstNode visitBboxTaggedText(Cql2Parser.BboxTaggedTextContext ctx) {
        return new AstNode(AstNodeType.BBoxLiteral, visit(ctx.bboxText()));
    }

    @Override
    public AstNode visitTemporalInstance(Cql2Parser.TemporalInstanceContext ctx) {
        if (ctx.instantInstance() != null) return visit(ctx.instantInstance());
        if (ctx.intervalInstance() != null) return visit(ctx.intervalInstance());
        return null;
    }

    @Override
    public AstNode visitInstantInstance(Cql2Parser.InstantInstanceContext ctx) {
        if (ctx.dateInstant() != null) return visit(ctx.dateInstant());
        if (ctx.timestampInstant() != null) return visit(ctx.timestampInstant());
        return null;
    }

    @Override
    public AstNode visitDateInstant(Cql2Parser.DateInstantContext ctx) {
        return new AstNode(AstNodeType.DateLiteral, trim(ctx.CharacterLiteral().getText(), '\''));
    }

    @Override
    public AstNode visitTimestampInstant(Cql2Parser.TimestampInstantContext ctx) {
        String trimed = trim(ctx.CharacterLiteral().getText(), '\'');
        OffsetDateTime odt = OffsetDateTime.parse(trimed, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return new AstNode(AstNodeType.TimestampLiteral, odt.format(formatter));
    }

    @Override
    public AstNode visitIntervalInstance(Cql2Parser.IntervalInstanceContext ctx) {
        return new AstNode(null, AstNodeType.IntervalInstance, Args(ctx.instantParameter()));
    }

    @Override
    public AstNode visitInstantParameter(Cql2Parser.InstantParameterContext ctx) {
        if (ctx.CharacterLiteral() != null) {
            String trimed = trim(ctx.CharacterLiteral().getText(), '\'');
            if (trimed.equals("..") || !trimed.contains("T")) {
                return new AstNode(AstNodeType.DateLiteral, trimed);
            } else {
                OffsetDateTime odt = OffsetDateTime.parse(trimed, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                return new AstNode(AstNodeType.TimestampLiteral, odt.format(formatter));
            }
        }

        if (ctx.dateInstant() != null) return visit(ctx.dateInstant());
        if (ctx.timestampInstant() != null) return visit(ctx.dateInstant());
        if (ctx.propertyName() != null) return visit(ctx.propertyName());
        if (ctx.function() != null) return visit(ctx.function());
        if (ctx.DDOT() != null) return new AstNode(AstNodeType.DateLiteral, "..");
        return null;
    }
}
