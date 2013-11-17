/**
 * 
 */
package marytts.tools.newinstall;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import marytts.tools.newinstall.objects.Component;

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

		/*
		 * #########TAB1########
		 */
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
		DefaultListModel componentListModel = new DefaultListModel();

		fillComponentList(componentListModel);

		JList componentList = new JList(componentListModel);
		componentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		componentList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		componentList.setVisibleRowCount(-1);
		componentList.setBounds(56, 141, 106, 163);

		// tab1ScrollPane.setEnabled(false);
		JScrollPane tab1ScrollPane = new JScrollPane();
		tab1ScrollPane.setViewportView(componentList);

		// GENERAL TAB1
		JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tab1Panel, tab1ScrollPane);
		jSplitPane.setDividerLocation(140);
		jSplitPane.setEnabled(false);

		// TODO fix separator not visible problem
		JSeparator tab1ControlsSeparator = new JSeparator(SwingConstants.HORIZONTAL);
		tab1ControlsSeparator.setBackground(Color.BLACK);
		tab1ControlsSeparator.setBounds(16, 69, 601, 3);
		tab1ControlsSeparator.setPreferredSize(new Dimension(600, 3));
		tab1ControlsSeparator.setForeground(Color.BLACK);
		tab1Panel.add(tab1ControlsSeparator);

		JTextArea componentDescrTextArea = new JTextArea();
		componentDescrTextArea.setLineWrap(true);
		componentDescrTextArea.setBounds(16, 75, 601, 52);
		componentDescrTextArea.setEditable(false);

		JScrollPane componentDescrTextAreaScrollPane = new JScrollPane();
		componentDescrTextAreaScrollPane.setViewportView(componentDescrTextArea);
		// for testing
		componentDescrTextAreaScrollPane.setBounds(16, 75, 601, 52);
		tab1Panel.add(componentDescrTextAreaScrollPane);

		/*
		 * #########TAB2########
		 */
		JPanel tab2Panel = new JPanel();
		JTextArea logTextArea = new JTextArea();
		tab2Panel.add(logTextArea);
		logTextArea.setEditable(false);
		// TODO fix size problem
		Dimension d = tab2Panel.getPreferredSize();
		logTextArea.setPreferredSize(d);
		logTextArea.setText("blablab");

		// GLOBAL

		JLabel maryPathLabel = new JLabel("Path to Marytts installation folder");
		maryPathLabel.setBounds(192, 19, 221, 16);
		contentPane.add(maryPathLabel);

		JTextField maryPathTextField = new JTextField();
		maryPathTextField.setBounds(415, 13, 134, 28);
		contentPane.add(maryPathTextField);
		maryPathTextField.setColumns(10);

		JFileChooser maryPathFileChooser = new JFileChooser();

		JButton maryPathButton = new JButton("Choose dir");
		maryPathButton.setBounds(551, 14, 117, 29);
		contentPane.add(maryPathButton);

		JTabbedPane jTabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		jTabbedPane.setBounds(6, 47, 662, 524);
		jTabbedPane.addTab("Installable components", jSplitPane);
		jTabbedPane.addTab("Log View", tab2Panel);

		// FILLING COMBOBOXES
		// HashMap<String, JComboBox> comboBoxes = new HashMap<String, JComboBox>();
		// comboBoxes.put("state", stateComboBox);
		// comboBoxes.put("gender", genderComboBox);
		// comboBoxes.put("locale", localeComboBox);
		// comboBoxes.put("type", typeComboBox);

		// fillComboBoxes(localeComboBox, typeComboBox, genderComboBox, stateComboBox);
		fillLogView(logTextArea);

		// sets up the listSelectionListener that fills the componentDescrTextArea with the details of the selected component
		// entry
		setUpListSelectionListener(componentList, componentDescrTextArea);

		contentPane.add(jTabbedPane);

		validate();
		repaint();

	}

	private void setUpListSelectionListener(final JList componentList, final JTextArea componentDescrTextArea) {

		ListSelectionModel selectionModel = componentList.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				String componentName = (String) componentList.getSelectedValue();
				Component c = installer.getComponentByName(componentName);

				if (c != null) {
					componentDescrTextArea.setText(c.toString());
				} else {
					System.err.println("Compnent with " + componentName + " was not found!");
				}

			}
		});
	}

	/**
	 * @param logTextArea
	 */
	private boolean fillLogView(JTextArea logTextArea) {
		// TODO Auto-generated method stub

		return false;
	}

	/**
	 * @param componentListModel
	 * @return
	 */
	private boolean fillComponentList(DefaultListModel componentListModel) {

		List<Component> resources = this.installer.getAvailableVoices();

		if (!(resources == null)) {
			for (Component oneComponent : resources) {

				componentListModel.addElement(oneComponent.getName());
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
		setSize(674, 594);
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
