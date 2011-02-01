package com.infineon.cv;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

class IntelMainTab extends AbstractLaunchConfigurationTab {
	private String mode;
	private Composite compParent,compButtons;
	private Group groupButtons;
	private Button reportButton,recordButton,replayButton;
	private Button reportButtonBrowse, recordButtonBrowse, replayButtonBrowse;			
	private Text reportFileName, recordFileName, replayFileName;

	
	public IntelMainTab(String mode) {
		super();
		this.mode = mode;
	}

	@Override
	public void createControl(Composite parent) {
		GridData gd;
		
//		compParent = new Composite(parent, SWT.NONE);
//		compParent.setLayout(new GridLayout(2, true));
//		compParent.setFont(parent.getFont());

		groupButtons = new Group(parent, SWT.NONE);
    	groupButtons.setLayout(new GridLayout(3, false));
    	groupButtons.setText("Mode");
    	groupButtons.setFont(parent.getFont());
    	gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
    	groupButtons.setLayoutData(gd);
    	
    	compButtons = new Composite(groupButtons, SWT.NONE);
    	GridLayout gl = new GridLayout(3, false);
    	gl.marginWidth = 0;
    	gl.marginHeight = 0;
    	compButtons.setLayout(gl);
    	compButtons.setFont(parent.getFont());
    	gd = new GridData(GridData.FILL_BOTH);
    	gd.horizontalSpan = 3;
    	compButtons.setLayoutData(gd);
    	    	
    	// REPORT
		reportButton = new Button(compButtons, SWT.RADIO);
		reportButton.setFont(parent.getFont());
		reportButton.setText("Report only");
		gd = new GridData();
//		gd.horizontalSpan = 3;
		reportButton.setLayoutData(gd);	
//		SWTFactory.setButtonDimensionHint(button);

    	reportFileName = new Text(compButtons, SWT.SINGLE | SWT.BORDER);
    	reportFileName.setFont(parent.getFont());
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.horizontalSpan = 1;
    	reportFileName.setLayoutData(gd);

		reportButtonBrowse = new Button(compButtons, SWT.RADIO);
		reportButtonBrowse.setFont(parent.getFont());
		reportButtonBrowse.setText("Browse");
		gd = new GridData();
		reportButtonBrowse.setLayoutData(gd);	

    	// RECORD
		recordButton = new Button(compButtons, SWT.RADIO);
		recordButton.setFont(parent.getFont());
		recordButton.setText("Report only");
		gd = new GridData();
		recordButton.setLayoutData(gd);	

    	recordFileName = new Text(compButtons, SWT.SINGLE | SWT.BORDER);
    	recordFileName.setFont(parent.getFont());
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.horizontalSpan = 1;
    	recordFileName.setLayoutData(gd);

		recordButtonBrowse = new Button(compButtons, SWT.RADIO);
		recordButtonBrowse.setFont(parent.getFont());
		recordButtonBrowse.setText("Browse");
		gd = new GridData();
		recordButtonBrowse.setLayoutData(gd);	

		// REPLAY
		replayButton = new Button(compButtons, SWT.RADIO);
		replayButton.setFont(parent.getFont());
		replayButton.setText("Report only");
		gd = new GridData();
		replayButton.setLayoutData(gd);	

    	replayFileName = new Text(compButtons, SWT.SINGLE | SWT.BORDER);
    	replayFileName.setFont(parent.getFont());
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.horizontalSpan = 1;
    	replayFileName.setLayoutData(gd);

		replayButtonBrowse = new Button(compButtons, SWT.RADIO);
		replayButtonBrowse.setFont(parent.getFont());
		replayButtonBrowse.setText("Browse");
		gd = new GridData();
		replayButtonBrowse.setLayoutData(gd);	
		
		// Configure
		reportButton.setSelection(true);
	}

	@Override
	public String getName() {
		return "Intel testcase launch configuration";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		recordButton.setEnabled(true);
		recordFileName.setText("my.rpt");
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		
	}
	
}
