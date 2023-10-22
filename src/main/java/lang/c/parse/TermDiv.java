package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class TermDiv extends CParseRule {
	// TermMult ::= '/' factor
	CToken op;
	CParseRule left, right;

	public TermDiv(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_DIV; //マイナスに変更した
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// /の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {//tkが数字かどうかを判別
			right = new Factor(pcx);
			right.parse(pcx); // Factorのparseチェック
		} else {
			pcx.fatalError(tk.toExplainString() + "/の後ろはfactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 割り算の型計算規則
		final int s[][] = {
				// T_err T_int T_pint
				{ CType.T_err, CType.T_err, CType.T_err }, // T_err
				{ CType.T_err, CType.T_int, CType.T_err }, // T_int
				{ CType.T_err, CType.T_err, CType.T_err}, // T_pint
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType(); // /の左辺の型
			int rt = right.getCType().getType(); // /の右辺の型
			int nt = s[lt][rt]; // 規則による型計算
			if (nt == CType.T_err) {
				pcx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]から右辺の型["
						+ right.getCType().toString() + "]は割れません");
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant()); // /の左右両方が定数のときだけ定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			left.codeGen(pcx); // 左部分木のコード生成を頼む
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			o.println("\tJSR\tDIV\t; TermDiv: ２数を取り出して、割り、R1で返す<" + op.toExplainString() + ">");
			o.println("\tSUB\t#2, R6\t; TermDiv:");
			o.println("\tMOV\tR1, (R6)+\t; TermDiv:");
		}
	}
}