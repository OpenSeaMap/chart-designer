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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

<<<<<<< HEAD
import osmb.utilities.file.GpxFileFilter;
import osmcd.OSMCDSettings;
import osmcd.OSMCDStrs;
import osmcd.data.gpx.GPXUtils;
import osmcd.data.gpx.gpx11.Gpx;
import osmcd.gui.MainFrame;
import osmcd.gui.gpxtree.JGpxPanel;
import osmcd.gui.mapview.GpxLayer;
=======
import osmcb.utilities.file.GpxFileFilter;
import osmcd.OSMCDStrs;
import osmcd.data.gpx.GPXUtils;
import osmcd.data.gpx.gpx11.Gpx;
import osmcd.gui.MainGUI;
import osmcd.gui.mapview.layer.GpxLayer;
import osmcd.gui.panels.JGpxPanel;
import osmcd.program.model.OSMCDSettings;

public class GpxLoad implements ActionListener
{
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318

public class GpxLoad implements ActionListener
{
	private Logger log = Logger.getLogger(GpxLoad.class);

	JGpxPanel panel;

	public GpxLoad(JGpxPanel panel)
	{
		super();
		this.panel = panel;
	}

<<<<<<< HEAD
	@Override
=======
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
	public void actionPerformed(ActionEvent event)
	{
		if (!GPXUtils.checkJAXBVersion())
			return;
		JFileChooser fc = new JFileChooser();
		try
		{
<<<<<<< HEAD
			File dir = new File(OSMCDSettings.getInstance().getGpxFileChooserDir());
=======
			File dir = new File(OSMCDSettings.getInstance().gpxFileChooserDir);
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
			fc.setCurrentDirectory(dir); // restore the saved directory
		}
		catch (Exception e)
		{
		}
		fc.setMultiSelectionEnabled(true);
		fc.addChoosableFileFilter(new GpxFileFilter(false));
		final MainFrame mainGUI = MainFrame.getMainGUI();
		int returnVal = fc.showOpenDialog(mainGUI);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;
<<<<<<< HEAD
		OSMCDSettings.getInstance().setGpxFileChooserDir(fc.getCurrentDirectory().getAbsolutePath());
=======
		OSMCDSettings.getInstance().gpxFileChooserDir = fc.getCurrentDirectory().getAbsolutePath();
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318

		File[] f = fc.getSelectedFiles();

		// check already opened gpx files
		boolean duplicates = false;
<<<<<<< HEAD
		for (File selectedFile : f)
=======
		for (File selectedFile: f)
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
		{
			duplicates = panel.isFileOpen(selectedFile.getAbsolutePath());
			if (duplicates)
				break;
		}
		if (duplicates)
		{
			int answer = JOptionPane.showConfirmDialog(mainGUI, OSMCDStrs.RStr("rp_gpx_msg_confirm_reopen_file"), OSMCDStrs.RStr("Warning"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (answer != JOptionPane.YES_OPTION)
				return;
		}

		// process
		if (f.length > 1)
		{
			doMultiLoad(f, mainGUI);
		}
		else if (f.length == 1)
		{
			doLoad(f[0], mainGUI);
		}
		mainGUI.previewMap.refreshMap();
	}

	/**
	 * @param f
	 */
	private void doLoad(File f, Component parent)
	{
		try
		{
			Gpx gpx = GPXUtils.loadGpxFile(f);
			GpxLayer gpxLayer = new GpxLayer(gpx);
			gpxLayer.setFile(f);
			panel.addGpxLayer(gpxLayer);
		}
		catch (JAXBException e)
		{
			JOptionPane.showMessageDialog(parent, "<html>Unable to load the GPX file <br><i>" + f.getAbsolutePath()
					+ "</i><br><br><b>Please make sure the file is a valid GPX v1.1 file.</b><br>" + "<br>Internal error message:<br>" + e.getMessage() + "</html>",
					"GPX loading failed", JOptionPane.ERROR_MESSAGE);
			throw new RuntimeException(e);
		}
	}

<<<<<<< HEAD
	private void doMultiLoad(final File[] files, final MainFrame mainGUI)
=======
	private void doMultiLoad(final File[] files, final MainGUI mainGUI)
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
	{
		final JDialog progressDialog = new JDialog(mainGUI);
		// prepare progress dialog
		progressDialog.setSize(400, 50);
		progressDialog.setResizable(false);
		progressDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		progressDialog.setLocation(Math.max(0, (int) (mainGUI.getLocation().getX() + mainGUI.getSize().getWidth() / 2 - 200)),
				Math.max(0, (int) (mainGUI.getLocation().getY() + mainGUI.getSize().getHeight() / 2 - 25)));
		final JProgressBar progressBar = new JProgressBar(0, files.length);
		progressDialog.add(progressBar);

		mainGUI.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		mainGUI.setEnabled(false);
		progressDialog.setVisible(true);

		Thread job = new Thread()
		{

			private int counter = 0;

<<<<<<< HEAD
			@Override
=======
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
			public void run()
			{
				try
				{
					// iterate over files to load
<<<<<<< HEAD
					for (final File file : files)
=======
					for (final File file: files)
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
					{
						counter++;
						SwingUtilities.invokeLater(new Runnable()
						{
<<<<<<< HEAD
							@Override
=======
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
							public void run()
							{
								progressBar.setValue(counter);
								progressDialog.setTitle("Processing " + counter + " of " + files.length + " <" + file.getName() + ">");
							}
						});
						doLoad(file, progressDialog);
					}
				}
				catch (RuntimeException e)
				{
					log.error(e.getMessage(), e);
				}
				finally
				{
					SwingUtilities.invokeLater(new Runnable()
					{
<<<<<<< HEAD
						@Override
=======
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
						public void run()
						{
							// close progress dialog
							mainGUI.previewMap.repaint();
							mainGUI.setCursor(Cursor.getDefaultCursor());
							if (progressDialog != null)
							{
								progressDialog.setVisible(false);
								progressDialog.dispose();
							}
							mainGUI.setEnabled(true);
							mainGUI.toFront();
						}
					});
				}
			};
		};

		job.start();
	}
}
