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
package osmcd.program.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import osmcd.program.annotations.AtlasCreatorName;
import osmcd.program.atlascreators.AFTrack;
import osmcd.program.atlascreators.AlpineQuestMap;
import osmcd.program.atlascreators.AndNav;
import osmcd.program.atlascreators.AtlasCreator;
import osmcd.program.atlascreators.BackCountryNavigator;
import osmcd.program.atlascreators.BigPlanetTracks;
import osmcd.program.atlascreators.CacheBox;
import osmcd.program.atlascreators.CacheWolf;
import osmcd.program.atlascreators.GCLive;
import osmcd.program.atlascreators.Galileo;
import osmcd.program.atlascreators.GarminCustom;
import osmcd.program.atlascreators.Glopus;
import osmcd.program.atlascreators.GlopusMapFile;
import osmcd.program.atlascreators.GoogleEarthOverlay;
import osmcd.program.atlascreators.GpsSportsTracker;
import osmcd.program.atlascreators.IPhone3MapTiles5;
import osmcd.program.atlascreators.MBTiles;
import osmcd.program.atlascreators.MGMaps;
import osmcd.program.atlascreators.MagellanRmp;
import osmcd.program.atlascreators.Maplorer;
import osmcd.program.atlascreators.Maverick;
import osmcd.program.atlascreators.MobileTrailExplorer;
import osmcd.program.atlascreators.MobileTrailExplorerCache;
import osmcd.program.atlascreators.NFComPass;
import osmcd.program.atlascreators.NaviComputer;
import osmcd.program.atlascreators.OSMAND;
import osmcd.program.atlascreators.OSMAND_SQlite;
import osmcd.program.atlascreators.OSMTracker;
import osmcd.program.atlascreators.OruxMaps;
import osmcd.program.atlascreators.OruxMapsSqlite;
import osmcd.program.atlascreators.Osmdroid;
import osmcd.program.atlascreators.OsmdroidGEMF;
import osmcd.program.atlascreators.OsmdroidSQLite;
import osmcd.program.atlascreators.Ozi;
import osmcd.program.atlascreators.PNGWorldfile;
import osmcd.program.atlascreators.PaperAtlasPdf;
import osmcd.program.atlascreators.PaperAtlasPng;
import osmcd.program.atlascreators.PathAway;
import osmcd.program.atlascreators.RMapsSQLite;
import osmcd.program.atlascreators.RunGPSAtlas;
import osmcd.program.atlascreators.SportsTracker;
import osmcd.program.atlascreators.TTQV;
import osmcd.program.atlascreators.TileStoreDownload;
import osmcd.program.atlascreators.TomTomRaster;
import osmcd.program.atlascreators.TrekBuddy;
import osmcd.program.atlascreators.TrekBuddyTared;
import osmcd.program.atlascreators.TwoNavRMAP;
import osmcd.program.atlascreators.Ublox;
import osmcd.program.atlascreators.Viewranger;
import osmcd.program.jaxb.AtlasOutputFormatAdapter;

@XmlRootElement
@XmlJavaTypeAdapter(AtlasOutputFormatAdapter.class)
public class AtlasOutputFormat implements Comparable<AtlasOutputFormat> {

	public static List<AtlasOutputFormat> FORMATS;

	public static final AtlasOutputFormat TILESTORE = createByClass(TileStoreDownload.class);

