package com.infineon.cv.wizard;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class IntelBCOWizardPage extends WizardPage {

	private Button browse;
	private Text testcaseName;
	private Text locText;
	private List projectType;

	protected IntelBCOWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription("Creates a new Intel bootcode project");
	}
	
	@Override
	public void createControl(Composite parent) {
		GridData gd;
		
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));
		
		Label loc = new Label(container, SWT.NONE);
		loc.setText("1- Browse for the project's location:");
		gd = new GridData();
		loc.setLayoutData(gd);

		locText = new Text(container, SWT.BOLD | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		locText.setLayoutData(gd);

		browse = new Button(container, SWT.NONE);
		browse.setText("Browse");
		gd = new GridData();
		browse.setLayoutData(gd);
		
		Label name = new Label(container, SWT.NONE);
		name.setText("2- Enter the project name:");
		gd = new GridData();
		name.setLayoutData(gd);
		
		testcaseName = new Text(container, SWT.BOLD | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		testcaseName.setLayoutData(gd);

		addListeners();

		setControl(container);
	}
	/**
	 * 
	 */
	private void addListeners() {
		browse.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(browse.getShell());
				dirDialog.setText("Select the directory where is/will be located the makefile");
				String startPath = System.getenv("VIEW_TAG");
				if (startPath != null) {
					startPath = "M:\\"+startPath+"\\S-Gold-Bootcode\\S-GOLD\\Target\\src";
					File f = new File(startPath);
					if (f.exists())
						dirDialog.setFilterPath(startPath);
				}
				String path = dirDialog.open();
				locText.setText(path);
				testcaseName.setText("Bootcode XGxxx");
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});	
	}
	public String getLocation() {
		return locText.getText();
	}


	public String getTestcaseName() {
		return testcaseName.getText();
	}

}
