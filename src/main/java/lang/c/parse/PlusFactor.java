package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class PlusFactor extends CParseRule {
	// Plusfactor ::= + unsignedFactor
	CToken op;
	CParseRule unsignedFactor;

	public PlusFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_PLUS;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx); // +
		// +の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if(UnsignedFactor.isFirst(tk)){ //先頭がunsignedFactor
			unsignedFactor = new UnsignedFactor(pcx);
			unsignedFactor.parse(pcx);
		}else{
			pcx.fatalError(tk.toExplainString() + "不正なplusFactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (unsignedFactor != null) {
			unsignedFactor.semanticCheck(pcx);
			setCType(unsignedFactor.getCType()); // unsignedFactor の型をそのままコピー
			setConstant(unsignedFactor.isConstant()); // unsignedFactor は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if(unsignedFactor != null){
			unsignedFactor.codeGen(pcx);
		}
	}
}