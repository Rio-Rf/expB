package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.FatalErrorException;
import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;
import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.CTokenRule;
import lang.c.CTokenizer;

/**
 * Before Testing Semantic Check by using this testing class, All ParseTest must be passed.
 * Bacause this testing class uses parse method to create testing data.
 */
public class SemanticCheckProgramAssignTest {

    InputStreamForTest inputStream;
    PrintStreamForTest outputStream;
    PrintStreamForTest errorOutputStream;
    CTokenizer tokenizer;
    IOContext context;
    CParseContext cpContext;

    @Before
    public void setUp() {
        inputStream = new InputStreamForTest();
        outputStream = new PrintStreamForTest(System.out);
        errorOutputStream = new PrintStreamForTest(System.err);
        context = new IOContext(inputStream, outputStream, errorOutputStream);
        tokenizer = new CTokenizer(new CTokenRule());
        cpContext = new CParseContext(context, tokenizer);
    }

    @After
    public void tearDown() {
        inputStream = null;
        outputStream = null;
        errorOutputStream = null;
        tokenizer = null;
        context = null;
        cpContext = null;
    }

    void resetEnvironment() {
        tearDown();
        setUp();
    }

    // 正当なテストケース
    @Test
    public void SemanticCheckAssignIntegerTypeOK() throws FatalErrorException { // 整数型
        String[] testDataArr = {"i_A=i_B;ip_A=ip_B;*ip_A=i_A;"};
        for ( String testData: testDataArr ) { 
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Program.isFirst(firstToken), is(true));
            Program cp = new Program(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        } 
    }

    // 1文目が不当な場合
    @Test
    public void SemanticCheckAssignIntegerTypeErrorOne() throws FatalErrorException {
        String[] testDataArr = { "i_a=&1;i_A=i_B;i_A=i_B;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Program.isFirst(firstToken), is(true));
            Program cp = new Program(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("左辺の型[int]と右辺の型[int*]が一致しません"));
            }
        } 
    }

    // 2文目が不当な場合
    @Test
    public void SemanticCheckAssignIntegerTypeErrorTwo() throws FatalErrorException {
        String[] testDataArr = { "i_A=i_B;i_a=&1;i_A=i_B;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Program.isFirst(firstToken), is(true));
            Program cp = new Program(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("左辺の型[int]と右辺の型[int*]が一致しません"));
            }
        } 
    }

    // 3文目が不当な場合
    @Test
    public void SemanticCheckAssignPinterTypeErrorThree() throws FatalErrorException {
        String[] testDataArr = { "i_A=i_B;i_A=i_B;ip_a=1;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Program.isFirst(firstToken), is(true));
            Program cp = new Program(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("左辺の型[int*]と右辺の型[int]が一致しません"));
            }
        } 
    }
}