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
package osmcd.program;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

<<<<<<< HEAD:src/main/java/osmcd/program/SettingsPaperAtlas.java
import osmb.program.WgsGridSettings;
import osmcd.program.PaperSize.Format;
=======
import osmcb.program.model.SettingsWgsGrid;
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/program/model/SettingsPaperAtlas.java
import osmcd.program.jaxb.PaperSizeAdapter;

@XmlRootElement
public class SettingsPaperAtlas implements Cloneable
{

	public static final int

	COMPRESSION_DEFAULT = 6, COMPRESSION_MAX = 9, COMPRESSION_MIN = 0,

	CROP_DEFAULT = 15, CROP_MAX = 100, CROP_MIN = 0,

	DPI_DEFAULT = 96, DPI_MAX = 300, DPI_MIN = 72;

	public static final double

	MARGIN_DEFAULT = 22.6d, MARGIN_MAX = 144d, MARGIN_MIN = 0.0d,

	OVERLAP_DEFAULT = 28.2d, OVERLAP_MIN = 0d, OVERLAP_MAX = 144d,

	PAPER_SIZE_MAX = 16384d, PAPER_SIZE_MIN = 1d;

	public static final PaperSize PAPER_SIZE_DEFAULT = new PaperSize(Format.A4, false);

	public int compression = COMPRESSION_DEFAULT, crop = CROP_DEFAULT, dpi = DPI_DEFAULT;

	public double marginBottom = MARGIN_DEFAULT, marginLeft = MARGIN_DEFAULT, marginRight = MARGIN_DEFAULT, marginTop = MARGIN_DEFAULT,
			overlap = OVERLAP_DEFAULT;

	public boolean compass = true, pageNumbers = true, scaleBar = true, wgsEnabled = true;

	@XmlElement(defaultValue = "A4")
	@XmlJavaTypeAdapter(PaperSizeAdapter.class)
	public PaperSize paperSize = PAPER_SIZE_DEFAULT;

<<<<<<< HEAD:src/main/java/osmcd/program/SettingsPaperAtlas.java
	public osmb.program.WgsGrid.WgsDensity wgsDensity = WgsGridSettings.DEFAULT_DENSITY;
=======
	public osmcb.program.WgsGrid.WgsDensity wgsDensity = SettingsWgsGrid.DEFAULT_DENSITY;
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/program/model/SettingsPaperAtlas.java

	@Override
	public SettingsPaperAtlas clone()
	{
		try
		{
			return (SettingsPaperAtlas) super.clone();
		}
		catch (Exception e)
		{
			throw new InternalError();
		}
	}

	public void checkValues()
	{
		if (compression < COMPRESSION_MIN || compression > COMPRESSION_MAX)
		{
			compression = COMPRESSION_DEFAULT;
		}
		if (crop < CROP_MIN || crop > CROP_MAX)
		{
			crop = CROP_DEFAULT;
		}
		if (dpi < DPI_MIN || dpi > DPI_MAX)
		{
			dpi = DPI_DEFAULT;
		}
		if (marginBottom < MARGIN_MIN || marginBottom > MARGIN_MAX)
		{
			marginBottom = MARGIN_DEFAULT;
		}
		if (marginLeft < MARGIN_MIN || marginLeft > MARGIN_MAX)
		{
			marginLeft = MARGIN_DEFAULT;
		}
		if (marginRight < MARGIN_MIN || marginRight > MARGIN_MAX)
		{
			marginRight = MARGIN_DEFAULT;
		}
		if (marginTop < MARGIN_MIN || marginTop > MARGIN_MAX)
		{
			marginTop = MARGIN_DEFAULT;
		}
		if (paperSize == null)
		{
			paperSize = PAPER_SIZE_DEFAULT;
		}
		if (wgsDensity == null)
		{
<<<<<<< HEAD:src/main/java/osmcd/program/SettingsPaperAtlas.java
			wgsDensity = WgsGridSettings.DEFAULT_DENSITY;
=======
			wgsDensity = SettingsWgsGrid.DEFAULT_DENSITY;
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/program/model/SettingsPaperAtlas.java
		}
	}
}
