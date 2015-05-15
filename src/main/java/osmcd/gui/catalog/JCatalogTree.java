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

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import osmb.program.catalog.Catalog;
import osmb.program.catalog.IfCapabilityDeletable;
import osmb.program.catalog.IfCatalog;
import osmb.program.catalog.IfCatalogObject;
import osmb.program.catalog.IfCatalogProfile;
import osmb.program.map.IfLayer;
import osmb.program.map.IfMap;
import osmb.program.tiles.TileImageParameters;
import osmb.utilities.GUIExceptionHandler;
import osmb.utilities.geo.EastNorthCoordinate;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;
import osmcd.gui.components.IfToolTipProvider;
import osmcd.gui.mapview.MapAreaHighlightingLayer;
import osmcd.gui.mapview.PreviewMap;
import osmcd.program.MapSelection;

/**
 * The catalog tree is a presentation of the catalogs layers and maps. It does not hold any information about the desired format for any bundle to be created
 * from this catalog. Those information exists only temporarily in ChartBundlers commandline.
 * 
 * @author humbach
 *
 */
public class JCatalogTree extends JTree implements Autoscroll
{
	// static/class data
	private static final long serialVersionUID = 1L;
	private static final int margin = 12;
	private static final String MSG_CATALOG_VERSION_MISMATCH = OSMCDStrs.RStr("Catalog.VersionMismatch");
	private static final String MSG_CATALOG_DATA_CHECK_FAILED = OSMCDStrs.RStr("Catalog.DataCheckFailed");
	private static final String MSG_CATALOG_EMPTY = OSMCDStrs.RStr("Catalog.Empty");
	private static final String ACTION_DELETE_NODE = OSMCDStrs.RStr("CatalogTree.DeleteNode");
	private static final Logger log = Logger.getLogger(JCatalogTree.class);

	// instance data
	private CatalogTreeModel treeModel;
	private PreviewMap mapView;
	protected NodeRenderer nodeRenderer;
	protected String defaultToolTiptext;
	protected KeyStroke deleteNodeKS;
	protected DragDropController ddc;
	protected boolean displaySelectedMapArea = false;

	public JCatalogTree(PreviewMap mapView)
	{
		super(new CatalogTreeModel());
		if (mapView == null)
			throw new NullPointerException(OSMCDStrs.RStr("CatalogTree.MVParamNULL"));
		this.mapView = mapView;
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		ddc = new DragDropController(this);
		treeModel = (CatalogTreeModel) getModel();
		// setRootVisible(false);
		setShowsRootHandles(true);
		nodeRenderer = new NodeRenderer();
		setCellRenderer(nodeRenderer);
		setCellEditor(new NodeEditor(this));
		setToolTipText(OSMCDStrs.RStr("Catalog.Empty"));
		defaultToolTiptext = OSMCDStrs.RStr("CatalogTree.DefaultTip");
		setAutoscrolls(true);
		addMouseListener(new MouseController(this));

		InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getActionMap();

		// map moving
		inputMap.put(deleteNodeKS = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ACTION_DELETE_NODE);
		actionMap.put(ACTION_DELETE_NODE, new AbstractAction(OSMCDStrs.RStr("lp_bundle_pop_menu_delete_node"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				deleteSelectedNode();
				JCatalogTree.this.mapView.repaint();
			}
		});
	}

	/**
	 * Tests if we don't have an empty catalog
	 * 
	 * @return
	 */
	public boolean testCatalogContentValid()
	{
		boolean bValid = false;
		IfCatalog catalog = getCatalog();
		// if (IfRequiresSQLite.class.isAssignableFrom(catalog.getOutputFormat().getMapCreatorClass()))
		// {
		// if (!SQLiteLoader.loadSQLiteOrShowError())
		// return false;
		// }
		if ((catalog.getFile() != null) || (catalog.calculateTilesToDownload() > 0))
		{
			bValid = true;
		}
		else
		{
			JOptionPane.showMessageDialog(null, OSMCDStrs.RStr("Gen.HTMLElem") + MSG_CATALOG_EMPTY + OSMCDStrs.RStr("Gen.HTMLElemEND"),
					OSMCDStrs.RStr("CatalogTree.ERRBundleEmpty"), JOptionPane.ERROR_MESSAGE);
		}
		return bValid;
	}

	@Override
	public String getToolTipText(MouseEvent event)
	{
		if (getRowForLocation(event.getX(), event.getY()) == -1)
			return defaultToolTiptext;
		TreePath curPath = getPathForLocation(event.getX(), event.getY());
		Object o = curPath.getLastPathComponent();
		if (o == null || !(o instanceof IfToolTipProvider))
			return null;
		return ((IfToolTipProvider) o).getToolTip();
	}

	@Override
	public boolean isPathEditable(TreePath path)
	{
		return super.isPathEditable(path) && (path.getLastPathComponent() instanceof IfCatalogObject);
	}

