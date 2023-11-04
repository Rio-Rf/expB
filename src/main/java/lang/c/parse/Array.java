package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Array extends CParseRule {
	// unsignedFactor ::= LBRA expression RBRA
	CParseRule expression;

	public Array(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) { // [
		return tk.getType() == CToken.TK_LBRA;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_LBRA)
			pcx.fatalError(tk.toExplainString() + "不正なarrayです");
		tk = ct.getNextToken(pcx);
		if(!Expression.isFirst(tk))
			pcx.fatalError(tk.toExplainString() + "'['の後ろはexpressionです");
		expression = new Expression(pcx);
		expression.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_RBRA)
			pcx.fatalError(tk.toExplainString() + "expressionの後ろは']'です");
		tk = ct.getNextToken(pcx); // ]の次のトークンを読む この後tkを同じクラス内で使っていないので別クラスに何らかの影響がある
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(expression != null){ // 追加
			expression.semanticCheck(pcx);
			setCType(expression.getCType());
			setConstant(expression.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; array starts");
		if(expression != null){ // 追加
			expression.codeGen(pcx);
		}
		o.println(";;; array completes");
	}
}