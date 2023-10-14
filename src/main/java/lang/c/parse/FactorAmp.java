package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class FactorAmp extends CParseRule {
	// FactorAmp ::= '&' term
	CToken op;
	CParseRule right;

	public FactorAmp(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx); // &
		CToken tk = ct.getNextToken(pcx); // number
		if (Number.isFirst(tk)) {//tkが数字かどうかを判別
			right = new Number(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "&の後ろはnumberです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (right != null) {
			right.semanticCheck(pcx);
			int rt = right.getCType().getType(); // &の右辺の型
			if(rt == CType.T_int){ // rightが整数ならばポインタ型へと変える
				this.setCType(CType.getCType(CType.T_pint)); // $100のテストが通ったのでここは問題無い
				this.setConstant(right.isConstant()); // &の右が定数のときだけ定数 // 定数ってのは問題ないって意味か？
			}else{
				pcx.fatalError(op.toExplainString() + "右辺の型[" + right.getCType().toString() + "]は&の右辺として不適です");
			}	
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if(right != null){
			right.codeGen(pcx);
		}
	}
}