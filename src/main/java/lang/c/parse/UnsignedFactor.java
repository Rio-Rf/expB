package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class UnsignedFactor extends CParseRule {
	// unsignedFactor ::= factorAmp | number | LPAR expression RPAR
	CParseRule number;
	CParseRule factorAmp;
	CParseRule expression;

	public UnsignedFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) { // & or number or (
		if(FactorAmp.isFirst(tk)){
			return FactorAmp.isFirst(tk);
		}else if(Number.isFirst(tk)){
			return Number.isFirst(tk);
		}else{
			return tk.getType() == CToken.TK_LPAR;
		}
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Number.isFirst(tk)){ //先頭がnumber
			number = new Number(pcx);
			number.parse(pcx);
		}else if(FactorAmp.isFirst(tk)){ // 先頭が&
			factorAmp = new FactorAmp(pcx); // ここで新しく宣言してる
			factorAmp.parse(pcx); // factorAmpのparseチェック
		}else{
			if(tk.getType() != CToken.TK_LPAR)
				pcx.fatalError(tk.toExplainString() + "不正なunsignedFactorです");
			tk = ct.getNextToken(pcx);
			if(!Expression.isFirst(tk))
				pcx.fatalError(tk.toExplainString() + "'('の後ろはexpressionです");
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() != CToken.TK_RPAR)
				pcx.fatalError(tk.toExplainString() + "expressionの後ろは')'です");
			tk = ct.getNextToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (number != null) {
			number.semanticCheck(pcx);
			setCType(number.getCType()); // number の型をそのままコピー
			setConstant(number.isConstant()); // number は常に定数
		} else if(factorAmp != null){ // 追加
			factorAmp.semanticCheck(pcx);
			setCType(factorAmp.getCType());
			setConstant(factorAmp.isConstant());
		} else if(expression != null){ // 追加
			expression.semanticCheck(pcx);
			setCType(expression.getCType());
			setConstant(expression.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; unsignedFactor starts");
		if (number != null) {
			number.codeGen(pcx);
		} else if(factorAmp != null){ // 追加
			factorAmp.codeGen(pcx);
		} else if(expression != null){ // 追加
			expression.codeGen(pcx);
		}
		o.println(";;; unsignedFactor completes");
	}
}