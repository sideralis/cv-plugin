package com.infineon.cv.makefile;

import java.util.ArrayList;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
/**
 * DropDown creates the components in the "Compiling" TabItem container.
 * users can configure values in variable "ARCH", and "Defines".
 */
public class DropDown {
	private DirectoryFieldEditor own;
	private Combo comboDropDown;
	String comboName;
	String ownScatterFile;
	
	/**
	 * The constructor
	 * @param parent The parent reference
	 * @param labelText The text of the drop down menu
	 * @param values The possible values of the drop down menu
	 * @param index The default selected value of the drop down menu
	 */
	public DropDown(final Composite parent, String labelText,ArrayList<String> values, int index) {

		if (labelText == "mem_scatter") {
			Composite c = new Composite(parent, SWT.NONE);
			c.setLayout(new GridLayout());
			Label label = new Label(c, SWT.LEFT);
			label.setText(labelText);
			comboDropDown = new Combo(parent, SWT.LEFT | SWT.BORDER);
			comboDropDown.setText(labelText);
			GridData gd = new GridData();
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
			comboDropDown.setLayoutData(gd);
			comboName = labelText;

			for (String value : values) {
				comboDropDown.add(value);
			}
			comboDropDown.select(index);
			final Composite c2 = new Composite(c, SWT.NONE);
			c2.setLayout(new FillLayout());
			own = new DirectoryFieldEditor("DirectoryChooser", "", c2);
			if (!getValue().equals("OWN")) {
				own.setEnabled(false, c2);
			}
			comboDropDown.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {

					if (!e.widget.toString().contains("OWN")) {
						/** set DirectoryFieldEditor invisible */
						own.setEnabled(false, c2);
					} else {
						own.setEnabled(true, c2);
					}
//					System.out.println(e.widget + " - Default Selection");
				}
			});

		} else {

			Label label = new Label(parent, SWT.LEFT);
			label.setText(labelText);
			comboDropDown = new Combo(parent, SWT.LEFT | SWT.BORDER);
			comboDropDown.setText(labelText);
			GridData gd = new GridData();
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
			comboDropDown.setLayoutData(gd);
			comboName = labelText;

			for (String value : values) {
				comboDropDown.add(value);
			}
			comboDropDown.select(index);
		}

	}
	/**
	 * Return the selected value of the drop down menu
	 * @return The selected value as String
	 */
	public String getValue() {
		return comboDropDown.getItem(comboDropDown.getSelectionIndex());
	}
	/**
	 * Return the name of the drop down object
	 * @return the name of the drop down object
	 */
	public String getName() {
		return comboName;
	}
	/**
	 * Return the path to the user scatter file if the user want to use his own scatter file. Else returns null
	 * @return Null or path to user's scatter file.
	 */
	public String getOwnScatterFile() {
		if (own != null) {
			return own.getStringValue();
		}
		return null;
	}
}
