/**
 * 
 */
package com.infineon.cv.editors;

import org.eclipse.swt.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author gautier
 * 
 */
public class IFXMakefileEditor extends MultiPageEditorPart {

	private TextEditor textEditor;
	private TreeViewer linkButtons;
	private int indexSource;

	/**
	 * Constructor
	 */
	public IFXMakefileEditor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	@Override
	protected void createPages() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
			IFile file = fileEditorInput.getFile();
//			IProject project = file.getProject();
			String fileLocation = file.getLocation().toOSString();
			System.out.println("File location = " + file.getLocation().toOSString());
		}
		createLinkPage();
		createSourcePage();
		setActivePage(indexSource);
	}

	/**
	 * 
	 */
	protected void createSourcePage() {
		try {
			textEditor = new TextEditor();
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
