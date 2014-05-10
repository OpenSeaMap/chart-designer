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
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;

import osmcbdef.exceptions.AtlasTestException;
import osmcbdef.exceptions.MapCreationException;
import osmcbdef.program.annotations.AtlasCreatorName;
import osmcbdef.program.annotations.SupportedParameters;
import osmcbdef.program.atlascreators.tileprovider.ConvertedRawTileProvider;
import osmcbdef.program.atlascreators.tileprovider.TileProvider;
import osmcbdef.program.interfaces.AtlasInterface;
import osmcbdef.program.interfaces.MapInterface;
import osmcbdef.program.interfaces.MapSource;
import osmcbdef.program.interfaces.RequiresSQLite;
import osmcbdef.program.interfaces.MapSpace.ProjectionCategory;
import osmcbdef.program.model.TileImageParameters.Name;
import osmcbdef.utilities.jdbc.SQLiteLoader;

/**
 * http://sourceforge.net/p/osmcbdef/patches/22/
 */
@AtlasCreatorName("Osmdroid SQLite")
@SupportedParameters(names = { Name.format })
public class OsmdroidSQLite extends AtlasCreator implements RequiresSQLite {

	private static final int MAX_BATCH_SIZE = 1000;

	protected Connection conn = null;

	public OsmdroidSQLite() {
		super();
		SQLiteLoader.loadSQLiteOrShowError();
	}

	@Override
	public boolean testMapSource(MapSource mapSource) {
		return mapSource.getMapSpace().getProjectionCategory().equals(ProjectionCategory.SPHERE);
	}

	@Override
	public void startAtlasCreation(AtlasInterface atlas, File customAtlasDir) throws IOException, AtlasTestException,
			InterruptedException {
		super.startAtlasCreation(atlas, customAtlasDir);
		String databaseFile = new File(atlasDir, atlas.getName() + ".sqlite").getAbsolutePath();
		try {
			SQLiteLoader.loadSQLite();
		} catch (SQLException e) {
			throw new AtlasTestException(SQLiteLoader.getMsgSqliteMissing());
		}
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
			Statement stat = conn.createStatement();
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS tiles (key INTEGER PRIMARY KEY, provider TEXT, tile BLOB)");
			stat.close();
		} catch (SQLException e) {
			throw new IOException("Error creating SQL database \"" + databaseFile + "\": " + e.getMessage(), e);
		}
		log.debug("SQLite Database file: " + databaseFile);
	}

	@Override
	public void initializeMap(MapInterface map, TileProvider mapTileProvider) {
		super.initializeMap(map, mapTileProvider);
		if (parameters != null)
			mapDlTileProvider = new ConvertedRawTileProvider(mapDlTileProvider, parameters.getFormat());
	}

	@Override
	public void createMap() throws MapCreationException, InterruptedException {
		try {
			String provider = map.getMapSource().getName();
			int maxMapProgress = 2 * (xMax - xMin + 1) * (yMax - yMin + 1);
			atlasProgress.initMapCreation(maxMapProgress);
			conn.setAutoCommit(false);
			int batchTileCount = 0;

			ImageIO.setUseCache(false);
			PreparedStatement prep = conn.prepareStatement("INSERT or REPLACE INTO tiles VALUES (?, ?, ?);");
			Runtime r = Runtime.getRuntime();
			long heapMaxSize = r.maxMemory();

			for (long x = xMin; x <= xMax; x++)
				for (long y = yMin; y <= yMax; y++) {
					checkUserAbort();
					atlasProgress.incMapCreationProgress();
					byte[] sourceTileData = mapDlTileProvider.getTileData((int) x, (int) y);
					if (sourceTileData != null) {
						long z = zoom;
						long index = (((z << z) + x) << z) + y;
						prep.setLong(1, index);
						prep.setString(2, provider);
						prep.setBytes(3, sourceTileData);
						prep.addBatch();

						long heapAvailable = heapMaxSize - r.totalMemory() + r.freeMemory();

						if ((heapAvailable < HEAP_MIN) || (batchTileCount >= MAX_BATCH_SIZE)) {
							log.trace("Executing batch containing " + batchTileCount + " tiles");
							prep.executeBatch();
							prep.clearBatch();
							System.gc();
							conn.commit();
							atlasProgress.incMapCreationProgress(batchTileCount);
							batchTileCount = 0;
						}

					}
				}
			prep.executeBatch();
			conn.setAutoCommit(true);
			atlasProgress.setMapCreationProgress(maxMapProgress);
		} catch (SQLException e) {
			throw new MapCreationException("Error writing tile image: " + e.getMessage(), map, e);
		} catch (IOException e) {
			throw new MapCreationException("Error writing tile image: " + e.getMessage(), map, e);
		}
	}

	@Override
	public void abortAtlasCreation() throws IOException {
		SQLiteLoader.closeConnection(conn);
		conn = null;
		super.abortAtlasCreation();
	}

	@Override
	public void finishAtlasCreation() throws IOException, InterruptedException {
		SQLiteLoader.closeConnection(conn);
		conn = null;
		super.finishAtlasCreation();
	}

}
