package io.github.hexafraction.balance_checker;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.intellij.uiDesigner.core.*;

public class AddressInput extends JDialog {
    public AddressInput() {
        initComponents();
		setContentPane(contentPane);
        setModal(true);
        setTitle("Address input");
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        AddressInput dialog = new AddressInput();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - unknown
		contentPane = new JPanel();
		JPanel panel1 = new JPanel();
		Spacer hSpacer1 = new Spacer();
		JPanel panel2 = new JPanel();
		buttonOK = new JButton();
		buttonCancel = new JButton();
		JPanel panel3 = new JPanel();
		JScrollPane scrollPane1 = new JScrollPane();
		area = new JTextArea();

		//======== contentPane ========
		{

			// JFormDesigner evaluation mark
			contentPane.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
					"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
					java.awt.Color.red), contentPane.getBorder())); contentPane.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

			contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));

			//======== panel1 ========
			{
				panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
				panel1.add(hSpacer1, new GridConstraints(0, 0, 1, 1,
					GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
					GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
					GridConstraints.SIZEPOLICY_CAN_SHRINK,
					null, null, null));

				//======== panel2 ========
				{
					panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));

					//---- buttonOK ----
					buttonOK.setText("OK");
					panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1,
						GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
						GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
						GridConstraints.SIZEPOLICY_FIXED,
						null, null, null));

					//---- buttonCancel ----
					buttonCancel.setText("Cancel");
					panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1,
						GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
						GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
						GridConstraints.SIZEPOLICY_FIXED,
						null, null, null));
				}
				panel1.add(panel2, new GridConstraints(0, 1, 1, 1,
					GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
					GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
					GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
					null, null, null));
			}
			contentPane.add(panel1, new GridConstraints(1, 0, 1, 1,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK,
				null, null, null));

			//======== panel3 ========
			{
				panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));

				//======== scrollPane1 ========
				{

					//---- area ----
					area.setLineWrap(true);
					scrollPane1.setViewportView(area);
				}
				panel3.add(scrollPane1, new GridConstraints(0, 0, 1, 1,
					GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
					GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
					GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
					new Dimension(512, 240), null, null));
			}
			contentPane.add(panel3, new GridConstraints(0, 0, 1, 1,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null, null, null));
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - unknown
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	JTextArea area;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
