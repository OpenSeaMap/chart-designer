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
package osmcd.program.model;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;
import osmcb.OSMCBStrs;
import osmcb.program.ProgramInfo;
import osmcb.program.model.OSMCBSettings;
import osmcb.utilities.OSMCBRsc;
import osmcb.utilities.OSMCBUtilities;
import osmcd.gui.actions.GpxLoad;
import osmcd.gui.panels.JCoordinatesPanel;

@XmlRootElement
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class OSMCDSettings extends OSMCBSettings
{
	protected static Logger log = Logger.getLogger(OSMCDSettings.class);

	/**
	 * Mapview related settings
	 */
	public int mapviewZoom = 3;
	public int mapviewGridZoom = -1;
	public Point mapviewSelectionMax = null;
	public Point mapviewSelectionMin = null;
	@XmlElementWrapper(name = "selectedZoomLevels")
	@XmlElement(name = "zoomLevel")
	public List<Integer> selectedZoomLevels = null;

	@XmlElement(nillable = false)
	public String mapviewMapSource = null;
	public String elementName = null;
	@XmlElementWrapper(name = "placeBookmarks")
	@XmlElement(name = "bookmark")
	public List<Bookmark> placeBookmarks = new ArrayList<Bookmark>();

	public String googleLanguage = "en";
	public String osmHikingTicket = "";

	/**
	 * Saves the last used directory of the GPX file chooser dialog. Used in {@link GpxLoad}.
	 */
	public String gpxFileChooserDir = "";

	public final BundleFormatSpecificSettings bundleFormatSpecificSettings = new BundleFormatSpecificSettings();

	public static class BundleFormatSpecificSettings
	{
		@XmlElement
		public Integer garminCustomMaxMapCount = 100;
	}

	public final MainWindowSettings mainWindow = new MainWindowSettings();

	public static class MainWindowSettings
	{
		public Dimension size = new Dimension();
		public Point position = new Point(-1, -1);
		public boolean maximized = true;

		public boolean leftPanelVisible = true;
		public boolean rightPanelVisible = true;

		@XmlElementWrapper(name = "collapsedPanels")
		@XmlElement(name = "collapsedPanel")
		public Vector<String> collapsedPanels = new Vector<String>();
	}

	public final SettingsPaperAtlas paperAtlas = new SettingsPaperAtlas();

	/**
	 * constructor should provide default values for every element
	 */
	protected OSMCDSettings()
	{
		elementName = "Layer";
		Dimension dScreen = Toolkit.getDefaultToolkit().getScreenSize();
		mainWindow.size.width = (int) (0.9f * dScreen.width);
		mainWindow.size.height = (int) (0.9f * dScreen.height);
		mainWindow.collapsedPanels.add(JCoordinatesPanel.NAME);
		mainWindow.collapsedPanels.add("Gpx");
	}

	public static OSMCDSettings getInstance()
	{
		if (instance == null)
			instance = new OSMCDSettings();
		return (OSMCDSettings) instance;
	}

	public static OSMCDSettings load() throws JAXBException
	{
		OSMCDSettings s = null;
		try
		{
			JAXBContext context = JAXBContext.newInstance(OSMCDSettings.class);
			Unmarshaller um = context.createUnmarshaller();
			um.setEventHandler(new ValidationEventHandler()
			{
				@Override
				public boolean handleEvent(ValidationEvent event)
				{
					log.warn("Problem on loading settings.xml: " + event.getMessage());
					return true;
				}
			});
			s = (OSMCDSettings) um.unmarshal(FILE);
			s.wgsGrid.checkValues();
			// instance.paperAtlas.checkValues();
			SETTINGS_LAST_MODIFIED = FILE.lastModified();
			OSMCBRsc.updateLocalizedStrings();
		}
		finally
		{
			instance = s;
		}
		return (OSMCDSettings) instance;
	}

	public static OSMCDSettings loadOrQuit()
	{
		OSMCDSettings s = null;
		try
		{
			s = OSMCDSettings.load();
		}
		catch (JAXBException e)
		{
			log.error(e);
			JOptionPane
					.showMessageDialog(null, OSMCBStrs.RStr(OSMCBStrs.RStr("msg_settings_file_can_not_parse")), OSMCBStrs.RStr("Error"), JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		return s;
	}

	public static void save() throws JAXBException
	{
		getInstance().version = ProgramInfo.getVersion();
		JAXBContext context = JAXBContext.newInstance(OSMCDSettings.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		ByteArrayOutputStream bo = null;
		FileOutputStream fo = null;
		try
		{
			// First we write to a buffer and if that works we write the buffer
			// to disk. Direct writing to file may result in an defect xml file
			// in case of an error
			bo = new ByteArrayOutputStream();
			m.marshal(getInstance(), bo);
			fo = new FileOutputStream(FILE);
			fo.write(bo.toByteArray());
			fo.close();
			SETTINGS_LAST_MODIFIED = FILE.lastModified();
		}
		catch (IOException e)
		{
			throw new JAXBException(e);
		}
		finally
		{
			OSMCBUtilities.closeStream(fo);
		}
	}
}
