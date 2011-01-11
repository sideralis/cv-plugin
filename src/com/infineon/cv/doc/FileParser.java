package com.infineon.cv.doc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ICodeReaderFactory;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.core.runtime.CoreException;

/**
 * FileParser gets all comments in a source file and store them in a Map(key: function name).
 * */


public class FileParser {
	private HashMap<String, String> functionDocs = new HashMap<String, String>();

	public ArrayList<FunctionDoc> getFunctionDocs(String fileLocation) {
		try {
			createFunctionDocs(fileLocation);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (functionDocs == null) {
			return null;

		}
		ArrayList<FunctionDoc> docs = new ArrayList<FunctionDoc>();
		for (String functionName : functionDocs.keySet()) {
			FunctionDoc doc = new FunctionDoc();
			doc.setFunctionName(functionDocs.get(functionName));
			// recuperer les parametres et le retour
			String docString = functionDocs.get(functionName);
			String[] docTraited = docString.split("@");
			for (String line : docTraited) {
				if (line.startsWith("@param")) {
					doc.addParameter(line);
				}
				if (line.startsWith("@return")) {
					doc.setReturN(line);
				}

			}
			docs.add(doc);

		}
		return docs;

	}
	public FunctionDoc splitDoc(String docString){
		FunctionDoc result = new FunctionDoc();
		//List<String> list = new ArrayList<String>();
		//list.add("int");
		if(docString.startsWith("/**")){
		String[] docTraited = docString.split("\n");
		for (String line : docTraited) {
			if(line.contains("@")){
			//line = line.substring(line.indexOf("@"));
			if (line.contains("@param")) {
				String paramStr = line.substring(line.indexOf("@param")+6);
				String[] params = paramStr.split(",");
				//result.addParameter(line.substring(line.indexOf("@param"+1)));
				//result.setFunctionName(line.substring(line.indexOf("@param"+1)));
				result.setParameter(Arrays.asList(params));
			}
			if (line.contains("@return")) {
				//result.setReturN("int");
				result.setReturN(line.substring(line.indexOf("@return")+7));
			}

		}}
		}
		return result;
		
	}

	public HashMap<String, String> createFunctionDocs(String fileLocation)
			throws CoreException {
		IParserLogService log = new DefaultLogService();
		String codeString = "";
		/******* Take a c source file ********/
		File file = new File(fileLocation);
		File fstreamreader = file;
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(fstreamreader));
			String strLine;
			// Read File Line By Line
			try {
				while ((strLine = in.readLine()) != null) {
					codeString = codeString.concat("\n" + strLine);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		char[] code = codeString.toCharArray();
		CodeReader reader = new CodeReader(code);
		Map<String,String> definedSymbols = new HashMap<String,String>();
		String[] includePaths = { "C:\\MinGW\\include",
				"C:\\MinGW\\lib\\gcc\\mingw32\\3.4.5\\include" };
		IScannerInfo info = new ScannerInfo(definedSymbols, includePaths);
		ICodeReaderFactory readerFactory = FileCodeReaderFactory.getInstance();
		IASTTranslationUnit translationUnit = GCCLanguage.getDefault()
				.getASTTranslationUnit(reader, info, readerFactory, null, log);

		// Function name
		ArrayList<IASTFunctionDefinition> functions = new ArrayList<IASTFunctionDefinition>();
		for (IASTDeclaration declaration : translationUnit.getDeclarations()) {
			if (declaration instanceof IASTFunctionDefinition) {
				functions.add((IASTFunctionDefinition) declaration);
			}
		}

		for (IASTComment com : translationUnit.getComments()) {
			for (IASTFunctionDefinition function : functions) {
				if (com.getFileLocation().getNodeOffset()
						+ com.getFileLocation().getNodeLength() + 1 == function
						.getFileLocation().getNodeOffset()) {
					functionDocs.put(function.getDeclarator().getName()
							.toString(), com.toString());
				}
			}
		}
		return this.functionDocs;

	}
}