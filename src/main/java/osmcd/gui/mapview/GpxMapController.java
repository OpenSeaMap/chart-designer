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
import java.math.BigDecimal;

import javax.swing.JOptionPane;

import osmb.program.map.IfMapSpace;
import osmcd.OSMCDStrs;
import osmcd.data.gpx.gpx11.Gpx;
import osmcd.data.gpx.gpx11.WptType;
import osmcd.gui.actions.GpxEditor;
import osmcd.gui.gpxtree.GpxEntry;
import osmcd.gui.gpxtree.GpxRootEntry;
import osmcd.gui.gpxtree.JGpxPanel;
import osmcd.gui.gpxtree.RteEntry;
import osmcd.gui.gpxtree.TrksegEntry;

/**
 * Allows to create new GPX way-points by clicking on the preview iMap
 */
public class GpxMapController extends JMapController implements MouseListener
{
	private JGpxPanel panel;
	private GpxEntry entry;

	public GpxMapController(PreviewMap map, JGpxPanel panel, boolean enabled)
	{
		super(map, enabled);
		this.panel = panel;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// Add new GPX point to currently selected GPX file
		disable();
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			entry = panel.getSelectedEntry();
			Gpx gpx = entry.getLayer().getGpx();
			Point p = e.getPoint();
			Point tl = map.getTopLeftCoordinate();
			p.x += tl.x;
			p.y += tl.y;
			IfMapSpace mapSpace = map.getMapSource().getMapSpace();
			int maxPixel = mapSpace.getMaxPixels(map.getZoom());
			if (p.x < 0 || p.x > maxPixel || p.y < 0 || p.y > maxPixel)
				return; // outside of world region
			double lon = mapSpace.cXToLon(p.x, map.getZoom());
			double lat = mapSpace.cYToLat(p.y, map.getZoom());
			String name = JOptionPane.showInputDialog(null, OSMCDStrs.RStr("dlg_gpx_inpu_point_name"));
			if (name == null)
				return;
			Gpx gpx11 = gpx;
			WptType wpt = new WptType();
			wpt.setName(name);
			wpt.setLat(new BigDecimal(lat));
			wpt.setLon(new BigDecimal(lon));
			GpxEditor editor = GpxEditor.getInstance();
			if (entry.getClass() == GpxRootEntry.class)
			{
				gpx11.getWpt().add(wpt);
			}
			else if (entry instanceof RteEntry)
			{
				editor.findRteAndAdd((RteEntry) entry, wpt);
			}
			else if (entry instanceof TrksegEntry)
			{
				editor.findTrksegAndAdd((TrksegEntry) entry, wpt);
			}
			panel.addWaypoint(wpt, entry);
		}
		map.repaint();
	}

	public void repaint()
	{
		map.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e)
	{

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{

	}

	@Override
	public void disable()
	{
		super.disable();
		map.getMapSelectionController().enable();
	}
}
