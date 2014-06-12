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

import java.awt.image.BufferedImage;

import osmcd.exceptions.BundleTestException;
import osmcd.mapsources.mapspace.MercatorPower2MapSpace;
import osmcd.program.interfaces.LayerInterface;
import osmcd.program.interfaces.MapInterface;
import osmcd.program.interfaces.MapSource;
import osmcd.program.interfaces.MapSpace;
import osmcd.program.interfaces.MapSpace.ProjectionCategory;

public abstract class AbstractPlainImage extends ChartBundle
{
	@Override
	public boolean testMapSource(MapSource mapSource)
	{
		MapSpace mapSpace = mapSource.getMapSpace();
		return (mapSpace instanceof MercatorPower2MapSpace && ProjectionCategory.SPHERE.equals(mapSpace.getProjectionCategory()));
	}

	@Override
	protected void testBundle() throws BundleTestException
	{
		Runtime r = Runtime.getRuntime();
		long heapMaxSize = r.maxMemory();
		int maxMapSize = (int) (Math.sqrt(heapMaxSize / 3d) * 0.8); // reduce maximum by 20%
		maxMapSize = (maxMapSize / 100) * 100; // round by 100;
		for (LayerInterface layer: atlas)
		{
			for (MapInterface map: layer)
			{
				int w = map.getMaxTileCoordinate().x - map.getMinTileCoordinate().x;
				int h = map.getMaxTileCoordinate().y - map.getMinTileCoordinate().y;
				if (w > maxMapSize || h > maxMapSize)
					throw new BundleTestException("Map size too large for memory (is: " + Math.max(w, h) + " max:  " + maxMapSize + ")", map);
			}
		}
	}

	/**
	 * @return maximum image height and width. In case an image is larger it will be scaled to fit.
	 */
	protected int getMaxImageSize()
	{
		return Integer.MAX_VALUE;
	}

	protected int getBufferedImageType()
	{
		return BufferedImage.TYPE_4BYTE_ABGR;
	}
}
