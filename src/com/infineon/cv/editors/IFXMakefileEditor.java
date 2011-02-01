/**
 * 
 */
package com.infineon.cv.editors;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.cdt.make.internal.ui.editor.MakefileEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.infineon.cv.makefile.ParserMakefile;
import com.infineon.cv.makefile.XMLNode;
import com.infineon.cv.makefile.XMLParser;
import com.infineon.cv.makefile.parser.MakefileParser;
import com.infineon.cv.makefile.parser.VariableManager;

/**
 * @author gautier
 * 
 */
@SuppressWarnings("restriction")
public class IFXMakefileEditor extends MultiPageEditorPart {

	private TextEditor textEditor;
	private TreeViewer linkButtons;
	
	private TreeViewer treeViewer;
	private TreeColumn keyColumn, valueColumn;
	
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
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
			IFile file = fileEditorInput.getFile();
			// IProject project = file.getProject();
			String fileLocation = file.getLocation().toOSString();
			System.out.println("File location = " + fileLocation);

			VariableManager var = new VariableManager();
			MakefileParser parMake = new MakefileParser(var);
			try {
				parMake.parse(new File(fileLocation));
				System.out.println(parMake);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		parseMakefileValues();
//		getValuesFromProject();
		
		createTargetPage();
		createSourcePage();
		setActivePage(indexSource);
	}

	/**
	 * 
	 */
	protected void createSourcePage() {
		try {
			textEditor = new MakefileEditor();
			indexSource = addPage(textEditor, getEditorInput());
			setPageText(indexSource, "Source");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	protected void createTargetPage() {
		Composite treeContainer = new Composite(getContainer(), SWT.NONE);
		TreeColumnLayout layout = new TreeColumnLayout();
		treeContainer.setLayout(layout);
		treeViewer = new TreeViewer(treeContainer, SWT.MULTI | SWT.FULL_SELECTION);
		
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		
		keyColumn = new TreeColumn(tree, SWT.NONE);
		keyColumn.setText("Define");
		layout.setColumnData(keyColumn, new ColumnWeightData(2));
		
		valueColumn = new TreeColumn(tree, SWT.NONE);
		valueColumn.setText("Value");
		layout.setColumnData(valueColumn, new ColumnWeightData(2));
			
		int index = addPage(treeContainer);
		setPageText(index, "Target");
	}

	protected void parseMakefileValues() {
		// Parse makefile
		if (parserMakefile != null && parserMakefile.getValid() == true) {
//			if (parserMakefile.getSrcdir().size() != 0)
//				System.out.println("SRCDIR=" + parserMakefile.getSrcdir().toString());
//			System.out.println("OWN_INIT_S=" + parserMakefile.getOwn_init_s().toString());
//			System.out.println("ARCH=" + parserMakefile.getArch().toString());
//			if (parserMakefile.getIncdir().size() != 0)
//				System.out.println("INCDIR=" + parserMakefile.getIncdir().toString());
//			if (parserMakefile.getVpath().size() != 0)
//				System.out.println("VPATH=" + parserMakefile.getVpath().toString());
//			if (parserMakefile.getSrcs().size() != 0)
//				System.out.println("SRCS=" + parserMakefile.getSrcs().toString());
//			System.out.println("EXEC=" + parserMakefile.getExec().toString());
//			System.out.println("ITCM_BASE_ADDRESS=" + parserMakefile.getItcm_base_address().toString());
//			System.out.println("FORBIDDEN_DEFINES=" + parserMakefile.getFobidden_defines().toString());
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
