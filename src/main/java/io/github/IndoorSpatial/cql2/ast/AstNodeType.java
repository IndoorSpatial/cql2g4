package io.github.IndoorSpatial.cql2.ast;

public enum AstNodeType {
    AndOrExpression,
    NotExpression,
    BinaryComparisonPredicate,
    ArithmeticExpression,
    IsLikePredicate,
    IsBetweenPredicate,
    IsInListPredicate,
    IsNullPredicate,
    CharacterClause,
    SpatialPredicate,
    TemporalPredicate,
    ArrayPredicate,

    FunctionRef,

    IntervalInstance,
    InListOperands,
    ArrayExpression,

    PropertyLiteral,
    BooleanLiteral,
    IntegerLiteral,
    DoubleLiteral,
    StringLiteral,
    GeometryLiteral,
    BBoxLiteral,
    DateLiteral,
    TimestampLiteral,
}
