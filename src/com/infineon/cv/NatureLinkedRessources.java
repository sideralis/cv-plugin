package com.infineon.cv;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.infineon.cv.makefile.parser.MakefileData;

/**
 * NatureLinkedRessources implements IProjectNature, it adds linked files
 * according to the project type.
 **/
@SuppressWarnings( { "unchecked", "serial" })
public class NatureLinkedRessources implements IProjectNature {
	/** The ID of this project nature */
	public static final String NATURE_ID = InfineonActivator.PLUGIN_ID + ".NatureLinkedRessources";

	private IProject project;

	private static final Map<String, String[]> libsrcPaths = new HashMap() {
		{
			put("CVTC", new String[] { "base S-Gold/S-GOLD_Family_Environment/_base", "lib S-Gold/S-GOLD_Family_Environment/_lib/_src", "halix S-Gold/S-GOLD_Family_Environment/_halix/_src" });
		}
		{
			put("CVLib", new String[] { "lib S-Gold/S-GOLD_Family_Environment/_lib/_src", "halix S-Gold/S-GOLD_Family_Environment/_halix/_src" });
		}
		{
			put("CVMem", new String[] { "cgu IFX_Tools/MemLoader/C_ASM/Target/SG/NOR Flash/_lld/CGU", "ebu IFX_Tools/MemLoader/C_ASM/Target/SG/NOR Flash/_lld/EBU",
					"emmc IFX_Tools/MemLoader/C_ASM/Target/SG/NOR Flash/_lld/eMMC", "pmu IFX_Tools/MemLoader/C_ASM/Target/SG/NOR Flash/_lld/PMU",
					"com IFX_Tools/MemLoader/C_ASM/Target/SG/NOR Flash/_lld/SerialInterface", "lld IFX_Tools/MemLoader/C_ASM/Target/SG/NOR Flash/_lld",
					"base IFX_Tools/MemLoader/C_ASM/Target/SG/NOR Flash/_base" });
		}
		{
			put("Bootcode", new String[] { "base S-Gold-Bootcode/S-GOLD/Target/base", "bs S-Gold-Bootcode/S-GOLD/Target/bs/src", "drv_mem S-Gold-Bootcode/S-GOLD/Target/drv_mem/src",
					"hal_src S-Gold-Bootcode/S-GOLD/Target/hal/src", "hal_inc S-Gold-Bootcode/S-GOLD/Target/hal/inc", "sc S-Gold-Bootcode/S-GOLD/Target/sc/src",
					"brl S-Gold-Bootcode/S-GOLD/Target/brl/src" });
		}
		{
			put("URFET", new String[] { "halix S-Gold/S-GOLD_Family_Environment/_halix/_src", "lib S-Gold/S-GOLD_Family_Environment/_lib/_src", "base S-Gold/S-GOLD_Family_Environment/_base",
					"fmr_hld fmr_cv/fmr_xg223/FMR_HLD", "fmr_lld fmr_cv/fmr_xg223/FMR_LLD" });
		}
		{
			put("Crypto", new String[] { "base S-Gold/S-GOLD_Family_Environment/_base", "lib S-Gold/S-GOLD_Family_Environment/_lib/_src", "halix S-Gold/S-GOLD_Family_Environment/_halix/_src",
					"crypto_hal CRYPTO/S-GOLD_Family_Environment/_halix_CV/_src", "crypto_all CRYPTO/S-GOLD_Family_Environment/Testcases/CRYPTO_test/CRYPTO_TC_All" });
		}
		{
			put("hades", new String[] { "base S-Gold/S-GOLD_Family_Environment/_base", "lib S-Gold/S-GOLD_Family_Environment/_lib/_src", "halix S-Gold/S-GOLD_Family_Environment/_halix/_src" });
		}
	};

