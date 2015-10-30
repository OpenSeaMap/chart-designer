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

/**
 * General purpose iMap layer
 */
public interface IfMapLayer
{
	/**
	 * 
	 * @param iMap
	 * @param g
	 * @param zoom
	 *          current zoom level
	 * @param minX
	 *          top left x coordinate of the visible iMap region
	 * @param minY
	 *          top left y coordinate of the visible iMap region
	 * @param maxX
	 *          bottom right x coordinate of the visible iMap region
	 * @param maxY
	 *          bottom right y coordinate of the visible iMap region
	 */
	public void paint(JMapViewer map, Graphics2D g, int zoom, int minX, int minY, int maxX, int maxY);
}