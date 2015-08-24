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

import java.awt.Graphics2D;
<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/MapLayer.java
=======
import osmcd.gui.mapview.JMapViewer;
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/interfaces/MapLayer.java

/**
 * General purpose iMap layer
 */
public interface MapLayer
{
	/**
	 * 
	 * @param iMap
	 * @param g
	 * @param zoom
	 *          current zoom level
	 * @param minX
<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/MapLayer.java
	 *            top left x coordinate of the visible iMap region
	 * @param minYtop
	 *            left y coordinate of the visible iMap region
	 * @param maxX
	 *            bottom right x coordinate of the visible iMap region
	 * @param maxY
	 *            bottom right y coordinate of the visible iMap region
=======
	 *          top left x coordinate of the visible map region
	 * @param minYtop
	 *          left y coordinate of the visible map region
	 * @param maxX
	 *          bottom right x coordinate of the visible map region
	 * @param maxY
	 *          bottom right y coordinate of the visible map region
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/interfaces/MapLayer.java
	 */
	public void paint(JMapViewer map, Graphics2D g, int zoom, int minX, int minY, int maxX, int maxY);

}
