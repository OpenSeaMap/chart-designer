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
package osmcd.gui.mapview;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import osmb.mapsources.ACMapSourcesManager;
import osmb.mapsources.IfMapSource;
import osmb.mapsources.IfMapSourceTextAttribution;
import osmb.mapsources.MP2MapSpace;
import osmb.mapsources.PixelAddress;
import osmb.program.WgsGrid;
import osmb.utilities.MyMath;
import osmb.utilities.geo.GeoCoordinate;
import osmcd.OSMCDSettings;
import osmcd.OSMCDStrs;
import osmcd.program.Bookmark;
import osmcd.program.MapSelection;

public class PreviewMap extends JMapViewer
{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(PreviewMap.class);

	public static final Color GRID_COLOR = new Color(0.7f, 0.1f, 0.1f, 0.4f);
	public static final Color SEL_COLOR = new Color(0.9f, 0.7f, 0.7f, 0.6f);
	public static final Color MAP_COLOR = new Color(1.0f, 0.84f, 0.0f, 0.1f);

	public static final int MAP_CONTROLLER_RECTANGLE_SELECT = 0;
	public static final int MAP_CONTROLLER_GPX = 1;

	protected static final Font LOADING_FONT = new Font("Sans Serif", Font.BOLD, 30);
	/**
	 * Interactive map selection max/min pixel coordinates regarding zoom level <code>mMaxZoom</code>
	 */
	private Point iSelectionMin;
	private Point iSelectionMax;

	/**
	 * Map selection max/min pixel coordinates regarding zoom level <code>mMaxZoom</code> with respect to the grid zoom.
	 */
	private Point gridSelectionStart;
	private Point gridSelectionEnd;

	/**
	 * Pre-painted transparent tile with grid lines on it. This makes painting the grid a lot faster in difference to painting each line or rectangle if the grid
	 * zoom is much higher that the current zoom level.
	 */
	private BufferedImage gridTile = new BufferedImage(MP2MapSpace.TECH_TILESIZE, MP2MapSpace.TECH_TILESIZE, BufferedImage.TYPE_INT_ARGB);//  W #mapSpace (IfMapSpace.TECH_TILESIZE, IfMapSpace.TECH_TILESIZE, BufferedImage.TYPE_INT_ARGB);

	protected LinkedList<IfMapEventListener> mapEventListeners = new LinkedList<IfMapEventListener>();

	protected ACMapController mapKeyboardController;
	protected ACMapController mapSelectionController;
	protected DefaultMapController defaultMapController;

	private final WgsGrid wgsGrid = new WgsGrid(OSMCDSettings.getInstance().getWgsGrid(), this);
	private int gridZoom = -1;
	private int gridSize;

	public PreviewMap()
	{
		super(ACMapSourcesManager.getInstance().getDefaultMapSource(), 5);
		setEnabled(false);
		defaultMapController = new DefaultMapController(this);
		mapMarkersVisible = false;

		mapKeyboardController = new MapKeyboardController(this, true);
		setMapSelectionController(new RectangleSelectionMapController(this));
		log.trace("PreviewMap() constructed");
	}

	public void setDisplayPositionByLatLon(GeoCoordinate c, int zoom)
	{
		setDisplayPositionByLatLon(new Point(getWidth() / 2, getHeight() / 2), c.lat, c.lon, zoom);
	}

	/**
	 * Updates the current position in {@link OSMCDSettings} to the current view
	 */
	public void settingsSave()
	{
		OSMCDSettings settings = OSMCDSettings.getInstance();
		settings.setMapviewZoom(getZoom());
		settings.setMapviewCenterCoordinate(getCenterCoordinate());
		settings.setMapviewGridZoom(gridZoom);
		settings.setMapviewMapSource(mMapSource.getName());
		settings.setMapviewSelectionMin(iSelectionMin);
		settings.setMapviewSelectionMax(iSelectionMax);
	}

