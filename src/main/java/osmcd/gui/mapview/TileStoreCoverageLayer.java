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
package osmcd.gui.mapview;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.swing.JOptionPane;

import osmb.mapsources.IfMapSource;
import osmb.program.DelayedInterruptThread;
import osmb.program.map.IfMapSpace;
import osmb.program.tilestore.ACSiTileStore;
import osmb.utilities.GUIExceptionHandler;
import osmb.utilities.OSMBStrs;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;
import osmcd.gui.dialogs.WorkinprogressDialog;

public class TileStoreCoverageLayer implements IfMapLayer
{
	private final IfMapSource mapSource;
	private final int zoom;
	private final Point pixelCoordinateMin;
	private final Point pixelCoordinateMax;
	private final Point tileNumMin;
	private final Point tileNumMax;
	private BufferedImage coverageImage = null;

	public static void removeCacheCoverageLayers()
	{
		try
		{
			PreviewMap previewMap = MainFrame.getMainGUI().previewMap;
			Iterator<IfMapLayer> mapLayers = previewMap.mapLayers.iterator();
			IfMapLayer ml;
			while (mapLayers.hasNext())
			{
				ml = mapLayers.next();
				if (ml instanceof TileStoreCoverageLayer)
				{
					mapLayers.remove();
				}
			}
		}
		catch (Exception e)
		{
		}
	}

	public TileStoreCoverageLayer(PreviewMap mapViewer, IfMapSource mapSource, int zoom)
	{
		this.mapSource = mapSource;
		this.zoom = zoom;

		IfMapSpace mapSpace = mapSource.getMapSpace();
		int tileSize = mapSpace.getTileSize();
		int mapViewerZoom = mapViewer.getZoom();
		Point min = mapViewer.getTopLeftCoordinate();
		Point max = new Point(min.x + mapViewer.getWidth(), min.y + mapViewer.getHeight());
		min = mapSpace.changeZoom(min, mapViewerZoom, zoom);
		max = mapSpace.changeZoom(max, mapViewerZoom, zoom);

		tileNumMax = new Point(max.x / tileSize, max.y / tileSize);
		tileNumMin = new Point(min.x / tileSize, min.y / tileSize);
		pixelCoordinateMax = new Point(tileNumMax.x * tileSize + tileSize - 1, tileNumMax.y * tileSize + tileSize - 1);
		pixelCoordinateMin = new Point(tileNumMin.x * tileSize, tileNumMin.y * tileSize);
		updateCoverageImage();
	}

	private void updateCoverageImage()
	{
		coverageImage = null;
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					coverageImage = ACSiTileStore.getInstance().getCacheCoverage(mapSource, zoom, tileNumMin, tileNumMax);
					if (coverageImage == null)
						JOptionPane.showMessageDialog(MainFrame.getMainGUI(), OSMCDStrs.RStr("msg_tile_store_failed_retrieve_coverage"), OSMBStrs.RStr("Error"),
		            JOptionPane.ERROR_MESSAGE);
				}
				catch (InterruptedException e)
				{
				}
				catch (Exception e)
				{
					GUIExceptionHandler.processException(e);
				}
				if (coverageImage == null)
					removeCacheCoverageLayers();
				MainFrame.getMainGUI().previewMap.repaint();
			}
		};
		WorkinprogressDialog dialog = new WorkinprogressDialog(MainFrame.getMainGUI(), "Loading coverage data", DelayedInterruptThread.createThreadFactory());
		dialog.startWork(r);
	}

	@Override
	public void paint(JMapViewer mapViewer, Graphics2D g, int zoom, int minX, int minY, int maxX, int maxY)
	{
		if (coverageImage == null)
			return;
		paintCoverage(g, zoom, minX, minY, maxX, maxY);
	}

	protected void paintCoverage(Graphics2D g, int zoom, int minX, int minY, int maxX, int maxY)
	{
		Point max = pixelCoordinateMax;
		Point min = pixelCoordinateMin;
		IfMapSpace mapSpace = mapSource.getMapSpace();
		int mapX = mapSpace.xChangeZoom(min.x, this.zoom, zoom);
		int mapY = mapSpace.yChangeZoom(min.y, this.zoom, zoom);
		int mapW = mapSpace.xChangeZoom(max.x - min.x + 1, this.zoom, zoom);
		int mapH = mapSpace.yChangeZoom(max.y - min.y + 1, this.zoom, zoom);
		int x = mapX - minX;
		int y = mapY - minY;
		int w = mapW;
		int h = mapH;
		g.drawImage(coverageImage, x, y, w, h, null);
	}
}