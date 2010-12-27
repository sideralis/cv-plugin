package com.infineon.cv.misc;

import org.eclipse.cdt.managedbuilder.core.IBuildPathResolver;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;

/**
 * testBuildPath is created for testing if we can create linked resources with 
 * envVarBuildPath 
 * @author zhaoxi
 *
 */
public class testBuildPath implements IBuildPathResolver {

	@Override
	public String[] resolveBuildPaths(int pathType, String variableName,
			String variableValue, IConfiguration configuration) {
		return null;
	}

}