	/**
	 * Sets the current view by the current values from {@link OSMCDSettings}
	 */
	public void settingsLoad()
	{
		OSMCDSettings settings = OSMCDSettings.getInstance();
		IfMapSource mapSource = ACMapSourcesManager.getInstance().getSourceByName(settings.getMapviewMapSource());
		if (mapSource != null)
			setMapSource(mapSource);
		else // W
			mapSource = ACMapSourcesManager.getInstance().getDefaultMapSource();
		GeoCoordinate c = settings.getMapviewCenterCoordinate();
		gridZoom = settings.getMapviewGridZoom();
		setDisplayPositionByLatLon(c, settings.getMapviewZoom());
		setSelectionByPixelCoordinate(mapSource.getMaxZoom(), settings.getMapviewSelectionMin(), settings.getMapviewSelectionMax(), true);
	}

	@Override
	public void setMapSource(IfMapSource newMapSource)
	{
		if (newMapSource.equals(mMapSource))
			return;
		log.trace("Preview map source changed from '" + mMapSource + "' to '" + newMapSource + "'");
		super.setMapSource(newMapSource);
		if (mapEventListeners == null)
			return;
		// for (MapEventListener listener: mapEventListeners)
		// listener.mapSourceChanged(mapSource);
		log.trace("setMapSource()");
	}

	@Override
	protected void zoomChanged(int oldZoom)
	{
		log.trace("Preview map zoom changed from " + oldZoom + " to " + mZoom);
		if (mapEventListeners != null)
			for (IfMapEventListener listener : mapEventListeners)
				listener.zoomChanged(mZoom);
		updateGridValues();
	}

	public void setGridZoom(int gridZoom)
	{
		if (gridZoom == this.gridZoom)
			return;
		this.gridZoom = gridZoom;
		updateGridValues();
		applyGridOnSelection();
		updateMapSelection();
		repaint();
	}

	public int getGridZoom()
	{
		return gridZoom;
	}

	/**
	 * Updates the <code>gridSize</code> and the <code>gridTile</code>. This method has to be called if <code>mapSource</code> or <code>zoom</code> has been
	 * changed.
	 */
	protected void updateGridValues()
	{
		log.trace("called");
		if (gridZoom < 0)
			return;
		int zoomToGridZoom = mZoom - gridZoom;
		int tileSize = MP2MapSpace.getTileSize(); // #mapSpace  mMapSource.getMapSpace().getTileSize();
		if (zoomToGridZoom > 0)
		{
			gridSize = tileSize << zoomToGridZoom;
			gridTile = null;
		}
		else
		{
			gridSize = tileSize >> (-zoomToGridZoom);
			BufferedImage newGridTile = null;
			if (gridSize > 2)
			{
				newGridTile = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = newGridTile.createGraphics();
				float alpha = 5 + (6 + zoomToGridZoom) * 16;
				alpha = Math.max(0, alpha);
				alpha = Math.min(130, alpha);
				// g.setColor(new Color(200, 20, 20, alpha));
				g.setColor(new Color(0.7f, 0.1f, 0.1f, alpha / 256.0f));
				for (int x = 0; x < tileSize; x += gridSize)
					g.drawLine(x, 0, x, tileSize - 1);
				for (int y = 0; y < tileSize; y += gridSize)
					g.drawLine(0, y, tileSize - 1, y);
			}
			gridTile = newGridTile;
		}
	}

