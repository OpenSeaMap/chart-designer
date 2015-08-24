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

//License: GPL. Copyright 2008 by Jan Peter Stotz

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/DefaultMapController.java

import osmb.utilities.OSUtilities;

/**
 * Default iMap controller which implements iMap moving by pressing the right mouse button and zooming by double click or by mouse wheel.
=======
import osmcb.utilities.OSUtilities;
import osmcd.gui.mapview.PreviewMap;

/**
 * Default map controller which implements map moving by pressing the right mouse button and zooming by double click or by mouse wheel.
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/controller/DefaultMapController.java
 * 
 * @author Jan Peter Stotz
 * 
 */
public class DefaultMapController extends JMapController implements MouseListener, MouseMotionListener, MouseWheelListener
{
<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/DefaultMapController.java
=======

>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/controller/DefaultMapController.java
	private static final int MOUSE_BUTTONS_MASK = MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK;

	private static final int MAC_MOUSE_BUTTON3_MASK = MouseEvent.CTRL_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK;

	public DefaultMapController(PreviewMap map)
	{
		super(map, true);
	}

	private Point lastDragPoint;

	private boolean isMoving = false;

	private boolean movementEnabled = true;

	private int movementMouseButton = MouseEvent.BUTTON3;
	private int movementMouseButtonMask = MouseEvent.BUTTON3_DOWN_MASK;

	private boolean wheelZoomEnabled = true;
	private boolean doubleClickZoomEnabled = true;

	@Override
	public void mouseDragged(MouseEvent e)
	{
<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/DefaultMapController.java
		if (!movementEnabled || !isMoving)
			return;
=======
		if (!movementEnabled || !isMoving) return;
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/controller/DefaultMapController.java
		// Is only the selected mouse button pressed?
		if ((e.getModifiersEx() & MOUSE_BUTTONS_MASK) == movementMouseButtonMask)
		{
			Point p = e.getPoint();
			if (lastDragPoint != null)
			{
				int diffx = lastDragPoint.x - p.x;
				int diffy = lastDragPoint.y - p.y;
				map.moveMap(diffx, diffy);
			}
			lastDragPoint = p;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/DefaultMapController.java
		if (doubleClickZoomEnabled && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
			map.zoomIn(e.getPoint());
=======
		if (doubleClickZoomEnabled && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) map.zoomIn(e.getPoint());
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/controller/DefaultMapController.java
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == movementMouseButton || OSUtilities.isPlatformOsx() && e.getModifiersEx() == MAC_MOUSE_BUTTON3_MASK)
		{
			lastDragPoint = null;
			isMoving = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == movementMouseButton || OSUtilities.isPlatformOsx() && e.getButton() == MouseEvent.BUTTON1)
		{
			lastDragPoint = null;
			isMoving = false;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/DefaultMapController.java
		if (wheelZoomEnabled)
			map.setZoom(map.getZoom() - e.getWheelRotation(), e.getPoint());
=======
		if (wheelZoomEnabled) map.setZoom(map.getZoom() - e.getWheelRotation(), e.getPoint());
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/controller/DefaultMapController.java
	}

	public boolean isMovementEnabled()
	{
		return movementEnabled;
	}

	/**
	 * Enables or disables that the iMap pane can be moved using the mouse.
	 * 
	 * @param movementEnabled
	 */
	public void setMovementEnabled(boolean movementEnabled)
	{
		this.movementEnabled = movementEnabled;
	}

	public int getMovementMouseButton()
	{
		return movementMouseButton;
	}

	/**
	 * Sets the mouse button that is used for moving the iMap. Possible values are:
	 * <ul>
	 * <li>{@link MouseEvent#BUTTON1} (left mouse button)</li>
	 * <li>{@link MouseEvent#BUTTON2} (middle mouse button)</li>
	 * <li>{@link MouseEvent#BUTTON3} (right mouse button)</li>
	 * </ul>
	 * 
	 * @param movementMouseButton
	 */
	public void setMovementMouseButton(int movementMouseButton)
	{
		this.movementMouseButton = movementMouseButton;
		switch (movementMouseButton)
		{
			case MouseEvent.BUTTON1:
				movementMouseButtonMask = MouseEvent.BUTTON1_DOWN_MASK;
				break;
			case MouseEvent.BUTTON2:
				movementMouseButtonMask = MouseEvent.BUTTON2_DOWN_MASK;
				break;
			case MouseEvent.BUTTON3:
				movementMouseButtonMask = MouseEvent.BUTTON3_DOWN_MASK;
				break;
			default:
				throw new RuntimeException("Unsupported button");
		}
	}

	public boolean isWheelZoomEnabled()
	{
		return wheelZoomEnabled;
	}

	public void setWheelZoomEnabled(boolean wheelZoomEnabled)
	{
		this.wheelZoomEnabled = wheelZoomEnabled;
	}

	public boolean isDoubleClickZoomEnabled()
	{
		return doubleClickZoomEnabled;
	}

	public void setDoubleClickZoomEnabled(boolean doubleClickZoomEnabled)
	{
		this.doubleClickZoomEnabled = doubleClickZoomEnabled;
	}

	@Override
	public void mouseEntered(MouseEvent e)
<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/DefaultMapController.java
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}
=======
	{}

	@Override
	public void mouseExited(MouseEvent e)
	{}
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/controller/DefaultMapController.java

	@Override
	public void mouseMoved(MouseEvent e)
	{

		// Mac OSX simulates with ctrl + mouse 1 the second mouse button hence
		// no dragging events get fired.
		if (!OSUtilities.isPlatformOsx() || !movementEnabled || !isMoving) return;
		// Is only the selected mouse button pressed?
		if (e.getModifiersEx() == MouseEvent.CTRL_DOWN_MASK)
		{
			Point p = e.getPoint();
			if (lastDragPoint != null)
			{
				int diffx = lastDragPoint.x - p.x;
				int diffy = lastDragPoint.y - p.y;
				map.moveMap(diffx, diffy);
			}
			lastDragPoint = p;
		}

	}

}
