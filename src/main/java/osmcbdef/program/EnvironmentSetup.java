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
package osmcbdef.program;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import osmcbdef.mapsources.MapSourcesManager;
import osmcbdef.program.interfaces.MapSource;
import osmcbdef.program.model.Atlas;
import osmcbdef.program.model.EastNorthCoordinate;
import osmcbdef.program.model.Layer;
import osmcbdef.program.model.Profile;
import osmcbdef.program.model.Settings;
import osmcbdef.utilities.GUIExceptionHandler;
import osmcbdef.utilities.I18nUtils;
import osmcbdef.utilities.Utilities;
import osmcbdef.utilities.file.FileExtFilter;
import osmcbdef.utilities.file.NamePatternFileFilter;

/**
 * Creates the necessary files on first time OpenSeaMap ChartBundler is started or tries to update the environment if the version has changed.
 */
public class EnvironmentSetup
{

	private static boolean FIRST_START = false;

	public static Logger log = Logger.getLogger(EnvironmentSetup.class);

	public static void checkMemory()
	{
		Runtime r = Runtime.getRuntime();
		long maxHeap = r.maxMemory();
		String heapMBFormatted = String.format(Locale.ENGLISH, "%3.2f MiB", maxHeap / 1048576d);
		log.info("Total available memory to OSMCB: " + heapMBFormatted);
		if (maxHeap < 200000000)
		{
			String msg = String.format(I18nUtils.localizedStringForKey("msg_environment_lack_memory"), heapMBFormatted);
			JOptionPane.showMessageDialog(null, msg, I18nUtils.localizedStringForKey("msg_environment_lack_memory_title"), JOptionPane.WARNING_MESSAGE);
		}
	}

	public static void upgrade()
	{
		FileFilter ff = new NamePatternFileFilter("tac-profile-.*.xml");
		File profilesDir = DirectoryManager.currentDir;
		File[] files = profilesDir.listFiles(ff);
		for (File f: files)
		{
			File dest = new File(profilesDir, f.getName().replaceFirst("tac-", "osmcbdef-"));
			f.renameTo(dest);
		}
	}

	/**
	 * In case the <tt>mapsources</tt> directory has been moved by configuration (directories.ini or settings.xml) we need to copy the existing map packs into the
	 * configured directory
	 */
	public static void copyMapPacks()
	{
		File userMapSourcesDir = Settings.getInstance().getMapSourcesDirectory();
		File progMapSourcesDir = new File(DirectoryManager.programDir, "mapsources");
		if (userMapSourcesDir.equals(progMapSourcesDir))
			return; // no user specific directory configured
		if (userMapSourcesDir.isDirectory())
			return; // directory already exists - map packs should have been already copied
		try
		{
			Utilities.mkDirs(userMapSourcesDir);
			FileUtils.copyDirectory(progMapSourcesDir, userMapSourcesDir, new FileExtFilter(".jar"));
		}
		catch (IOException e)
		{
			log.error(e);
			JOptionPane.showMessageDialog(null, I18nUtils.localizedStringForKey("msg_environment_error_init_mapsrc_dir") + e.getMessage(),
					I18nUtils.localizedStringForKey("Error"), JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	/**
	 * Note: This method has be be called before {@link Settings#loadOrQuit()}. Therefore no localization is available at this point.
	 */
	public static void checkFileSetup()
	{
		checkDirectory(DirectoryManager.userSettingsDir, "user settings", true);
		checkDirectory(DirectoryManager.atlasProfilesDir, "atlas profile", true);
		checkDirectory(DirectoryManager.tileStoreDir, "tile store", true);
		checkDirectory(DirectoryManager.tempDir, "temporary atlas download", true);
		if (!Settings.FILE.exists())
		{
			try
			{
				FIRST_START = true;
				Settings.save();
			}
			catch (Exception e)
			{
				log.error("Error while creating settings.xml: " + e.getMessage(), e);
				String[] options = {"Exit", "Show error report"};
				int a = JOptionPane.showOptionDialog(null, "Could not create file settings.xml - program will exit.", "Error", 0, JOptionPane.ERROR_MESSAGE, null,
						options, options[0]);
				if (a == 1)
					GUIExceptionHandler.showExceptionDialog(e);
				System.exit(1);
			}
		}
	}

	protected static void checkDirectory(File dir, String dirName, boolean checkIsWriteable)
	{
		try
		{
			Utilities.mkDirs(dir);
		}
		catch (IOException e)
		{
			GUIExceptionHandler.processFatalExceptionSimpleDialog(
					String.format(I18nUtils.localizedStringForKey("msg_environment_error_create_dir"), dirName, dir.getAbsolutePath()), e);
		}
		if (!checkIsWriteable)
			return;
		try
		{
			// test if we can write into that directory
			File testFile = File.createTempFile("OSMCB", "", dir);
			testFile.createNewFile();
			testFile.deleteOnExit();
			testFile.delete();
		}
		catch (IOException e)
		{
			GUIExceptionHandler.processFatalExceptionSimpleDialog(
					String.format(I18nUtils.localizedStringForKey("msg_environment_error_write_file"), dirName, dir.getAbsolutePath()), e);
		}
	}

	public static void createDefaultAtlases()
	{
		if (!FIRST_START)
			return;
		// TODO:MP change sample to Chinese
		Profile p = new Profile("Google Maps New York");
		Atlas atlas = Atlas.newInstance();
		try
		{
			EastNorthCoordinate max = new EastNorthCoordinate(40.97264, -74.142609);
			EastNorthCoordinate min = new EastNorthCoordinate(40.541982, -73.699036);
			Layer layer = new Layer(atlas, "GM New York", 14);
			MapSource ms = MapSourcesManager.getInstance().getSourceByName("Mapnik");
			if (ms == null)
				return;
			layer.addMapsAutocut("GM New York 16", ms, max, min, 16, null, 32000);
			layer.addMapsAutocut("GM New York 14", ms, max, min, 14, null, 32000);
			atlas.addLayer(layer);
			p.save(atlas);
		}
		catch (Exception e)
		{
			log.error("Creation for example profiles failed", e);
			GUIExceptionHandler.showExceptionDialog(e);
		}
	}
}
