package com.infineon.cv;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.envvar.EnvironmentVariable;
import org.eclipse.cdt.core.envvar.IContributedEnvironment;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.envvar.IEnvironmentVariableManager;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.CIncludePathEntry;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICFolderDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSetting;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSettingEntry;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * InfineonPreferencePage allows to change the tool view drive.
 * 
 * @author zhaoxi
 * 
 */
public class InfineonPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	static String[] EnvVar = { "Path", "ARMINC", "ARMLIB", "RVCT31BIN", "RVCT31INC", "RVCT31LIB", "ARMCONF", "ARMDLL", "ARMHOME" };
	protected DirectoryFieldEditor toolDir;

	/**
	 * The constructor
	 * Creates a preference store
	 */
	public InfineonPreferencePage() {
		super(FieldEditorPreferencePage.GRID);

		// Set the preference store for the preference page.
		IPreferenceStore store = InfineonActivator.getDefaut().getPreferenceStore();// JFacePreferences.getPreferenceStore();
		setPreferenceStore(store);

	}

	@SuppressWarnings("deprecation")
	protected void createFieldEditors() {
		toolDir = new DirectoryFieldEditor(PreferenceConstants.INFINEON_TOOLVIEW_DRIVE, "Tool Drive", getFieldEditorParent());
		toolDir.setPreferencePage(this);
		toolDir.setPreferenceStore(getPreferenceStore());
		toolDir.load();
		addField(toolDir);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	protected void performDefauts() {
		toolDir.loadDefault();
		super.performDefaults();
	}

	public boolean performOk() { // set environment variables' tool drive to be
		// // "toolDir.getStringValue()"
		System.out.println(toolDir.getStringValue());
		redefineToolDrive(new ProcessBuilder());
		// ToolViewDrive.set(toolDir.getStringValue().charAt(0));
		toolDir.store();
		return super.performOk();
	}

	private String changeDrive(String oldpath) {
		char[] newpath = new char[oldpath.length()];
		char oldDrive = oldpath.charAt(0);
		int i = 0;
		char c = 0;
		for (i = 0; i < oldpath.length(); i++) {
			c = oldpath.charAt(i);
			if (c == oldDrive && i + 1 < oldpath.length()) {
				if (oldpath.charAt(i + 1) == ':') {
					c = toolDir.getStringValue().charAt(0);
				}
			}
			newpath[i] = c;
		}
		return String.valueOf(newpath);
	}

	private void redefineToolDrive(ProcessBuilder processBuilder) {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject[] projects = workspace.getRoot().getProjects();
		ICProjectDescription prjDesc;
		for (IProject project : projects) {
			prjDesc = CoreModel.getDefault().getProjectDescription(project);
			ICConfigurationDescription desc = prjDesc.getActiveConfiguration();

			// modify env variable "Path"
			IEnvironmentVariableManager envManager = CCorePlugin.getDefault().getBuildEnvironmentManager();
			IContributedEnvironment contribEnv = envManager.getContributedEnvironment();
			IEnvironmentVariable[] paths = envManager.getVariables(prjDesc.getActiveConfiguration(), true);
			String oldpath = null;
			ArrayList<String> envVarList = new ArrayList<String>(Arrays.asList(EnvVar));
			for (IEnvironmentVariable path : paths) {
				if (envVarList.contains(path.getName())) {
					oldpath = path.getValue();
					String newpath = changeDrive(oldpath);
					final IEnvironmentVariable var = new EnvironmentVariable(path.getName(), newpath);
					for (ICConfigurationDescription iconf : prjDesc.getConfigurations()) {
						contribEnv.addVariable(var, iconf);
					}
					// break;
				}
			}

			// Try setting an environment variable

			// modify include file
			for (ICFolderDescription filedes : desc.getFolderDescriptions()) {
				for (ICLanguageSetting lang : filedes.getLanguageSettings())// ;//.getLanguageSettings();
				{
					int i = 0;
					ICLanguageSettingEntry[] entries = lang.getSettingEntries(INFORMATION);
					for (ICLanguageSettingEntry entry : entries) {
						if (entry.getName().startsWith(String.valueOf(oldpath.charAt(0))))// oldpath.charAt(0))))
						{
							// ICLanguageSettingEntry[] newEntries = new
							// ICLanguageSettingEntry[entries.length];
							entries[i] = new CIncludePathEntry(entry.getName().replaceFirst(String.valueOf(oldpath.charAt(0)), String.valueOf(toolDir.getStringValue().charAt(0))), 0);
						}
						i++;
					}
					lang.setSettingEntries(ICSettingEntry.INCLUDE_PATH, entries);
				}
			}

			// Get an environment variable:
			// IEnvironmentVariable var2 = envManager.getVariable(var.getName(),
			// prjDesc.getConfigurationById(id1), true);

			// Save the changes to the environment
			try {
				CoreModel.getDefault().setProjectDescription(project, prjDesc);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
}
