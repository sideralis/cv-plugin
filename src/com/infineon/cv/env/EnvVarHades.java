package com.infineon.cv.env;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;

import com.infineon.cv.InfineonPreferencePage;
import com.infineon.cv.PreferenceConstants;

/**
 * 
 * @author gautier
 *
 */
public class EnvVarHades implements IConfigurationEnvironmentVariableSupplier {

	private Map<String, IBuildEnvironmentVariable> envvars = null;

	private static final String DELIMITER = ";";

	private void addvar(Map<String, IBuildEnvironmentVariable> envvars, IBuildEnvironmentVariable var) {
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
			InfineonPreferencePage pref = new InfineonPreferencePage();
			char toolViewDrive = pref.getPreferenceStore().getString(PreferenceConstants.INFINEON_TOOLVIEW_DRIVE).charAt(0);
			dirs[0] = toolViewDrive + ":\\IFX_Tools\\ARM\\RVDS\\RVCT\\Programs\\2.2\\349\\win_32-pentium";
			buff = new StringBuffer();

			addPath(buff, dirs, DELIMITER);

			addvar(envvars, new ARMBuildEnvironmentVariable("RVCT22BIN", buff.toString(), IBuildEnvironmentVariable.ENVVAR_REPLACE));
			addvar(envvars, new ARMBuildEnvironmentVariable("RVCT22INC", toolViewDrive + ":\\IFX_Tools\\ARM\\RVDS\\RVCT\\Data\\2.2\\349\\include\\windows", IBuildEnvironmentVariable.ENVVAR_REPLACE));
			addvar(envvars, new ARMBuildEnvironmentVariable("ARMLMD_LICENSE_FILE", "2045@elicserv1.muc.infineon.com;2045@elicserv2.muc.infineon.com;2045@elicserv3.muc.infineon.com",
					IBuildEnvironmentVariable.ENVVAR_REPLACE));
			addvar(envvars, new ARMBuildEnvironmentVariable("RVCT22LIB", toolViewDrive + ":\\IFX_Tools\\ARM\\RVDS\\RVCT\\Data\\2.2\\349\\lib", IBuildEnvironmentVariable.ENVVAR_REPLACE));

			addvar(envvars, new ARMBuildEnvironmentVariable("Path", 
					toolViewDrive + ":\\IFX_Tools\\gnu\\bin;" 
					+ toolViewDrive + ":\\IFX_Tools\\GSMTimer;" 
					+ toolViewDrive + ":\\IFX_Tools\\ARM\\RVDS\\RVCT\\Programs\\2.2\\349\\win_32-pentium;"
					+ "C:\\Program Files\\Rational\\ClearCase\\bin", IBuildEnvironmentVariable.ENVVAR_REPLACE));

		}
		return envvars;
	}

	public IBuildEnvironmentVariable getVariable(String variableName, IConfiguration configuration, IEnvironmentVariableProvider provider) {		
		return getVars().get(variableName);
	}

	public IBuildEnvironmentVariable[] getVariables(IConfiguration configuration, IEnvironmentVariableProvider provider) {
//		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
//		for (IProject project : projects)
//			System.out.println("Project = "+project.getName());

		Map<String, IBuildEnvironmentVariable> envvars = getVars();
		return envvars.values().toArray(new IBuildEnvironmentVariable[envvars.size()]);
	}
}