	/**
	 * This paints the grids. The actual map is painted in the {@link IfMapTileLayer} implementations. Usually these are {@link DefaultMapTileLayer} or
	 * {@link OverlayMapTileLayer}.
	 * 
	 */
	@Override
	protected void paintComponent(Graphics graphics)
	{
		if (!isEnabled())
		{
			graphics.setFont(LOADING_FONT);
			graphics.drawString(OSMCDStrs.RStr("map_loading_wait"), 100, 100);
			return;
		}
		if (mMapSource == null)
			return;
		Graphics2D g = (Graphics2D) graphics;
		super.paintComponent(g);

		Point tlc = getTopLeftCoordinate();
		if (gridZoom >= 0)
		{
			// Paint grid only if it is enabled (gridZoom not -1)
			int max = (256 << mZoom);
			int w = Math.min(getWidth(), max - tlc.x);
			int h = Math.min(getHeight(), max - tlc.y);
			g.setColor(GRID_COLOR);
			// g.setStroke(new BasicStroke(4.0f));
			if (gridSize > 1)
			{
				int tilesize = MP2MapSpace.getTileSize(); // #mapSpace  mMapSource.getMapSpace().getTileSize();
				if (gridSize >= tilesize)
				{
					int off_x = tlc.x < 0 ? -tlc.x : -(tlc.x % gridSize);
					int off_y = tlc.y < 0 ? -tlc.y : -(tlc.y % gridSize);
					for (int x = off_x; x <= w; x += gridSize)
					{
						g.drawLine(x, off_y, x, h);
					}
					for (int y = off_y; y <= h; y += gridSize)
					{
						g.drawLine(off_x, y, w, y);
					}
				}
				else
				{
					int off_x = (tlc.x < 0) ? tlc.x : tlc.x % tilesize;
					int off_y = (tlc.y < 0) ? tlc.y : tlc.y % tilesize;
					for (int x = -off_x; x < w; x += 256)
					{
						for (int y = -off_y; y < h; y += 256)
						{
							g.drawImage(gridTile, x, y, null);
						}
					}
				}
			}
		}
		if (gridSelectionStart != null && gridSelectionEnd != null)
		{
			// Mark the selected area aligned with the current grid (reddish marker)
			int zoomDiff = getMaxZoom() - mZoom;
			int x_min = (gridSelectionStart.x >> zoomDiff) - tlc.x;
			int y_min = (gridSelectionStart.y >> zoomDiff) - tlc.y;
			int x_max = (gridSelectionEnd.x >> zoomDiff) - tlc.x;
			int y_max = (gridSelectionEnd.y >> zoomDiff) - tlc.y;

			int w = x_max - x_min + 1;
			int h = y_max - y_min + 1;
			g.setColor(SEL_COLOR);
			g.fillRect(x_min, y_min, w, h);
		}
		if (iSelectionMin != null && iSelectionMax != null)
		{
			// Draw the selection border exactly as it has been specified by the user
			int zoomDiff = getMaxZoom() - mZoom;
			int x_min = (iSelectionMin.x >> zoomDiff) - tlc.x;
			int y_min = (iSelectionMin.y >> zoomDiff) - tlc.y;
			int x_max = (iSelectionMax.x >> zoomDiff) - tlc.x;
			int y_max = (iSelectionMax.y >> zoomDiff) - tlc.y;

			int w = x_max - x_min + 1;
			int h = y_max - y_min + 1;
			g.setColor(GRID_COLOR);
			g.drawRect(x_min, y_min, w, h);
		}
		if (mMapSource instanceof IfMapSourceTextAttribution)
		{
			IfMapSourceTextAttribution ta = (IfMapSourceTextAttribution) mMapSource;
			String attributionText = ta.getAttributionText();
			if (attributionText != null)
			{
				Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(attributionText, g);
				int text_x = getWidth() - 10 - (int) stringBounds.getWidth();
				int text_y = getHeight() - 1 - (int) stringBounds.getHeight();
				g.setColor(Color.black);
				g.drawString(attributionText, text_x + 1, text_y + 1);
				g.setColor(Color.white);
				g.drawString(attributionText, text_x, text_y);
			}
		}
		if (OSMCDSettings.getInstance().getWgsGrid().enabled)
		{
			wgsGrid.paintWgsGrid(g, tlc, mZoom); // W #mapSpace (g, mMapSource.getMapSpace(), tlc, mZoom);
		}
		ScaleBar.paintScaleBar(this, g, tlc, mZoom); // W #mapSpace (this, g, mMapSource.getMapSpace(), tlc, mZoom);
	}

	public Bookmark getPositionBookmark()
	{
		return new Bookmark(mMapSource, mZoom, center.x, center.y);
	}

	public void gotoPositionBookmark(Bookmark bookmark)
	{
		setMapSource(bookmark.getMapSource());
		setDisplayPositionByLatLon(bookmark, bookmark.getZoom());
		setZoom(bookmark.getZoom());
	}

	/**
	 * @return Coordinate of the point in the center of the currently displayed map region
	 */
	public GeoCoordinate getCenterCoordinate()
	{
		double lon = MP2MapSpace.cXToLonLeftBorder(center.x, mZoom);
		double lat = MP2MapSpace.cYToLatUpperBorder(center.y, mZoom);
		return new GeoCoordinate(lat, lon);
	}

