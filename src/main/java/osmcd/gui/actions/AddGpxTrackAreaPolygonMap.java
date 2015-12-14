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
package osmcd.gui.actions;

import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import osmb.exceptions.InvalidNameException;
import osmb.mapsources.IfMapSource;
import osmb.mapsources.MP2MapSpace;
import osmb.program.catalog.IfCatalog;
import osmb.program.map.IfMap;
// W #mapSpace import osmb.program.map.IfMapSpace;
import osmb.program.map.Layer;
import osmb.program.map.MapPolygon;
import osmb.program.tiles.TileImageParameters;
import osmb.utilities.geo.GeoCoordinate;
//W #mapSpace import osmb.utilities.geo.EastNorthCoordinate;
import osmcd.OSMCDSettings;
import osmcd.OSMCDStrs;
import osmcd.data.gpx.gpx11.TrkType;
import osmcd.data.gpx.gpx11.TrksegType;
import osmcd.data.gpx.interfaces.GpxPoint;
import osmcd.gui.MainFrame;
import osmcd.gui.gpxtree.GpxEntry;
import osmcd.gui.gpxtree.GpxRootEntry;
import osmcd.gui.gpxtree.TrkEntry;
import osmcd.gui.gpxtree.TrksegEntry;
import osmcd.program.SelectedZoomLevels;

public class AddGpxTrackAreaPolygonMap implements ActionListener
{
	public static final AddGpxTrackAreaPolygonMap INSTANCE = new AddGpxTrackAreaPolygonMap();

	@Override
	public void actionPerformed(ActionEvent event)
	{
		MainFrame mg = MainFrame.getMainGUI();
		GpxEntry entry = mg.getSelectedGpx();

		if (entry == null)
			return;

		TrksegType trk = null;
		TrkType t = null;
		if (entry instanceof TrksegEntry)
		{
			trk = ((TrksegEntry) entry).getTrkSeg();
		}
		else if (entry instanceof GpxRootEntry)
		{
			GpxRootEntry re = (GpxRootEntry) entry;
			List<TrkType> tlist = re.getLayer().getGpx().getTrk();
			if (tlist.size() > 1)
			{
				JOptionPane.showMessageDialog(mg, OSMCDStrs.RStr("msg_add_gpx_polygon_too_many_track"));
				return;
			}
			else if (tlist.size() == 1)
				t = tlist.get(0);
		}
		if (entry instanceof TrkEntry)
			t = ((TrkEntry) entry).getTrk();
		if (t != null)
		{
			if (t.getTrkseg().size() > 1)
			{
				JOptionPane.showMessageDialog(mg, OSMCDStrs.RStr("msg_add_gpx_polygon_too_many_segment"));
				return;
			}
			else if (t.getTrkseg().size() == 1)
				trk = t.getTrkseg().get(0);
		}
		if (trk == null)
		{
			JOptionPane.showMessageDialog(mg, OSMCDStrs.RStr("msg_add_gpx_polygon_no_select"), OSMCDStrs.RStr("Error"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		final String mapNameFmt = "%s %02d";
		IfCatalog catalog = mg.getCatalog();
		String name = mg.getCatalogName();
		IfMapSource mapSource = mg.getSelectedMapSource();
		SelectedZoomLevels sZL = mg.getSelectedZoomLevels();
		int[] zoomLevels = sZL.getZoomLevels();
		if (zoomLevels.length == 0)
		{
			JOptionPane.showMessageDialog(mg, OSMCDStrs.RStr("msg_no_zoom_level_selected"));
			return;
		}
		List<? extends GpxPoint> points = trk.getTrkpt();
		
	//W #mapSpace EastNorthCoordinate -> GeoCoordinate
		
		GeoCoordinate[] trackPoints = new GeoCoordinate[points.size()];
		GeoCoordinate minCoordinate = new GeoCoordinate(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		GeoCoordinate maxCoordinate = new GeoCoordinate(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		for (int i = 0; i < trackPoints.length; i++)
		{
			GpxPoint gpxPoint = points.get(i);
			GeoCoordinate c = new GeoCoordinate(gpxPoint.getLat().doubleValue(), gpxPoint.getLon().doubleValue());
			minCoordinate.lat = Math.min(minCoordinate.lat, c.lat);
			minCoordinate.lon = Math.min(minCoordinate.lon, c.lon);
			maxCoordinate.lat = Math.max(maxCoordinate.lat, c.lat);
			maxCoordinate.lon = Math.max(maxCoordinate.lon, c.lon);
			trackPoints[i] = c;
		}

		final int maxZoom = zoomLevels[zoomLevels.length - 1];
		 // W #mapSpace final IfMapSpace mapSpace = mapSource.getMapSpace();

		TileImageParameters customTileParameters = mg.getSelectedTileImageParameters();

		int[] xPoints = new int[trackPoints.length];
		int[] yPoints = new int[trackPoints.length];
		for (int i = 0; i < trackPoints.length; i++)
		{
			GeoCoordinate coord = trackPoints[i];
			xPoints[i] = MP2MapSpace.cLonToX(coord.lon, maxZoom); // W #mapSpace
			yPoints[i] = MP2MapSpace.cLatToY(coord.lat, maxZoom); // W #mapSpace
		}

		Polygon p = new Polygon(xPoints, yPoints, xPoints.length);
		MapPolygon maxZoomMap = new MapPolygon(null, "Dummy", mapSource, maxZoom, p, customTileParameters);

		int width = maxZoomMap.getMaxPixelCoordinate().x - maxZoomMap.getMinPixelCoordinate().x;
		int height = maxZoomMap.getMaxPixelCoordinate().y - maxZoomMap.getMinPixelCoordinate().y;
		if (Math.max(width, height) > OSMCDSettings.getInstance().getMaxMapSize())
		{
			String msg = OSMCDStrs.RStr("msg_add_gpx_polygon_maxsize");
			int result = JOptionPane.showConfirmDialog(mg, msg, OSMCDStrs.RStr("msg_add_gpx_polygon_maxsize_title"), JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (result != JOptionPane.YES_OPTION)
				return;
		}

		Layer layer = null;
		for (int zoom : zoomLevels)
		{
			String layerName = name;
			int c = 1;
			boolean success = false;
			do
			{
				try
				{
					layer = new Layer(catalog, layerName, zoom);
					success = true;
				}
				catch (InvalidNameException e)
				{
					layerName = name + "_" + Integer.toString(c++);
				}
			} while (!success);

			String mapName = String.format(mapNameFmt, new Object[]
			{ layerName, zoom });
			IfMap map = MapPolygon.createFromMapPolygon(layer, mapName, zoom, maxZoomMap);
			layer.addMap(map);
		}
		catalog.addLayer(layer);
		mg.notifyLayerInsert(layer);
	}
}
