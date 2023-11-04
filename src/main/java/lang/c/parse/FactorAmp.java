package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class FactorAmp extends CParseRule {
	// FactorAmp ::= '&' ( number | primary )
	CToken op;
	CParseRule number;
	CParseRule primary;

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
			number = new Number(pcx);
			number.parse(pcx);
		} else if(Primary.isFirst(tk)){
			if(PrimaryMult.isFirst(tk)){
				pcx.fatalError(tk.toExplainString() + "&の後ろにPrimaryMultは不適です");
			}else{
				primary = new Primary(pcx);
				primary.parse(pcx);
			}
		} else {
			pcx.fatalError(tk.toExplainString() + "&の後ろはnumberまたはprimaryです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (number != null) {
			number.semanticCheck(pcx);
			int rt = number.getCType().getType(); // &の右辺の型
			if(rt == CType.T_int){ // rightが整数ならばポインタ型へと変える
				this.setCType(CType.getCType(CType.T_pint)); // $100のテストが通ったのでここは問題無い
				this.setConstant(number.isConstant()); // &の右が定数のときだけ定数 // 定数ってのは問題ないって意味か？
			}else{
				pcx.fatalError(op.toExplainString() + "右辺の型[" + number.getCType().toString() + "]は&の右辺として不適です");
			}	
		}else if(primary != null){
			primary.semanticCheck(pcx);
			int rt = primary.getCType().getType(); // &の右辺の型
			if(rt == CType.T_int){ // rightが整数ならばポインタ型へと変える
				this.setCType(CType.getCType(CType.T_pint));
				this.setConstant(primary.isConstant());
			}else{
				pcx.fatalError(op.toExplainString() + "右辺の型[" + primary.getCType().toString() + "]は&の右辺として不適です");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if(number != null){
			number.codeGen(pcx);
		}else if(primary != null){
			primary.codeGen(pcx);
		}
	}
}