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
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenRule;
import lang.c.CTokenizer;
import lang.c.CType;

/**
 * Before Testing Semantic Check by using this testing class, All ParseTest must be passed.
 * Bacause this testing class uses parse method to create testing data.
 */
public class SemanticCheckProgramTest_cv02 {

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

    @Test
    public void ExpressionAddTypeNoError() throws FatalErrorException {
        String[] testDataArr = {"1+1", "&1+1", "1+&1"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);
            cp.parse(cpContext);
    
            cp.semanticCheck(cpContext);
            String errorOutput = errorOutputStream.getPrintBufferString();
            assertThat(errorOutput, is(""));
        }
    }

    @Test
    public void FactorAmpNoError() throws FatalErrorException {
        inputStream.setInputString("&100");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(FactorAmp.isFirst(firstToken), is(true));
        FactorAmp cp = new FactorAmp(cpContext);
        cp.parse(cpContext);

        cp.semanticCheck(cpContext);
        assertThat(cp.getCType().getType(), is(CType.T_pint));
        String errorOutput = errorOutputStream.getPrintBufferString();
        assertThat(errorOutput, is(""));
    }

    @Test
    public void FactorWithAmpOverflow() throws FatalErrorException {
        String[] testDataArr = {"&65536", "&0200000", "&0x10000"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Factor.isFirst(firstToken), is(true));
            Factor cp = new Factor(cpContext);
            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData);
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("&の後ろはnumberです"));
            }    
        }
    }
    
    @Test
    public void FactorWithPlusSignOverflow() throws FatalErrorException {
        String[] testDataArr = {"32769"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Factor.isFirst(firstToken), is(false));
            Factor cp = new Factor(cpContext);
            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData);
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("不正なfactorです"));
            }    
        }
    }

    @Test
    public void FactorWithPlusSign32768() throws FatalErrorException {
        String[] testDataArr = {"32768"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Succeeded with " + testData, Factor.isFirst(firstToken), is(true));
            Factor cp = new Factor(cpContext);
            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                // assertThat(e.getMessage(), containsString("不正なfactorです"));
            }    
        }
    }
    


    
    @Test
    public void FactorWithMinuSignMax() throws FatalErrorException {
        String[] testDataArr = {"13-32769"}; // 負数の構文の代わりに減算で確認
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Expression.isFirst(firstToken), is(true)); // 第三引数は第二引数isFirst()の期待される結果
            Expression cp = new Expression(cpContext);
            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("-の後ろはtermです"));
            }    
        }
    }
    
    @Test
    public void FactorWithPlusSignMax() throws FatalErrorException {
        String[] testDataArr = {"32767"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Succeeded with " + testData, Factor.isFirst(firstToken), is(true));
            Factor cp = new Factor(cpContext);
            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("不正なfactorです"));
            }    
        }
    }

    @Test
    public void FactorWithMinusSignMax() throws FatalErrorException {
        String[] testDataArr = {"13-32768"}; // 負数の構文の代わりに減算で確認
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Succeeded with " + testData, Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);
            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("不正factorです"));
            }    
        }
    }

    @Test
    public void ExpressionSubNoError() throws FatalErrorException {
        inputStream.setInputString("13-32768"); // 負数の構文の代わりに減算で確認
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(Expression.isFirst(firstToken), is(true));
        CParseRule cp = new Expression(cpContext);
        cp.parse(cpContext);

        cp.semanticCheck(cpContext);
        String errorOutput = errorOutputStream.getPrintBufferString();
        assertThat(errorOutput, is(""));
    }
}
