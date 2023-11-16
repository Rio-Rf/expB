package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Variable extends CParseRule {
	// variable ::= ident [ array ] // []は0回または1回の出現を意味する？
	CParseRule ident;
	CParseRule array;

	public Variable(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Ident.isFirst(tk)){ // 先頭がident
			ident = new Ident(pcx);
			ident.parse(pcx);
			tk = ct.getCurrentToken(pcx); //これで次のトークンを読めてるはず
			if(Array.isFirst(tk)){ // 先頭がarray
				array = new Array(pcx);
				array.parse(pcx);
			}
		}else{
			pcx.fatalError(tk.toExplainString() + "不正なvariableです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			ident.semanticCheck(pcx);
			if(array != null){
				array.semanticCheck(pcx);
				if(ident.getCType() == CType.getCType(CType.T_int_array)) // ia_hoge[123]とか 非定数
					this.setCType(CType.getCType(CType.T_int));
				else if(ident.getCType() == CType.getCType(CType.T_pint_array)) // ipa_hoge[123]とか 非定数
					this.setCType(CType.getCType(CType.T_pint));
				else if(ident.getCType() == CType.getCType(CType.T_int))
					pcx.fatalError("i_は配列の変数名として不適です");
				else if(ident.getCType() == CType.getCType(CType.T_pint))
					pcx.fatalError("ip_は配列の変数名として不適です");
				else
					pcx.fatalError(ident.getCType().toString() + "は配列ではありません");
			}else{
				if(ident.getCType() == CType.getCType(CType.T_int_array))
					pcx.fatalError("ia_は整数の変数名として不適です");
				else if(ident.getCType() == CType.getCType(CType.T_pint_array))
					pcx.fatalError("ipa_は整数の変数名として不適です");
				else
					this.setCType(ident.getCType());
					setConstant(ident.isConstant());
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (ident != null) {
			ident.codeGen(pcx);
			if(array != null){
				array.codeGen(pcx); // [expression]のexpressionがスタックトップに
				o.println("\tMOV\t-(R6), R0\t; Variable: 配列の添字をポップ");
				o.println("\tMOV\t-(R6), R1\t; Variable: 変数アドレスをポップ");
				o.println("\tADD\tR1, R0\t; Variable: 添字を加算して、配列要素のアドレスを求める");
				o.println("\tMOV\tR0, (R6)+\t; Variable:");
			}
		}
	}
}
