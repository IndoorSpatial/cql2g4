package ai.flexgalaxy;

import ai.flexgalaxy.Cql2g4.Cql2Parser;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonConverterVisitor extends ai.flexgalaxy.Cql2g4.Cql2ParserBaseVisitor<ObjectNode> {

    public JsonConverterVisitor() {

    }

    public ObjectNode convert() {
        return null;
    }

    @Override
    public ObjectNode visitBooleanExpression(Cql2Parser.BooleanExpressionContext ctx) {
        ctx.booleanTerm().stream().forEach(x->{});
        return null;
    }

    @Override
    public ObjectNode visitBooleanTerm(Cql2Parser.BooleanTermContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitBooleanFactor(Cql2Parser.BooleanFactorContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitBooleanPrimary(Cql2Parser.BooleanPrimaryContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitPredicate(Cql2Parser.PredicateContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitComparisonPredicate(Cql2Parser.ComparisonPredicateContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitBinaryComparisonPredicate(Cql2Parser.BinaryComparisonPredicateContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitScalarExpression(Cql2Parser.ScalarExpressionContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitComparisonOperator(Cql2Parser.ComparisonOperatorContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitIsLikePredicate(Cql2Parser.IsLikePredicateContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitPatternExpression(Cql2Parser.PatternExpressionContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitIsBetweenPredicate(Cql2Parser.IsBetweenPredicateContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitNumericExpression(Cql2Parser.NumericExpressionContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitIsInListPredicate(Cql2Parser.IsInListPredicateContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitInList(Cql2Parser.InListContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitIsNullPredicate(Cql2Parser.IsNullPredicateContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitIsNullOperand(Cql2Parser.IsNullOperandContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitSpatialPredicate(Cql2Parser.SpatialPredicateContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitSpatialFunction(Cql2Parser.SpatialFunctionContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitGeomExpression(Cql2Parser.GeomExpressionContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitTemporalFunction(Cql2Parser.TemporalFunctionContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitTemporalPredicate(Cql2Parser.TemporalPredicateContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitTemporalExpression(Cql2Parser.TemporalExpressionContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitArrayFunction(Cql2Parser.ArrayFunctionContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitArrayPredicate(Cql2Parser.ArrayPredicateContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitArrayExpression(Cql2Parser.ArrayExpressionContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitArray(Cql2Parser.ArrayContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitArrayElement(Cql2Parser.ArrayElementContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitArithmeticExpression(Cql2Parser.ArithmeticExpressionContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitArithmeticTerm(Cql2Parser.ArithmeticTermContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitPowerTerm(Cql2Parser.PowerTermContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitArithmeticFactor(Cql2Parser.ArithmeticFactorContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitArithmeticOperand(Cql2Parser.ArithmeticOperandContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitPropertyName(Cql2Parser.PropertyNameContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitFunction(Cql2Parser.FunctionContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitArgumentList(Cql2Parser.ArgumentListContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitArgument(Cql2Parser.ArgumentContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitCharacterExpression(Cql2Parser.CharacterExpressionContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitCharacterClause(Cql2Parser.CharacterClauseContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitSignedInteger(Cql2Parser.SignedIntegerContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitDecimalNumericLiteral(Cql2Parser.DecimalNumericLiteralContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitScientificNumericLiteral(Cql2Parser.ScientificNumericLiteralContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitUnsignedNumericLiteral(Cql2Parser.UnsignedNumericLiteralContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitSignedNumericLiteral(Cql2Parser.SignedNumericLiteralContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitNumericLiteral(Cql2Parser.NumericLiteralContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitBooleanLiteral(Cql2Parser.BooleanLiteralContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitSpatialInstance(Cql2Parser.SpatialInstanceContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitGeometryLiteral(Cql2Parser.GeometryLiteralContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitPointTaggedText(Cql2Parser.PointTaggedTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitLinestringTaggedText(Cql2Parser.LinestringTaggedTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitPolygonTaggedText(Cql2Parser.PolygonTaggedTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitMultipointTaggedText(Cql2Parser.MultipointTaggedTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitMultilinestringTaggedText(Cql2Parser.MultilinestringTaggedTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitMultipolygonTaggedText(Cql2Parser.MultipolygonTaggedTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitGeometryCollectionTaggedText(Cql2Parser.GeometryCollectionTaggedTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitPointText(Cql2Parser.PointTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitPoint(Cql2Parser.PointContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitXCoord(Cql2Parser.XCoordContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitYCoord(Cql2Parser.YCoordContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitZCoord(Cql2Parser.ZCoordContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitLineStringText(Cql2Parser.LineStringTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitLinearRingText(Cql2Parser.LinearRingTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitPolygonText(Cql2Parser.PolygonTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitMultiPointText(Cql2Parser.MultiPointTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitMultiLineStringText(Cql2Parser.MultiLineStringTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitMultiPolygonText(Cql2Parser.MultiPolygonTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitGeometryCollectionText(Cql2Parser.GeometryCollectionTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitBboxTaggedText(Cql2Parser.BboxTaggedTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitBboxText(Cql2Parser.BboxTextContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitWestBoundLon(Cql2Parser.WestBoundLonContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitEastBoundLon(Cql2Parser.EastBoundLonContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitNorthBoundLat(Cql2Parser.NorthBoundLatContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitSouthBoundLat(Cql2Parser.SouthBoundLatContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitMinElev(Cql2Parser.MinElevContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitMaxElev(Cql2Parser.MaxElevContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitTemporalInstance(Cql2Parser.TemporalInstanceContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitInstantInstance(Cql2Parser.InstantInstanceContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitDateInstant(Cql2Parser.DateInstantContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitTimestampInstant(Cql2Parser.TimestampInstantContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitIntervalInstance(Cql2Parser.IntervalInstanceContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visitInstantParameter(Cql2Parser.InstantParameterContext ctx) {
        return null;
    }

    @Override
    public ObjectNode visit(ParseTree tree) {
        return null;
    }

    @Override
    public ObjectNode visitChildren(RuleNode node) {
        return null;
    }

    @Override
    public ObjectNode visitTerminal(TerminalNode node) {
        return null;
    }

    @Override
    public ObjectNode visitErrorNode(ErrorNode node) {
        return null;
    }
}
