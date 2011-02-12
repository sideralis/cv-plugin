package com.infineon.cv.wizard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;

import com.infineon.cv.ToggleNature;

@SuppressWarnings("restriction")
public class IntelProjectManager {
	String make = new String(
			  "#------------------------------------------------------------------------------\n" 
			+ "# Specify the name of your project.\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "EXEC := TESTCASE_NAME\n" 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# Specify the default architecture (type gnumake list in the cmd line for help)\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "ARCH = REAL+IRAM+STD_IO_USIF+DEBUG+GENERIC_SDRAM_AND_NO_NVM\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# Specify below your own scatter file if needed\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "MY_SCF =\n" 
			+ "#------------------------------------------------------------------------------\n"
			+ "# Specify below your own initialization file if needed\n" 
			+ "#------------------------------------------------------------------------------\n" 
			+ "OWN_INIT_S =\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# Specify below your own assembler flags if needed\n"
			+ "# WARNING : Keep in mind to get out compilation flags which have to be replaced\n" 
			+ "# by your own flags... by instance use $(filter-out OLD_FLAGS,$(ASFLAGS))\n"
			+ "# OWN_ASFLAGS 	-> Assembler flags\n" 
			+ "# OWN_CFLAGS	-> C compiler flags\n" 
			+ "# OWN_LKFLAGS	-> Linker flags\n" 
			+ "# OWN_ARFLAGS	-> Archiver flags\n"
			+ "# OWN_DISFLAGS	-> Disassembler flags\n" 
			+ "#------------------------------------------------------------------------------\n" 
			+ "OWN_ASFLAGS  =\n" 
			+ "OWN_CFLAGS   =\n"
			+ "OWN_LKFLAGS  =\n" 
			+ "OWN_ARFLAGS  =\n" 
			+ "OWN_DISFLAGS =\n" 
			+ "\n" 
			+ "#------------------------------------------------------------------------------\n"
			+ "# By default, the TCM is not used.\n" + "# In case of it has to be used :\n" 
			+ "#			- Hard code the address value of ITCM,DTCM in the current makefile\n"
			+ "#			- Set theirs variable values in the command line\n" 
			+ "#------------------------------------------------------------------------------\n" 
			+ "ITCM_BASE_ADDRESS =\n"
			+ "DTCM_BASE_ADDRESS =\n" 
			+ "\n" 
			+ "#------------------------------------------------------------------------------\n"
			+ "# Specify the location of your S-Gold_sofware_environment folder\n" 
			+ "#------------------------------------------------------------------------------\n" 
			+ "ROOTDIR := ../..\n" 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# You can add specific file to compile which are not in the current folder\n"
			+ "# All source files (.c,.asm,.c) located in the folder are automatically added\n" 
			+ "# to the source file list to compile\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "SRCS += TESTCASE_NAME.c\n" 
			+ "VPATH += " 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# You can add a folder on which all sources files located in it will be added\n"
			+ "# to the project\n" 
			+ "#------------------------------------------------------------------------------\n" 
			+ "SRCDIR +=\n" + "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# You can add a folder in the include path\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "#INCDIR +=\n" 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# You can exclude a lib from the project\n" 
			+ "#\n" 
			+ "# Example :\n"
			+ "# 			LIBUNWANTED += SCCU.lib\n" 
			+ "#------------------------------------------------------------------------------\n" 
			+ "#LIBUNWANTED += USIF.lib\n" 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# Call the main makefile\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "include $(ROOTDIR)/_makefile/Makefile.mak\n" 
			+ "");
	String test = new String(
			"/*\n" + 
			" * TESTCASE_NAME.c\n" + 
			" *\n" + 
			" *  Created on: DD MM YYYY\n" + 
			" *      Author: USERNAME\n" + 
			" */\n" + "\n" + 
			"#include <stdio.h>\n" + 
			"#include <STD_stdio.h>\n" + 
			"\n" + 
			"#include <REG_SGOLD.h>\n" + 
			"\n" + 
			"#include <CGU_lib.h>\n" + 
			"#include <TEST_lib.h>\n" + 
			"#include <HADES_lib.h>\n" + 
			"\n" + 
			"/**\n" + 
			" * Main function\n" + 
			" * @return always 0.\n" + 
			" */\n" +
			"int main(void) {\n" + 
			"	int ret = 0;\n" + 
			"\n" + 
			"	// Configure clock to fast clock settings\n" + 
			"	CGU_ConfAllClocks(FAST_CLOCKS_SETTINGS);\n" + 
			"\n" + 
			"	// Initialize USIF port\n" +
			"	STD_IO_Init(STD_IO_115200);\n" + 
			"\n" + 
			"	// Fill the Hades data used for report\n" + 
			"	HADES_QCBlock(\"Block name in QC\");\n" + 
			"	HADES_QCName(\"Test name in QC\");\n" +
			"	HADES_TestDescription(\"Description of your test\");\n" + 
			"\n" + 
			"	// TODO Add the code of your test\n" + 
			"	// ...\n" + 
			"\n" + 
			"	// Send the pass or fail status of your test.\n" +
			"	TEST_sendTcVerdict(TEST_PASS);\n" + 
			"\n" + 
			"	// Return 0\n" + 
			"	return 0;\n" + 
			"}\n" + 
			"");

