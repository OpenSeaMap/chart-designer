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

import javax.swing.JTextField;

import osmcb.program.model.Catalog;

/**
 * A {@link JTextField} that only accepts ASCII characters, numbers and spaces.
 * 
 */
public class JBundleNameField extends JRegexTextField
{

	private static final long serialVersionUID = 1L;

	public JBundleNameField() {
		super(Catalog.CATALOG_NAME_REGEX, 40);
	}

}
