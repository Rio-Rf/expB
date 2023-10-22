package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class MinusFactor extends CParseRule {
	// Minusfactor ::= - unsignedFactor
	CToken op;
	CParseRule unsignedFactor;

	public MinusFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx); // -
		// -の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if(UnsignedFactor.isFirst(tk)){ //先頭がunsignedFactor
			unsignedFactor = new UnsignedFactor(pcx);
			unsignedFactor.parse(pcx);
		}else{
			pcx.fatalError(tk.toExplainString() + "不正なminusFactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (unsignedFactor != null) {
			unsignedFactor.semanticCheck(pcx);
			int uf = unsignedFactor.getCType().getType(); // &の右辺の型
			if(uf != CType.T_pint){
				setCType(unsignedFactor.getCType()); // unsignedFactor の型をそのままコピー
				setConstant(unsignedFactor.isConstant()); // unsignedFactor は常に定数
			}else{
				pcx.fatalError(op.toExplainString() + "不正なFactorAmpです");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream(); 
		if(unsignedFactor != null){
			unsignedFactor.codeGen(pcx); // ExpressionAddでいうところのright.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; MinusFactor: 1数を取り出して、負にする<" + op.toExplainString() + ">");
			o.println("\tMOV\t#0, R1\t; MinusFactor:");
			o.println("\tSUB\tR0, R1\t; MinusFactor:");
			o.println("\tMOV\tR1, (R6)+\t; MinusFactor:");			
		}
	}
}