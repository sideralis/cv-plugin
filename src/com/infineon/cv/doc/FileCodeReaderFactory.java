package com.infineon.cv.doc;
import org.eclipse.cdt.core.dom.ICodeReaderFactory;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.ICodeReaderCache;
/**
 * FileCodeReaderFactory implements ICodeReaderFactory interface,it will be used
 * to get IASTTranslationUnit in FileParser Class.
 * */
public class FileCodeReaderFactory implements ICodeReaderFactory {

    private static FileCodeReaderFactory instance = new FileCodeReaderFactory();
	//private ICodeReaderCache cache = null;
	public static FileCodeReaderFactory getInstance(){return instance;}
	public CodeReader createCodeReaderForInclusion(String path) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public CodeReader createCodeReaderForTranslationUnit(String path) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ICodeReaderCache getCodeReaderCache() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getUniqueIdentifier() {
		// TODO Auto-generated method stub
		return 0;
	}
	

}