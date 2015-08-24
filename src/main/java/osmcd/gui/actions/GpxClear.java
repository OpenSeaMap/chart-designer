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
<<<<<<< HEAD

import osmcd.gui.MainFrame;
import osmcd.gui.gpxtree.JGpxPanel;
import osmcd.gui.mapview.GpxLayer;
import osmcd.gui.mapview.MapLayer;
=======
import osmcd.gui.MainGUI;
import osmcd.gui.mapview.interfaces.MapLayer;
import osmcd.gui.mapview.layer.GpxLayer;
import osmcd.gui.panels.JGpxPanel;
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318

/**
 * Deletes all loaded {@link GpxLayer}s from the main iMap viewer.
 * 
 */
public class GpxClear implements ActionListener
{

	JGpxPanel panel;

	public GpxClear(JGpxPanel panel)
	{
		super();
		this.panel = panel;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Iterator<MapLayer> mapLayers = MainFrame.getMainGUI().previewMap.mapLayers.iterator();
		while (mapLayers.hasNext())
		{
			if (mapLayers.next() instanceof GpxLayer) mapLayers.remove();
		}
		panel.resetModel();
		MainFrame.getMainGUI().previewMap.repaint();
	}

}
