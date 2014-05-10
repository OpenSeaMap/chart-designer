/*******************************************************************************
 * Copyright (c) OSM developers
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
 * 
 * This port is based on MobAC
 ******************************************************************************/
package osmcbdef.mapsources.mappacks.openstreetmap;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import osmcbdef.exceptions.TileException;

public class OpenStreetMapDE extends AbstractOsmMapSource {

	private static final String MAP_OSMDE = "http://a.tile.openstreetmap.de/tiles/osmde/";

	/**
	 * Maximum of 2 download threads
	 * 
	 * @see http://wiki.openstreetmap.org/wiki/Tile_usage_policy
	 */
	private static final Semaphore SEM = new Semaphore(2);

	public OpenStreetMapDE() {
		super("OpenStreetMapDE");
		maxZoom = 18;
	}

	@Override
	public byte[] getTileData(int zoom, int x, int y, LoadMethod loadMethod) throws IOException, TileException,
			InterruptedException {
		SEM.acquire();
		try {
			return super.getTileData(zoom, x, y, loadMethod);
		} finally {
			SEM.release();
		}
	}

	@Override
	public String getTileUrl(int zoom, int tilex, int tiley) {
		return MAP_OSMDE + super.getTileUrl(zoom, tilex, tiley);
	}

	public TileUpdate getTileUpdate() {
		return TileUpdate.IfNoneMatch;
	}

	@Override
	public String toString() {
		return "OpenStreetMap DE";
	}

}
