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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import osmb.utilities.GUIExceptionHandler;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;
import osmcd.program.Logging;

public class DebugShowLogFile implements ActionListener
{
	@Override
	public void actionPerformed(ActionEvent event)
	{
		Logger log = Logger.getLogger(DebugShowLogFile.class);
		String logFile = Logging.getLogFile();
		if (logFile == null)
		{
			log.error("No file logger configured");
			JOptionPane.showMessageDialog(MainFrame.getMainGUI(), OSMCDStrs.RStr("msg_no_log_file_config"), OSMCDStrs.RStr("Error"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		File f = new File(logFile);
		if (!f.isFile())
		{
			log.error("Log file does not exists: " + f.getAbsolutePath());
			JOptionPane.showMessageDialog(MainFrame.getMainGUI(), String.format(OSMCDStrs.RStr("msg_no_log_file"), f.getAbsolutePath()), OSMCDStrs.RStr("Error"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try
		{
			Desktop.getDesktop().open(f);
		}
		catch (IOException e)
		{
			GUIExceptionHandler.processException(e);
		}
	}
}
