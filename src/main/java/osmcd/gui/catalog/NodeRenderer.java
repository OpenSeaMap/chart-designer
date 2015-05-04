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
package osmcd.gui.catalog;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import osmb.program.catalog.IfCatalog;
import osmb.program.map.IfLayer;
import osmb.utilities.OSMBUtilities;

/**
 * This renders the elements in the catalogTree. There are three TreeCellRenderers: catalogRenderer, layerRenderer and mapRenderer.
 * They differ in the icons used
 * 
 * @author humbach
 *
 */
public class NodeRenderer implements TreeCellRenderer
{
	private static ImageIcon catalogIcon = new ImageIcon();
	private static ImageIcon layerIcon = new ImageIcon();
	private static ImageIcon mapIcon = new ImageIcon();

	static
	{
		catalogIcon = OSMBUtilities.loadResourceImageIcon("atlas.png");
		layerIcon = OSMBUtilities.loadResourceImageIcon("layer.png");
		mapIcon = OSMBUtilities.loadResourceImageIcon("map.png");
	}

	DefaultTreeCellRenderer catalogRenderer;
	DefaultTreeCellRenderer layerRenderer;
	DefaultTreeCellRenderer mapRenderer;

	/**
	 * @see NodeRenderer
	 */
	public NodeRenderer()
	{
		catalogRenderer = new SimpleTreeCellRenderer(catalogIcon);
		layerRenderer = new SimpleTreeCellRenderer(layerIcon);
		mapRenderer = new SimpleTreeCellRenderer(mapIcon);
	}

	/**
	 * @see NodeRenderer
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		TreeCellRenderer tcr;
		if (value instanceof IfCatalog)
			tcr = catalogRenderer;
		else if (value instanceof IfLayer)
			tcr = layerRenderer;
		else
			tcr = mapRenderer;
		return tcr.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}

	/**
	 * The SimpleTreeCellRenderer uses one icon for all states
	 * 
	 * @see NodeRenderer
	 */
	protected static class SimpleTreeCellRenderer extends DefaultTreeCellRenderer
	{
		private static final long serialVersionUID = 1L;

		public SimpleTreeCellRenderer(Icon icon)
		{
			super();
			setIcon(icon);
			setOpenIcon(icon);
			setClosedIcon(icon);
			setLeafIcon(icon);
		}
	}
}
