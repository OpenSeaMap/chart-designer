<<<<<<< HEAD:src/main/java/osmcd/OSMCDApp.java
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

import osmb.mapsources.DefaultMapSourcesManager;
import osmb.program.ACWindowsApp;
import osmb.program.EnvironmentSetup;
import osmb.program.catalog.Catalog;
import osmb.program.tilestore.ACSiTileStore;
import osmb.utilities.GUIExceptionHandler;
import osmcd.gui.MainFrame;
import osmcd.gui.SplashFrame;
import osmcd.program.Logging;
import osmcd.program.ProgramInfo;

public class OSMCDApp extends ACWindowsApp
{
	protected Catalog gCatalog = null;

	static public OSMCDApp getApp()
	{
		return (OSMCDApp) gApp;
	}

	@Override
	public OSMCDSettings getSettings()
	{
		if (pSets == null)
			pSets = OSMCDSettings.getInstance();
		return (OSMCDSettings) pSets;
	}

	public OSMCDApp()
	{
		try
		{
			gApp = this;
		}
		catch (Throwable t)
		{
			System.exit(1);
		}
	}

	@Override
	public int runWork()
	{
		try
		{
			findProgramDir();
			parseCommandLine();
			if (showSplashScreen())
				SplashFrame.showFrame();

			// init the default dirs
			Logging.configureLogging(this);

			// MySocketImplFactory.install();
			ProgramInfo.initialize(); // Load revision info
			// Logging.logSystemInfo();
			GUIExceptionHandler.installToolkitEventQueueProxy();
			// Logging.logSystemProperties();
			ImageIO.setUseCache(false);

			// try to read settings.xml and check file/dir settings therein
			EnvironmentSetup.checkSettingsSetup();
			pSets = OSMCDSettings.loadOrQuit();
			EnvironmentSetup.checkFileSetup();
			EnvironmentSetup.checkMemory();

			Logging.logSystemInfo();

			// Setup mappacks
			// EnvironmentSetup.copyMapPacks();
			DefaultMapSourcesManager.initialize();
			ACSiTileStore.initialize(); // multiple instances are accessing tilestore ????
			// EnvironmentSetup.upgrade();
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					Logging.LOG.debug("Starting GUI");
					MainFrame.createMainGui();
					SplashFrame.hideFrame();

					// /W #firstStart
					MainFrame.runFirstStart();

					runMainGUI();
				}
			});
			return 0;
		}
		catch (Throwable t)
		{
			GUIExceptionHandler.processException(t);
			return -1;
		}
	}

	@Override
	protected void parseCommandLine()
	{
		if ((ARGS != null) && (ARGS.length >= 2))
		{
			Logging.LOG.debug("tried to call OSMCD with args.");
		}
	}

	@Override
	public void setArgs(String[] strArgs)
	{
		ARGS = strArgs;
	}

	@Override
	public void runMainGUI()
	{
	}
}
=======
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

import osmcb.mapsources.DefaultMapSourcesManager;
import osmcb.program.EnvironmentSetup;
import osmcb.program.tilestore.ACSiTileStore;
import osmcb.utilities.GUIExceptionHandler;
import osmcd.gui.MainGUI;
import osmcd.gui.SplashFrame;
import osmcd.program.Logging;
import osmcd.program.ProgramInfo;
import osmcd.program.model.OSMCDSettings;

public class OSMCDApp extends ACWindowsApp
{
	static public OSMCDApp getApp()
	{
		return (OSMCDApp) gApp;
	}

	public OSMCDSettings getSettings()
	{
		if (pSets == null)
			pSets = OSMCDSettings.getInstance();
		return (OSMCDSettings) pSets;
	}

	public OSMCDApp() {
		try
		{
			gApp = this;
		}
		catch (Throwable t)
		{
			System.exit(1);
		}
	}

	public int runWork()
	{
		try
		{
			findProgramDir();
			parseCommandLine();
			if (showSplashScreen())
				SplashFrame.showFrame();

			// init the default dirs
			Logging.configureLogging(this);

			// MySocketImplFactory.install();
			ProgramInfo.initialize(); // Load revision info
			// Logging.logSystemInfo();
			GUIExceptionHandler.installToolkitEventQueueProxy();
			// Logging.logSystemProperties();
			ImageIO.setUseCache(false);

			// try to read settings.xml and check file/dir settings therein
			EnvironmentSetup.checkSettingsSetup();
			pSets = OSMCDSettings.loadOrQuit();
			EnvironmentSetup.checkFileSetup();
			EnvironmentSetup.checkMemory();

			Logging.logSystemInfo();

			// Setup mappacks
			EnvironmentSetup.copyMapPacks();
			DefaultMapSourcesManager.initialize();
			ACSiTileStore.initialize(); // multiple instances are accessing tilestore ????
			// EnvironmentSetup.upgrade();
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Logging.LOG.debug("Starting GUI");
					MainGUI.createMainGui();
					SplashFrame.hideFrame();
					runMainGUI();
				}
			});
			return 0;
		}
		catch (Throwable t)
		{
			GUIExceptionHandler.processException(t);
			return -1;
		}
	}

	protected void parseCommandLine()
	{
		if ((ARGS != null) && (ARGS.length >= 2))
		{
			Logging.LOG.debug("tried to call OSMCD with args.");
		}
	}

	@Override
	public void setArgs(String[] strArgs)
	{
		ARGS = strArgs;
	}

	@Override
	public void runMainGUI()
	{
	}
}
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/OSMCDApp.java
