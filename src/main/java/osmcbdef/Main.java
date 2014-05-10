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
package osmcbdef;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import osmcbdef.gui.MainGUI;
import osmcbdef.gui.SplashFrame;
import osmcbdef.mapsources.DefaultMapSourcesManager;
import osmcbdef.program.DirectoryManager;
import osmcbdef.program.EnvironmentSetup;
import osmcbdef.program.Logging;
import osmcbdef.program.ProgramInfo;
import osmcbdef.program.commandline.CommandLineEmpty;
import osmcbdef.program.commandline.CreateAtlas;
import osmcbdef.program.interfaces.CommandLineAction;
import osmcbdef.program.model.Settings;
import osmcbdef.program.tilestore.TileStore;
import osmcbdef.utilities.GUIExceptionHandler;

/**
 * Java 6 version of the main starter class
 */
public class Main {

	protected CommandLineAction cmdAction = new CommandLineEmpty();

	public Main() {
		try {
			parseCommandLine();
			if (cmdAction.showSplashScreen())
				SplashFrame.showFrame();

			DirectoryManager.initialize();
			Logging.configureLogging();

			// MySocketImplFactory.install();
			ProgramInfo.initialize(); // Load revision info
			Logging.logSystemInfo();
			GUIExceptionHandler.installToolkitEventQueueProxy();
			// Logging.logSystemProperties();
			ImageIO.setUseCache(false);

			EnvironmentSetup.checkFileSetup();
			Settings.loadOrQuit();
			EnvironmentSetup.checkMemory();

			EnvironmentSetup.copyMapPacks();
			DefaultMapSourcesManager.initialize();
			EnvironmentSetup.createDefaultAtlases();
			TileStore.initialize();
			EnvironmentSetup.upgrade();
			cmdAction.runBeforeMainGUI();
			if (cmdAction.showMainGUI()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Logging.LOG.debug("Starting GUI");
						MainGUI.createMainGui();
						SplashFrame.hideFrame();
						cmdAction.runMainGUI();
					}
				});
			}
		} catch (Throwable t) {
			GUIExceptionHandler.processException(t);
			System.exit(1);
		}
	}

	protected void parseCommandLine() {
		String[] args = StartOSMCBDef.ARGS;
		if (args.length >= 2) {
			if ("create".equalsIgnoreCase(args[0])) {
				if (args.length > 2)
					cmdAction = new CreateAtlas(args[1], args[2]);
				else
					cmdAction = new CreateAtlas(args[1]);
				return;
			}
		}
	}

	/**
	 * Start OSMCB without Java Runtime version check
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new Main();
		} catch (Throwable t) {
			GUIExceptionHandler.processException(t);
		}
	}
}
