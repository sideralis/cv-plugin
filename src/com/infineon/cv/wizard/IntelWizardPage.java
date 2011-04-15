package com.infineon.cv.wizard;

import java.io.File;

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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class IntelWizardPage extends WizardPage {

	/** The different types of project */
	public static final int TESTCASE = 0;
	public static final int LIBRARY = 1;
	public static final int BCO_TESTCASE = 2;
	public static final int ML_LOADER = 3;
	
	/** The ui name of each projects */
	private static final String[] PROJECT_TYPE = new String[] {"CV Testcase", 
		"CV Library",
		"BCO Testcase",
		"Memloader loader"};
	
	/** The starting path where the project should be located */
	private static final String[] PROJECT_PATH = new String[] {
		"\\S-Gold\\S-GOLD_Family_Environment\\Testcases",
		"\\S-Gold\\S-GOLD_Family_Environment\\_lib\\_src",
		"\\S-Gold-Bootcode\\S-GOLD\\Verification",
		"\\IFX_Tools\\MemLoader\\C_ASM\\Target\\SG\\NOR Flash\\"};
		

	private Button browse;
	private Text testcaseName;
	private Text locText;
	private List projectType;
	
	protected IntelWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription("Creates new Intel CV Testcase");
	}

	@Override
	public void createControl(Composite parent) {
		GridData gd;
		
		Composite container = new Composite(parent, SWT.NULL);
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
		
		Label prjTypeLabel = new Label(container, SWT.NONE);
		prjTypeLabel.setText("Select the project type");
		gd = new GridData();
		prjTypeLabel.setLayoutData(gd);
		
		projectType = new List(container, SWT.SINGLE | SWT.BOLD | SWT.BORDER);
		for (String e:PROJECT_TYPE)
			projectType.add(e);
		projectType.setSelection(0);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		projectType.setLayoutData(gd);

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
				String startPath = System.getenv("VIEW_TAG");
				if (startPath != null) {
					startPath = "M:\\"+startPath+PROJECT_PATH[getProjectType()];
					File f = new File(startPath);
					if (f.exists())
						dirDialog.setFilterPath(startPath);
				}
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
	
	public int getProjectType() {
		return projectType.getSelectionIndex();
	}
	

}
