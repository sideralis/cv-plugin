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
		addMacro("__value_in_regs","");
		addMacro("__BLACK(p)","p");
		addMacro("__RED(p)","p");
		addMacro("__GREEN(p)","p");
		addMacro("__YELLOW(p)","p");
		addMacro("__BLUE(p)","p");
		addMacro("__MAGENTA(p)","p");
		addMacro("__CYAN(p)","p");
		addMacro("__WHITE(p)","p");
	}
}
