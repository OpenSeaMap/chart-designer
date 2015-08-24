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
package osmcd.program;

import org.apache.log4j.Logger;

<<<<<<< HEAD
public class Logging extends osmb.program.Logging
{
	protected static final String LOG_FILENAME = "OpenSeaMap ChartDesigner.log";
	public static final Logger LOG = Logger.getLogger("OSMCD");
=======
public class Logging extends osmcb.program.Logging
{
	protected static final String LOG_FILENAME = "OpenSeaMap ChartDesigner.log";
	public static final Logger LOG = Logger.getLogger("OSMCD");
	// public static final Layout ADVANCED_LAYOUT = new PatternLayout("%d{ISO8601} %-5p [%t] %c{1}: %m%n");

>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
}
