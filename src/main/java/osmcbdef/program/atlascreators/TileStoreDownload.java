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
package osmcbdef.program.atlascreators;

import osmcbdef.exceptions.MapCreationException;
import osmcbdef.program.annotations.AtlasCreatorName;
import osmcbdef.program.interfaces.MapSource;

@AtlasCreatorName(value = "Tile store download only", type = "TILESTORE")
public class TileStoreDownload extends AtlasCreator {

	public TileStoreDownload() {
	}

	@Override
	public boolean testMapSource(MapSource mapSource) {
		return true;
	}

	@Override
	public void createMap() throws MapCreationException, InterruptedException {
	}

}