	/**
	 * Called when a nature is added to a project (by using
	 * project.setDescription)
	 */
	@Override
	public void configure() throws CoreException {
		System.out.println("I am adding a project nature (BG)");
		// Code example for Job is coming from:
		// http://blog.eclipse-tips.com/2009/02/using-progress-bars.html
		Job jobLinks = new Job("Adding links and includes...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Adding libraries and include links...", 100);
				
				// Add default links depending on project type
				try {
					java.util.Map<QualifiedName, String> properties = project.getPersistentProperties();
					String conf = properties.get(new QualifiedName("org.eclipse.cdt.core", "activeConfiguration"));
					if (conf != null)
						addLinks(conf, monitor);
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
				// Add source folder links extracted from makefile
				for (String s : MakefileData.getSourceDir()) {
					String name;
					IPath linkLocation;
					IPath projectLocation = getProject().getLocation();
					linkLocation = projectLocation.append(s);

					if (!linkLocation.toString().equals(projectLocation.toString())) {
						name = linkLocation.lastSegment();
						IWorkspace workspace = ResourcesPlugin.getWorkspace();
						IFolder folder = project.getFolder(name);
						if (workspace.validateLinkLocation(folder, linkLocation).getSeverity() != IStatus.ERROR) {
							try {
								folder.createLink(linkLocation, IResource.NONE, null);
							} catch (CoreException e) {
								e.printStackTrace();
							}
						} else {
							System.out.println(workspace.validateLinkLocation(folder, linkLocation).toString());
						}
					}
				}
				// Add include folder links extracted from makefile
				for (String s : MakefileData.getIncludeDir()) {
					String name;
					IPath linkLocation;
					IPath projectLocation = getProject().getLocation();
					linkLocation = projectLocation.append(s);

					if (!linkLocation.toString().equals(projectLocation.toString())) {
						name = linkLocation.lastSegment();
						IWorkspace workspace = ResourcesPlugin.getWorkspace();
						IFolder folder = project.getFolder(name);
						if (workspace.validateLinkLocation(folder, linkLocation).getSeverity() != IStatus.ERROR) {
							try {
								folder.createLink(linkLocation, IResource.NONE, null);
							} catch (CoreException e) {
								e.printStackTrace();
							}
						} else {
							System.out.println(workspace.validateLinkLocation(folder, linkLocation).toString());
						}
					}
				}
				
				// Adding includes extracted from makefile
				// TODO: this code below is not working
				/*
				ICProjectDescription prjDesc = CoreModel.getDefault().getProjectDescription(project);
				ICConfigurationDescription desc = prjDesc.getActiveConfiguration();

				ICFolderDescription filedes = desc.getRootFolderDescription(); 
				for (ICLanguageSetting lang : filedes.getLanguageSettings())// ;//.getLanguageSettings();
				{
					int nbDefaultEntries = 0;
					ICLanguageSettingEntry[] entries = lang.getSettingEntries(ICSettingEntry.INCLUDE_PATH);
					// Count how many default entries
					for (ICLanguageSettingEntry entry : entries) {
						if (entry.getName().indexOf("${") != -1)
							nbDefaultEntries++;
					}
					ICLanguageSettingEntry[] newEntries = new ICLanguageSettingEntry[includeDir.size()+nbDefaultEntries];
					// Create new entries by copying default entry
					int nbNewEntries = 0;
					for (ICLanguageSettingEntry entry : entries) {
						if (entry.getName().indexOf("${") != -1) {
							newEntries[nbNewEntries++] = entry;
						}
					}
					// Create new entries by adding new entries found in makefile
					for (String s : includeDir) {
						ICIncludePathEntry entry = (ICIncludePathEntry)CDataUtil.createEntry(ICLanguageSettingEntry.INCLUDE_PATH, s, s, null, 0);
						newEntries[nbNewEntries++] = entry;
					}
					
					lang.setSettingEntries(ICSettingEntry.INCLUDE_PATH, newEntries);
				}
				*/

				monitor.done();
				return Status.OK_STATUS;
			}
		};
		
		jobLinks.schedule();
	}

	/**
	 * Add links to the project
	 * 
	 * @param conf
	 *            The configuration which is used
	 * @param monitor
	 *            A reference to the monitor object to gauge progress
	 */
	private void addLinks(String conf, IProgressMonitor monitor) {
		for (String key : libsrcPaths.keySet()) {
			if (conf.contains(key)) {
				conf = key;
				break;
			}
		}
		for (String libpath : libsrcPaths.get(conf)) {
			int pos = libpath.indexOf(" ");
			addLink(libpath.substring(0, pos), libpath.substring(pos + 1, libpath.length()));
			monitor.worked(20);
		}

	}

	/**
	 * TODO: to be completed.
	 */
	@Override
	public void deconfigure() throws CoreException {
		System.out.println("I am removing a project nature (BG)");
	}

	@Override
	public IProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;

	}

	/**
	 * Add a linked folder to the project
	 * 
	 * @param linkfile
	 *            name of the link
	 * @param linkPath
	 *            path of the link
	 */
	private void addLink(String linkfile, String linkPath) {
		String projLoc, root;
		int pos1, pos2, pos3;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFolder folder = project.getFolder(linkfile);

		// Search for root path of project location (mainly the name of the
		// drive or of the view)
		projLoc = project.getLocation().toString();
		pos1 = projLoc.indexOf("S-Gold");
		pos2 = projLoc.indexOf("CRYPTO");
		pos3 = projLoc.indexOf("IFX_Tools");
		if ((pos1 != -1) || (pos2 != -1) || (pos3 != -1)) {
			if (pos1 != -1)
				root = projLoc.substring(0, pos1);
			else if (pos2 != -1)
				root = projLoc.substring(0, pos2);
			else
				root = projLoc.substring(0, pos3);
			// Create the full path of the link
			linkPath = root.concat(linkPath);
			IPath location = new Path(linkPath);
			// Add this full path
			if (workspace.validateLinkLocation(folder, location).getSeverity() != IStatus.ERROR) {
				try {
					folder.createLink(location, IResource.NONE, null);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println(workspace.validateLinkLocation(folder, location).toString());
			}
		}
	}
}
