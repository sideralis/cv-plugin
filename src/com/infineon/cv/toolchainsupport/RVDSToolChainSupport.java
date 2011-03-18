package com.infineon.cv.toolchainsupport;

import org.eclipse.cdt.managedbuilder.core.IManagedIsToolChainSupported;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.core.runtime.PluginVersionIdentifier;

/**
 * Used to decide if the tool chain is supported
 * 
 * @author gautier
 * 
 */
@SuppressWarnings("deprecation")
public class RVDSToolChainSupport implements IManagedIsToolChainSupported {
	/**
	 * Return true if tool chain is supported or false
	 */
	@Override
	public boolean isSupported(IToolChain toolChain, PluginVersionIdentifier version, String instance) {
		String var;

		var = System.getenv("HADES_PERL");
//		if (var == null)
//			return true;
//		else
//			return false;
		return true;
	}
}
