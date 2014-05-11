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
import java.util.Iterator;

import osmcd.gui.MainGUI;
import osmcd.gui.mapview.interfaces.MapLayer;
import osmcd.gui.mapview.layer.GpxLayer;
import osmcd.gui.panels.JGpxPanel;

/**
 * Deletes all loaded {@link GpxLayer}s from the main map viewer.
 * 
 */
public class GpxClear implements ActionListener
{

	JGpxPanel panel;

	public GpxClear(JGpxPanel panel) {
		super();
		this.panel = panel;
	}

	public void actionPerformed(ActionEvent e)
	{
		Iterator<MapLayer> mapLayers = MainGUI.getMainGUI().previewMap.mapLayers.iterator();
		while (mapLayers.hasNext())
		{
			if (mapLayers.next() instanceof GpxLayer)
				mapLayers.remove();
		}
		panel.resetModel();
		MainGUI.getMainGUI().previewMap.repaint();
	}

}
