package com.infineon.cv.misc;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.EndOfFileException;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.internal.core.dom.parser.BacktrackException;
import org.eclipse.cdt.internal.core.dom.parser.c.GNUCSourceParser;

@SuppressWarnings("restriction")
public class ARMCSourcParser extends GNUCSourceParser {

	public ARMCSourcParser(IScanner scanner, ParserMode parserMode, IParserLogService logService, ICParserExtensionConfiguration config) {
		super(scanner, parserMode, logService, config);
	}

	public ARMCSourcParser(IScanner scanner, ParserMode parserMode, IParserLogService logService, ICParserExtensionConfiguration parserExtensionConfiguration, IIndex index) {
		// TODO Auto-generated constructor stub
		super(scanner, parserMode, logService, parserExtensionConfiguration, index);
	}

	protected IASTDeclaration asmDeclaration() throws EndOfFileException, BacktrackException {
        final int offset= consume().getOffset(); // t_asm
        if (LT(1) == IToken.t_volatile) {
        	consume();
        }
        
        if (supportFunctionStyleAsm && LT(1) != IToken.tLPAREN) {
        	return functionStyleAsmDeclaration();
        }
        
        StringBuilder buffer= new StringBuilder();
        asmExpression(buffer);
        int lastOffset = consume(IToken.tSEMI).getEndOffset();

        return buildASMDirective(offset, buffer.toString(), lastOffset);
    }
	
}
