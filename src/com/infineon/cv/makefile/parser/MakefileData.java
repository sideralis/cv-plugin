package com.infineon.cv.makefile.parser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;

public class MakefileData {
	private static HashSet<String> sourceDir;
	private static HashSet<String> includeDir;
	private static HashMap<String, String> defines;
	
	/**
	 * Parse a makefile and extract all relevant data
	 * @param fileLocation the path location of the makefile
	 */
	public static void parse(File makefilePath) {
		sourceDir = new HashSet<String>();
		sourceDir = new HashSet<String>();
		defines = new HashMap<String, String>();
		
		VariableManager var = new VariableManager();
		MakefileParser parMake = new MakefileParser(var);
		try {
			parMake.parse(makefilePath);
			System.out.println(parMake);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		parMake.getSourceDir(sourceDir);
		parMake.getIncludeDir(includeDir);
		parMake.getDefines(defines);
	}
	/**
	 * Return the list of directories which are used to find sources during compilation
	 * @return a list of directories for C sources
	 */
	public static HashSet<String> getSourceDir() {
		return sourceDir;
	}
	/**
	 * Return a list of directories which are used to find includes during compilation
	 * @return the list of include directories
	 */
	public static HashSet<String> getIncludeDir() {
		return includeDir;
	}
}
