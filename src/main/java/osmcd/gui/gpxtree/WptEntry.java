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
package osmcd.gui.gpxtree;

import osmcd.OSMCDStrs;
import osmcd.data.gpx.gpx11.WptType;
<<<<<<< HEAD
import osmcd.gui.mapview.GpxLayer;
=======
import osmcd.gui.mapview.layer.GpxLayer;
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318

public class WptEntry extends GpxEntry {
	private WptType wpt;

	public WptEntry(WptType wpt, GpxLayer layer) {
		this.wpt = wpt;
		this.setLayer(layer);
	}

	public String toString() {
		String name = "";
		try {
			name = wpt.getName();
		} catch (NullPointerException e) {
			// no name set
		}
		if (name != null && !name.equals("")) {
			return name;
		} else {
			return OSMCDStrs.RStr("rp_gpx_unname_wpt_name");
		}
	}

	public WptType getWpt() {
		return wpt;
	}
}
