package com.infineon.cv.wizard;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;

@SuppressWarnings("restriction")
public class IntelProjectManager {
	/**
	 * See http://cdt-devel-faq.wikidot.com/#toc25
	 * 
	 * @param name
	 * @param path
	 * @param monitor
	 */
	@SuppressWarnings("restriction")
	public void createIntelProject(String name, String path, IProgressMonitor monitor) {
		IWorkspaceRoot wrkSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject newProjectHandle = wrkSpaceRoot.getProject(name);
		monitor.beginTask("Creating Intel project", 0);
		IProjectDescription projDesc = ResourcesPlugin.getWorkspace().newProjectDescription(newProjectHandle.getName());
		if (!("".equals(path)) && path != null) {
			Path myPath = new Path(path);
			projDesc.setLocation(myPath);
		}
		try {
			IProject cdtProj = CCorePlugin.getDefault().createCDTProject(projDesc, newProjectHandle, monitor);

			CProjectNature.addCNature(cdtProj, monitor);
			ICProjectDescriptionManager mgr = CoreModel.getDefault().getProjectDescriptionManager();
			ICProjectDescription des = mgr.getProjectDescription(cdtProj, true);
			if (des != null)
				return; // C project description already exists
			des = mgr.createProjectDescription(cdtProj, true);

			ManagedBuildInfo info = ManagedBuildManager.createBuildInfo(cdtProj);
			IProjectType projType = ManagedBuildManager.getExtensionProjectType("com.infineon.cv.projectTypeBHades");
//			IToolChain toolChains[] = new IToolChain[4];
//			toolChains[0] = ManagedBuildManager.getExtensionToolChain("com.infineon.cv.toolChain.hadesGnumake");
//			toolChains[1] = ManagedBuildManager.getExtensionToolChain("com.infineon.cv.toolChain.HadesMake");
//			toolChains[2] = ManagedBuildManager.getExtensionToolChain("com.infineon.cv.toolChain.hadesCompile");
//			toolChains[3] = ManagedBuildManager.getExtensionToolChain("com.infineon.cv.toolChain.hadesRun");
			IToolChain toolChain = ManagedBuildManager.getExtensionToolChain("com.infineon.cv.toolChain.hadesCompile");
			
			ManagedProject mProj = new ManagedProject(cdtProj, projType);
			info.setManagedProject(mProj);

//			 IConfiguration[] configs = projType.getConfigurations(); // *** BG
			IConfiguration[] configs = ManagedBuildManager.getExtensionConfigurations(toolChain, projType);

			for (IConfiguration icf : configs) {
				if (!(icf instanceof Configuration)) {
					continue;
				}
				Configuration cf = (Configuration) icf;

				String id = ManagedBuildManager.calculateChildId(cf.getId(), null);
				Configuration config = new Configuration(mProj, cf, id, false, true);

				ICConfigurationDescription cfgDes = des.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, config.getConfigurationData());
				config.setConfigurationDescription(cfgDes);
				config.exportArtifactInfo();

				IBuilder bld = config.getEditableBuilder();
				if (bld != null) {
					bld.setManagedBuildOn(true);
				}

				config.setName(config.getName());
				config.setArtifactName(cdtProj.getName());

			}

			mgr.setProjectDescription(cdtProj, des);

		} catch (OperationCanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
