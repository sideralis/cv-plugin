package com.infineon.cv;

import javax.swing.JOptionPane;

/**
 * ToolViewAlert launches a MessageDialog in case that users require a unavailable tool view.
 * */
public class ToolViewAlert {
	
	public void showAlert() {
		javax.swing.JOptionPane.showMessageDialog(null,
				"Attention! No available tool view on your machine!", "Error",
				JOptionPane.ERROR_MESSAGE);
//		JFrame aWindow = new JFrame("Warning");
//		int windowWidth = 400;
//		int windowHeight = 150;
//		aWindow.setBounds(50, 100, windowWidth, windowHeight);
//		// aWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		JLabel label = new JLabel(
//				"Tool view has not been installed on your machine!");
//		aWindow.add(label);
//		aWindow.setVisible(true);
	}
}
