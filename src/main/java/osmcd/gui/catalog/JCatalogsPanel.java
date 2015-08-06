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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import osmb.program.catalog.Catalog;
import osmb.program.catalog.IfCatalog;
import osmb.program.catalog.IfCatalogProfile;
import osmb.utilities.GBC;
import osmb.utilities.OSMBUtilities;
import osmcd.OSMCDSettings;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;
import osmcd.gui.actions.AddRectangleMapAutocut;
import osmcd.gui.components.JCollapsiblePanel;
import osmcd.gui.mapview.PreviewMap;

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
//	private JButton loadButton;
	private JButton saveButton;
	private JButton discardChangesButton;
//	private JButton renameButton;
	private JButton addToLayersButton;
	private boolean bIsContentChanged;
	private boolean bExistingLoaded;
	private boolean bNewNamedLoaded;

	private JCatalogTree jCatalogTree = null;
	
//	public void addListener() // /W #combi wer ruft den auf #??? nur der Konstruktor?
//	{
//		jCatalogTree.getTreeModel().addTreeModelListener(new CatalogModelListener(jCatalogTree, this));
//	}
	
	public JCatalogsPanel(PreviewMap previewMap)
	{
		super(OSMCDStrs.RStr("Catalog.Title"), new GridBagLayout());

		jCatalogTree = new JCatalogTree(previewMap);
		if (jCatalogTree == null)
			throw new NullPointerException();
		
		jCatalogTree.getTreeModel().addTreeModelListener(new CatalogModelListener(jCatalogTree, this)); // /W #??? == addListener();		
		
		// catalogs combo box
		mCatalogsCombo = new JCatalogsComboBox();
		mCatalogsCombo.setToolTipText(OSMCDStrs.RStr("Catalog.ListTips"));
		mCatalogsCombo.addItemListener(new CatalogListListener()); // mCatalogsCombo.addActionListener(new CatalogListListener());

		// delete catalog button
		deleteButton = new JButton(OSMCDStrs.RStr("Catalog.Delete"));
		deleteButton.setToolTipText(OSMCDStrs.RStr("Catalog.DeleteTips"));
		deleteButton.addActionListener(new DeleteCatalogListener());

		// save catalog as button
		saveButton = new JButton(OSMCDStrs.RStr("Catalog.Save"));
		saveButton.setToolTipText(OSMCDStrs.RStr("Catalog.SaveTips"));
		saveButton.addActionListener(new SaveCatalogListener());

//		loadButton = new JButton(OSMCDStrs.RStr("Catalog.Load"));
//		loadButton.setToolTipText(OSMCDStrs.RStr("Catalog.LoadTips"));
//		loadButton.addActionListener(new LoadCatalogListener()); // /W (previewMap)
		
		discardChangesButton = new JButton(OSMCDStrs.RStr("Catalog.DiscardChanges"));
		discardChangesButton.setToolTipText(OSMCDStrs.RStr("Catalog.DiscardChangesTips")); // /W #RStr
		discardChangesButton.addActionListener(new DiscardChangesListener());
		
		GBC gbc = GBC.eol().fill().insets(5, 5, 5, 5);
		reloadButton = new JButton(OSMBUtilities.loadResourceImageIcon("refresh.png"));
		reloadButton.setToolTipText(OSMCDStrs.RStr("Catalog.RefreshTips"));
		reloadButton.addActionListener(new ReloadListener());
		reloadButton.setPreferredSize(new Dimension(24, 0));

//		renameButton = new JButton(OSMCDStrs.RStr("Catalog.Rename"));
//		renameButton.setToolTipText(OSMCDStrs.RStr("Catalog.RenameTips"));
//		renameButton.addActionListener(new RenameCatalogListener());	
		
		addToLayersButton = new JButton(OSMCDStrs.RStr("CatalogTree.AddSelected"));
		addToLayersButton.setToolTipText(OSMCDStrs.RStr("CatalogTree.AddSelectedTips")); // /W #RStr
		//addToLayersButton.addActionListener(AddMapLayer.INSTANCE);// -> NullpointerException
		addToLayersButton.addActionListener(new AddMapLayerListener());//.INSTANCE);
		
		GBC gbc_eol = GBC.eol().insets(2, 1, 2, 1);
		GBC gbc_std = GBC.std().insets(2, 1, 2, 1);
				
		JScrollPane treeScrollPane = new JScrollPane(jCatalogTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScrollPane.setMinimumSize(new Dimension(100, 200));
		treeScrollPane.setPreferredSize(new Dimension(100, 400));
		treeScrollPane.setAutoscrolls(true);
		
		// compose the complete panel
		JPanel p = new JPanel(new BorderLayout());
		p.add(mCatalogsCombo, BorderLayout.CENTER);
		p.add(reloadButton, BorderLayout.EAST);

		contentContainer.add(p, gbc);
		contentContainer.add(deleteButton, gbc_std);
		contentContainer.add(saveButton, gbc_std);
		contentContainer.add(discardChangesButton, gbc_eol);
		
//		contentContainer.add(addToLayersButton, gbc_std);
//		contentContainer.add(renameButton, gbc_eol);
		contentContainer.add(addToLayersButton, gbc_eol);
		
		// this.addContent(treeScrollPane, GBC.eol().fill().insets(0, 1, 0, 0));
		contentContainer.add(treeScrollPane, gbc_eol.fillH());

		// /W #buttonManagement
		bIsContentChanged = false; // setIsContentChanged(false);
		bExistingLoaded = false;
		bNewNamedLoaded = false;
		enableButtons();
	}

	public void initialize()
	{
		// Load all catalogs from the catalogs file from disk
		mCatalogsCombo.loadCatalogsList();
		
		OSMCDSettings settings = OSMCDSettings.getInstance();
		String settingsCatalogName = settings.getCatalogName();
		if (settings.getCatalogNameMakeNew() || (!Catalog.isCatalogsFileNamePart(settingsCatalogName)))
		{
			String newName = Catalog.makeNewCatalogsName();
			setCatalogName(newName);
			getCatalogTree().newCatalog(newName);
		}
		else
		{
			String cName = settingsCatalogName;
			setCatalogName(cName);
			if (Catalog.isCatalogsFileNamePart(cName))
			{
				if (mCatalogsCombo.setSelectedCatalogByName(cName))
					;// /W #??? true
			}
		}
	}

	public void reloadCatalogList()
	{
		// Load all catalogs from the catalogs file from disk
		mCatalogsCombo.loadCatalogsList();
	}

	public IfCatalog getCatalog()
	{
		return jCatalogTree.getCatalog();
	}
	
	public JCatalogTree getCatalogTree() // /W #combi
	{
		return jCatalogTree;
	}

//	public JCatalogsComboBox getCatalogsCombo()
//	{
//		return mCatalogsCombo;
//	}
//
//	public JButton getLoadButton()
//	{
//		return loadButton;
//	}
//	
//	public JButton getDiscardChangesButton()
//	{
//		return discardChangesButton;
//		
//	}
//
//	public JButton getDeleteButton()
//	{
//		return deleteButton;
//	}
//
//	public JButton getSaveButton()
//	{
//		return saveButton;
//	}
//	
//	public JButton getRenameButton()
//	{
//		return renameButton;
//	}
//	
//	public JButton getAddToLayersButton()
//	{
//		return addToLayersButton;
//	}

	public IfCatalogProfile getSelectedCatalog()
	{
		return mCatalogsCombo.getSelectedCatalog();
	}
	
//	public boolean getIsContentChanged()
//	{
//		return bIsContentChanged;
//	}
//	
	// /W -> CatalogModelListener!
	public void setIsContentChanged(boolean bIsChanged)
	{
		bIsContentChanged = bIsChanged;
	}

	// /W
	public void enableButtons()
	{
		// delete
		IfCatalogProfile profile = getSelectedCatalog();
		deleteButton.setEnabled(profile != null && !bIsContentChanged);
		saveButton.setEnabled(jCatalogTree.getCatalog().getLayerCount() > 0 && bIsContentChanged);
		
		if (bExistingLoaded || bNewNamedLoaded)
		{
			addToLayersButton.setEnabled(true);
			mCatalogsCombo.setEnabled(bIsContentChanged ? false : true);
			discardChangesButton.setEnabled(true);
		}
		else
		{
			addToLayersButton.setEnabled(false);
			mCatalogsCombo.setEnabled(true);
			discardChangesButton.setEnabled(false);
		}
		
		jCatalogTree.getTreeModel().notifyStructureChanged(); // repaint content
	}


	private class AddMapLayerListener implements ActionListener // /W #???
	{
		// /W public static final AddMapLayer INSTANCE = new AddMapLayer();
		//public static final AddMapLayerListener INSTANCE = new AddMapLayerListener();
		
//		JCatalogTree mCatalogTree;
//
//		public AddMapLayerListener(JCatalogTree thisCatalogTree)
//		{
//			super();
//			mCatalogTree = thisCatalogTree; // /W #???
//		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			new AddRectangleMapAutocut().actionPerformed(e);
			
			int nBreack = 0;
			bIsContentChanged = true;
			enableButtons();
			
			jCatalogTree.getTreeModel().notifyStructureChanged();
		}
	}

	private class SaveCatalogListener implements ActionListener
	{
//		JCatalogTree mCatalogTree;
//
//		public SaveCatalogListener(JCatalogTree thisCatalogTree)
//		{
//			super();
//			mCatalogTree = thisCatalogTree;
//		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!jCatalogTree.testCatalogContentValid())
				return;
			Object selObject = mCatalogsCombo.getEditor().getItem();
			String catalogName = null;
			IfCatalogProfile catalog = null;
			
			if (selObject instanceof IfCatalogProfile) // /testen !!!
			{
				catalog = (IfCatalogProfile) selObject;
				catalogName = catalog.getName();
			}
			else // /W mCatalogTree == valid, kein IfCatalogProfile
			{
				catalogName = (String) selObject; // /W new catalog
				
// /W #??? was ist das?				
//				log.info("not a catalog selected");
//				catalogName = (String) selObject;
//				Catalog newCatalog = new Catalog(catalogName); // /W => catalog.load() => JAXBException bei neuem catalog 
//				newCatalog.deepClone(); // /W +++ *****************************deepClone
			}
			
			if (catalogName.length() == 0)
			{
				log.info("not a catalog name given");
				JOptionPane.showMessageDialog(null, OSMCDStrs.RStr("Catalog.EnterName"), OSMCDStrs.RStr("Error"), JOptionPane.ERROR_MESSAGE);
				
				// /W #???
//				mCatalogsCombo.setEnabled(true);
				
				return;
				
			}
			else
			{
				// catalog = new Catalog(catalogName);
// /W ohne Abfrage überschreiben <--> ergänzen #???
//				if (catalog.exists())
//				{
//					int response = JOptionPane.showConfirmDialog(null, String.format(OSMCDStrs.RStr("Catalog.ConfirmOverwrite"), catalogName),
//							OSMCDStrs.RStr("Catalog.Confirm"), JOptionPane.YES_NO_OPTION);
//					if (response != JOptionPane.YES_OPTION)
//						return;
//				}

				if (jCatalogTree.save())
				{
					// /W #??? -> discard?#???
					reloadCatalogList();
					// /W load name of saved catalog from settings
					OSMCDSettings settings = OSMCDSettings.getInstance();
					String settingsCatalogName = settings.getCatalogName();
					mCatalogsCombo.setSelectedCatalogByName(settingsCatalogName);
					bIsContentChanged = false; // setIsContentChanged(false);
					enableButtons();
				}
				// else ???
			}
//			mCatalogsCombo.setEnabled(true); // #??
		}
	}

	private class DeleteCatalogListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			mCatalogsCombo.deleteSelectedCatalog();
			bIsContentChanged = false;
			
			String newName = Catalog.makeNewCatalogsName();
			setCatalogName(newName);
			
			getCatalogTree().newCatalog("new ..."); // clear content settings #???
			bNewNamedLoaded = false;
			bExistingLoaded = false;
			enableButtons();
		}
	}
	
	private class DiscardChangesListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// reload catalog
			Object selObject = mCatalogsCombo.getEditor().getItem();
			mCatalogsCombo.setSelectedIndex(-1); // /W #??? Hilfskonstruktion? -> geht so zum ListListener
			mCatalogsCombo.setSelectedItem(selObject);//.getEditor().setItem(selObject);
			
			bIsContentChanged = false; // setIsContentChanged(false);
			enableButtons();
		}
	}
	
	protected void loadCatalog()
	{
		IfCatalogProfile profile = getSelectedCatalog();
		
//		deleteButton.setEnabled(profile != null);// #???
		if (profile == null)
			return;
		if (profile instanceof Catalog)
		{
			log.info("loadCatalog(): name1 = " + profile.getName());
			log.info("loadCatalog(): version1 = " + ((Catalog) profile).getVersion());
			log.info("loadCatalog(): LayerCount1 = " + ((Catalog) profile).getSize());
		}
		
		jCatalogTree.load(profile); // Zeichnet Tree
		MainFrame.getMainGUI().previewMap.repaint(); // #???
		
		log.info("loadCatalog(): name2 = " + getCatalogTree().getTreeModel().getCatalog().getName());
		// /W name1 != name2 Warndialog aufrufen???
		
		bExistingLoaded = true;
		enableButtons();
		
		// mTileImageParametersPanel.bundleFormatChanged(jBundleTree.getBundle().getOutputFormat());
	}
	
	private class LoadCatalogListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			int breakpoint = 0;
			IfCatalogProfile profile = getSelectedCatalog();
			deleteButton.setEnabled(profile != null);
			if (profile == null)
				return;

			jCatalogTree.load(profile); // Zeichnet Tree
			MainFrame.getMainGUI().previewMap.repaint(); // #???
			
			// mTileImageParametersPanel.bundleFormatChanged(jBundleTree.getBundle().getOutputFormat());
		}
	}

	private class ReloadListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object selObject = mCatalogsCombo.getEditor().getItem();
			reloadCatalogList();
			// falls selObject weg -> #???
			mCatalogsCombo.getEditor().setItem(selObject);
		}
	}
	
