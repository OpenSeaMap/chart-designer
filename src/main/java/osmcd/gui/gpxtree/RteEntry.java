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
import osmcd.data.gpx.gpx11.RteType;
import osmcd.gui.mapview.GpxLayer;

public class RteEntry extends GpxEntry {
	private RteType rte;

	public RteEntry(RteType rte, GpxLayer layer) {
		this.setRte(rte);
		this.setLayer(layer);
		this.setWaypointParent(true);
	}

	public String toString() {
		String name = "";
		try {
			name = getRte().getName();
		} catch (NullPointerException e) {
			// no name set
		}
		if (name != null && !name.equals("")) {
			return name;
		} else {
			return OSMCDStrs.RStr("rp_gpx_unname_route_name");
		}
	}

	private void setRte(RteType rte) {
		this.rte = rte;
	}

	public RteType getRte() {
		return rte;
	}
}
