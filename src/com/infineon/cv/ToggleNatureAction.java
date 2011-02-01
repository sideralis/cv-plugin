package com.infineon.cv;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * ToggleNatureAction implements IObjectActionDelegate interface, which run an
 * action following an action event. The action executed in this class is to
 * create the linked ressources corresponding to the project.
 * */
public class ToggleNatureAction implements IObjectActionDelegate {

	private ISelection selection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@SuppressWarnings("unchecked")
	public void run(IAction action) {

		if (selection instanceof IStructuredSelection) {
			for (Iterator it = ((IStructuredSelection) selection).iterator(); it.hasNext();) {
				Object element = it.next();
				IProject project = null;
				if (element instanceof IProject) {
					project = (IProject) element;
				} else if (element instanceof IAdaptable) {
					project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
				}
				if (project != null) {
					new ToggleNature(project).start();
					// toggleNature(project);
					System.out.println("Add Nature Project");
				}
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	// private void toggleNature(IProject project) {
	// try {
	// IProjectDescription description = project.getDescription();
	// String[] natures = description.getNatureIds();
	//
	// for (int i = 0; i < natures.length; ++i) {
	// if (NatureLinkedRessources.NATURE_ID.equals(natures[i])) {
	// // Remove the nature
	// String[] newNatures = new String[natures.length - 1];
	// System.arraycopy(natures, 0, newNatures, 0, i);
	// System.arraycopy(natures, i + 1, newNatures, i,
	// natures.length - i - 1);
	// description.setNatureIds(newNatures);
	// project.setDescription(description, null);
	// return;
	// }
	// }
	//
	// // Add the nature
	// String[] newNatures = new String[natures.length + 1];
	// System.arraycopy(natures, 0, newNatures, 0, natures.length);
	// newNatures[natures.length] = NatureLinkedRessources.NATURE_ID;
	// description.setNatureIds(newNatures);
	// project.setDescription(description, null);
	// } catch (CoreException e) {
	// e.printStackTrace();
	// }
	// }

}
