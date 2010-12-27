package Tests;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

public class addSymbols {
	public static void main(String[] args) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject[] projects = workspace.getRoot().getProjects();
		ICProjectDescription prjDesc;
		for (IProject project : projects) {
			prjDesc = CoreModel.getDefault().getProjectDescription(project);
			ICConfigurationDescription[] descs = prjDesc.getConfigurations();
			for(ICConfigurationDescription desc : descs){
            IConfiguration iConfig = ManagedBuildManager.getConfigurationForDescription(desc);            
            IOption newSymbol = iConfig.getToolChain().getTool(null).createOption(null,"symbolTestName",null,false);
			try {				
				newSymbol.setValue("symbolTestValue");
			} catch (BuildException e) {
				e.printStackTrace();
			}
			}}

	}
		}

