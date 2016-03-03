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

import osmb.mapsources.ACMapSource;
import osmb.mapsources.IfFileBasedMapSource;
import osmb.mapsources.SiACMapSourcesManager;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;

public class RefreshCustomMapsources implements ActionListener
{
	@Override
	public void actionPerformed(ActionEvent e)
	{
		SiACMapSourcesManager manager = SiACMapSourcesManager.getInstance();
		MainFrame gui = MainFrame.getMainGUI();
		ACMapSource selectedMapSource = gui.getSelectedMapSource();
		boolean updateGui = false;
		int count = 0;
		for (ACMapSource mapSource : manager.getAllAvailableMapSources())
		{
			if (mapSource instanceof IfFileBasedMapSource)
			{
				IfFileBasedMapSource fbms = (IfFileBasedMapSource) mapSource;
				fbms.reinitialize();
				count++;
				if (mapSource.equals(selectedMapSource))
					updateGui = true;
			}
		}
		if (updateGui)
		{
			// The currently selected map source was updated - we have to force an GUI update in case the available zoom levels has been changed
			gui.mapSourceChanged(selectedMapSource);
		}
		JOptionPane.showMessageDialog(gui, String.format(OSMCDStrs.RStr("MapSources.RefreshCustomMapSources"), count));
	}
}
