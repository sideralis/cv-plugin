package com.infineon.cv.misc;

import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.parser.IScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.ISourceCodeParser;
import org.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.internal.core.dom.parser.c.GNUCSourceParser;
import org.eclipse.cdt.internal.core.pdom.dom.IPDOMLinkageFactory;
import org.eclipse.cdt.internal.core.pdom.dom.c.PDOMCLinkageFactory;

import com.infineon.cv.InfineonActivator;
import com.infineon.cv.ARMScannerExtensionConfiguration;

public class ARMLanguage extends GCCLanguage {// AbstractCLikeLanguage{
	protected static final ARMScannerExtensionConfiguration ARM_SCANNER_EXTENSION = new ARMScannerExtensionConfiguration();
	protected static final ARMParserExtensionConfiguration ARM_PARSER_EXTENSION = ARMParserExtensionConfiguration.getInstance();
	// Must match the id in the extension
	public static final String ID = InfineonActivator.PLUGIN_ID + ".arm"; //$NON-NLS-1$ 

	
	private static final ARMLanguage DEFAULT_INSTANCE = new ARMLanguage();
	
	public static ARMLanguage getDefault() {
		return DEFAULT_INSTANCE;
	}

	@Override
	@SuppressWarnings({ "unchecked", "restriction" })
	public Object getAdapter(Class adapter) {
		if (adapter == IPDOMLinkageFactory.class) {
			return new PDOMCLinkageFactory();
		}
		return super.getAdapter(adapter);
	}

	public String getId() {
		return ID;
	}

	protected IScannerExtensionConfiguration getScannerExtensionConfiguration() {
		return ARM_SCANNER_EXTENSION;
	}

	protected ICParserExtensionConfiguration getParserExtensionConfiguration() {
		return ARM_PARSER_EXTENSION;
	}

	@Override
	protected ISourceCodeParser createParser(IScanner scanner, ParserMode parserMode, IParserLogService logService, IIndex index) {
		return new ARMCSourcParser(scanner, parserMode, logService, getParserExtensionConfiguration(), index);
	}

	@Override
	protected ParserLanguage getParserLanguage() {
		return ParserLanguage.C;
	}
	
}
