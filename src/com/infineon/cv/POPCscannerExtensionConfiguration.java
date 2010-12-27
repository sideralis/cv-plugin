package com.infineon.cv;

import org.eclipse.cdt.core.dom.parser.c.GCCScannerExtensionConfiguration;
import org.eclipse.cdt.core.parser.IToken;
/**
 * recognize specific ARM keywords
 * @author zhaoxi
 *
 */
public class POPCscannerExtensionConfiguration extends GCCScannerExtensionConfiguration {
/*
	public static final String IRQ = "__irq";
	public static final char[] popcIRQ = "__irq".toCharArray();*/
	//$NON-NLS-1$
	/**
	 * Constructor
	 * A try to add some keyword to the C default scanner from CDT
	 */
    public POPCscannerExtensionConfiguration(){
    	//addKeyword(popcIRQ, 0);
    	super();
    	addKeyword("__asm".toCharArray(), IToken.FIRST_RESERVED_IExtensionToken);
    	addKeyword("__irq".toCharArray(), IToken.FIRST_RESERVED_IExtensionToken);
    	
    }

/*	public CharArrayIntMap getAdditionalKeywords() {
		CharArrayIntMap additionalCPPKeywords = new CharArrayIntMap(0, 0);
		additionalCPPKeywords.put( POPCscannerExtensionConfiguration.popcIRQ,IToken.FIRST_RESERVED_IGCCToken);
		return additionalCPPKeywords;
	}*/
	}
