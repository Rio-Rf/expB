package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class Ident extends CParseRule {
	// FactorAmp ::= 'IDENT'
	CToken ident;

	public Ident(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ident = tk;
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			if(ident.getText().startsWith("i_")) {
				this.setCType(CType.getCType(CType.T_int));
			} else if(ident.getText().startsWith("ip_")) {
				this.setCType(CType.getCType(CType.T_pint));
			} else if(ident.getText().startsWith("ia_")) {
				this.setCType(CType.getCType(CType.T_int_array));
			} else if(ident.getText().startsWith("ipa_")) {
				this.setCType(CType.getCType(CType.T_pint_array));
			} else if(ident.getText().startsWith("c_")) {
				this.setCType(CType.getCType(CType.T_int));
				this.setConstant(true);
			} else {
				this.setCType(CType.getCType(CType.T_err));
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; ident starts");
		if (ident != null) {
			o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<" + ident.toExplainString() + ">");
		}
		o.println(";;; ident completes");
	}
}