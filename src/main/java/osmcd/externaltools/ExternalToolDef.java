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
package osmcd.externaltools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import osmb.utilities.GUIExceptionHandler;
import osmb.utilities.OSMBStrs;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;
import osmcd.program.MapSelection;

@XmlRootElement(name = "ExternalTool")
public class ExternalToolDef implements ActionListener
{
	private static final Logger log = Logger.getLogger(ExternalToolDef.class);

	/**
	 * Name used for the menu entry in MOBAC
	 */
	public String name;

	/**
	 * For starting a commandline-script on Windows use <code>cmd /c start mybatch.cmd</code>
	 */
	public String command;

	public boolean debug = false;

	@XmlList
	public List<ToolParameters> parameters = new ArrayList<ToolParameters>();

	private boolean mapSelNull(MapSelection mapSel)
	{
		if (mapSel != null)
			return false;
		JOptionPane.showMessageDialog(MainFrame.getMainGUI(), OSMCDStrs.RStr("msg_tools_exec_error_selected_area"), OSMBStrs.RStr("Error"),
		    JOptionPane.ERROR_MESSAGE);
		return true;

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			String executeCommand = command;
			MainFrame mainFrame = MainFrame.getMainGUI();
			MapSelection mapSel = mainFrame.getMapSelectionCoordinates();
			int[] zooms = mainFrame.getSelectedZoomLevels().getZoomLevels();
			for (ToolParameters param : parameters)
			{
				String add = "";
				switch (param)
				{
					case MAX_LAT:
						if (mapSelNull(mapSel))
							return;
						add = Double.toString(mapSel.getMax().lat);
						break;
					case MIN_LAT:
						if (mapSelNull(mapSel))
							return;
						add = Double.toString(mapSel.getMin().lat);
						break;
					case MAX_LON:
						if (mapSelNull(mapSel))
							return;
						add = Double.toString(mapSel.getMax().lon);
						break;
					case MIN_LON:
						if (mapSelNull(mapSel))
							return;
						add = Double.toString(mapSel.getMin().lon);
						break;
					case MAX_ZOOM:
						if (zooms.length == 0)
						{
							JOptionPane.showMessageDialog(mainFrame, OSMCDStrs.RStr("msg_no_zoom_level_selected"), OSMBStrs.RStr("Error"), JOptionPane.ERROR_MESSAGE);
							return;
						}
						add = Integer.toString(zooms[zooms.length - 1]);
						break;
					case MIN_ZOOM:
						if (zooms.length == 0)
						{
							JOptionPane.showMessageDialog(mainFrame, OSMCDStrs.RStr("msg_no_zoom_level_selected"), OSMBStrs.RStr("Error"), JOptionPane.ERROR_MESSAGE);
							return;
						}
						add = Integer.toString(zooms[0]);
						break;
					case MAPSOURCE_NAME:
						add = mainFrame.previewMap.getMapSource().getName();
						break;
					case MAPSOURCE_DISPLAYNAME:
						add = mainFrame.previewMap.getMapSource().toString();
						break;
					// case NAME_EDITBOX:
					// // add = mainFrame.getUserTextCatalogName();
					// add = mainFram;
					// break;
					default:
						throw new RuntimeException("Unsupported parameter type: " + param);
				}
				if (add.indexOf(' ') >= 0)
					add = "\"" + add + "\"";
				executeCommand += " " + add;
			}
			if (debug)
			{
				int r = JOptionPane.showConfirmDialog(mainFrame, String.format(OSMCDStrs.RStr("msg_tools_exec_command_ask"), executeCommand),
				    OSMCDStrs.RStr("msg_tools_exec_command_ask_title"), JOptionPane.OK_CANCEL_OPTION);
				if (r != JOptionPane.OK_OPTION)
					return;
			}
			log.debug("Executing " + executeCommand);
			Runtime.getRuntime().exec(executeCommand);
		}
		catch (Exception e1)
		{
			GUIExceptionHandler.processException(e1);
		}
	}
}
