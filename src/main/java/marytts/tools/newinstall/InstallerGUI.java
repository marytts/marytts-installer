/**
 * 
 */
package marytts.tools.newinstall;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import marytts.tools.newinstall.objects.Component;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
/**
 * @author Jonathan
 * 
 */
@Deprecated
public class InstallerGUI extends JFrame {

	// data
	private Installer installer;
	private JScrollPane componentGroupScrollPane;

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

		JPanel componentControlPanel = new JPanel();
		componentControlPanel.setLayout(null);
		componentControlPanel.setBounds(13, 61, 647, 77);

		JLabel componentsControlLabel = new JLabel("Use the following controls to filter the component list:");
		componentsControlLabel.setBounds(159, 0, 344, 16);
		componentControlPanel.add(componentsControlLabel);

		// locale
		JLabel localeLabel = new JLabel("Locale");
		localeLabel.setBounds(27, 19, 50, 16);
		componentControlPanel.add(localeLabel);
		JComboBox localeComboBox = new JComboBox();
		localeComboBox.setBounds(6, 34, 92, 34);
		componentControlPanel.add(localeComboBox);

		// type
		JLabel typeLabel = new JLabel("Type");
		typeLabel.setBounds(131, 19, 50, 16);
		componentControlPanel.add(typeLabel);
		JComboBox typeComboBox = new JComboBox();
		typeComboBox.setBounds(110, 34, 92, 34);
		componentControlPanel.add(typeComboBox);

		// gender
		JLabel genderLabel = new JLabel("Gender");
		genderLabel.setBounds(234, 19, 50, 16);
		componentControlPanel.add(genderLabel);
		JComboBox genderComboBox = new JComboBox();
		genderComboBox.setBounds(213, 34, 92, 34);
		componentControlPanel.add(genderComboBox);

		// state
		JLabel stateLabel = new JLabel("State");
		stateLabel.setBounds(438, 19, 38, 16);
		componentControlPanel.add(stateLabel);
		JComboBox stateComboBox = new JComboBox();
		stateComboBox.setBounds(411, 34, 92, 34);
		componentControlPanel.add(stateComboBox);

		// testing
		// JXCollapsiblePane descriptionPane = ComponentPanel.createDescriptionPane(this.installer
		// .getComponentByName("cmu-slt-hsmm"));
		// descriptionPane.setBounds(200, 200, 200, 200);
		//
		// Action toggleAction = descriptionPane.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION);
		// toggleAction.putValue(JXCollapsiblePane.COLLAPSE_ICON, UIManager.getIcon("Tree.collapsedIcon"));
		// toggleAction.putValue(JXCollapsiblePane.EXPAND_ICON, UIManager.getIcon("Tree.expandedIcon"));
		// JToggleButton toggleButt = new JToggleButton(toggleAction);
		// toggleButt.setText("expand");
		// toggleButt.setBounds(50, 50, 50, 50);
		//
		// contentPane.add(toggleButt);
		// contentPane.add(descriptionPane);
		// end testing

		JPanel componentGroupPanel = new JPanel();
		componentGroupPanel.setLayout(new javax.swing.BoxLayout(componentGroupPanel, javax.swing.BoxLayout.Y_AXIS));

		JScrollPane componentScrollPane = new JScrollPane(componentGroupPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		componentScrollPane.setBounds(13, 150, 647, 300);
		componentScrollPane.setViewportView(componentGroupPanel);

		// GLOBAL

		JLabel maryPathLabel = new JLabel("Path to Marytts installation folder");
		maryPathLabel.setBounds(192, 19, 221, 16);

		JTextField maryPathTextField = new JTextField();
		maryPathTextField.setBounds(415, 13, 134, 28);
		maryPathTextField.setColumns(10);

		// JFileChooser maryPathFileChooser = new JFileChooser();

		JButton maryPathButton = new JButton("Choose dir");
		maryPathButton.setBounds(551, 14, 117, 29);

		JButton logViewButton = new JButton("Open Log view");
		logViewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new LogViewFrame();
			}
		});
		logViewButton.setBounds(551, 55, 117, 29);

		// fillComboBoxes(localeComboBox, typeComboBox, genderComboBox, stateComboBox);

		fillComponentGroupPanel(componentGroupPanel);

		contentPane.add(maryPathTextField);
		contentPane.add(maryPathLabel);
		contentPane.add(maryPathButton);
		contentPane.add(logViewButton);
		contentPane.add(componentScrollPane);
		contentPane.add(componentControlPanel);

		// pack();
		validate();
		repaint();
	}

	/**
	 * @param componentGroupPanel
	 * @param componentTableModel
	 * @return
	 */
	private boolean fillComponentGroupPanel(JPanel componentGroupPanel) {

		List<Component> resources = this.installer.getAvailableComponents();

		if (!(resources == null)) {
			for (Component oneComponent : resources) {
				componentGroupPanel.add(new ComponentPanel(oneComponent));
				componentGroupPanel.add(Box.createVerticalGlue());
			}
			return true;
		}

		return false;

	}

	/**
	 * TODO remove hardcodings, make generic
	 * 
	 * @param localeComboBox
	 * @param typeComboBox
	 * @param genderComboBox
	 * @param stateComboBox
	 * @return
	 */
	private boolean fillComboBoxes(JComboBox localeComboBox, JComboBox typeComboBox, JComboBox genderComboBox,
			JComboBox stateComboBox) {

		// localeComboBox.addItem("de");
		// localeComboBox.addItem("en-gb");
		// localeComboBox.addItem("en-us");
		// localeComboBox.addItem("it");
		// localeComboBox.addItem("ru");
		// localeComboBox.addItem("te");
		// localeComboBox.addItem("tr");

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
		setSize(674, 641);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Component Installer");
		setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args) {
		Installer installer = new Installer();
		new InstallerGUI(installer);
	}
}
