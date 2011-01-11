package com.infineon.cv.makefile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
/**
 * LinkingTab creates the components in the "Linking" TabItem container.
 * Users can configure variable "unwantedlib".   
 */
public class LinkingTab {
	private ArrayList<Button> linkingBoxs;

	/**
	 * The constructor of the linked libraries tab
	 * @param parent The parent of this tab
	 * @param linkings The linked libraries information
	 */
	public LinkingTab(Composite parent, HashMap<String, Integer> linkings) {
		SortedSet<String> sortedset = new TreeSet<String>(linkings.keySet());

		Iterator<String> it = sortedset.iterator();

		// Create a child composite to hold the controls
		Composite child = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginWidth = 40;
		gridLayout.makeColumnsEqualWidth = true;
		child.setLayout(gridLayout);
		linkingBoxs = new ArrayList<Button>();
		while (it.hasNext()) {
			String name = it.next();
			int selected = linkings.get(name);
			Button button = new Button(child, SWT.CHECK);
			button.setText(name);
			button.setSelection(selected == 1);
			linkingBoxs.add(button);
		}
	}
	/**
	 * Return the user data (what to link and what to not link)
	 * @return The libraries to be linked and not linked
	 */
	public ArrayList<String> getOutputs() {
		ArrayList<String> outputs = new ArrayList<String>();
		for (Button b : linkingBoxs) {
			if (!b.getSelection()) {
				outputs.add(b.getText());
			}
		}
		return outputs;
	}
}
