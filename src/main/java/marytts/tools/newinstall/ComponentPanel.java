/**
 * 
 */
package marytts.tools.newinstall;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import marytts.tools.newinstall.objects.Component;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXSearchField.LayoutStyle;

/**
 * Ambiguous use of <i>Component</i>, the one denotes a swing component, the other one a marytts void/language component
 * 
 * 
 * @author Jonathan
 * 
 */
@Deprecated
public class ComponentPanel extends JPanel {

	private JLabel componentNameLabel;
	private JButton installButton;
	private JLabel status;
	private JXCollapsiblePane descriptionColPane;

	/**
	 * 
	 */
	public ComponentPanel(Component component) {
		initComponents(component);
	}

	public void initComponents(Component component) {

		Dimension componentPanelSize = new Dimension(500, 50);
		this.setPreferredSize(componentPanelSize);
		this.setMaximumSize(new Dimension(500, 80));
		// this.setMinimumSize(componentPanelSize);

		// setLayout(new GridBagLayout());
		setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

		// ### componentNameLabel ###
		this.componentNameLabel = new JLabel();
		this.componentNameLabel.setText("Dummyname");
		// GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		// gridBagConstraints_2.gridheight = 2;
		// gridBagConstraints_2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		// gridBagConstraints_2.anchor = java.awt.GridBagConstraints.WEST;
		// gridBagConstraints_2.weightx = 1.0;
		// gridBagConstraints_2.weighty = 0.5;
		// gridBagConstraints_2.insets = new java.awt.Insets(0, 5, 0, 0);
		GroupLayout componentNameLabelLayout = new GroupLayout(this);
		this.setLayout(componentNameLabelLayout);
		componentNameLabelLayout.setAutoCreateGaps(true);
		componentNameLabelLayout.setAutoCreateContainerGaps(true);

		// ### installButton ###
		this.installButton = new JButton();
		this.installButton.setText("INSTALL");
		this.installButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("To be implemented: Installation of component");
			}
		});
		// GridBagConstraints gridBagConstraints_1 = new java.awt.GridBagConstraints();
		// gridBagConstraints_1.gridx = 1;
		// gridBagConstraints_1.gridy = 1;
		// gridBagConstraints_1.weighty = 0.2;
		add(this.installButton);

		// ### availStateLabel ###
		this.status = new JLabel();
		this.status.setFont(new Font("Lucida Grande", 0, 10));
		// TODO only for testing
		this.status.setText("TEXT");
		// this.status.setText(component.getStatus().toString());
		// GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		// gridBagConstraints.gridx = 1;
		// gridBagConstraints.gridy = 0;
		// gridBagConstraints.anchor = GridBagConstraints.CENTER;
		// gridBagConstraints.weighty = 1.0;
		// gridBagConstraints.weightx = 1.0;
		// gridBagConstraints.insets = new Insets(1, 0, 0, 0);
		add(this.status);

		// ### description ###
		this.descriptionColPane = createDescriptionPane(component);

		// GridBagConstraints gridBagConstraints_3 = new java.awt.GridBagConstraints();
		// gridBagConstraints_3.gridx = 0;
		// gridBagConstraints_3.gridy = 2;
		// gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		// gridBagConstraints_3.weighty = 1.0;
		// gridBagConstraints.weightx = 1.0;
		// gridBagConstraints_3.insets = new Insets(2, 0, 0, 0);
		add(this.descriptionColPane);

		JToggleButton jToggleButton = new JToggleButton("Button");
		add(jToggleButton);
		/* @formatter:off */
		componentNameLabelLayout.setHorizontalGroup(componentNameLabelLayout.createSequentialGroup()
				.addGroup(componentNameLabelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(componentNameLabel)
					.addComponent(jToggleButton))
				.addGap(10)
				.addGroup(componentNameLabelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(status)
					.addComponent(installButton))
				);
		componentNameLabelLayout.setVerticalGroup(componentNameLabelLayout.createSequentialGroup()
				.addGroup(componentNameLabelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(componentNameLabel)
						.addComponent(status))
				.addGroup(componentNameLabelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(jToggleButton)
						.addComponent(installButton))
				);
		/* @formatter:on */

		// here
	}

	public JXCollapsiblePane createDescriptionPane(Component component) {

		JXCollapsiblePane descrCollapsiblePane = new JXCollapsiblePane();
		descrCollapsiblePane.setLayout(new GridBagLayout());
		descrCollapsiblePane.setAnimated(true);
		descrCollapsiblePane.setPreferredSize(new Dimension(30, 30));
		descrCollapsiblePane.setMaximumSize(new Dimension(200, 50));

		// TESTING
		JLabel label = new JLabel(
				"This is a test description. This is a test description. This is a test description. This is a test description. This is a test description. This is a test description. ");
		Font newFont = new Font(label.getFont().getName(), Font.PLAIN, 8);
		label.setFont(newFont);
		label.setForeground(Color.WHITE);

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(label);
		panel.setOpaque(true);

		return descrCollapsiblePane;
	}

	public static void main(String[] args) {
		new ComponentPanel(null);
	}

}