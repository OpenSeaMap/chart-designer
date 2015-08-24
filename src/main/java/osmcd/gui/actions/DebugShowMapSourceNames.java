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

<<<<<<< HEAD
import osmb.mapsources.ACMapSourcesManager;
import osmb.mapsources.IfMapSource;
import osmb.mapsources.MapSourceLoaderInfo;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;

public class DebugShowMapSourceNames implements ActionListener
{
	@Override
=======
import osmcb.mapsources.ACMapSourcesManager;
import osmcb.program.interfaces.IfMapSource;
import osmcb.program.model.MapSourceLoaderInfo;
import osmcd.OSMCDStrs;
import osmcd.gui.MainGUI;

public class DebugShowMapSourceNames implements ActionListener
{

>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
	public void actionPerformed(ActionEvent e)
	{
		ArrayList<IfMapSource> mapSources = new ArrayList<IfMapSource>(ACMapSourcesManager.getInstance().getAllAvailableMapSources());

		Collections.sort(mapSources, new Comparator<IfMapSource>()
		{

<<<<<<< HEAD
			@Override
=======
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
			public int compare(IfMapSource o1, IfMapSource o2)
			{
				return o1.getName().compareTo(o2.getName());
			}

		});
		JFrame dialog = new JFrame(OSMCDStrs.RStr("dlg_show_source_title"));
<<<<<<< HEAD
		dialog.setLocationRelativeTo(MainFrame.getMainGUI());
=======
		dialog.setLocationRelativeTo(MainGUI.getMainGUI());
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
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

		List<IfMapSource> mapSources;

<<<<<<< HEAD
		public MapSourcesTableModel(List<IfMapSource> mapSources)
		{
=======
		public MapSourcesTableModel(List<IfMapSource> mapSources) {
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
			super();
			this.mapSources = mapSources;
		}

<<<<<<< HEAD
		@Override
=======
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
		public int getRowCount()
		{
			return mapSources.size();
		}

<<<<<<< HEAD
		@Override
=======
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
		public int getColumnCount()
		{
			return 4;
		}

		@Override
		public String getColumnName(int column)
		{
			switch (column)
			{
<<<<<<< HEAD
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
=======
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

>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			IfMapSource ms = mapSources.get(rowIndex);
			MapSourceLoaderInfo li;
			switch (columnIndex)
			{
<<<<<<< HEAD
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
=======
			case 0:
				return ms.getName();
			case 1:
				return ms.toString();
			case 2:
				li = ms.getLoaderInfo();
				if (li == null)
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
					return null;
			}
		}
	}
}
