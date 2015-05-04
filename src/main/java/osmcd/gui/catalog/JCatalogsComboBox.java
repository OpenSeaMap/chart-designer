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
package osmcd.gui.catalog;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import osmb.program.catalog.Catalog;
import osmb.program.catalog.IfCatalogProfile;

/**
 * An editable {@link JComboBox} for displaying the saved atlases catalogs.
 */
public class JCatalogsComboBox extends JComboBox
{
	private static final long serialVersionUID = 1L;

	public JCatalogsComboBox()
	{
		super();
		setEditable(true);
		setEditor(new CatalogsComboBoxEditor());
	}

	/**
	 * This fills the combo box data from the list of catalogs as detected by {@link Catalog.getCatalogs()}
	 */
	public void loadCatalogsList()
	{
		setModel(new DefaultComboBoxModel(Catalog.getCatalogs()));
		setSelectedIndex(-1);
	}

	/**
	 * Deletes the catalog with the selected name. This affects the ComboBoxModel as well as the file system and the current catalog object.
	 * 
	 * @return Delete was successful
	 */
	public boolean deleteSelectedCatalog()
	{
		boolean bOk = false;
		IfCatalogProfile catalog = (IfCatalogProfile) getSelectedItem();
		if (catalog != null)
		{
			catalog.delete();
			setSelectedIndex(-1);
			removeItem(catalog);
			bOk = true;
		}
		return bOk;
	}

	/**
	 * 
	 * @return the selected catalog or <code>null</code> if no catalog is selected or a new unsaved catalog name is selected
	 */
	public IfCatalogProfile getSelectedCatalog()
	{
		Object selItem = getSelectedItem();
		if (selItem instanceof IfCatalogProfile)
			return (IfCatalogProfile) selItem;
		else
			return null;
	}

	protected static class CatalogsComboBoxEditor extends BasicComboBoxEditor
	{
		@Override
		protected JTextField createEditorComponent()
		{
			JCatalogNameField field = new JCatalogNameField();
			field.setBorder(new EmptyBorder(2, 2, 2, 0));
			return field;
		}
	}
}
