package ai.flexgalaxy;

import ai.flexgalaxy.Cql2g4.Cql2Parser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonConverterVisitor extends ai.flexgalaxy.Cql2g4.Cql2ParserBaseVisitor<JsonNode> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonConverterVisitor() {

    }

    String toJsonString(ParseTree tree) throws JsonProcessingException {
        JsonNode j = visit(tree);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(j);
    }

    @Override
    public JsonNode visitBooleanExpression(Cql2Parser.BooleanExpressionContext ctx) {
        if (ctx.booleanTerm().size() > 1) {
            ObjectNode n = objectMapper.createObjectNode();
            n.put("op", "or");
            ArrayNode args = objectMapper.createArrayNode();
            ctx.booleanTerm().forEach(term -> args.add(visit(term)));
            n.set("args", args);
            return n;
        } else {
            return visit(ctx.booleanTerm().getFirst());
        }
    }

    @Override
    public JsonNode visitBooleanTerm(Cql2Parser.BooleanTermContext ctx) {
        ObjectNode n = objectMapper.createObjectNode();
        n.put("op", "and");
        ArrayNode args = objectMapper.createArrayNode();
        ctx.booleanFactor().forEach(term -> args.add(visit(term)));
        n.set("args", args);
        return n;
    }

    @Override
    public JsonNode visitBooleanFactor(Cql2Parser.BooleanFactorContext ctx) {
        JsonNode primary = visit(ctx.booleanPrimary());
        if (ctx.NOT() == null) {
            return primary;
        } else {
            ObjectNode n = objectMapper.createObjectNode();
            n.put("op", "not");
            ArrayNode args = objectMapper.createArrayNode();
            args.add(primary);
            n.set("args", args);
            return n;
        }
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
        return null;
    }

    @Override
    public JsonNode visitComparisonPredicate(Cql2Parser.ComparisonPredicateContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitBinaryComparisonPredicate(Cql2Parser.BinaryComparisonPredicateContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitScalarExpression(Cql2Parser.ScalarExpressionContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitComparisonOperator(Cql2Parser.ComparisonOperatorContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitIsLikePredicate(Cql2Parser.IsLikePredicateContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitPatternExpression(Cql2Parser.PatternExpressionContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitIsBetweenPredicate(Cql2Parser.IsBetweenPredicateContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitNumericExpression(Cql2Parser.NumericExpressionContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitIsInListPredicate(Cql2Parser.IsInListPredicateContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitInList(Cql2Parser.InListContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitIsNullPredicate(Cql2Parser.IsNullPredicateContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitIsNullOperand(Cql2Parser.IsNullOperandContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitSpatialPredicate(Cql2Parser.SpatialPredicateContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitSpatialFunction(Cql2Parser.SpatialFunctionContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitGeomExpression(Cql2Parser.GeomExpressionContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitTemporalFunction(Cql2Parser.TemporalFunctionContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitTemporalPredicate(Cql2Parser.TemporalPredicateContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitTemporalExpression(Cql2Parser.TemporalExpressionContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitArrayFunction(Cql2Parser.ArrayFunctionContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitArrayPredicate(Cql2Parser.ArrayPredicateContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitArrayExpression(Cql2Parser.ArrayExpressionContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitArray(Cql2Parser.ArrayContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitArrayElement(Cql2Parser.ArrayElementContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitArithmeticExpression(Cql2Parser.ArithmeticExpressionContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitArithmeticTerm(Cql2Parser.ArithmeticTermContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitPowerTerm(Cql2Parser.PowerTermContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitArithmeticFactor(Cql2Parser.ArithmeticFactorContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitArithmeticOperand(Cql2Parser.ArithmeticOperandContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitPropertyName(Cql2Parser.PropertyNameContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitFunction(Cql2Parser.FunctionContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitArgumentList(Cql2Parser.ArgumentListContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitArgument(Cql2Parser.ArgumentContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitCharacterExpression(Cql2Parser.CharacterExpressionContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitCharacterClause(Cql2Parser.CharacterClauseContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitSignedInteger(Cql2Parser.SignedIntegerContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitDecimalNumericLiteral(Cql2Parser.DecimalNumericLiteralContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitScientificNumericLiteral(Cql2Parser.ScientificNumericLiteralContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitUnsignedNumericLiteral(Cql2Parser.UnsignedNumericLiteralContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitSignedNumericLiteral(Cql2Parser.SignedNumericLiteralContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitNumericLiteral(Cql2Parser.NumericLiteralContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitBooleanLiteral(Cql2Parser.BooleanLiteralContext ctx) {
        return objectMapper.valueToTree(ctx.BOOL().getSymbol().getText());
    }

    @Override
    public JsonNode visitSpatialInstance(Cql2Parser.SpatialInstanceContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitGeometryLiteral(Cql2Parser.GeometryLiteralContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitPointTaggedText(Cql2Parser.PointTaggedTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitLinestringTaggedText(Cql2Parser.LinestringTaggedTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitPolygonTaggedText(Cql2Parser.PolygonTaggedTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitMultipointTaggedText(Cql2Parser.MultipointTaggedTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitMultilinestringTaggedText(Cql2Parser.MultilinestringTaggedTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitMultipolygonTaggedText(Cql2Parser.MultipolygonTaggedTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitGeometryCollectionTaggedText(Cql2Parser.GeometryCollectionTaggedTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitPointText(Cql2Parser.PointTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitPoint(Cql2Parser.PointContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitXCoord(Cql2Parser.XCoordContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitYCoord(Cql2Parser.YCoordContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitZCoord(Cql2Parser.ZCoordContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitLineStringText(Cql2Parser.LineStringTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitLinearRingText(Cql2Parser.LinearRingTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitPolygonText(Cql2Parser.PolygonTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitMultiPointText(Cql2Parser.MultiPointTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitMultiLineStringText(Cql2Parser.MultiLineStringTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitMultiPolygonText(Cql2Parser.MultiPolygonTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitGeometryCollectionText(Cql2Parser.GeometryCollectionTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitBboxTaggedText(Cql2Parser.BboxTaggedTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitBboxText(Cql2Parser.BboxTextContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitWestBoundLon(Cql2Parser.WestBoundLonContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitEastBoundLon(Cql2Parser.EastBoundLonContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitNorthBoundLat(Cql2Parser.NorthBoundLatContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitSouthBoundLat(Cql2Parser.SouthBoundLatContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitMinElev(Cql2Parser.MinElevContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitMaxElev(Cql2Parser.MaxElevContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitTemporalInstance(Cql2Parser.TemporalInstanceContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitInstantInstance(Cql2Parser.InstantInstanceContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitDateInstant(Cql2Parser.DateInstantContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitTimestampInstant(Cql2Parser.TimestampInstantContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitIntervalInstance(Cql2Parser.IntervalInstanceContext ctx) {
        return null;
    }

    @Override
    public JsonNode visitInstantParameter(Cql2Parser.InstantParameterContext ctx) {
        return null;
    }

//    @Override
//    public JsonNode visit(ParseTree tree) {
//        return null;
//    }

    @Override
    public JsonNode visitChildren(RuleNode node) {
        return null;
    }

    @Override
    public JsonNode visitTerminal(TerminalNode node) {
        return null;
    }

    @Override
    public JsonNode visitErrorNode(ErrorNode node) {
        return null;
    }
}
