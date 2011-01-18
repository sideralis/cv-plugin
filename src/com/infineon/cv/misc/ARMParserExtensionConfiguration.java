package com.infineon.cv.misc;

import org.eclipse.cdt.core.dom.parser.c.GCCParserExtensionConfiguration;

/**
 * 
 * @author gautier
 *
 */
public class ARMParserExtensionConfiguration extends GCCParserExtensionConfiguration{
	private static ARMParserExtensionConfiguration sInstance= new ARMParserExtensionConfiguration();

	public static ARMParserExtensionConfiguration getInstance() {
		return sInstance;
	}
	public boolean supportFunctionStyleAssembler() {
		return true;
	}
}
