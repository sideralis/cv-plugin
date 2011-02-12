package com.infineon.cv.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class IntelLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	public IntelLaunchConfigurationTabGroup() {
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { new IntelMainTab(mode), new CommonTab()};
		setTabs(tabs);
	}
}
