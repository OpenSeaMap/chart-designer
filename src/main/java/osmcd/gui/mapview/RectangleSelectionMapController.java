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

/**
 * Implements the GUI logic for the preview iMap panel that manages the iMap selection and actions triggered by key
 * strokes.
 * 
 */
public class RectangleSelectionMapController extends ACMapController implements MouseMotionListener, MouseListener
{
	/**
	 * start point of selection rectangle in absolute tile coordinated regarding {@link JMapViewer#MAX_ZOOM}
	 */
	private Point iStartSelectionPoint;

	/**
	 * end point of selection rectangle in absolute tile coordinated regarding {@link JMapViewer#MAX_ZOOM}
	 */
	private Point iEndSelectionPoint;

	protected RectangleSelectionLayer mapLayer;

	public RectangleSelectionMapController(PreviewMap map)
	{
		super(map, false);
		mapLayer = new RectangleSelectionLayer(this);
	}

	@Override
	public void enable()
	{
		super.enable();
		// iMap.mapLayers.add(mapLayer);
	}

	@Override
	public void disable()
	{
		mMap.mapLayers.remove(mapLayer);
		mMap.setSelectionByPixelCoordinate(null, null, true);
		super.disable();
	}

	/**
	 * Start drawing the selection rectangle if it was the 1st button (left button)
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			iStartSelectionPoint = convertToAbsolutePoint(e.getPoint());
			iEndSelectionPoint = convertToAbsolutePoint(e.getPoint());
		}
		mMap.grabFocus();
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK)
		{
			if (iStartSelectionPoint != null)
			{
				iEndSelectionPoint = convertToAbsolutePoint(e.getPoint());
				mMap.setSelectionByPixelCoordinate(mMap.getMaxZoom(), iStartSelectionPoint, iEndSelectionPoint, true);
			}
		}
	}

	/**
	 * When dragging the iMap change the cursor back to it's pre-move cursor. If a double-click occurs center and zoom
	 * the iMap on the clicked location.
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			if (e.getClickCount() == 1)
			{
				mMap.setSelectionByPixelCoordinate(mMap.getMaxZoom(), iStartSelectionPoint, convertToAbsolutePoint(e.getPoint()), true);
			}
		}
		mMap.grabFocus();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		mMap.grabFocus();
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	public Point getiStartSelectionPoint()
	{
		return iStartSelectionPoint;
	}

	public Point getiEndSelectionPoint()
	{
		return iEndSelectionPoint;
	}

	public RectangleSelectionLayer getMapLayer()
	{
		return mapLayer;
	}

	public PreviewMap getMap()
	{
		return mMap;
	}
}
