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

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import osmcd.gui.MainGUI;
import osmcd.gui.SplashFrame;
import osmcd.mapsources.DefaultMapSourcesManager;
import osmcd.program.DirectoryManager;
import osmcd.program.EnvironmentSetup;
import osmcd.program.Logging;
import osmcd.program.ProgramInfo;
import osmcd.program.commandline.CommandLineEmpty;
import osmcd.program.interfaces.CommandLineAction;
import osmcd.program.model.Settings;
import osmcd.program.tilestore.TileStore;
import osmcd.utilities.GUIExceptionHandler;

/**
 * Java 6 version of the main starter class. Detect operating system to decide user interface
 */
public class Main
{
	protected CommandLineAction cmdAction = new CommandLineEmpty();

	// protected OSType osType = "Win";

	public Main() {
		try
		{
			parseCommandLine();
			if (cmdAction.showSplashScreen())
				SplashFrame.showFrame();

			// init the default dirs
			DirectoryManager.initialize();
			Logging.configureLogging();

			// MySocketImplFactory.install();
			ProgramInfo.initialize(); // Load revision info
			Logging.logSystemInfo();
			GUIExceptionHandler.installToolkitEventQueueProxy();
			// Logging.logSystemProperties();
			ImageIO.setUseCache(false);

			// try to read settings.xml and check file/dir settings therein
			EnvironmentSetup.checkSettingsSetup();
			Settings.loadOrQuit();
			EnvironmentSetup.checkFileSetup();
			EnvironmentSetup.checkMemory();

			// Setup mappacks
			EnvironmentSetup.copyMapPacks();
			DefaultMapSourcesManager.initialize();
			TileStore.initialize();
			// EnvironmentSetup.upgrade();
			cmdAction.runBeforeMainGUI();
			if (cmdAction.showMainGUI())
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						Logging.LOG.debug("Starting GUI");
						MainGUI.createMainGui();
						SplashFrame.hideFrame();
						cmdAction.runMainGUI();
					}
				});
			}
		}
		catch (Throwable t)
		{
			GUIExceptionHandler.processException(t);
			System.exit(1);
		}
	}

	protected void parseCommandLine()
	{
		String[] args = StartOSMCD.ARGS;
		if (args.length >= 2)
		{
		}
	}

	/**
	 * Start OSMCD without Java Runtime version check
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new Main();
		}
		catch (Throwable t)
		{
			GUIExceptionHandler.processException(t);
		}
	}
}
