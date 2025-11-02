package io.github.IndoorSpatial.cql2;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.IndoorSpatial.cql2.ast.AstNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class Cql2G4Test {
    static Stream<String> testFiles() {
        String textPrefix = "src/test/resources/marie/";
        File dir = new File(textPrefix);
        File[] files = dir.listFiles((f, name) -> name.endsWith(".txt"));
        assertNotNull(files);
        return Arrays.stream(files).map(f -> textPrefix + f.getName());
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    void textToAst(String filename) throws IOException {
        // read origin text
        String originText = Files.readString(Paths.get(filename), java.nio.charset.StandardCharsets.UTF_8);
        System.out.println(originText);

        // text -> ast
        Cql2G4 cql2G4 = new Cql2G4();
        AstNode astNode = cql2G4.textToAst(originText);
        System.out.println(astNode.ToString());
    }

    @Test
    void jsonNodeToAst() {
    }

    @Test
    void jsonStringToAst() {
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    void astToSql(String filename) throws IOException {
        // read origin text
        String originText = Files.readString(Paths.get(filename), java.nio.charset.StandardCharsets.UTF_8);
        System.out.println(originText);

        // text -> ast
        Cql2G4 cql2G4 = new Cql2G4();
        AstNode astNode = cql2G4.textToAst(originText);
        System.out.println(astNode.ToString());

        // ast -> sql
        System.out.println(cql2G4.astToSql(astNode));
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    void astToText(String filename) throws IOException {
        // read origin text
        String originText = Files.readString(Paths.get(filename), java.nio.charset.StandardCharsets.UTF_8);
        System.out.println(originText);

        // text -> ast
        Cql2G4 cql2G4 = new Cql2G4();
        AstNode astNode = cql2G4.textToAst(originText);
        System.out.println(astNode.ToString());

        // ast -> text
        System.out.println(cql2G4.astToText(astNode));
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    void astToJsonNode(String filename) throws IOException {
        // read origin text
        String originText = Files.readString(Paths.get(filename), java.nio.charset.StandardCharsets.UTF_8);
        System.out.println(originText);

        // text -> ast
        Cql2G4 cql2G4 = new Cql2G4();
        AstNode astNode = cql2G4.textToAst(originText);
        System.out.println(astNode.ToString());

        // ast -> json node
        JsonNode jsonNode = cql2G4.astToJsonNode(astNode);
        System.out.println(jsonNode.toString());
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    void astToJsonString(String filename) throws IOException {
        // read origin text
        String originText = Files.readString(Paths.get(filename), java.nio.charset.StandardCharsets.UTF_8);
        System.out.println(originText);

        // text -> ast
        Cql2G4 cql2G4 = new Cql2G4();
        AstNode astNode = cql2G4.textToAst(originText);
        System.out.println(astNode.ToString());

        // ast -> json string
        System.out.println(cql2G4.astToJsonString(astNode));
    }
}