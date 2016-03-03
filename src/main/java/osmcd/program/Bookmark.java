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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import osmb.mapsources.ACMapSource;
import osmb.mapsources.SiACMapSourcesManager;
import osmb.mapsources.MP2Corner;
import osmb.utilities.geo.GeoCoordinate;

//W #mapSpaceimport osmb.utilities.geo.EastNorthCoordinate;
//W #mapSpace EastNorthCoordinate <-> GeoCoordinate MP2Corner <-> MercatorPixelCoordinate
@XmlAccessorType(XmlAccessType.FIELD)
public class Bookmark extends GeoCoordinate// #mapSpace ??? EastNorthCoordinate
{
	@XmlAttribute
	protected int zoom;

	@XmlAttribute
	protected String name;

	@XmlAttribute
	protected String mapSource;

	/**
	 * Needed for JAXB
	 */
	@SuppressWarnings("unused")
	private Bookmark()
	{

	}

	public Bookmark(ACMapSource mapSource, int zoom, int pixelCoordinateX, int pixelCoordinateY)
	{
		this(null, mapSource, zoom, pixelCoordinateX, pixelCoordinateY);
	}

	public Bookmark(String name, ACMapSource mapSource, int zoom, int pixelCoordinateX, int pixelCoordinateY)
	{
		// W #mapSpace EastNorthCoordinate <-> GeoCoordinate MP2Corner <-> MercatorPixelCoordinate
		// super(mapSource.getMapSpace(), zoom, pixelCoordinateX, pixelCoordinateY);
		super(new MP2Corner(pixelCoordinateX, pixelCoordinateY, zoom));
		this.mapSource = mapSource.getName();
		this.zoom = zoom;
		this.name = name;
	}

	public ACMapSource getMapSource()
	{
		return SiACMapSourcesManager.getInstance().getSourceByName(mapSource);
	}

	public int getZoom()
	{
		return zoom;
	}

	public void setName(String name)
	{
		if (name != null && name.trim().length() == 0)
			name = null;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		if (name != null)
			return name;
		return String.format("%s at lat=%.3f lon=%.3f (zoom = %d)", mapSource, lat, lon, zoom);
	}

}
