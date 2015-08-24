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
package osmcd.gui.components;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import osmcb.program.model.Catalog;

/**
 * An editable {@link JComboBox} for displaying the saved atlases catalogs.
 */
public class JCatalogsComboBox extends JComboBox
{

	private static final long serialVersionUID = 1L;

	public JCatalogsComboBox() {
		super();
		setEditable(true);
		setEditor(new ProfilesComboBoxEditor());
	}

	public void loadCatalogsList()
	{
		setModel(new DefaultComboBoxModel(Catalog.getCatalogs()));
		setSelectedIndex(-1);
	}

	public boolean deleteSelectedProfile()
	{
		Catalog profile = (Catalog) getSelectedItem();
		if (profile == null)
			return false;
		profile.delete();
		setSelectedIndex(-1);
		removeItem(profile);
		return true;
	}

	/**
	 * 
	 * @return the selected profile or <code>null</code> if no profile is selected or a new unsaved profile is selected
	 */
	public Catalog getSelectedCatalog()
	{
		Object selItem = getSelectedItem();
		if (selItem instanceof Catalog)
			return (Catalog) selItem;
		else
			return null;
	}

	protected static class ProfilesComboBoxEditor extends BasicComboBoxEditor
	{
		@Override
		protected JTextField createEditorComponent()
		{
			JBundleNameField field = new JBundleNameField();
			field.setBorder(new EmptyBorder(2, 2, 2, 0));
			return field;
		}
	}
}
