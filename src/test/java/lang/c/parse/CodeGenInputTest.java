package lang.c.parse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.FatalErrorException;
import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CTokenRule;
import lang.c.CTokenizer;
import lang.c.TestHelper;

public class CodeGenInputTest {
    
    InputStreamForTest inputStream;
    PrintStreamForTest outputStream;
    PrintStreamForTest errorOutputStream;
    CTokenizer tokenizer;
    IOContext context;
    CParseContext cpContext;
    TestHelper helper = new TestHelper();

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

    @Test
    public void codeGenInputTest() throws FatalErrorException {
        inputStream.setInputString(
            """
            input ip_a; 
            """);
                                            
        String expected = 
            """
                . = 0x100
                JMP __START
            __START:
                MOV #0x1000, R6
                MOV #ip_a, (R6)+ ;input ip_a
                MOV #0xFFE0, R0
                MOV (R0), (R6)+
                MOV -(R6), R1
                MOV -(R6), R0
                MOV R1, (R0)
            HLT
            .END
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // Please copy and paste the above code and add the specified test case to the following
    
    @Test
    public void codeGenInputTestIa() throws FatalErrorException {
        inputStream.setInputString(
            """
            input ia_a[2]; 
            """);
                                            
        String expected = 
            """
                . = 0x100
                JMP __START
            __START:
                MOV #0x1000, R6
                MOV #ia_a, (R6)+ ;input ia_a[2]
                MOV #2, (R6)+
                MOV -(R6), R0
                MOV -(R6), R1
                ADD R1, R0
                MOV R0, (R6)+
                MOV #0xFFE0, R0
                MOV (R0), (R6)+
                MOV -(R6), R1
                MOV -(R6), R0
                MOV R1, (R0)
            HLT
            .END
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenInputTestIp() throws FatalErrorException {
        inputStream.setInputString(
            """
            input *ip_a; 
            """);
                                            
        String expected = 
            """
                . = 0x100
                JMP __START
            __START:
                MOV #0x1000, R6
                MOV #ip_a, (R6)+ ;input *ip_a
                MOV -(R6), R0
                MOV (R0), (R6)+
                MOV #0xFFE0, R0
                MOV (R0), (R6)+
                MOV -(R6), R1
                MOV -(R6), R0
                MOV R1, (R0)
            HLT
            .END
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenInputTestIpa() throws FatalErrorException {
        inputStream.setInputString(
            """
            input *ipa_a[2]; 
            """);
                                            
        String expected = 
            """
                . = 0x100
                JMP __START
            __START:
                MOV #0x1000, R6
                MOV #ipa_a, (R6)+ ;input *ip_a[2]
                MOV #2, (R6)+
                MOV -(R6), R0
                MOV -(R6), R1
                ADD R1, R0
                MOV R0, (R6)+
                MOV -(R6), R0
                MOV (R0), (R6)+
                MOV #0xFFE0, R0
                MOV (R0), (R6)+
                MOV -(R6), R1
                MOV -(R6), R0
                MOV R1, (R0)
            HLT
            .END
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

}
