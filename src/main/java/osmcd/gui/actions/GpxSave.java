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
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import osmb.utilities.file.GpxFileFilter;
import osmcd.OSMCDSettings;
import osmcd.OSMCDStrs;
import osmcd.data.gpx.GPXUtils;
import osmcd.data.gpx.gpx11.Gpx;
import osmcd.gui.MainFrame;
import osmcd.gui.gpxtree.GpxEntry;
import osmcd.gui.gpxtree.JGpxPanel;

public class GpxSave implements ActionListener
{
	private JGpxPanel panel;
	private boolean saveAs;

	public GpxSave(JGpxPanel panel)
	{
		this(panel, false);
	}

	/**
	 * 
	 * @param panel
	 * @param saveAs
	 *          if true a file chooser dialog is displayed where the user can change the filename
	 */
	public GpxSave(JGpxPanel panel, boolean saveAs)
	{
		super();
		this.panel = panel;
		this.saveAs = saveAs;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{

		GpxEntry entry = panel.getSelectedEntry();
		if (entry == null)
		{
			JOptionPane.showMessageDialog(null, OSMCDStrs.RStr("rp_gpx_msg_error_save_gpx_file"), OSMCDStrs.RStr("rp_gpx_msg_no_select_file"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!GPXUtils.checkJAXBVersion())
			return;

		Gpx gpx = entry.getLayer().getGpx();

		try
		{
			File f = entry.getLayer().getFile();
			if (saveAs || f == null)
				f = selectFile(f);
			if (f == null)
				return;
			if (!f.getName().toLowerCase().endsWith(".gpx"))
				f = new File(f.getAbsolutePath() + ".gpx");
			entry.getLayer().setFile(f);
			GPXUtils.saveGpxFile(gpx, f);
		}
		catch (JAXBException e)
		{
			throw new RuntimeException(e);
		}
		MainFrame.getMainGUI().previewMap.repaint();
	}

	private File selectFile(File f)
	{
		JFileChooser fc = new JFileChooser();
		try
		{
			File dir = new File(OSMCDSettings.getInstance().getGpxFileChooserDir());
			if (f == null)
				fc.setCurrentDirectory(dir); // restore the saved directory
			else
				fc.setSelectedFile(f);
		}
		catch (Exception e)
		{
		}
		fc.addChoosableFileFilter(new GpxFileFilter(true));
		int returnVal = fc.showSaveDialog(MainFrame.getMainGUI());
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return null;
		OSMCDSettings.getInstance().setGpxFileChooserDir(fc.getCurrentDirectory().getAbsolutePath());
		return fc.getSelectedFile();
	}
}
