package osmcd.gui.components;

import java.io.File;
import java.util.regex.Matcher;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

import osmb.program.catalog.Catalog;
import osmcd.OSMCDSettings;
import osmcd.gui.MainFrame;

/**
 * catalogs with conform filename will show the programs icon and only the catalogs name in FileView
 */
public class CatalogFileView extends FileView
{
	protected static ImageIcon catalogIcon = new ImageIcon(MainFrame.OSMCD_ICONS.get(2));
	
	@Override
	public String getDescription(File f) // A human readable description of the file.
	{
		return null;
	}
	
	@Override
	public Icon getIcon(File f) // The icon that represents this file in the JFileChooser.
	{
		Matcher m = Catalog.CATALOG_FILENAME_PATTERN.matcher(f.getName());
		if (m.matches())
			return catalogIcon;
		else
			return null;
	}
	
	@Override
	public String getName(File f) // The name of the file.
	{
		String strRet = null;
		OSMCDSettings settings = OSMCDSettings.getInstance();
		File catalogsDir = settings.getCatalogsDirectory();
		if (f.isFile() && f.getParentFile().equals(catalogsDir))
		{
			Matcher m = Catalog.CATALOG_FILENAME_PATTERN.matcher(f.getName());
			if (m.matches())
			{
				strRet = m.group(1);
			}
		}
		return strRet;
	}
	
	@Override
	public String 	getTypeDescription(File f) // A human readable description of the type of the file.
	{
		return null;
	}
	
	@Override
	public Boolean isTraversable(File f) // Whether the directory is traversable or not.
	{
		return null;
	}
}
