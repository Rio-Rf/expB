package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementAssign extends CParseRule {
	// statementAssign ::= primary ASSIGN expression SEMI
	CParseRule primary;
	CParseRule expression;

	public StatementAssign(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) { // primary
		return Primary.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// isFirst()実行済みなのでPrimaryはチェックしない
		primary = new Primary(pcx);
		primary.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_ASSIGN)
			pcx.fatalError(tk.toExplainString() + "primaryの後ろは'='です");
		tk = ct.getNextToken(pcx);
		if(!Expression.isFirst(tk))
			pcx.fatalError(tk.toExplainString() + "'='の後ろはexpressionです");
		expression = new Expression(pcx);
		expression.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_SEMI)
			pcx.fatalError(tk.toExplainString() + "expressionの後ろは';'です");
		tk = ct.getNextToken(pcx); // ]の次のトークンを読む この後tkを同じクラス内で使っていないので別クラスに何らかの影響がある
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(primary != null){ // 追加
			primary.semanticCheck(pcx);
			setCType(primary.getCType());
			setConstant(primary.isConstant());
		}
		if(expression != null){ // 追加
			expression.semanticCheck(pcx);
			setCType(expression.getCType());
			setConstant(expression.isConstant());
		}
		if(primary.isConstant()){
			pcx.fatalError("定数に値を代入することはできません");
		} else {
			if(primary.getCType() != expression.getCType())
				pcx.fatalError("左辺の型[" + primary.getCType().toString() + "]と右辺の型[" + expression.getCType().toString() + "]が一致しません");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementAssign starts");
		if(primary != null)// アドレス値として利用する
			primary.codeGen(pcx);
		if(expression != null)
			expression.codeGen(pcx);
		o.println("\tMOV\t-(R6), R1\t; StatementAssign: '='の右辺をポップ");
		o.println("\tMOV\t-(R6), R0\t; StatementAssign: '='の左辺をポップ");
		o.println("\tMOV\tR1, (R0)\t; StatementAssign: '='の右辺を左辺に代入");
		o.println(";;; StatementAssign completes");
	}
}