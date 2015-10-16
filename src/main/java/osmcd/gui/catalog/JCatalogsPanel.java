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
import java.io.File;
import java.util.regex.Matcher;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import osmb.program.catalog.Catalog;
import osmb.utilities.GBC;
import osmb.utilities.GUIExceptionHandler;
import osmb.utilities.OSMBStrs;
import osmcd.OSMCDApp;
import osmcd.OSMCDSettings;
import osmcd.OSMCDStrs;
import osmcd.gui.actions.AddRectangleMapAutocut;
import osmcd.gui.components.JCatalogFileChooser;
import osmcd.gui.components.JCollapsiblePanel;
import osmcd.gui.mapview.PreviewMap;

/**
 * UI-Panel containing buttons to manage the currently loaded catalog. There is a view showing the content of the current catalog,
 * maps containing the selected area in the checked zoomlevels can be added.
 * The current catalog can be saved, changes can be discarded or a stored catalog can be loaded.
 */
public class JCatalogsPanel extends JCollapsiblePanel
{
	// class/static data
	private static final long serialVersionUID = 1L;
	protected static Logger log = Logger.getLogger(JCatalogsPanel.class);

	// instance data
	private JButton loadButton;
	private JButton saveButton;
	private JButton discardChangesButton;
	private JButton addToLayersButton;
	
	private JCatalogTree jCatalogTree = null;
	
