package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Statement extends CParseRule {
	// statement ::= statementAssign | statementInput | statementOutput |
	//               statementIf | statementWhile | statementBlock
	CParseRule statementAssign;
	CParseRule statementInput;
	CParseRule statementOutput;
	CParseRule statementIf;
	CParseRule statementWhile;
	CParseRule statementBlock;

	public Statement(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		if(StatementAssign.isFirst(tk)){
			return StatementAssign.isFirst(tk);
		}else if(StatementInput.isFirst(tk)){
			return StatementInput.isFirst(tk);
		}else if(StatementOutput.isFirst(tk)){
			return StatementOutput.isFirst(tk);
		}else if(StatementIf.isFirst(tk)){
			return StatementIf.isFirst(tk);
		}else if(StatementWhile.isFirst(tk)){
			return StatementWhile.isFirst(tk);
		}else if(StatementBlock.isFirst(tk)){
			return StatementBlock.isFirst(tk);
		}else{
			return false;
		}
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (StatementAssign.isFirst(tk)) {
			statementAssign = new StatementAssign(pcx);
			statementAssign.parse(pcx);
		} else if (StatementInput.isFirst(tk)) {
			statementInput = new StatementInput(pcx);
			statementInput.parse(pcx);
		} else if (StatementOutput.isFirst(tk)) {
			statementOutput = new StatementOutput(pcx);
			statementOutput.parse(pcx);
		} else if (StatementIf.isFirst(tk)) {
			statementIf = new StatementIf(pcx);
			statementIf.parse(pcx);
		} else if (StatementWhile.isFirst(tk)) {
			statementWhile = new StatementWhile(pcx);
			statementWhile.parse(pcx);
		} else if (StatementBlock.isFirst(tk)) {
			statementBlock = new StatementBlock(pcx);
			statementBlock.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "不正なstatementです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statementAssign != null) {
			statementAssign.semanticCheck(pcx);
			this.setCType(statementAssign.getCType()); 
			this.setConstant(statementAssign.isConstant());
		} else if (statementInput != null) {
			statementInput.semanticCheck(pcx);
		} else if (statementOutput != null) {
			statementOutput.semanticCheck(pcx);
		} else if (statementIf != null) {
			statementIf.semanticCheck(pcx);
		} else if (statementWhile != null) {
			statementWhile.semanticCheck(pcx);
		} else if (statementBlock != null) {
			statementBlock.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (statementAssign != null){
			statementAssign.codeGen(pcx);
		}else if (statementInput != null){
			statementInput.codeGen(pcx);
		}else if (statementOutput != null){
			statementOutput.codeGen(pcx);
		}else if (statementIf != null){
			statementIf.codeGen(pcx);
		}else if (statementWhile != null){
			statementWhile.codeGen(pcx);
		}else if (statementBlock != null){
			statementBlock.codeGen(pcx);
		}
	}
}
