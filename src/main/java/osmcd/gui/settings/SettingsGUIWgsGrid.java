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
package osmcd.gui.settings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

<<<<<<< HEAD
import osmb.program.WgsGridSettings;
import osmb.utilities.GBCTable;
import osmcd.OSMCDSettings;
import osmcd.OSMCDStrs;
import osmcd.gui.dialogs.FontChooser;
=======
import osmcb.program.model.SettingsWgsGrid;
import osmcb.utilities.GBCTable;
import osmcd.OSMCDStrs;
import osmcd.gui.dialogs.FontChooser;
import osmcd.program.model.OSMCDSettings;

public class SettingsGUIWgsGrid extends JPanel
{
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318

public class SettingsGUIWgsGrid extends JPanel
{
	private static final long serialVersionUID = -3067609813682787669L;

	private final FontChooser fontChooser = new FontChooser();

	private final JButton jButtonFont = new JButton(FontChooser.encodeFont(FontChooser.DEFAULT));

	private final JCheckBox jCheckBoxCompressLabels = new JCheckBox();

	private final JPanel jPanelColor = new JPanel();

	private final SpinnerNumberModel modelWidth = new SpinnerNumberModel(0.5d, 0.5d, 5.0d, 0.5d);

	private final JSpinner jSpinnerWidth = new JSpinner(modelWidth);

	private JLabel jLabelColor = new JLabel(), jLabelFont = new JLabel(), jLabelWidth = new JLabel();

	private String title;

	public SettingsGUIWgsGrid()
	{
		super(new GridBagLayout());
		i18n();

		jButtonFont.addActionListener(new ActionListener()
		{
<<<<<<< HEAD
			@Override
=======
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
			public void actionPerformed(ActionEvent e)
			{
				fontChooser.show();
				if (fontChooser.wasCanceled())
				{
					return;
				}
				String text = FontChooser.encodeFont(fontChooser.getFont());
				jButtonFont.setText(text);
			}
		});

		jPanelColor.setPreferredSize(new Dimension(64, 18));
		jPanelColor.setOpaque(true);
		jPanelColor.setBorder(BorderFactory.createEtchedBorder());
		jPanelColor.addMouseListener(new MouseAdapter()
		{
<<<<<<< HEAD
			@Override
=======
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
			public void mouseClicked(MouseEvent e)
			{
				Color color = jPanelColor.getBackground();
				color = JColorChooser.showDialog(jPanelColor, title, color);
				if (color != null)
				{
					jPanelColor.setBackground(color);
				}
			}

<<<<<<< HEAD
			@Override
=======
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
			public void mouseEntered(MouseEvent e)
			{
				jPanelColor.setBorder(BorderFactory.createRaisedBevelBorder());
			}

<<<<<<< HEAD
			@Override
=======
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
			public void mouseExited(MouseEvent e)
			{
				jPanelColor.setBorder(BorderFactory.createEtchedBorder());
			}
		});

		GBCTable gbc = new GBCTable();
		add(jLabelColor, gbc.begin());
		add(jLabelWidth, gbc.incY());
		add(jPanelColor, gbc.incX());
		add(jSpinnerWidth, gbc.incY());
		add(jLabelFont, gbc.incX());
		add(jCheckBoxCompressLabels, gbc.incY().gridwidth(3));
		add(jButtonFont, gbc.incX());
		add(Box.createHorizontalGlue(), gbc.incX().fillH());
	}

	public void i18n()
	{
		jCheckBoxCompressLabels.setText(OSMCDStrs.RStr("set_display_grid_compress"));
		jCheckBoxCompressLabels.setToolTipText(OSMCDStrs.RStr("set_display_grid_compress_tips"));
		setBorder(SettingsGUI.createSectionBorder(OSMCDStrs.RStr("set_display_grid")));
		title = OSMCDStrs.RStr("set_display_grid_title");// TODO: recovery
		jLabelWidth.setText(OSMCDStrs.RStr("set_display_grid_width"));
		String width = OSMCDStrs.RStr("set_display_grid_width_tips");
		jLabelWidth.setToolTipText(width);
		jSpinnerWidth.setToolTipText(width);
		jLabelColor.setText(OSMCDStrs.RStr("set_display_grid_color"));
		String color = OSMCDStrs.RStr("set_display_grid_color_tips");
		jLabelColor.setToolTipText(color);
		jPanelColor.setToolTipText(color);
		jLabelFont.setText(OSMCDStrs.RStr("set_display_grid_font"));
		String font = OSMCDStrs.RStr("set_display_grid_font_tips");
		jLabelFont.setToolTipText(font);
		jButtonFont.setToolTipText(font);
	}

	public void applySettings(OSMCDSettings s)
	{
<<<<<<< HEAD
		applySettings(s.getWgsGrid());
	}

	public void applySettings(WgsGridSettings s)
=======
		applySettings(s.wgsGrid);
	}

	public void applySettings(SettingsWgsGrid s)
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
	{
		s.compressLabels = jCheckBoxCompressLabels.isSelected();
		// s.font = fontChooser.getFont();
		s.color = jPanelColor.getBackground();
		s.width = modelWidth.getNumber().floatValue();
	}

	public void loadSettings(OSMCDSettings s)
	{
<<<<<<< HEAD
		loadSettings(s.getWgsGrid());
	}

	public void loadSettings(WgsGridSettings s)
=======
		loadSettings(s.wgsGrid);
	}

	public void loadSettings(SettingsWgsGrid s)
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
	{
		jCheckBoxCompressLabels.setSelected(s.compressLabels);
		// fontChooser.setFont(s.font);
		// jButtonFont.setText(FontChooser.encodeFont(s.font));
		jPanelColor.setBackground(s.color);
		modelWidth.setValue((double) s.width);
	}
}
