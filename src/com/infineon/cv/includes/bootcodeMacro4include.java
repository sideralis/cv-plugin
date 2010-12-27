package com.infineon.cv.includes;

import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.macros.IProjectBuildMacroSupplier;

import com.infineon.cv.InfineonPreferencePage;
import com.infineon.cv.PreferenceConstants;

/**
 * bootcodeMacro4include implements IProjectBuildMacroSupplier interface.
 * Add build variables(project Location) for resolving relative include path of "bootcode" project.
 * */
public class bootcodeMacro4include implements IProjectBuildMacroSupplier {

	private InfineonMacro[] macros = new InfineonMacro[2];

	public IBuildMacro getMacro(final String macroName,
			final IManagedProject project, IBuildMacroProvider provider) {
		initializeMacros(project, provider);
		for (InfineonMacro macro : macros) {
			if (macroName.equals(macro.getName())) {
				return macro;
			}
		}
		return null;
	}

	public IBuildMacro[] getMacros(IManagedProject project,
			IBuildMacroProvider provider) {
		return initializeMacros(project, provider);
	}

	private IBuildMacro[] initializeMacros(IManagedProject project,
			IBuildMacroProvider provider) {
		macros[0] = new InfineonMacro("bootcodeProjectDir", Character.toString(
				project.getOwner().getLocation().toString().charAt(0)).concat(
				":/S-Gold-Bootcode/S-GOLD/Target"));
		InfineonPreferencePage pref = new InfineonPreferencePage();
		macros[1] = new InfineonMacro("toolDir", pref.getPreferenceStore()
				.getString(PreferenceConstants.INFINEON_TOOLVIEW_DRIVE));
		return this.macros;
	}
}
