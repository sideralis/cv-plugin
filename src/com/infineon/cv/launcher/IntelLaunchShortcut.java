package com.infineon.cv.launcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.internal.core.model.CProject;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

@SuppressWarnings("restriction")
public class IntelLaunchShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		System.out.println("Launching shortcut (selection)");
		if (selection instanceof IStructuredSelection) {
			if (((IStructuredSelection) selection).getFirstElement() instanceof CProject) {
				System.out.println(((IStructuredSelection) selection).getFirstElement());
				CProject c = (CProject) ((IStructuredSelection) selection).getFirstElement();
				launch(mode, c);
			}
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		System.out.println("Launching shortcut (editor)");
		// launch(mode);
	}

	@SuppressWarnings("deprecation")
	private void launch(String mode, CProject project) {
		HashMap<String, String> arg = new HashMap<String, String>();

		ILaunchConfiguration config = findLaunchConfiguration(mode);
		
		IProject myProj = project.getProject();
		arg.put("REPORTFILE", "my.rpt");
		try {
			System.out.println("---");
			ICommand[] cmd = myProj.getDescription().getBuildSpec();
//			for (ICommand element : cmd) {
//				System.out.println(element.getBuilderName());
//				System.out.println(element.getArguments());
//				System.out.println(element);
//			}
			System.out.println("---");
			IManagedBuildInfo buildInfo = ManagedBuildManager.getBuildInfo(myProj);
			IConfiguration myConf = buildInfo.getDefaultConfiguration();
			
			System.out.println(myConf);
			System.out.println(myConf.getDescription());

			// Get configurations
			System.out.println("---");
			ICProjectDescription pDesc = CoreModel.getDefault().getProjectDescription(myProj);
			ICConfigurationDescription cfgDesc = pDesc.getActiveConfiguration();
			ICConfigurationDescription[] cfgDescs = pDesc.getConfigurations();
			for (ICConfigurationDescription e : cfgDescs)
				System.out.println(e.getId());
			// Change Configurations
//			pDesc.setActiveConfiguration(cfgDescs[0]);
//			CoreModel.getDefault().setProjectDescription(myProj, pDesc);
			
			System.out.println("---");
			IBuilder builder = myConf.getBuilder();
			System.out.println("Builder arg "+builder.getArguments());
			System.out.println("Builder autobuild :"+builder.getAutoBuildTarget());
			System.out.println("Builder build arg :"+builder.getBuildArguments());
			System.out.println("Builder inc build target :"+builder.getIncrementalBuildTarget());
			System.out.println(builder.getId());
			builder.setAutoBuildTarget("run");
			builder.setIncrementalBuildTarget("run");

			System.out.println(myProj.getPersistentProperty(new QualifiedName("org.eclipse.cdt.core", "activeConfiguration")));
			// myProj.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
			myProj.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, "org.eclipse.cdt.managedbuilder.core.genmakebuilder", arg, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private ILaunchConfiguration findLaunchConfiguration(String mode) {
		ILaunchConfiguration configuration = null;
		ILaunchConfigurationType configType = getIntelLaunchConfigType();

		ArrayList<LaunchConfiguration> candidateConfigs = (ArrayList<LaunchConfiguration>) Collections.EMPTY_LIST;
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
			candidateConfigs = new ArrayList<LaunchConfiguration>(configs.length);
			for (int i = 0; i < configs.length; i++) {
				System.out.println("Configuration: "+configs[i]);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		
		return null;
	}

	private ILaunchConfiguration createConfiguration(IBinary bin, /*ICDebugConfiguration debugConfig,*/ String mode) {
		ILaunchConfiguration config = null;
//		try {
//			String projectName = bin.getResource().getProjectRelativePath().toString();
//			ILaunchConfigurationType configType = getCLaunchConfigType();
//			ILaunchConfigurationWorkingCopy wc =
//				configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(bin.getElementName()));
//			wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, projectName);
//			wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, bin.getCProject().getElementName());
//			wc.setMappedResources(new IResource[] {bin.getResource().getProject()});
//			wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, (String) null);
//			wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, true);
//			wc.setAttribute(
//				ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE,
//				ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN);
//			wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_ID, debugConfig.getID());
//
//	        // Workaround for bug 262840: select the standard CDT launcher by default.
//	        HashSet<String> set = new HashSet<String>();
//	        set.add(mode);
//	        try {
//	            ILaunchDelegate preferredDelegate = wc.getPreferredDelegate(set);
//	            if (preferredDelegate == null) {
//                    wc.setPreferredLaunchDelegate(set, "org.eclipse.cdt.cdi.launch.localCLaunch");
//	            }
//	        } catch (CoreException e) {}
//			// End workaround for bug 262840
//	        
//			ICProjectDescription projDes = CCorePlugin.getDefault().getProjectDescription(bin.getCProject().getProject());
//			if (projDes != null)
//			{
//				String buildConfigID = projDes.getActiveConfiguration().getId();
//				wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_BUILD_CONFIG_ID, buildConfigID);				
//			}
//
//			// Load up the debugger page to set the defaults. There should probably be a separate
//			// extension point for this.
//			ICDebuggerPage page = CDebugUIPlugin.getDefault().getDebuggerPage(debugConfig.getID());
//			page.setDefaults(wc);
//			
//			config = wc.doSave();
//		} catch (CoreException ce) {
//			CDebugUIPlugin.log(ce);
//		}
		return config;
	}
	
	private ILaunchConfigurationType getIntelLaunchConfigType() {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("com.infineon.cv.launchConfigurationType");
	}
}
