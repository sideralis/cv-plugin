package com.infineon.cv.wizard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.CMacroEntry;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICFolderDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSetting;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.core.settings.model.ICSettingEntry;
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
import com.infineon.cv.makefile.parser.MakefileData;

@SuppressWarnings("restriction")
public class IntelProjectManager {
	/**
	 * See http://cdt-devel-faq.wikidot.com/#toc25
	 * 
	 * @param name
	 * @param path
	 * @param projectType
	 * @param monitor
	 */
	public void createIntelProject(String name, String path, int projectType, IProgressMonitor monitor) {
		IProjectType projType = null;
		IToolChain toolChain = null;

		// First check if makefile exist
		File makefile = new File(path + "\\makefile");
		if (!makefile.exists()) {
			// We need to create the makefile
			createMakefile(makefile, path);
			createCfile(name, path);
		}
		// Parse makefile
		MakefileData.parse(makefile);
		
		// Get workspace in order to create a project
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

			if (projectType == IntelWizardPage.TESTCASE) {
				projType = ManagedBuildManager.getExtensionProjectType("com.infineon.cv.projectTypeBHades");
				toolChain = ManagedBuildManager.getExtensionToolChain("com.infineon.cv.toolChain.hadesCompile");
			} else if (projectType == IntelWizardPage.LIBRARY) {
				projType = ManagedBuildManager.getExtensionProjectType("com.infineon.cv.projectTypeCHadesLib");
				toolChain = ManagedBuildManager.getExtensionToolChain("com.infineon.cv.toolChain.hadesLibGnumake");
			}
			if (projType != null && toolChain != null) {
				ManagedProject mProj = new ManagedProject(cdtProj, projType);
				info.setManagedProject(mProj);

				IConfiguration[] configs = ManagedBuildManager.getExtensionConfigurations(toolChain, projType);

				for (IConfiguration icf : configs) {
					if (!(icf instanceof Configuration)) {
						continue;
					}
					Configuration cf = (Configuration) icf;

					String id = ManagedBuildManager.calculateChildId(cf.getId(), null);
					Configuration config = new Configuration(mProj, cf, id, false, true);

					ICConfigurationDescription cfgDes = des.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, config.getConfigurationData());
					// Add define
					{
						ArrayList<String> symbols = new ArrayList<String>();
//						ArrayList<String> values = new ArrayList<String>();
						symbols.add("XGOLD223");
						symbols.add("DEBUG");
						symbols.add("STD_IO_USIF");
						Object[] symbolsAdd = symbols.toArray();
						for (ICFolderDescription fileDesc : cfgDes.getFolderDescriptions()) {
							for (ICLanguageSetting lang : fileDesc.getLanguageSettings())// ;//.getLanguageSettings();
							{
								ICLanguageSettingEntry[] newEntries = new ICLanguageSettingEntry[symbolsAdd.length];
								int i = 0;
								for (String symbol : symbols) {
									newEntries[i++] = new CMacroEntry(symbol, "1", 0);
								}
								lang.setSettingEntries(ICSettingEntry.MACRO, newEntries);
							}
						}
					}
					
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
			}

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
		m = p.matcher(IntelProjectTemplate.cFileTestcase);
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
			output = new BufferedWriter(new FileWriter(path + "\\" + name + ".c"));
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
		Matcher m = p.matcher(IntelProjectTemplate.makefileTestcase);
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
