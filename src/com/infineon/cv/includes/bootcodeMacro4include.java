package com.infineon.cv.includes;

import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.macros.IProjectBuildMacroSupplier;

import com.infineon.cv.InfineonPreferencePage;

/**
 * bootcodeMacro4include implements IProjectBuildMacroSupplier interface. Add
 * build variables(project Location) for resolving relative include path of
 * "bootcode" project.
 * */
public class bootcodeMacro4include implements IProjectBuildMacroSupplier {

	private InfineonMacro[] macros = new InfineonMacro[3];

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
		pos = projLoc.indexOf("S-Gold-Bootcode");
		if (pos != -1) {
			root = projLoc.substring(0, pos);

			InfineonPreferencePage pref = new InfineonPreferencePage();

			macros[0] = new InfineonMacro("bcoRomProjectDir", root.concat("S-Gold-Bootcode/S-GOLD/Target/"));
			macros[1] = new InfineonMacro("bcoTcProjectDir", root.concat("S-Gold-Bootcode/S-GOLD/Verification/"));			
			macros[2] = new InfineonMacro("CV_FoundationDir", root.concat("CV_Foundation/"));
		}
		return this.macros;
	}
}
