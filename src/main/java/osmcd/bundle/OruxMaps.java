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
package osmcd.bundle;

import java.awt.Color;
import java.io.File;

import osmcd.ChartBundleName;
import osmcd.SupportedParameters;
import osmcd.exceptions.BundleTestException;
import osmcd.mapsources.mapspace.MercatorPower2MapSpace;
import osmcd.program.interfaces.LayerInterface;
import osmcd.program.interfaces.MapInterface;
import osmcd.program.interfaces.MapSource;
import osmcd.program.model.TileImageParameters.Name;

/**
 * Creates maps using the OruxMaps (Android) atlas format.
 * 
 * @author orux
 */
@ChartBundleName("OruxMaps")
@SupportedParameters(names = {Name.format})
public class OruxMaps extends ChartBundle
{
	protected static final String ORUXMAPS_EXT = ".otrk2.xml"; // Calibration file extension
	protected static final int TILE_SIZE = 512; // OruxMaps tile size
	protected String calVersionCode;
	protected static final Color BG_COLOR = new Color(0xcb, 0xd3, 0xf3); // OruxMaps background color
	protected File oruxMapsMainDir; // Each layer is a Main map for OruxMaps
	protected File oruxMapsLayerDir; // Each map is a Layer map for OruxMaps
	protected File oruxMapsImagesDir; // Images directory for each map
	protected LayerInterface currentLayer;
	protected String mapName; // We need to override the map name, All maps must have the same prefix (layer name)

	public OruxMaps() {
		super();
		calVersionCode = "2.1";
	}

	@Override
	public boolean testMapSource(MapSource mapSource)
	{
		return (mapSource.getMapSpace() instanceof MercatorPower2MapSpace);
	}

	@Override
	protected void testBundle() throws BundleTestException
	{
		for (LayerInterface layer: atlas)
		{
			int cont = layer.getMapCount();
			for (int i = 0; i < cont; i++)
			{
				MapInterface currMap = layer.getMap(i);
				int currZoomLevel = currMap.getZoom();
				for (int j = i + 1; j < cont; j++)
				{
					MapInterface nextMap = layer.getMap(j);
					int nextZoomLevel = nextMap.getZoom();
					if (currZoomLevel == nextZoomLevel)
						throw new BundleTestException("Unable to create a map with more than a layer with the same zoom level: " + currMap + " & " + nextMap
								+ "\nPossible causes:\n" + "You are combining several layers (using drag & drop in 'Bundle Content')\n"
								+ "You are creating a large map, and you have not selected the maximum value in 'Settings - Map size'");
				}
			}
		}
	}
}
