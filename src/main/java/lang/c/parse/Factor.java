package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Factor extends CParseRule {
	// factor ::= plusFactor | minusFactor | unsignedFactor
	CParseRule plusFactor;
	CParseRule minusFactor;
	CParseRule unsignedFactor;

	public Factor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		if(PlusFactor.isFirst(tk)){
			return PlusFactor.isFirst(tk);
		}else if(MinusFactor.isFirst(tk)){
			return MinusFactor.isFirst(tk);
		}else if(UnsignedFactor.isFirst(tk)){
			return UnsignedFactor.isFirst(tk);
		}else{
			return false;
		}
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(PlusFactor.isFirst(tk)){ // 先頭が+
			plusFactor = new PlusFactor(pcx);
			plusFactor.parse(pcx);
		}else if(MinusFactor.isFirst(tk)){ // 先頭が-
			minusFactor = new MinusFactor(pcx); // ここで新しく宣言してる
			minusFactor.parse(pcx);  
		}else if(UnsignedFactor.isFirst(tk)){ // 先頭がunsigned
			unsignedFactor = new UnsignedFactor(pcx); // ここで新しく宣言してる
			unsignedFactor.parse(pcx); 
		}else{
			pcx.fatalError(tk.toExplainString() + "不正なfactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (plusFactor != null) {
			plusFactor.semanticCheck(pcx);
			setCType(plusFactor.getCType()); // plusFactor の型をそのままコピー
			setConstant(plusFactor.isConstant()); // plusFactor は常に定数
		} else if(minusFactor != null){ // 追加
			minusFactor.semanticCheck(pcx);
			setCType(minusFactor.getCType());
			setConstant(minusFactor.isConstant());
		} else if(unsignedFactor != null){ // 追加
			unsignedFactor.semanticCheck(pcx);
			setCType(unsignedFactor.getCType());
			setConstant(unsignedFactor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (plusFactor != null) {
			plusFactor.codeGen(pcx);
		} else if(minusFactor != null){ // 追加
			minusFactor.codeGen(pcx);
		} else if(unsignedFactor != null){ // 追加
			unsignedFactor.codeGen(pcx);
		}
		o.println(";;; factor completes");
	}
}