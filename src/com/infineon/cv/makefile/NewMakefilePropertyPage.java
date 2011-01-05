package com.infineon.cv.makefile;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * NewMakefilePropertyPage extends PropertyPage, implements IWorkbenchPropertyPage,
 * which integrates a property page in the project properties. Via this page, users can 
 * configure several defined variables required in the makefile.
 * */
@SuppressWarnings("unchecked")
public class NewMakefilePropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	private IProject project;

	@Override
	protected Control createContents(Composite parent) {
		initData();
		if (getElement() instanceof IProject) {
			project = (IProject) getElement();
		}
//		System.out.println(project.getName());
		return init(parent);

	}

	private ParserMakefile parser;
	private ArrayList<String> defautValues;
	private TabItemMKEditor tab1;
	private ArrayList<DropDown> dropDowns;
	private LinkingTab tab3;
	private TabAdvanced tab4;
	private static HashMap<String, String> liens = new HashMap() {

		private static final long serialVersionUID = 9075004251281770798L;
		{
			put("Project Name", "exec");
		}
		{
			put("User assembly flags", "own_asflags");
		}
		{
			put("User compiler flags", "own_cflags");
		}
		{
			put("User linker flags", "own_lkflags");
		}
		{
			put("Own init file name", "own_init_s");
		}
		{
			put("ITCM base address", "itcm_base_address");
		}
		{
			put("DTCM base address", "dtcm_base_address");
		}
		{
			put("Source Files", "srcs");
		}
		{
			put("Source Directory", "srcdir");
		}
		{
			put("Path where to search for source file", "vpath");
		}
		{
			put("Include Dirs", "incdir");
		}
		{
			put("", "libunwanted");
		}
		{
			put("", "forbidden_defines");
		}
		{
			put("", "defines");
		}
		{
			put("", "exec_path");
		}

	};

	private Composite init(Composite parent) {
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		final TabFolder tabFolder = new TabFolder(sc, SWT.CANCEL);

		// first TabItem
		TabItem one = new TabItem(tabFolder, SWT.NONE);
		one.setText("Source");
		one.setToolTipText("This is tab one");
		Composite c1 = new Composite(tabFolder, SWT.NULL);
		RowLayout rowLayout1 = new RowLayout();
		rowLayout1.type = SWT.VERTICAL;
		c1.setLayout(rowLayout1);

		tab1 = new TabItemMKEditor(c1, getData4FirstTab());
		one.setControl(c1);

		// second TabItem: 1 listviewer(attribut "Defines" est vide), 4
		// dropdownList
		TabItem two = new TabItem(tabFolder, SWT.NONE);
		two.setText("Compiling");
		two.setToolTipText("This is tab two");
		Composite c2 = new Composite(tabFolder, SWT.NULL);
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		c2.setLayout(gridLayout2);

		HashMap<String, Arch> xml = getData4SecondTab();
		dropDowns = new ArrayList<DropDown>();
		for (String attribut : xml.keySet()) {
			dropDowns.add(new DropDown(c2, attribut, xml.get(attribut)
					.getValueList(), xml.get(attribut).getDefautValue()));
		}
		two.setControl(c2);

		TabItem three = new TabItem(tabFolder, SWT.NONE);
		three.setText("Linking");
		three.setToolTipText("This is tab three");
		Composite c3 = new Composite(tabFolder, SWT.NULL);
		RowLayout f3 = new RowLayout();
		f3.type = SWT.VERTICAL;
		c3.setLayout(new FillLayout());
		tab3 = new LinkingTab(c3, getData4ThirdTab());
		three.setControl(c3);

		// 3 Textfiels, 1 directory browser, 2 Textfields
		TabItem four = new TabItem(tabFolder, SWT.NONE);
		four.setText("Advanced");
		four.setToolTipText("This is tab four");
		Composite c4 = new Composite(tabFolder, SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		c4.setLayout(gridLayout);
		HashMap<String, String> advancedMap = getData4FourthTab();

		tab4 = new TabAdvanced(c4, advancedMap);
		four.setControl(c4);

		sc.setContent(tabFolder);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(tabFolder.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		/**** setting values avec le button validation par defaut de property page */

		return parent;

	}

	private void initData() {
		parser = new ParserMakefile(getMakefile());
		parser.readMakefile();
	}

	protected String getMakefile() {
		IProject project = (IProject) getElement();
		IFile makefile = project.getFile("makefile");
		return makefile.getLocation().toString();
	}

	private HashMap<String, ArrayList<String>> getData4FirstTab() {
		String exec = parser.getExec();
//		System.out.println(exec);
		ArrayList<String> srcs = parser.getSrcs();
		ArrayList<String> srcDir = parser.getSrcdir();
		ArrayList<String> vpath = parser.getVpath();
		ArrayList<String> incDir = parser.getIncdir();
		HashMap<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();
		ArrayList<String> execList = new ArrayList<String>();
		execList.add(exec);
		data.put("Project Name", execList);
		data.put("Source Files", srcs);
		data.put("Source Directory", srcDir);
		data.put("Path where to search for source file", vpath);
		data.put("Include Dirs", incDir);

		return data;

	}

	class Arch {
		ArrayList<String> valueList;
		int defautValue;

		public Arch(ArrayList<String> list, int index) {
			this.valueList = list;
			this.defautValue = index;
		}

		public ArrayList<String> getValueList() {
			return valueList;
		}

		public void setValueList(ArrayList<String> valueList) {
			this.valueList = valueList;
		}

		public int getDefautValue() {
			return defautValue;
		}

		public void setDefautValue(int defautValue) {
			this.defautValue = defautValue;
		}
	}

	private HashMap<String, Arch> getData4SecondTab() {
		HashMap<String, ArrayList<String>> xml = null;
		XMLParser parser = new XMLParser("ProjectSetting.xml");
		ArrayList<XMLNode> prjNodes = parser.getXMLNodes();
		for (XMLNode node : prjNodes) {
//			System.out.println(node.getProjectType());
			if (this.parser.getArch().contains(node.getProjectType().trim())) {
				xml = node.getAttributs();
				break;
			}
		}
		HashMap<String, Arch> result = new HashMap<String, Arch>();
		ArrayList<String> defautValues = this.parser.getArch();
		for (String key : xml.keySet()) {
			int index = 0;
			for (String value : xml.get(key)) {
				if (defautValues.contains(value)) {
					defautValues.remove(value);
					break;
				}
				index++;
			}
			result.put(key, new Arch(xml.get(key), index));
		}
		this.defautValues = defautValues;
		return result;
	}

	private HashMap<String, Integer> getData4ThirdTab() {
		File mkLocation = new File(getMakefile());
		File libLoc = mkLocation;
		File[] cFiles;
		do {
			libLoc = new File(libLoc.getParent());
			cFiles = libLoc.listFiles(new FilenameFilter() {
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith("_lib");

				}
			});
//			System.out.println("");
		} while (cFiles.length == 0);
		
		HashMap<String, Integer> linkings = new HashMap<String, Integer>();
		File dir = cFiles[0];

		File[] libDirs = dir.listFiles(new FileFilter() {

			public boolean accept(File arg0) {
				return arg0.isDirectory() && arg0.getName().startsWith("_lib_");
			}
		});
		for (File file : libDirs) {
			String[] libs = file.list();
			for (String lib : libs) {
				if (parser.getLibunwanted().contains(lib)) {
					linkings.put(lib, 0);
				} else
					linkings.put(lib, 1);
			}
		}
		return linkings;
	}

	private HashMap<String, String> getData4FourthTab() {

		String asFlags = parser.getOwn_asflags();
		String cFlags = parser.getOwn_cflags();
		String lFlags = parser.getOwn_lkflags();
		String itcm = parser.getItcm_base_address();
		String dtcm = parser.getDtcm_base_address();
		String initFile = parser.getOwn_init_s();
		HashMap<String, String> defaut = new HashMap<String, String>();
		defaut.put("User assembly flags", asFlags);
		defaut.put("User compiler flags", cFlags);
		defaut.put("User linker flags", lFlags);
		defaut.put("Own init file name", initFile);// .getPath());
		defaut.put("ITCM base address", "0x".concat(itcm));// Long.toHexString(itcm)));
		defaut.put("DTCM base address", "0x".concat(dtcm));// Long.toHexString(dtcm)));
		return defaut;
	}
	/**
	 * Return the default values
	 * @return The item default values
	 */
	public ArrayList<String> getDefautMKValues() {
		return this.defautValues;
	}

	public boolean performOk() {

		HashMap<String, ArrayList<String>> outputs1 = tab1.getoutputs();
		for (String key : outputs1.keySet()) {
			for (String value : outputs1.get(key)) {
//				System.out.println("key:" + key + "  " + value);
			}
		}

		// get values in tab2
		HashMap<String, String> outputs2 = new HashMap<String, String>();
		for (DropDown dp : dropDowns) {
			if (dp.getName().equals("mem_scatter")
					&& dp.getValue().equals("OWN")) {
				parser.setMy_scf(dp.getOwnScatterFile());
			} else {
				outputs2.put(dp.getName(), dp.getValue());
			}

		}
		parser.setArch(new ArrayList<String>(outputs2.values()));

		// set unwantedlibs, values in Tab3
		ArrayList<String> outputs3 = tab3.getOutputs();
		parser.setLibunwanted(outputs3);
//		for (String key : outputs3) {
//			System.out.println(key);
//		}

		// set values in Tab4
		HashMap<String, String> outputs4 = tab4.getOutputs();
//		for (String key : outputs4.keySet()) {
//			System.out.println("key:" + key + "  " + outputs4.get(key));
//		}
		// set values in Tab1,Tab2,Tab4
		// Tab2 ne s'agit que de l'attribut "ARCH"
		parser.setDefautMKValues(getDefautMKValues());

		HashMap<String, Object> outAll = new HashMap<String, Object>();
		outAll.putAll(outputs1);
		outAll.putAll(outputs4);
		Method[] methods = parser.getClass().getMethods();
		String variableName;
		for (String name : outAll.keySet()) {
			for (Method method : methods) {
				if (method.getName().contains("set")) {
					variableName = method.getName().substring(3).toLowerCase();
					if (liens.get(name).equals(variableName)) {
						try {
//							System.out.println(variableName);
//							System.out.println(outAll.get(name).getClass().getName());
							method.invoke(parser, outAll.get(name));
							break;
						} catch (IllegalArgumentException e1) {
							e1.printStackTrace();
						} catch (IllegalAccessException e1) {
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							e1.printStackTrace();
						}
					}
				}

			}
		}
		parser.writeMakefile();
		return super.performOk();
	}

}
