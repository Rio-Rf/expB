package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class StatementInput extends CParseRule {
	// StatementInput ::= INPUT primary SEMI
	CToken op;
	CParseRule primary;

	public StatementInput(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INPUT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx); // *
		CToken tk = ct.getNextToken(pcx); // primary
		if(!Primary.isFirst(tk))
			pcx.fatalError(tk.toExplainString() + "'INPUT'の後ろはprimaryです");
		primary = new Primary(pcx);
		primary.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_SEMI)
			pcx.fatalError(tk.toExplainString() + "primaryの後ろは';'です");
		tk = ct.getNextToken(pcx); 
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
		}
		if(primary.isConstant()){
			pcx.fatalError("定数には書き込めません");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementInput starts");
		primary.codeGen(pcx); // アドレス値として利用する
		o.println("\tMOV\t#0xFFE0, R0\t; StatementInput:");
		o.println("\tMOV\t(R0), (R6)+\t; StatementInput:");
		o.println("\tMOV\t-(R6), R1\t; StatementInput: '='の右辺をポップ");
		o.println("\tMOV\t-(R6), R0\t; StatementInput: '='の左辺をポップ");
		o.println("\tMOV\tR1, (R0)\t; StatementInput: '='の右辺を左辺に代入");
		o.println(";;; StatementInput completes");
	}
}