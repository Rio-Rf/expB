package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementWhile extends CParseRule {
	// statementWhile ::= WHILE conditionBlock statement
	CParseRule conditionBlock;
	CParseRule statement;

	public StatementWhile(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) { // [
		return tk.getType() == CToken.TK_WHILE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		// 終端記号で終わるものは最後にgetNextTokenを呼ぶ
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_WHILE)
			pcx.fatalError(tk.toExplainString() + "不正なstatementWhileです");
		tk = ct.getNextToken(pcx);
		if(!ConditionBlock.isFirst(tk))
			pcx.fatalError(tk.toExplainString() + "'While'の後ろはconditionBlockです");
		conditionBlock = new ConditionBlock(pcx);
		conditionBlock.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		if(!Statement.isFirst(tk))
			pcx.fatalError(tk.toExplainString() + "conditionBlockの後ろはstatementです");
		statement = new Statement(pcx);
		statement.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(conditionBlock != null){ // 追加
			conditionBlock.semanticCheck(pcx);
		} else if (statement != null){
			statement.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementWhile starts");
		int seq = pcx.getSeqId();
		o.println("WHILE" + seq + ":\t\t\t\t; StatementWhile:");
		conditionBlock.codeGen(pcx);
		o.println("\tMOV\t-(R6), R0\t; StatementWhile:");
		o.println("\tBRZ\tWHILEEND" + seq + "\t; StatementWhile:");
		statement.codeGen(pcx);
		o.println("\tJMP\tWHILE" + seq + "\t\t; StatementWhile:");
		o.println("WHILEEND" + seq + ":\t\t\t\t; StatementWhile:");
		o.println(";;; statementWhile completes");
	}
}