package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Factor extends CParseRule {
	// factor ::= factorAmp | number
	CParseRule number;
	CParseRule factorAmp;

	public Factor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) { // & or number
		if(FactorAmp.isFirst(tk)){
			return FactorAmp.isFirst(tk);
		}else{
			return Number.isFirst(tk);
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
		}else{ //16bitを超えてnumberとして認識されなかった場合など
			pcx.fatalError(tk.toExplainString() + "不正なfactorです");
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
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (number != null) {
			number.codeGen(pcx);
		} else if(factorAmp != null){ // 追加
			factorAmp.codeGen(pcx);
		}
		o.println(";;; factor completes");
	}
}