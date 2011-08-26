package com.infineon.cv.nature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;


/**
 * ToggleNature extends Thread class, it sets LinkedRessources Nature to the
 * given project
 */
public class ToggleNature extends Thread {
	private IProject project;

	/**
	 * Constructor
	 * 
	 * @param project The project reference
	 */
	public ToggleNature(IProject project) {
		this.project = project;
	}
	/**
	 * TODO: When remove the nature is done, the NatureLinkedRessources.deconfigure is called but currently this function is empty.
	 */
	public void run() {
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (NatureLinkedRessources.NATURE_ID.equals(natures[i])) {
					// Remove the nature
					String[] newNatures = new String[natures.length - 1];
					System.arraycopy(natures, 0, newNatures, 0, i);
					System.arraycopy(natures, i + 1, newNatures, i, natures.length - i - 1);
					description.setNatureIds(newNatures);
					project.setDescription(description, null);
//					return;
				}
			}

			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = NatureLinkedRessources.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
