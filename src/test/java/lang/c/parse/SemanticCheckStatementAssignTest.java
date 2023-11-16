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
public class SemanticCheckStatementAssignTest {

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

    // (1) 整数型の扱い
    // If it is difficult to understand, separate the test cases and create a new test.
    @Test
    public void SemanticCheckAssignIntegerTypeOK() throws FatalErrorException { // 整数型
        String[] testDataArr = {"i_A=i_B;"};
        for ( String testData: testDataArr ) { 
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        } 
    }
    
    @Test
    public void SemanticCheckAssignIntegerTypeErrorMult() throws FatalErrorException {
        String[] testDataArr = { "*i_a=1;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("*の後はpint型です"));
            }
        } 
    }

    @Test
    public void SemanticCheckAssignIntegerTypeErrorArray() throws FatalErrorException {
        String[] testDataArr = { "i_a[3]=1;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("i_は配列の変数名として不適です"));
            }
        } 
    }

    @Test
    public void SemanticCheckAssignIntegerTypeError() throws FatalErrorException {
        String[] testDataArr = { "i_a=&1;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("左辺の型[int]と右辺の型[int*]が一致しません"));
            }
        } 
    }
    // (2) ポインタ型の扱い
    @Test
    public void SemanticCheckAssignPointerTypeOK() throws FatalErrorException { // ポインタ型
        String[] testDataArr = {"ip_A=ip_B;", "*ip_A=i_A;", "ip_A=&i_A;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        } 
    }

    @Test
    public void SemanticCheckAssignPinterTypeError() throws FatalErrorException {
        String[] testDataArr = { "ip_a=1;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("左辺の型[int*]と右辺の型[int]が一致しません"));
            }
        } 
    }

    // (3) 配列型の扱い
    @Test
    public void SemanticCheckAssignArrayTypeError() throws FatalErrorException {
        String[] testDataArr = { "ia_a=1;", "ia_a=ia_a;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("ia_は整数の変数名として不適です"));
            }
        } 
    }

    // (3) ポインタ配列型の扱い
    @Test
    public void SemanticCheckAssignPointArrayTypeError() throws FatalErrorException {
        String[] testDataArr = { "ipa_a=&1;", "ipa_a=ipa_a;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("ipa_は整数の変数名として不適です"));
            }
        } 
    }

    @Test
    public void SemanticCheckAssignPointArrayTypeErrorNotEqual() throws FatalErrorException {
        String[] testDataArr = { "*ipa_a[3]=&3;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("左辺の型[int]と右辺の型[int*]が一致しません"));
            }
        } 
    }

    // (5) 定数には代入できないことの確認
    @Test
    public void SemanticCheckAssignConstantTypeError() throws FatalErrorException {
        String[] testDataArr = { "c_a=1;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("定数に値を代入することはできません"));
            }
        } 
    }

    // (extra) code should be written as follows
    @Test
    public void SemanticCheckAssignIntPint() throws FatalErrorException {
        String[] testDataArr = { "i_A=ip_B;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("左辺の型[int]と右辺の型[int*]が一致しません"));
            }
        } 
    }

    @Test
    public void SemanticCheckAssignPintInt() throws FatalErrorException {
        String[] testDataArr = { "ip_A=i_B;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("左辺の型[int*]と右辺の型[int]が一致しません"));
            }
        } 
    }

    @Test
    public void SemanticCheckAssignMultPint() throws FatalErrorException {
        String[] testDataArr = { "*ip_A=ip_A;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("左辺の型[int]と右辺の型[int*]が一致しません"));
            }
        } 
    }

    @Test
    public void SemanticCheckAssignIntPintAmp() throws FatalErrorException {
        String[] testDataArr = { "i_A=&i_B;" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("左辺の型[int]と右辺の型[int*]が一致しません"));
            }
        } 
    }

    // 2文以上のテストケースはSemanticCheckProgramAssignTest.javaに追加した
}