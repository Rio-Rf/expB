package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementIf extends CParseRule {
	// statementIf ::= IF conditionBlock statement [ ELSE statement ]
	CParseRule conditionBlock;
	CParseRule statement1;
	CParseRule statement2;

	public StatementIf(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) { // [
		return tk.getType() == CToken.TK_IF;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		// 終端記号で終わるものは最後にgetNextTokenを呼ぶ
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_IF)
			pcx.fatalError(tk.toExplainString() + "不正なstatementIFです");
		tk = ct.getNextToken(pcx);
		if(!ConditionBlock.isFirst(tk))
			pcx.fatalError(tk.toExplainString() + "'IF'の後ろはconditionBlockです");
		conditionBlock = new ConditionBlock(pcx);
		conditionBlock.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		if(!Statement.isFirst(tk))
			pcx.fatalError(tk.toExplainString() + "conditionBlockの後ろはstatementです");
		statement1 = new Statement(pcx);
		statement1.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		/*if(tk.getType() == CToken.TK_ELSE)
			tk = ct.getNextToken(pcx);
			if(!Statement.isFirst(tk))
				pcx.fatalError(tk.toExplainString() + "'ELSE'の後ろはstatementです");
			statement2 = new Statement(pcx);
			statement2.parse(pcx);*/ // この書き方だとTK_ELSEのif文が機能しなかったorz
		if(tk.getType() == CToken.TK_ELSE){
			tk = ct.getNextToken(pcx);
			if(!Statement.isFirst(tk))
				pcx.fatalError(tk.toExplainString() + "'ELSE'の後ろはstatementです");
			statement2 = new Statement(pcx);
			statement2.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(conditionBlock != null){ // 追加
			conditionBlock.semanticCheck(pcx);
		} else if (statement1 != null){
			statement1.semanticCheck(pcx);
		} else if (statement2 != null){
			statement2.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementIf starts");
		conditionBlock.codeGen(pcx);
		int seq = pcx.getSeqId();
		o.println("\tMOV\t-(R6), R0\t; StatementIf:");
		if(statement2 != null){
			o.println("\tBRZ\tIFELSE" + seq + "\t; StatementIf:");
		} else {
			o.println("\tBRZ\tIFEND" + seq + "\t; StatementIf:");
		}
		statement1.codeGen(pcx);
		if(statement2 != null){
			o.println("\tJMP\tIFEND" + seq + "\t; StatementIf:");
			o.println("IFELSE" + seq + ":\t\t\t\t; StatementIf:");
			statement2.codeGen(pcx);
		}
		o.println("IFEND" + seq + ":\t\t\t\t; StatementIf:");
		o.println(";;; statementIf completes");
	}
}