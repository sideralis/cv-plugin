package com.infineon.cv;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
/**
 * ProgressBarToolView run clearcase commands for mapping files on the server to
 * local driver in a snapshot view.
 * **/

public class ProgressBarToolView extends JPanel implements ActionListener,
		PropertyChangeListener {

	private static final long serialVersionUID = -8237476147515203960L;
	private JProgressBar progressBar;
	private JButton startButton;
	// private JTextArea taskOutput;
	private Task task;

	private String viewTag;

	class Task extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() {
			try {
				// String viewTag = "test";
				// window alert: do you want to create the tool view?
				String host = InetAddress.getLocalHost().getHostName();
				String command_mk = "\"C:\\Program Files\\Rational\\ClearCase\\bin\\cleartool\" mkview -snapshot -tag "
						+ viewTag
						+ " -host "
						+ host
						+ " -hpath \\\\"
						+ host
						+ "\\Clearcase_Storage\\"
						+ viewTag
						+ ".vws -gpath \\\\"
						+ host
						+ "\\Clearcase_Storage\\"
						+ viewTag
						+ ".vws -stgloc comneon.viwi \\\\"
						+ host
						+ "\\Clearcase_Storage\\" + viewTag + ".vws";

				Runtime runtime = Runtime.getRuntime();
				Process p1 = runtime.exec(command_mk);
				p1.waitFor();
				String s;
				// edit configspec
				String configspecPath = "\"config_spec\"";
				String command_md2 = "cleartool setcs -tag " + viewTag
						+ " ".concat(configspecPath);
				try {
					// progress bar for installation
					int progress = 0;
					int download = 0;
					int sizeOnDisk = 1728741376;
					setProgress(0);
					Process p2 = runtime.exec(command_md2, null, new File(
							"C:\\Clearcase_Storage\\" + viewTag + ".vws"));
					BufferedReader stdOut = new BufferedReader(
							new InputStreamReader(p2.getInputStream()));
					while ((s = stdOut.readLine()) != null && progress < 100) {
						System.out.println(s);
						if (s.contains("Loading")) {
							int begin = s.lastIndexOf('(');
							int end = s.indexOf("bytes");
							char[] bytes = new char[end - begin - 2];
							s.getChars(++begin, end - 1, bytes, 0);
							download += Integer.parseInt(new String(bytes));
							progress = download / (sizeOnDisk / 100);
							setProgress(progress);
						}
					}
					setProgress(100);
				} catch (Exception e) {
					System.out.println("Error occured:");
					e.printStackTrace();
					return null;
				}
				;
				// sharing the tool view file
				runtime.exec("net share test=C:\\Clearcase_Storage\\"
								+ viewTag);
				// mapping the tool view file to U:
				runtime.exec("net use U: \\\\" + host + "\\" + viewTag
						+ " /PERSISTENT:YES");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			startButton.setEnabled(true);
			setCursor(null); // turn off the wait cursor
			// taskOutput.append("Done!\n");
		}
	}

	public ProgressBarToolView(String viewTag) {
		super(new BorderLayout());
		this.viewTag = viewTag;
		startButton = new JButton("Start");
		startButton.setActionCommand("start");
		startButton.addActionListener(this);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		// taskOutput = new JTextArea(5, 20);
		// taskOutput.setMargin(new Insets(5,5,5,5));
		// taskOutput.setEditable(false);

		JPanel panel = new JPanel();
		panel.add(startButton);
		panel.add(progressBar);

		add(panel, BorderLayout.PAGE_START);
		// add(new JScrollPane(taskOutput), BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

	}

	public void actionPerformed(ActionEvent evt) {
		startButton.setEnabled(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		task = new Task();
		task.addPropertyChangeListener(this);
		task.execute();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
			// taskOutput.append(String.format(
			// "Completed %d%% of task.\n", task.getProgress()));
		}
	}

	private static void createAndShowGUI(String viewTag) {
		final JFrame frame = new JFrame(
				"No Tool view installed, click on start to install");
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JComponent newContentPane = new ProgressBarToolView(viewTag);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		frame.pack();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				frame.dispose();
			}
		});
	}

	public static void create(final String viewTag) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI("test");
			}
		});
	}

}
