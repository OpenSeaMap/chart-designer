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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.EnumSet;

import org.apache.log4j.Logger;

import osmcd.exceptions.BundleTestException;
import osmcd.program.interfaces.BundleInterface;
import osmcd.program.interfaces.LayerInterface;
import osmcd.program.interfaces.MapInterface;
import osmcd.program.interfaces.MapSource;
import osmcd.program.model.TileImageFormat;
import osmcd.program.model.TileImageParameters;
import osmcd.utilities.Charsets;

public abstract class ChartBundle
{
	/**
	 * Abstract base class for all ChartBundle implementations. The ChartBundle is the description of a ChartBundle, containing name and info about format and
	 * structure. The bundle creation is done by BundleCreator
	 * 
	 */
	public static final Charset TEXT_FILE_CHARSET = Charsets.ISO_8859_1;
	protected final Logger log;

	/**
	 * atlas specific fields
	 */
	protected BundleInterface atlas;
	protected File atlasDir;

	/**
	 * map specific fields
	 */
	protected MapInterface map;
	protected int xMin;
	protected int xMax;
	protected int yMin;
	protected int yMax;
	protected int zoom;
	protected MapSource mapSource;
	protected int tileSize;

	/**
	 * Custom tile processing parameters. <code>null</code> if disabled in GUI
	 */
	protected TileImageParameters parameters;

	// protected BundleOutputFormat atlasOutputFormat;
	// protected TileProvider mapDlTileProvider;

	/**
	 * Default constructor - initializes the logging environment
	 */
	protected ChartBundle() {
		log = Logger.getLogger(this.getClass());
	};

	protected void testBundle() throws BundleTestException
	{
	}

	public void initLayerCreation(LayerInterface layer) throws IOException
	{
	}

	public void finishLayerCreation() throws IOException
	{
	}

	/**
	 * Test if the {@link ChartBundle} instance supports the selected {@link MapSource}
	 * 
	 * @param mapSource
	 * @return <code>true</code> if supported otherwise <code>false</code>
	 * @see BundleCreator
	 */
	public abstract boolean testMapSource(MapSource mapSource);

	public int getXMin()
	{
		return xMin;
	}

	public int getXMax()
	{
		return xMax;
	}

	public int getYMin()
	{
		return yMin;
	}

	public int getYMax()
	{
		return yMax;
	}

	public MapInterface getMap()
	{
		return map;
	}

	public TileImageParameters getParameters()
	{
		return parameters;
	}

	/**
	 * Tests all maps of the currently active bundle if a custom tile image format has been specified and if the specified format is equal to the
	 * <code>allowedFormat</code>.
	 * 
	 * @param allowedFormat
	 * @throws AtlasTestException
	 */
	protected void performTest_BundleTileFormat(EnumSet<TileImageFormat> allowedFormats) throws BundleTestException
	{
		for (LayerInterface layer: atlas)
		{
			for (MapInterface map: layer)
			{
				TileImageParameters parameters = map.getParameters();
				if (parameters == null)
					continue;
				if (!allowedFormats.contains(parameters.getFormat()))
					throw new BundleTestException("Selected custom tile format not supported - only the following format(s) are supported: " + allowedFormats, map);
			}
		}
	}

	protected void performTest_MaxMapZoom(int maxZoom) throws BundleTestException
	{
		for (LayerInterface layer: atlas)
		{
			for (MapInterface map: layer)
			{
				if (map.getZoom() > maxZoom)
					throw new BundleTestException("Maximum zoom is " + maxZoom + " for this atlas format", map);
			}
		}
	}

	public void abortBundleCreation()
	{
		// TODO Auto-generated method stub

	}
}
