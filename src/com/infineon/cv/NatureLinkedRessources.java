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
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;

/**
 * NatureLinkedRessources implements IProjectNature, it adds linked files
 * according to the project type.
 * */
@SuppressWarnings( { "unchecked", "serial" })
public class NatureLinkedRessources implements IProjectNature {
	/** The ID of this project nature */
	public static final String NATURE_ID = "com.infineon.cv.NatureLinkedRessources";
	private IProject project;
	private static final Map<String, String[]> libsrcPaths = new HashMap() {
		{
			put("CVTC", new String[] { 
					"base \\S-Gold\\S-GOLD_Family_Environment\\_base", 
					"lib \\S-Gold\\S-GOLD_Family_Environment\\_lib\\_src",
					"halix \\S-Gold\\S-GOLD_Family_Environment\\_halix\\_src" });
		}
		{
			put("CVLib", new String[] { 
					"lib \\S-Gold\\S-GOLD_Family_Environment\\_lib\\_src", 
					"halix \\S-Gold\\S-GOLD_Family_Environment\\_halix\\_src" });
		}
		{
			put("CVMem", new String[] { 
					"cgu \\IFX_Tools\\MemLoader\\C_ASM\\Target\\SG\\NOR Flash\\_lld\\CGU", 
					"ebu \\IFX_Tools\\MemLoader\\C_ASM\\Target\\SG\\NOR Flash\\_lld\\EBU",
					"emmc \\IFX_Tools\\MemLoader\\C_ASM\\Target\\SG\\NOR Flash\\_lld\\eMMC", 
					"pmu \\IFX_Tools\\MemLoader\\C_ASM\\Target\\SG\\NOR Flash\\_lld\\PMU",
					"com \\IFX_Tools\\MemLoader\\C_ASM\\Target\\SG\\NOR Flash\\_lld\\SerialInterface", 
					"lld \\IFX_Tools\\MemLoader\\C_ASM\\Target\\SG\\NOR Flash\\_lld",
					"base \\IFX_Tools\\MemLoader\\C_ASM\\Target\\SG\\NOR Flash\\_base" });
		}
		{
			put("Bootcode", new String[] { 
					"base \\S-Gold-Bootcode\\S-GOLD\\Target\\base", 
					"bs \\S-Gold-Bootcode\\S-GOLD\\Target\\bs\\src", 
					"drv_mem \\S-Gold-Bootcode\\S-GOLD\\Target\\drv_mem\\src",
					"hal_src \\S-Gold-Bootcode\\S-GOLD\\Target\\hal\\src", 
					"hal_inc \\S-Gold-Bootcode\\S-GOLD\\Target\\hal\\inc", 
					"sc \\S-Gold-Bootcode\\S-GOLD\\Target\\sc\\src" });
		}
		{
			put("URFET", new String[] { 
					"halix \\S-Gold\\S-GOLD_Family_Environment\\_halix\\_src", 
					"lib \\S-Gold\\S-GOLD_Family_Environment\\_lib\\_src",
					"base \\S-Gold\\S-GOLD_Family_Environment\\_base", 
					"fmr_hld \\fmr_cv\\fmr_xg223\\FMR_HLD", 
					"fmr_lld \\fmr_cv\\fmr_xg223\\FMR_LLD" });
		}
		{
			put("Crypto", new String[] { 
					"base \\S-Gold\\S-GOLD_Family_Environment\\_base", 
					"lib \\S-Gold\\S-GOLD_Family_Environment\\_lib\\_src",
					"halix \\S-Gold\\S-GOLD_Family_Environment\\_halix\\_src",
					"crypto_hal \\CRYPTO\\S-GOLD_Family_Environment\\_halix_CV\\_src",
					"crypto_all \\CRYPTO\\S-GOLD_Family_Environment\\Testcases\\CRYPTO_test\\CRYPTO_TC_All"});
		}
		{
			put("hades", new String[] { 
					"base \\S-Gold\\S-GOLD_Family_Environment\\_base", 
					"lib \\S-Gold\\S-GOLD_Family_Environment\\_lib\\_src",
					"halix \\S-Gold\\S-GOLD_Family_Environment\\_halix\\_src" });
		}
	};

	/**
	 * 
	 */
	@Override
	public void configure() throws CoreException {
		// Code example for Job is coming from: http://blog.eclipse-tips.com/2009/02/using-progress-bars.html
		Job job = new Job("Adding links...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Adding libraries and include links...", 100);
				try {
					java.util.Map<QualifiedName, String> properties = project.getPersistentProperties();
					String conf = properties.get(new QualifiedName("org.eclipse.cdt.core", "activeConfiguration"));
					if (conf != null)
						addLinks(conf, monitor);

				} catch (CoreException e1) {
					e1.printStackTrace();
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	/**
	 * 
	 * @param conf
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

	@Override
	public void deconfigure() throws CoreException {

	}

	@Override
	public IProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;

	}

	private void addLink(String linkfile, String linkPath) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFolder link = project.getFolder(linkfile);
		linkPath = Character.toString(project.getLocation().toString().charAt(0)).concat(":").concat(linkPath);
		IPath location = new Path(linkPath);

		if (workspace.validateLinkLocation(link, location).getSeverity() != IStatus.ERROR) {
			try {
				link.createLink(location, IResource.NONE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(workspace.validateLinkLocation(link, location).toString());
		}

	}

}
