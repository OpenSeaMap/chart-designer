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
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import osmb.program.catalog.Catalog;
import osmb.program.catalog.IfCatalogProfile;
import osmb.utilities.GBC;
import osmb.utilities.OSMBUtilities;
import osmcd.OSMCDStrs;
import osmcd.gui.components.JCollapsiblePanel;

/**
 * UI-Panel containing a list of all catalogs found in the catalogs directory as specified in settings.xml.
 * It also contains a comboBox, where the user can select one of the existing catalogs or enter a new name to be used with the current catalog.
 * A catalog can be deleted from here, the current catalog can be saved or a stored catalog can be loaded.
 * 
 * @author humbach
 *
 */
public class JCatalogsPanel extends JCollapsiblePanel
{
	// class/static data
	private static final long serialVersionUID = 1L;
	protected static Logger log = Logger.getLogger(JCatalogsPanel.class);

	// instance data
	private JCatalogsComboBox mCatalogsCombo;
	private JButton reloadButton;
	private JButton deleteButton;
	private JButton loadButton;
	private JButton saveAsButton;

	public JCatalogsPanel(JCatalogTree catalogTree) {
		super(OSMCDStrs.RStr("Catalog.Title"), new GridBagLayout());

		if (catalogTree == null)
			throw new NullPointerException();

		// catalogs combo box
		mCatalogsCombo = new JCatalogsComboBox();
		mCatalogsCombo.setToolTipText(OSMCDStrs.RStr("Catalog.ListTips"));
		mCatalogsCombo.addActionListener(new CatalogListListener());

		// delete catalog button
		deleteButton = new JButton(OSMCDStrs.RStr("Catalog.Delete"));
		deleteButton.setToolTipText(OSMCDStrs.RStr("Catalog.DeleteTips"));
		deleteButton.addActionListener(new DeleteCatalogListener());

		// save catalog as button
		saveAsButton = new JButton(OSMCDStrs.RStr("Catalog.Save"));
		saveAsButton.setToolTipText(OSMCDStrs.RStr("Catalog.SaveTips"));
		saveAsButton.addActionListener(new SaveAsCatalogListener(catalogTree));

		loadButton = new JButton(OSMCDStrs.RStr("Catalog.Load"));
		loadButton.setToolTipText(OSMCDStrs.RStr("Catalog.LoadTips"));

		GBC gbc = GBC.eol().fill().insets(5, 5, 5, 5);
		reloadButton = new JButton(OSMBUtilities.loadResourceImageIcon("refresh.png"));
		reloadButton.setToolTipText(OSMCDStrs.RStr("Catalog.RefreshTips"));
		reloadButton.addActionListener(new ReloadListener());
		reloadButton.setPreferredSize(new Dimension(24, 0));

		// compose the complete panel
		JPanel p = new JPanel(new BorderLayout());
		p.add(mCatalogsCombo, BorderLayout.CENTER);
		p.add(reloadButton, BorderLayout.EAST);

		contentContainer.add(p, gbc);
		contentContainer.add(deleteButton, gbc.toggleEol());
		contentContainer.add(saveAsButton, gbc);
		contentContainer.add(loadButton, gbc.toggleEol());

		// disable the newly created buttons
		saveAsButton.setEnabled(false);
		deleteButton.setEnabled(false);
		loadButton.setEnabled(false);
	}

	public void initialize()
	{
		// Load all catalogs from the catalogs file from disk
		mCatalogsCombo.loadCatalogsList();
		deleteButton.setEnabled(false);
		loadButton.setEnabled(false);
	}

	public void reloadCatalogList()
	{
		initialize();
	}

	public JCatalogsComboBox getCatalogsCombo()
	{
		return mCatalogsCombo;
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

	public IfCatalogProfile getSelectedCatalog()
	{
		return mCatalogsCombo.getSelectedCatalog();
	}

	private class SaveAsCatalogListener implements ActionListener
	{
		JCatalogTree mCatalogTree;

		public SaveAsCatalogListener(JCatalogTree thisCatalogTree) {
			super();
			mCatalogTree = thisCatalogTree;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!mCatalogTree.testCatalogContentValid())
				return;
			Object selObject = mCatalogsCombo.getEditor().getItem();
			String catalogName = null;
			IfCatalogProfile catalog = null;
			if (selObject instanceof IfCatalogProfile)
			{
				catalog = (IfCatalogProfile) selObject;
				catalogName = catalog.getName();
			}
			else
			{
				log.info("not a catalog selected");
				catalogName = (String) selObject;
				Catalog newCatalog = new Catalog(catalogName);
				newCatalog.deepClone();
			}

			if (catalogName.length() == 0)
			{
				log.info("not a catalog name given");
				JOptionPane.showMessageDialog(null, OSMCDStrs.RStr("Catalog.EnterName"), OSMCDStrs.RStr("Error"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			else
			{
				// catalog = new Catalog(catalogName);

				if (catalog.exists())
				{
					int response = JOptionPane.showConfirmDialog(null, String.format(OSMCDStrs.RStr("Catalog.ConfirmOverwrite"), catalogName),
							OSMCDStrs.RStr("Catalog.Confirm"), JOptionPane.YES_NO_OPTION);
					if (response != JOptionPane.YES_OPTION)
						return;
				}

				if (mCatalogTree.save())
				{
					reloadCatalogList();
					mCatalogsCombo.setSelectedItem(catalog);
				}
			}
		}
	}

	private class DeleteCatalogListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			mCatalogsCombo.deleteSelectedCatalog();
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

	/**
	 * listens on changing the selected catalog by selecting another catalog from the list or entering a new name
	 * 
	 * @author humbach
	 *
	 */
	private class CatalogListListener implements ActionListener
	{
		/**
		 * Another catalog has been selected or the name has been modified
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			boolean existingCatalogSelected = (mCatalogsCombo.getSelectedCatalog() != null);
			loadButton.setEnabled(existingCatalogSelected);
			deleteButton.setEnabled(existingCatalogSelected);
		}
	}

	/**
	 * a new name is set in the combobox. It may be the name of an already existing catalog or it may be a new name
	 * should trigger the ActionListener.actionPerformed()
	 * 
	 * @param catalogName
	 */
	public void setCatalogName(String catalogName)
	{
		// mCatalogsCombo.setText(catalogName);
		mCatalogsCombo.setSelectedItem(catalogName);
	}

	String getSelectedCatalogName()
	{
		return mCatalogsCombo.getSelectedCatalog().getName();
	}
}
