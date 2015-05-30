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
package osmcd;

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
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import osmb.program.ACSettings;
import osmb.utilities.OSMBRsc;
import osmb.utilities.OSMBStrs;
import osmb.utilities.OSMBUtilities;
import osmb.utilities.geo.EastNorthCoordinate;
import osmcd.gui.actions.GpxLoad;
import osmcd.gui.components.JCoordinatesPanel;
import osmcd.program.Bookmark;
import osmcd.program.ProgramInfo;
import osmcd.program.SettingsPaperAtlas;

@XmlRootElement
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class OSMCDSettings extends ACSettings
{
	// class data / statics
	protected static Logger initLogger()
	{
		return log = Logger.getLogger(OSMCDSettings.class);
	}

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

	// instance data, usually all protected
	// esp. this classes instances are load from a xml-file by loadOrQuit()

	@XmlElement
	// GUI/Main Window related
	// public String googleLanguage = "en";
	private MainWindowSettings mainWindow = new MainWindowSettings();

	// Mapview related settings
	private int maxMapSize = 65536;
	private int mapviewZoom = 3;
	private int mapviewGridZoom = -1;
	private int mapOverlapTiles = 0;
	private Point mapviewSelectionMax = null;
	private Point mapviewSelectionMin = null;
	private EastNorthCoordinate mapviewCenterCoordinate = new EastNorthCoordinate(50, 9);
	@XmlElementWrapper(name = "selectedZoomLevels")
	@XmlElement(name = "zoomLevel")
	private List<Integer> selectedZoomLevels = null;

	// @XmlElement(nillable = false)
	private String mapviewMapSource = null;
	private String catalogName = null;

	// Bookmark related
	@XmlElementWrapper(name = "placeBookmarks")
	@XmlElement(name = "bookmark")
	private List<Bookmark> placeBookmarks = new ArrayList<Bookmark>();

	// public String osmHikingTicket = "";

	// GPX-tracks
	/**
	 * Saves the last used directory of the GPX file chooser dialog. Used in {@link GpxLoad}.
	 */
	private String gpxFileChooserDir = "";

	@XmlElement
	// Paper Atlas related settings
	private final SettingsPaperAtlas paperAtlas = new SettingsPaperAtlas();

	/**
	 * constructor should provide default values for every element
	 */
	protected OSMCDSettings() {
		catalogName = "Layer";
		Dimension dScreen = Toolkit.getDefaultToolkit().getScreenSize();
		getMainWindow().size.width = (int) (0.9f * dScreen.width);
		getMainWindow().size.height = (int) (0.9f * dScreen.height);
		getMainWindow().collapsedPanels.add(JCoordinatesPanel.NAME);
		getMainWindow().collapsedPanels.add("Gpx");
	}

	public static OSMCDSettings getInstance()
	{
		if (instance == null)
			instance = new OSMCDSettings();
		return (OSMCDSettings) instance;
	}

	public static ACSettings load() throws JAXBException
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
			s = (OSMCDSettings) um.unmarshal(getFile());
			s.getWgsGrid().checkValues();
			s.paperAtlas.checkValues();
			SETTINGS_LAST_MODIFIED = getFile().lastModified();
			OSMBRsc.updateLocalizedStrings();
		}
		finally
		{
			instance = s;
		}
		return instance;
	}

	/**
	 * If the settings file cannot be read, the app quits
	 * 
	 * @return
	 */
	public static OSMCDSettings loadOrQuit()
	{
		OSMCDSettings s = null;
		try
		{
			s = (OSMCDSettings) OSMCDSettings.load();
		}
		catch (JAXBException e)
		{
			log.error(e);
			JOptionPane.showMessageDialog(null, OSMBStrs.RStr(OSMBStrs.RStr("msg_settings_file_can_not_parse")), OSMBStrs.RStr("Error"), JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		return s;
	}

	public static void save() throws JAXBException
	{
		getInstance().cfgVersion = ProgramInfo.getVersion();
		JAXBContext context = JAXBContext.newInstance(OSMCDSettings.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		ByteArrayOutputStream bOS = null;
		FileOutputStream fOS = null;
		try
		{
			// First we write to a buffer and if that works we write the buffer
			// to disk. Direct writing to file may result in an defect xml file
			// in case of an error
			bOS = new ByteArrayOutputStream();
			m.marshal(getInstance(), bOS);
			fOS = new FileOutputStream(getFile());
			fOS.write(bOS.toByteArray());
			fOS.close();
			SETTINGS_LAST_MODIFIED = getFile().lastModified();
		}
		catch (IOException e)
		{
			throw new JAXBException(e);
		}
		finally
		{
			OSMBUtilities.closeStream(fOS);
		}
	}

	public MainWindowSettings getMainWindow()
	{
		return mainWindow;
	}

	/**
	 * @return the maxMapSize
	 */
	public int getMaxMapSize()
	{
		return maxMapSize;
	}

	/**
	 * @param maxMapSize
	 *          the maxMapSize to set
	 */
	public void setMaxMapSize(int maxMapSize)
	{
		this.maxMapSize = maxMapSize;
	}

	/**
	 * @return the mapviewZoom
	 */
	public int getMapviewZoom()
	{
		return mapviewZoom;
	}

	/**
	 * @param mapviewZoom
	 *          the mapviewZoom to set
	 */
	public void setMapviewZoom(int mapviewZoom)
	{
		this.mapviewZoom = mapviewZoom;
	}

	/**
	 * @return the mapviewGridZoom
	 */
	public int getMapviewGridZoom()
	{
		return mapviewGridZoom;
	}

	/**
	 * @param mapviewGridZoom
	 *          the mapviewGridZoom to set
	 */
	public void setMapviewGridZoom(int mapviewGridZoom)
	{
		this.mapviewGridZoom = mapviewGridZoom;
	}

	/**
	 * @return the mapOverlapTiles
	 */
	public int getMapOverlapTiles()
	{
		return mapOverlapTiles;
	}

	/**
	 * @param mapOverlapTiles
	 *          the mapOverlapTiles to set
	 */
	public void setMapOverlapTiles(int mapOverlapTiles)
	{
		this.mapOverlapTiles = mapOverlapTiles;
	}

	/**
	 * @return the mapviewSelectionMax
	 */
	public Point getMapviewSelectionMax()
	{
		return mapviewSelectionMax;
	}

	/**
	 * @param mapviewSelectionMax
	 *          the mapviewSelectionMax to set
	 */
	public void setMapviewSelectionMax(Point mapviewSelectionMax)
	{
		this.mapviewSelectionMax = mapviewSelectionMax;
	}

	/**
	 * @return the mapviewSelectionMin
	 */
	public Point getMapviewSelectionMin()
	{
		return mapviewSelectionMin;
	}

	/**
	 * @param mapviewSelectionMin
	 *          the mapviewSelectionMin to set
	 */
	public void setMapviewSelectionMin(Point mapviewSelectionMin)
	{
		this.mapviewSelectionMin = mapviewSelectionMin;
	}

	/**
	 * @return the mapviewCenterCoordinate
	 */
	public EastNorthCoordinate getMapviewCenterCoordinate()
	{
		return mapviewCenterCoordinate;
	}

	/**
	 * @param mapviewCenterCoordinate
	 *          the mapviewCenterCoordinate to set
	 */
	public void setMapviewCenterCoordinate(EastNorthCoordinate mapviewCenterCoordinate)
	{
		this.mapviewCenterCoordinate = mapviewCenterCoordinate;
	}

	/**
	 * @return the selectedZoomLevels
	 */
	@XmlTransient
	public List<Integer> getSelectedZoomLevels()
	{
		return selectedZoomLevels;
	}

	/**
	 * @param selectedZoomLevels
	 *          the selectedZoomLevels to set
	 */
	public void setSelectedZoomLevels(List<Integer> selectedZoomLevels)
	{
		this.selectedZoomLevels = selectedZoomLevels;
	}

	/**
	 * @return the mapviewMapSource
	 */
	public String getMapviewMapSource()
	{
		return mapviewMapSource;
	}

	/**
	 * @param mapviewMapSource
	 *          the mapviewMapSource to set
	 */
	public void setMapviewMapSource(String mapviewMapSource)
	{
		this.mapviewMapSource = mapviewMapSource;
	}

	/**
	 * @return the catalogName
	 */
	public String getCatalogName()
	{
		return catalogName;
	}

	/**
	 * @param catalogName
	 *          the catalogName to set
	 */
	public void setCatalogName(String catalogName)
	{
		this.catalogName = catalogName;
	}

	/**
	 * @return the placeBookmarks
	 */
	@XmlTransient
	public List<Bookmark> getPlaceBookmarks()
	{
		return placeBookmarks;
	}

	/**
	 * @param placeBookmarks
	 *          the placeBookmarks to set
	 */
	public void setPlaceBookmarks(List<Bookmark> placeBookmarks)
	{
		this.placeBookmarks = placeBookmarks;
	}

	/**
	 * @return the paperAtlas
	 */
	public SettingsPaperAtlas getPaperAtlas()
	{
		return paperAtlas;
	}

	public String getGpxFileChooserDir()
	{
		return gpxFileChooserDir;
	}

	public void setGpxFileChooserDir(String gpxFileChooserDir)
	{
		this.gpxFileChooserDir = gpxFileChooserDir;
	}
}
