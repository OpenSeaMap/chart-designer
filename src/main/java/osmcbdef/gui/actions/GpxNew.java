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
package osmcbdef.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import osmcbdef.data.gpx.GPXUtils;
import osmcbdef.data.gpx.gpx11.Gpx;
import osmcbdef.gui.MainGUI;
import osmcbdef.gui.gpxtree.GpxRootEntry;
import osmcbdef.gui.mapview.layer.GpxLayer;
import osmcbdef.gui.panels.JGpxPanel;


public class GpxNew implements ActionListener {

	JGpxPanel panel;

	public GpxNew(JGpxPanel panel) {
		super();
		this.panel = panel;
	}

	public void actionPerformed(ActionEvent event) {
		if (!GPXUtils.checkJAXBVersion())
			return;
		newGpx();
		MainGUI.getMainGUI().previewMap.repaint();
	}

	public GpxRootEntry newGpx() {
		Gpx gpx = Gpx.createGpx();
		GpxLayer gpxLayer = new GpxLayer(gpx);
		return panel.addGpxLayer(gpxLayer);
	}
}
