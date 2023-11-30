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
public class ParseWhileTest {

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

    // 正当
    @Test
    public void parseNoneError()  {
        String[] testDataArr = {"while (true) { i_a=1; }",
                                "while (true) { while (true) { i_a=1; i_b=2;} }",
                                // // 以下は単独ステートメントも許す場合 (StatementBlock を使っている場合上手くやれば以下は自動的にできる)
                                "while (true) i_a=1;",
                                "while (true) while (true) while (true) i_a=1;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementWhile.isFirst(firstToken), is(true));
            StatementWhile cp = new StatementWhile(cpContext);

            try {
                cp.parse(cpContext);
                // fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("Unexpected FatalErrorException"));
            }
        } 
    }

    // conditionBlock がない
    @Test
    public void parseErrorForNoConditionBlock()  {
        String[] testDataArr = {"while i_a==1"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementWhile.isFirst(firstToken), is(true));
            StatementWhile cp = new StatementWhile(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("'While'の後ろはconditionBlockです"));
            }
        }
    }

    // statement がない
    @Test
    public void parseErrorForNoStatement()  {
        String[] testDataArr = {"while (true)"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementWhile.isFirst(firstToken), is(true));
            StatementWhile cp = new StatementWhile(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("conditionBlockの後ろはstatementです"));
            }
        }
    }
    
}