package osmcd.gui.mapview;

import osmb.mapsources.IfMapSource;
import osmb.mapsources.MP2Corner;
//W #mapSpaceimport osmb.utilities.image.MercatorPixelCoordinate;

public interface IfMapEventListener
{
//W #mapSpace EastNorthCoordinate <-> GeoCoordinate MP2Corner <-> MercatorPixelCoordinate
	/** the selection changed */
	public void selectionChanged(MP2Corner max, MP2Corner min);

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