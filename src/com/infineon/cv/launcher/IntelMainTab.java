package com.infineon.cv.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Bernard
 *
 */
class IntelMainTab extends AbstractLaunchConfigurationTab {
	private String mode;
	private Composite compParent,compRecord,compRepRep;
	private Group groupRecord,groupRepRep,groupButtons;
	private Button reportButton,repRepButton,replayButton,noneButton;
	private Button reportButtonBrowse, recordButtonBrowse, replayButtonBrowse;			
	private Text reportFileName, recordFileName, replayFileName;

	
	public IntelMainTab(String mode) {
		super();
		this.mode = mode;
	}

	@Override
	public void createControl(Composite parent) {
		GridData gd;
		
		// Create main parent
		compParent = new Composite(parent, SWT.NONE);
		setControl(compParent);
		compParent.setLayout(new GridLayout(1, true));
		compParent.setFont(parent.getFont());

		// Create group
		groupButtons = new Group(compParent, SWT.NONE);
		groupButtons.setLayout(new GridLayout(3, false));
		groupButtons.setText("Modes");
		groupButtons.setFont(compParent.getFont());
    	gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		groupButtons.setLayoutData(gd);
		
		// Create children
		createGroupRecord(groupButtons);
		createGroupReplayReport(groupButtons);	
		createGroupNone(groupButtons);
		//
		noneButton.setSelection(true);
		
		addListeners(getShell());
	}

	private void addListeners(final Shell shell) {
		
		MouseListener listener;
		listener = new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {		
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				FileDialog fileDialog = new FileDialog(shell);
				fileDialog.setText("Select your file");
				fileDialog.setFilterPath("c:\\S-Gold");
				String path = fileDialog.open();
				System.out.println(e);
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		};
		reportButtonBrowse.addMouseListener(listener);
		replayButtonBrowse.addMouseListener(listener);
		recordButtonBrowse.addMouseListener(listener);
	}

	private void createGroupNone(Group group) {
		GridData gd;
		
		noneButton = new Button(group, SWT.RADIO);
		noneButton.setFont(compParent.getFont());
		noneButton.setText("None: no report, no replay, no record");
		gd = new GridData();
		noneButton.setLayoutData(gd);			
	}

	private void createGroupReplayReport(Group group) {
		GridData gd;

    	repRepButton = new Button(group, SWT.RADIO);
		repRepButton.setFont(compParent.getFont());
		repRepButton.setText("Report with/without replay");
		gd = new GridData();
		gd.horizontalSpan = 3;
		repRepButton.setLayoutData(gd);	
		
		Group group1 = new Group(group,SWT.NONE);
		group1.setLayout(new GridLayout(3, false));
		group1.setText("Files location");
		group1.setFont(compParent.getFont());
    	gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		group1.setLayoutData(gd);

		Text text1 = new Text(group1, SWT.SINGLE | SWT.READ_ONLY | SWT.RIGHT);
		text1.setFont(compParent.getFont());
		text1.setText("Report");
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.horizontalSpan = 1;
    	recordFileName.setLayoutData(gd);

		reportFileName = new Text(group1, SWT.SINGLE | SWT.BORDER);
    	reportFileName.setFont(compParent.getFont());
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.horizontalSpan = 1;
    	reportFileName.setLayoutData(gd);

		reportButtonBrowse = new Button(group1, SWT.PUSH);
		reportButtonBrowse.setFont(compParent.getFont());
		reportButtonBrowse.setText("Browse");
		gd = new GridData();
		reportButtonBrowse.setLayoutData(gd);	

		replayButton = new Button(group1, SWT.CHECK);
		replayButton.setFont(compParent.getFont());
		replayButton.setText("Replay");
		gd = new GridData();
		replayButton.setLayoutData(gd);	

    	replayFileName = new Text(group1, SWT.SINGLE | SWT.BORDER);
    	replayFileName.setFont(compParent.getFont());
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.horizontalSpan = 1;
    	replayFileName.setLayoutData(gd);

		replayButtonBrowse = new Button(group1, SWT.PUSH);
		replayButtonBrowse.setFont(compParent.getFont());
		replayButtonBrowse.setText("Browse");
		gd = new GridData();
		replayButtonBrowse.setLayoutData(gd);	
		
	}

	private void createGroupRecord(Group group) {
		GridData gd;
    	    	    	
		repRepButton = new Button(group, SWT.RADIO);
		repRepButton.setFont(compParent.getFont());
		repRepButton.setText("Record");
		gd = new GridData();
		gd.horizontalSpan = 3;
		repRepButton.setLayoutData(gd);	
		
		Group group1 = new Group(group,SWT.NONE);
		group1.setLayout(new GridLayout(3, false));
		group1.setText("File location");
		group1.setFont(compParent.getFont());
    	gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		group1.setLayoutData(gd);

    	recordFileName = new Text(group1, SWT.SINGLE | SWT.BORDER);
    	recordFileName.setFont(compParent.getFont());
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.horizontalSpan = 2;
    	recordFileName.setLayoutData(gd);
    	recordFileName.addListener(SWT.SELECTED, new Listener()  {
			
			@Override
			public void handleEvent(Event event) {
				System.out.println("Selection");
				setDirty(true);
			}
		});

		recordButtonBrowse = new Button(group1, SWT.PUSH);
		recordButtonBrowse.setFont(compParent.getFont());
		recordButtonBrowse.setText("Browse");
		gd = new GridData();
		recordButtonBrowse.setLayoutData(gd);			
	}

	@Override
	public String getName() {
		return "Intel testcase launch configuration";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		int mode;
		try {
			mode = configuration.getAttribute(IntelLaunchConfigurationConstants.MODES, 0);
			if (mode == 0)
				noneButton.setSelection(true);
			else if (mode == 1)
				replayButton.setSelection(true);
			else if (mode == 2 || mode == 3) {
				repRepButton.setSelection(true);
				if (mode == 3)
					reportButton.setSelection(true);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		System.out.println("Tabs: initialize");
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (noneButton.getSelection() == true)
			configuration.setAttribute(IntelLaunchConfigurationConstants.MODES, 0);
		else if (replayButton.getSelection() == true)
			configuration.setAttribute(IntelLaunchConfigurationConstants.MODES, 1);
		else if (repRepButton.getSelection() == true) {
			if (replayButton.getSelection() == true)
				configuration.setAttribute(IntelLaunchConfigurationConstants.MODES,3);
			else
				configuration.setAttribute(IntelLaunchConfigurationConstants.MODES,2);
		}
		System.out.println("Tabs: performApply");
		
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		System.out.println("Tabs: set defaults");
//		noneButton.setSelection(true);
//		reportFileName.setText("report\\my.rpt");
//		replayFileName.setText("replay\\my.rec");
//		recordFileName.setText("replay\\my.rec");	
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		return true;
	}

	@Override
	public boolean canSave() {
		return true;
	}
	
}
