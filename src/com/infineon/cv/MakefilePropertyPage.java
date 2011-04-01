package com.infineon.cv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * MakefilePropertyPage is the 1st version of makefile GUI
 * @author zhaoxi
 * TODO can be removed
 *
 */
public class MakefilePropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	ArrayList<Button> buttons = new ArrayList<Button>();
	ArrayList<String> defautValues;

	protected Control createContents(Composite parent) {
		File makefile = getMakefile();
		Composite panel = new Composite(parent, SWT.NONE);
		//ListViewer  listViewer1 = new ListViewer(panel);
		InputStream stream;
		stream = getClass().getClassLoader().getResourceAsStream("data.xml");
		HashMap<String, ArrayList<String>> valuesList = new HashMap<String, ArrayList<String>>();
		Document document;

		try {
			document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(stream);
			org.w3c.dom.Element racine = document.getDocumentElement();
			NodeList listAll = racine.getChildNodes();
			ArrayList<String> list = null;
			for (int i = 0; i < listAll.getLength(); i++) {

				Node courant = listAll.item(i);
				if (courant.getTextContent().startsWith("\n")
						|| courant.getTextContent().startsWith("\t")) {
					continue;
				} else if (!valuesList.containsKey(courant.getNodeName())) {
					list = new ArrayList<String>();
					list.add(courant.getTextContent());
					valuesList.put(courant.getNodeName(), list);
				} else {
					list.add(courant.getTextContent());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		panel.setLayout(new FillLayout());
		final TabFolder tabFolder = new TabFolder(panel, SWT.BORDER);
		String strLine = getMKDefautValues(makefile);
		defautValues = new ArrayList<String>();
		// curoiusly split() function doesn't work
		// defautValues.addAll(Arrays.asList(strLine.split("+")));
		// split() manually
		String chars = "";
		int i;
		for (i = 0; i < strLine.length(); i++) {

			if (strLine.charAt(i) == '+') {
				defautValues.add(chars);
				chars = "";
			} else {
				chars = chars.concat(Character.toString(strLine.charAt(i)));
			}

		}
		defautValues.add(chars);
		// defautValues.add(strLine.substring(i));

		for (String key : valuesList.keySet()) {
			TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
			tabItem.setText(key);
			Composite composite = new Composite(tabFolder, SWT.NULL);
			tabItem.setControl(composite);
			composite.setLayout(new RowLayout());

			for (Object value : valuesList.get(key).toArray()) {
				final Button button = new Button(composite, SWT.RADIO);
				button.setText(value.toString());
				buttons.add(button);
				if (defautValues.contains(value)) {
					button.setSelection(true);
					defautValues.remove(value);
				}
				button.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						if (button.getSelection()) {
							// to do, modify makefile using all button selected
							// texts
						}
					}
				});
			}

		}
		System.out.println(buttons.size());
		return panel;
	}

	protected String getMKDefautValues(File file) {
		String strLine = null;
		try {
			File fstreamreader = file;
			BufferedReader in = new BufferedReader(
					new FileReader(fstreamreader));

			while ((strLine = in.readLine()) != null) {
				if (strLine.startsWith("ARCH")) {
					break;
				}
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		strLine = strLine.substring(strLine.indexOf('=')+1);
		strLine = strLine.trim();
		return strLine;

	}

	protected File getMakefile() {
		IProject project = (IProject) getElement();
		IFile makefile = project.getFile("makefile");
		System.out.println("££££££££££££££££££££££££££££££££££££££££");
		System.out.println(makefile.getLocation().toString());
//		File makefile = new File(project.getLocation().toString().concat(
//				"/makefile"));
//		System.out.println(makefile.getPath());
//		return makefile;
		return null;
	}

	protected void modifyMakefile(File file, ArrayList<String> values) {
		try {
			// Open the file that is the first
			// command line parameter
			File fstreamreader = file;
			BufferedReader in = new BufferedReader(
					new FileReader(fstreamreader));

			// Create file FileWriter
			File fstreamwriter = new File("out");
			BufferedWriter out = new BufferedWriter(new FileWriter(
					fstreamwriter));

			String strLine;
			// Read File Line By Line
			while ((strLine = in.readLine()) != null) {
				// is it variable ARCH?
				if (strLine.startsWith("ARCH")) {
					String result = new String();
					for (String s : values) {
						result = result.concat(s).concat("+");
					}
					for (String s : defautValues) {
						result = result.concat(s).concat("+");
					}

					out.write("ARCH = "
							+ result.substring(0, result.length() - 1));
					out.write("\n");
					// out.flush();
				} else if (strLine.startsWith("SRCS +=")) {
					File srcPath = file.getParentFile();
					System.out.println("get Parent file:" + srcPath.toString());
					String[] cFiles = srcPath.list(new FilenameFilter() {
						public boolean accept(File arg0, String arg1) {
							return arg1.endsWith(".c") || arg1.endsWith(".asm");
						}
					});
					String srcs = "SRCS +=";
					for (String cfile : cFiles) {
						srcs = srcs.concat(cfile).concat("\\\n");
					}
					out.write(srcs);
				} else {
					out.write(strLine);
					out.write("\n");
				}

			}
			// Close the input stream
			in.close();
			out.close();
			fstreamreader.delete();
			System.out.print(fstreamwriter.renameTo(file));
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public boolean performOk() {
		ArrayList<String> modifiedValues = new ArrayList<String>();
		for (Button b : buttons) {
			if (b.getSelection()) {
				modifiedValues.add(b.getText());
			}
		}
		modifyMakefile(getMakefile(), modifiedValues);
		return super.performOk();
	}
}
