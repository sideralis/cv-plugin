package com.infineon.cv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.CMacroEntry;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICFolderDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSetting;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSettingEntry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * AddSymbolAction sets defined symbols according to makefile
 * 
 * @author zhaoxi
 * 
 * TODO could be removed as this should be done automatically with makefile saving
 * 
 */
public class AddSymbolAction implements IObjectActionDelegate {
	private ISelection selection;

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

	@Override
	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> it = (Iterator<?>) ((IStructuredSelection) selection).iterator(); it.hasNext();) {
				Object element = it.next();
				IFile makefile = null;
				if (element instanceof IFile) {
					makefile = (IFile) element;
				} else if (element instanceof IAdaptable) {
					makefile = (IFile) ((IAdaptable) element).getAdapter(IFile.class);
				}
				if (makefile != null) {
					// String symbols[] =getSymbolsFromMF(makefile);
					addSymbolsFromeMF(makefile, makefile.getProject());

					System.out.print("Add symbols to the project");
				}
			}
		}

	}

	private void addSymbolsFromeMF(IFile makefile, IProject project) {
		String strLine = null;
		ArrayList<String> symbols = new ArrayList<String>();
		try {
			File fstreamreader = new File(makefile.getRawLocationURI().getPath().toString());
			BufferedReader in = new BufferedReader(new FileReader(fstreamreader));

			while ((strLine = in.readLine()) != null) {
				if (strLine.startsWith("OWN_CFLAGS")) {
					int symbolIndex = strLine.indexOf("-D");
					symbols.add(strLine.substring(symbolIndex + 2));

				}
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		Object[] symbolsAdd = symbols.toArray();
		// IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// IProject[] projects = workspace.getRoot().getProjects();
		ICProjectDescription prjDesc;
		prjDesc = CoreModel.getDefault().getProjectDescription(project);
		ICConfigurationDescription[] descs = prjDesc.getConfigurations();
		for (ICConfigurationDescription desc : descs) {
			for (ICFolderDescription filedes : desc.getFolderDescriptions()) {
				for (ICLanguageSetting lang : filedes.getLanguageSettings())// ;//.getLanguageSettings();
				{
					ICLanguageSettingEntry[] newEntries = new ICLanguageSettingEntry[symbolsAdd.length];
					int i = 0;
					for (String symbol : symbols) {
						newEntries[i++] = new CMacroEntry(symbol, "1", 0);
					}

					lang.setSettingEntries(ICSettingEntry.MACRO, newEntries);
				}
			}
			try {
				CoreModel.getDefault().setProjectDescription(project, prjDesc);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	// }

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;

	}

}
