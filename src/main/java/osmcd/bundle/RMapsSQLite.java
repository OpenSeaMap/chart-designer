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

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

import osmcd.ChartBundleName;
import osmcd.SupportedParameters;
import osmcd.program.interfaces.MapSource;
import osmcd.program.interfaces.MapSpace;
import osmcd.program.interfaces.MapSpace.ProjectionCategory;
import osmcd.program.interfaces.RequiresSQLite;
import osmcd.program.model.TileImageParameters.Name;
import osmcd.utilities.jdbc.SQLiteLoader;

/**
 * Bundle/Map creator for "BigPlanet-Maps application for Android" (offline SQLite maps) http://code.google.com/p/bigplanet/
 * <p>
 * Some source parts are taken from the "android-map.blogspot.com Version of Mobile Bundle Creator": http://code.google.com/p/android-map/
 * </p>
 * <p>
 * Additionally the created BigPlanet SQLite database has one additional table containing special info needed by the Android application <a
 * href="http://robertdeveloper.blogspot.com/search/label/rmaps.release" >RMaps</a>.<br>
 * (Database statements: {@link #RMAPS_TABLE_INFO_DDL} and {@link #RMAPS_UPDATE_INFO_SQL} ).<br>
 * Changes made by <a href="mailto:robertk506@gmail.com">Robert</a>, author of RMaps.
 * <p>
 */
@ChartBundleName(value = "RMaps SQLite", type = "RMaps")
@SupportedParameters(names = {Name.format})
public class RMapsSQLite extends ChartBundle implements RequiresSQLite
{
	private static final int MAX_BATCH_SIZE = 1000;
	private static final String TABLE_DDL = "CREATE TABLE IF NOT EXISTS tiles (x int, y int, z int, s int, image blob, PRIMARY KEY (x,y,z,s))";
	private static final String INDEX_DDL = "CREATE INDEX IF NOT EXISTS IND on tiles (x,y,z,s)";
	private static final String INSERT_SQL = "INSERT or REPLACE INTO tiles (x,y,z,s,image) VALUES (?,?,?,0,?)";
	private static final String RMAPS_TABLE_INFO_DDL = "CREATE TABLE IF NOT EXISTS info AS SELECT 99 AS minzoom, 0 AS maxzoom";
	private static final String RMAPS_CLEAR_INFO_SQL = "DELETE FROM info;";
	private static final String RMAPS_UPDATE_INFO_MINMAX_SQL = "INSERT INTO info (minzoom,maxzoom) VALUES (?,?);";
	private static final String RMAPS_INFO_MAX_SQL = "SELECT DISTINCT z FROM tiles ORDER BY z DESC LIMIT 1;";
	private static final String RMAPS_INFO_MIN_SQL = "SELECT DISTINCT z FROM tiles ORDER BY z ASC LIMIT 1;";
	protected File databaseFile;
	protected Connection conn = null;
	protected PreparedStatement prepStmt;

	public RMapsSQLite() {
		super();
		SQLiteLoader.loadSQLiteOrShowError();
	}

	@Override
	public boolean testMapSource(MapSource mapSource)
	{
		MapSpace mapSpace = mapSource.getMapSpace();
		boolean correctTileSize = (256 == mapSpace.getTileSize());
		ProjectionCategory pc = mapSpace.getProjectionCategory();
		boolean correctProjection = (ProjectionCategory.SPHERE.equals(pc) || ProjectionCategory.ELLIPSOID.equals(pc));
		return correctTileSize && correctProjection;
	}

	protected String getDatabaseFileName()
	{
		return atlas.getName() + ".sqlitedb";
	}
}
