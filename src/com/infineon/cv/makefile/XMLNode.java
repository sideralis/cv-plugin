package com.infineon.cv.makefile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * XMLNode presents the structure of a node in ProjectSetting.xml, with the project type,
 * and all attributes attached to this project type.
 * */
class XMLNode {
	private String projectType;
	private HashMap<String, ArrayList<String>> attributs;

	public XMLNode(String projectType) {
		this.projectType = projectType;
		attributs = new HashMap<String, ArrayList<String>>();
	}

	public ArrayList<String> getAttributValues(String attribut) {

		return attributs.get(attribut);

	}

	public HashMap<String, ArrayList<String>> getAttributs() {
		return attributs;
	}

	public void setAttributs(HashMap<String, ArrayList<String>> attributs) {
		this.attributs = attributs;
	}

	public void addAttribut(String key, String value) {
		if (attributs.containsKey(key)) {
			attributs.get(key).add(value);
		} else {
			ArrayList<String> values = new ArrayList<String>();
			values.add(value);
			attributs.put(key, values);
		}
	}

	public String getProjectType() {
		return this.projectType;
	}

}
