package com.infineon.cv.includes;

import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.macros.IProjectBuildMacroSupplier;

import com.infineon.cv.InfineonPreferencePage;
import com.infineon.cv.PreferenceConstants;

/**
 * testcaseMacro4include implements IProjectBuildMacroSupplier interface. Add
 * build variables(project Location) for resolving relative include path of
 * "CV Testcase" project.
 * */
public class testcaseMacro4include implements IProjectBuildMacroSupplier {

	/** The 2 main macros */
	private InfineonMacro[] macros = new InfineonMacro[3];

	/**
	 * 
	 */
	public IBuildMacro getMacro(final String macroName, final IManagedProject project, IBuildMacroProvider provider) {
		initializeMacros(project, provider);
		for (InfineonMacro macro : macros) {
			if (macroName.equals(macro.getName())) {
				return macro;
			}
		}
		return null;
	}
	/**
	 * 
	 */
	public IBuildMacro[] getMacros(IManagedProject project, IBuildMacroProvider provider) {
		return initializeMacros(project, provider);
	}

	/**
	 * 
	 * @param project
	 * @param provider
	 * @return
	 */
	private IBuildMacro[] initializeMacros(IManagedProject project, IBuildMacroProvider provider) {
		macros[0] = new InfineonMacro("testcaseProjectDir", Character.toString(project.getOwner().getLocation().toString().charAt(0)).concat(":/S-Gold/S-GOLD_Family_Environment/"));
		InfineonPreferencePage pref = new InfineonPreferencePage();
		macros[1] = new InfineonMacro("toolDir", pref.getPreferenceStore().getString(PreferenceConstants.INFINEON_TOOLVIEW_DRIVE));
		macros[2] = new InfineonMacro("cryptoProjectDir", Character.toString(project.getOwner().getLocation().toString().charAt(0)).concat(":/CRYPTO/S-GOLD_Family_Environment/"));
		return this.macros;
	}

}
