package com.infineon.cv.doc;
import java.util.ArrayList;
import java.util.List;

/**
 * FunctionDoc is the documentation attached to a function.
 * one instance FunctionDoc contains a function name, the parameter list and a function return.
 * */
public class FunctionDoc {
	
	private String functionName;
	private List<String> parameter = new ArrayList<String>();
	private String returN;
	
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public List<String> getParameter() {
		return parameter;
	}
	public void setParameter(List<String> parameter) {
		this.parameter = parameter;
	}
	public String getReturN() {
		return returN;
	}
	public void setReturN(String returN) {
		this.returN = returN;
	}
	public void addParameter(String param){
		parameter.add(param);
	}
	public void getWindow(){
		
	}

}
