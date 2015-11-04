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

import osmb.mapsources.IfMapSource;
import osmb.program.tiles.Tile;
import osmb.program.tiles.Tile.TileState;

/**
 * This paints tiles having a transparent background over another layer of base tiles.
 * 
 * @author humbach
 */
public class OverlayMapTileLayer implements IfMapTileLayer
{
	protected JMapViewer mapViewer;
	protected IfMapSource mapSource;

	public OverlayMapTileLayer(JMapViewer mapViewer, IfMapSource tileSource)
	{
		this.mapViewer = mapViewer;
		this.mapSource = tileSource;
	}

	@Override
	public void startPainting(IfMapSource mapSource)
	{
	}

	/**
	 * This retrieves the tile image from the memory cache and paints it onto the specified Graphics at position (gx|gy).
	 */
	@Override
	public void paintTile(Graphics g, int gx, int gy, int tilex, int tiley, int zoom)
	{
		Tile tile = getTile(tilex, tiley, zoom);
		if (tile == null)
			return;
		tile.paintTransparent(g, gx, gy);
	}

	/**
	 * retrieves a tile from the cache. If the tile is not present in the cache a load job is added to the working queue
	 * of {@link JobThread}.
	 * 
	 * @param tilex
	 * @param tiley
	 * @param zoom
	 * @return specified tile from the cache or <code>null</code> if the tile was not found in the cache.
	 */
	protected Tile getTile(int tilex, int tiley, int zoom)
	{
		int max = (1 << zoom);
		if (tilex < 0 || tilex >= max || tiley < 0 || tiley >= max)
			return null;
		Tile tile = mapViewer.getTileImageCache().getTile(mapSource, tilex, tiley, zoom);
		if (tile == null)
		{
			tile = new Tile(mapSource, tilex, tiley, zoom);
			mapViewer.getTileImageCache().addTile(tile);
		}
		if (tile.getTileState() == TileState.TS_NEW)
		{
			// mapViewer.getJobDispatcher().addJob(mapViewer.getTileLoader().createTileLoaderJob(mapSource, tilex, tiley, zoom));
			mapViewer.getJobDispatcher().execute(mapViewer.getTileLoader().createTileLoaderJob(mapSource, tilex, tiley, zoom));
		}
		return tile;
	}
}
