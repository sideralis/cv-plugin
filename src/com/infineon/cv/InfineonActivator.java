package com.infineon.cv;

import java.util.prefs.Preferences;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
/**
 * 
 * @author gautier
 * Activator class
 */
public class InfineonActivator extends AbstractUIPlugin {
	/** The plugin id */
	public static final String PLUGIN_ID = "com.infineon.cv";
	private static InfineonActivator plugin;
	private IEclipsePreferences configPrefs;

	/**
	 * The constructor
	 */
	public InfineonActivator() {

	}
	/**
	 * Called when plugin is launched
	 * @param context the bundle context
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}
	/**
	 * Return the plugin object
	 * @return the plugin object
	 */
	public static InfineonActivator getDefaut() {
		return plugin;

	}

//	public static ImageDescriptor getImangeDescriptor(String path) {
//		return imageDescriptorFromPlugin(PLUGIN_ID, path);
//	}
	/**
	 * Return the Preferences of the plugin (create it if it does not yet exist)
	 * @return The preferences of the plugin
	 */
	public Preferences getConfigPrefs() {
		if (configPrefs == null) {
			configPrefs = new ConfigurationScope().getNode(PLUGIN_ID);
		}
		return (Preferences) configPrefs;
	}
	/**
	 * Stop the plugin
	 * @param context the bundle context
	 */
	public void stop(BundleContext context) throws Exception {
		saveConfigPrefs();
		plugin = null;
		super.stop(context);
	}
	/**
	 * Save the preferences
	 */
	public void saveConfigPrefs() {
		if (configPrefs != null) {
			try {
				configPrefs.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
	}
}
