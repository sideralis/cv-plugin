package com.infineon.cv.misc;

import org.eclipse.cdt.core.dom.parser.c.GCCParserExtensionConfiguration;

/**
 * 
 * @author gautier
 *
 */
public class CAssemblyParserExtensionConfiguration extends GCCParserExtensionConfiguration{
	private static CAssemblyParserExtensionConfiguration sInstance= new CAssemblyParserExtensionConfiguration();

	public static CAssemblyParserExtensionConfiguration getInstance() {
		return sInstance;
	}
	public boolean supportFunctionStyleAssembler() {
		return true;
	}
}
