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

public class CodeGenWhileTest {
    
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
    public void codeGenWhileTest2() throws FatalErrorException {
        inputStream.setInputString(
            """
            i_b = 1;
            while (i_b == 1) {
                input i_a;
                i_b = 4;
            }
            """);
                                            
        String expected = 
            """
                . = 0x100
                JMP __START
            __START:
                MOV #0x1000, R6
                MOV	#i_b, (R6)+ ;i_b=1
                MOV	#1, (R6)+
                MOV	-(R6), R1
                MOV	-(R6), R0
                MOV	R1, (R0)
            WHILE1:
                MOV #i_b, (R6)+ ;i_b==1
                MOV -(R6), R0
                MOV (R0), (R6)+
                MOV #1, (R6)+
                MOV -(R6), R0
                MOV -(R6), R1
                MOV #0x0001, R2
                CMP R0, R1
                BRZ EQ2
                CLR R2
            EQ2: MOV R2, (R6)+
                MOV -(R6), R0
                BRZ WHILEEND1
                MOV #i_a, (R6)+ ;input i_a
                MOV #0xFFE0, R0
                MOV (R0), (R6)+
                MOV -(R6), R1
                MOV -(R6), R0
                MOV R1, (R0)
                MOV #i_b, (R6)+ ;i_b=4
                MOV #4, (R6)+
                MOV -(R6), R1
                MOV -(R6), R0
                MOV R1, (R0)
                JMP WHILE1
            WHILEEND1:
            HLT
            .END
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // Please copy and paste the above code and add the specified test case to the following
    
    @Test
    public void codeGenWhileTestWhile() throws FatalErrorException {
        inputStream.setInputString(
            """
            while (true) {
                input i_a;
                while (false) {
                    output i_a;
                }
                i_a=4;
            }
            """);
                                            
        String expected = 
            """
                . = 0x100
                JMP __START
            __START:
                MOV #0x1000, R6
            WHILE1:
                MOV #1, (R6)+ ;true
                MOV -(R6), R0
                BRZ WHILEEND1
                MOV #i_a, (R6)+ ;input i_a
                MOV #0xFFE0, R0
                MOV (R0), (R6)+
                MOV -(R6), R1
                MOV -(R6), R0
                MOV R1, (R0)
            WHILE2:
                MOV #0, (R6)+ ;false
                MOV -(R6), R0
                BRZ WHILEEND2
                MOV #0xFFE0, (R6)+ ;output i_a
                MOV #i_a, (R6)+
                MOV -(R6), R0
                MOV (R0), (R6)+
                MOV -(R6), R1
                MOV -(R6), R0
                MOV R1, (R0)
                JMP WHILE2
            WHILEEND2:
                MOV #i_a, (R6)+ ;i_a=4
                MOV #4, (R6)+
                MOV -(R6), R1
                MOV -(R6), R0
                MOV R1, (R0)
                JMP WHILE1
            WHILEEND1:
            HLT
            .END
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenWhileTestSingle1() throws FatalErrorException {
        inputStream.setInputString(
            """
            while(true) i_c=1;
            """);
                                            
        String expected = 
            """
                . = 0x100
                JMP __START
            __START:
                MOV #0x1000, R6
            WHILE1:
                MOV #1, (R6)+ ;true
                MOV -(R6), R0
                BRZ WHILEEND1
                MOV #i_c, (R6)+ ;i_c=1
                MOV #1, (R6)+
                MOV -(R6), R1
                MOV -(R6), R0
                MOV R1, (R0)
                JMP WHILE1
            WHILEEND1:
            HLT
            .END
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenWhileTestSingle2() throws FatalErrorException {
        inputStream.setInputString(
            """
            while(false) while(true) while(true) input i_a;
            """);
                                            
        String expected = 
            """
                . = 0x100
                JMP __START
            __START:
                MOV #0x1000, R6
            WHILE1:
                MOV #0, (R6)+ ;false
                MOV -(R6), R0
                BRZ WHILEEND1
            WHILE2:
                MOV #1, (R6)+ ;true
                MOV -(R6), R0
                BRZ WHILEEND2
            WHILE3:
                MOV #1, (R6)+ ;true
                MOV -(R6), R0
                BRZ WHILEEND3
                MOV #i_a, (R6)+ ;input i_a
                MOV #0xFFE0, R0
                MOV (R0), (R6)+
                MOV -(R6), R1
                MOV -(R6), R0
                MOV R1, (R0)
                JMP WHILE3
            WHILEEND3:
                JMP WHILE2
            WHILEEND2:
                JMP WHILE1
            WHILEEND1:
            HLT
            .END
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

}
