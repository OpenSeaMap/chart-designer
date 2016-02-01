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
package osmcd.gui.dialogs;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import osmb.utilities.GBC;
import osmb.utilities.OSMBStrs;
import osmcd.OSMCDSettings;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;
import osmcd.program.Bookmark;

public class ManageBookmarks extends JDialog implements ListSelectionListener, ActionListener
{
	private static final long serialVersionUID = 1L;

	private JButton deleteButton;

	private JButton applyButton;

	private JList<Bookmark> bookmarks;

	private DefaultListModel<Bookmark> bookmarksModel;

	public ManageBookmarks(Window owner) throws HeadlessException
	{
		super(owner, OSMCDStrs.RStr("dlg_mgn_bookmark_title"));
		setIconImages(MainFrame.OSMCD_ICONS);
		setLayout(new GridBagLayout());
		applyButton = new JButton(OSMBStrs.RStr("Close"));
		applyButton.addActionListener(this);
		applyButton.setDefaultCapable(true);

		deleteButton = new JButton(OSMCDStrs.RStr("dlg_mgn_bookmark_delete"));
		deleteButton.addActionListener(this);

		bookmarksModel = new DefaultListModel<Bookmark>();
		for (Bookmark b : OSMCDSettings.getInstance().getPlaceBookmarks())
			bookmarksModel.addElement(b);
		bookmarks = new JList<Bookmark>(bookmarksModel);
		bookmarks.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		bookmarks.addListSelectionListener(this);
		bookmarks.setVisibleRowCount(10);
		bookmarks.setPreferredSize(new Dimension(250, 300));

		add(bookmarks, GBC.eol().insets(10, 10, 10, 10).fill());
		add(deleteButton, GBC.eol().anchor(GBC.CENTER).insets(0, 0, 0, 10));
		add(applyButton, GBC.eol().anchor(GBC.CENTER).insets(0, 0, 0, 10));
		pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getWidth()) / 2, (dim.height - getHeight()) / 2);

		valueChanged(null);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (deleteButton.equals(e.getSource()))
			deleteSelectedEntries();
		else if (applyButton.equals(e.getSource()))
			apply();
	}

	protected void deleteSelectedEntries()
	{
		int[] selected = bookmarks.getSelectedIndices();
		for (int i = selected.length - 1; i >= 0; i--)
			bookmarksModel.remove(selected[i]);
	}

	protected void apply()
	{
		ArrayList<Bookmark> bookmarksList = new ArrayList<Bookmark>(bookmarksModel.getSize());
		for (int i = 0; i < bookmarksModel.getSize(); i++)
			bookmarksList.add(bookmarksModel.get(i));
		OSMCDSettings.getInstance().setPlaceBookmarks(bookmarksList);
		setVisible(false);
		dispose();
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		deleteButton.setEnabled(bookmarks.getSelectedIndices().length > 0);
	}

}
