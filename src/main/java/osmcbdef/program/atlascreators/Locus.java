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

import java.sql.SQLException;
import java.sql.Statement;

import osmcbdef.program.annotations.AtlasCreatorName;

@AtlasCreatorName(value = "Locus")
public class Locus extends RMapsSQLite {

	private static final String INFO_TABLE = "CREATE TABLE info (minzoom INTEGER, maxzoom INTEGER, "
			+ "center_x DOUBLE, center_y DOUBLE, zooms TEXT, provider INTEGER)";

	@Override
	protected void createInfoTable(Statement stat) throws SQLException {
		stat.executeUpdate(INFO_TABLE);
	}

}
