package com.infineon.cv.env;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;

import com.infineon.cv.InfineonPreferencePage;
import com.infineon.cv.PreferenceConstants;

/***
 * EnvVarADS1_2 implements IConfigurationEnvironmentVariableSupplier interface
 * which offers the environment variables of ADS 1.2 on the level of toolchain
 * configuration.
 * */
public class EnvVarADS1_2 implements IConfigurationEnvironmentVariableSupplier {

	private Map<String, IBuildEnvironmentVariable> envvars;

	private static final String DELIMITER = ";";
	/**
	 * Add a new environment variable
	 * @param var the variable to be added
	 */
	public void addvar(ARMBuildEnvironmentVariable var) {
		envvars.put(var.getName(), var);

	}

	private StringBuffer addPath(StringBuffer buf, String[] dirs, String sep) {
		for (int i = 0; i < dirs.length; i++) {
			buf.append(dirs[i]);
			if (i != dirs.length - 1)
				buf.append(sep);
		}
		return buf;
	}

	private Map<String, IBuildEnvironmentVariable> getVars() {
		if (envvars == null) {
			envvars = new HashMap<String, IBuildEnvironmentVariable>();
			StringBuffer buff = new StringBuffer();
			String[] dirs = new String[1];
			buff = new StringBuffer();
			InfineonPreferencePage pref = new InfineonPreferencePage();
			char toolViewDrive = pref.getPreferenceStore().getString(PreferenceConstants.INFINEON_TOOLVIEW_DRIVE).charAt(0);
			addPath(buff, dirs, DELIMITER);

			addvar(new ARMBuildEnvironmentVariable("ARMLMD_LICENSE_FILE", "2045@elicserv1.muc.infineon.com;2045@elicserv2.muc.infineon.com;2045@elicserv3.muc.infineon.com",
					IBuildEnvironmentVariable.ENVVAR_REPLACE));
			addvar(new ARMBuildEnvironmentVariable("ARMCONF", toolViewDrive + ":\\IFX_Tools\\ARM\\ADS\\Bin", IBuildEnvironmentVariable.ENVVAR_REPLACE));

			addvar(new ARMBuildEnvironmentVariable("ARMDLL", toolViewDrive + ":\\IFX_Tools\\ARM\\ADS\\Bin", IBuildEnvironmentVariable.ENVVAR_REPLACE));

			addvar(new ARMBuildEnvironmentVariable("ARMHOME", toolViewDrive + ":\\IFX_Tools\\ARM\\ADS", IBuildEnvironmentVariable.ENVVAR_REPLACE));

			addvar(new ARMBuildEnvironmentVariable("ARMINC", toolViewDrive + ":\\IFX_Tools\\ARM\\ADS\\Include", IBuildEnvironmentVariable.ENVVAR_REPLACE));

			addvar(new ARMBuildEnvironmentVariable("ARMLIB", toolViewDrive + ":\\IFX_Tools\\ARM\\ADS\\Lib", IBuildEnvironmentVariable.ENVVAR_REPLACE));

			// String Drive = "";// read the value from a file
			addvar(new ARMBuildEnvironmentVariable("Path", toolViewDrive + ":\\IFX_Tools\\gnu\\bin;" + toolViewDrive + ":\\IFX_Tools\\GSMTimer;"
					+ "C:\\Program Files\\Rational\\ClearCase\\bin",IBuildEnvironmentVariable.ENVVAR_REPLACE));

		}
		return envvars;
	}

	public IBuildEnvironmentVariable getVariable(String variableName, IConfiguration configuration, IEnvironmentVariableProvider provider) {
		return getVars().get(variableName);
	}
	/**
	 * 
	 * @param configuration
	 * @param provider
	 * @return The variables
	 */
	@Override
	public IBuildEnvironmentVariable[] getVariables(IConfiguration configuration, IEnvironmentVariableProvider provider) {
		Map<String, IBuildEnvironmentVariable> envvars = getVars();
		return envvars.values().toArray(new IBuildEnvironmentVariable[envvars.size()]);
	}
}
