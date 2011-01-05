package com.infineon.cv.makefile;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * TabAdvanced creates the components in the "advanced" TabItem container.
 * users can configure variables like "User assembly flags", "User compiler flags",
 * "User linker flags" etc.  
 */
public class TabAdvanced {
	Composite parent;
	HashMap<String, Text> texts;
	String[] labels = { "User assembly flags", "User compiler flags",
			"User linker flags", "Own init file name", "ITCM base address",
			"DTCM base address" };

	public TabAdvanced(Composite parent, HashMap<String, String> values) {
		this.parent = parent;
		texts = new HashMap<String, Text>();

		int index = 0;
		Label l1 = new Label(parent, SWT.LEFT);
		l1.setText(labels[index]);

		final Text t1 = new Text(parent, SWT.BORDER | SWT.SINGLE);
		t1.setText(values.get(labels[index]));
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		t1.setLayoutData(gd);
		values.remove(labels[index]);
		texts.put(l1.getText(), t1);

		Label previous = l1;
		for (index = 1; index < labels.length; index++) {
			previous = createComposite(labels[index],
					values.get(labels[index]), previous);
		}

	}

	private Label createComposite(String labelName, String text, Label previous) {
		Label l = new Label(parent, SWT.LEFT);
		l.setText(labelName);
		Text t = new Text(parent, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		t.setLayoutData(gd);

		t.setText(text);
		texts.put(l.getText(), t);
		return l;

	}

	public HashMap<String, String> getOutputs() {
		HashMap<String, String> result = new HashMap<String, String>();
		for (String key : texts.keySet()) {
			Text text = texts.get(key);
			result.put(key, text.getText());

		}
		return result;
	}

}
