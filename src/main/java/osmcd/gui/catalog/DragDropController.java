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

import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import osmb.exceptions.InvalidNameException;
import osmb.program.map.IfLayer;
import osmb.program.map.IfMap;
import osmcd.OSMCDStrs;

public class DragDropController
{
	static Logger log = Logger.getLogger(DragDropController.class);

	public DragDropController(JCatalogTree newCatalogTree)
	{
		super();
		this.catalogTree = newCatalogTree;
		new CatalogDragSource();
		new CatalogDropTarget();
	}

	JCatalogTree catalogTree;

	protected class CatalogDragSource implements DragSourceListener, DragGestureListener
	{
		final DragGestureRecognizer recognizer;
		final DragSource source;

		public CatalogDragSource()
		{
			source = new DragSource();
			recognizer = source.createDefaultDragGestureRecognizer(catalogTree, DnDConstants.ACTION_MOVE, this);
		}

		@Override
		public void dragGestureRecognized(DragGestureEvent dge)
		{
			TreePath path = catalogTree.getSelectionPath();
			if ((path == null) || (path.getPathCount() <= 1))
				// We can't move the root node or an empty selection
				return;
			TreeNode oldNode = (TreeNode) path.getLastPathComponent();
			if (!(oldNode instanceof IfLayer || oldNode instanceof IfMap))
				return;
			Transferable transferable = new NodeTransferWrapper(oldNode);
			source.startDrag(dge, DragSource.DefaultMoveNoDrop, transferable, this);
		}

		/**
		 * Called whenever the drop target changes and it has bee accepted (
		 */
		@Override
		public void dragEnter(DragSourceDragEvent dsde)
		{
			dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
		}

		@Override
		public void dragOver(DragSourceDragEvent dsde)
		{
		}

		@Override
		public void dragDropEnd(DragSourceDropEvent dsde)
		{
		}

		@Override
		public void dragExit(DragSourceEvent dse)
		{
			dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
		}

		@Override
		public void dropActionChanged(DragSourceDragEvent dsde)
		{
		}

	}

	protected class CatalogDropTarget implements DropTargetListener
	{
		final DropTarget target;

		public CatalogDropTarget() throws HeadlessException
		{
			super();
			target = new DropTarget(catalogTree, this);
		}

		@Override
		public synchronized void dragEnter(DropTargetDragEvent dtde)
		{
		}

		@Override
		public synchronized void dragExit(DropTargetEvent dte)
		{
		}

		@Override
		public synchronized void dragOver(DropTargetDragEvent dtde)
		{
			try
			{
				Transferable t = dtde.getTransferable();
				Object o = t.getTransferData(NodeTransferWrapper.CATALOG_OBJECT_FLAVOR);
				TreeNode node = getNodeForEvent(dtde);
				if (o instanceof IfLayer && node instanceof IfLayer)
				{
					dtde.acceptDrag(dtde.getDropAction());
					return;
				}
				if (o instanceof IfMap && node instanceof IfLayer || node instanceof IfMap)
				{
					dtde.acceptDrag(dtde.getDropAction());
					return;
				}
				dtde.rejectDrag();
			}
			catch (Exception e)
			{
				log.error("", e);
			}
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde)
		{
		}

		@Override
		public synchronized void drop(DropTargetDropEvent dtde)
		{
			try
			{
				TreeNode sourceNode = (TreeNode) dtde.getTransferable().getTransferData(NodeTransferWrapper.CATALOG_OBJECT_FLAVOR);

				Point pt = dtde.getLocation();
				DropTargetContext dtc = dtde.getDropTargetContext();
				JTree tree = (JTree) dtc.getComponent();
				TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
				TreeNode targetNode = (TreeNode) parentpath.getLastPathComponent();

				if (targetNode.equals(sourceNode) || targetNode.getParent().equals(sourceNode))
				{
					dtde.rejectDrop();
					return;
				}
				CatalogTreeModel atlasTreeModel = (CatalogTreeModel) catalogTree.getModel();
				if (sourceNode instanceof IfLayer && targetNode instanceof IfLayer)
					mergeLayers(atlasTreeModel, (IfLayer) sourceNode, (IfLayer) targetNode);

				if (targetNode instanceof IfMap)
					// We can not make a iMap child of another iMap
					// -> use it's layer instead
					targetNode = targetNode.getParent();

				if (sourceNode instanceof IfMap && targetNode instanceof IfLayer)
					moveMap(atlasTreeModel, (IfMap) sourceNode, (IfLayer) targetNode);

			}
			catch (Exception e)
			{
				log.error("", e);
				catalogTree.getTreeModel().notifyStructureChanged();
				dtde.rejectDrop();
			}
		}

		protected void mergeLayers(CatalogTreeModel atlasTreeModel, IfLayer sourceLayer, IfLayer targetLayer) throws InvalidNameException
		{
			int answer = JOptionPane.showConfirmDialog(null, String.format(OSMCDStrs.RStr("msg_confirm_merge_layer"), sourceLayer.getName(), targetLayer.getName()),
					OSMCDStrs.RStr("msg_confirm_merge_layer_title"), JOptionPane.YES_NO_OPTION);
			if (answer != JOptionPane.YES_OPTION)
				return;
			try
			{
				atlasTreeModel.mergeLayers(sourceLayer, targetLayer);
			}
			catch (InvalidNameException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage(), OSMCDStrs.RStr("msg_merge_layer_failed"), JOptionPane.ERROR_MESSAGE);
				throw e;
			}
		}

		protected void moveMap(CatalogTreeModel atlasTreeModel, IfMap map, IfLayer targetLayer) throws InvalidNameException
		{
			atlasTreeModel.moveMap(map, targetLayer);
		}

		private TreeNode getNodeForEvent(DropTargetDragEvent dtde)
		{
			Point p = dtde.getLocation();
			DropTargetContext dtc = dtde.getDropTargetContext();
			JTree tree = (JTree) dtc.getComponent();
			TreePath path = tree.getClosestPathForLocation(p.x, p.y);
			return (TreeNode) path.getLastPathComponent();
		}
	}
}
