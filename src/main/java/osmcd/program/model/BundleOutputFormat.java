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

import osmcd.ChartBundleName;
import osmcd.bundle.ChartBundle;
import osmcd.bundle.OruxMaps;
import osmcd.bundle.TileStoreDownload;
import osmcd.bundle.TrekBuddy;
import osmcd.bundle.TrekBuddyTared;
import osmcd.program.jaxb.BundleOutputFormatAdapter;

@XmlRootElement
@XmlJavaTypeAdapter(BundleOutputFormatAdapter.class)
public class BundleOutputFormat implements Comparable<BundleOutputFormat>
{
	public static List<BundleOutputFormat> FORMATS;
	public static final BundleOutputFormat TILESTORE = createByClass(TileStoreDownload.class);

	static
	{
		FORMATS = new ArrayList<BundleOutputFormat>(40);
		// FORMATS.add(createByClass(AFTrack.class));
		// FORMATS.add(createByClass(AlpineQuestMap.class));
		// FORMATS.add(createByClass(AndNav.class));
		// FORMATS.add(createByClass(BackCountryNavigator.class));
		// FORMATS.add(createByClass(BigPlanetTracks.class));
		// FORMATS.add(createByClass(CacheBox.class));
		// FORMATS.add(createByClass(CacheWolf.class));
		// FORMATS.add(createByClass(Galileo.class));
		// FORMATS.add(createByClass(GarminCustom.class));
		// FORMATS.add(createByClass(GCLive.class));
		// FORMATS.add(createByClass(Glopus.class));
		// FORMATS.add(createByClass(GlopusMapFile.class));
		// FORMATS.add(createByClass(GoogleEarthOverlay.class));
		// FORMATS.add(createByClass(GpsSportsTracker.class));
		// FORMATS.add(createByClass(IPhone3MapTiles5.class));
		// FORMATS.add(createByClass(MagellanRmp.class));
		// FORMATS.add(createByClass(Maplorer.class));
		// FORMATS.add(createByClass(Maverick.class));
		// FORMATS.add(createByClass(MBTiles.class));
		// FORMATS.add(createByClass(MGMaps.class));
		// FORMATS.add(createByClass(MobileTrailExplorer.class));
		// FORMATS.add(createByClass(MobileTrailExplorerCache.class));
		// FORMATS.add(createByClass(NaviComputer.class));
		// FORMATS.add(createByClass(NFComPass.class));
		FORMATS.add(createByClass(OruxMaps.class));
		// FORMATS.add(createByClass(OruxMapsSqlite.class));
		// FORMATS.add(createByClass(OSMAND.class));
		// FORMATS.add(createByClass(OSMAND_SQlite.class));
		// FORMATS.add(createByClass(Osmdroid.class));
		// FORMATS.add(createByClass(OsmdroidGEMF.class));
		// FORMATS.add(createByClass(OsmdroidSQLite.class));
		// FORMATS.add(createByClass(OSMTracker.class));
		// FORMATS.add(createByClass(Ozi.class));
		// FORMATS.add(createByClass(PaperAtlasPdf.class));
		// FORMATS.add(createByClass(PaperAtlasPng.class));
		// FORMATS.add(createByClass(PathAway.class));
		// FORMATS.add(createByClass(PNGWorldfile.class));
		// FORMATS.add(createByClass(RMapsSQLite.class));
		// FORMATS.add(createByClass(RunGPSAtlas.class));
		// FORMATS.add(createByClass(SportsTracker.class));
		// FORMATS.add(createByClass(TomTomRaster.class));
		// FORMATS.add(createByClass(TTQV.class));
		FORMATS.add(createByClass(TrekBuddyTared.class));
		FORMATS.add(createByClass(TrekBuddy.class));
		// FORMATS.add(createByClass(TwoNavRMAP.class));
		// FORMATS.add(createByClass(Ublox.class));
		// FORMATS.add(createByClass(Viewranger.class));
		FORMATS.add(TILESTORE);
	}

	public static Vector<BundleOutputFormat> getFormatsAsVector()
	{
		return new Vector<BundleOutputFormat>(FORMATS);
	}

	public static BundleOutputFormat getFormatByName(String Name)
	{
		for (BundleOutputFormat af: FORMATS)
		{
			if (af.getTypeName().equals(Name))
				return af;
		}
		throw new NoSuchElementException("Unknown bundle format: \"" + Name + "\"");
	}

	private Class<? extends ChartBundle> chartBundleClass;
	private String typeName;
	private String name;

	private static BundleOutputFormat createByClass(Class<? extends ChartBundle> chartBundleClass)
	{
		ChartBundleName acName = chartBundleClass.getAnnotation(ChartBundleName.class);
		if (acName == null)
			throw new RuntimeException("ChartBundle " + chartBundleClass.getName() + " has no name");
		String typeName = acName.type();
		if (typeName == null || typeName.length() == 0)
			typeName = chartBundleClass.getSimpleName();
		String name = acName.value();
		return new BundleOutputFormat(chartBundleClass, typeName, name);
	}

	private BundleOutputFormat(Class<? extends ChartBundle> chartBundleClass, String typeName, String name) {
		this.chartBundleClass = chartBundleClass;
		this.typeName = typeName;
		this.name = name;
	}

	public String toString()
	{
		return name;
	}

	public Class<? extends ChartBundle> getMapCreatorClass()
	{
		return chartBundleClass;
	}

	public String getTypeName()
	{
		return typeName;
	}

	// public ChartBundle createAtlasCreatorInstance()
	// {
	// if (chartBundleClass == null)
	// return null;
	// try
	// {
	// return chartBundleClass.newInstance();
	// }
	// catch (Throwable t)
	// {
	// throw new RuntimeException(t);
	// }
	// }

	public int compareTo(BundleOutputFormat o)
	{
		return getTypeName().compareTo(o.toString());
	}
}
