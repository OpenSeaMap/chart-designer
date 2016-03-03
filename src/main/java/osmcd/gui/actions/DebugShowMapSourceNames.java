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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import osmb.mapsources.ACMapSource;
import osmb.mapsources.SiACMapSourcesManager;
import osmb.mapsources.MapSourceLoaderInfo;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;

public class DebugShowMapSourceNames implements ActionListener
{
	@Override
	public void actionPerformed(ActionEvent e)
	{
		ArrayList<ACMapSource> mapSources = new ArrayList<ACMapSource>(SiACMapSourcesManager.getInstance().getAllAvailableMapSources());

		Collections.sort(mapSources, new Comparator<ACMapSource>()
		{

			@Override
			public int compare(ACMapSource o1, ACMapSource o2)
			{
				return o1.getName().compareTo(o2.getName());
			}

		});
		JFrame dialog = new JFrame(OSMCDStrs.RStr("dlg_show_source_title"));
		dialog.setLocationRelativeTo(MainFrame.getMainGUI());
		dialog.setLocation(100, 40);
		Dimension dScreen = Toolkit.getDefaultToolkit().getScreenSize();
		dScreen.height -= 200;
		dScreen.width = Math.min(dScreen.width - 100, 700);
		dialog.setSize(dScreen);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JTable mapSourcesTable = new JTable(new MapSourcesTableModel(mapSources));
		JScrollPane scroller = new JScrollPane(mapSourcesTable);

		mapSourcesTable.getColumnModel().getColumn(2).setMaxWidth(100);
		dialog.add(scroller);
		dialog.setVisible(true);
	}

	static class MapSourcesTableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 1L;

		List<ACMapSource> mapSources;

		public MapSourcesTableModel(List<ACMapSource> mapSources)
		{
			super();
			this.mapSources = mapSources;
		}

		@Override
		public int getRowCount()
		{
			return mapSources.size();
		}

		@Override
		public int getColumnCount()
		{
			return 4;
		}

		@Override
		public String getColumnName(int column)
		{
			switch (column)
			{
				case 0:
					return OSMCDStrs.RStr("dlg_show_source_column_name");
				case 1:
					return OSMCDStrs.RStr("dlg_show_source_column_display_text");
				case 2:
					return OSMCDStrs.RStr("dlg_show_source_column_rev");
				case 3:
					return OSMCDStrs.RStr("dlg_show_source_column_type");
				default:
					return null;
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			ACMapSource ms = mapSources.get(rowIndex);
			MapSourceLoaderInfo li;
			switch (columnIndex)
			{
				case 0:
					return ms.getName();
				case 1:
					return ms.toString();
				case 2:
					li = ms.getLoaderInfo();
					if (li == null)
						return null;
					return li.getRevision();
				case 3:
					li = ms.getLoaderInfo();
					if (li == null)
						return null;
					String s = "";
					File f = li.getSourceFile();
					if (f != null)
						s += f.getName() + " / ";
					return s + li.getLoaderType();
				default:
					return null;
			}
		}
	}
}
