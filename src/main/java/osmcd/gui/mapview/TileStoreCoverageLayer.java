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

import org.apache.log4j.Logger;

import osmb.mapsources.IfMapSource;
import osmb.mapsources.MP2Corner;
import osmb.mapsources.MP2MapSpace;
import osmb.mapsources.MP2Pixel;
import osmb.mapsources.MP2Tile;
import osmb.program.DelayedInterruptThread;
import osmb.program.tilestore.ACSiTileStore;
import osmb.utilities.GUIExceptionHandler;
import osmb.utilities.OSMBStrs;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;
import osmcd.gui.dialogs.WorkinprogressDialog;

public class TileStoreCoverageLayer implements IfMapLayer
{
	private static final Logger log = Logger.getLogger(TileStoreCoverageLayer.class);
	
	private final IfMapSource mapSource;
	private final int mZoom;
	private final MP2Tile mtcMin_mZoom;
	private final MP2Tile mtcMax_mZoom;

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
		this.mZoom = zoom;
		int mapViewerZoom = mapViewer.getZoom();
		MP2Corner mccMin_MapViewerZoom = new MP2Corner(mapViewer.getTopLeftCoordinate().x, mapViewer.getTopLeftCoordinate().y, mapViewerZoom);
		MP2Corner mccMin_mZoom = mccMin_MapViewerZoom.adaptToZoomlevel(mZoom);
		MP2Corner mccMax_MapViewerZoom = new MP2Corner(mapViewer.getTopLeftCoordinate().x + mapViewer.getWidth() - 1,
				                                            mapViewer.getTopLeftCoordinate().y + mapViewer.getHeight() - 1, mapViewerZoom);
		MP2Corner mccMax_mZoom = mccMax_MapViewerZoom.adaptToZoomlevel(mZoom);
		
		mtcMin_mZoom = new MP2Pixel(mccMin_mZoom).getTileCoordinate();
		mtcMax_mZoom = new MP2Pixel(mccMax_mZoom).getTileCoordinate();
		
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
					Point tileNumMin = new Point(mtcMin_mZoom.getX(), mtcMin_mZoom.getY());
					Point tileNumMax = new Point(mtcMax_mZoom.getX(), mtcMax_mZoom.getY());
					coverageImage = ACSiTileStore.getInstance().getCacheCoverage(mapSource, mZoom, tileNumMin, tileNumMax);
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
		int maxTechZoom = MP2MapSpace.MAX_TECH_ZOOM;
		MP2Corner mccMin = mtcMin_mZoom.getUpperLeftCorner().adaptToZoomlevel(zoom);
		int width_MaxTechZoom = ((mtcMax_mZoom.getX() - mtcMin_mZoom.getX() + 1) * MP2MapSpace.TECH_TILESIZE) << (maxTechZoom - mZoom);
		int height_MaxTechZoom = ((mtcMax_mZoom.getY() - mtcMin_mZoom.getY() + 1) * MP2MapSpace.TECH_TILESIZE) << (maxTechZoom - mZoom);
		int x = mccMin.getX() - minX;
		int y = mccMin.getY() - minY;
		int w = width_MaxTechZoom >> (maxTechZoom - zoom);
		int h = height_MaxTechZoom >> (maxTechZoom - zoom);
		log.debug("x = " + x + ", y = " + y + ", w = " + w + ", h = " + h);
		g.drawImage(coverageImage, x, y, w, h, null); // + 1 + 1
	}
}
