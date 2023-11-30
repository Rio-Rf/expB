package lang.c;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;

public class CTokenizerIfWhileInputOutputTest {

    InputStreamForTest inputStream;
    PrintStreamForTest outputStream;
    PrintStreamForTest errorOutputStream;
    IOContext context;
    CTokenizer tokenizer;
    CParseContext cpContext;
    CTokenizerTestHelper helper;

    @Before
    public void setUp() {
        inputStream = new InputStreamForTest();
        outputStream = new PrintStreamForTest(System.out);
        errorOutputStream = new PrintStreamForTest(System.err);
        context = new IOContext(inputStream, outputStream, errorOutputStream);
        tokenizer = new CTokenizer(new CTokenRule());
        cpContext = new CParseContext(context, tokenizer);
        helper = new CTokenizerTestHelper();
    }

    @After
    public void tearDown() {
        inputStream = null;
        outputStream = null;
        errorOutputStream = null;
        context = null;
        tokenizer = null;
        cpContext = null;
        helper = null;
    }

    @Test
    public void statementIf() {
        String testString = "if";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_IF, "if", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 3);
    }

    @Test
    public void statementElse() {
        String testString = "else";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_ELSE, "else", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 5);
    }

    @Test
    public void statementWhile() {
        String testString = "while";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_WHILE, "while", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 6);
    }

    @Test
    public void statementInput() {
        String testString = "input";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_INPUT, "input", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 6);
    }

    @Test
    public void statementOUTPUT() {
        String testString = "output";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_OUTPUT, "output", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 7);
    }

    @Test
    public void statementLRCAR() {
        String testString = "{}";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_LCUR, "{", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token2, CToken.TK_RCUR, "}", 1, 2);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_EOF, "end_of_file", 1, 3);
    }

    // Please copy and paste the above code and add the specified test case to the following
    // if{else}endif;while(input)output

    @Test
    public void statementIfElseEndifWhileInputOutput2() {
        String testString = "if{else}endif;while(input)output";
        CToken[] expectedTokens = {
            new CToken(CToken.TK_IF, 1, 1, "if"),
            new CToken(CToken.TK_LCUR, 1, 3, "{"),
            new CToken(CToken.TK_ELSE, 1, 4, "else"),
            new CToken(CToken.TK_RCUR, 1, 8, "}"),
            new CToken(CToken.TK_IDENT, 1, 9, "endif"),
            new CToken(CToken.TK_SEMI, 1, 14, ";"),
            new CToken(CToken.TK_WHILE, 1, 15, "while"),
            new CToken(CToken.TK_LPAR, 1, 20, "("),
            new CToken(CToken.TK_INPUT, 1, 21, "input"),
            new CToken(CToken.TK_RPAR, 1, 26, ")"),
            new CToken(CToken.TK_OUTPUT, 1, 27, "output"),
            new CToken(CToken.TK_EOF, 1, 33, "end_of_file")
        };

        inputStream.setInputString(testString);
        for (int i=0; i<expectedTokens.length; i++) {
            CToken token = tokenizer.getNextToken(cpContext);
            helper.checkToken("token "+(i+1) , token, expectedTokens[i]);
        }
    }
}