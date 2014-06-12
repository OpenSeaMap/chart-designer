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
import osmcd.program.interfaces.MapSpace;
import osmcd.program.interfaces.MapSpace.ProjectionCategory;
import osmcd.program.model.TileImageParameters.Name;

@ChartBundleName(value = "TrekBuddy untared atlas", type = "UntaredAtlas")
@SupportedParameters(names = {Name.format, Name.height, Name.width})
public class TrekBuddy extends ChartBundle
{
	protected static final String FILENAME_PATTERN = "t_%d_%d.%s";
	protected File layerDir = null;
	protected File mapDir = null;

	@Override
	public boolean testMapSource(MapSource mapSource)
	{
		MapSpace mapSpace = mapSource.getMapSpace();
		return (mapSpace instanceof MercatorPower2MapSpace && ProjectionCategory.SPHERE.equals(mapSpace.getProjectionCategory()));
		// TODO supports Mercator ellipsoid?
	}

	@Override
	public void abortBundleCreation()
	{
		// TODO Auto-generated method stub

	}
}
