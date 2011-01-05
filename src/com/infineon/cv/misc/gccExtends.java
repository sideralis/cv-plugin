package com.infineon.cv.misc;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.parser.IScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.ISourceCodeParser;
import org.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.internal.core.dom.parser.c.GNUCSourceParser;

import com.infineon.cv.POPCscannerExtensionConfiguration;

public class gccExtends extends GCCLanguage {// AbstractCLikeLanguage{
 protected static final POPCscannerExtensionConfiguration POPC_SCANNER_EXTENSION = new POPCscannerExtensionConfiguration();
 protected static final CAssemblyParserExtensionConfiguration CAssembly_GNU_PARSER_EXTENSION= CAssemblyParserExtensionConfiguration.getInstance();
 private static final gccExtends DEFAULT_INSTANCE = new gccExtends();
	public static final String ID = CCorePlugin.PLUGIN_ID + ".ifx"; //$NON-NLS-1$ 

	 public static gccExtends getDefault() {
	 return DEFAULT_INSTANCE;
	 }
	public String getId() {
		return ID;
	}

	protected IScannerExtensionConfiguration getScannerExtensionConfiguration() {
		return POPC_SCANNER_EXTENSION;
	}
	protected ICParserExtensionConfiguration getParserExtensionConfiguration() {
		return CAssembly_GNU_PARSER_EXTENSION;
	}

	@SuppressWarnings("restriction")
	@Override
	protected ISourceCodeParser createParser(IScanner scanner, ParserMode parserMode, IParserLogService logService, IIndex index) {
		return new GNUCSourceParser(scanner, parserMode, logService, getParserExtensionConfiguration(), index);
	}
}
