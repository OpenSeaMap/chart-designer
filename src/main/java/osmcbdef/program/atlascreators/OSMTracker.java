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
package osmcbdef.program.atlascreators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import osmcbdef.exceptions.MapCreationException;
import osmcbdef.mapsources.mapspace.MercatorPower2MapSpace;
import osmcbdef.program.annotations.AtlasCreatorName;
import osmcbdef.program.annotations.SupportedParameters;
import osmcbdef.program.atlascreators.impl.MapTileWriter;
import osmcbdef.program.atlascreators.tileprovider.ConvertedRawTileProvider;
import osmcbdef.program.atlascreators.tileprovider.TileProvider;
import osmcbdef.program.interfaces.MapInterface;
import osmcbdef.program.interfaces.MapSource;
import osmcbdef.program.model.TileImageParameters.Name;
import osmcbdef.utilities.Utilities;

/**
 * Creates maps identical to the atlas format used by OSMTracker.
 * 
 * Please note that this atlas format ignores the defined atlas structure. It uses a separate directory for each used
 * map source and inside one directory for each zoom level.
 */
@AtlasCreatorName("OSMTracker tile storage")
@SupportedParameters(names = { Name.format })
public class OSMTracker extends AtlasCreator {

	protected String tileFileNamePattern = "%d/%d/%d.%s";

	protected File mapDir = null;

	protected String tileType = null;

	protected MapTileWriter mapTileWriter = null;

	@Override
	public boolean testMapSource(MapSource mapSource) {
		return MercatorPower2MapSpace.INSTANCE_256.equals(mapSource.getMapSpace());
	}

	@Override
	public void initializeMap(MapInterface map, TileProvider mapTileProvider) {
		super.initializeMap(map, mapTileProvider);
		mapDir = new File(atlasDir, map.getMapSource().getName());
		tileType = mapSource.getTileImageType().getFileExt();
		if (parameters != null) {
			mapDlTileProvider = new ConvertedRawTileProvider(mapDlTileProvider, parameters.getFormat());
			tileType = parameters.getFormat().getFileExt();
		}
	}

	public void createMap() throws MapCreationException, InterruptedException {
		// This means there should not be any resizing of the tiles.
		if (mapTileWriter == null)
			mapTileWriter = new OSMTileWriter();
		createTiles();
	}

	protected void createTiles() throws InterruptedException, MapCreationException {
		atlasProgress.initMapCreation((xMax - xMin + 1) * (yMax - yMin + 1));
		ImageIO.setUseCache(false);

		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				checkUserAbort();
				atlasProgress.incMapCreationProgress();
				try {
					byte[] sourceTileData = mapDlTileProvider.getTileData(x, y);
					if (sourceTileData != null)
						mapTileWriter.writeTile(x, y, tileType, sourceTileData);
				} catch (IOException e) {
					throw new MapCreationException("Error writing tile image: " + e.getMessage(), map, e);
				}
			}
		}
	}

	protected class OSMTileWriter implements MapTileWriter {

		public void writeTile(int tilex, int tiley, String tileType, byte[] tileData) throws IOException {
			File file = new File(mapDir, String.format(tileFileNamePattern, zoom, tilex, tiley, tileType));
			writeTile(file, tileData);
		}

		protected void writeTile(File file, byte[] tileData) throws IOException {
			Utilities.mkDirs(file.getParentFile());
			FileOutputStream out = new FileOutputStream(file);
			try {
				out.write(tileData);
			} finally {
				Utilities.closeStream(out);
			}
		}

		public void finalizeMap() throws IOException {
			// Nothing to do
		}

	}
}