	public CatalogTreeModel getTreeModel()
	{
		return treeModel;
	}

	// public void newCatalog(String name, BundleOutputFormat format)
	// {
	// log.debug(OSMCDStrs.RStr("CatalogTree.CreateNewBundle"));
	// CatalogProfile catalog = CatalogProfile.newInstance();
	// // catalog.setOutputFormat(format);
	// catalog.setName(name);
	// treeModel.setCatalog(catalog);
	// mapView.repaint();
	// }
	public void newCatalog(String name)
	{
		log.debug(OSMCDStrs.RStr("CatalogTree.CreateNewBundle"));
		Catalog catalog = Catalog.newInstance();
		catalog.setName(name);
		treeModel.setCatalog(catalog);

		mapView.repaint();
	}

	// /**
	// * Changes the bundle format
	// */
	// public void convertBundle(BundleOutputFormat format)
	// {
	// log.debug(OSMCDStrs.RStr("CatalogTree.ConvertBundle") + format);
	// treeModel.getBundle().setOutputFormat(format);
	// }
	//
	public void deleteSelectedNode()
	{
		TreePath path = getSelectionPath();
		if (path == null)
			return;
		TreeNode selected = (TreeNode) path.getLastPathComponent();
		int[] selectedRows = getSelectionRows();

		if (!(selected instanceof IfCapabilityDeletable))
			return;
		treeModel.notifyNodeDelete(selected);
		((IfCapabilityDeletable) selected).delete();

		int selRow = Math.min(selectedRows[0], getRowCount() - 1);
		TreePath path1 = path.getParentPath();
		TreePath path2 = getPathForRow(selRow).getParentPath();
		if (path1 != path2)
		{
			// next row belongs to different parent node -> we select parent
			// node instead
			setSelectionPath(path1);
		}
		else
		{
			setSelectionRow(selRow);
			scrollRowToVisible(selRow);
		}
	}

	public IfCatalog getCatalog()
	{
		return treeModel.getCatalog();
	}

	public boolean load(IfCatalogProfile profile)
	{
		log.debug(OSMCDStrs.RStr("CatalogTree.LoadCatalog") + profile);
		try
		{
			Catalog catalog = null;
			treeModel.load(profile);
			// Check if the file we got is really a catalog
			if (treeModel.getCatalog() instanceof Catalog)
			{
				catalog = (Catalog) treeModel.getCatalog();
				if (catalog.getVersion() < Catalog.CURRENT_CATALOG_VERSION)
				{
					JOptionPane.showMessageDialog(null, MSG_CATALOG_VERSION_MISMATCH, OSMCDStrs.RStr("CatalogTree.OldVersion"), JOptionPane.WARNING_MESSAGE);
					return true;
				}
			}
			boolean problemsDetected = treeModel.getCatalog().check();
			if (problemsDetected)
			{
				JOptionPane.showMessageDialog(null, MSG_CATALOG_DATA_CHECK_FAILED, OSMCDStrs.RStr("CatalogTree.ProblemLoading"), JOptionPane.WARNING_MESSAGE);
			}
			return true;
		}
		catch (Exception e)
		{
			GUIExceptionHandler.processException(e);
			return false;
		}
	}

	// public boolean save(IfCatalogProfile catalog)
	public boolean save()
	{
		try
		{
			treeModel.save();
			return true;
		}
		catch (Exception e)
		{
			GUIExceptionHandler.processException(e);
			return false;
		}
	}

