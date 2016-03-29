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

import java.awt.Graphics;

import org.apache.log4j.Logger;

import osmb.mapsources.ACMapSource;
import osmb.mapsources.TileAddress;
import osmb.program.JobDispatcher;
import osmb.program.tiles.Tile;
import osmb.program.tiles.Tile.TileState;

/**
 * This is a layer to be displayed in a {@link JMapViewer}. It allows painting of a tile via {@link #paintTile}()
 */
public class DefaultMapTileLayer implements IfMapTileLayer
{
	private static Logger log = Logger.getLogger(DefaultMapTileLayer.class);

	protected JMapViewer mapViewer;
	protected ACMapSource mapSource;
	protected boolean usePlaceHolders;

	public DefaultMapTileLayer(JMapViewer mapViewer, ACMapSource mapSource)
	{
		this.mapViewer = mapViewer;
		this.mapSource = mapSource;
	}

	@Override
	public void startPainting(ACMapSource mapSource)
	{
		usePlaceHolders = mapViewer.isUsePlaceHolderTiles();
	}

	/**
	 * This retrieves the tile image from the memory cache and paints it onto the specified Graphics at position (gx|gy).
	 */
	@Override
	public void paintTile(Graphics g, int gx, int gy, int tilex, int tiley, int zoom)
	{
		TileAddress tAddr = new TileAddress(tilex, tiley, zoom);
		log.trace("start paint tile " + tAddr);
		Tile tile = getTile(tAddr);
		if (tile != null)
			tile.paint(g, gx, gy);
	}

	/**
	 * Retrieves a tile from the cache. If the tile is not present in the cache a load job is added to the working queue
	 * of {@link JobDispatcher}. The loader job will notify the mapViewer via {@link JMapViewer#tileLoadingFinished}() when it has done so.
	 * In the time between it places a placeholder in the cache instead of the tile to be loaded.
	 * 
	 * @param tilex
	 * @param tiley
	 * @param zoom
	 * @return specified tile from the cache or <code>null</code> if the tile was not found in the cache.
	 */
	protected Tile getTile(TileAddress tAddr)
	{
		Tile tile = null;
		log.trace("start get tile from mtc " + tAddr);
		int max = (1 << tAddr.getZoom());
		if (tAddr.getX() < 0 || tAddr.getX() >= max || tAddr.getY() < 0 || tAddr.getY() >= max)
		{
			log.debug("tile out of range: x=" + tAddr.getX() + ", y=" + tAddr.getY());
			return tile;
		}
		tile = mapViewer.getTileImageCache().getTile(mapSource, tAddr);
		if (tile == null)
		{
			tile = new Tile(mapSource, tAddr);
			tile = mapViewer.getTileImageCache().loadPlaceholderFromCache(tile);
			if (tile.getTileState() == TileState.TS_ZOOMED)
				mapViewer.getTileImageCache().addTile(tile);
		}
		if ((tile.getTileState() == TileState.TS_NEW) || (tile.getTileState() == TileState.TS_LOADING))
		{
			log.debug("queue load job for " + tile);
			mapViewer.getJobDispatcher().execute(mapViewer.getTileLoader().createTileLoaderJob(mapSource, tAddr));
			tile.setLoadingImage();
		}
		else
			log.debug("use found tile=" + tile + " from mtc");
		return tile;
	}

	/**
	 * use {@link #getTile(TileAddress)}
	 */
	@Deprecated
	protected Tile getTile(int tilex, int tiley, int zoom)
	{
		return getTile(new TileAddress(tilex, tiley, zoom));
	}
}
