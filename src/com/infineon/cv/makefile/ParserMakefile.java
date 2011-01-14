package com.infineon.cv.makefile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.envvar.IEnvironmentVariableManager;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

enum Flag {
	SRCS, SRCDIR, VPATH, EXECPATH, INCDIR, NULL
};

/**
 * ParserMakefile parses a makefile with the given path, users get variables'
 * values by getters and modify them by setters.
 */
public class ParserMakefile {
	File file;
	private String prjLoc;// = "Y:/S-Gold/S-GOLD_Family_Environment/Testcases";;
	final static String backslash_newline = " \\n";

	private String exec;
	private ArrayList<String> arch;
	private String my_scf; // url
	private String own_init_s; // url
	private String own_asflags;
	private String own_cflags;
	private String own_lkflags;
	private String own_arflags;
	private String own_disflags;
	private String itcm_base_address;
	private String dtcm_base_address;
	private ArrayList<String> srcs;
	private ArrayList<String> srcdir;
	/** The vpath value of the makefile */
	public ArrayList<String> vpath;
	/** The incdir value of the makefile */
	public ArrayList<String> incdir;
	/** The unwanted lib value from the makefile */
	public ArrayList<String> libunwanted;
	private ArrayList<String> forbidden_defines;
	private String exec_path;
	private ArrayList<String> defines;
	private String rootDir;
	private Flag flag;
	private ArrayList<String> defautMKValues;
	/**
	 * Constructor
	 * @param fileLoc The path to the makefile
	 */
	public ParserMakefile(String fileLoc) {
		flag = Flag.SRCS;
		srcs = new ArrayList<String>();
		srcdir = new ArrayList<String>();
		vpath = new ArrayList<String>();
		incdir = new ArrayList<String>();
		libunwanted = new ArrayList<String>();
		forbidden_defines = new ArrayList<String>();
		defines = new ArrayList<String>();
		defautMKValues = new ArrayList<String>();
		own_asflags = "";
		own_cflags = "";
		own_lkflags = "";
		own_arflags = "";
		own_disflags = "";
		itcm_base_address = "";
		dtcm_base_address = "";
		file = new File(fileLoc);
		this.prjLoc = file.getParent().toString();
	}

