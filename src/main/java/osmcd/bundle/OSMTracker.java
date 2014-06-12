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

import osmcd.ChartBundleName;
import osmcd.SupportedParameters;
import osmcd.mapsources.mapspace.MercatorPower2MapSpace;
import osmcd.program.interfaces.MapSource;
import osmcd.program.model.TileImageParameters.Name;

/**
 * Creates maps identical to the atlas format used by OSMTracker.
 * 
 * Please note that this atlas format ignores the defined atlas structure. It uses a separate directory for each used map source and inside one directory for
 * each zoom level.
 */
@ChartBundleName("OSMTracker tile storage")
@SupportedParameters(names = {Name.format})
public class OSMTracker extends ChartBundle
{
	protected String tileFileNamePattern = "%d/%d/%d.%s";
	protected File mapDir = null;
	protected String tileType = null;

	@Override
	public boolean testMapSource(MapSource mapSource)
	{
		return MercatorPower2MapSpace.INSTANCE_256.equals(mapSource.getMapSpace());
	}
}
