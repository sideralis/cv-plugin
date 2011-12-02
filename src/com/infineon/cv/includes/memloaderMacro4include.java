package com.infineon.cv.includes;

import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.macros.IProjectBuildMacroSupplier;

import com.infineon.cv.InfineonPreferencePage;

/**
 * memloaderMacro4include implements IProjectBuildMacroSupplier. Add build
 * variables(project Location) for resolving relative include path of
 * "memloader" project.
 * */
public class memloaderMacro4include implements IProjectBuildMacroSupplier {

	private InfineonMacro[] macros = new InfineonMacro[1];

	public IBuildMacro getMacro(final String macroName, final IManagedProject project, IBuildMacroProvider provider) {
		initializeMacros(project, provider);
		for (InfineonMacro macro : macros) {
			if (macroName.equals(macro.getName())) {
				return macro;
			}
		}
		return null;
	}

	private IBuildMacro[] initializeMacros(IManagedProject project, IBuildMacroProvider provider) {
		String projLoc, root;
		int pos;

		projLoc = project.getOwner().getLocation().toString();
		pos = projLoc.indexOf("MemLoader");
		if (pos != -1) {
			root = projLoc.substring(0, pos);

			InfineonPreferencePage pref = new InfineonPreferencePage();
			macros[0] = new InfineonMacro("testcaseProjectDir", root.concat("MemLoader/C_ASM/Target/SG/NOR_Flash"));
//			macros[1] = new InfineonMacro("CV_FoundationDir", root.concat("CV_Foundation/"));
		}
		return this.macros;
	}

	public IBuildMacro[] getMacros(IManagedProject project, IBuildMacroProvider provider) {
		return initializeMacros(project, provider);
	}

}
