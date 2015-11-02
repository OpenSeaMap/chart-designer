package osmcd.gui.components;

import java.io.File;
import java.util.regex.Matcher;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import osmb.program.catalog.Catalog;
import osmcd.OSMCDSettings;
import osmcd.OSMCDStrs;

/**
 * a JFileChooser with special FileFilter and FileView for catalogs
 */
public class JCatalogFileChooser extends javax.swing.JFileChooser
{
	private static final long serialVersionUID = 1L;

	public JCatalogFileChooser()
	{
		setFileView(new CatalogFileView());

		setDialogType(CUSTOM_DIALOG);
		setDialogTitle(OSMCDStrs.RStr("Catalog.FileChooser.Title"));
		setFileSelectionMode(JFileChooser.FILES_ONLY);

		OSMCDSettings settings = OSMCDSettings.getInstance();
		File catalogsDir = settings.getCatalogsDirectory();
		setCurrentDirectory(catalogsDir);

		String settingsCatalogName = settings.getCatalogName();
		if (settings.getCatalogNameMakeNew())
		{
			String newName = Catalog.makeNewCatalogsName();
			newName = Catalog.getCatalogFileName(newName);
			File catalogFile = new File(catalogsDir, newName);
			setSelectedFile(catalogFile);
		}
		else
		{
			String cName = settingsCatalogName;
			cName = Catalog.getCatalogFileName(cName);
			File catalogFile = new File(catalogsDir, cName);
			setSelectedFile(catalogFile);
		}

		setAcceptAllFileFilterUsed(true); // all
		setFileFilter(new FileFilter() // default
		{
			@Override
			public boolean accept(File catFile)
			{
				Matcher m = Catalog.CATALOG_FILENAME_PATTERN.matcher(catFile.getName());
				return m.matches() ? true : false;
			}

			@Override
			public String getDescription()
			{
				return "osmcb-catalog-\"name(latin)\".xml files";
			}
		});
	}

	// @Override
	// protected JDialog createDialog(Component parent) throws HeadlessException
	// {
	// JDialog dialog = super.createDialog(parent);
	// dialog.setLocation(500, 500);
	// dialog.setResizable(true);
	// dialog.setSize(400, 500);
	// return dialog;
	// }
	//
	// public JDialog showLoadDialog(Component parent)
	// {
	// return createDialog(parent);
	// }
}
