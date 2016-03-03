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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;

import osmb.exceptions.InvalidNameException;
import osmb.mapsources.ACMapSource;
import osmb.program.catalog.Catalog;
import osmb.program.map.Layer;
import osmb.program.tiles.TileImageParameters;
import osmcd.OSMCDSettings;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;
import osmcd.gui.catalog.JCatalogTree;
import osmcd.program.Logging;
import osmcd.program.MapSelection;
import osmcd.program.SelectedZoomLevels;

/**
 * AH OSM Selection strategy for SeaChartBundles
 * 
 * @author humbach
 * 
 *         include the lower zoomlevels covering the selection too allowed tile numbers are 5, 9, 17, 33, 65
 * 
 *         name scheme for trekbuddy atlases LNN with iMap names LNN-XXXX
 * 
 *         probably redirect this to AtlasCreator as base class of Bundle, since a lot of parameters are depending on the atlas format to produce Layer
 *         AtlasCreator.AddLayer(zoom, GUI.getUserText()) Add Layer (if necessary) and give name conforming to name scheme of this atlas, use user given name,
 *         if any, AtlasCreator.
 * 
 */

public class AddRectangleMapAutocut implements ActionListener
{
	@Override
	public void actionPerformed(ActionEvent event)
	{
		MainFrame mg = MainFrame.getMainGUI();
		JCatalogTree catalogTree = mg.getCatalogTree();
		// final String mapNameFmt = "%s %03d";
		// final String mapNameFmt = "%s";
		Catalog catalog = catalogTree.getCatalog();
		// String name = mg.getUserText();
		ACMapSource mapSource = mg.getSelectedMapSource();
		SelectedZoomLevels sZL = mg.getSelectedZoomLevels();
		MapSelection ms = mg.getMapSelectionCoordinates();
		if (ms == null)
		{
			JOptionPane.showMessageDialog(mg, OSMCDStrs.RStr("GUI.NoSelectedArea"));
			return;
		}
		OSMCDSettings settings = OSMCDSettings.getInstance();
		// String errorText = mg.validateInput();
		// if (errorText.length() > 0) {
		// JOptionPane.showMessageDialog(mg, errorText, "Errors", JOptionPane.ERROR_MESSAGE);
		// return;
		// }

		boolean firstLayers = (catalog.getLayerCount() == 0);
		int[] zoomLevels = sZL.getZoomLevels();
		if (zoomLevels.length == 0)
		{
			JOptionPane.showMessageDialog(mg, OSMCDStrs.RStr("GUI.NoZoomLevels"));
			return;
		}

		for (int z = zoomLevels.length; z > 0; --z)
		{
			int zoom = zoomLevels[z - 1];
			boolean bNewLayer = false;
			// String layerName = name;
			String layerName = String.format("L%02d", zoom);
			Layer layer = null;
			boolean success = false;
			if ((layer = Layer.GetLayerByZoom(catalog, zoom)) != null)
				success = true;
			else
			{
				do
				{
					try
					{
						layer = new Layer(catalog, layerName, zoom);
						success = true;
						bNewLayer = true;
						break;
					}
					catch (InvalidNameException e)
					{
						// layerName = layerName + "_" + Integer.toString(c++);
						for (int layerNum = 0; layerNum < catalog.getLayerCount(); ++layerNum)
						{
							if (layerName.compareToIgnoreCase(catalog.getLayer(layerNum).getName()) == 0)
							{
								layer = (Layer) catalog.getLayer(layerNum);
								success = true;
								break;
							}
						}
					}
				} while (!success);
			}

			Point tl = ms.getTopLeftPixelCoordinate(zoom);
			Point br = ms.getBottomRightPixelCoordinate(zoom);
			TileImageParameters customTileParameters = mg.getSelectedTileImageParameters();
			try
			{
				// String mapName = String.format(mapNameFmt, new Object[]
				// { layerName, layer.getMapCount() });
				String mapName = layerName;
				layer.addMapsAutocut(mapName, mapSource, tl, br, zoom, customTileParameters, settings.getMaxMapSize(), settings.getMapOverlapTiles());
				// #??? maybe a map is added in addMapsAutocut
				mg.getCatalogTree().setHasUnsavedChanges(true);
			}
			catch (InvalidNameException e)
			{
				Logging.LOG.error("", e);
			}
			if (bNewLayer)
			{
				catalog.addLayer(layer);
				catalogTree.getTreeModel().notifyNodeInsert(layer); // == mg.notifyLayerInsert(Layer layer)
				if (firstLayers)
					catalogTree.getTreeModel().notifyStructureChanged();
			}
			// Check for duplicate maps either here or in layer.addMapsAutocut()

			// 'repaint' layer in content
			catalogTree.getTreeModel().notifyStructureChanged(catalog, layer);
			// scroll to new map in expanded layer (finally with minimum zoomlevel)
			if (catalogTree.isExpanded(catalogTree.getTreeModel().getNodePath(layer)))
			{
				TreeNode lastMap = layer.getChildAt(layer.getChildCount() - 1);
				catalogTree.scrollPathToVisible(catalogTree.getTreeModel().getNodePath(lastMap));
			}
		}
	}
}
