package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class ConditionGE extends CParseRule {
	// conditionGE ::= '>=' expression
	CToken op;
	CParseRule left, right;

	public ConditionGE(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_GE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// >=の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ">=の後ろはexpressionです");
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
		o.println(";;; condition >= (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionGE: ２数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; ConditionGE:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionGE: set true");
			o.println("\tCMP\tR1, R0\t; ConditionGE: R1>=R0 = 0>=R0-R1");
			o.println("\tBRN\tGE" + seq + "\t; ConditionGE");
			o.println("\tBRZ\tGE" + seq + "\t; ConditionGE");
			o.println("\tCLR\tR2\t\t; ConditionGE: set false");
			o.println("GE" + seq + ":\tMOV\tR2, (R6)+\t; ConditionGE:");
		}
		o.println(";;;condition >= (compare) completes");
	}
}