	/**
	 * See http://cdt-devel-faq.wikidot.com/#toc25
	 * 
	 * @param name
	 * @param path
	 * @param monitor
	 */
	@SuppressWarnings("restriction")
	public void createIntelProject(String name, String path, IProgressMonitor monitor) {
		// First check if makefile exist
		File makefile = new File(path + "\\makefile");
		if (!makefile.exists()) {
			// We need to create the makefile
			createMakefile(makefile, path);
			createCfile(name, path);
		}
		IWorkspaceRoot wrkSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject newProjectHandle = wrkSpaceRoot.getProject(name);
		monitor.beginTask("Creating Intel project", 0);
		IProjectDescription projDesc = ResourcesPlugin.getWorkspace().newProjectDescription(newProjectHandle.getName());
		if (!("".equals(path)) && path != null) {
			Path myPath = new Path(path);
			projDesc.setLocation(myPath);
		}
		try {
			IProject cdtProj = CCorePlugin.getDefault().createCDTProject(projDesc, newProjectHandle, monitor);

			CProjectNature.addCNature(cdtProj, monitor);
			ICProjectDescriptionManager mgr = CoreModel.getDefault().getProjectDescriptionManager();
			ICProjectDescription des = mgr.getProjectDescription(cdtProj, true);
			if (des != null)
				return; // C project description already exists
			des = mgr.createProjectDescription(cdtProj, true);

			ManagedBuildInfo info = ManagedBuildManager.createBuildInfo(cdtProj);
			IProjectType projType = ManagedBuildManager.getExtensionProjectType("com.infineon.cv.projectTypeBHades");
			IToolChain toolChain = ManagedBuildManager.getExtensionToolChain("com.infineon.cv.toolChain.hadesCompile");

			ManagedProject mProj = new ManagedProject(cdtProj, projType);
			info.setManagedProject(mProj);

			// IConfiguration[] configs = projType.getConfigurations(); // ***
			// BG
			IConfiguration[] configs = ManagedBuildManager.getExtensionConfigurations(toolChain, projType);

			for (IConfiguration icf : configs) {
				if (!(icf instanceof Configuration)) {
					continue;
				}
				Configuration cf = (Configuration) icf;

				String id = ManagedBuildManager.calculateChildId(cf.getId(), null);
				Configuration config = new Configuration(mProj, cf, id, false, true);

				ICConfigurationDescription cfgDes = des.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, config.getConfigurationData());
				config.setConfigurationDescription(cfgDes);
				config.exportArtifactInfo();

				IBuilder bld = config.getEditableBuilder();
				if (bld != null) {
					bld.setManagedBuildOn(true);
				}

				config.setName(config.getName());
				config.setArtifactName(cdtProj.getName());

			}
			mgr.setProjectDescription(cdtProj, des);

			// Add Intel project nature
			new ToggleNature(cdtProj).start();

		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void createCfile(String name, String path) {
		String res;
		BufferedWriter output;
		IPath myPath;
		Pattern p;
		Matcher m;

		p = Pattern.compile("TESTCASE_NAME");
		m = p.matcher(test);
		myPath = new Path(path);
		String nameOfFile = myPath.lastSegment();
		res = m.replaceAll(nameOfFile);
		
		p = Pattern.compile("USERNAME");
		m = p.matcher(res);
		res = m.replaceAll(System.getProperty("user.name"));
		
		p = Pattern.compile("DD MM YYYY");
		m = p.matcher(res);
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		String df = DateFormat.getDateInstance().format(date);
		res = m.replaceAll(df);
		
		try {
			output = new BufferedWriter(new FileWriter(path+"\\"+name+".c"));
			output.write(res);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	private void createMakefile(File makefile, String path) {
		String res;
		BufferedWriter output;
		IPath myPath;

		Pattern p = Pattern.compile("TESTCASE_NAME");
		Matcher m = p.matcher(make);
		myPath = new Path(path);
		String nameOfFile = myPath.lastSegment();
		res = m.replaceAll(nameOfFile);

		try {
			output = new BufferedWriter(new FileWriter(makefile));
			output.write(res);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