//	private class RenameCatalogListener implements ActionListener
//	{
//		@Override
//		public void actionPerformed(ActionEvent e)
//		{
//
//		}
//	}

	/**
	 * listens on changing the selected catalog by selecting another catalog from the list or entering a new name
	 * 
	 * @author humbach
	 *
	 */
	private class CatalogListListener implements ItemListener
	{
		/**
		 * Another catalog has been selected or the name has been modified
		 */
		@Override
		public void itemStateChanged(ItemEvent iE)
		{
			if (iE.getStateChange() == ItemEvent.SELECTED)
			{
				boolean existingCatalogSelected = (mCatalogsCombo.getSelectedCatalog() != null);
				if (existingCatalogSelected == false) // String in ComboBoxEditor
				{
					String newName = (String)mCatalogsCombo.getEditor().getItem();
					if (newName.length() < 1)
					{
						log.info("not a catalog name given");
						JOptionPane.showMessageDialog(null, OSMCDStrs.RStr("Catalog.EnterName"), OSMCDStrs.RStr("Error"), JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (Catalog.isCatalogsFileNamePart(newName)) // existing name
					{
						int response = JOptionPane.showConfirmDialog(null, String.format(OSMCDStrs.RStr("Catalog.ConfirmOverwrite"), newName),
								OSMCDStrs.RStr("Catalog.Confirm"), JOptionPane.YES_NO_OPTION);
						if (response == JOptionPane.YES_OPTION) // overwrite
						{
							getCatalogTree().newCatalog(newName); // /W #??? Tastuturbedienung geht schief!
							bNewNamedLoaded = true;
						}
						else // load existing catalog 
						{
							mCatalogsCombo.setSelectedCatalogByName(newName);
							loadCatalog();
							// /W statt
								// LoadCatalogListener loadListener = new LoadCatalogListener();
								// loadListener.actionPerformed(null);
						}
					}
					else // new name
					{
						getCatalogTree().newCatalog(newName);
						bNewNamedLoaded = true;
					}
				}
				else // existing catalog selected
				{
					loadCatalog();
					// /W statt
						// LoadCatalogListener loadListener = new LoadCatalogListener();
						// loadListener.actionPerformed(null);
				}
				
				enableButtons();
				
				
			}
			if (iE.getStateChange() == ItemEvent.DESELECTED)
			{
				int nBreakpoint = 1;
			}
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
		mCatalogsCombo.getEditor().setItem(catalogName);
		// mCatalogsCombo.setSelectedItem(catalogName);
	}

	String getSelectedCatalogName()
	{
		return mCatalogsCombo.getSelectedCatalog().getName();
	}
}
