package com.infineon.cv.makefile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * TextFieldBlock creates a subset components in TabItemMKEditor.
 * */
public class TextFieldBlock {
	Composite parent;
	String labelName;
	String text;
	Text textField;
	String newValue;

	public TextFieldBlock(Composite c, String labelName, String text) {
		this.parent = c;
		this.labelName = labelName;
		this.text = text;
		init();
	}

	private void init() {
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelName);
		FormData data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(0, 5);
		data.bottom = new FormAttachment(50, -5);
		data.right = new FormAttachment(50, -5);
		label.setLayoutData(data);

		textField = new Text(parent, SWT.BORDER);
		textField.setText(text);
		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(label, 5);
		data.bottom = new FormAttachment(50, -5);
		data.right = new FormAttachment(100, -5);
		textField.setLayoutData(data);
		ModifyListener listener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				valueChanged((Text) e.widget);
			}
		};

		textField.addModifyListener(listener);
	}

	private void valueChanged(Text text) {
		if (!text.isFocusControl())
			return;

		if (text == textField) {
			setNewValue(text.getText());

		}

	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

}
