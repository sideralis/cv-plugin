package com.infineon.cv.env;

import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;

/**
 * 
 * A class used to describe some environment variables Used by class EnvVar2_2,
 * EnvVar3_1, EnvVarADS1_2
 * 
 * @author gautier
 */
public class ARMBuildEnvironmentVariable implements IBuildEnvironmentVariable {

	private final String name;
	private final String value;
	private final int operation;
	private static final String DELIMITER = ";";

	/**
	 * Constructor
	 * 
	 * @param name The name of the variable
	 * @param value The value of the variable
	 * @param operation Can be IBuildEnvironmentVariable.ENVVAR_REPLACE, ...
	 */
	public ARMBuildEnvironmentVariable(String name, String value, int operation) {
		this.name = name;
		this.value = value;
		this.operation = operation;
	}

	/**
	 * Return ;
	 * 
	 * @return ;
	 */
	public String getDelimiter() {
		return DELIMITER;
	}

	/**
	 * It returns the name of the variable
	 * 
	 * @return the name of the variable
	 */
	public String getName() {
		return name;
	}

	/**
	 * It returns the value of the variable
	 * 
	 * @return the value of the variable
	 */
	public String getValue() {
		return value;
	}

	/**
	 * It returns the operation to be done on the variable
	 * 
	 * @return IBuildEnvironmentVariable.ENVVAR_REPLACE, ...
	 */
	public int getOperation() {
		return operation;
	}
}
