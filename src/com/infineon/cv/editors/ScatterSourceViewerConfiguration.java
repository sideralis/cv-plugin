package com.infineon.cv.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class ScatterSourceViewerConfiguration extends TextSourceViewerConfiguration {
	private ITokenScanner scanner=null;
	 
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
 
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
 
		return reconciler;
	}
 
	private ContentAssistant assistant = null;
 
//	@Override
//	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
//		if(assistant==null){
//			assistant = new ContentAssistant();
//			assistant.setContentAssistProcessor(new EditorContentAssistProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
//			assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
//		}
//		return assistant;
//	}
 
	private ITokenScanner getScanner(){
		if(scanner == null)
			scanner=new ScatterScanner();
		return scanner;
	}

}
