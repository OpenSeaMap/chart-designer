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
package osmcd.gui.mapview;

import java.awt.Graphics;

<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/MapGridLayer.java
import osmb.mapsources.IfMapSource;
=======
import osmcb.program.interfaces.IfMapSource;
import osmcd.gui.mapview.interfaces.MapTileLayer;

>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/layer/MapGridLayer.java

/**
 * A simple layer that paints the tile borders.
 */
public class MapGridLayer implements MapTileLayer
{
	protected int tileSize;

<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/MapGridLayer.java
	@Override
	public void startPainting(IfMapSource mapSource)
	{
=======
	public void startPainting(IfMapSource mapSource) {
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/layer/MapGridLayer.java
		tileSize = mapSource.getMapSpace().getTileSize();
	}

	@Override
	public void paintTile(Graphics g, int gx, int gy, int tilex, int tiley, int zoom)
	{
		g.drawRect(gx, gy, tileSize, tileSize);
	}
}
