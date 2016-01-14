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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;

import javax.swing.JComponent;

import osmb.mapsources.MP2MapSpace;
//W #mapSpace import osmb.program.map.IfMapSpace;
import osmb.utilities.MyMath;
import osmb.utilities.UnitSystem;
import osmcd.OSMCDSettings;

/**
 * Simple scale bar showing the map scale using the selected unit system.
 */
public class ScaleBar
{
	private static final Stroke STROKE = new BasicStroke(1);
	private static final Font FONT = new Font("Sans Serif", Font.PLAIN, 12);

	/**
	 * Horizontal margin between scale bar and right border of the map
	 */
	private static final int MARGIN_X = 0; //40;

//	/**
//	 * Vertical margin between scale bar and bottom border of the iMap
//	 */
//	private static final int MARGIN_Y = 40;////////////////////

	private static final int SCALE_BAR_HIGHT = 19;
	private static final int DESIRED_SCALE_BAR_WIDTH = 150;

	public static void paintScaleBar(JComponent c, Graphics2D g, Point tlc, int zoom) // W #mapSpace (JComponent c, Graphics2D g, IfMapSpace mapSpace, Point tlc, int zoom)
	{
		Rectangle r = c.getBounds();
		int posX;
		int posY = r.height / 2 - r.y;
		//posY -= MARGIN_Y;
		posX = MARGIN_X;

		// int coordX = tlc.x + posX;
		int coordY = tlc.y + posY;

		int w1 = DESIRED_SCALE_BAR_WIDTH;

		UnitSystem unitSystem = OSMCDSettings.getInstance().getUnitSystem();

		// Calculate the angular distance of our desired scale bar
		double ad = MP2MapSpace.horizontalDistance(zoom, coordY, w1); // W #mapSpace mapSpace.horizontalDistance(zoom, coordY, w1);

		String unit = unitSystem.unitLarge;
		// convert angular into the selected unit system
		double dist1 = ad * unitSystem.earthRadius;
		// distance is smaller that one (km/mi)? the use smaller units (m/ft)
		if (dist1 < 1.0)
		{
			dist1 *= unitSystem.unitFactor;
			unit = unitSystem.unitSmall;
		}
		// Round everything to a nice value
		double dist2 = MyMath.prettyRound(dist1);
		double factor = dist2 / dist1;
		// apply the round factor to the width of our scale bar
		int w2 = (int) (w1 * factor);

		g.setStroke(STROKE);
		// g.setColor(Color.YELLOW);
		g.setColor(new Color(1.0f, 1.0f, 0.0f, 0.5f));
		g.drawLine(posX + w2 + 1, posY, r.width - posX, posY);
		g.fillRect(posX, posY - SCALE_BAR_HIGHT / 2, w2, SCALE_BAR_HIGHT);
		
		g.setColor(Color.BLACK);
		g.drawRect(posX, posY - SCALE_BAR_HIGHT / 2, w2, SCALE_BAR_HIGHT);
		String value = Integer.toString((int) dist2) + " " + unit;
		g.setFont(FONT);
		g.drawString(value, posX + 10, posY + 5);
	}
}
