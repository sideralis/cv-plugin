package com.infineon.cv.includes;

import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.macros.IProjectBuildMacroSupplier;

import com.infineon.cv.InfineonPreferencePage;
import com.infineon.cv.PreferenceConstants;

/**
 * URFETMacro4inlucde implements IProjectBuildMacroSupplier interface. Add build
 * variables(project Location) for resolving relative include path of "URFET"
 * project.
 * */
public class URFETMacro4include implements IProjectBuildMacroSupplier {

	private InfineonMacro[] macros = new InfineonMacro[2];

	public IBuildMacro getMacro(final String macroName, final IManagedProject project, IBuildMacroProvider provider) {
		initializeMacros(project, provider);
		for (InfineonMacro macro : macros) {
			if (macroName.equals(macro.getName())) {
				return macro;
			}
		}
		return null;
	}

	public IBuildMacro[] getMacros(IManagedProject project, IBuildMacroProvider provider) {
		return initializeMacros(project, provider);
	}

	private IBuildMacro[] initializeMacros(IManagedProject project, IBuildMacroProvider provider) {
		String projLoc, root;
		int pos;

		projLoc = project.getOwner().getLocation().toString();
		pos = projLoc.indexOf("S-Gold");
		if (pos != -1) {
			root = projLoc.substring(0, pos);
			
			InfineonPreferencePage pref = new InfineonPreferencePage();

			macros[0] = new InfineonMacro("URFETProjectDir", root);
			macros[1] = new InfineonMacro("toolDir", pref.getPreferenceStore().getString(PreferenceConstants.INFINEON_TOOLVIEW_DRIVE));
		}
		return this.macros;
	}

}