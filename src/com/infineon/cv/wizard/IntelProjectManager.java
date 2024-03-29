package com.infineon.cv.wizard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
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
import org.eclipse.jface.dialogs.MessageDialog;

import com.infineon.cv.makefile.parser.MakefileData;
import com.infineon.cv.nature.ToggleNature;

/**
 * 
 * @author gautier
 * 
 */
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
			createMakefile(makefile, path, projectType);
			createCfile(name, path);
		}

		// Secondly check if eclipse project exist already
		File eclipse = new File(path + "\\.cproject");
		if (eclipse.exists()) {
			// The project has been created already, let's ask the user if he
			// wants to overwrite it!
			// TODO: this does not work correctly as some linked folders are not added if we say yes.
			boolean answer = MessageDialog.openQuestion(null, "Eclipse project already exists", "Do you want to overwrite the existing eclipse project?\n"
					+ "If not, press no and use the File\\Import... menu, then General\\Existing projects into Workspace to open the existing project");
			if (answer == false)
				return;
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

			switch (projectType) {
			case IntelWizardPage.TESTCASE:
				projType = ManagedBuildManager.getExtensionProjectType("com.infineon.cv.projectTypeBHades");
				toolChain = ManagedBuildManager.getExtensionToolChain("com.infineon.cv.toolChain.hadesCompile");
				break;
			case IntelWizardPage.LIBRARY:
				projType = ManagedBuildManager.getExtensionProjectType("com.infineon.cv.projectTypeCHadesLib");
				toolChain = ManagedBuildManager.getExtensionToolChain("com.infineon.cv.toolChain.hadesLibGnumake");
				break;
			case IntelWizardPage.BCO_TESTCASE:
				projType = ManagedBuildManager.getExtensionProjectType("com.infineon.cv.projectTypeDHadesBCOTC");
				toolChain = ManagedBuildManager.getExtensionToolChain("com.infineon.cv.toolChain.hadesBCOGnumake");
				break;
			case IntelWizardPage.ML_LOADER:
				projType = ManagedBuildManager.getExtensionProjectType("com.infineon.cv.projectTypeEHadesMemloader");
				toolChain = ManagedBuildManager.getExtensionToolChain("com.infineon.cv.toolChain.hadesMemloaderGnumake");
				break;
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
						int size = MakefileData.getDefines().size();
						HashMap<String, String> defines = MakefileData.getDefines();
						Set<String> keys = defines.keySet();

						for (ICFolderDescription fileDesc : cfgDes.getFolderDescriptions()) {
							for (ICLanguageSetting lang : fileDesc.getLanguageSettings())// ;//.getLanguageSettings();
							{
								ICLanguageSettingEntry[] newEntries = new ICLanguageSettingEntry[size];
								int i = 0;
								Iterator<String> ite = keys.iterator();
								while (ite.hasNext()) {
									String key = ite.next();
									newEntries[i++] = new CMacroEntry(key, defines.get(key), 0);
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
	/**
	 * 
	 * @param name
	 * @param path
	 */
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
			e.printStackTrace();
		}
	}
	/**
	 * @param makefile
	 * @param path
	 * @param projectType 
	 */
	private void createMakefile(File makefile, String path, int projectType) {
		String res;
		BufferedWriter output;
		IPath myPath;
		Pattern p;
		Matcher m;

		// Replace Testcase_name
		p = Pattern.compile("TESTCASE_NAME");
		m = p.matcher(IntelProjectTemplate.makefileTestcase);
		myPath = new Path(path);
		String nameOfFile = myPath.lastSegment();
		res = m.replaceAll(nameOfFile);

		// Replace ROOT_DIR
		p = Pattern.compile("ROOT_DIR");
		m = p.matcher(res);
		String rootDir = findRootDir(path);
		res = m.replaceAll(rootDir);

		// Replace REAL
		p = Pattern.compile("REAL");
		m = p.matcher(res);
		if (projectType == IntelWizardPage.BCO_TESTCASE)
			res = m.replaceAll("BCOTC");
		else if (projectType == IntelWizardPage.ML_LOADER)
			res = m.replaceAll("BCOML");
		
		try {
			output = new BufferedWriter(new FileWriter(makefile));
			output.write(res);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Find the root directory, e.g the base folder, there is nothing interesting below.
	 * Practically, go down in the folder hierarchy and stop once _makefile folder is found.
	 * @param path the path of the project location
	 * @return a relative path from the project location to root dir (ex: ../..), or Root_dir_not_found if the root dir was not found.
	 */
	private String findRootDir(String path) {
		String rootDir = "" ;
		int level = 0;
		int pos=0;
		File makefile;
		// Depending on the project, find some particular folder
		// If CV testcase or libraries, must find _makefile
		// If memloader, must find _makefile
		// If boot code must find _makefile
		makefile = new File(path + "\\_makefile");
		while (!makefile.exists() && pos!=-1) {
			level += 1;
			pos = path.lastIndexOf("\\");
			if (pos != -1) {
				path = path.substring(0,pos);
				makefile = new File(path + "\\_makefile");
			}			
		}
		if (makefile.exists()) {
			for (int i=0;i<level-1;i++)
				rootDir = rootDir.concat("../");
			rootDir = rootDir.concat("..");
		} else {
			rootDir = "Root_dir_not_found";
		}
		return rootDir;
	}
}