	protected void writeMakefile() {
		return;
		// try {
		// // Open the file that is the first
		// // command line parameter
		// File fstreamreader = file;
		// BufferedReader in = new BufferedReader(new
		// FileReader(fstreamreader));
		//
		// // Create file FileWriter
		// File fstreamwriter = new File("out");
		// BufferedWriter out = new BufferedWriter(new
		// FileWriter(fstreamwriter));
		//
		// String strLine;
		// // Read File Line By Line
		// while ((strLine = in.readLine()) != null) {
		// // is it variable ARCH?
		// if (strLine.startsWith("#")) {
		// out.write(strLine);
		// out.write("\n");
		// }
		// if (strLine.startsWith("EXEC")) {
		// out.write("EXEC := " + getExec());
		// out.write("\n");
		// }
		//
		// else if (strLine.startsWith("ARCH")) {
		// String arch = "";
		// for (String value : getArch()) {
		// arch = arch.concat(value);
		// arch = arch.concat("+");
		// }
		// for (String value : this.defautMKValues) {
		// arch = arch.concat(value);
		// arch = arch.concat("+");
		// }
		//
		// out.write("ARCH = " + arch.substring(0, arch.length() - 1));
		// out.write("\n");
		// } else if (strLine.startsWith("SRCS +=")) {
		// if (getSrcs().size() == 1) {
		// out.write("SRCS += " + formatPath(getSrcs().get(0)));
		// } else {
		// out.write("SRCS += " + formatPath(getSrcs().get(0)) + " \\\n");
		// int i = 1;
		// for (; i < getSrcs().size() - 1; i++) {
		// out.write("\t\t" + formatPath(getSrcs().get(i)) + " \\\n");
		// }
		// out.write("\t\t" + formatPath(getSrcs().get(i)) + "\n");
		//
		// }
		// }
		//
		// else if (strLine.startsWith("SRCDIR")) {
		// if (getSrcdir().size() == 1) {
		// out.write("SRCDIR += " + formatPath(getSrcdir().get(0)));
		// } else {
		// out.write("SRCDIR += " + formatPath(getSrcdir().get(0)) + " \\\n");
		// int i = 1;
		// for (; i < getSrcdir().size() - 1; i++) {
		// out.write("\t\t" + formatPath(getSrcdir().get(i)) + " \\\n");
		// }
		// out.write("\t\t" + formatPath(getSrcdir().get(i)) + "\n");
		// }
		// /*************** SRCDIR **************/
		//
		// // $(ROOTDIR)/Testcases/DMA_test/common
		// } else if (strLine.startsWith("VPATH")) {
		// if (getVpath().size() == 1) {
		// out.write("VPATH += " + formatPath(getVpath().get(0)));
		// } else {
		// out.write("VPATH += " + formatPath(getVpath().get(0)) + " \\\n");
		//
		// int i = 1;
		// for (; i < getVpath().size() - 1; i++) {
		// out.write("\t\t" + formatPath(getVpath().get(i)) + " \\\n");
		// }
		// out.write("\t\t" + formatPath(getVpath().get(i)) + "\n");
		// }
		//
		// } else if (strLine.startsWith("LIBUNWANTED")) {
		// out.write("LIBUNWANTED +=");
		// for (String lib : getLibunwanted()) {
		// out.write(" " + lib);
		// }
		// out.write("\n");
		// } else if (strLine.startsWith("INCDIR")) {
		//
		// if (getVpath().size() == 1) {
		// out.write("INCDIR += " + formatPath(getIncdir().get(0)));
		//
		// } else {
		// out.write("INCDIR += " + formatPath(getIncdir().get(0)) + " \\\n");
		//
		// int i = 1;
		// for (; i < getIncdir().size() - 1; i++) {
		// out.write("\t\t" + formatPath(getIncdir().get(i)) + " \\\n");
		//
		// }
		// out.write("\t\t" + formatPath(getIncdir().get(i)) + "\n");
		//
		// }
		//
		// } else if (strLine.startsWith("OWN_INIT_S")) {
		// out.write("OWN_INIT_S = " + getOwn_init_s());
		// out.write("\n");
		// } else if (strLine.startsWith("OWN_ASFLAGS")) {
		// out.write("OWN_ASFLAGS = " + getOwn_asflags());
		// out.write("\n");
		// } else if (strLine.startsWith("OWN_CFLAGS")) {
		// out.write("OWN_CFLAGS = " + getOwn_cflags());
		// out.write("\n");
		// } else if (strLine.startsWith("OWN_LKFLAGS")) {
		// out.write("OWN_LKFLAGS = " + getOwn_lkflags());
		// out.write("\n");
		// } else if (strLine.startsWith("ITCM_BASE_ADDRESS")) {
		// out.write("ITCM_BASE_ADDRESS = 0x" + getItcm_base_address());
		// out.write("\n");
		// } else if (strLine.startsWith("DTCM_BASE_ADDRESS")) {
		// out.write("DTCM_BASE_ADDRESS = 0x" + getDtcm_base_address());
		// out.write("\n");
		// } else if (!(strLine.startsWith("\t") || strLine.startsWith(" "))) {
		//
		// out.write(strLine);
		// out.write("\n");
		// }
		//
		// }
		// // Close the input stream
		// in.close();
		// out.close();
		// fstreamreader.delete();
		// System.out.print(fstreamwriter.renameTo(file));
		// } catch (Exception e) {// Catch exception if any
		// System.err.println("Error: " + e.getMessage());
		// }
	}

