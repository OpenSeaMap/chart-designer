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
package osmcd.gui.components;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import osmb.mapsources.ACMultiLayerMapSource;
import osmb.mapsources.IfMapSource;
import osmb.utilities.GBC;
import osmb.utilities.image.MercatorPixelCoordinate;
import osmcd.OSMCDStrs;
import osmcd.gui.mapview.JMapController;
import osmcd.gui.mapview.MapEventListener;
import osmcd.gui.mapview.PreviewMap;
import osmcd.gui.mapview.TileStoreCoverageLayer;

public class JTileStoreCoveragePanel extends JCollapsiblePanel implements MapEventListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	public static final String NAME = OSMCDStrs.RStr("lp_tile_store_title"); // /W to collapse panel at #firstStart

	private JButton showCoverage;
	private JButton hideCoverage;
	private JComboBox<IfMapSource> layerSelector;
	private JComboBox<Integer> zoomCombo;
	private PreviewMap mapViewer;

	public JTileStoreCoveragePanel(PreviewMap mapViewer)
	{
		super(OSMCDStrs.RStr("lp_tile_store_title"));
		setName(NAME);
		contentContainer.setLayout(new GridBagLayout());
		this.mapViewer = mapViewer;

		showCoverage = new JButton(OSMCDStrs.RStr("lp_tile_store_show_coverage_btn_title"));
		showCoverage.addActionListener(this);
		showCoverage.setToolTipText(OSMCDStrs.RStr("lp_tile_store_show_coverage_btn_tips"));
		hideCoverage = new JButton(OSMCDStrs.RStr("lp_tile_store_hide_coverage_btn_title"));
		hideCoverage.addActionListener(this);
		hideCoverage.setEnabled(false);
		zoomCombo = new JComboBox<Integer>();
		zoomCombo.setToolTipText(OSMCDStrs.RStr("lp_tile_store_zoom_combo_tips"));
		titlePanel.setToolTipText(OSMCDStrs.RStr("lp_tile_store_title_tips"));
		layerSelector = new JComboBox<IfMapSource>();

		GBC gbc_eol = GBC.eol().insets(2, 2, 2, 2);
		GBC gbc_std = GBC.std().insets(2, 2, 2, 2);

		contentContainer.add(new JLabel(OSMCDStrs.RStr("lp_tile_store_zoom_title")), gbc_std);
		contentContainer.add(zoomCombo, gbc_eol);
		contentContainer.add(new JLabel(OSMCDStrs.RStr("lp_tile_store_layer_title")), gbc_std);
		contentContainer.add(layerSelector, gbc_eol);
		contentContainer.add(showCoverage, gbc_eol.fillH());
		contentContainer.add(hideCoverage, gbc_eol.fillH());
		mapSourceChanged(mapViewer.getMapSource());
		mapViewer.addMapEventListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (hideCoverage.equals(e.getSource()))
		{
			TileStoreCoverageLayer.removeCacheCoverageLayers();
			mapViewer.repaint();
			hideCoverage.setEnabled(false);
			return;
		}
		Integer zoom = (Integer) zoomCombo.getSelectedItem();
		if (zoom == null)
			return;
		TileStoreCoverageLayer.removeCacheCoverageLayers();
		mapViewer.repaint();
		TileStoreCoverageLayer tscl = new TileStoreCoverageLayer(mapViewer, (IfMapSource) layerSelector.getSelectedItem(), zoom);
		mapViewer.mapLayers.add(tscl);
		hideCoverage.setEnabled(true);
	}

	@Override
	public void gridZoomChanged(int newGridZoomLevel)
	{
	}

	@Override
	public void mapSourceChanged(IfMapSource newMapSource)
	{
		TileStoreCoverageLayer.removeCacheCoverageLayers();
		hideCoverage.setEnabled(false);
		Integer selZoom = (Integer) zoomCombo.getSelectedItem();
		if (selZoom == null)
			selZoom = new Integer(8);
		int zoomLevels = Math.max(0, newMapSource.getMaxZoom() - newMapSource.getMinZoom() + 1);
		Integer[] items = new Integer[zoomLevels];
		int zoom = newMapSource.getMinZoom();
		for (int i = 0; i < items.length; i++)
		{
			items[i] = new Integer(zoom++);
		}
		zoomCombo.setModel(new DefaultComboBoxModel<Integer>(items));
		zoomCombo.setMaximumRowCount(10);
		zoomCombo.setSelectedItem(selZoom);
		IfMapSource[] layers;
		if (newMapSource instanceof ACMultiLayerMapSource)
		{
			layers = ((ACMultiLayerMapSource) newMapSource).getLayerMapSources();
			layerSelector.setEnabled(true);
		}
		else
		{
			layers = new IfMapSource[]
			{ newMapSource };
			layerSelector.setEnabled(false);
		}
		layerSelector.setModel(new DefaultComboBoxModel<IfMapSource>(layers));
		layerSelector.setSelectedIndex(0);
	}

	@Override
	public void selectNextMapSource()
	{
	}

	@Override
	public void selectPreviousMapSource()
	{
	}

	@Override
	public void selectionChanged(MercatorPixelCoordinate max, MercatorPixelCoordinate min)
	{
	}

	@Override
	public void zoomChanged(int newZoomLevel)
	{
	}

	@Override
	public void mapSelectionControllerChanged(JMapController newMapController)
	{
	}

}
