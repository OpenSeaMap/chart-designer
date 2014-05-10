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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import osmcbdef.exceptions.InvalidNameException;
import osmcbdef.gui.MainGUI;
import osmcbdef.gui.atlastree.JAtlasTree;
import osmcbdef.program.Logging;
import osmcbdef.program.interfaces.AtlasInterface;
//import osmcbdef.program.interfaces.LayerInterface;
import osmcbdef.program.interfaces.MapSource;
import osmcbdef.program.model.Layer;
import osmcbdef.program.model.MapSelection;
import osmcbdef.program.model.SelectedZoomLevels;
import osmcbdef.program.model.Settings;
import osmcbdef.program.model.TileImageParameters;
import osmcbdef.utilities.I18nUtils;

/**
 * AH OSM Selection strategy for SeaChartBundles
 * 
 * @author humbach
 * 
 *         include the lower zoomlevels covering the selection too allowed tile numbers are 5, 9, 17, 33, 65
 * 
 *         name scheme for trekbuddy atlases LNN with map names LNN-XXXX
 * 
 *         probably redirect this to AtlasCreator as base class of Atlas, since a lot of parameters are depending on the atlas format to produce Layer
 *         AtlasCreator.AddLayer(zoom, GUI.getUserText()) Add Layer (if necessary) and give name conformimg to name scheme of this atlas, use user given name,
 *         if any, AtlasCreator.
 * 
 */

public class AddRectangleMapAutocut implements ActionListener
{

	public void actionPerformed(ActionEvent event)
	{
		MainGUI mg = MainGUI.getMainGUI();
		JAtlasTree jAtlasTree = mg.jAtlasTree;
		final String mapNameFmt = "%s %03d";
		AtlasInterface atlasInterface = jAtlasTree.getAtlas();
		// String name = mg.getUserText();
		MapSource mapSource = mg.getSelectedMapSource();
		SelectedZoomLevels sZL = mg.getSelectedZoomLevels();
		MapSelection ms = mg.getMapSelectionCoordinates();
		if (ms == null)
		{
			JOptionPane.showMessageDialog(mg, I18nUtils.localizedStringForKey("msg_no_select_area"));
			return;
		}
		Settings settings = Settings.getInstance();
		// String errorText = mg.validateInput();
		// if (errorText.length() > 0) {
		// JOptionPane.showMessageDialog(mg, errorText, "Errors", JOptionPane.ERROR_MESSAGE);
		// return;
		// }

		int[] zoomLevels = sZL.getZoomLevels();
		if (zoomLevels.length == 0)
		{
			JOptionPane.showMessageDialog(mg, I18nUtils.localizedStringForKey("msg_no_zoom_level_selected"));
			return;
		}

		for (int zoom: zoomLevels)
		{
			boolean bNewLayer = false;
			// String layerName = name;
			String layerName = String.format("L%02d", zoom);
			Layer layer = null;
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
						bNewLayer = true;
						break;
					}
					catch (InvalidNameException e)
					{
						// layerName = layerName + "_" + Integer.toString(c++);
						for (int layerNum = 0; layerNum < atlasInterface.getLayerCount(); ++layerNum)
						{
							if (layerName.compareToIgnoreCase(atlasInterface.getLayer(layerNum).getName()) == 0)
							{
								layer = (Layer) atlasInterface.getLayer(layerNum);
								success = true;
								break;
							}
						}
					}
				}
				while (!success);
			}

			Point tl = ms.getTopLeftPixelCoordinate(zoom);
			Point br = ms.getBottomRightPixelCoordinate(zoom);
			TileImageParameters customTileParameters = mg.getSelectedTileImageParameters();
			try
			{
				String mapName = String.format(mapNameFmt, new Object[] {layerName, layer.getMapCount()});
				layer.addMapsAutocut(mapName, mapSource, tl, br, zoom, customTileParameters, settings.maxMapSize, settings.mapOverlapTiles);
			}
			catch (InvalidNameException e)
			{
				Logging.LOG.error("", e);
			}
			if (bNewLayer)
			{
				atlasInterface.addLayer(layer);
			}
			// Check for duplicate maps either here or in layer.addMapsAutocut()
			jAtlasTree.getTreeModel().notifyNodeInsert(layer);
		}
	}
}
