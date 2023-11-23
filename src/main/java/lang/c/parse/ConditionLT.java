package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class ConditionLT extends CParseRule {
	// conditionLT ::= '<' expression
	CToken op;
	CParseRule left, right;

	public ConditionLT(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// <の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			right = new Expression(pcx); //TermクラスはCparseRuleクラスのextend
			right.parse(pcx); // Termのparseチェック
		} else {
			pcx.fatalError(tk.toExplainString() + "<の後ろはexpressionです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType()))
            pcx.fatalError("左辺の型 [" + left.getCType().toString()
                               + "] と右辺の型 [" + right.getCType().toString() 
                               + "] が一致しないので比較できません");
			this.setCType(CType.getCType(CType.T_bool));
			this.setConstant(true);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition < (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionLT: ２数を取り出して、比べる"); // R0=right
			o.println("\tMOV\t-(R6), R1\t; ConditionLT:");// R1=left
			o.println("\tMOV\t#0x0001, R2\t; ConditionLT: set true");
			o.println("\tCMP\tR0, R1\t; ConditionLT: R1<R0 = R1-R0<0");
			o.println("\tBRN\tLT" + seq + "\t; ConditionLT"); // R1-R0<0ならばtrue
			o.println("\tCLR\tR2\t\t; ConditionLT: set false");
			o.println("LT" + seq + ":\tMOV\tR2, (R6)+\t; ConditionLT:");
		}
		o.println(";;;condition < (compare) completes");
	}
}