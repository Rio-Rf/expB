package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class Condition extends CParseRule {
	// Condition ::= TRUE | FALSE
                // | expression ( conditionLT | conditionLE | conditionGT
				// | conditionGE | conditionEQ | conditionNE )
	CToken op;
	CParseRule expression;
	CParseRule conditionLT;
	CParseRule conditionLE;
	CParseRule conditionGT;
	CParseRule conditionGE;
	CParseRule conditionEQ;
	CParseRule conditionNE;
	boolean flagT;
	boolean flagF;

	public Condition(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		if(tk.getType() == CToken.TK_TRUE){
			return tk.getType() == CToken.TK_TRUE;
		}else if(tk.getType() == CToken.TK_FALSE){
			return tk.getType() == CToken.TK_FALSE;
		}else if(Expression.isFirst(tk)){
			return Expression.isFirst(tk);
		}else{
			return false;
		}
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx); // TRUE or FALSE or expression
		if(op.getType() == CToken.TK_TRUE){
			flagT = true;
		}else if(op.getType() == CToken.TK_FALSE){
			flagF = true;
		}
		if(Expression.isFirst(op)){
			expression = new Expression(pcx);
			expression.parse(pcx);
			CToken tk = ct.getCurrentToken(pcx);
			if (ConditionLT.isFirst(tk)) {
				conditionLT = new ConditionLT(pcx, expression);
				conditionLT.parse(pcx);
			} else if (ConditionLE.isFirst(tk)) {
				conditionLE = new ConditionLE(pcx, expression);
				conditionLE.parse(pcx);
			} else if (ConditionGT.isFirst(tk)) {
				conditionGT = new ConditionGT(pcx, expression);
				conditionGT.parse(pcx);
			} else if (ConditionGE.isFirst(tk)) {
				conditionGE = new ConditionGE(pcx, expression);
				conditionGE.parse(pcx);
			} else if (ConditionEQ.isFirst(tk)) {
				conditionEQ = new ConditionEQ(pcx, expression);
				conditionEQ.parse(pcx);
			} else if (ConditionNE.isFirst(tk)) {
				conditionNE = new ConditionNE(pcx, expression);
				conditionNE.parse(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "expressionの後ろはconditionXXです");
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
			if(conditionLT != null){
				conditionLT.semanticCheck(pcx);
				this.setCType(conditionLT.getCType());
				this.setConstant(conditionLT.isConstant());
			} else if(conditionLE != null){
				conditionLE.semanticCheck(pcx);
				this.setCType(conditionLE.getCType());
				this.setConstant(conditionLE.isConstant());
			} else if(conditionGT != null){
				conditionGT.semanticCheck(pcx);
				this.setCType(conditionGT.getCType());
				this.setConstant(conditionGT.isConstant());
			} else if(conditionGE != null){
				conditionGE.semanticCheck(pcx);
				this.setCType(conditionGE.getCType());
				this.setConstant(conditionGE.isConstant());
			} else if(conditionEQ != null){
				conditionEQ.semanticCheck(pcx);
				this.setCType(conditionEQ.getCType());
				this.setConstant(conditionEQ.isConstant());
			} else if(conditionNE != null){
				conditionNE.semanticCheck(pcx);
				this.setCType(conditionNE.getCType());
				this.setConstant(conditionNE.isConstant());
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if(flagT){
			flagT = false;
			o.println("\tMOV\t#1, (R6)+\t; Condition: true");
		} else if (flagF){
			flagF = false;
			o.println("\tMOV\t#0, (R6)+\t; Condition: false");
		} else if(conditionLT != null){
			conditionLT.codeGen(pcx);
		} else if(conditionLE != null){
			conditionLE.codeGen(pcx);
		} else if(conditionGT != null){
			conditionGT.codeGen(pcx);
		} else if(conditionGE != null){
			conditionGE.codeGen(pcx);
		} else if(conditionEQ != null){
			conditionEQ.codeGen(pcx);
		} else if(conditionNE != null){
			conditionNE.codeGen(pcx);
		}
	}
}