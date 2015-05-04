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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.tree.TreeNode;

import osmb.program.catalog.IfCatalogObject;

public class NodeTransferWrapper implements Transferable
{
	public static final DataFlavor CATALOG_OBJECT_FLAVOR = new DataFlavor(IfCatalogObject.class, "IfCatalogObject");
	public static final DataFlavor[] FLAVORS = new DataFlavor[]
	{ CATALOG_OBJECT_FLAVOR };

	private TreeNode node;

	public NodeTransferWrapper(TreeNode node)
	{
		this.node = node;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (!CATALOG_OBJECT_FLAVOR.equals(flavor))
			throw new UnsupportedFlavorException(flavor);
		return node;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return FLAVORS;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return CATALOG_OBJECT_FLAVOR.equals(flavor);
	}
}
