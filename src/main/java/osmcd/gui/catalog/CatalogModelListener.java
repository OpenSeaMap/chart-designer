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
/**
 * 
 */
package osmcd.gui.catalog;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 * This should trace all modification made in the catalog tree.
 * 
 * @author humbach
 *
 */
public class CatalogModelListener implements TreeModelListener
{
	JCatalogTree catalogTree;
	JCatalogsPanel catalogsPanel;

	public CatalogModelListener(JCatalogTree newCatalogTree, JCatalogsPanel newCatalogsPanel)
	{
		super();
		this.catalogTree = newCatalogTree;
		this.catalogsPanel = newCatalogsPanel;
	}

	/**
	 * This sets button states in JCatalogsPanel depending on whether there are unsaved changes.<br>
	 * 
	 * @see osmcd.gui.catalog.JCatalogsPanel#setIsContentChanged(boolean)
	 */
	protected void changed()
	{
		catalogsPanel.setIsContentChanged();
	}

	@Override
	public void treeNodesChanged(TreeModelEvent e)
	{
		changed();
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e)
	{
		changed();
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e)
	{
		changed();
	}

	@Override
	public void treeStructureChanged(TreeModelEvent e)
	{
		changed();
	}
}