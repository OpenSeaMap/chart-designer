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
package osmcd.gui.mapview;

<<<<<<< HEAD:src/main/java/osmcd/gui/mapview/MapEventListener.java
import osmb.mapsources.IfMapSource;
import osmb.utilities.image.MercatorPixelCoordinate;
=======
import osmcb.program.interfaces.IfMapSource;
import osmcb.program.model.MercatorPixelCoordinate;
import osmcd.gui.mapview.controller.JMapController;

public interface MapEventListener
{
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318:src/main/java/osmcd/gui/mapview/interfaces/MapEventListener.java

public interface MapEventListener
{
	/** the selection changed */
	public void selectionChanged(MercatorPixelCoordinate max, MercatorPixelCoordinate min);

	/** the zoom changed */
	public void zoomChanged(int newZoomLevel);

	/** the grid zoom changed */
	public void gridZoomChanged(int newGridZoomLevel);

	/** select the next iMap source from the iMap list */
	public void selectNextMapSource();

	/** select the previous iMap source from the iMap list */
	public void selectPreviousMapSource();

	public void mapSourceChanged(IfMapSource newMapSource);

	public void mapSelectionControllerChanged(JMapController newMapController);
}
