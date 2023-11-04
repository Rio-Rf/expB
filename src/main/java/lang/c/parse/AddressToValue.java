package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class AddressToValue extends CParseRule {
	// addressToValue ::= primary
	CParseRule primary;

	public AddressToValue(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Primary.isFirst(tk)){ // 先頭がPrimary
			primary = new Primary(pcx);
			primary.parse(pcx);
		}else{
			pcx.fatalError(tk.toExplainString() + "不正なaddressToValueです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
			this.setCType(primary.getCType()); 
			this.setConstant(primary.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (primary != null) {
			primary.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; AddressToValue: 変数アドレスをポップ");
			o.println("\tMOV\t(R0), (R6)+\t; AddressToValue: アドレスから値を取り出して、積む");
		}
	}
}
