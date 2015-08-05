package osmcd.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import osmb.program.catalog.IfCatalog;
import osmb.utilities.GBC;
import osmcd.OSMCDStrs;
import osmcd.gui.actions.AddMapLayer;
import osmcd.gui.catalog.CatalogModelListener;
import osmcd.gui.catalog.CatalogNew;
import osmcd.gui.catalog.JCatalogNameField;
import osmcd.gui.catalog.JCatalogTree;
import osmcd.gui.catalog.JCatalogsPanel;
import osmcd.gui.components.JCollapsiblePanel;
import osmcd.gui.mapview.PreviewMap;

public class JCatalogContentPanel extends JCollapsiblePanel
{
	public JCatalogTree jCatalogTree = null;
	public JCatalogNameField catalogNameTextField = null;

	public JCatalogContentPanel(Container container)
	{
		super(container);
		// TODO Auto-generated constructor stub
	}

	private JCatalogContentPanel(String title)
	{
		super(title);
		// TODO Auto-generated constructor stub
	}

	public JCatalogContentPanel(String title, LayoutManager layout)
	{
		super(title, layout);
		// TODO Auto-generated constructor stub
	}

	public JCatalogContentPanel(Container container, String title)
	{
		super(container, title);
		// TODO Auto-generated constructor stub
	}

	public JCatalogContentPanel(PreviewMap previewMap)
	{
		this(OSMCDStrs.RStr("CatalogTree.Title"));
		contentContainer.setLayout(new GridBagLayout());

		GBC gbc_eol = GBC.eol().insets(2, 1, 2, 1);
		GBC gbc_std = GBC.std().insets(2, 1, 2, 1);

		jCatalogTree = new JCatalogTree(previewMap);
		JScrollPane treeScrollPane = new JScrollPane(jCatalogTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScrollPane.setMinimumSize(new Dimension(100, 150));
		treeScrollPane.setPreferredSize(new Dimension(100, 200));
		treeScrollPane.setAutoscrolls(true);
		// this.addContent(treeScrollPane, GBC.eol().fill().insets(0, 1, 0, 0));
		contentContainer.add(treeScrollPane, gbc_eol.fillH());

		JButton newCatalog = new JButton(OSMCDStrs.RStr("CatalogTree.NewButton"));
		contentContainer.add(newCatalog, gbc_std);
		newCatalog.addActionListener(new CatalogNew());
		JButton addToLayers = new JButton(OSMCDStrs.RStr("CatalogTree.AddSelected"));
		contentContainer.add(addToLayers, gbc_eol);
		addToLayers.addActionListener(AddMapLayer.INSTANCE);
		// /W --
		//contentContainer.add(new JLabel(OSMCDStrs.RStr("CatalogTree.NameLabel")), gbc_eol);
		// /W ++
		contentContainer.add(new JLabel(OSMCDStrs.RStr("CatalogTree.NameLabel")), gbc_std);
		JButton saveNewCatalog = new JButton("Save new catalog");
		contentContainer.add(saveNewCatalog, gbc_eol);
		saveNewCatalog.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				getCatalogTree().save(); ///W true: Erfolg else???
			}
		});
		// /W end
		
		// catalog name text field
		catalogNameTextField = new JCatalogNameField();
		catalogNameTextField.setColumns(12);
		catalogNameTextField.setActionCommand("bundleNameTextField");
		catalogNameTextField.setToolTipText(OSMCDStrs.RStr("Catalog.NameFieldTip"));
		this.addContent(catalogNameTextField, GBC.eol().insets(5, 2, 5, 3).fill(GBC.HORIZONTAL));
	}

	public JCatalogTree getCatalogTree()
	{
		return jCatalogTree;
	}

	public void addListener(JCatalogsPanel catalogsPanel)
	{
		jCatalogTree.getTreeModel().addTreeModelListener(new CatalogModelListener(jCatalogTree, catalogsPanel));
	}

	public IfCatalog getCatalog()
	{
		return jCatalogTree.getCatalog();
	}
}
