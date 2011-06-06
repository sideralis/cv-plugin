package com.infineon.cv.editors;

import org.eclipse.ui.editors.text.TextEditor;

// TODO:  add completion
public class ScatterEditor extends TextEditor {
	
	public ScatterEditor() {
		super();
		ScatterSourceViewerConfiguration configuration = new ScatterSourceViewerConfiguration();
		setSourceViewerConfiguration(configuration);		
	}
}
