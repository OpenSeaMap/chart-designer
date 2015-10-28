package osmcd.gui.mapview;

import osmb.mapsources.IfMapSource;
import osmb.utilities.image.MercatorPixelCoordinate;

public interface IfMapEventListener
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

	public void mapSelectionControllerChanged(ACMapController newMapController);
}
