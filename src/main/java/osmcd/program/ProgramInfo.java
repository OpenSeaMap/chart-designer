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

import java.io.InputStream;
import java.util.Properties;

import osmb.utilities.GUIExceptionHandler;
import osmb.utilities.OSMBUtilities;
import osmcd.OSMCDApp;

public class ProgramInfo
{
	public static String PROG_NAME = "OpenSeaMap ChartDesigner";
	public static String PROG_NAME_SHORT = "OSMCD";
	private static String VERSION = null;
	private static String SVN_REVISION = "unknown";
	private static String userAgent = "";

	/**
	 * Show or hide the detailed revision info in the main windows title
	 */
	private static boolean titleHideRevision = false;

	public static void initialize()
	{
		InputStream propIn = OSMCDApp.class.getResourceAsStream("osmcd.properties");
		try
		{
			Properties props = new Properties();
			props.load(propIn);
			VERSION = props.getProperty("osmcd.version");
			titleHideRevision = Boolean.parseBoolean(props.getProperty("osmcd.revision.hide", "false"));
			System.getProperties().putAll(props);
		}
		catch (Exception e)
		{
			String msg = "Error reading osmcd.properties";
			GUIExceptionHandler.processFatalExceptionSimpleDialog(msg, e);
		}
		finally
		{
			OSMBUtilities.closeStream(propIn);
		}
		propIn = OSMCDApp.class.getResourceAsStream("osmcd-rev.properties");
		try
		{
			String rev;
			if (propIn != null)
			{
				Properties props = new Properties();
				props.load(propIn);
				rev = props.getProperty("osmcd.revision");
				SVN_REVISION = Integer.toString(OSMBUtilities.parseSVNRevision(rev));
			}
			else
			{
				rev = System.getProperty("osmcd.revision.fallback");
				SVN_REVISION = Integer.toString(OSMBUtilities.parseSVNRevision(rev)) + " exported";
			}
		}
		catch (Exception e)
		{
			Logging.LOG.error("Error reading osmcd-rev.properties", e);
		}
		finally
		{
			OSMBUtilities.closeStream(propIn);
		}
		userAgent = PROG_NAME_SHORT + "/" + (getVersion().replaceAll(" ", "_"));
	}

	public static String getVersion()
	{
		if (VERSION != null)
			return VERSION;
		else
			return "UNKNOWN";
	}

	public static String getRevisionStr()
	{
		return SVN_REVISION;
	}

	public static String getVersionTitle()
	{
		String title = PROG_NAME;
		if (PROG_NAME_SHORT != null)
			title += " (" + PROG_NAME_SHORT + ") ";
		else
			title += " ";
		if (VERSION != null)
		{
			title += getVersion();
		}
		else
			title += "unknown version";
		return title;
	}

	public static String getCompleteTitle()
	{
		String title = getVersionTitle();
		if (!titleHideRevision)
			title += " (" + SVN_REVISION + ")";
		return title;
	}

	public static String getUserAgent()
	{
		return userAgent;
	}
}
