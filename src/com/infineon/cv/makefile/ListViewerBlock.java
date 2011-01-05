package com.infineon.cv.makefile;

import java.util.ArrayList;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * ListViewerBlock creates a subset components in TabItemMKEditor.
 */
public class ListViewerBlock {
	Composite parent;
	ArrayList<String> input;
	String labelText;
	ListViewer listViewer;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            The reference to the parent
	 * @param labelText
	 *            The name of object
	 * @param input
	 *            The content of the object
	 */
	public ListViewerBlock(Composite parent, String labelText, ArrayList<String> input) {
		FormLayout formlayout = new FormLayout();
		parent.setLayout(formlayout);
		this.parent = parent;
		this.input = input;
		this.labelText = labelText;
		init();
		addButtons4Src();

	}

	class PathsContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object arg0) {
			return input.toArray();
		}

		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {

		}

		public void removePath(String s) {
			input.remove(s);
		}

		public void addPath(String s) {
			input.add(s);
		}

	}

	private void init() {

		FormLayout formLayout = new FormLayout();
		parent.setLayout(formLayout);
		Label label = new Label(parent, SWT.LEFT);
		label.setText(labelText);
		FormData data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(0, 5);
		data.bottom = new FormAttachment(50, -5);
		data.right = new FormAttachment(30, -5);
		label.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(label, 5);
		data.bottom = new FormAttachment(50, -5);
		data.right = new FormAttachment(60, -5);

		Composite composite1 = new Composite(parent, SWT.NONE);
		composite1.setLayout(new FillLayout());
		composite1.setLayoutData(data);

		listViewer = new ListViewer(composite1, SWT.BORDER);
		listViewer.setContentProvider(new PathsContentProvider());
		listViewer.setInput(input);
		listViewer.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				return element.toString();
			}
		});
		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				// System.out.println("selected: " +
				// selection.getFirstElement());
			}
		});

		/******** add buttons *********/
		Composite composite3 = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite3.setLayout(gridLayout);
		final StringButtonFieldEditor browserAdd4Src;
		Button buttonAdd4Src;
		Button buttonRemove4Src;
		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(composite1, 5);
		data.bottom = new FormAttachment(80, -5);
		data.right = new FormAttachment(80, -5);

		if (labelText == "Source Files") {
			browserAdd4Src = new FileFieldEditor("FieldEditor", "", composite3);
		}

		else {
			browserAdd4Src = new DirectoryFieldEditor("DirectoryChooser", "", composite3);
		}
		composite3.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(composite3, 5);
		data.bottom = new FormAttachment(100, -5);
		data.right = new FormAttachment(100, -5);
		Composite composite2 = new Composite(parent, SWT.NULL);
		gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite2.setLayout(gridLayout);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		buttonAdd4Src = new Button(composite2, SWT.PUSH);
		buttonAdd4Src.setData(gridData);
		buttonAdd4Src.setText("Add");
		buttonAdd4Src.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				input.add(browserAdd4Src.getStringValue());
				listViewer.refresh(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		gridData = new GridData(GridData.FILL_BOTH);
		buttonRemove4Src = new Button(composite2, SWT.PUSH);
		buttonRemove4Src.setData(gridData);
		buttonRemove4Src.setText("Remove");
		buttonRemove4Src.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
				String element = (String) selection.getFirstElement();
				if (event == null) {

				}
				input.remove(element);
				System.out.println("Include Directory: " + element + "is removed!");
				listViewer.refresh(true);

			}
		});
		composite2.setLayoutData(data);
	}

	private void addButtons4Src() {
	}

	/**
	 * Return the output of the object
	 * 
	 * @return This represents the selected sources to be compiled
	 */
	public ArrayList<String> getOutputs() {
		ArrayList<String> outputs = new ArrayList<String>();
		Object[] elements = ((PathsContentProvider) listViewer.getContentProvider()).getElements(null);
		for (Object o : elements) {
			outputs.add(o.toString());
		}
		return outputs;
	}
}
