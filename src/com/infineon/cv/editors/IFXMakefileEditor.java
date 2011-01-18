/**
 * 
 */
package com.infineon.cv.editors;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.cdt.make.internal.ui.editor.*;

import com.infineon.cv.makefile.ParserMakefile;
import com.infineon.cv.makefile.XMLNode;
import com.infineon.cv.makefile.XMLParser;

/**
 * @author gautier
 * 
 */
public class IFXMakefileEditor extends MultiPageEditorPart {

	private TextEditor textEditor;
	private TreeViewer linkButtons;
	private ListViewer targetList;
	private int indexSource;
	private ParserMakefile parserMakefile;
	private XMLParser valuesMakefile;

	/**
	 * Constructor
	 */
	public IFXMakefileEditor() {
		parserMakefile = null;
		parseMakefilePossibleValues();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	@Override
	protected void createPages() {
		// Job job = new Job("Parsing makefile...") {
		// @Override
		// protected IStatus run(IProgressMonitor monitor) {
		// monitor.beginTask("Parsing makefile...", 100);

		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
			IFile file = fileEditorInput.getFile();
			// IProject project = file.getProject();
			String fileLocation = file.getLocation().toOSString();
			System.out.println("File location = " + fileLocation);
			parserMakefile = new ParserMakefile(fileLocation);
			parserMakefile.readMakefile();
			// monitor.worked(40);
		}
		if (parserMakefile != null && parserMakefile.getValid() == true) {
			parseMakefileValues();
			// monitor.worked(20);
			getValuesFromProject();
			// monitor.worked(10);
			createTargetPage();
			// monitor.worked(10);
			createLinkPage();
			// monitor.worked(10);
		}
		createSourcePage();
		setActivePage(indexSource);

		// monitor.done();
		// return Status.OK_STATUS;
		// }
		// };
		// job.schedule();
	}

	/**
	 * 
	 */
	@SuppressWarnings("restriction")
	protected void createSourcePage() {
		try {
			textEditor = new MakefileEditor();
			indexSource = addPage(textEditor, getEditorInput());
			setPageText(indexSource, "Source");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	protected void createLinkPage() {
		linkButtons = new TreeViewer(getContainer(), SWT.MULTI | SWT.FULL_SELECTION);
		int index = addPage(linkButtons.getControl());
		setPageText(index, "Linked libraries");
	}

	protected void createTargetPage() {
		targetList = new ListViewer(getContainer());
		int index = addPage(targetList.getControl());
		setPageText(index, "Target");
	}

	protected void parseMakefileValues() {
		// Parse makefile
		if (parserMakefile != null && parserMakefile.getValid() == true) {
			if (parserMakefile.getSrcdir().size() != 0)
				System.out.println("SRCDIR=" + parserMakefile.getSrcdir().toString());
			System.out.println("OWN_INIT_S=" + parserMakefile.getOwn_init_s().toString());
			System.out.println("ARCH=" + parserMakefile.getArch().toString());
			if (parserMakefile.getIncdir().size() != 0)
				System.out.println("INCDIR=" + parserMakefile.getIncdir().toString());
			if (parserMakefile.getVpath().size() != 0)
				System.out.println("VPATH=" + parserMakefile.getVpath().toString());
			if (parserMakefile.getSrcs().size() != 0)
				System.out.println("SRCS=" + parserMakefile.getSrcs().toString());
			System.out.println("EXEC=" + parserMakefile.getExec().toString());
			System.out.println("ITCM_BASE_ADDRESS=" + parserMakefile.getItcm_base_address().toString());
			System.out.println("FORBIDDEN_DEFINES=" + parserMakefile.getFobidden_defines().toString());
		}
	}

	/**
	 * Parse the xml file describing all possible values for the different
	 * defines of the makefile
	 */
	protected void parseMakefilePossibleValues() {
		// Parse possible values
		valuesMakefile = new XMLParser("ProjectSetting.xml");
	}

	/**
	 * Get the possible values for the different defines of the makefile for a
	 * given project
	 */
	protected void getValuesFromProject() {
		HashMap<String, ArrayList<String>> xml = null;
		ArrayList<XMLNode> prjNodes = valuesMakefile.getXMLNodes();
		for (XMLNode node : prjNodes) {
			if (parserMakefile.getArch().contains(node.getProjectType().trim())) {
				// Get only node for current project
				xml = node.getAttributs();
				break;
			}
		}
	}

	@Override
	public void setFocus() {
		super.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		textEditor.doSave(monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
