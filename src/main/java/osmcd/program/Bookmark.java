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

<<<<<<< HEAD:src/main/java/osmcd/program/Bookmark.java
import osmb.mapsources.ACMapSourcesManager;
import osmb.mapsources.IfMapSource;
import osmb.utilities.geo.EastNorthCoordinate;
=======
import osmcb.mapsources.ACMapSourcesManager;
import osmcb.program.interfaces.IfMapSource;
import osmcb.program.model.EastNorthCoordinate;
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/program/model/Bookmark.java

@XmlAccessorType(XmlAccessType.FIELD)
public class Bookmark extends EastNorthCoordinate
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

<<<<<<< HEAD:src/main/java/osmcd/program/Bookmark.java
	public Bookmark(IfMapSource mapSource, int zoom, int pixelCoordinateX, int pixelCoordinateY)
	{
		this(null, mapSource, zoom, pixelCoordinateX, pixelCoordinateY);
	}

	public Bookmark(String name, IfMapSource mapSource, int zoom, int pixelCoordinateX, int pixelCoordinateY)
	{
=======
	public Bookmark(IfMapSource mapSource, int zoom, int pixelCoordinateX, int pixelCoordinateY) {
		this(null, mapSource, zoom, pixelCoordinateX, pixelCoordinateY);
	}

	public Bookmark(String name, IfMapSource mapSource, int zoom, int pixelCoordinateX, int pixelCoordinateY) {
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/program/model/Bookmark.java
		super(mapSource.getMapSpace(), zoom, pixelCoordinateX, pixelCoordinateY);
		this.mapSource = mapSource.getName();
		this.zoom = zoom;
		this.name = name;
	}

<<<<<<< HEAD:src/main/java/osmcd/program/Bookmark.java
	public IfMapSource getMapSource()
	{
=======
	public IfMapSource getMapSource() {
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/program/model/Bookmark.java
		return ACMapSourcesManager.getInstance().getSourceByName(mapSource);
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
