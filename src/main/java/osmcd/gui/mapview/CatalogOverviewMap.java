/**
 * copyright ???????????????
 */
package osmcd.gui.mapview;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ConcurrentModificationException;

import javax.imageio.ImageIO;

import osmb.mapsources.ACMapSourcesManager;
import osmb.mapsources.IfMapSource;
import osmb.mapsources.mapspace.MercatorPower2MapSpace;
import osmb.program.WgsGrid;
import osmb.program.WgsGridSettings;
import osmb.program.catalog.Catalog;
import osmb.utilities.geo.EastNorthCoordinate;
import osmcd.OSMCDApp;
import osmcd.OSMCDSettings;

/**
 * @author wilbert
 *
 */
@SuppressWarnings("serial")
public class CatalogOverviewMap extends JMapViewer
{
	int nSquare = 7 * 256 + 1; // 1 * 256 + 1; // 1792; // 259;  // 4 * 256;
	protected int OVERVIEW_WIDTH = nSquare;
	protected int OVERVIEW_HEIGHT = nSquare;
	
	Catalog catalog = null;
	WgsGridSettings overviewWGSGridSettings = new WgsGridSettings();
	private final WgsGrid wgsGrid = new WgsGrid(overviewWGSGridSettings, this);
	
	BufferedImage overviewImage = null;
	Graphics2D graphics;
	
	public CatalogOverviewMap()
	{
		super(ACMapSourcesManager.getInstance().getDefaultMapSource(), 5); // int downloadThreadCount = 5 unused
		catalog = OSMCDApp.getApp().getCatalog();
		//mapSource = ACMapSourcesManager.getInstance().getDefaultMapSource(); //???
		OSMCDSettings settings = OSMCDSettings.getInstance();
		IfMapSource mapSource = ACMapSourcesManager.getInstance().getSourceByName(settings.getMapviewMapSource());
		if (mapSource != null)
			setMapSource(mapSource);
		else
			mapSource = ACMapSourcesManager.getInstance().getDefaultMapSource();
		
		usePlaceHolderTiles = false;
		final MapAreaHighlightingLayer msl = new MapAreaHighlightingLayer(catalog);
		mapLayers.add(msl);
		overviewWGSGridSettings.enabled = true;		
		
		int xMin = catalog.getXBorderMin();
		int xMax = catalog.getXBorderMax();
		int yMin = catalog.getYBorderMin();
		int yMax = catalog.getYBorderMax();
		int zDiff = MAX_ZOOM - mapSource.getMaxZoom();
		//setDisplayToFitPixelCoordinates(xMin << zDiff, yMin << zDiff, xMax << zDiff, yMax << zDiff); // uses: getWidth(), getHeight()
		setDisplayToFitPixelCoordinates(xMin << zDiff, yMin << zDiff, (xMax << zDiff) - 1, (yMax << zDiff) - 1); // /W #???

		overviewImage = new BufferedImage(OVERVIEW_WIDTH, OVERVIEW_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		graphics = overviewImage.createGraphics();
	}
	
	@Override
	public int getHeight()
	{
		return OVERVIEW_HEIGHT;
	}

	@Override
	public int getWidth()
	{
		return OVERVIEW_WIDTH;
	}
	
	public void callPaintComponent_TWICE()
	{
		paintComponent(graphics); // init tile loading
		
		int nBreak = 7; // wait till tiles are loaded
		try
		{
	    //thread to sleep for the specified number of milliseconds
	    Thread.sleep(3000); //1000
		}
		catch ( java.lang.InterruptedException ie)
		{
	    System.out.println(ie + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		}
		
		paintComponent(graphics); // repaint();
		
		Point topLeftCoordinate = new Point(center.x - (getWidth() / 2), center.y - (getHeight() / 2));
		wgsGrid.paintWgsGrid(graphics, mapSource.getMapSpace(), topLeftCoordinate, zoom);
		
		save();
	}

	
	public void save()
	{
		try
		{
//			Toolkit.getDefaultToolkit().beep();
			
			String overviewFileName =Catalog.getCatalogOverviewFileName(catalog.getName());
			File overviewFile = new File(OSMCDSettings.getInstance().getCatalogsDirectory(), overviewFileName);
			ImageIO.write(overviewImage,"png", overviewFile);
		}
		catch(Exception e)
		{
			Exception p = e;
			int nBreak = 0;
		}
	}
}
