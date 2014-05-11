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

import java.awt.Dimension;
import java.awt.Point;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.tree.TreeNode;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import osmcbdef.exceptions.InvalidNameException;
import osmcbdef.program.interfaces.AtlasInterface;
import osmcbdef.program.interfaces.CapabilityDeletable;
import osmcbdef.program.interfaces.LayerInterface;
import osmcbdef.program.interfaces.MapInterface;
import osmcbdef.program.interfaces.MapSource;
import osmcbdef.program.interfaces.MapSpace;
import osmcbdef.program.interfaces.ToolTipProvider;
import osmcbdef.utilities.I18nUtils;
import osmcbdef.utilities.Utilities;

/**
 * A layer holding one or multiple maps of the same map source and the same zoom level. The number of maps depends on the size of the covered area - if it is
 * smaller than the specified <code>maxMapSize</code> then there will be only one map.
 * 
 * 20140128 Ah zoom level introduced as property of layer
 * 
 */
@XmlRootElement
public class Layer implements LayerInterface, TreeNode, ToolTipProvider, CapabilityDeletable
{

	private static Logger log = Logger.getLogger(Layer.class);

	@XmlTransient
	private AtlasInterface atlasInterface;

	private String name;
	private int nZoomLvl;

	@XmlElements({@XmlElement(name = "PolygonMap", type = MapPolygon.class), @XmlElement(name = "Map", type = Map.class)})
	private LinkedList<MapInterface> maps = new LinkedList<MapInterface>();

	protected Layer() {
	}

	public Layer(AtlasInterface atlasInterface, String name, int zoom) throws InvalidNameException {
		this.atlasInterface = atlasInterface;
		setName(name);
		nZoomLvl = zoom;
	}

	public void addMapsAutocut(String mapNameBase, MapSource mapSource, EastNorthCoordinate minCoordinate, EastNorthCoordinate maxCoordinate, int zoom,
			TileImageParameters parameters, int maxMapSize) throws InvalidNameException
	{
		MapSpace mapSpace = mapSource.getMapSpace();
		addMapsAutocut(mapNameBase, mapSource, minCoordinate.toTileCoordinate(mapSpace, zoom), maxCoordinate.toTileCoordinate(mapSpace, zoom), zoom, parameters,
				maxMapSize, 0);
	}

