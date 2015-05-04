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
package osmcd.gui.catalog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;

public class CatalogNew implements ActionListener
{
	@Override
	public void actionPerformed(ActionEvent event)
	{
		MainFrame mainFrame = MainFrame.getMainGUI();
		JPanel panel = new JPanel();
		BorderLayout layout = new BorderLayout();
		layout.setVgap(4);
		panel.setLayout(layout);

		// JPanel formatPanel = new JPanel(new BorderLayout());
		// formatPanel.add(new JLabel(OSMCDStrs.RStr("Bundle.PleaseSelect")), BorderLayout.NORTH);
		// JList bundleFormatList = new JList(BundleOutputFormat.getFormatsAsVector());
		// bundleFormatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// JScrollPane scroller = new JScrollPane(bundleFormatList);
		// scroller.setPreferredSize(new Dimension(140, 200));
		// formatPanel.add(scroller, BorderLayout.CENTER);

		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.add(new JLabel(OSMCDStrs.RStr("Catalog.NewName")), BorderLayout.NORTH);
		JTextField catalogName = new JTextField(OSMCDStrs.RStr("Catalog.DefaultName"));
		namePanel.add(catalogName, BorderLayout.SOUTH);

		panel.add(namePanel, BorderLayout.NORTH);
		// panel.add(formatPanel, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(300, 300));
		// BundleOutputFormat currentAOF = null;
		// try
		// {
		// currentAOF = mg.getBundle().getOutputFormat();
		// }
		// catch (Exception e)
		// {
		// }
		// if (currentAOF != null)
		// bundleFormatList.setSelectedValue(currentAOF, true);
		// else
		// bundleFormatList.setSelectedIndex(1);
		int result = JOptionPane.showConfirmDialog(mainFrame, panel, OSMCDStrs.RStr("Catalog.NewTitle"), JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION)
		{
			// BundleOutputFormat format = (BundleOutputFormat) bundleFormatList.getSelectedValue();
			// mainFrame.jCatalogTree.newCatalog(catalogName.getText());
			mainFrame.newCatalog(catalogName.getText());
			// mainFrame.catalogNameTextField.setText(catalogName.getText());
			// mg.getParametersPanel().catalogFormatChanged(format);
		}
		else
			;
	}
}
