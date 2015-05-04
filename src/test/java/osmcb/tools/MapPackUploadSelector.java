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
package osmcb.tools;

import java.io.File;
import java.security.cert.CertificateException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import osmcd.mapsources.loader.MapPackManager;
import osmcd.program.Logging;
import osmcd.program.ProgramInfo;
import osmcd.OSMCBUtilities.OSMCBUtilities;

public class MapPackUploadSelector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logging.configureConsoleLogging(Level.DEBUG);
		Logger log = Logger.getLogger(MapPackUploadSelector.class);
		ProgramInfo.initialize();
		try {
			File mapPackDir = new File("mapsources");
			File mapPackUpdateDir = new File(mapPackDir, "updates");
			OSMCBUtilities.mkDirs(mapPackUpdateDir);
			for (File newMapPack : mapPackUpdateDir.listFiles())
				OSMCBUtilities.deleteFile(newMapPack);

			OSMCBUtilities.mkDirs(mapPackUpdateDir);
			MapPackManager mpm = new MapPackManager(mapPackDir);
			String md5sumList = mpm.downloadMD5SumList();
			String[] changedMapPacks = mpm.searchForOutdatedMapPacks(md5sumList);
			for (String mapPackName : changedMapPacks) {
				log.info("Changed local map pack found: " + mapPackName);
				File mapPack = new File(mapPackDir, mapPackName);
				try {
					mpm.testMapPack(mapPack);
					File mapPackCopy = new File(mapPackUpdateDir, mapPackName);
					OSMCBUtilities.copyFile(mapPack, mapPackCopy);
				} catch (CertificateException e) {
					log.error("Map pack not copied because of invalid signature", e);
				}
			}
			if (changedMapPacks.length > 0) {
				OSMCBUtilities.copyFile(new File(mapPackDir, "mappacks-md5.txt"), new File(mapPackUpdateDir,
						"mappacks-md5.txt"));
			} else {
				log.info("No updated map packs found");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
