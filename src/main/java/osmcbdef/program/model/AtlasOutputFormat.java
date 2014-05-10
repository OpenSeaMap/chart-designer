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
package osmcbdef.program.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import osmcbdef.program.annotations.AtlasCreatorName;
import osmcbdef.program.atlascreators.AFTrack;
import osmcbdef.program.atlascreators.AlpineQuestMap;
import osmcbdef.program.atlascreators.AndNav;
import osmcbdef.program.atlascreators.AtlasCreator;
import osmcbdef.program.atlascreators.BackCountryNavigator;
import osmcbdef.program.atlascreators.BigPlanetTracks;
import osmcbdef.program.atlascreators.CacheBox;
import osmcbdef.program.atlascreators.CacheWolf;
import osmcbdef.program.atlascreators.GCLive;
import osmcbdef.program.atlascreators.Galileo;
import osmcbdef.program.atlascreators.GarminCustom;
import osmcbdef.program.atlascreators.Glopus;
import osmcbdef.program.atlascreators.GlopusMapFile;
import osmcbdef.program.atlascreators.GoogleEarthOverlay;
import osmcbdef.program.atlascreators.GpsSportsTracker;
import osmcbdef.program.atlascreators.IPhone3MapTiles5;
import osmcbdef.program.atlascreators.MBTiles;
import osmcbdef.program.atlascreators.MGMaps;
import osmcbdef.program.atlascreators.MagellanRmp;
import osmcbdef.program.atlascreators.Maplorer;
import osmcbdef.program.atlascreators.Maverick;
import osmcbdef.program.atlascreators.MobileTrailExplorer;
import osmcbdef.program.atlascreators.MobileTrailExplorerCache;
import osmcbdef.program.atlascreators.NFComPass;
import osmcbdef.program.atlascreators.NaviComputer;
import osmcbdef.program.atlascreators.OSMAND;
import osmcbdef.program.atlascreators.OSMAND_SQlite;
import osmcbdef.program.atlascreators.OSMTracker;
import osmcbdef.program.atlascreators.OruxMaps;
import osmcbdef.program.atlascreators.OruxMapsSqlite;
import osmcbdef.program.atlascreators.Osmdroid;
import osmcbdef.program.atlascreators.OsmdroidGEMF;
import osmcbdef.program.atlascreators.OsmdroidSQLite;
import osmcbdef.program.atlascreators.Ozi;
import osmcbdef.program.atlascreators.PNGWorldfile;
import osmcbdef.program.atlascreators.PaperAtlasPdf;
import osmcbdef.program.atlascreators.PaperAtlasPng;
import osmcbdef.program.atlascreators.PathAway;
import osmcbdef.program.atlascreators.RMapsSQLite;
import osmcbdef.program.atlascreators.RunGPSAtlas;
import osmcbdef.program.atlascreators.SportsTracker;
import osmcbdef.program.atlascreators.TTQV;
import osmcbdef.program.atlascreators.TileStoreDownload;
import osmcbdef.program.atlascreators.TomTomRaster;
import osmcbdef.program.atlascreators.TrekBuddy;
import osmcbdef.program.atlascreators.TrekBuddyTared;
import osmcbdef.program.atlascreators.TwoNavRMAP;
import osmcbdef.program.atlascreators.Ublox;
import osmcbdef.program.atlascreators.Viewranger;
import osmcbdef.program.jaxb.AtlasOutputFormatAdapter;

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
