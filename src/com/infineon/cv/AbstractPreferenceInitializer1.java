package com.infineon.cv;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * ToolDrive Preference initialization class
 * 
 * @author zhaoxin
 * 
 * TODO this class can be removed as there is no need any more to select the location of tools
 * 
 */
public class AbstractPreferenceInitializer1 extends AbstractPreferenceInitializer {

	/**
	 * Constructor
	 */
	public AbstractPreferenceInitializer1() {

	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = InfineonActivator.getDefaut().getPreferenceStore();
		store.setDefault(PreferenceConstants.INFINEON_TOOLVIEW_DRIVE, "U:\\");

	}

}
