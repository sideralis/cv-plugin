package com.infineon.cv.wizard;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.widgets.Text;

public class IntelWizardPage extends WizardPage {

	private Button browse;
	private Text testcaseName;
	private Text locText;
	
	protected IntelWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription("Creates new Intel CV Testcase");
	}

	@Override
	public void createControl(Composite parent) {
		GridData gd;
		
		Composite container = new Composite(parent, SWT.NULL);
//		container.setBounds(15, 25, 300, 400);
		container.setLayout(new GridLayout(3, false));
		
		Label name = new Label(container, SWT.NONE);
		name.setText("Enter the testcase name:");
		gd = new GridData();
		name.setLayoutData(gd);
		
		testcaseName = new Text(container, SWT.BOLD | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		testcaseName.setLayoutData(gd);

		Label loc = new Label(container, SWT.NONE);
		loc.setText("Enter the location:");
		gd = new GridData();
		loc.setLayoutData(gd);

		locText = new Text(container, SWT.BOLD | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		locText.setLayoutData(gd);

		browse = new Button(container, SWT.NONE);
		browse.setText("Browse");
		gd = new GridData();
		browse.setLayoutData(gd);

		addListeners();

		setControl(container);

	}

	private void addListeners() {
		browse.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(browse.getShell());
				dirDialog.setText("Select the directory where is/will be located the makefile");
				dirDialog.setFilterPath("c:\\S-Gold");
				String path = dirDialog.open();
				locText.setText(path);
				IPath iPath = new Path(path);
				testcaseName.setText(iPath.lastSegment());
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
