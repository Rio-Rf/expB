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

public class CodeGenIfTest {
    
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
    public void codeGenIfTest2() throws FatalErrorException {
        inputStream.setInputString(
            """
            if (false) {
                i_a=3;
            }
            """);
                                            
        String expected = 
            """
               MOV #0,(R6)+
               MOV -(R6),R0
               BRZ IFEND1
               MOV #i_a,(R6)+
               MOV #3,(R6)+
               MOV -(R6),R1
               MOV -(R6),R0
               MOV R1,(R0)
            IFEND1:
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new StatementIf(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // Please copy and paste the above code and add the specified test case to the following

    @Test
    public void codeGenIfTestElse() throws FatalErrorException {
        inputStream.setInputString(
            """
            if (true) {
                i_a=1;
            } else {
                i_a=2;
            }
            """);
                                            
        String expected = 
            """
               MOV #1,(R6)+ ;true
               MOV -(R6),R0
               BRZ IFELSE1
               MOV #i_a,(R6)+ ;i_a=1
               MOV #1,(R6)+
               MOV -(R6),R1
               MOV -(R6),R0
               MOV R1,(R0)
               JMP IFEND1
            IFELSE1:
                MOV #i_a,(R6)+ ;i_a=2
                MOV #2,(R6)+
                MOV -(R6),R1
                MOV -(R6),R0
                MOV R1,(R0)
            IFEND1:
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new StatementIf(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenIfTestElseIfElse() throws FatalErrorException {
        inputStream.setInputString(
            """
            if (i_a == 3) {
                i_a=0;
            } else if (i_a == 4){
                i_a=1;
            } else {
                i_a=2;
            }
            """);
                                            
        String expected = 
            """
                MOV #i_a, (R6)+ ;i_a==3
                MOV -(R6), R0
                MOV (R0), (R6)+
                MOV #3, (R6)+
                MOV -(R6), R0
                MOV -(R6), R1
                MOV #0x0001, R2
                CMP R0, R1
                BRZ EQ1
                CLR R2
            EQ1: MOV R2, (R6)+
                MOV -(R6),R0
                BRZ IFELSE2
                MOV #i_a,(R6)+ ;i_a=0
                MOV #0,(R6)+
                MOV -(R6),R1
                MOV -(R6),R0
                MOV R1,(R0)
                JMP IFEND2
            IFELSE2:
                MOV #i_a, (R6)+ ;i_a==4
                MOV -(R6), R0
                MOV (R0), (R6)+
                MOV #4, (R6)+
                MOV -(R6), R0
                MOV -(R6), R1
                MOV #0x0001, R2
                CMP R0, R1
                BRZ EQ3
                CLR R2
            EQ3: MOV R2, (R6)+
                MOV -(R6),R0
                BRZ IFELSE4
                MOV #i_a,(R6)+ ;i_a=1
                MOV #1,(R6)+
                MOV -(R6),R1
                MOV -(R6),R0
                MOV R1,(R0)
                JMP IFEND4
            IFELSE4:
                MOV #i_a,(R6)+ ;i_a=2
                MOV #2,(R6)+
                MOV -(R6),R1
                MOV -(R6),R0
                MOV R1,(R0)
            IFEND4:
            IFEND2:
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new StatementIf(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenIfTestElseIfElseIfElse() throws FatalErrorException {
        inputStream.setInputString(
            """
            i_a = 54;
            if (i_a == 3) {
                i_a=0;
            } else if (i_a == 4){
                i_a=1;
            } else if (i_a ==54){
                i_a=2;
            } else {
                i_a=3;
            }
            """);
                                            
        String expected = 
            """
                . = 0x100
                JMP __START
            __START:
                MOV #0x1000, R6
                MOV	#i_a, (R6)+ ;i_a=54
                MOV	#54, (R6)+
                MOV	-(R6), R1
                MOV	-(R6), R0
                MOV	R1, (R0)
                MOV #i_a, (R6)+ ;i_a==3
                MOV -(R6), R0
                MOV (R0), (R6)+
                MOV #3, (R6)+
                MOV -(R6), R0
                MOV -(R6), R1
                MOV #0x0001, R2
                CMP R0, R1
                BRZ EQ1
                CLR R2
            EQ1: MOV R2, (R6)+
                MOV -(R6),R0
                BRZ IFELSE2
                MOV #i_a,(R6)+ ;i_a=0
                MOV #0,(R6)+
                MOV -(R6),R1
                MOV -(R6),R0
                MOV R1,(R0)
                JMP IFEND2
            IFELSE2:
                MOV #i_a, (R6)+ ;i_a==4
                MOV -(R6), R0
                MOV (R0), (R6)+
                MOV #4, (R6)+
                MOV -(R6), R0
                MOV -(R6), R1
                MOV #0x0001, R2
                CMP R0, R1
                BRZ EQ3
                CLR R2
            EQ3: MOV R2, (R6)+
                MOV -(R6),R0
                BRZ IFELSE4
                MOV #i_a,(R6)+ ;i_a=1
                MOV #1,(R6)+
                MOV -(R6),R1
                MOV -(R6),R0
                MOV R1,(R0)
                JMP IFEND4
            IFELSE4:
                MOV #i_a, (R6)+ ;i_a==54
                MOV -(R6), R0
                MOV (R0), (R6)+
                MOV #54, (R6)+
                MOV -(R6), R0
                MOV -(R6), R1
                MOV #0x0001, R2
                CMP R0, R1
                BRZ EQ5
                CLR R2
            EQ5: MOV R2, (R6)+
                MOV -(R6),R0
                BRZ IFELSE6
                MOV #i_a,(R6)+ ;i_a=2
                MOV #2,(R6)+
                MOV -(R6),R1
                MOV -(R6),R0
                MOV R1,(R0)
                JMP IFEND6
            IFELSE6:
                MOV #i_a,(R6)+ ;i_a=3
                MOV #3,(R6)+
                MOV -(R6),R1
                MOV -(R6),R0
                MOV R1,(R0)
            IFEND6:
            IFEND4:
            IFEND2:
            HLT
            .END
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext); // StatementAssignに変更
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenIfTestSingle1() throws FatalErrorException {
        inputStream.setInputString(
            """
            if (true) i_a=1;
            else i_b=1;
            """);
                                            
        String expected = 
            """
               MOV #1,(R6)+ ;true
               MOV -(R6),R0
               BRZ IFELSE1
               MOV #i_a,(R6)+
               MOV #1,(R6)+
               MOV -(R6),R1
               MOV -(R6),R0
               MOV R1,(R0)
               JMP IFEND1
            IFELSE1:
                MOV #i_b,(R6)+
                MOV #1,(R6)+
                MOV -(R6),R1
                MOV -(R6),R0
                MOV R1,(R0)
            IFEND1:
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new StatementIf(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenIfTestSingle2() throws FatalErrorException {
        inputStream.setInputString(
            """
            if (true) if (false) if (true) i_a=1; 
            """);
                                            
        String expected = 
            """
                MOV #1,(R6)+ ;true
                MOV -(R6),R0
                BRZ IFEND1
                MOV #0,(R6)+ ;false
                MOV -(R6),R0
                BRZ IFEND2
                MOV #1,(R6)+ ;true
                MOV -(R6),R0
                BRZ IFEND3
                MOV #i_a,(R6)+
                MOV #1,(R6)+
                MOV -(R6),R1
                MOV -(R6),R0
                MOV R1,(R0)
            IFEND3:
            IFEND2:
            IFEND1:
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new StatementIf(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

}
