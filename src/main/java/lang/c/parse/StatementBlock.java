package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

import java.util.ArrayList;

public class StatementBlock extends CParseRule {
	// statementBlock ::= LCUR { statement } RCUR
	ArrayList<CParseRule> list = new ArrayList<>();

	public StatementBlock(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LCUR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_LCUR)
			pcx.fatalError(tk.toExplainString() + "不正なstatementBlockです");
		tk = ct.getNextToken(pcx);
		CParseRule statement = null;
		while (Statement.isFirst(tk)) {
			statement = new Statement(pcx);
			statement.parse(pcx);
			list.add(statement);
			tk = ct.getCurrentToken(pcx); // おそらく次のトークンを読んでる. でないとずっと同じトークンを読み続ける
		}

		tk = ct.getCurrentToken(pcx);
		if (tk.getType() != CToken.TK_RCUR) {
			pcx.fatalError(tk.toExplainString() + "statementの後ろは'}'です");
		}
		tk = ct.getNextToken(pcx); // }の次のトークンを読む
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		for (CParseRule statement : list) {
			statement.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		for (CParseRule statement : list) {
			statement.codeGen(pcx);
		}
	}
}
