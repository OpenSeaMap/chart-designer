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
package osmcd.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import osmcb.program.model.Catalog;
import osmcb.utilities.GBC;
import osmcb.utilities.OSMCBUtilities;
import osmcd.OSMCDStrs;
import osmcd.gui.bundletree.JBundleTree;
import osmcd.gui.components.JCatalogsComboBox;
import osmcd.gui.components.JCollapsiblePanel;

public class JCatalogsPanel extends JCollapsiblePanel
{
	private static final long serialVersionUID = 1L;
	private JCatalogsComboBox catalogssCombo;
	private JButton reloadButton;
	private JButton deleteButton;
	private JButton loadButton;
	private JButton saveAsButton;

	public JCatalogsPanel(JBundleTree bundleTree)
	{
		super(OSMCDStrs.RStr("Catalog.Title"), new GridBagLayout());

		if (bundleTree == null)
			throw new NullPointerException();

		// catalogs combo box
		catalogssCombo = new JCatalogsComboBox();
		catalogssCombo.setToolTipText(OSMCDStrs.RStr("Catalog.ListTips"));
		catalogssCombo.addActionListener(new ProfileListListener());

		// delete profile button
		deleteButton = new JButton(OSMCDStrs.RStr("Catalog.Delete"));
		deleteButton.addActionListener(new DeleteProfileListener());
		deleteButton.setToolTipText(OSMCDStrs.RStr("Catalog.DeleteTips"));

		// save as profile button
		saveAsButton = new JButton(OSMCDStrs.RStr("Catalog.Save"));
		saveAsButton.setToolTipText(OSMCDStrs.RStr("Catalog.SaveTips"));
		saveAsButton.addActionListener(new SaveAsCatalogListener(bundleTree));

		loadButton = new JButton(OSMCDStrs.RStr("Catalog.Load"));
		loadButton.setToolTipText(OSMCDStrs.RStr("Catalog.LoadTips"));

		GBC gbc = GBC.eol().fill().insets(5, 5, 5, 5);
		reloadButton = new JButton(OSMCBUtilities.loadResourceImageIcon("refresh.png"));
		reloadButton.setToolTipText(OSMCDStrs.RStr("Catalog.RefreshTips"));
		reloadButton.addActionListener(new ReloadListener());
		reloadButton.setPreferredSize(new Dimension(24, 0));

		JPanel p = new JPanel(new BorderLayout());
		p.add(catalogssCombo, BorderLayout.CENTER);
		p.add(reloadButton, BorderLayout.EAST);

		contentContainer.add(p, gbc);
		contentContainer.add(deleteButton, gbc.toggleEol());
		contentContainer.add(saveAsButton, gbc);
		contentContainer.add(loadButton, gbc.toggleEol());

		saveAsButton.setEnabled(false);
		deleteButton.setEnabled(false);
		loadButton.setEnabled(false);
	}

	public void initialize()
	{
		// Load all catalogs from the catalogs file from disk
		catalogssCombo.loadCatalogsList();
		deleteButton.setEnabled(false);
		loadButton.setEnabled(false);
	}

	public void reloadCatalogList()
	{
		initialize();
	}

	public JCatalogsComboBox getCatalogsCombo()
	{
		return catalogssCombo;
	}

	public JButton getLoadButton()
	{
		return loadButton;
	}

	public JButton getDeleteButton()
	{
		return deleteButton;
	}

	public JButton getSaveAsButton()
	{
		return saveAsButton;
	}

	public Catalog getSelectedCatalog()
	{
		return catalogssCombo.getSelectedCatalog();
	}

	private class SaveAsCatalogListener implements ActionListener
	{

		JBundleTree jBundleTree;

		public SaveAsCatalogListener(JBundleTree atlasTree)
		{
			super();
			jBundleTree = atlasTree;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!jBundleTree.testBundleContentValid())
				return;
			Object selObject = catalogssCombo.getEditor().getItem();
			String catalogName = null;
			Catalog catalog = null;
			if (selObject instanceof Catalog)
			{
				catalog = (Catalog) selObject;
				catalogName = catalog.getName();
			}
			else
				catalogName = (String) selObject;

			if (catalogName.length() == 0)
			{
				JOptionPane.showMessageDialog(null, OSMCDStrs.RStr("Catalog.EnterName"), OSMCDStrs.RStr("Error"), JOptionPane.ERROR_MESSAGE);
				return;
			}

			catalog = new Catalog(catalogName);

			if (catalog.exists())
			{
				int response = JOptionPane.showConfirmDialog(null, String.format(OSMCDStrs.RStr("Catalog.ConfirmOverwrite"), catalogName),
						OSMCDStrs.RStr("Catalog.Confirm"), JOptionPane.YES_NO_OPTION);
				if (response != JOptionPane.YES_OPTION)
					return;
			}

			if (jBundleTree.save(catalog))
			{
				reloadCatalogList();
				catalogssCombo.setSelectedItem(catalog);
			}
		}
	}

	private class DeleteProfileListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			catalogssCombo.deleteSelectedProfile();
		}
	}

	private class ReloadListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			reloadCatalogList();
		}
	}

	private class ProfileListListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			boolean existingProfileSelected = catalogssCombo.getSelectedCatalog() != null;
			loadButton.setEnabled(existingProfileSelected);
			deleteButton.setEnabled(existingProfileSelected);
		}
	}
}
