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
/**
 * 
 */
package osmcbdef.mapsources.mappacks.openstreetmap;

import java.awt.image.BufferedImage;
import java.io.IOException;

import osmcbdef.exceptions.TileException;
import osmcbdef.mapsources.AbstractHttpMapSource;
import osmcbdef.mapsources.AbstractMultiLayerMapSource;
import osmcbdef.program.interfaces.HttpMapSource;
import osmcbdef.program.interfaces.MapSource;
import osmcbdef.program.model.TileImageType;

public class Hikebikemap extends AbstractMultiLayerMapSource {

	public Hikebikemap() {
		super("OpenStreetMap Hikebikemap.de", TileImageType.PNG);
		mapSources = new MapSource[] { new HikebikemapBase(), new HikebikemapRelief() };
		initializeValues();
	}

	/**
	 * http://hikebikemap.de/
	 */
	public static class HikebikemapBase extends AbstractHttpMapSource {

		public HikebikemapBase() {
			super("HikebikemapTiles", 0, 17, TileImageType.PNG, HttpMapSource.TileUpdate.None);
		}

		@Override
		public String toString() {
			return "OpenStreetMap Hikebikemap Map";
		}

		public String getTileUrl(int zoom, int tilex, int tiley) {
			return "http://toolserver.org/tiles/hikebike/" + zoom + "/" + tilex + "/" + tiley + ".png";
		}

	}

	/**
	 * Hill shades / relief
	 * 
	 * http://hikebikemap.de/
	 */
	public static class HikebikemapRelief extends AbstractHttpMapSource {

		public HikebikemapRelief() {
			super("HikebikemapRelief", 0, 17, TileImageType.PNG, HttpMapSource.TileUpdate.None);
		}

		public String getTileUrl(int zoom, int tilex, int tiley) {
			return "http://toolserver.org/~cmarqu/hill/" + zoom + "/" + tilex + "/" + tiley + ".png";
		}

		@Override
		public BufferedImage getTileImage(int zoom, int x, int y, LoadMethod loadMethod) throws IOException,
				TileException, InterruptedException {
			if (zoom > 16)
				return null;
			return super.getTileImage(zoom, x, y, loadMethod);
		}

	}
}