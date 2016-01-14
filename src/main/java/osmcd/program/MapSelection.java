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
package osmcd.program;

import java.awt.Point;

import org.apache.log4j.Logger;

import osmb.mapsources.ACMultiLayerMapSource;
import osmb.mapsources.IfMapSource;
import osmb.mapsources.MP2MapSpace;
import osmb.mapsources.MP2Pixel;
import osmb.program.map.IfMap;
import osmb.utilities.geo.GeoCoordinate;

public class MapSelection
{
	private static final Logger log = Logger.getLogger(MapSelection.class);

	// W #selCoord Only used in public JCoordinatesPanel(): new JCoordinateField(MapSelection.LAT_MIN, MapSelection.LAT_MAX) [JCoordinateField(double min, double
	// max)]
	public static final double LAT_MAX = MP2MapSpace.MAX_LAT;
	public static final double LAT_MIN = MP2MapSpace.MIN_LAT;
	public static final double LON_MAX = 180.0;
	public static final double LON_MIN = -180.0;

	private final IfMapSource mapSource;
	private final int mapSourceTileSize;
	private final int zoom;
	private int minPixelCoordinate_x;
	private int minPixelCoordinate_y;
	private int maxPixelCoordinate_x;
	private int maxPixelCoordinate_y;

	public MapSelection(IfMapSource mapSource, GeoCoordinate max, GeoCoordinate min)
	{
		super();
		this.mapSource = mapSource;
		mapSourceTileSize = MP2MapSpace.getTileSize();
		zoom = Math.min(mapSource.getMaxZoom(), MP2MapSpace.MAX_TECH_ZOOM); // MP2MapSpace.MAX_TECH_ZOOM;
		int x1 = MP2MapSpace.cLonToX(min.lon, zoom); // W #mapSpace mapSpace.cLonToX(min.lon, zoom);
		int x2 = MP2MapSpace.cLonToX(max.lon, zoom); // W #mapSpace mapSpace.cLonToX(max.lon, zoom);
		int y1 = MP2MapSpace.cLatToY(min.lat, zoom); // W #mapSpace mapSpace.cLatToY(min.lat, zoom);
		int y2 = MP2MapSpace.cLatToY(max.lat, zoom); // W #mapSpace mapSpace.cLatToY(max.lat, zoom);
		setCoordinates(x1, x2, y1, y2);
		// log.debug("x1=" + x1 + ", x2=" + x2 + ", y1=" + y1 + ", y2=" + y2);
	}

	public MapSelection(IfMap map)
	{
		this(map.getMapSource(), map.getMaxPixelCoordinate(), map.getMinPixelCoordinate(), map.getZoom());
	}

	/**
	 * @param mapSource
	 * @param p1
	 *          pixel coordinate
	 * @param p2
	 *          pixel coordinate
	 * @param zoom
	 */
	public MapSelection(IfMapSource mapSource, Point p1, Point p2, int zoom)
	{
		super();
		this.mapSource = mapSource;
		mapSourceTileSize = MP2MapSpace.getTileSize();
		this.zoom = zoom;
		setCoordinates(p1.x, p2.x, p1.y, p2.y);
	}

	// W #mapSpace MP2Pixel
	public MapSelection(IfMapSource mapSource, MP2Pixel c1, MP2Pixel c2)
	{
		if (c1.getZoom() != c2.getZoom())
			throw new RuntimeException("Different zoom levels - unsuported!");
		this.mapSource = mapSource;
		mapSourceTileSize = MP2MapSpace.getTileSize();
		this.zoom = c1.getZoom();
		setCoordinates(c1.getX(), c2.getX(), c1.getY(), c2.getY());
	}

	protected void setCoordinates(int x1, int x2, int y1, int y2)
	{
		maxPixelCoordinate_x = Math.max(x1, x2);
		minPixelCoordinate_x = Math.min(x1, x2);
		maxPixelCoordinate_y = Math.max(y1, y2);
		minPixelCoordinate_y = Math.min(y1, y2);
	}

	/**
	 * Is an area selected or only one point?
	 * 
	 * @return
	 */
	public boolean isAreaSelected()
	{
		boolean result = maxPixelCoordinate_x != minPixelCoordinate_x && maxPixelCoordinate_y != minPixelCoordinate_y;
		return result;
	}

	/**
	 * Warning: maximum lat/lon is the top left corner of the top right pixel of the map selection! // W Warning: maximum lat/lon is the top right corner of the
	 * map selection!
	 * 
	 * @return maximum lat/lon
	 */
	public GeoCoordinate getMax()
	{
		return new MP2Pixel(maxPixelCoordinate_x, minPixelCoordinate_y, zoom).toGeoUpperLeftCorner();
	}

	/**
	 * Warning: minimum lat/lon is the top left corner of the bottom left pixel of the map selection! // W Warning: minimum lat/lon is the bottom left corner of
	 * the map selection!
	 * 
	 * @return minimum lat/lon
	 */
	public GeoCoordinate getMin()
	{
		return new MP2Pixel(minPixelCoordinate_x, maxPixelCoordinate_y, zoom).toGeoUpperLeftCorner();
	}

