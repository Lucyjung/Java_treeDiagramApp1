/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelTestFTA.view;

/**
 *
 * @author pimthip
 */

import RelTestFTA.config.Configurations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AppForm extends JFrame {
	public JButton btnGoal;
	public JComboBox goalList;
	public JTextField txtFile;
	public JButton btnButton;
	public JTable table;

	public AppForm() {

		// Create Form Frame
		super(Configurations.HEADER_APP);
		setSize(Configurations.APP_WIDTH, Configurations.APP_HEIGHT);
		setLocation(Configurations.APP_LOCATION_X, Configurations.APP_LOCATION_Y);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(Configurations.IMAGE_ICON)));

		getContentPane().setBackground(Color.white);

		txtFile = new JTextField("");
		txtFile.setBounds(20, 50, 490, 20);
		txtFile.setEditable(false);
		getContentPane().add(txtFile);
                
		// Label
		final JLabel lbltxt = new JLabel("Please select success goal...");
		lbltxt.setBounds(20, 89, 200, 14);
		getContentPane().add(lbltxt);


		goalList = new JComboBox();
		//goalList.setSelectedIndex(1);
		goalList.setBounds(205, 85, 305, 23);

		getContentPane().add(goalList);

		btnGoal = new JButton("Choose Goal");

		btnGoal.setBounds(520, 85, 110, 23);
		btnGoal.setEnabled(false);
		getContentPane().add(btnGoal);

		// Create Button Open JFileChooser
		btnButton = new JButton("Choose File");
		btnButton.setBounds(520, 50, 110, 23);

		getContentPane().add(btnButton);

		initTable();
		getContentPane().add(table);

		JScrollPane scroll = new JScrollPane(table);
		scroll.setBounds(20, 125, 610, 80);
		getContentPane().add(scroll);

	}

	public JButton getBtnGoal() {
		return btnGoal;
	}

	public void setBtnGoal(JButton btnGoal) {
		this.btnGoal = btnGoal;
	}

	public JComboBox getGoalList() {
		return goalList;
	}

	public void setGoalList(JComboBox goalList) {
		this.goalList = goalList;
	}

	public JButton getBtnButton() {
		return btnButton;
	}

	public void setBtnButton(JButton btnButton) {
		this.btnButton = btnButton;
	}

	public JTextField getTxtFile() {
		return txtFile;
	}

	public void setTxtFile(JTextField txtFile) {
		this.txtFile = txtFile;
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}
	public void initTable(){
		DefaultTableModel dm = new DefaultTableModel();
		dm.setDataVector(new Object[][] { { Configurations.CCTM_DISPLAY_TEXT, "", ""},
						{ Configurations.STD_DISPLAY_TEXT, "", ""},
						{ Configurations.FTD_DISPLAY_TEXT, "" , "" } },
				new Object[] { Configurations.DIAGRAM_COLUMN_NAME, Configurations.TESTCASE_COLUMN_NAME, Configurations.BUTTON_COLUMN_NAME });

		table = new JTable(dm);
		table.getColumn(Configurations.DIAGRAM_COLUMN_NAME).setPreferredWidth(Configurations.CCTM_COLUMN_PREFERRED_WIDTH);

	}
	public void reInitTable(){
		table.setValueAt("", 0, Configurations.BUTTON_COLUMN);
		table.setValueAt("", 1, Configurations.BUTTON_COLUMN);
		table.setValueAt("", 2, Configurations.BUTTON_COLUMN);

		table.setValueAt("", 0, Configurations.TESTCASE_COLUMN);
		table.setValueAt("", 1, Configurations.TESTCASE_COLUMN);
		table.setValueAt("", 2, Configurations.TESTCASE_COLUMN);

		table.getColumn(Configurations.BUTTON_COLUMN_NAME).setCellRenderer(null);
		table.getColumn(Configurations.BUTTON_COLUMN_NAME).setCellEditor(null);


	}
}

