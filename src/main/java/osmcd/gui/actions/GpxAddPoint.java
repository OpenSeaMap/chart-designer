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
package osmcd.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import osmb.utilities.OSMBStrs;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;
import osmcd.gui.gpxtree.GpxEntry;
import osmcd.gui.gpxtree.JGpxPanel;
import osmcd.gui.mapview.GpxMapController;
import osmcd.gui.mapview.PreviewMap;

public class GpxAddPoint implements ActionListener
{

	JGpxPanel panel;

	private GpxMapController mapController = null;

	public GpxAddPoint(JGpxPanel panel) {
		super();
		this.panel = panel;
	}

	public synchronized void actionPerformed(ActionEvent event)
	{
		GpxEntry entry = panel.getSelectedEntry();
		if (entry == null)
		{
			int answer = JOptionPane.showConfirmDialog(null, OSMCDStrs.RStr("rp_gpx_msg_ask_create_new"), OSMCDStrs.RStr("rp_gpx_msg_ask_create_new_title"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (answer != JOptionPane.YES_OPTION)
				return;
			entry = new GpxNew(panel).newGpx();
		}

		if (!entry.isWaypointParent())
		{
			JOptionPane.showMessageDialog(null, OSMCDStrs.RStr("rp_gpx_msg_add_point_failed"), OSMBStrs.RStr("Error"), JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		PreviewMap map = MainFrame.getMainGUI().previewMap;
		map.getMapSelectionController().disable();
		if (mapController == null)
			mapController = new GpxMapController(map, panel, false);
		mapController.enable();
	}
}
