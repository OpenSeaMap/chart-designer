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

//License: GPL. Copyright 2008 by Jan Peter Stotz

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import osmb.mapsources.IfMapSource;
import osmb.program.JobDispatcher;
import osmb.program.map.IfMapSpace;
import osmb.program.tiles.IfTileLoaderListener;
import osmb.program.tiles.MemoryTileCache;
import osmb.program.tiles.Tile;
import osmb.program.tiles.TileLoader;
import osmb.utilities.OSMBUtilities;

/**
 * 
 * Provides a simple panel that displays rendered map tiles loaded from a specified map source.
 * 
 * @author Jan Peter Stotz
 * 
 */
public class JMapViewer extends JPanel implements IfTileLoaderListener
{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(JMapViewer.class);

	/**
	 * Vectors for clock-wise tile painting
	 */
	protected static final Point[] move =
	{ new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1) };

	protected TileLoader tileLoader;
	/**
	 * The mapTileLayers use this to actually paint the tiles on the graphics context
	 */
	protected MemoryTileCache tileCache;
	protected IfMapSource mMapSource;
	protected boolean usePlaceHolderTiles = true;

	protected boolean mapMarkersVisible;
	protected MapGridLayer mapGridLayer = null;

	protected List<IfMapTileLayer> mapTileLayers;
	public List<IfMapLayer> mapLayers;

	/**
	 * x- and y-position of the center of this map-panel on the world map denoted in screen pixel regarding the current zoom level.
	 * 
	 * ---- setting center to (width / 2 , height / 2)
	 * -> width (height) % 2 == 0 : center is right (bottom) pixel of (2 pixel)-center
	 * -> width (height) % 2 == 1 : center is (1 pixel)-center
	 */
	protected Point center = new Point();

	/**
	 * The minimum zoom level available for the currently displayed map. This will be modified when the map source is changed.
	 * It is the bigger of {@link IfMapSpace.MIN_TECH_ZOOM} and {@link IfMapSource.getMinZoom()}
	 */
	protected int mMinZoom = IfMapSpace.MIN_TECH_ZOOM;

	/**
	 * The maximum zoom level available for the currently displayed map. This will be modified when the map source is changed.
	 * It is the smaller of {@link IfMapSpace.MAX_TECH_ZOOM} and {@link IfMapSource.getMaxZoom()}
	 */
	protected int mMaxZoom = IfMapSpace.MAX_TECH_ZOOM;
	/**
	 * Current zoom level
	 */
	protected int mZoom;

	protected JSlider zoomSlider = new JSlider(0, 0);
	protected JButton zoomInButton;
	protected JButton zoomOutButton;
	/**
	 * The JobDispatcher is a 'normal' class, not a singleton any longer. {@link JobDispatcher}.
	 */
	protected JobDispatcher mJobDispatcher = null;

	public JMapViewer(IfMapSource defaultMapSource, int downloadThreadCount)
	{
		super();
		mapTileLayers = new LinkedList<IfMapTileLayer>();
		mapLayers = new LinkedList<IfMapLayer>();
		tileLoader = new TileLoader(this);
		tileCache = new MemoryTileCache();
		mapMarkersVisible = true;
		setLayout(null);
		setMapSource(defaultMapSource);
		initializeZoomSlider();
		setMinimumSize(new Dimension(IfMapSpace.TECH_TILESIZE, IfMapSpace.TECH_TILESIZE));
		setPreferredSize(new Dimension(5 * IfMapSpace.TECH_TILESIZE, 3 * IfMapSpace.TECH_TILESIZE));
		setDisplayPositionByLatLon(52.0, 7.0, 8);
		mJobDispatcher = new JobDispatcher(downloadThreadCount);
	}

	protected void initializeZoomSlider()
	{
		zoomSlider.setOrientation(JSlider.VERTICAL);
		zoomSlider.setBounds(10, 10, 30, 150);
		zoomSlider.setOpaque(false);
		zoomSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				setZoom(zoomSlider.getValue());
			}
		});
		add(zoomSlider);
		int size = 18;
		try
		{
			ImageIcon icon = OSMBUtilities.loadResourceImageIcon("plus.png");
			zoomInButton = new JButton(icon);
		}
		catch (Exception e)
		{
			zoomInButton = new JButton("+");
			zoomInButton.setFont(new Font("sansserif", Font.BOLD, 9));
			zoomInButton.setMargin(new Insets(0, 0, 0, 0));
		}
		zoomInButton.setBounds(4, 155, size, size);
		zoomInButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				zoomIn();
			}
		});
		add(zoomInButton);
		try
		{
			ImageIcon icon = OSMBUtilities.loadResourceImageIcon("minus.png");
			zoomOutButton = new JButton(icon);
		}
		catch (Exception e)
		{
			zoomOutButton = new JButton("-");
			zoomOutButton.setFont(new Font("sansserif", Font.BOLD, 9));
			zoomOutButton.setMargin(new Insets(0, 0, 0, 0));
		}
		zoomOutButton.setBounds(8 + size, 155, size, size);
		zoomOutButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				zoomOut();
			}
		});
		add(zoomOutButton);
	}

	/**
	 * Changes the map pane so that it is centered on the specified coordinate at the given zoom level.
	 * 
	 * @param lat
	 *          latitude of the specified coordinate
	 * @param lon
	 *          longitude of the specified coordinate
	 * @param zoom
	 *          {@link #mMinZoom} <= zoom level <= {@link #mMaxZoom}
	 */
	public void setDisplayPositionByLatLon(double lat, double lon, int zoom)
	{
		setDisplayPositionByLatLon(new Point(getWidth() / 2, getHeight() / 2), lat, lon, zoom);
	}

	/**
	 * Changes the map pane so that the specified coordinate at the given zoom level is displayed on the map at the screen coordinate <code>mapPoint</code>.
	 * 
	 * @param mapPoint
	 *          point on the map denoted in pixels where the coordinate should be set
	 * @param lat
	 *          latitude of the specified coordinate
	 * @param lon
	 *          longitude of the specified coordinate
	 * @param zoom
	 *          {@link #mMinZoom} <= zoom level <= {@link #mMaxZoom}
	 */
	public void setDisplayPositionByLatLon(Point mapPoint, double lat, double lon, int zoom)
	{
		zoom = Math.max(Math.min(zoom, mMapSource.getMaxZoom()), mMapSource.getMinZoom());
		IfMapSpace mapSpace = mMapSource.getMapSpace();
		int x = mapSpace.cLonToX(lon, zoom);
		int y = mapSpace.cLatToY(lat, zoom);
		setDisplayPosition(mapPoint, x, y, zoom);
	}

	public void setDisplayPosition(int x, int y, int zoom)
	{
		setDisplayPosition(new Point(getWidth() / 2, getHeight() / 2), x, y, zoom);
	}

	public void setDisplayPosition(Point mapPoint, int x, int y, int zoom)
	{
		if (zoom > mMapSource.getMaxZoom() || zoom < IfMapSpace.MAX_TECH_ZOOM)
			return;

		// Get the plain tile number
		Point p = new Point();
		p.x = x - mapPoint.x + getWidth() / 2;
		p.y = y - mapPoint.y + getHeight() / 2;
		center = p;
		setIgnoreRepaint(true);
		try
		{
			int oldZoom = this.mZoom;
			this.mZoom = zoom;
			if (oldZoom != zoom)
				zoomChanged(oldZoom);
			if (zoomSlider.getValue() != zoom)
				zoomSlider.setValue(zoom);
		}
		finally
		{
			setIgnoreRepaint(false);
			repaint();
		}
	}

	/**
	 * Sets the displayed iMap pane and zoom level so that the two points (x1/y1) and (x2/y2) visible. Please note that the coordinates have to be specified
	 * regarding {@link #MAX_ZOOM}.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void setDisplayToFitPixelCoordinates(int x1, int y1, int x2, int y2)
	{
		int mapZoomMax = mMapSource.getMaxZoom();
		int height = Math.max(0, getHeight());
		int width = Math.max(0, getWidth());
		int newZoom = IfMapSpace.MAX_TECH_ZOOM;
		int x = Math.abs(x1 - x2);
		int y = Math.abs(y1 - y2);
		// while (x > width || y > height || newZoom > mapZoomMax) // /W >=
		while (x >= width || y >= height || newZoom > mapZoomMax)
		{
			newZoom--;
			x >>= 1;
			y >>= 1;
		}

		// Do not select a zoom level that is unsupported by the current iMap
		// source
		newZoom = Math.max(mMapSource.getMinZoom(), Math.min(mMapSource.getMaxZoom(), newZoom));

		x = Math.min(x2, x1) + Math.abs(x1 - x2) / 2;
		y = Math.min(y2, y1) + Math.abs(y1 - y2) / 2;
		int z = 1 << (IfMapSpace.MAX_TECH_ZOOM - newZoom);
		x /= z;
		y /= z;
		// setDisplayPosition(x, y, newZoom); // /W +1, +1
		setDisplayPosition(x + 1, y + 1, newZoom); // set center: see protected Point center = new Point();
	}

	public Point2D.Double getPosition()
	{
		IfMapSpace mapSpace = mMapSource.getMapSpace();
		double lon = mapSpace.cXToLon(center.x, mZoom);
		double lat = mapSpace.cYToLat(center.y, mZoom);
		return new Point2D.Double(lat, lon);
	}

	public Point2D.Double getPosition(Point mapPoint)
	{
		IfMapSpace mapSpace = mMapSource.getMapSpace();
		int x = center.x + mapPoint.x - getWidth() / 2;
		int y = center.y + mapPoint.y - getHeight() / 2;
		double lon = mapSpace.cXToLon(x, mZoom);
		double lat = mapSpace.cYToLat(y, mZoom);
		return new Point2D.Double(lat, lon);
	}

	/**
	 * Calculates the position on the iMap of a given coordinate
	 * 
	 * @param lat
	 * @param lon
	 * @return point on the iMap or <code>null</code> if the point is not visible
	 */
	public Point getMapPosition(double lat, double lon)
	{
		IfMapSpace mapSpace = mMapSource.getMapSpace();
		int x = mapSpace.cLonToX(lon, mZoom);
		int y = mapSpace.cLatToY(lat, mZoom);
		x -= center.x - getWidth() / 2;
		y -= center.y - getHeight() / 2;
		if (x < 0 || y < 0 || x > getWidth() || y > getHeight())
			return null;
		return new Point(x, y);
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		// if (mapIsMoving) {
		// mapIsMoving = false;
		// Doesn't look very pretty but is much more faster
		// g.copyArea(0, 0, getWidth(), getHeight(), -mapMoveX, -mapMoveY);
		// return;
		// }
		super.paintComponent(g);

		int iMove = 0;

		int tileSize = mMapSource.getMapSpace().getTileSize();

		int tilex = center.x / tileSize; // /W #??? center BR <-> UL 256
		int tiley = center.y / tileSize;
		int off_x = (center.x % tileSize);
		int off_y = (center.y % tileSize);

		int w2 = getWidth() / 2;
		int h2 = getHeight() / 2;
		int topLeftX = center.x - w2;
		int topLeftY = center.y - h2;

		int posx = w2 - off_x;
		int posy = h2 - off_y;

		int diff_left = off_x;
		int diff_right = tileSize - off_x;
		int diff_top = off_y;
		int diff_bottom = tileSize - off_y;

		boolean start_left = diff_left < diff_right;
		boolean start_top = diff_top < diff_bottom;

		if (start_top)
		{
			if (start_left)
				iMove = 2;
			else
				iMove = 3;
		}
		else
		{
			if (start_left)
				iMove = 1;
			else
				iMove = 0;
		} // calculate the visibility borders
		int x_min = -tileSize + 1; // /W + 1 inserted
		int y_min = -tileSize + 1; // /W + 1 inserted
		int x_max = getWidth() - 1; // /W - 1 inserted
		int y_max = getHeight() - 1; // /W - 1 inserted

		// paint the tiles in a spiral, starting from center of the map
		boolean painted = (mapTileLayers.size() > 0);
		for (IfMapTileLayer l : mapTileLayers)
		{
			l.startPainting(mMapSource);
		}
		int x = 0;
		while (painted)
		{
			painted = false;
			for (int i = 0; i < 4; i++)
			{
				if (i % 2 == 0)
					x++;
				for (int j = 0; j < x; j++)
				{
					if (x_min <= posx && posx <= x_max && y_min <= posy && posy <= y_max)
					{
						// tile is visible
						painted = true;
						for (IfMapTileLayer l : mapTileLayers)
						{
							l.paintTile(g, posx, posy, tilex, tiley, mZoom);
							// System.out.println(zoom + " xxxx " + tilex + " yyyy " + tiley);
						}
					}
					Point p = move[iMove];
					posx += p.x * tileSize;
					posy += p.y * tileSize;
					tilex += p.x;
					tiley += p.y;
				}
				iMove = (iMove + 1) % move.length;
			}
		}

		int bottomRightX = topLeftX + getWidth();
		int bottomRightY = topLeftY + getHeight();
		try
		{
			for (IfMapLayer l : mapLayers)
			{
				l.paint(this, g, mZoom, topLeftX, topLeftY, bottomRightX, bottomRightY);
			}
		}
		catch (ConcurrentModificationException e)
		{
			// This may happen when multiple GPX files are loaded at once and in the mean time the iMap view is
			// repainted.
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					JMapViewer.this.repaint();
				}
			});
		}

		// outer border of the iMap
		int mapSize = tileSize << mZoom;
		g.setColor(Color.BLACK);
		g.drawRect(w2 - center.x, h2 - center.y, mapSize, mapSize);

		// g.drawString("Tiles in cache: " + tileCache.getTileCount(), 50, 20);
	}

	/**
	 * Moves the visible iMap pane.
	 * 
	 * @param x
	 *          horizontal movement in pixel.
	 * @param y
	 *          vertical movement in pixel
	 */
	public void moveMap(int x, int y)
	{
		center.x += x;
		center.y += y;
		repaint();
	}

	/**
	 * @return the current zoom level
	 */
	public int getZoom()
	{
		return mZoom;
	}

	/**
	 * Increases the current zoom level by one
	 */
	public void zoomIn()
	{
		setZoom(mZoom + 1);
	}

	/**
	 * Increases the current zoom level by one
	 */
	public void zoomIn(Point mapPoint)
	{
		setZoom(mZoom + 1, mapPoint);
	}

	/**
	 * Decreases the current zoom level by one
	 */
	public void zoomOut()
	{
		setZoom(mZoom - 1);
	}

	/**
	 * Decreases the current zoom level by one
	 */
	public void zoomOut(Point mapPoint)
	{
		setZoom(mZoom - 1, mapPoint);
	}

	public void setZoom(int zoom, Point mapPoint)
	{
		if (zoom > mMapSource.getMaxZoom() || zoom < mMapSource.getMinZoom() || zoom == this.mZoom)
			return;
		Point2D.Double zoomPos = getPosition(mapPoint);
		mJobDispatcher.cancelOutstandingJobs(); // Clearing outstanding load
		// requests
		setDisplayPositionByLatLon(mapPoint, zoomPos.x, zoomPos.y, zoom);
	}

	public void setZoom(int zoom)
	{
		setZoom(zoom, new Point(getWidth() / 2, getHeight() / 2));
		repaint();
	}

	/**
	 * Every time the zoom level changes this method is called. Override it in derived implementations for adapting zoom dependent values. The new zoom level can
	 * be obtained via {@link #getZoom()}.
	 * 
	 * @param oldZoom
	 *          the previous zoom level
	 */
	protected void zoomChanged(int oldZoom)
	{
		zoomSlider.setToolTipText("Zoom level " + mZoom);
		zoomInButton.setToolTipText("Zoom to level " + (mZoom + 1));
		zoomOutButton.setToolTipText("Zoom to level " + (mZoom - 1));
		zoomOutButton.setEnabled(mZoom > mMapSource.getMinZoom());
		zoomInButton.setEnabled(mZoom < mMapSource.getMaxZoom());
	}

	public boolean isTileGridVisible()
	{
		return (mapGridLayer != null);
	}

	public void setTileGridVisible(boolean tileGridVisible)
	{
		if (isTileGridVisible() == tileGridVisible)
			return;
		if (tileGridVisible)
		{
			mapGridLayer = new MapGridLayer();
			addMapTileLayers(mapGridLayer);
		}
		else
		{
			removeMapTileLayers(mapGridLayer);
			mapGridLayer = null;
		}
		repaint();
	}

	public boolean getMapMarkersVisible()
	{
		return mapMarkersVisible;
	}

	public void setZoomControlsVisible(boolean visible)
	{
		zoomSlider.setVisible(visible);
		zoomInButton.setVisible(visible);
		zoomOutButton.setVisible(visible);
	}

	public boolean getZoomContolsVisible()
	{
		return zoomSlider.isVisible();
	}

	@Override
	public MemoryTileCache getTileImageCache()
	{
		return tileCache;
	}

	public TileLoader getTileLoader()
	{
		return tileLoader;
	}

	public IfMapSource getMapSource()
	{
		return mMapSource;
	}

	public void setMapSource(IfMapSource mapSource)
	{
		this.mMapSource = mapSource;
		mMinZoom = Math.max(mapSource.getMinZoom(), IfMapSpace.MIN_TECH_ZOOM);
		zoomSlider.setMinimum(mMinZoom);
		mMaxZoom = Math.min(mapSource.getMaxZoom(), IfMapSpace.MAX_TECH_ZOOM);
		zoomSlider.setMaximum(mMaxZoom);
		// mJobDispatcher.cancelOutstandingJobs();
		if (mZoom > mMaxZoom)
			setZoom(mMaxZoom);
		if (mZoom < mMinZoom)
			setZoom(mMinZoom);
		mapTileLayers.clear();
		log.info("Map layer changed to: " + mapSource);
		mapTileLayers.add(new DefaultMapTileLayer(this, mapSource));
		if (mapGridLayer != null)
			mapTileLayers.add(mapGridLayer);
		repaint();
	}

	public JobDispatcher getJobDispatcher()
	{
		return mJobDispatcher;
	}

	public boolean isUsePlaceHolderTiles()
	{
		return usePlaceHolderTiles;
	}

	/**
	 * The loader has finished to retrieve the tile. Place it in the tile cache.
	 * 
	 */
	@Override
	public void tileLoadingFinished(Tile tile, boolean success)
	{
		if (success)
			tileCache.addTile(tile);
		repaint();
	}

	public void addMapTileLayers(IfMapTileLayer mapTileLayer)
	{
		mapTileLayers.add(mapTileLayer);
	}

	public void removeMapTileLayers(IfMapTileLayer mapTileLayer)
	{
		mapTileLayers.remove(mapTileLayer);
	}

	/**
	 * @return the maximum zoom level
	 */
	public int getMaxZoom()
	{
		return mMaxZoom;
	}

	/**
	 * @param maxZoom
	 *          the maximum zoom level to set
	 */
	public void setMaxZoom(int maxZoom)
	{
		mMaxZoom = maxZoom;
	}

	/**
	 * @return the minimum zoom level
	 */
	public int getMinZoom()
	{
		return mMinZoom;
	}

	/**
	 * @param mMinZoom
	 *          the minimum zoom level to set
	 */
	public void setMinZoom(int minZoom)
	{
		mMinZoom = minZoom;
	}

}