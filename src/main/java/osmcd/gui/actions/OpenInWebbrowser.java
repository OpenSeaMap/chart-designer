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
package osmcd.gui.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import osmcd.program.Logging;

public class OpenInWebbrowser implements ActionListener, MouseListener
{
	URI uri;

	public OpenInWebbrowser(URI uri)
	{
		super();
		this.uri = uri;
	}

	public OpenInWebbrowser(String uri) throws URISyntaxException
	{
		super();
		this.uri = new URI(uri);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (Desktop.isDesktopSupported())
		{
			Desktop desktop = Desktop.getDesktop();
			try
			{
				desktop.browse(uri);
			}
			catch (IOException e)
			{
				Logging.LOG.error("Failed to open web browser", e);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		actionPerformed(null);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{}

	@Override
	public void mouseEntered(MouseEvent e)
	{}

	@Override
	public void mouseExited(MouseEvent e)
	{}

	@Override
	public void mousePressed(MouseEvent e)
	{}
}