	/**
	 * @return Coordinate of the top left corner visible regarding the current map source (pixel)
	 */
	public Point getTopLeftCoordinate()
	{
		return new Point(center.x - (getWidth() / 2), center.y - (getHeight() / 2));
	}

	public void zoomTo(MapSelection ms)
	{
		if (!ms.isAreaSelected())
			return;
		log.trace("Setting selection to: " + ms);
		Point max = ms.getBottomRightPixelCoordinate(getMaxZoom());
		Point min = ms.getTopLeftPixelCoordinate(getMaxZoom());
		setDisplayToFitPixelCoordinates(max.x, max.y, min.x, min.y);
	}

	/**
	 * Zooms to the specified {@link MapSelection} and sets the selection to it;
	 * 
	 * @param ms
	 * @param notifyListeners
	 */
	public void setSelectionAndZoomTo(MapSelection ms, boolean notifyListeners)
	{
		log.trace("Setting selection to: " + ms);
		Point max = ms.getBottomRightPixelCoordinate(getMaxZoom());
		Point min = ms.getTopLeftPixelCoordinate(getMaxZoom());
		setDisplayToFitPixelCoordinates(max.x, max.y, min.x, min.y);
		Point pStart = ms.getTopLeftPixelCoordinate(mZoom);
		Point pEnd = ms.getBottomRightPixelCoordinate(mZoom);
		setSelectionByPixelCoordinate(pStart, pEnd, notifyListeners);
	}

	/**
	 * 
	 * @param pStart
	 *          x/y tile coordinate of the top left tile regarding the current zoom level
	 * @param pEnd
	 *          x/y tile coordinate of the bottom right tile regarding the current zoom level
	 * @param notifyListeners
	 */
	public void setSelectionByPixelCoordinate(Point pStart, Point pEnd, boolean notifyListeners)
	{
		setSelectionByPixelCoordinate(mZoom, pStart, pEnd, notifyListeners);
	}

	/**
	 * Sets the rectangular selection to the absolute tile coordinates <code>pStart</code> and <code>pEnd</code> regarding the zoom-level <code>cZoom</code>.
	 * 
	 * @param cZoom
	 * @param pStart
	 * @param pEnd
	 * @param notifyListeners
	 */
	public void setSelectionByPixelCoordinate(int cZoom, Point pStart, Point pEnd, boolean notifyListeners)
	{
		if (pStart == null || pEnd == null)
		{
			iSelectionMin = null;
			iSelectionMax = null;
			gridSelectionStart = null;
			gridSelectionEnd = null;
			return;
		}

		Point pNewStart = new Point();
		Point pNewEnd = new Point();
		int mapMaxCoordinate = MP2MapSpace.getSizeInPixel(cZoom) - 1; // W #mapSpace mMapSource.getMapSpace().getMaxPixels(cZoom) - 1;
		// Sort x/y coordinate of points so that pNewStart < pnewEnd and limit selection to iMap size
		pNewStart.x = Math.max(0, Math.min(mapMaxCoordinate, Math.min(pStart.x, pEnd.x)));
		pNewStart.y = Math.max(0, Math.min(mapMaxCoordinate, Math.min(pStart.y, pEnd.y)));
		pNewEnd.x = Math.max(0, Math.min(mapMaxCoordinate, Math.max(pStart.x, pEnd.x)));
		pNewEnd.y = Math.max(0, Math.min(mapMaxCoordinate, Math.max(pStart.y, pEnd.y)));

		int zoomDiff = getMaxZoom() - cZoom;

		// /W pNewEnd.x <<= zoomDiff;
		pNewEnd.x = ((pNewEnd.x + 1) << zoomDiff) - 1;
		// /W pNewEnd.y <<= zoomDiff;
		pNewEnd.y = ((pNewEnd.y + 1) << zoomDiff) - 1;
		pNewStart.x <<= zoomDiff;
		pNewStart.y <<= zoomDiff;

		iSelectionMin = pNewStart;
		iSelectionMax = pNewEnd;
		gridSelectionStart = null;
		gridSelectionEnd = null;

		updateGridValues();
		applyGridOnSelection();

		if (notifyListeners)
			updateMapSelection();
		repaint();
	}

