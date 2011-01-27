package com.infineon.cv.misc;

import org.eclipse.cdt.core.dom.parser.c.GCCScannerExtensionConfiguration;

/**
 * recognize specific ARM keywords
 * 
 * @author zhaoxi
 * 
 */
public class ARMScannerExtensionConfiguration extends GCCScannerExtensionConfiguration {

	public static final char [] cp__ARM = "__arm".toCharArray(); //$NON-NLS-1$

	private static ARMScannerExtensionConfiguration sInstance = new ARMScannerExtensionConfiguration();

	/**
	 * @since 5.1
	 */
	public static ARMScannerExtensionConfiguration getInstance() {
		return sInstance;
	}

	/**
	 * Constructor A try to add some keyword to the C default scanner from CDT
	 */
	public ARMScannerExtensionConfiguration() {
		super();
		addMacro("__irq", "");
		addMacro("__align(p)","");
		addMacro("__int64","long long");
		addMacro("__global_reg(p)","");
		addMacro("__weak","");
		addMacro("__packed","");
	}
}
