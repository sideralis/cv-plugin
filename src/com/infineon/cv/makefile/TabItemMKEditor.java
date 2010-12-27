package com.infineon.cv.makefile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
/**
 * TabItemMKEditor creates the components in the "Sources" TabItem container.
 * users can configure variables like "Project Name", "Source Directories",
 * "Include dirs" etc.  
 */
public class TabItemMKEditor {
	private Composite parent;
	private List<ListViewerBlock> listViewers;
	HashMap<String, ArrayList<String>> inputs;

	public TabItemMKEditor(Composite parent,
			HashMap<String, ArrayList<String>> inputs) {
		this.parent = parent;
		this.inputs = inputs;
		listViewers = new ArrayList<ListViewerBlock>();
		createContents();
	}

	public void createContents() {
		for (String key : inputs.keySet()) {
			Composite c = new Composite(parent, SWT.NONE);
			c.setLayout(new FormLayout());
			if (key == "Project Name") {
				new TextFieldBlock(c, key, inputs.get(key).toArray()[0]
						.toString());
			} else {
				ListViewerBlock block = new ListViewerBlock(c, key, inputs
						.get(key));
				listViewers.add(block);
			}
		}
	}

	public HashMap<String, ArrayList<String>> getoutputs() {
		HashMap<String, ArrayList<String>> outputs = new HashMap<String, ArrayList<String>>();
		for (ListViewerBlock block : listViewers) {
			outputs.put(block.labelText, block.getOutputs());
		}
		return outputs;

	}

}
