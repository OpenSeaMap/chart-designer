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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/RectangleSelectionLayer.java

import osmb.program.map.IfMapSpace;
=======
import osmcb.program.interfaces.IfMapSpace;
import osmcd.gui.mapview.JMapViewer;
import osmcd.gui.mapview.PreviewMap;
import osmcd.gui.mapview.controller.RectangleSelectionMapController;
import osmcd.gui.mapview.interfaces.MapLayer;
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/layer/RectangleSelectionLayer.java

/**
 * Displays a polygon on the iMap - only for testing purposes
 */
public class RectangleSelectionLayer implements MapLayer
{
<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/RectangleSelectionLayer.java

=======
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/layer/RectangleSelectionLayer.java
	private final RectangleSelectionMapController mapController;

	public RectangleSelectionLayer(RectangleSelectionMapController rectangleSelectionMapController)
	{
		this.mapController = rectangleSelectionMapController;
	}

<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/RectangleSelectionLayer.java
=======
	@Override
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/layer/RectangleSelectionLayer.java
	public void paint(JMapViewer map, Graphics2D g, int zoom, int minX, int minY, int maxX, int maxY)
	{
		IfMapSpace mapSpace = map.getMapSource().getMapSpace();
		g.setColor(Color.BLUE);
		Point p1 = mapController.getiStartSelectionPoint();
		Point p2 = mapController.getiEndSelectionPoint();
		if (p1 == null || p2 == null) return;
		p1 = mapSpace.changeZoom(p1, PreviewMap.MAX_ZOOM, zoom);
		p2 = mapSpace.changeZoom(p2, PreviewMap.MAX_ZOOM, zoom);

		int x = Math.min(p1.x, p2.x);
		int y = Math.min(p1.y, p2.y);
		int w = Math.abs(p1.x - p2.x);
		int h = Math.abs(p1.y - p2.y);

		AffineTransform at = g.getTransform();
		try
		{
			g.translate(-minX, -minY);
			g.drawRect(x, y, w, h);
		}
		finally
		{
			g.setTransform(at);
		}
	}
}
