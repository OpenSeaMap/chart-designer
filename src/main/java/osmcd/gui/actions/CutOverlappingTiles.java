package osmcd.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.tree.TreeNode;

import osmb.program.catalog.Catalog;
import osmb.program.map.Layer;
import osmcd.gui.MainFrame;

public class CutOverlappingTiles implements ActionListener
{

	@Override
	public void actionPerformed(ActionEvent e)
	{
		MainFrame mg = MainFrame.getMainGUI();
		Catalog cat = mg.getCatalog();
		if (cat != null)
		{
			for (int z = cat.getSize() - 1; z >= 0; --z)
			{
				((Layer) cat.getLayer(z)).cutOverlap();
				mg.getCatalogTree().setHasUnsavedChanges(true);
				mg.getCatalogTree().getTreeModel().notifyStructureChanged((TreeNode)cat, cat.getLayer(z));
			}
		}
	}

}