	/**
	 * Returns the top left tile x- and y-tile-number (minimum) of the selected area marked by the {@link MapSelection}.
	 * 
	 * @param aZoomLevel
	 * @return tile number [0..2<sup>zoom</sup>]
	 */
	public Point getTopLeftTileNumber(int aZoomlevel)
	{
		Point tlc = getTopLeftPixelCoordinate(aZoomlevel);
		tlc.x /= mapSourceTileSize;
		tlc.y /= mapSourceTileSize;
		return tlc;
	}

	// W #mapSpace MP2Pixel
	public MP2Pixel getTopLeftPixelCoordinate()
	{
		return new MP2Pixel(minPixelCoordinate_x, minPixelCoordinate_y, zoom);
	}

	/**
	 * Returns the top left pixel x- and pixel y-coordinate (minimum) of the selected area marked by the {@link MapSelection}.
	 * 
	 * @param aZoomlevel
	 * @return Pixel coordinate [0..(256 * 2<sup>zoom</sup>)]
	 */
	public Point getTopLeftPixelCoordinate(int aZoomlevel)
	{
		int zoomDiff = this.zoom - aZoomlevel;
		int x = minPixelCoordinate_x;
		int y = minPixelCoordinate_y;
		if (zoomDiff < 0)
		{
			zoomDiff = -zoomDiff;
			x <<= zoomDiff;
			y <<= zoomDiff;
		}
		else
		{
			x >>= zoomDiff;
			y >>= zoomDiff;
		}
		return new Point(x, y);
	}

	/**
	 * Returns the bottom right tile x- and y-tile-number (minimum) of the selected area marked by the {@link MapSelection}.
	 * 
	 * @param aZoomlevel
	 * @return tile number [0..2<sup>zoom</sup>]
	 */
	public Point getBottomRightTileNumber(int aZoomlevel)
	{
		Point brc = getBottomRightPixelCoordinate(aZoomlevel);
		brc.x = brc.x / mapSourceTileSize;
		brc.y = brc.y / mapSourceTileSize;
		return brc;
	}

	/**
	 * Returns the bottom right tile x- and y-tile-coordinate (minimum) of the selected area regarding the zoom level specified at creation time of this
	 * {@link MapSelection} instance.
	 * 
	 * @return tile coordinate [0..(256 * 2<sup>zoom</sup>)]
	 */
	public MP2Pixel getBottomRightPixelCoordinate()
	{
		return new MP2Pixel(maxPixelCoordinate_x, maxPixelCoordinate_y, zoom);
	}

	/**
	 * Returns the bottom right tile x- and y-tile-coordinate (minimum) of the selected area marked by the {@link MapSelection}.
	 * 
	 * @param aZoomlevel
	 * @return tile coordinate [0..(256 * 2<sup>zoom</sup>)]
	 */
	public Point getBottomRightPixelCoordinate(int aZoomlevel)
	{
		int zoomDiff = this.zoom - aZoomlevel;
		int x = maxPixelCoordinate_x;
		int y = maxPixelCoordinate_y;
		if (zoomDiff < 0)
		{
			zoomDiff = -zoomDiff;
			x <<= zoomDiff;
			y <<= zoomDiff;
		}
		else
		{
			x >>= zoomDiff; // W !!! 127->63->31->15->7->3->1->0
			y >>= zoomDiff;
		}
		return new Point(x, y);
	}

	/**
	 * Return the amount of tiles for the current selection in the specified zoom level.
	 * 
	 * @param zoom
	 *          is the zoom level to calculate the amount of tiles for
	 * @return the amount of tiles in the current selection in the supplied zoom level
	 */
	public long calculateNrOfTiles(int zoom)
	{
		Point max = getBottomRightTileNumber(zoom);
		Point min = getTopLeftTileNumber(zoom);
		long width = max.x - min.x + 1;
		long height = max.y - min.y + 1;
		long tileCount = width * height;
		if (mapSource instanceof ACMultiLayerMapSource)
		{
			int mapLayerCount = ((ACMultiLayerMapSource) mapSource).getLayerMapSources().length;
			tileCount *= mapLayerCount;
		}
		return tileCount;
	}

	public long[] calculateNrOfTilesEx(int zoom)
	{
		Point max = getBottomRightTileNumber(zoom);
		Point min = getTopLeftTileNumber(zoom);
		long width = max.x - min.x + 1;
		long height = max.y - min.y + 1;
		long tileCount = width * height;
		if (mapSource instanceof ACMultiLayerMapSource)
		{
			int mapLayerCount = ((ACMultiLayerMapSource) mapSource).getLayerMapSources().length;
			tileCount *= mapLayerCount;
		}
		return new long[]
		{ tileCount, width, height };
	}

	@Override
	public String toString() // used in PreviewMap#zoomTo(MapSelection ms)
	{
		GeoCoordinate max = getMax();
		GeoCoordinate min = getMin();
		return String.format("lat/lon: max(%6f/%6f) min(%6f/%6f)", new Object[]
		{ max.lat, max.lon, min.lat, min.lon });
	}
}
