package com.infineon.cv.errorParsing;

import java.util.regex.Matcher;

import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.core.errorparsers.ErrorPattern;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

public class ARMErrorPattern extends ErrorPattern {
	/** 
	 * Full Pattern Constructor. Note that a group equal 0 means that
	 * the parameter is missing in the error message.
	 *
	 * @param pattern - regular expression describing the message
	 * @param groupFileName - matcher group of file name
	 * @param groupLineNum - matcher group of line number
	 * @param groupDesc - matcher group of description
	 * @param groupVarName - matcher group of variable name
	 * @param severity - severity, one of
	 *        <br>{@link IMarkerGenerator#SEVERITY_INFO},
	 *        <br>{@link IMarkerGenerator#SEVERITY_WARNING},
	 *        <br>{@link IMarkerGenerator#SEVERITY_ERROR_RESOURCE},
	 *        <br>{@link IMarkerGenerator#SEVERITY_ERROR_BUILD}	
	 */
	public ARMErrorPattern(String pattern, int groupFileName, int groupLineNum, int groupDesc, int groupVarName, int severity) {
		super(pattern, groupFileName, groupLineNum, groupDesc, groupVarName, severity);
	}
	/**
	 * 
	 */
	protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
		int severity = getSeverity(matcher);
		if (severity == -1)
			// Skip
			return true;

		String fileName = getFileName(matcher);
		int lineNum = getLineNum(matcher);
		String desc = getDesc(matcher);
		String varName = getVarName(matcher);
		IPath externalPath = null ;
		
		IResource file = null;
		if (fileName != null) {
			fileName = checkAndReplaceExtension(fileName);
			file = eoParser.findFileName(fileName);

			if (file == null) {
				// If the file is not found in the workspace we attach the problem to the project
				// and add the external path to the file.
				file = eoParser.getProject();
				IPath projectLocation = file.getLocation();
				externalPath = projectLocation.append(fileName);
			}
		}
		
		eoParser.generateExternalMarker(file, lineNum, desc, severity, varName, externalPath);
		return true;
	}
	/**
	 * For some linker error, the file creating a problem is a o file, it should be converted to the existing file (c or s)
	 * @param file
	 */
	private String checkAndReplaceExtension(String file) {
		if (file.endsWith("o")) {
			// Replace by C file
			int endIndex = file.lastIndexOf(".o");
			file = file.substring(0, endIndex);
			file = file.concat(".c");
		}
		return file;
	}
}
