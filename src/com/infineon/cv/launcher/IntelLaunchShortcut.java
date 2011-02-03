package com.infineon.cv.launcher;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

public class IntelLaunchShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		System.out.println("Launching shortcut (selection)");
		if (selection instanceof IStructuredSelection) {
			launch(mode);
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		System.out.println("Launching shortcut (editor)");
		launch(mode);
	}

	private void launch(String mode) {
		IWorkspace root = ResourcesPlugin.getWorkspace();
		IProject[] projects = root.getRoot().getProjects();

		String[] nameOfProjects = new String[projects.length];

		for (int i = 0; i < projects.length; i++) {
			nameOfProjects[i] = projects[i].getName();
			System.out.println(nameOfProjects[i]);
			try {
				projects[i].build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