	public JCatalogsPanel(PreviewMap previewMap)
	{
		super(OSMCDStrs.RStr("Catalog.Title"), new GridBagLayout());

		jCatalogTree = new JCatalogTree(previewMap);
		if (jCatalogTree == null)
			throw new NullPointerException();
		
		jCatalogTree.getTreeModel().addTreeModelListener(new CatalogModelListener(jCatalogTree, this));
		
		saveButton = new JButton(OSMCDStrs.RStr("Catalog.Save"));
		saveButton.setToolTipText(OSMCDStrs.RStr("Catalog.SaveTips"));
		saveButton.addActionListener(new SaveCatalogListener());

		loadButton = new JButton(OSMCDStrs.RStr("Catalog.Load"));
		loadButton.setToolTipText(OSMCDStrs.RStr("Catalog.LoadTips"));
		loadButton.addActionListener(new LoadCatalogListener(false));
		
		discardChangesButton = new JButton(OSMCDStrs.RStr("Catalog.DiscardChanges"));
		discardChangesButton.setToolTipText(OSMCDStrs.RStr("Catalog.DiscardChangesTips"));
		discardChangesButton.addActionListener(new LoadCatalogListener(true)); // (new DiscardChangesListener());
		
		addToLayersButton = new JButton(OSMCDStrs.RStr("CatalogTree.AddSelected"));
		addToLayersButton.setToolTipText(OSMCDStrs.RStr("CatalogTree.AddSelectedTips"));
		//addToLayersButton.addActionListener(AddMapLayer.INSTANCE);// -> NullpointerException
		addToLayersButton.addActionListener(new AddMapLayerListener());//.INSTANCE);
		
		GBC gbc = GBC.eol().fill().insets(5, 5, 5, 5);
		GBC gbc_eol = GBC.eol().insets(2, 1, 2, 1);
		GBC gbc_std = GBC.std().insets(2, 1, 2, 1);
		GBC gbc_farRight = GBC.std().insets(2, 1, 25, 1);
				
		JScrollPane treeScrollPane = new JScrollPane(jCatalogTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScrollPane.setMinimumSize(new Dimension(100, 200));
		treeScrollPane.setPreferredSize(new Dimension(100, 400));
		treeScrollPane.setAutoscrolls(true);
		
		// compose the complete panel
		JPanel p = new JPanel(new BorderLayout());
		contentContainer.add(p, gbc);
		contentContainer.add(loadButton, gbc_farRight);
		contentContainer.add(saveButton, gbc_std);
		contentContainer.add(discardChangesButton, gbc_eol);
		contentContainer.add(treeScrollPane, gbc_eol.fillH());
		contentContainer.add(addToLayersButton, gbc_eol);
	}

	public void initialize()
	{
		OSMCDSettings settings = OSMCDSettings.getInstance();
		String settingsCatalogName = settings.getCatalogName();
		if (settings.getCatalogNameMakeNew() || (!Catalog.isCatalogsFileNamePart(settingsCatalogName)))
		{
			String newName = Catalog.makeNewCatalogsName();
			getCatalogTree().newCatalog(newName);
		}
		else // !settings.getCatalogNameMakeNew() && Catalog.isCatalogsFileNamePart(settingsCatalogName)
		{
			getCatalogTree().newCatalog(settingsCatalogName);
		}
		new LoadCatalogListener(true).actionPerformed(null);
	}

	public Catalog getCatalog()
	{
		return OSMCDApp.getApp().getCatalog(); // #gCatalog
	}
	
	public JCatalogTree getCatalogTree()
	{
		return jCatalogTree;
	}


	// control button states
	public void setIsContentChanged(boolean bIsContentChanged)
	{
		saveButton.setEnabled(jCatalogTree.getCatalog().getLayerCount() > 0 && bIsContentChanged);
		loadButton.setEnabled(!bIsContentChanged);
		discardChangesButton.setEnabled(bIsContentChanged);
	}

	private class AddMapLayerListener implements ActionListener
	{
		// /W #???
		// /W public static final AddMapLayer INSTANCE = new AddMapLayer();
		//public static final AddMapLayerListener INSTANCE = new AddMapLayerListener();

		@Override
		public void actionPerformed(ActionEvent e)
		{
			new AddRectangleMapAutocut().actionPerformed(e);
			setIsContentChanged(true);
		}
	}

	private class SaveCatalogListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Catalog catalog = OSMCDApp.getApp().getCatalog();
			// test for empty catalog (possible after deleting last map: see CatalogTreeModel#notifyNodeDelete(TreeNode node))
			if (catalog.calculateTilesToDownload() < 1)
			{
				JOptionPane.showMessageDialog(null, OSMCDStrs.RStr(OSMCDStrs.RStr("Catalog.Empty")),
						OSMCDStrs.RStr("CatalogTree.ERRBundleEmpty"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			// check catalogs name (possible?)
			if (catalog.getName() == null || catalog.getName().length() == 0)
			{
				log.info("not a catalog name given");
				JOptionPane.showMessageDialog(null, OSMCDStrs.RStr("Catalog.EnterName"), OSMBStrs.RStr("Error"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (jCatalogTree.save())
				setIsContentChanged(false);
		}
	}
			
	private class LoadCatalogListener implements ActionListener
	{
		// to be able to discard changes
		boolean bDiscard;
		LoadCatalogListener(boolean discard)
		{
			bDiscard = discard;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			File fileSel = null;
			if (!bDiscard)
			{
				JCatalogFileChooser catalogChooser = new JCatalogFileChooser();
		    int returnVal = catalogChooser.showOpenDialog(JCatalogsPanel.this);
		    if(returnVal != JFileChooser.APPROVE_OPTION)
		    	return;
				// else open ...
		    fileSel = catalogChooser.getSelectedFile();
			}
			else
			{
				fileSel = JCatalogsPanel.this.getCatalog().getFile();
			}
	    
	    OSMCDSettings settings = OSMCDSettings.getInstance();
			File catalogsDir = settings.getCatalogsDirectory();
			boolean isCatalogsDir = fileSel.getParentFile().equals(catalogsDir) ? true : false;
	    boolean isExistingFile = fileSel.isFile(); // exists() + normal(not a directory + other system-dependent criteria)
	    
	    String newCatalogsName = null;
	    
	    if (!isExistingFile) // new catalog
	    {
	    	Matcher m = Catalog.CATALOG_FILENAME_PATTERN.matcher(fileSel.getName());
	    	if (m.matches())
	    	{
	    		newCatalogsName = m.group(1);
	    		if (!isCatalogsDir) // anotherDir
	    		{
	    			CatalogRenameDialog renameDlg = new CatalogRenameDialog(JCatalogsPanel.this, newCatalogsName, null, null);
		    		renameDlg.actionPerformed(null);
		    		newCatalogsName = renameDlg.getChosenName();
	    		}
	    		// else: isCatalogsDir -> continue with newCatalog(newCatalogsName)
	    	}
	    	else // filename does not match // #rename?
	    	{
	    		CatalogRenameDialog renameDlg = new CatalogRenameDialog(JCatalogsPanel.this, null, null, fileSel.getName());
	    		renameDlg.actionPerformed(null);
	    		newCatalogsName = renameDlg.getChosenName();
	    	}
	    	if (newCatalogsName == null)
	    		return;
	    	// else: go on
	    	getCatalogTree().newCatalog(newCatalogsName);
	    	setIsContentChanged(false);
	    }
	    else // existing file selected
	    {
		    Catalog testCatalog = null;
				try
				{
					testCatalog = (Catalog) Catalog.load(fileSel);
				}
				catch (Exception e1) // (cException e1)
				{
					GUIExceptionHandler.processException(e1);
					return;
				}
				// multipleChecks
				if (testCatalog == null)
					return;
				if (getCatalog().getVersion() < Catalog.CURRENT_CATALOG_VERSION)
				{
					JOptionPane.showMessageDialog(null, OSMCDStrs.RStr("Catalog.VersionMismatch"), OSMCDStrs.RStr("CatalogTree.OldVersion"), JOptionPane.WARNING_MESSAGE);
					return;
				}
				// empty: getLayerCount() || calculateTilesToDownload() < 1 // only in SaveCatalogListener
				// Check for duplicate layer names || duplicate layer zoomlevels // #???

				String catalogsName = null;
				if (testCatalog.getName() == null) // #rename
				{
					CatalogRenameDialog renameDlg = new CatalogRenameDialog(JCatalogsPanel.this, null, fileSel.getPath(), "code: 'null'");
	    		renameDlg.actionPerformed(null);
	    		if (renameDlg.getChosenName() == null)
	    			return;
	    		// else:
	    		catalogsName = renameDlg.getChosenName();
	    		setIsContentChanged(true);
				}
				else
				{
					catalogsName = testCatalog.getName();
					Matcher m_catName = Catalog.CATALOG_FILENAME_PATTERN.matcher(Catalog.getCatalogFileName(catalogsName));
					if (m_catName.matches())
					{
						if (!isCatalogsDir || !(Catalog.getCatalogFileName(catalogsName).equals(fileSel.getName()))) // dir mismatch || name mismatch // #rename
						{
							CatalogRenameDialog renameDlg = new CatalogRenameDialog(JCatalogsPanel.this, catalogsName, fileSel.getPath(), null);
			    		renameDlg.actionPerformed(null);
			    		if (renameDlg.getChosenName() == null)
			    			return;
			    		// else: catalogsName is unused or user wants to overwrite
			    		setIsContentChanged(true);
						}
						// else: existing correctly named catalog in catalogsDir -> continue with loading testCatalog
						setIsContentChanged(false);
		    	}
			    else // catName does not match // #rename
			    {
						CatalogRenameDialog renameDlg = new CatalogRenameDialog(JCatalogsPanel.this, null, fileSel.getPath(), catalogsName);
		    		renameDlg.actionPerformed(null);
		    		if (renameDlg.getChosenName() == null)
		    			return;
		    		// else: go one
		    		catalogsName = renameDlg.getChosenName();
		    		setIsContentChanged(true);
			    }
	    	}
				testCatalog.setName(catalogsName);
				OSMCDApp.getApp().setCatalog(testCatalog);
				jCatalogTree.getTreeModel().notifyStructureChanged();

//				MainFrame.getMainGUI().previewMap.repaint(); // #???				
				log.info("loadCatalog(): name = " + getCatalogTree().getCatalog().getName());
				log.info("loadCatalog(): version = " + ((Catalog) getCatalogTree().getCatalog()).getVersion());
				log.info("loadCatalog(): LayerCount = " + ((Catalog) getCatalogTree().getCatalog()).getSize());
				log.info("loadCatalog(): file = " + ((Catalog) getCatalogTree().getCatalog()).getFile());
			}
		}
	}
}
