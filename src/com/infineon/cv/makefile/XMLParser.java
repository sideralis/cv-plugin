package com.infineon.cv.makefile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XMLParser parses ProjectSetting.xml and stores all nodes in an ArrayList.
 * */
public class XMLParser {

	String xmlfile;
	ArrayList<XMLNode> prjNodes;

	public XMLParser(String xmlfile) {
		InputStream stream;
		stream = getClass().getClassLoader().getResourceAsStream(xmlfile);
		prjNodes = new ArrayList<XMLNode>();
		Document document;

		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
			org.w3c.dom.Element racine = document.getDocumentElement();
			NodeList listAll = racine.getChildNodes();
			for (int i = 0; i < listAll.getLength(); i++) {

				Node courant = listAll.item(i);
				if (courant.getNodeName().contains("project")) {
					courant.getAttributes().getNamedItem("name").getTextContent();
					// System.out.println(courant.hasAttributes());
					NodeList children = courant.getChildNodes();
					XMLNode pNode = new XMLNode(courant.getAttributes().getNamedItem("name").getTextContent());
					for (int j = 0; j < children.getLength(); j++) {
						Node child = children.item(j);
						if (child.getNodeValue() == null) {
							pNode.addAttribut(child.getNodeName(), child.getTextContent());
						}
					}
					prjNodes.add(pNode);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<XMLNode> getXMLNodes() {
		return prjNodes;
	}

	public static void main(String args[]) {
		XMLParser parser = new XMLParser("ProjectSetting.xml");
		ArrayList<XMLNode> prjNodes = parser.getXMLNodes();
		for (XMLNode node : prjNodes) {
			// System.out.println(node.getProjectType());
			HashMap<String, ArrayList<String>> attributs = node.getAttributs();
			for (String key : attributs.keySet()) {
				ArrayList<String> values = attributs.get(key);
				// System.out.println(key);
				// for (String value : values) {
				// System.out.println(value);
				// }
			}
		}
	}

}
