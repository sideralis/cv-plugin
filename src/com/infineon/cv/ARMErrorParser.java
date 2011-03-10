package com.infineon.cv;

import java.util.regex.Matcher;

import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.core.errorparsers.AbstractErrorParser;
import org.eclipse.cdt.core.errorparsers.ErrorPattern;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * The ARM error parser class. It parses the error generated by the compiler
 * 
 * @author gautier
 */
public class ARMErrorParser extends AbstractErrorParser {

	private static final ErrorPattern[] patterns = { 
		new ErrorPattern("\"(.+)\", line ([\\d]+): Error: (.*)", 1, 2, 3, 0, IMarkerGenerator.SEVERITY_ERROR_BUILD),
		new ErrorPattern("\"(.+)\", line ([\\d]+): Warning: (.*)", 1, 2, 3, 0, IMarkerGenerator.SEVERITY_WARNING),
		new ErrorPattern("(.*?)\\(line ([0-9]+), col [0-9]+\\) Warning: (.*)",1,2,3,0, IMarkerGenerator.SEVERITY_WARNING) {
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
		},
	// new ErrorPattern(
	// "\"([a-zA-Z0-9_\\]+)\"(, line ) ([0-9]+) (: Error: ) (.*)", 1,
	// 2, 3, 0, IMarkerGenerator.SEVERITY_ERROR_RESOURCE),
	// new ErrorPattern(
	// "\"([a-zA-Z0-9_\\]+)\"(, line ) ([0-9]+) (: Warning: ) (.*)", 1,
	// 2, 3, 0, IMarkerGenerator.SEVERITY_WARNING),
	};

	/**
	 * Constructor to set the error pattern.
	 */
	public ARMErrorParser() {
		super(patterns);
	}
}
