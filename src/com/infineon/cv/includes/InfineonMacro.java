package com.infineon.cv.includes;

import org.eclipse.cdt.core.cdtvariables.ICdtVariable;
import org.eclipse.cdt.managedbuilder.macros.BuildMacroException;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacro;

/**
 * An Infineon macro
 * @author Zhao Xin
 *
 */
public class InfineonMacro implements IBuildMacro {

	private int type;
	private String stringValue;
	private String name;

	/**
	 * Constructor - Set type to 1
	 * @param name name of the macro
	 * @param value value of the macro
	 */
	public InfineonMacro(String name, String value) {
		this.type = ICdtVariable.VALUE_TEXT;
		this.stringValue = value;
		this.name = name;

	}
	/**
	 * Return type
	 * @return 1
	 */
	public int getMacroValueType() {
		return getValueType();
	}
	/**
	 * 
	 */
	public String[] getStringListValue() throws BuildMacroException {
		String[] valueList = { getStringValue() };
		return valueList;
	}
	/**
	 * 
	 */
	public String getStringValue() throws BuildMacroException {
		return stringValue;
	}
	/**
	 * 
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 */
	public int getValueType() {
		return type;
	}

}
