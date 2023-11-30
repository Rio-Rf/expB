package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConditionBlock extends CParseRule {
	// conditionBlock ::= LPAR condition RPAR
	CParseRule condition;

	public ConditionBlock(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) { // [
		return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_LPAR)
			pcx.fatalError(tk.toExplainString() + "不正なconditionBlockです");
		tk = ct.getNextToken(pcx);
		if(!Condition.isFirst(tk))
			pcx.fatalError(tk.toExplainString() + "'('の後ろはconditionです");
		condition = new Condition(pcx);
		condition.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_RPAR)
			pcx.fatalError(tk.toExplainString() + "conditionの後ろは')'です");
		tk = ct.getNextToken(pcx); // ]の次のトークンを読む この後tkを同じクラス内で使っていないので別クラスに何らかの影響がある
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(condition != null){ // 追加
			condition.semanticCheck(pcx);
			setCType(condition.getCType());
			setConstant(condition.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; conditionBlock starts");
		if(condition != null){ // 追加
			condition.codeGen(pcx);
		}
		o.println(";;; conditionBlock completes");
	}
}