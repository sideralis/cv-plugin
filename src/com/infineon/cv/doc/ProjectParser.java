package com.infineon.cv.doc;
import java.util.ArrayList;
import java.util.HashMap;

/******
 * Cette classe n'est pas servie finalement, mais ca peut etre interessant de garder pour la suite
 * ***********/
public class ProjectParser {
	
	private HashMap<String, FunctionDoc> projectDocs = new HashMap<String, FunctionDoc>();
	public ProjectParser(String projectLocation){
		FileParser parser = new FileParser();
		ArrayList<FunctionDoc> docs = parser.getFunctionDocs(projectLocation);//"Y:\\IFX_Tools\\Plugin_Eclipse\\workspace\\ParserCDT\\src\\CFile.c");
		//affecter docs dans projectDocs;
		for(FunctionDoc fd : docs){
			projectDocs.put(fd.getFunctionName(), fd);
		}
	}
	public HashMap<String,FunctionDoc> getDocs(){
		return projectDocs;
	}
	
//	public void main(String[] args){
//		FileParser parser = new FileParser();
//		ArrayList<FunctionDoc> docs = parser.getFunctionDocs("Y:\\IFX_Tools\\Plugin_Eclipse\\workspace\\ParserCDT\\src\\CFile.c");
//	}

}