	/**
	 * addMapsAutocut() checks if the new map is already completely covered in another map
	 */
	public void addMapsAutocut(String mapNameBase, MapSource mapSource, Point minTileCoordinate, Point maxTileCoordinate, int zoom,
			TileImageParameters parameters, int maxMapSize, int overlapTiles) throws InvalidNameException
	{
		log.trace("Adding new map(s): \"" + mapNameBase + "\" " + mapSource + " zoom=" + zoom + " min=" + minTileCoordinate.x + "/" + minTileCoordinate.y + " max="
				+ maxTileCoordinate.x + "/" + maxTileCoordinate.y);

		// if no zoom level yet, set it, else check if it is correct
		if (nZoomLvl == -1)
			nZoomLvl = zoom;
		if (zoom == nZoomLvl)
		{
			int tileSize = mapSource.getMapSpace().getTileSize();
			int nXSize = (maxTileCoordinate.x - minTileCoordinate.x) / tileSize + 1;
			int nYSize = (maxTileCoordinate.y - minTileCoordinate.y) / tileSize + 1;
			int nXExp = 1, nYExp = 1;
			int nXGridSize = 0, nYGridSize = 0;

			// get size in 2^n grid
			while ((nXSize >>= 1) >= 1)
				++nXExp;
			while ((nYSize >>= 1) >= 1)
				++nYExp;

			// fit into encouraged grid widths (4, 8, 16, 32, 64, 128 tiles)
			nXExp = Math.min(2, Math.max(7, nXExp));
			nXGridSize = tileSize << nXExp;
			nYExp = Math.min(2, Math.max(7, nYExp));
			nYGridSize = tileSize << nYExp;

			// align left/top with map grid
			minTileCoordinate.x -= minTileCoordinate.x % nXGridSize;
			minTileCoordinate.y -= minTileCoordinate.y % nYGridSize;

			// align right/bottom with map grid
			maxTileCoordinate.x += nXGridSize - 1 - (maxTileCoordinate.x % nXGridSize) + tileSize * overlapTiles;
			maxTileCoordinate.y += nYGridSize - 1 - (maxTileCoordinate.y % nYGridSize) + tileSize * overlapTiles;

			// if the user set parameters we use them
			Dimension tileDimension;
			if (parameters == null)
				tileDimension = new Dimension(tileSize, tileSize);
			else
				tileDimension = parameters.getDimension();

			// We adapt the max map size to the tile size so that we do not get ugly cut/incomplete tiles at the borders
			Dimension maxMapDimension = new Dimension(maxMapSize, maxMapSize);
			maxMapDimension.width -= maxMapSize % tileDimension.width;
			maxMapDimension.height -= maxMapSize % tileDimension.height;

			// is the new map an extension of an already existing map

			// does the map fit the allowed size or has it be cut into several maps
			int mapWidth = maxTileCoordinate.x - minTileCoordinate.x;
			int mapHeight = maxTileCoordinate.y - minTileCoordinate.y;
			if (mapWidth < maxMapDimension.width && mapHeight < maxMapDimension.height)
			{
				// check if this map is not a sub/superset of another already existing map
				if (CheckMapArea(minTileCoordinate, maxTileCoordinate))
				{
					if (!CheckMapIsExtension(minTileCoordinate, maxTileCoordinate))
					{
						// String mapName = String.format(mapNameFormat, new Object[] {mapNameBase, mapCounter++});
						String mapName = MakeValidMapName(mapNameBase, "000");
						Map s = new Map(this, mapName, mapSource, zoom, minTileCoordinate, maxTileCoordinate, parameters);
						maps.add(s);
					}
				}
			}
			else
			{
				Dimension nextMapStep = new Dimension(maxMapDimension.width - (tileDimension.width * overlapTiles), maxMapDimension.height
						- (tileDimension.height * overlapTiles));

				for (int mapX = minTileCoordinate.x; mapX < maxTileCoordinate.x; mapX += nextMapStep.width)
				{
					for (int mapY = minTileCoordinate.y; mapY < maxTileCoordinate.y; mapY += nextMapStep.height)
					{
						int maxX = Math.min(mapX + maxMapDimension.width, maxTileCoordinate.x);
						int maxY = Math.min(mapY + maxMapDimension.height, maxTileCoordinate.y);
						Point min = new Point(mapX, mapY);
						Point max = new Point(maxX - 1, maxY - 1);
						// check if this map is not a sub/superset of another already existing map
						if (CheckMapArea(min, max))
						{
							if (!CheckMapIsExtension(min, max))
							{
								// String mapName = String.format(mapNameFormat, new Object[] {mapNameBase, mapCounter++});
								String mapName = MakeValidMapName(mapNameBase, "000");
								Map s = new Map(this, mapName, mapSource, zoom, min, max, parameters);
								maps.add(s);
							}
						}
					}
				}
			}
		}
	}

	public String MakeValidMapName(String mapName, String mapNum)
	{
		String newMapName = mapName + "-" + mapNum;
		int c = 1;
		for (int mapNr = 0; mapNr < getMapCount(); ++mapNr)
		{
			if (newMapName.compareTo(getMap(mapNr).getName()) == 0)
			{
				newMapName = String.format("%s-%03d", mapName, c++);
				mapNr = 0;
				continue;
			}
		}
		return newMapName;
	}

