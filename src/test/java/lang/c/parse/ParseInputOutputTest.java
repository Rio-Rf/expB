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
public class ParseInputOutputTest {

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

    // input primary がない
    @Test
    public void parseErrorForNoPrimaryInput()  {
        String[] testDataArr = {"input 3;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementInput.isFirst(firstToken), is(true));
            StatementInput cp = new StatementInput(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("'INPUT'の後ろはprimaryです"));
            }
        }
    }

    // input ; がない
    @Test
    public void parseErrorForNoSEMIInput()  {
        String[] testDataArr = {"input i_a+2;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementInput.isFirst(firstToken), is(true));
            StatementInput cp = new StatementInput(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("primaryの後ろは';'です"));
            }
        }
    }

    // input ; がない
    @Test
    public void parseErrorForNoSEMIInput2()  {
        String[] testDataArr = {"input 3"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementInput.isFirst(firstToken), is(true));
            StatementInput cp = new StatementInput(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("'INPUT'の後ろはprimaryです"));
            }
        }
    }
    
    // output expression がない
    @Test
    public void parseErrorForNoPrimaryOutput()  {
        String[] testDataArr = {"output ;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementOutput.isFirst(firstToken), is(true));
            StatementOutput cp = new StatementOutput(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("'OUTPUT'の後ろはexpressionです"));
            }
        }
    }

    // output ; がない
    @Test
    public void parseErrorForNoSEMIOutput()  {
        String[] testDataArr = {"output &i_a"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementOutput.isFirst(firstToken), is(true));
            StatementOutput cp = new StatementOutput(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("expressionの後ろは';'です"));
            }
        }
    }

    // Input 正当
    @Test
    public void parseNoneErrorInput()  {
        String[] testDataArr = {"input i_a;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementInput.isFirst(firstToken), is(true));
            StatementInput cp = new StatementInput(cpContext);

            try {
                cp.parse(cpContext);
                // fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("Unexpected FatalErrorException"));
            }
        } 
    }

    // input ; がない
    @Test
    public void parseErrorForNoSEMIInputI_b()  {
        String[] testDataArr = {"input i_b"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementInput.isFirst(firstToken), is(true));
            StatementInput cp = new StatementInput(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("primaryの後ろは';'です"));
            }
        }
    }

    // input primary がない
    @Test
    public void parseErrorForNoPrimaryInput2()  {
        String[] testDataArr = {"input"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementInput.isFirst(firstToken), is(true));
            StatementInput cp = new StatementInput(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("'INPUT'の後ろはprimaryです"));
            }
        }
    }

    // output 正当
    @Test
    public void parseNoneErrorOutput()  {
        String[] testDataArr = {"output 1+3;", "output &ip_b;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementOutput.isFirst(firstToken), is(true));
            StatementOutput cp = new StatementOutput(cpContext);

            try {
                cp.parse(cpContext);
                // fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("Unexpected FatalErrorException"));
            }
        } 
    }

    // output ; がない
    @Test
    public void parseErrorForNoSEMIOutput2()  {
        String[] testDataArr = {"output 3"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementOutput.isFirst(firstToken), is(true));
            StatementOutput cp = new StatementOutput(cpContext);

            try {
                cp.parse(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("expressionの後ろは';'です"));
            }
        }
    }

}