package osmcd.gui.catalog;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import osmb.program.catalog.Catalog;
import osmb.utilities.OSMBStrs;
import osmcd.OSMCDSettings;
import osmcd.OSMCDStrs;

/**
 * An ActionListener to invoke a modal dialog for choosing a catalogs name, if not a conform name is provided.
 * The input will be limited so that chosen names are up to standard of catalogs names.
 * If chosen or provided name is going to overwrite an existing catalog, user will have to confirm.
 */
public class CatalogRenameDialog implements ActionListener
{
	// /*
	// String.format(OSMCDStrs.RStr("Catalog.ConfirmOverwrite.New"), newCatalogsName, fileSel.getName(), catalogsDir);
	// OSMCDStrs.RStr("Catalog.Confirm");
	// String.format(OSMCDStrs.RStr("Catalog.ConfirmOverwrite.Existing"), catName, fileSel.getPath(), Catalog.getCatalogFileName(catName), catalogsDir);
	// OSMCDStrs.RStr("Catalog.Confirm");
	// String.format(OSMCDStrs.RStr("Catalog.ConfirmOverwrite.Existing.Mismatching"), fileSel.getPath(), newCatalogsName,
	// Catalog.getCatalogFileName(newCatalogsName), catalogsDir);
	// OSMCDStrs.RStr("Catalog.Confirm");
	//
	// OSMCDStrs.RStr("Catalog.Rename.New.Title");
	// String.format(OSMCDStrs.RStr("Catalog.Rename.New.Message"), fileSel.getName(), strSavingFile);
	// OSMCDStrs.RStr("Catalog.Rename.Existing.Title");
	// String.format(OSMCDStrs.RStr("Catalog.Rename.Existing.Message"), catName, fileSel.getPath(), strSavingFile);
	// */
	//
	protected Component parentComponent;
	protected String conformLatinName = null;
	protected String existingPath = null;
	protected String wrongText = null;
	protected String strCatalogsDir;
	protected String strSavingFile;
	protected String chosenName = null;

	/**
	 * Checks, whether either an optional as parameter given (conform) name
	 * or an in a dialog chosen name leads to overwrite an existing catalog.
	 * In case of possible overwriting the user has to confirm this.
	 * 
	 * The result can be retrieved by {@link}String getChosenName(): null -> do not rename
	 * 
	 * @param parentComponent
	 *          determines the Frame in which the dialog is displayed
	 * @param conformLatinName
	 *          has to be a name being up to standard of catalogs names or {@code}null,
	 *          if {@code}null a modal dialogs for choosing a catalogs name starts
	 * @param existingPath
	 *          has to be the name of an existing path, if a catalog in an existing file should be renamed or {@code}null
	 * @param wrongText
	 *          text to inform for which reason something is 'wrong'
	 */
	public CatalogRenameDialog(Component parentComponent, String conformLatinName, String existingPath, String wrongText)
	{
		this.parentComponent = parentComponent;
		this.conformLatinName = conformLatinName;
		this.existingPath = existingPath;
		this.wrongText = wrongText;
		OSMCDSettings settings = OSMCDSettings.getInstance();
		strCatalogsDir = settings.getCatalogsDirectory().getAbsolutePath();
		strSavingFile = strCatalogsDir + File.separator + Catalog.getCatalogFileName("'name'");
	}

	protected String makeOptionDlgMsg()
	{
		if (existingPath == null) // new
			return String.format(OSMCDStrs.RStr("Catalog.ConfirmOverwrite.New"), chosenName, Catalog.getCatalogFileName(chosenName), strCatalogsDir);
		// else: existing file
		if (conformLatinName == null)
			return String.format(OSMCDStrs.RStr("Catalog.ConfirmOverwrite.Existing.Mismatching"), existingPath, chosenName, Catalog.getCatalogFileName(chosenName),
			    strCatalogsDir);
		else
			return String.format(OSMCDStrs.RStr("Catalog.ConfirmOverwrite.Existing"), chosenName, existingPath, Catalog.getCatalogFileName(chosenName),
			    strCatalogsDir);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (conformLatinName == null)
		{
			String dlgTitle = (existingPath == null) ? OSMCDStrs.RStr("Catalog.Rename.New.Title") : OSMCDStrs.RStr("Catalog.Rename.Existing.Title");
			String dlgMessage = (existingPath == null) ? String.format(OSMCDStrs.RStr("Catalog.Rename.New.Message"), wrongText, strSavingFile)
			    : String.format(OSMCDStrs.RStr("Catalog.Rename.Existing.Message"), wrongText, existingPath, strSavingFile);

			JCatalogNameField catalogNameField = new JCatalogNameField();
			catalogNameField.setText(Catalog.makeNewCatalogsName());
			catalogNameField.selectAll();

			// W problems to set focus on JTextField catalogNameField: solution ->
			// https://tips4java.wordpress.com/2010/03/14/dialog-focus/
			catalogNameField.addHierarchyListener(new HierarchyListener()
			{
				@Override
				public void hierarchyChanged(HierarchyEvent e)
				{
					final Component c = e.getComponent();
					if (c.isShowing() && (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0)
					{
						Window toplevel = SwingUtilities.getWindowAncestor(c);
						toplevel.addWindowFocusListener(new WindowAdapter()
						{
							@Override
							public void windowGainedFocus(WindowEvent e)
							{
								c.requestFocus();
							}
						});
					}
				}
			});

			Object[] array =
			{ dlgMessage, OSMCDStrs.RStr("Catalog.RenameField.Info"), catalogNameField };
			int result = JOptionPane.showConfirmDialog(parentComponent, array, dlgTitle, JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION && catalogNameField.getText().length() > 0)
				chosenName = catalogNameField.getText();
			else // chosenName == null
				return;
		}
		else // conformLatinName != null
			chosenName = conformLatinName;
		{
			// check whether chosen name is unused
			if (Catalog.isCatalogsFileNamePart(chosenName))
			{
				Object[] options =
				{ OSMBStrs.RStr("Yes"), OSMBStrs.RStr("No") };
				int response = JOptionPane.showOptionDialog(parentComponent, makeOptionDlgMsg(), OSMCDStrs.RStr("Catalog.Confirm"), JOptionPane.DEFAULT_OPTION,
				    JOptionPane.WARNING_MESSAGE, null, options, options[1]);
				if (response != JOptionPane.YES_OPTION) // do not overwrite
					chosenName = null; // report result
				// else: chosenName != null
			}
			// else: unused name
			return;
		}
	}

	/**
	 * @return
	 *         the chosen name or {@code} null, if no name was chosen or user does not want to overwrite
	 */
	public String getChosenName()
	{
		return chosenName;
	}
}
