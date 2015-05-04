/*******************************************************************************
 * Copyright (c) OSMCD developers
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
package osmcd;

import javax.swing.JOptionPane;

import osmb.program.ACStarter;

/**
 * OSMCBApp class for starting OpenSeaMap ChartDefiner.
 * 
 * This class performs the Java Runtime version check and if the correct version is installed it creates a new instance of the class specified by
 * {@link #MAIN_CLASS}. The class to be instantiated is specified by it's name intentionally as this allows to compile this class without any further class
 * dependencies.
 * 
 */
public class StartOSMCD extends ACStarter
{
	static final String MAIN_CLASS = "osmcd.OSMCDApp";
	static protected OSMCDApp theApp;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		setLookAndFeel();
		checkJavaVersion();
		try
		{
			int nRet = 0;
			theApp = (OSMCDApp) Class.forName(MAIN_CLASS).newInstance();
			theApp.setArgs(args);
			if ((nRet = theApp.runWork()) < 0)
				System.exit(nRet);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, OSMCDStrs.RStr("msg_environment_unable_to_start") + e.getMessage(), OSMCDStrs.RStr("Error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
