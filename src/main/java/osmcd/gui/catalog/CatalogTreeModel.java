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

import java.awt.Toolkit;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import osmb.exceptions.InvalidNameException;
import osmb.program.catalog.Catalog;
import osmb.program.catalog.IfCatalogObject;
import osmb.program.map.IfLayer;
import osmb.program.map.IfMap;
import osmb.program.map.Map;
import osmcd.OSMCDApp;

public class CatalogTreeModel implements TreeModel
{
	// static/class data
	private static Logger log = Logger.getLogger(CatalogTreeModel.class);

	// instance data
	// protected IfCatalog mCatalog; // #gCatalog
	// protected IfCatalogProfile mCatalog;
	protected Set<TreeModelListener> listeners = new HashSet<TreeModelListener>();

	public CatalogTreeModel()
	{
		// mCatalog = Catalog.newInstance(); // #gCatalog
	}

	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
		listeners.add(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
		listeners.remove(l);
	}

	public void notifyStructureChanged()
	{
		notifyStructureChanged((TreeNode) getCatalog()); // #gCatalog
	}

	public void notifyStructureChanged(TreeNode root)
	{
		notifyStructureChanged(new TreeModelEvent(this, new Object[]
		{ root }));
	}
	
	public void notifyStructureChanged(TreeNode catalog, TreeNode layer)
	{
		notifyStructureChanged(new TreeModelEvent(this, new Object[]
		{ catalog, layer }));
	}

	/**
	 * IMPORTANT: This method have to be called BEFORE deleting the element in the data model!!! Otherwise the child index can not be retrieved anymore which is
	 * important.
	 * 
	 * @param node
	 */
	public void notifyNodeDelete(TreeNode node)
	{
		TreeNode parent = node.getParent();
		Object[] children = new Object[]
		{ node };
		int childrenIdx = parent.getIndex(node);
		if (childrenIdx == -1)
		{
			// A problem detected - use fall back solution
			notifyStructureChanged();
			return;
		}
		TreePath path = getNodePath(parent);

		TreeModelEvent event = new TreeModelEvent(this, path, new int[]
		{ childrenIdx }, children);
		for (TreeModelListener l : listeners)
			l.treeNodesRemoved(event);
	}

	protected void notifyStructureChanged(TreeModelEvent event)
	{
		for (TreeModelListener l : listeners)
			l.treeStructureChanged(event);
	}

	public void notifyNodeInsert(TreeNode insertedNode)
	{
		TreeNode parent = insertedNode.getParent();
		TreePath path = getNodePath(parent);
		TreeNode[] childs = new TreeNode[]
		{ insertedNode };
		int childId = parent.getIndex(insertedNode);
		assert (childId <= 0);
		TreeModelEvent event = new TreeModelEvent(this, path, new int[]
		{ childId }, childs);
		for (TreeModelListener l : listeners)
			l.treeNodesInserted(event);
	}

	public void notifyNodesChanged(TreeModelEvent event)
	{
		for (TreeModelListener l : listeners)
			l.treeNodesChanged(event);
	}
	
	public TreePath getNodePath(TreeNode node)
	{
		LinkedList<TreeNode> path = new LinkedList<TreeNode>();
		TreeNode n = node;
		while (n != null)
		{
			path.addFirst(n);
			n = n.getParent();
		}
		return new TreePath(path.toArray());
	}

	@Override
	public Object getChild(Object parent, int index)
	{
		return ((TreeNode) parent).getChildAt(index);
	}

	@Override
	public int getChildCount(Object parent)
	{
		return ((TreeNode) parent).getChildCount();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		return ((TreeNode) parent).getIndex((TreeNode) child);
	}

	@Override
	public Object getRoot()
	{
		return getCatalog(); // #gCatalog
	}

	@Override
	public boolean isLeaf(Object node)
	{
		return ((TreeNode) node).isLeaf();
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		Object o = path.getLastPathComponent();
		boolean success = false;
		try
		{
			IfCatalogObject sel = (IfCatalogObject) o;
			String newName = (String) newValue;
			if (newName.length() == 0)
				return;
			if (sel instanceof Catalog)
			{
				// #rename
				CatalogRenameDialog renameDlg = new CatalogRenameDialog(null, newName, getCatalog().getFile().getPath(), null);
				renameDlg.actionPerformed(null);
				if (renameDlg.getChosenName() == null)
					return;

				sel.setName(newName);
				notifyNodesChanged(new TreeModelEvent(newName, path)); // repaint (longer names)
				success = true;
			}
			else if (sel instanceof Map)
			{
				sel.setName(newName);
				notifyNodesChanged(new TreeModelEvent(newName, path)); // repaint (longer names)
				success = true;
			}
			// else: sel instanceof Layer -> do nothing!
		}
		catch (ClassCastException e)
		{
			log.error("", e);
		}
		catch (InvalidNameException e)
		{
			log.error(e.getLocalizedMessage());
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), "Renaming failed", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			if (!success)
			{
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	@Deprecated // /W #deprecated
	public void mergeLayers(IfLayer source, IfLayer target) throws InvalidNameException
	{

		boolean sourceFound = false;
		boolean targetFound = false;
		for (IfLayer l : getCatalog()) // #gCatalog
		{
			if (l.equals(source))
				sourceFound = true;
			if (l.equals(target))
				targetFound = true;
		}
		if (!targetFound)
			return;
		// Check for duplicate names
		HashSet<String> names = new HashSet<String>();
		for (IfMap map : source)
			names.add(map.getName());
		for (IfMap map : target)
			names.add(map.getName());
		if (names.size() < (source.getMapCount() + target.getMapCount()))
			throw new InvalidNameException("Map naming conflict:\n" + "The layers to be merged contain maps of the same name.");

		if (sourceFound)
			getCatalog().deleteLayer(source); // #gCatalog
		for (IfMap map : source)
		{
			target.addMap(map);
		}
		notifyNodeDelete(source);
		notifyStructureChanged(target);
	}

	public void moveMap(IfMap map, IfLayer targetLayer)
	{
		notifyNodeDelete((TreeNode) map);
		map.delete();
		targetLayer.addMap(map);
		notifyNodeInsert((TreeNode) map);
	}

	public Catalog getCatalog()
	{
		return OSMCDApp.getApp().getCatalog(); // #gCatalog
	}

	// #gCatalog
	// public void setCatalog(Catalog catalog)
	// {
	// this.mCatalog = catalog;
	// notifyStructureChanged();
	// }

	// #gCatalog
	// /**
	// * This simply delegates the command to the current catalog
	// * 
	// ** @throws Exception
	// */
	// // public void save(IfCatalogProfile catalog) throws Exception
	// public void save() throws Exception
	// {
	// mCatalog.save();
	// }

	// #gCatalog
	// /**
	// * This simply delegates the command to the catalog
	// * 
	// * @param catalog
	// * @throws Exception
	// */
	// public void load(IfCatalogProfile catalog) throws Exception
	// {
	// mCatalog = Catalog.load(catalog.getFile());
	// notifyStructureChanged();
	// }
}
