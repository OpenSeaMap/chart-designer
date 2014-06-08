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
package osmcd.program.interfaces;

import osmcd.program.model.BundleOutputFormat;

public interface BundleInterface extends BundleObject, Iterable<LayerInterface>
{
	/**
	 * @return Number of layers in this atlas
	 */
	public int getLayerCount();

	/**
	 * 
	 * @param index
	 *          0 - ({@link #getLayerCount()}-1)
	 * @return
	 */
	public LayerInterface getLayer(int index);

	public void addLayer(LayerInterface l);

	public void deleteLayer(LayerInterface l);

	public void setOutputFormat(BundleOutputFormat atlasOutputFormat);

	public BundleOutputFormat getOutputFormat();

	public long calculateTilesToDownload();

	public int getVersion();

	public BundleInterface deepClone();
}
