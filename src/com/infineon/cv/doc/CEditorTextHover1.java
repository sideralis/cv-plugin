package com.infineon.cv.doc;


import java.util.HashMap;

import org.eclipse.cdt.ui.text.c.hover.ICEditorTextHover;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

/**
 * CEditorTextHover1 specifies your own hover information in the C Editor view.
 * If you need to support richer content than DefautInformationControl can handle,you'll need to
 * create your own hover control(reference: http://www.outofwhatbox.com/blog/2009/05/eclipse-rich-hovers-redux/).
 * 
 * */
public class CEditorTextHover1 implements ICEditorTextHover{//, ITextHoverExtension2{
	private IEditorPart editor;
	@Override
	public void setEditor(IEditorPart editor) {
		//System.out.println("SET EDITOR");
		this.editor = editor;
		
		
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if (hoverRegion != null) {
			try {
				if (hoverRegion.getLength() > -1)					
					return findDoc(textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength()));

				} catch (BadLocationException x) {
			}
		}
		return "JavaTextHover.emptySelection";
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		Point selection= textViewer.getSelectedRange();
		if (selection.x <= offset && offset < selection.x + selection.y)
			return new Region(selection.x, selection.y);
		return new Region(offset, 0);
	}

	private String findDoc(String functionName) {
		FileParser parser = new FileParser();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String locationName = editor.getEditorInput().getToolTipText();
		String subLoc = locationName.substring(0,locationName.indexOf("/"));
		IProject project = workspace.getRoot().getProject(subLoc);
		String prjLoc = project.getLocation().toString();
		String fileName = prjLoc + "/"+editor.getEditorInput().getName();
		
		HashMap<String, String> docs = null;
		try {
			docs = parser.createFunctionDocs(fileName);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		String functionDoc = docs.get(functionName);
		System.out.println(functionDoc);
		FunctionDoc splitdoc = parser.splitDoc(functionDoc);
		String result;
		result = "Function Name: " + functionName +"\n";
		result +="Parameters:";
		for(String param:splitdoc.getParameter()){
			result+=" "+ param;			
		}
		
		result+= "\n"+"Return"+splitdoc.getReturN();
		System.out.println(result);
		return result;
	}



}

