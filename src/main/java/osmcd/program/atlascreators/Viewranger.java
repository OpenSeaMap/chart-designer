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
package osmcd.program.atlascreators;

import java.io.File;

import osmcd.exceptions.AtlasTestException;
import osmcd.program.annotations.AtlasCreatorName;
import osmcd.program.annotations.SupportedParameters;
import osmcd.program.atlascreators.tileprovider.ConvertedRawTileProvider;
import osmcd.program.atlascreators.tileprovider.PngTileProvider;
import osmcd.program.atlascreators.tileprovider.TileProvider;
import osmcd.program.interfaces.LayerInterface;
import osmcd.program.interfaces.MapInterface;
import osmcd.program.model.TileImageType;
import osmcd.program.model.TileImageParameters.Name;

/**
 * http://sourceforge.net/p/osmcd/feature-requests/219/
 */
@AtlasCreatorName("Viewranger")
@SupportedParameters(names = { Name.format_png })
public class Viewranger extends OSMTracker {

	public Viewranger() {
		super();
		tileFileNamePattern = "%d/%d/%d";
	}

	@Override
	protected void testAtlas() throws AtlasTestException {
		for (LayerInterface layer : atlas) {
			for (MapInterface map : layer) {
				if (map.getParameters() == null)
					continue;
				if (!TileImageType.PNG.equals(map.getParameters().getFormat().getType()))
					throw new AtlasTestException("Only PNG output format allowed for Viewranger", map);
				if (map.getZoom() > 18)
					throw new AtlasTestException("Unsupported zoom level: " + map.getZoom()
							+ "\nMaximum supported zoom level is 18", map);
				if (map.getZoom() < 3)
					throw new AtlasTestException("Unsupported zoom level: " + map.getZoom()
							+ "\nMinimum suupported toom level is 3", map);
			}
		}
	}

	@Override
	public void initializeMap(MapInterface map, TileProvider mapTileProvider) {
		super.initializeMap(map, mapTileProvider);
		mapDir = new File(atlasDir, map.getLayer().getName());
		tileType = "";
		if (parameters == null)
			mapDlTileProvider = new PngTileProvider(mapDlTileProvider);
		else
			mapDlTileProvider = new ConvertedRawTileProvider(mapDlTileProvider, parameters.getFormat());
	}

}