	public void readMakefile() {
		String makefileVarOutput = "\nall:\n"
								+  "\t@echo ### This file is generated by IFX CV plugin for Eclipse. Do not touch!\n"
								+  "\t@echo EXEC=$(EXEC)\n"
								+  "\t@echo ARCH=$(ARCH)\n"
								+  "\t@echo MY_SCF=$(MY_SCF)\n"
								+  "\t@echo OWN_INIT_S=$(OWN_INIT_S)\n"
								+  "\t@echo OWN_ASFLAGS=$(OWN_ASFLAGS)\n"
								+  "\t@echo OWN_CFLAGS=$(OWN_CFLAGS)\n"
								+  "\t@echo OWN_LKFLAGS=$(OWN_LKFLAGS)\n"
								+  "\t@echo OWN_ARFLAGS=$(OWN_ARFLAGS)\n"
								+  "\t@echo OWN_DISFLAGS=$(OWN_DISFLAGS)\n"
								+  "\t@echo ITCM_BASE_ADDRESS=$(ITCM_BASE_ADDRESS)\n"
								+  "\t@echo DTCM_BASE_ADDRESS=$(DTCM_BASE_ADDRESS)\n"
								+  "\t@echo SRCS=$(SRCS)\n"
								+  "\t@echo VPATH=$(VPATH)\n"
								+  "\t@echo SRCDIR=$(SRCDIR)\n"
								+  "\t@echo INCDIR=$(INCDIR)\n"
								+  "\t@echo LIBUNWANTED=$(LIBUNWANTED)\n"
								+  "\t@echo FORBIDDEN_DEFINES=$(FORBIDDEN_DEFINES)\n"
								+  "\t@echo EXEC_PATH=$(EXEC_PATH)\n"
								+  "\t@echo DEFINES=$(DEFINES)\n";
		StringBuffer makefile;
		Pattern pattern;
		Matcher matcher;
		String strLine;
		// Read all makefile
		makefile = new StringBuffer(6000);
		try {
			File fstreamreader = file;
			BufferedReader in = new BufferedReader(new FileReader(fstreamreader));
			while ((strLine = in.readLine()) != null) {
				// char c = strLine.charAt(strLine.length()-1);
				if ((strLine.length() != 0) && (strLine.charAt(strLine.length() - 1) == '\\'))
					makefile.append(strLine.substring(0, strLine.length() - 1));
				else
					makefile.append(strLine + "\n");

			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		// Search for "include ... makefile.mak" in makefile
		// and replace it with display of interesting variables
		pattern = Pattern.compile("\ninclude.*Makefile.mak");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			makefile.replace(matcher.start(), matcher.end(), makefileVarOutput );
		}
		// Save new makefile
		File fstreamwriter = new File(prjLoc + "\\makefile.1");
		try {
			FileWriter out = new FileWriter(fstreamwriter);
			out.write(makefile.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		String cmd = "C:\\Users\\Bernard\\WorkspaceRCP\\gnumake.exe -f " + prjLoc + "\\makefile.1";
		String cmd = "gnumake.exe -f " + prjLoc + "\\makefile.1";		
		try {

			System.out.println(System.getProperty("user.dir"));
			System.out.println("Execing " + cmd);
			// EXEC
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(cmd);
			
			// PROCESS BUILDER
//			ProcessBuilder pb = new ProcessBuilder(cmd);
//			Map<String, String> envi = pb.environment();
//			envi.clear();
//			envi.put(pathToGnumake.getName(),pathToGnumake.getValue());
//			System.out.println("dir="+pb.directory());
//			Process proc = pb.start();
			
	        // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");                        
            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
                
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
                                    
            // any error???
            int exitVal = proc.waitFor();
            System.out.println("ExitValue: " + exitVal);  
            readVariables(outputGobbler.getOutput());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void readVariables(String makefile) {
		Pattern pattern;
		Matcher matcher;
		// Search for EXEC
		pattern = Pattern.compile("\nEXEC[^_].*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			exec = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for ARCH
		pattern = Pattern.compile("\nARCH.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			arch = getStrings(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for MY_SCF
		pattern = Pattern.compile("\nMY_SCF.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			my_scf = getPath(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for OWN_INIT_S
		pattern = Pattern.compile("\nOWN_INIT_S.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			own_init_s = getPath(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for OWN_ASFLAGS
		pattern = Pattern.compile("\nOWN_ASFLAGS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			own_asflags = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for OWN_CFLAGS
		pattern = Pattern.compile("\nOWN_CFLAGS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			own_cflags = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for OWN_LKFLAGS
		pattern = Pattern.compile("\nOWN_LKFLAGS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			own_lkflags = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for OWN_ARFLAGS
		pattern = Pattern.compile("\nOWN_ARFLAGS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			own_arflags = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for OWN_DISFLAGS
		pattern = Pattern.compile("\nOWN_DISFLAGS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			own_disflags = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for ITCM_BASE_ADDRESS
		pattern = Pattern.compile("\nITCM_BASE_ADDRESS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			itcm_base_address = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for DTCM_BASE_ADDRESS
		pattern = Pattern.compile("\nDTCM_BASE_ADDRESS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			dtcm_base_address = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for SRCS
		pattern = Pattern.compile("\nSRCS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			srcs = getStrings(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for VPATH
		pattern = Pattern.compile("\nVPATH.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			vpath = getPaths(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for SRCDIR
		pattern = Pattern.compile("\nSRCDIR.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			srcdir = getPaths(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for INCDIR
		pattern = Pattern.compile("\nINCDIR.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			incdir = getPaths(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for LIBUNWANTED
		pattern = Pattern.compile("\nLIBUNWANTED.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			libunwanted = getStrings(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for FORBIDDEN_DEFINES
		pattern = Pattern.compile("\nFORBIDDEN_DEFINES.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			forbidden_defines = getStrings(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for EXEC_PATH
		pattern = Pattern.compile("\nEXEC_PATH.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			exec_path = getPath(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for DEFINES
		pattern = Pattern.compile("\nDEFINES.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			defines = getStrings(makefile.substring(matcher.start(), matcher.end()));
		}
	}

	protected void readMakefile0() {
		String strLine = null;
		StringBuffer makefile;
		Pattern pattern;
		Matcher matcher;

		// Read all makefile
		makefile = new StringBuffer(6000);
		try {
			File fstreamreader = file;
			BufferedReader in = new BufferedReader(new FileReader(fstreamreader));
			while ((strLine = in.readLine()) != null) {
				// char c = strLine.charAt(strLine.length()-1);
				if ((strLine.length() != 0) && (strLine.charAt(strLine.length() - 1) == '\\'))
					makefile.append(strLine.substring(0, strLine.length() - 1));
				else
					makefile.append(strLine + "\n");

			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		// Search for ROOTDIR
		pattern = Pattern.compile("\nROOTDIR.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			rootDir = getPath(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for EXEC
		pattern = Pattern.compile("\nEXEC[^_].*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			exec = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for ARCH
		pattern = Pattern.compile("\nARCH.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			arch = getStrings(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for MY_SCF
		pattern = Pattern.compile("\nMY_SCF.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			my_scf = getPath(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for OWN_INIT_S
		pattern = Pattern.compile("\nOWN_INIT_S.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			own_init_s = getPath(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for OWN_ASFLAGS
		pattern = Pattern.compile("\nOWN_ASFLAGS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			own_asflags = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for OWN_CFLAGS
		pattern = Pattern.compile("\nOWN_CFLAGS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			own_cflags = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for OWN_LKFLAGS
		pattern = Pattern.compile("\nOWN_LKFLAGS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			own_lkflags = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for OWN_ARFLAGS
		pattern = Pattern.compile("\nOWN_ARFLAGS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			own_arflags = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for OWN_DISFLAGS
		pattern = Pattern.compile("\nOWN_DISFLAGS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			own_disflags = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for ITCM_BASE_ADDRESS
		pattern = Pattern.compile("\nITCM_BASE_ADDRESS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			itcm_base_address = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for DTCM_BASE_ADDRESS
		pattern = Pattern.compile("\nDTCM_BASE_ADDRESS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			dtcm_base_address = getString(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for SRCS
		// TODO manage += or =
		pattern = Pattern.compile("\nSRCS.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			srcs = getStrings(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for VPATH
		pattern = Pattern.compile("\nVPATH.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			vpath = getPaths(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for SRCDIR
		pattern = Pattern.compile("\nSRCDIR.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			srcdir = getPaths(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for INCDIR
		pattern = Pattern.compile("\nINCDIR.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			incdir = getPaths(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for LIBUNWANTED
		pattern = Pattern.compile("\nLIBUNWANTED.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			libunwanted = getStrings(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for FORBIDDEN_DEFINES
		pattern = Pattern.compile("\nFORBIDDEN_DEFINES.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			forbidden_defines = getStrings(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for EXEC_PATH
		pattern = Pattern.compile("\nEXEC_PATH.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			exec_path = getPath(makefile.substring(matcher.start(), matcher.end()));
		}
		// Search for DEFINES
		pattern = Pattern.compile("\nDEFINES.*\n");
		matcher = pattern.matcher(makefile);
		while (matcher.find()) {
			defines = getStrings(makefile.substring(matcher.start(), matcher.end()));
		}
		// Resolve path if needed
		resolve();
	}

	private void resolve() {
		// Start with incdir
		String s;
		int i, j;

		for (i = incdir.size() - 1; i >= 0; i--) {
			s = incdir.get(i);
			if (s.compareTo("$(VPATH)") == 0) {
				incdir.remove(i);
				for (j = 0; j < vpath.size(); j++)
					incdir.add(vpath.get(j));
			}
		}

	}

	private String getPath(String s) {
		String ret = "";

		ret = getString(s);
		if (ret == null || ret.isEmpty() || ret.compareTo("") == 0)
			return "";
		else
			return resolvePath(ret);
	}

	private String getString(String s) {
		Pattern pattern;
		Matcher matcher;
		String ret = "";

		pattern = Pattern.compile("=.*\n");
		matcher = pattern.matcher(s);
		while (matcher.find()) {
			String tmp = s.substring(matcher.start() + 1, matcher.end() - 1).trim();
			if (!tmp.startsWith("#"))
				ret = tmp;
		}
		return ret;
	}

	private String resolvePath(String ret) {
		String prj;
		int back = 0;

		if (ret.startsWith(".."))
			prj = prjLoc;
		else if (ret.startsWith("$(ROOTDIR)")) {
			ret = ret.replace("$(ROOTDIR)", "");
			prj = rootDir;
		} else
			prj = "";

		// get the project location
		while (ret.contains("..")) {
			ret = ret.substring(ret.indexOf("..") + 2);
			back++;
		}
		for (; back > 0; back--) {
			prj = prj.substring(0, prj.lastIndexOf("\\"));
		}
		return prj + ret;
	}

	private ArrayList<String> getPaths(String s) {
		ArrayList<String> as;
		int i;
		String e;

		as = getStrings(s);

		for (i = 0; i < as.size(); i++) {
			e = as.get(i);
			e = resolvePath(e);
			as.set(i, e);
		}
		return as;

	}

	private ArrayList<String> getStrings(String s) {
		ArrayList<String> arch = new ArrayList<String>();

		Pattern pattern;
		Matcher matcher;
		s = s.substring(s.indexOf("=") + 1, s.length());
		while (s.startsWith(" "))
			// Remove space at beginning
			s = s.substring(1, s.length());
		pattern = Pattern.compile(".*?[+ \n]");
		matcher = pattern.matcher(s);
		while (matcher.find()) {
			String tmp = s.substring(matcher.start(), matcher.end() - 1).trim();
			if ((tmp.compareTo("") != 0) && (!tmp.startsWith("#")))
				arch.add(tmp);
		}

		return arch;
	}

	protected void readMakefile2() {
		String strLine = null;
		try {
			File fstreamreader = file;
			BufferedReader in = new BufferedReader(new FileReader(fstreamreader));

			while ((strLine = in.readLine()) != null) {
				if (strLine.startsWith("#")) {
				} else if (strLine.startsWith("EXEC :")) {
					strLine = strLine.substring(strLine.indexOf('=') + 1);
					exec = strLine.trim();
				}

				else if (strLine.startsWith("ARCH")) {
					arch = new ArrayList<String>();
					strLine = strLine.substring(strLine.indexOf('=') + 1);
					strLine = strLine.trim();
					String chars = "";
					int i;
					for (i = 0; i < strLine.length(); i++) {

						if (strLine.charAt(i) == '+') {
							arch.add(chars);
							chars = "";
						} else {
							chars = chars.concat(Character.toString(strLine.charAt(i)));
						}

					}
					arch.add(chars);
				}

				else if (strLine.startsWith("MY_SCF")) {
					strLine = strLine.substring(strLine.indexOf('=') + 1);
					strLine = strLine.trim();

					int back = 0;
					String s = prjLoc;
					// get the project location
					while (strLine.contains("..")) {
						strLine = strLine.substring(strLine.indexOf("..") + 2);
						back++;
					}
					for (; back > 0; back--) {
						s = s.substring(0, s.lastIndexOf("\\"));
					}
					my_scf = s + strLine;
				} else if (strLine.startsWith("OWN_INIT_S")) {
					strLine = strLine.substring(strLine.indexOf('=') + 1);
					strLine = strLine.trim();
					own_init_s = prjLoc + "/" + strLine;
				} else if (strLine.startsWith("OWN_ASFLAGS")) {
					strLine = strLine.substring(strLine.indexOf('=') + 1);
					if (!strLine.equals("")) {
						strLine = strLine.trim();
						own_asflags = strLine;
						// own_asflags.add(strLine);
					} // un seul valeur pour cet attribut?
				}

				else if (strLine.startsWith("OWN_CFLAGS")) {
					strLine = strLine.substring(strLine.indexOf('=') + 1);
					if (!strLine.equals("")) {
						own_cflags = strLine.trim();
					}
				}

				else if (strLine.startsWith("OWN_LKFLAGS")) {
					strLine = strLine.substring(strLine.indexOf('=') + 1);
					if (!strLine.equals("")) {
						own_lkflags = strLine.trim();

					}
				} else if (strLine.startsWith("OWN_ARFLAGS")) {
					strLine = strLine.substring(strLine.indexOf('=') + 1);
					if (!strLine.equals("")) {
						own_arflags = strLine.trim();

					}
				} else if (strLine.startsWith("OWN_DISFLAGS")) {
					strLine = strLine.substring(strLine.indexOf('=') + 1);
					if (!strLine.equals("")) {
						own_disflags = strLine.trim();

					}
				} else if (strLine.startsWith("ITCM_BASE_ADDRESS")) {
					strLine = strLine.substring(strLine.indexOf('=') + 1);
					// if (!strLine.equals("")) {
					strLine = strLine.trim();
					itcm_base_address = strLine;// Long.decode(strLine);
					// }
				}

				else if (strLine.startsWith("DTCM_BASE_ADDRESS")) {
					strLine = strLine.substring(strLine.indexOf('=') + 1);
					// if (!strLine.equals("")) {
					strLine = strLine.trim();
					dtcm_base_address = strLine;// Long.decode(strLine);
					// }
				} else if (strLine.startsWith("ROOTDIR")) {
					strLine = strLine.substring(strLine.indexOf('=') + 1);
					strLine = strLine.trim();
					int back = 0;
					String s = prjLoc;

					// get the project location
					while (strLine.contains("..")) {
						strLine = strLine.substring(strLine.indexOf("..") + 2);
						back++;
					}
					for (; back > 0; back--) {
						s = s.substring(0, s.lastIndexOf("\\"));
					}
					rootDir = s;

				} else if (strLine.startsWith("SRCS") || (strLine.contains("$(ROOTDIR)") && flag == Flag.SRCS)) {

					// srcs = new ArrayList<String>();
					if (strLine.startsWith("SRCS")) {
						flag = Flag.SRCS;
						strLine = strLine.substring(strLine.indexOf('=') + 1);
						if (strLine.indexOf('\\') != -1) {
							strLine = strLine.substring(0, strLine.indexOf('\\'));
						}

					}
					if (strLine.contains("$(ROOTDIR)")) {
						strLine = strLine.trim();
						strLine = strLine.replace("$(ROOTDIR)", rootDir);
						if (strLine.indexOf('\\') != -1) {
							strLine = strLine.substring(0, strLine.indexOf('\\'));
						} else {
							flag = Flag.NULL;
						}
					}
					strLine = strLine.trim();
					srcs.add(strLine);

				}

				else if (strLine.startsWith("VPATH") || (strLine.contains("$(ROOTDIR") && flag == Flag.VPATH)) {
					if (strLine.startsWith("VPATH")) {
						flag = Flag.VPATH;
						strLine = strLine.substring(strLine.indexOf('=') + 1);

					}
					if (strLine.contains("$(ROOTDIR)")) {
						strLine = strLine.replace("$(ROOTDIR)", rootDir);
						if (strLine.indexOf('\\') != -1) {
							strLine = strLine.substring(0, strLine.indexOf('\\'));
						} else {
							flag = Flag.NULL;
						}
					}
					strLine = strLine.trim();
					vpath.add(strLine);

				}

				else if (strLine.startsWith("SRCDIR") || (strLine.startsWith("$(ROOTDIR)") && flag == Flag.SRCDIR)) {
					srcdir = new ArrayList<String>();
					if (strLine.startsWith("SRCDIR")) {
						flag = Flag.SRCDIR;
						strLine = strLine.substring(strLine.indexOf('=') + 1);

					}
					if (strLine.contains("$(ROOTDIR)")) {
						strLine = strLine.replace("$(ROOTDIR)", rootDir);
						if (strLine.indexOf('\\') != -1) {
							strLine = strLine.substring(0, strLine.indexOf('\\'));
						}
					}
					strLine = strLine.trim();
					srcdir.add(strLine);

				} else if (strLine.startsWith("INCDIR")) {
					incdir = new ArrayList<String>();
					if (strLine.startsWith("INCDIR")) {
						flag = Flag.INCDIR;
						strLine = strLine.substring(strLine.indexOf('=') + 1);
					}
					if (strLine.contains("$")) // resolve the variable
					{
						String variable = strLine.substring(strLine.indexOf('(') + 1, strLine.indexOf(')'));
						String lower = variable.toLowerCase();
						for (Field f : ParserMakefile.class.getFields()) {
							if (lower.equals(f.getName())) {
								// Type type = f.getGenericType();
								Method method = ParserMakefile.class.getMethod("get" + lower.toUpperCase().charAt(0) + lower.substring(1), (Class<?>) null);
								Object o = method.invoke(this, (Object[]) null);
								if (o instanceof ArrayList<?>) {
									for (Object element : (ArrayList<?>) o) {
										incdir.add((String) element);
									}
									// incdir = new
									// ArrayList<String>((ArrayList<String>)o);
									// System.out.println(((ArrayList<String>)
									// o).get(0).toString());
								}
							}

						}

					}

				}

				else if (strLine.startsWith("LIBUNWANTED")) {
					strLine = strLine.substring(strLine.indexOf('='));
					if (!strLine.equals("")) {
						libunwanted.addAll(Arrays.asList(strLine.split(" ")));

					}
				} else if (strLine.startsWith("FORBIDDEN_DEFINES")) {
					strLine = strLine.substring(strLine.indexOf('='));
					if (!strLine.equals("")) {
						forbidden_defines.addAll(Arrays.asList(strLine.split(" ")));
					}
				} else if (strLine.startsWith("EXEC_PATH")) {
					strLine = strLine.substring(strLine.indexOf('='));
					if (strLine.startsWith("$(ROOTDIR)")) {
						strLine = strLine.replace("$(ROOTDIR)", rootDir);
					}
					exec_path = strLine.trim();
				} else if (strLine.startsWith("DEFINES")) {

				}

			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

	}

	private String formatPath(String path) {
		if (path.contains(getRootDir())) {
			return path.replace(getRootDir(), "$(ROOTDIR)");
		}
		return path;
	}
	/**
	 * Return the exec value from the makefile
	 * @return The exec value from the makefile
	 */
	public String getExec() {
		return exec;
	}
	/**
	 * Set the new exec value of the makefile
	 * @param exec the new value of the exec value of the makefile
	 */
	public void setExec(String exec) {
		this.exec = exec;
	}
	/**
	 * Return the arch values of the makefile
	 * @return The arch values of the make file as a table of String
	 */
	public ArrayList<String> getArch() {
		return arch;
	}
	/**
	 * Set the new values of the arch value of the makefile
	 * @param arch the new value of the arch value in the makefile
	 */
	public void setArch(ArrayList<String> arch) {
		this.arch = arch;
	}
	/**
	 * Return the scf value of the makefile
	 * @return The scf value of the makefile
	 */
	public String getMy_scf() {
		return my_scf;
	}
	/**
	 * Set the new value of the scf value of the makefile
	 * @param myScf the new value of scf
	 */
	public void setMy_scf(String myScf) {
		my_scf = myScf;
	}
	/**
	 * Return the value of own_init_s in the makefile
	 * @return The value of own_init_s in the makefile
	 */
	public String getOwn_init_s() {
		return own_init_s;
	}
	/**
	 * Set the new value of own_init_s in the makefile
	 * @param ownInitS The new value of own_init_s in the makefile
	 */
	public void setOwn_init_s(String ownInitS) {

		this.own_init_s = ownInitS;
	}
	/**
	 * Return the own_as_flags value from the makefile
	 * @return The own_as_flags value from the makefile
	 */
	public String getOwn_asflags() {
		return own_asflags;
	}
	/**
	 * Set the new value of the own_as_flags
	 * @param ownAsflags The new value
	 */
	public void setOwn_asflags(String ownAsflags) {
		own_asflags = ownAsflags;
	}
	/**
	 * Return the value of the own_c_flags
	 * @return The value of own_c_flags
	 */
	public String getOwn_cflags() {
		return own_cflags;
	}
	/**
	 * Set the new value of the own_c_flags
	 * @param ownCflags the new value
	 */
	public void setOwn_cflags(String ownCflags) {
		own_cflags = ownCflags;
	}
	/**
	 * Return the value of the own_lk_flags
	 * @return The value of own_lk_flags
	 */
	public String getOwn_lkflags() {
		return own_lkflags;
	}

	public void setOwn_lkflags(String ownLkflags) {
		own_lkflags = ownLkflags;
	}

	/**
	 * Return the value of the own_ar_flags
	 * @return The value of own_ar_flags
	 */
	public String getOwn_arflags() {
		return own_arflags;
	}

	public void setOwn_arflags(String ownArflags) {
		own_arflags = ownArflags;
	}

	/**
	 * Return the value of the own_dis_flags
	 * @return The value of own_dis_flags
	 */
	public String getOwn_disflags() {
		return own_disflags;
	}

	public void setOwn_disflags(String ownDisflags) {
		own_disflags = ownDisflags;
	}
	/**
	 * Return the value of the itcm_base_address
	 * @return The value of itcm_base_address
	 */
	public String getItcm_base_address() {
		return itcm_base_address;
	}
	
	public void setItcm_base_address(String itcmBaseAddress) {
		itcmBaseAddress = itcmBaseAddress.substring(2);
		itcm_base_address = itcmBaseAddress;// Long.parseLong(itcmBaseAddress,
		// 16);
	}
	/**
	 * Return the value of the dtcm_base_address
	 * @return The value of dtcm_base_address
	 */
	public String getDtcm_base_address() {
		return dtcm_base_address;
	}

	public void setDtcm_base_address(String dtcmBaseAddress) {
		dtcmBaseAddress = dtcmBaseAddress.substring(2);
		dtcm_base_address = dtcmBaseAddress;// Long.parseLong(dtcmBaseAddress,16);
	}

	public ArrayList<String> getSrcs() {
		return srcs;
	}

	public void setSrcs(ArrayList<String> srcs) {
		this.srcs = srcs;
	}

	public ArrayList<String> getSrcdir() {
		return srcdir;
	}

	public void setSrcdir(ArrayList<String> srcdir) {
		this.srcdir = srcdir;
	}

	public ArrayList<String> getVpath() {
		return vpath;
	}

	public void setVpath(ArrayList<String> vpath) {
		this.vpath = vpath;
	}

	public ArrayList<String> getIncdir() {
		return incdir;
	}

	public void setIncdir(ArrayList<String> incdir) {
		this.incdir = incdir;
	}

	public ArrayList<String> getLibunwanted() {
		return libunwanted;
	}

	public void setLibunwanted(ArrayList<String> libunwanted) {
		this.libunwanted = libunwanted;
	}

	public ArrayList<String> getFobidden_defines() {
		return forbidden_defines;
	}

	public void setFobidden_defines(ArrayList<String> fobiddenDefines) {
		forbidden_defines = fobiddenDefines;
	}

	public String getExec_path() {
		return exec_path;
	}

	public void setExec_path(String execPath) {
		exec_path = execPath;
	}

	public ArrayList<String> getDefines() {
		return defines;
	}

	public void setDefines(ArrayList<String> defines) {
		this.defines = defines;
	}

	public String getRootDir() {
		return rootDir;
	}
	/**
	 * Set root dir (the directory S-Gold\S-GOLD_Software_Environment)
	 * @param rootDir The new value of root dir
	 */
	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}
	/**
	 * Set default values
	 * @param values The new default values
	 */
	public void setDefautMKValues(ArrayList<String> values) {
		this.defautMKValues = values;
	}
	/**
	 * For test purpose
	 * @param args The input arguments
	 */
	public static void main(String args[]) {
		String file = "Z:\\S-Gold\\S-GOLD_Family_Environment\\Testcases\\CC_test\\CC_ALL_RAM\\makefile";
		ParserMakefile parser = new ParserMakefile(file);
		parser.readMakefile();
		if (parser.getSrcdir().size() != 0)
			System.out.println("SRCDIR=" + parser.getSrcdir().toString());
		System.out.println("OWN_INIT_S=" + parser.getOwn_init_s().toString());
		System.out.println("ARCH=" + parser.getArch().toString());
		if (parser.getIncdir().size() != 0)
			System.out.println("INCDIR=" + parser.getIncdir().toString());
		if (parser.getVpath().size() != 0)
			System.out.println("VPATH=" + parser.getVpath().toString());
		if (parser.getSrcs().size() != 0)
			System.out.println("SRCS=" + parser.getSrcs().toString());
		System.out.println("EXEC=" + parser.getExec().toString());
		System.out.println("ITCM_BASE_ADDRESS=" + parser.getItcm_base_address().toString());
		System.out.println("FORBIDDEN_DEFINES=" + parser.getFobidden_defines().toString());
	}

}

class StreamGobbler extends Thread {
	InputStream is;
	String type;
	StringBuffer output;

	StreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;
		output = new StringBuffer(3000); 
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
				output.append(line + "\n");
				
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}	
	}
	public String getOutput() {
		return output.toString();
	}
	
}
