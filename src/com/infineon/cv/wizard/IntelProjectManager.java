package com.infineon.cv.wizard;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;

public class IntelProjectManager {
	/**
	 * See http://cdt-devel-faq.wikidot.com/#toc25
	 * @param name
	 * @param path
	 * @param monitor
	 */
	public void createIntelProject(String name, String path, IProgressMonitor monitor) {
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
		} catch (OperationCanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
