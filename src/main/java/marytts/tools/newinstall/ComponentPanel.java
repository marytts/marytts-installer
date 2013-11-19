/**
 * 
 */
package marytts.tools.newinstall;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import marytts.tools.newinstall.objects.Component;

/**
 * Ambiguous use of <i>Component</i>, the one denotes a swing component, the other one a marytts void/language component
 * 
 * 
 * @author Jonathan
 * 
 */
public class ComponentPanel extends JPanel {

	private JLabel componentNameLabel;
	private JButton installButton;
	private JLabel status;
	private JPanel descriptionPanel;

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
		this.setMinimumSize(componentPanelSize);

		setLayout(new GridBagLayout());
		setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

		// ### componentNameLabel ###
		this.componentNameLabel = new JLabel();
		this.componentNameLabel.setText(component.getName());
		GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2 = new java.awt.GridBagConstraints();
		gridBagConstraints_2.gridheight = 2;
		gridBagConstraints_2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_2.weightx = 1.0;
		gridBagConstraints_2.weighty = 0.5;
		gridBagConstraints_2.insets = new java.awt.Insets(0, 5, 0, 0);
		add(this.componentNameLabel, gridBagConstraints_2);

		// ### installButton ###
		this.installButton = new JButton();
		this.installButton.setText("INSTALL");
		this.installButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("To be implemented: Installation of component");
			}
		});
		GridBagConstraints gridBagConstraints_1 = new java.awt.GridBagConstraints();
		gridBagConstraints_1.gridx = 1;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.weighty = 0.2;
		add(this.installButton, gridBagConstraints_1);

		// ### availStateLabel ###
		this.status = new JLabel();
		this.status.setFont(new Font("Lucida Grande", 0, 10));
		this.status.setText(component.getStatus().toString());
		GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.weighty = 1.0;
		// gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(1, 0, 0, 0);
		add(this.status, gridBagConstraints);

		// ### description ###
		// HERE
		this.descriptionPanel = createDescriptionPanel(component);

	}

	private JPanel createDescriptionPanel(Component component) {

		JPanel descriptionPanelToBe = new JPanel(new GridBagLayout());
		descriptionPanelToBe.setPreferredSize(new Dimension(200, 20));
		descriptionPanelToBe.setMaximumSize(new Dimension(500, 50));

		
		return descriptionPanelToBe;
	}
}