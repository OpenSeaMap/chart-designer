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
package osmcbdef.gui.actions;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import osmcbdef.exceptions.InvalidNameException;
import osmcbdef.gui.MainGUI;
import osmcbdef.gui.atlastree.JAtlasTree;
import osmcbdef.gui.mapview.JMapViewer;
import osmcbdef.gui.mapview.controller.AbstractPolygonSelectionMapController;
import osmcbdef.program.interfaces.AtlasInterface;
import osmcbdef.program.interfaces.MapSource;
import osmcbdef.program.interfaces.MapSpace;
import osmcbdef.program.model.Layer;
import osmcbdef.program.model.MapPolygon;
import osmcbdef.program.model.SelectedZoomLevels;
import osmcbdef.program.model.TileImageParameters;
import osmcbdef.utilities.I18nUtils;

public class AddPolygonMapLayer implements ActionListener
{

	public void actionPerformed(ActionEvent event)
	{
		MainGUI mg = MainGUI.getMainGUI();

		AbstractPolygonSelectionMapController msc = (AbstractPolygonSelectionMapController) mg.previewMap.getMapSelectionController();

		JAtlasTree jAtlasTree = mg.jAtlasTree;
		final String mapNameFmt = "%s %02d";
		AtlasInterface atlasInterface = jAtlasTree.getAtlas();
		String name = mg.getUserText();
		MapSource mapSource = mg.getSelectedMapSource();
		MapSpace mapSpace = mapSource.getMapSpace();
		SelectedZoomLevels sZL = mg.getSelectedZoomLevels();

		int[] zoomLevels = sZL.getZoomLevels();
		if (zoomLevels.length == 0)
		{
			JOptionPane.showMessageDialog(mg, I18nUtils.localizedStringForKey("msg_no_zoom_level_selected"));
			return;
		}

		List<Point> polygonPoints = msc.getPolygonPoints();
		Layer layer = null;

		for (int zoom: zoomLevels)
		{
			String layerName = name;
			int c = 1;
			boolean success = false;
			if ((layer = Layer.GetLayerByZoom(atlasInterface, zoom)) != null)
				success = true;
			else
			{
			do
			{
				try
				{
					layer = new Layer(atlasInterface, layerName, zoom);
					success = true;
				}
				catch (InvalidNameException e)
				{
					layerName = name + "_" + Integer.toString(c++);
				}
				} while (!success);
			}

			int xpoints[] = new int[polygonPoints.size()];
			int ypoints[] = new int[polygonPoints.size()];
			for (int i = 0; i < xpoints.length; i++)
			{
				Point p = mapSpace.changeZoom(polygonPoints.get(i), JMapViewer.MAX_ZOOM, zoom);
				xpoints[i] = p.x;
				ypoints[i] = p.y;
			}
			TileImageParameters customTileParameters = mg.getSelectedTileImageParameters();
			Polygon polygon = new Polygon(xpoints, ypoints, xpoints.length);
			// Rectangle bounds = polygon.getBounds();
			// int maxMapSize = Settings.getInstance().maxMapSize;
			// System.out.println(bounds.height + " " + bounds.width);

			String mapName = String.format(mapNameFmt, new Object[] {layerName, zoom});
			MapPolygon map = new MapPolygon(layer, mapName, mapSource, zoom, polygon, customTileParameters);
			layer.addMap(map);
		}
		atlasInterface.addLayer(layer);
		jAtlasTree.getTreeModel().notifyNodeInsert(layer);

		msc.finishPolygon();
	}

}