	protected void applyGridOnSelection()
	{
		if (gridZoom < 0)
		{
			gridSelectionStart = iSelectionMin;
			gridSelectionEnd = iSelectionMax;
			return;
		}

		if (iSelectionMin == null || iSelectionMax == null)
			return;

		int gridZoomDiff = getMaxZoom() - gridZoom;
		int gridFactor = MP2MapSpace.getTileSize() << gridZoomDiff; // W #mapSpace mapSource.getMapSpace().getTileSize();mMapSource.getMapSpace().getTileSize() << gridZoomDiff;

		Point pNewStart = new Point(iSelectionMin);
		Point pNewEnd = new Point(iSelectionMax);

		// Snap to the current grid

		pNewStart.x = MyMath.roundDownToNearest(pNewStart.x, gridFactor);
		pNewStart.y = MyMath.roundDownToNearest(pNewStart.y, gridFactor);
		pNewEnd.x = MyMath.roundUpToNearest(pNewEnd.x, gridFactor) - 1;
		pNewEnd.y = MyMath.roundUpToNearest(pNewEnd.y, gridFactor) - 1;

		gridSelectionStart = pNewStart;
		gridSelectionEnd = pNewEnd;
	}

	/**
	 * Notifies all registered {@link IfMapEventListener} of a {@link IfMapEventListener#selectionChanged(MercatorPixelCoordinate, MercatorPixelCoordinate)}
	 * event.
	 */
	public void updateMapSelection()
	{
		int x_min, y_min, x_max, y_max;

		if (gridZoom >= 0)
		{
			if (gridSelectionStart == null || gridSelectionEnd == null)
				return;
			x_min = gridSelectionStart.x;
			y_min = gridSelectionStart.y;
			x_max = gridSelectionEnd.x;
			y_max = gridSelectionEnd.y;
		}
		else
		{
			if (iSelectionMin == null || iSelectionMax == null)
				return;
			x_min = iSelectionMin.x;
			y_min = iSelectionMin.y;
			x_max = iSelectionMax.x;
			y_max = iSelectionMax.y;
		}
		//W #mapSpace
		PixelAddress min = new PixelAddress(x_min, y_min, getMaxZoom());
		PixelAddress max = new PixelAddress( x_max, y_max, getMaxZoom());
		// log.debug("sel min: [" + min + "]");
		// log.debug("sel max: [" + max + "]");
		for (IfMapEventListener listener : mapEventListeners)
			listener.selectionChanged(max, min);
	}

	public void addMapEventListener(IfMapEventListener l)
	{
		mapEventListeners.add(l);
	}

	public void selectPreviousMap()
	{
		for (IfMapEventListener listener : mapEventListeners)
		{
			listener.selectPreviousMapSource();
		}
	}

	public void selectNextMap()
	{
		for (IfMapEventListener listener : mapEventListeners)
		{
			listener.selectPreviousMapSource();
		}
	}

	/**
	 * Clears the in-memory tile cache and performs a repaint which causes a reload of all displayed tiles (from disk or if not present from the iMap source via
	 * network).
	 */
	public void refreshMap()
	{
		mTileCache.clear();
		repaint();
	}

	public ACMapController getMapKeyboardController()
	{
		return mapKeyboardController;
	}

	/**
	 * @return Currently active <code>mapSelectionController</code>
	 */
	public ACMapController getMapSelectionController()
	{
		return mapSelectionController;
	}

	/**
	 * Sets a new mapSelectionController. Previous controller are disabled and removed.
	 * 
	 * @param mapSelectionController
	 */
	public void setMapSelectionController(ACMapController mapSelectionController)
	{
		if (this.mapSelectionController != null)
			this.mapSelectionController.disable();
		this.mapSelectionController = mapSelectionController;
		mapSelectionController.enable();
		for (IfMapEventListener listener : mapEventListeners)
		{
			listener.mapSelectionControllerChanged(mapSelectionController);
		}
		repaint();
	}
}