	/**
	 * checks if the new map from MinC to MaxC is not covered by an already existing map
	 * 
	 * @param MinC
	 *          minimun coordinate (upper left corner, NW-C)
	 * @param MaxC
	 *          maximum coordinate (lower right corner, SE-C)
	 * @return true if it truly is new map
	 */
	public boolean CheckMapArea(Point MinC, Point MaxC)
	{
		boolean bSub = true;
		for (int mapNr = 0; mapNr < getMapCount(); ++mapNr)
		{
			MapInterface iMap = getMap(mapNr);
			if ((iMap.getMinTileCoordinate().x <= MinC.getX()) && (iMap.getMinTileCoordinate().y <= MinC.getY()))
			{
				if ((iMap.getMaxTileCoordinate().x >= MaxC.getX()) && (iMap.getMaxTileCoordinate().y >= MaxC.getY()))
				{
					bSub = false;
					break;
				}
			}
		}
		return bSub;
	}

	/**
	 * checks if the new map is an extension of an already existing map. If it is, the exeisting map will be changed to new coordinates which includes the new
	 * map. 20140511 case new map is between two already existing maps is not covered yet.
	 * 
	 * @param MinC
	 *          minimun coordinate (upper left corner, NW-C)
	 * @param MaxC
	 *          maximum coordinate (lower right corner, SE-C)
	 * @return true if the new map is an extension to an existing map
	 */
	public boolean CheckMapIsExtension(Point MinC, Point MaxC)
	{
		boolean bIsExt = false;
		for (int mapNr = 0; mapNr < getMapCount(); ++mapNr)
		{
			MapInterface iMap = getMap(mapNr);
			if ((iMap.getMinTileCoordinate().y == MinC.getY()) && (iMap.getMaxTileCoordinate().y == MaxC.getY()))
			{
				if ((iMap.getMinTileCoordinate().x >= MinC.getX()) && (iMap.getMinTileCoordinate().x <= MaxC.getX()))
				{
					iMap.setMinTileCoordinate(MinC);
					bIsExt = true;
				}
				if ((iMap.getMaxTileCoordinate().x <= MaxC.getX()) && (iMap.getMaxTileCoordinate().x >= MinC.getX()))
				{
					iMap.setMaxTileCoordinate(MaxC);
					bIsExt = true;
				}
			}
			if ((iMap.getMinTileCoordinate().x == MinC.getX()) && (iMap.getMaxTileCoordinate().x == MaxC.getX()))
			{
				if ((iMap.getMinTileCoordinate().y >= MinC.getY()) && (iMap.getMinTileCoordinate().y <= MaxC.getY()))
				{
					iMap.setMinTileCoordinate(MinC);
					bIsExt = true;
				}
				if ((iMap.getMaxTileCoordinate().y <= MaxC.getY()) && (iMap.getMaxTileCoordinate().y >= MinC.getY()))
				{
					iMap.setMaxTileCoordinate(MaxC);
					bIsExt = true;
				}
			}
			if (bIsExt)
				break;
		}
		return bIsExt;
	}

	public void delete()
	{
		maps.clear();
		atlasInterface.deleteLayer(this);
	}

	public AtlasInterface getAtlas()
	{
		return atlasInterface;
	}

	public void addMap(MapInterface map)
	{
		// TODO: Add name collision check
		maps.add(map);
		map.setLayer(this);
	}

	public MapInterface getMap(int index)
	{
		return maps.get(index);
	}

	public int getMapCount()
	{
		return maps.size();
	}

	@XmlAttribute
	public String getName()
	{
		return name;
	}

	public void setName(String newName) throws InvalidNameException
	{
		if (atlasInterface != null)
		{
			for (LayerInterface layer: atlasInterface)
			{
				if ((layer != this) && newName.equals(layer.getName()))
					throw new InvalidNameException("There is already a layer named \"" + newName + "\" in this atlas.\nLayer names have to be unique within an atlas.");
			}
		}
		this.name = newName;
	}