	public void showNodePopupMenu(MouseEvent event)
	{
		JPopupMenu pm = new JPopupMenu();
		final TreePath selPath = getPathForLocation(event.getX(), event.getY());
		setSelectionPath(selPath);
		JMenuItem mi = null;
		if (selPath != null)
		{
			// not clicked on empty area
			final Object o = selPath.getLastPathComponent();
			if (o == null)
				return;
			if (o instanceof IfToolTipProvider)
			{
				mi = new JMenuItem(OSMCDStrs.RStr("lp_bundle_pop_menu_show_detail"));
				mi.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						IfToolTipProvider ttp = (IfToolTipProvider) o;
						JOptionPane.showMessageDialog(MainFrame.getMainGUI(), ttp.getToolTip());
					}
				});
				pm.add(mi);
			}
			if (o instanceof IfCatalogObject)
			{
				final JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(OSMCDStrs.RStr("lp_bundle_pop_menu_display_select_area"));
				final MapAreaHighlightingLayer msl = new MapAreaHighlightingLayer(this);
				cbmi.setSelected(displaySelectedMapArea);
				cbmi.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						if (displaySelectedMapArea)
						{
							MapAreaHighlightingLayer.removeHighlightingLayers();
						}
						else
						{
							mapView.setSelectionByTileCoordinate(null, null, false);
							MapAreaHighlightingLayer.removeHighlightingLayers();
							mapView.mapLayers.add(msl);
						}
						displaySelectedMapArea = !displaySelectedMapArea;
						mapView.repaint();
					}
				});
				pm.add(cbmi);
			}
			if (o instanceof IfMap)
			{
				mi = new JMenuItem(OSMCDStrs.RStr("lp_bundle_pop_menu_select_map_box"));
				mi.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						IfMap map = (IfMap) o;
						mapView.setMapSource(map.getMapSource());
						mapView.setSelectionByTileCoordinate(map.getZoom(), map.getMinTileCoordinate(), map.getMaxTileCoordinate(), true);
					}
				});
				pm.add(mi);
				mi = new JMenuItem(OSMCDStrs.RStr("lp_bundle_pop_menu_zoom_to_map_box"));
				mi.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						IfMap map = (IfMap) o;
						MapSelection ms = new MapSelection(map);
						mapView.setMapSource(map.getMapSource());
						mapView.setSelectionAndZoomTo(ms, true);
						mapView.setSelectionByTileCoordinate(map.getZoom(), map.getMinTileCoordinate(), map.getMaxTileCoordinate(), true);
					}
				});
				pm.add(mi);
			}
			if (o instanceof IfLayer)
			{
				mi = new JMenuItem(OSMCDStrs.RStr("lp_bundle_pop_menu_zoom_to"));
				mi.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						IfLayer layer = (IfLayer) o;
						EastNorthCoordinate max = new EastNorthCoordinate(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
						EastNorthCoordinate min = new EastNorthCoordinate(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
						for (IfMap map : layer)
						{
							MapSelection ms = new MapSelection(map);
							EastNorthCoordinate mapMax = ms.getMax();
							EastNorthCoordinate mapMin = ms.getMin();
							max.lat = Math.max(max.lat, mapMax.lat);
							max.lon = Math.max(max.lon, mapMax.lon);
							min.lat = Math.min(min.lat, mapMin.lat);
							min.lon = Math.min(min.lon, mapMin.lon);
						}
						MapSelection ms = new MapSelection(mapView.getMapSource(), max, min);
						mapView.zoomTo(ms);
					}
				});
				pm.add(mi);
			}
			if (o instanceof IfCatalogObject)
			{
				mi = new JMenuItem(OSMCDStrs.RStr("lp_bundle_pop_menu_rename"));
				mi.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						JCatalogTree.this.startEditingAtPath(selPath);
					}
				});
				pm.add(mi);
				mi = new JMenuItem(OSMCDStrs.RStr("lp_bundle_pop_menu_apply_tile_process"));
				mi.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						IfCatalogObject catalogObj = (IfCatalogObject) o;
						TileImageParameters p = MainFrame.getMainGUI().getSelectedTileImageParameters();
						applyTileImageParameters(catalogObj, p);
					}
				});
				pm.add(mi);
			}
			if (o instanceof IfCapabilityDeletable)
			{
				pm.addSeparator();
				mi = new JMenuItem(getActionMap().get(ACTION_DELETE_NODE));
				mi.setAccelerator(deleteNodeKS);
				pm.add(mi);
			}
		}
		if (pm.getComponentCount() > 0)
			pm.addSeparator();
		mi = new JMenuItem(OSMCDStrs.RStr("lp_bundle_pop_menu_clear_atals"));
		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				newCatalog("NoName");
			}
		});
		pm.add(mi);
		pm.show(this, event.getX(), event.getY());
	}

	protected void applyTileImageParameters(Object o, TileImageParameters p)
	{
		if (o instanceof Iterable<?>)
		{
			Iterable<?> it = (Iterable<?>) o;
			for (Object ao : it)
			{
				applyTileImageParameters(ao, p);
			}
		}
		else if (o instanceof IfMap)
		{
			((IfMap) o).setParameters(p);
		}
	}

	public void selectElementOnMap(Object o)
	{
		if (o instanceof IfMap)
		{
			IfMap map = (IfMap) o;
			mapView.setMapSource(map.getMapSource());
			mapView.setSelectionByTileCoordinate(map.getZoom(), map.getMinTileCoordinate(), map.getMaxTileCoordinate(), true);
		}
	}

	@Override
	public void autoscroll(Point cursorLocn)
	{
		int realrow = getRowForLocation(cursorLocn.x, cursorLocn.y);
		Rectangle outer = getBounds();
		realrow = (cursorLocn.y + outer.y <= margin ? realrow < 1 ? 0 : realrow - 1 : realrow < getRowCount() - 1 ? realrow + 1 : realrow);
		scrollRowToVisible(realrow);
	}

	@Override
	public Insets getAutoscrollInsets()
	{
		Rectangle outer = getBounds();
		Rectangle inner = getParent().getBounds();
		return new Insets(inner.y - outer.y + margin, inner.x - outer.x + margin, outer.height - inner.height - inner.y + outer.y + margin, outer.width
				- inner.width - inner.x + outer.x + margin);
	}

}
