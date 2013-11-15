/**
 * 
 */
package marytts.tools.newinstall;

import java.awt.Container;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 * @author Jonathan
 * 
 */
public class InstallerGUI extends JFrame {

	// data
	private Installer installer;

	// controller -> not sure if needed
	// private InstallerController installerController;

	/**
	 * constructor
	 */
	public InstallerGUI(Installer installer) {
		if (installer == null) {
			System.err.println("Installer should not be null at this point!");
			installer = new Installer();
		}

		this.installer = installer;
		// this.installerController = new InstallerController();

		initFrame();
		initComponents();

	}

	private void initComponents() {

		Container contentPane = this.getContentPane();
		contentPane.setLayout(null);

		// TOP TAB1
		JPanel tab1Panel = new JPanel();
		tab1Panel.setLayout(null);

		JLabel componentListControlLabel = new JLabel("Use the following controls to filter the component list:");
		componentListControlLabel.setBounds(159, 0, 344, 16);
		tab1Panel.add(componentListControlLabel);

		// locale
		JLabel localeLabel = new JLabel("Locale");
		localeLabel.setBounds(27, 19, 50, 16);
		tab1Panel.add(localeLabel);
		JComboBox localeComboBox = new JComboBox();
		localeComboBox.setBounds(6, 34, 92, 34);
		tab1Panel.add(localeComboBox);

		// type
		JLabel typeLabel = new JLabel("Type");
		typeLabel.setBounds(131, 19, 50, 16);
		tab1Panel.add(typeLabel);
		JComboBox typeComboBox = new JComboBox();
		typeComboBox.setBounds(110, 34, 92, 34);
		tab1Panel.add(typeComboBox);

		// gender
		JLabel genderLabel = new JLabel("Gender");
		genderLabel.setBounds(234, 19, 50, 16);
		tab1Panel.add(genderLabel);
		JComboBox genderComboBox = new JComboBox();
		genderComboBox.setBounds(213, 34, 92, 34);
		tab1Panel.add(genderComboBox);

		// state
		JLabel stateLabel = new JLabel("State");
		stateLabel.setBounds(438, 19, 38, 16);
		tab1Panel.add(stateLabel);
		JComboBox stateComboBox = new JComboBox();
		stateComboBox.setBounds(411, 34, 92, 34);
		tab1Panel.add(stateComboBox);

		// BOTTOM TAB1
		JList jList = new JList();
		jList.setBounds(56, 141, 106, 163);

		JScrollPane tab1ScrollPane = new JScrollPane();
		// tab1ScrollPane.setEnabled(false);
		tab1ScrollPane.add(jList);

		// GENERAL TAB1
		JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tab1Panel, tab1ScrollPane);
		jSplitPane.setDividerLocation(70);
		jSplitPane.setEnabled(false);

		// TAB2
		JPanel tab2Panel = new JPanel();

		// GLOBAL
		JTabbedPane jTabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		jTabbedPane.setBounds(6, 66, 662, 505);
		jTabbedPane.addTab("Installable components", jSplitPane);
		jTabbedPane.addTab("Log View", tab2Panel);

		// FILLING COMBOBOXES
		// HashMap<String, JComboBox> comboBoxes = new HashMap<String, JComboBox>();
		// comboBoxes.put("state", stateComboBox);
		// comboBoxes.put("gender", genderComboBox);
		// comboBoxes.put("locale", localeComboBox);
		// comboBoxes.put("type", typeComboBox);

		// fillComboBoxes(localeComboBox, typeComboBox, genderComboBox, stateComboBox);

		contentPane.add(jTabbedPane);

		validate();
		repaint();

	}

	/**
	 * TODO remove hardcoded
	 * 
	 * @param localeComboBox
	 * @param typeComboBox
	 * @param genderComboBox
	 * @param stateComboBox
	 * @return
	 */
	private boolean fillComboBoxes(JComboBox localeComboBox, JComboBox typeComboBox, JComboBox genderComboBox,
			JComboBox stateComboBox) {

		//
		// if (!comboBoxes.isEmpty()) {
		// Entry oneEntry;
		// for (Iterator<Entry<String, JComboBox>> it = comboBoxes.entrySet().iterator(); it.hasNext();) {
		// oneEntry = it.next();
		//
		// if (oneEntry.getKey().equals("")) {
		//
		// }
		// }
		// }
		return false;
	}

	/**
	 * configures the jFrame
	 */
	private void initFrame() {
		setAlwaysOnTop(false);
		setSize(674, 594);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Component Installer");
		setResizable(false);
		setVisible(true);
	}

}