	static public Layer GetLayerByZoom(AtlasInterface atlasInterface, int zoom)
	{
		if (atlasInterface != null)
		{
			for (LayerInterface layer: atlasInterface)
			{
				if (layer.getZoomLvl() == zoom)
					return (Layer) layer;
			}
		}
		return null;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public long calculateTilesToDownload()
	{
		long result = 0;
		for (MapInterface map: maps)
			result += map.calculateTilesToDownload();
		return result;
	}

	public double getMinLat()
	{
		double lat = 90d;
		for (MapInterface m: maps)
		{
			lat = Math.min(lat, m.getMinLat());
		}
		return lat;
	}

	public double getMaxLat()
	{
		double lat = -90d;
		for (MapInterface m: maps)
		{
			lat = Math.max(lat, m.getMaxLat());
		}
		return lat;
	}

	public double getMinLon()
	{
		double lon = 180d;
		for (MapInterface m: maps)
		{
			lon = Math.min(lon, m.getMinLon());
		}
		return lon;
	}

	public double getMaxLon()
	{
		double lon = -180d;
		for (MapInterface m: maps)
		{
			lon = Math.max(lon, m.getMaxLon());
		}
		return lon;
	}

	public String getToolTip()
	{
		StringWriter sw = new StringWriter(1024);
		sw.write("<html>");
		sw.write(I18nUtils.localizedStringForKey("lp_atlas_info_layer_title"));
		sw.write(I18nUtils.localizedStringForKey("lp_atlas_info_layer_map_count", maps.size()));
		sw.write(I18nUtils.localizedStringForKey("lp_atlas_info_max_tile", calculateTilesToDownload()));
		sw.write(I18nUtils.localizedStringForKey("lp_atlas_info_area_start", Utilities.prettyPrintLatLon(getMaxLat(), true),
				Utilities.prettyPrintLatLon(getMinLon(), false)));
		sw.write(I18nUtils.localizedStringForKey("lp_atlas_info_area_end", Utilities.prettyPrintLatLon(getMinLat(), true),
				Utilities.prettyPrintLatLon(getMaxLon(), false)));
		sw.write("</html>");
		return sw.toString();
	}

	public Iterator<MapInterface> iterator()
	{
		return maps.iterator();
	}

	public Enumeration<?> children()
	{
		return Collections.enumeration(maps);
	}

	public boolean getAllowsChildren()
	{
		return true;
	}

	public TreeNode getChildAt(int childIndex)
	{
		return (TreeNode) maps.get(childIndex);
	}

	public int getChildCount()
	{
		return maps.size();
	}

	public int getIndex(TreeNode node)
	{
		return maps.indexOf(node);
	}

	public TreeNode getParent()
	{
		return (TreeNode) atlasInterface;
	}

	public boolean isLeaf()
	{
		return false;
	}

	public void afterUnmarshal(Unmarshaller u, Object parent)
	{
		this.atlasInterface = (Atlas) parent;
	}

	public boolean checkData()
	{
		if (atlasInterface == null)
			return true;
		if (name == null)
			return true;
		// Check for duplicate map names
		HashSet<String> names = new HashSet<String>(maps.size());
		for (MapInterface map: maps)
			names.add(map.getName());
		if (names.size() < maps.size())
			return true; // at least one duplicate name found
		return false;
	}

	public void deleteMap(Map map)
	{
		maps.remove(map);
	}

	public LayerInterface deepClone(AtlasInterface atlas)
	{
		Layer layer = new Layer();
		layer.atlasInterface = atlas;
		layer.name = name;
		layer.setZoomLvl(getZoomLvl());
		for (MapInterface map: maps)
			layer.maps.add(map.deepClone(layer));
		return layer;
	}

	@Override
	public int getZoomLvl()
	{
		return nZoomLvl;
	}

	@Override
	public void setZoomLvl(int nZoom)
	{
		nZoomLvl = nZoom;
	}

}
