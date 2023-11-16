package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Statement extends CParseRule {
	// statement ::= statementAssign
	CParseRule statementAssign;

	public Statement(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return StatementAssign.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(StatementAssign.isFirst(tk)){ // 先頭がPrimary
			statementAssign = new StatementAssign(pcx);
			statementAssign.parse(pcx);
		}else{
			pcx.fatalError(tk.toExplainString() + "不正なstatementです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statementAssign != null) {
			statementAssign.semanticCheck(pcx);
			this.setCType(statementAssign.getCType()); 
			this.setConstant(statementAssign.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (statementAssign != null)
			statementAssign.codeGen(pcx);
	}
}
