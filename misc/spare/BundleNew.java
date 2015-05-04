/*******************************************************************************
 * Copyright (c) OSMCB developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package osmcd.gui.actions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import osmcb.program.bundle.BundleOutputFormat;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;

public class BundleNew implements ActionListener
{
	@Override
	public void actionPerformed(ActionEvent event)
	{
		MainGUI mg = MainGUI.getMainGUI();
		JPanel panel = new JPanel();
		BorderLayout layout = new BorderLayout();
		layout.setVgap(4);
		panel.setLayout(layout);

		JPanel formatPanel = new JPanel(new BorderLayout());

		formatPanel.add(new JLabel(OSMCDStrs.RStr("Bundle.PleaseSelect")), BorderLayout.NORTH);
		JList bundleFormatList = new JList(BundleOutputFormat.getFormatsAsVector());
		bundleFormatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scroller = new JScrollPane(bundleFormatList);
		scroller.setPreferredSize(new Dimension(140, 200));
		formatPanel.add(scroller, BorderLayout.CENTER);

		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.add(new JLabel(OSMCDStrs.RStr("Bundle.NewName")), BorderLayout.NORTH);
		JTextField bundleName = new JTextField(OSMCDStrs.RStr("Bundle.DefaultName"));
		namePanel.add(bundleName, BorderLayout.SOUTH);

		panel.add(namePanel, BorderLayout.NORTH);
		panel.add(formatPanel, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(300, 300));
		BundleOutputFormat currentAOF = null;
		try
		{
			currentAOF = mg.getCatalog().getOutputFormat();
		}
		catch (Exception e)
		{
		}
		if (currentAOF != null)
			bundleFormatList.setSelectedValue(currentAOF, true);
		else
			bundleFormatList.setSelectedIndex(1);
		int result = JOptionPane.showConfirmDialog(MainGUI.getMainGUI(), panel, OSMCDStrs.RStr("Bundle.NewTitle"), JOptionPane.OK_CANCEL_OPTION);
		if (result != JOptionPane.OK_OPTION)
			return;

		// BundleOutputFormat format = (BundleOutputFormat) bundleFormatList.getSelectedValue();
		// mg.jBundleTree.newCatalog(bundleName.getText(), format);
		// mg.getParametersPanel().catalogFormatChanged(format);
	}
}
