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
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

<<<<<<< HEAD
import osmb.exceptions.InvalidNameException;
import osmb.mapsources.IfMapSource;
import osmb.program.catalog.IfCatalog;
import osmb.program.map.IfMapSpace;
import osmb.program.map.Layer;
import osmb.program.map.MapPolygon;
import osmb.program.tiles.TileImageParameters;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;
import osmcd.gui.catalog.JCatalogTree;
import osmcd.gui.mapview.AbstractPolygonSelectionMapController;
import osmcd.gui.mapview.JMapViewer;
import osmcd.program.SelectedZoomLevels;
=======
import osmcb.exceptions.InvalidNameException;
import osmcb.program.interfaces.IfBundle;
import osmcb.program.interfaces.IfMapSource;
import osmcb.program.interfaces.IfMapSpace;
import osmcb.program.model.Layer;
import osmcb.program.model.MapPolygon;
import osmcb.program.model.TileImageParameters;
import osmcd.OSMCDStrs;
import osmcd.gui.MainGUI;
import osmcd.gui.bundletree.JBundleTree;
import osmcd.gui.mapview.JMapViewer;
import osmcd.gui.mapview.controller.AbstractPolygonSelectionMapController;
import osmcd.program.model.SelectedZoomLevels;
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318

/**
 * Handles the addition of maps to the catalog
 * 
 * @author humbach
 *
 */
public class AddPolygonMapLayer implements ActionListener
{
	@Override
	public void actionPerformed(ActionEvent event)
	{
		MainFrame mg = MainFrame.getMainGUI();
		// use fixed layer name "LZZ" with ZZ = zoom level
		// String name = mg.getUserTextCatalogName();
		String name = "L";

		AbstractPolygonSelectionMapController msc = (AbstractPolygonSelectionMapController) mg.previewMap.getMapSelectionController();

<<<<<<< HEAD
		JCatalogTree catalogTree = mg.getCatalogTree();
		// final String mapNameFmt = "%s %02d";
		String mapNameFmt = "%s%02d";
		IfCatalog catalog = catalogTree.getCatalog();
=======
		JBundleTree jAtlasTree = mg.jBundleTree;
		final String mapNameFmt = "%s %02d";
		IfBundle atlasInterface = jAtlasTree.getBundle();
		String name = mg.getUserText();
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
		IfMapSource mapSource = mg.getSelectedMapSource();
		IfMapSpace mapSpace = mapSource.getMapSpace();
		SelectedZoomLevels sZL = mg.getSelectedZoomLevels();

		int[] zoomLevels = sZL.getZoomLevels();
		if (zoomLevels.length == 0)
		{
			JOptionPane.showMessageDialog(mg, OSMCDStrs.RStr("msg_no_zoom_level_selected"));
			return;
		}

		List<Point> polygonPoints = msc.getPolygonPoints();
		Layer layer = null;

		for (int zoom : zoomLevels)
		{
			String layerName = name;
			int c = 1;
			boolean success = false;
			if ((layer = Layer.GetLayerByZoom(catalog, zoom)) != null)
				success = true;
			else
			{
				do
				{
					try
					{
<<<<<<< HEAD
						layer = new Layer(catalog, layerName, zoom);
=======
						layer = new Layer(atlasInterface, layerName, zoom);
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
						success = true;
					}
					catch (InvalidNameException e)
					{
						layerName = name + "_" + Integer.toString(c++);
					}
<<<<<<< HEAD
				} while (!success);
=======
				}
				while (!success);
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
			}

			int[] xpoints = new int[polygonPoints.size()];
			int[] ypoints = new int[polygonPoints.size()];
			for (int i = 0; i < xpoints.length; i++)
			{
				Point p = mapSpace.changeZoom(polygonPoints.get(i), JMapViewer.MAX_ZOOM, zoom);
				xpoints[i] = p.x;
				ypoints[i] = p.y;
			}
			TileImageParameters customTileParameters = mg.getSelectedTileImageParameters();
			Polygon polygon = new Polygon(xpoints, ypoints, xpoints.length);
			// Rectangle bounds = polygon.getBounds();
			// int maxMapSize = OSMCDSettings.getInstance().maxMapSize;
			// System.out.println(bounds.height + " " + bounds.width);

			String mapName = String.format(mapNameFmt, new Object[]
			{ layerName, zoom });
			MapPolygon map = new MapPolygon(layer, mapName, mapSource, zoom, polygon, customTileParameters);
			layer.addMap(map);
		}
		catalog.addLayer(layer);
		catalogTree.getTreeModel().notifyNodeInsert(layer);

		msc.finishPolygon();
	}
}
