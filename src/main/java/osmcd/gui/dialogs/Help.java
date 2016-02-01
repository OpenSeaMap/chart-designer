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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import osmb.utilities.Charsets;
import osmb.utilities.GBC;
import osmb.utilities.OSMBStrs;
import osmcd.OSMCDApp;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;

public class Help extends JFrame implements WindowListener
{
	private static final long serialVersionUID = 1L;

	private static Help INSTANCE = null;

	public static synchronized void showHelp()
	{
		if (INSTANCE == null)
			INSTANCE = new Help();
		INSTANCE.setVisible(true);
	}

	public Help() throws HeadlessException
	{
		super(OSMCDStrs.RStr("dlg_help_title"));
		setIconImages(MainFrame.OSMCD_ICONS);
		setLayout(new GridBagLayout());
		JLabel text = new JLabel();
		JButton closeButton = new JButton(OSMBStrs.RStr("Close"));
		closeButton.setDefaultCapable(true);
		closeButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
				dispose();
			}
		});

		DataInputStream in = new DataInputStream(OSMCDApp.class.getResourceAsStream("resources/text/help_dialog.html"));
		byte[] buf;
		try
		{
			buf = new byte[in.available()];
			in.readFully(buf);
			in.close();
			String helpMessage = new String(buf, Charsets.UTF_8);
			// Strip out all line breaks because JOptionPane shows
			// the raw HTML code otherwise
			// helpMessage = helpMessage.replaceAll("\n", "");
			// text.setFont(osmcd.gui.MainFrame.defaultFont());
			text.setText(helpMessage);
		}
		catch (IOException e)
		{
		}
		add(text, GBC.eol().insets(10, 10, 10, 10));
		add(closeButton, GBC.eol().anchor(GBC.CENTER).insets(0, 0, 0, 10));
		pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getWidth()) / 2, (dim.height - getHeight()) / 2);

		setAlwaysOnTop(true);
		setResizable(false);
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		INSTANCE = null;
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
	}
}
