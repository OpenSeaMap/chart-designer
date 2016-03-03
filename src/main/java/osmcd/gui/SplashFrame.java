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
package osmcd.gui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

import osmb.utilities.OSMBUtilities;

@SuppressWarnings("serial")
public class SplashFrame extends JFrame
{
	private static SplashFrame startupFrame;

	public static void showFrame()
	{
		startupFrame = new SplashFrame();
	}

	public SplashFrame() throws HeadlessException
	{
		// super(MainFrame.localizedStringForKey("splash_title"));
		super("OSMCD");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setUndecorated(true);
		setIconImages(MainFrame.OSMCD_ICONS);
		JLabel image = new JLabel(OSMBUtilities.loadResourceImageIcon("Splash.jpg"));
		image.setBorder(BorderFactory.createEtchedBorder());
		add(image);
		pack();
		setFocusable(false);
		Dimension dScreen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dScreen.width - getWidth()) / 2, (dScreen.height - getHeight()) / 2);
		setSize(getMinimumSize());
		setVisible(true);
	}

	public static void hideFrame()
	{
		if (startupFrame == null)
			return;
		startupFrame.setVisible(false);
		startupFrame.dispose();
		startupFrame = null;
	}

	public static void main(String[] arg) // W ? What's the use of this?
	{
		new SplashFrame();
	}
}
