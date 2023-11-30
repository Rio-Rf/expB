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
public class ParseIfTest {

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
        String[] testDataArr = {"if (true) { i_a=1; i_b=2; }",
                                "if (true) { i_a=1; i_b=2; } else { i_a=2; i_b=3;}",
                                "if (true) { i_a=1; i_b=2; } else if ( true ) { i_a=2; } else { i_a=3; }",
                                "if (true) { if (true) { if (true) { i_a=1; i_b=2; }}}",
                                "if (true) i_a=1;",
                                "if (true) i_a=1; else i_a=2;",
                                "if (true) if (true) if (true) i_a=3;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementIf.isFirst(firstToken), is(true));
            StatementIf cp = new StatementIf(cpContext);

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
        String[] testDataArr = {"if i_a==1"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementIf.isFirst(firstToken), is(true));
            StatementIf cp = new StatementIf(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("'IF'の後ろはconditionBlockです"));
            }
        }
    }

    // statement がない
    @Test
    public void parseErrorForNoStatement()  {
        String[] testDataArr = {"if (true)"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementIf.isFirst(firstToken), is(true));
            StatementIf cp = new StatementIf(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("conditionBlockの後ろはstatementです"));
            }
        }
    }

    // else のあと statement がない
    @Test
    public void parseErrorForNoStatementElse()  {
        String[] testDataArr = {"if (true) i_a=1; else"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementIf.isFirst(firstToken), is(true));
            StatementIf cp = new StatementIf(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("'ELSE'の後ろはstatementです"));
            }
        }
    }
    
}