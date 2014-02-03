/**
 * 
 */
package marytts.tools.newinstall.gui;

import javax.swing.JFrame;

/**
 * @author Jonathan
 *
 */
public class LogViewFrame extends JFrame {

	public LogViewFrame() {
		setAlwaysOnTop(false);
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Log View");
		setResizable(true);
		setVisible(true);
	}
	
	

	public static void main(String[] args) {
		new LogViewFrame();
	}

}
