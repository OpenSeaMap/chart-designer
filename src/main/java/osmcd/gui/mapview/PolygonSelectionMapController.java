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
<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/PolygonSelectionMapController.java
=======
import osmcd.gui.mapview.PreviewMap;
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/controller/PolygonSelectionMapController.java

/**
 * Implements the GUI logic for the preview iMap panel that manages the iMap selection and actions triggered by key
 * strokes.
 * 
 */
public class PolygonSelectionMapController extends AbstractPolygonSelectionMapController implements MouseListener
{

	public PolygonSelectionMapController(PreviewMap map)
	{
		super(map);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{}

	@Override
	public void mousePressed(MouseEvent e)
	{

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			if (finished) reset();
			Point mapPoint = map.getTopLeftCoordinate();
			mapPoint.x += e.getX();
			mapPoint.y += e.getY();
			mapPoint = map.getMapSource().getMapSpace().changeZoom(mapPoint, map.getZoom(), PreviewMap.MAX_ZOOM);
			polygonPoints.add(mapPoint);
		}
		map.grabFocus();
		map.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{}

	@Override
	public void mouseExited(MouseEvent e)
	{}

}
