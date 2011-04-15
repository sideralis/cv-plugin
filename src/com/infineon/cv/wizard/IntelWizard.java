package com.infineon.cv.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class IntelWizard extends Wizard implements INewWizard, IRunnableWithProgress {

	private IntelWizardPage wizardPage;

	public IntelWizard() {
		super();
	}

	@Override
	public boolean performFinish() {

		try {
			getContainer().run(false, true, this);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		if (!wizardPage.getTestcaseName().equals("") && !wizardPage.getLocation().equals(""))
			new IntelProjectManager().createIntelProject(wizardPage.getTestcaseName(),wizardPage.getLocation(),wizardPage.getProjectType(),monitor);
	}

	@Override
	public void addPages() {
		super.addPages();
		wizardPage = new IntelWizardPage("New Intel CV testcase");
		addPage(wizardPage);
	}

}
