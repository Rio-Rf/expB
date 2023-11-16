package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class PrimaryMult extends CParseRule {
	// PrimaryMult ::= '*' variable
	CToken op;
	CParseRule variable;

	public PrimaryMult(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx); // *
		CToken tk = ct.getNextToken(pcx); // variable
		if (Variable.isFirst(tk)) {//tkが数字かどうかを判別
			variable = new Variable(pcx);
			variable.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "*の後ろはvariableです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (variable != null) {
			variable.semanticCheck(pcx);
			if(variable.getCType().getType() == CType.T_int){ 
				pcx.fatalError(op.toExplainString() + "*の後はpint型です");
			}else{
				this.setCType(CType.getCType(CType.T_int)); // *ip_ABC, *ipa_ABC[123]とか 非定数
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if(variable != null){
			variable.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; PrimaryMult: アドレスを取り出して、内容を参照して、積む<" + op.toExplainString() + ">"); // 追加
			o.println("\tMOV\t(R0), (R6)+\t; PrimaryMult:");
		}
	}
}