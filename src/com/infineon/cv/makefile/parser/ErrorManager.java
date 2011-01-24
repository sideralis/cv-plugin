/**
 * Copyright (C) 2009, Gajo Csaba
 *
 * This file is free software; the author gives unlimited 
 * permission to copy and/or distribute it, with or without
 * modifications, as long as this notice is preserved.
 * 
 * For more information read the LICENSE file that came
 * with this distribution. 
 */
package com.infineon.cv.makefile.parser;

/**
 * @author cgajo
 *
 */
public class ErrorManager {

	public boolean shouldFail(String msg, Class<?> clazz) {
		System.err.println(msg);
		if (clazz.equals(VariableManager.class) || clazz.equals(Variable.class)) {
			return true;
		}
		return false;
	}
	
	private static ErrorManager instance;
	
	public static ErrorManager get() {
		if (instance == null) {
			instance = new ErrorManager();
		}
		return instance;
	}
	
}
