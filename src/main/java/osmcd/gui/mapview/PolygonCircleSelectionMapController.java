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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/PolygonCircleSelectionMapController.java
=======
import osmcd.gui.mapview.PreviewMap;
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/controller/PolygonCircleSelectionMapController.java

/**
 * Implements the GUI logic for the preview iMap panel that manages the iMap selection and actions triggered by key
 * strokes.
 * 
 */
public class PolygonCircleSelectionMapController extends AbstractPolygonSelectionMapController implements MouseMotionListener, MouseListener
{

	private static final int POLYGON_POINTS = 16;
	private static final double ANGLE_PART = Math.PI * 2.0 / POLYGON_POINTS;

	private Point center;

	public PolygonCircleSelectionMapController(PreviewMap map)
	{
		super(map);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			center = convertToAbsolutePoint(e.getPoint());
			polygonPoints.ensureCapacity(POLYGON_POINTS);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK)
		{
			if (center != null)
			{
				Point circlePoint = convertToAbsolutePoint(e.getPoint());
				double radius = circlePoint.distance(center);
				polygonPoints.clear();
				for (int i = 0; i < POLYGON_POINTS; i++)
				{
					double angle = ANGLE_PART * i;
					int y = (int) Math.round(Math.sin(angle) * radius);
					int x = (int) Math.round(Math.cos(angle) * radius);
					polygonPoints.add(new Point(center.x + x, center.y + y));
				}
				map.grabFocus();
				map.repaint();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{}

	@Override
	public void mouseReleased(MouseEvent e)
	{}

	@Override
	public void mouseEntered(MouseEvent e)
	{}

	@Override
	public void mouseExited(MouseEvent e)
	{}

}
