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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import osmb.utilities.GBC;
import osmb.utilities.OSMBUtilities;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame;

public class LicensesDialog extends JFrame implements ChangeListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private LicenseInfo[] licenses = new LicenseInfo[]
	{ new LicenseInfo("<h2>OpenSeaMap ChartBundler</h2>", "gpl.txt"), new LicenseInfo("<h3>Library Apache Log4J</h3>", "apache-2.0.txt"),
			new LicenseInfo("<h3>Library Apache Commons Codec</h3>", "apache-2.0.txt"), new LicenseInfo("<h3>Library Apache Commons IO</h3>", "apache-2.0.txt"),
			new LicenseInfo("<h3>Library Berkely-DB JavaEdition</h3>", "license-dbd-je.txt"), new LicenseInfo("<h3>Library BeanShell</h3>", "lgpl-3.0.txt"),
			new LicenseInfo("<h3>Library JavaPNG</h3>", "gpl.txt"), new LicenseInfo("<h3>Library iTextPDF</h3>", "agpl.txt") };

	private final JTextArea textArea;
	private final JTabbedPane tab;
	private String currentLicense = null;

	public LicensesDialog()
	{
		super(OSMCDStrs.RStr("dlg_license_title"));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLayout(new GridBagLayout());
		setIconImages(MainFrame.OSMCD_ICONS);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JButton ok = new JButton("OK");
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBackground(this.getBackground());
		JScrollPane textScroller = new JScrollPane(textArea);
		textScroller.setPreferredSize(new Dimension(700, (int) (dim.height * 0.8)));

		tab = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
		Icon icon = new ImageIcon(new BufferedImage(1, 50, BufferedImage.TYPE_INT_ARGB));

		boolean first = true;
		for (LicenseInfo li : licenses)
		{
			tab.addTab("<html>" + li.name + "</html>", icon, (first) ? textScroller : null);
			first = false;
		}
		tab.addChangeListener(this);
		stateChanged(null);
		add(tab, GBC.eol().anchor(GBC.NORTH).fill());

		// add(textScroller, GBC.eol());
		add(ok, GBC.eol().anchor(GBC.CENTER).insets(5, 10, 10, 10));
		ok.addActionListener(this);
		pack();

		setLocation((dim.width - getWidth()) / 2, (dim.height - getHeight()) / 2);
	}

	@Override
	public void stateChanged(ChangeEvent event)
	{
		String license;
		try
		{
			String nextLicense = licenses[tab.getSelectedIndex()].licenseResource;
			if (nextLicense.equals(currentLicense))
				return;
			license = OSMBUtilities.loadTextResource("text/" + nextLicense);
			currentLicense = nextLicense;
		}
		catch (IOException e)
		{
			license = "Failed to load license: " + e.getMessage();
		}
		textArea.setText(license);
		textArea.setCaretPosition(0);

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		dispose();
	}

	private static class LicenseInfo
	{
		public final String name;
		public final String licenseResource;

		public LicenseInfo(String name, String licenseResource)
		{
			super();
			this.name = name;
			this.licenseResource = licenseResource;
		}
	}
}