	static {
		FORMATS = new ArrayList<AtlasOutputFormat>(40);
		FORMATS.add(createByClass(AFTrack.class));
		FORMATS.add(createByClass(AlpineQuestMap.class));
		FORMATS.add(createByClass(AndNav.class));
		FORMATS.add(createByClass(BackCountryNavigator.class));
		FORMATS.add(createByClass(BigPlanetTracks.class));
		FORMATS.add(createByClass(CacheBox.class));
		FORMATS.add(createByClass(CacheWolf.class));
		FORMATS.add(createByClass(Galileo.class));
		FORMATS.add(createByClass(GarminCustom.class));
		FORMATS.add(createByClass(GCLive.class));
		FORMATS.add(createByClass(Glopus.class));
		FORMATS.add(createByClass(GlopusMapFile.class));
		FORMATS.add(createByClass(GoogleEarthOverlay.class));
		FORMATS.add(createByClass(GpsSportsTracker.class));
		FORMATS.add(createByClass(IPhone3MapTiles5.class));
		FORMATS.add(createByClass(MagellanRmp.class));
		FORMATS.add(createByClass(Maplorer.class));
		FORMATS.add(createByClass(Maverick.class));
		FORMATS.add(createByClass(MBTiles.class));
		FORMATS.add(createByClass(MGMaps.class));
		FORMATS.add(createByClass(MobileTrailExplorer.class));
		FORMATS.add(createByClass(MobileTrailExplorerCache.class));
		FORMATS.add(createByClass(NaviComputer.class));
		FORMATS.add(createByClass(NFComPass.class));
		FORMATS.add(createByClass(OruxMaps.class));
		FORMATS.add(createByClass(OruxMapsSqlite.class));
		FORMATS.add(createByClass(OSMAND.class));
		FORMATS.add(createByClass(OSMAND_SQlite.class));
		FORMATS.add(createByClass(Osmdroid.class));
		FORMATS.add(createByClass(OsmdroidGEMF.class));
		FORMATS.add(createByClass(OsmdroidSQLite.class));
		FORMATS.add(createByClass(OSMTracker.class));
		FORMATS.add(createByClass(Ozi.class));
		FORMATS.add(createByClass(PaperAtlasPdf.class));
		FORMATS.add(createByClass(PaperAtlasPng.class));
		FORMATS.add(createByClass(PathAway.class));
		FORMATS.add(createByClass(PNGWorldfile.class));
		FORMATS.add(createByClass(RMapsSQLite.class));
		FORMATS.add(createByClass(RunGPSAtlas.class));
		FORMATS.add(createByClass(SportsTracker.class));
		FORMATS.add(createByClass(TomTomRaster.class));
		FORMATS.add(createByClass(TTQV.class));
		FORMATS.add(createByClass(TrekBuddyTared.class));
		FORMATS.add(createByClass(TrekBuddy.class));
		FORMATS.add(createByClass(TwoNavRMAP.class));
		FORMATS.add(createByClass(Ublox.class));
		FORMATS.add(createByClass(Viewranger.class));
		FORMATS.add(TILESTORE);
	}

	public static Vector<AtlasOutputFormat> getFormatsAsVector() {
		return new Vector<AtlasOutputFormat>(FORMATS);
	}

	public static AtlasOutputFormat getFormatByName(String Name) {
		for (AtlasOutputFormat af : FORMATS) {
			if (af.getTypeName().equals(Name))
				return af;
		}
		throw new NoSuchElementException("Unknown atlas format: \"" + Name + "\"");
	}

	private Class<? extends AtlasCreator> atlasCreatorClass;
	private String typeName;
	private String name;

	private static AtlasOutputFormat createByClass(Class<? extends AtlasCreator> atlasCreatorClass) {
		AtlasCreatorName acName = atlasCreatorClass.getAnnotation(AtlasCreatorName.class);
		if (acName == null)
			throw new RuntimeException("AtlasCreator " + atlasCreatorClass.getName() + " has no name");
		String typeName = acName.type();
		if (typeName == null || typeName.length() == 0)
			typeName = atlasCreatorClass.getSimpleName();
		String name = acName.value();
		return new AtlasOutputFormat(atlasCreatorClass, typeName, name);
	}

	private AtlasOutputFormat(Class<? extends AtlasCreator> atlasCreatorClass, String typeName, String name) {
		this.atlasCreatorClass = atlasCreatorClass;
		this.typeName = typeName;
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public Class<? extends AtlasCreator> getMapCreatorClass() {
		return atlasCreatorClass;
	}

	public String getTypeName() {
		return typeName;
	}

	public AtlasCreator createAtlasCreatorInstance() {
		if (atlasCreatorClass == null)
			return null;
		try {
			return atlasCreatorClass.newInstance();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public int compareTo(AtlasOutputFormat o) {
		return getTypeName().compareTo(o.toString());
	}

}
