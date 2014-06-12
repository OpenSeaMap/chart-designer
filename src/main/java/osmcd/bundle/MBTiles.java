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

import java.util.EnumSet;

import osmcd.ChartBundleName;
import osmcd.exceptions.BundleTestException;
import osmcd.mapsources.mapspace.MercatorPower2MapSpace;
import osmcd.program.interfaces.LayerInterface;
import osmcd.program.interfaces.MapInterface;
import osmcd.program.interfaces.MapSource;
import osmcd.program.model.TileImageParameters;
import osmcd.program.model.TileImageType;

/**
 * https://github.com/mapbox/mbtiles-spec/tree/master/1.1
 */
@ChartBundleName(value = "MBTiles SQLite")
public class MBTiles extends RMapsSQLite
{

	private static final String INSERT_SQL = "INSERT or REPLACE INTO tiles (tile_column,tile_row,zoom_level,tile_data) VALUES (?,?,?,?)";
	private static final String TABLE_TILES = "CREATE TABLE IF NOT EXISTS tiles (zoom_level integer, tile_column integer, tile_row integer, tile_data blob);";
	private static final String INDEX_TILES = "CREATE INDEX IF NOT EXISTS tiles_idx on tiles (zoom_level, tile_column, tile_row)";
	private static final String TABLE_METADATA = "CREATE TABLE IF NOT EXISTS metadata (name text, value text);";
	private static final String INSERT_METADATA = "INSERT INTO metadata (name,value) VALUES (?,?);";
	private static final String INDEX_METADATA = "CREATE UNIQUE INDEX IF NOT EXISTS metadata_idx  ON metadata (name);";

	private double boundsLatMin;
	private double boundsLatMax;
	private double boundsLonMin;
	private double boundsLonMax;

	private TileImageType atlasTileImageType;

	@Override
	public boolean testMapSource(MapSource mapSource)
	{
		return MercatorPower2MapSpace.INSTANCE_256.equals(mapSource.getMapSpace());
	}

	@Override
	protected void testBundle() throws BundleTestException
	{
		EnumSet<TileImageType> allowed = EnumSet.of(TileImageType.JPG, TileImageType.PNG);
		// Test of output format - only jpg xor png is allowed
		TileImageType tit = null;
		for (LayerInterface layer: atlas)
		{
			for (MapInterface map: layer)
			{
				TileImageParameters parameters = map.getParameters();
				TileImageType currentTit;
				if (parameters == null)
				{
					currentTit = map.getMapSource().getTileImageType();
					if (!allowed.contains(currentTit))
						throw new BundleTestException("Map source format incompatible - tile format conversion to PNG or JPG is required for this map.", map);
				}
				else
				{
					currentTit = parameters.getFormat().getType();
					if (!allowed.contains(currentTit))
						throw new BundleTestException("Selected custom tile format not supported - only JPG and PNG formats are supported.", map);
				}
				if (tit != null && !currentTit.equals(tit))
				{
					throw new BundleTestException("All maps within one atlas must use the same format (PNG or JPG). "
							+ "Use tile format conversion converting maps with a different format.", map);
				}
				tit = currentTit;
			}
		}
		atlasTileImageType = tit;
	}

	protected String getDatabaseFileName()
	{
		return atlas.getName() + ".mbtiles";
	}

}
