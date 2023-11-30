package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class StatementOutput extends CParseRule {
	// StatementOutput ::= OUTPUT expression SEMI
	CToken op;
	CParseRule expression;

	public StatementOutput(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_OUTPUT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx); // OUTPUT
		CToken tk = ct.getNextToken(pcx); // expression
		if(!Expression.isFirst(tk))
			pcx.fatalError(tk.toExplainString() + "'OUTPUT'の後ろはexpressionです");
		expression = new Expression(pcx);
		expression.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_SEMI)
			pcx.fatalError(tk.toExplainString() + "expressionの後ろは';'です");
		tk = ct.getNextToken(pcx); 
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementOutput starts");
		o.println("\tMOV\t#0xFFE0, (R6)+\t; StatementOutput:"); // アドレス値として利用する
		expression.codeGen(pcx);
		o.println("\tMOV\t-(R6), R1\t; StatementOutput: '='の右辺をポップ");
		o.println("\tMOV\t-(R6), R0\t; StatementOutput: '='の左辺をポップ");
		o.println("\tMOV\tR1, (R0)\t; StatementOutput: '='の右辺を左辺に代入");
		o.println(";;; StatementOutput completes");
	}
}