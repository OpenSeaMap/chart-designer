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

import osmb.mapsources.IfMapSource;

public interface IfMapTileLayer
{
	public void startPainting(IfMapSource mapSource);

	/**
	 * Paints the tile identified by <code>tilex</code>/<code>tiley</code>/ <code>zoom</code> onto the {@link Graphics} <code>g</code> with it's
	 * upper left corner at <code>gx</code>/<code>gy</code>. The size of each
	 * tile has to be 256 pixel x 256 pixel.
	 * 
	 * @param g
	 * @param gx
	 * @param gy
	 * @param tilex
	 * @param tiley
	 */
	public void paintTile(Graphics g, int gx, int gy, int tilex, int tiley, int zoom);
}
