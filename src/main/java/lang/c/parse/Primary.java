package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Primary extends CParseRule {
	// primary ::= primaryMult | variable
	CParseRule primaryMult;
	CParseRule variable;

	public Primary(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		if(PrimaryMult.isFirst(tk)){
			return PrimaryMult.isFirst(tk);
		}else if(Variable.isFirst(tk)){
			return Variable.isFirst(tk);
		}else{
			return false;
		}
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(PrimaryMult.isFirst(tk)){ // 先頭がPrimaryMult
			primaryMult = new PrimaryMult(pcx);
			primaryMult.parse(pcx);
		}else if(Variable.isFirst(tk)){ // 先頭がvariable
			variable = new Variable(pcx); // ここで新しく宣言してる
			variable.parse(pcx);  
		}else{
			pcx.fatalError(tk.toExplainString() + "不正なprimaryです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primaryMult != null) {
			primaryMult.semanticCheck(pcx);
			setCType(primaryMult.getCType()); // primaryMult の型をそのままコピー
		} else if(variable != null){
			variable.semanticCheck(pcx);
			setCType(variable.getCType());
			setConstant(variable.isConstant()); // ここは微妙
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primary starts");
		if (primaryMult != null) {
			primaryMult.codeGen(pcx);
		} else if(variable != null){ // 追加
			variable.codeGen(pcx);
		}
		o.println(";;; primary completes");
	}
}