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
        
        int lt1 = LT(1);
        
        if (supportFunctionStyleAsm && lt1 != IToken.tLBRACE && lt1 != IToken.tLPAREN) {
        	return functionStyleAsmDeclaration();
        }
        
        StringBuilder buffer= new StringBuilder();
        int lastOffset;
        if (lt1 == IToken.tLPAREN) {
        	asmExpression(buffer);
        	lastOffset = consume(IToken.tSEMI).getOffset();
        } else {
        	asmExpressionBrace(buffer);
        	lastOffset = offset+buffer.length()+1; 
        }

        return buildASMDirective(offset, buffer.toString(), lastOffset);
    }
	
	protected IToken asmExpressionBrace(StringBuilder content) throws EndOfFileException, BacktrackException {
		IToken t= consume(IToken.tLBRACE);
    	boolean needspace= false;
        int open= 1;
        while (open > 0) {
        	t= consume();
			switch(t.getType()) {
			case IToken.tLBRACE:
				open++;
				break;
        	case IToken.tRBRACE:
        		open--;
        		break;
        	case IToken.tEOC:
        		throw new EndOfFileException();
        	
        	default:
        		if (content != null) {
        			if (needspace) {
        				content.append(' ');
        			}
        			content.append(t.getCharImage());
        			needspace= true;
        		}
        		break;
			}
        }
		return t;
	}	
}
