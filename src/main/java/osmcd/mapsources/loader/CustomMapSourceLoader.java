package osmcd.mapsources.loader;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.apache.log4j.Logger;

import osmcd.exceptions.MapSourceCreateException;
import osmcd.mapsources.MapSourcesManager;
import osmcd.mapsources.custom.CustomCloudMade;
import osmcd.mapsources.custom.CustomLocalTileFilesMapSource;
import osmcd.mapsources.custom.CustomLocalTileSQliteMapSource;
import osmcd.mapsources.custom.CustomLocalTileZipMapSource;
import osmcd.mapsources.custom.CustomMapSource;
import osmcd.mapsources.custom.CustomMultiLayerMapSource;
import osmcd.mapsources.custom.CustomWmsMapSource;
import osmcd.program.interfaces.FileBasedMapSource;
import osmcd.program.interfaces.MapSource;
import osmcd.program.interfaces.WrappedMapSource;
import osmcd.program.model.MapSourceLoaderInfo;
import osmcd.program.model.MapSourceLoaderInfo.LoaderType;
import osmcd.utilities.file.FileExtFilter;

public class CustomMapSourceLoader implements ValidationEventHandler {

	private final Logger log = Logger.getLogger(MapPackManager.class);
	private final MapSourcesManager mapSourcesManager;
	private final File mapSourcesDir;

	private final Unmarshaller unmarshaller;

	public CustomMapSourceLoader(MapSourcesManager mapSourceManager, File mapSourcesDir) {
		this.mapSourcesManager = mapSourceManager;
		this.mapSourcesDir = mapSourcesDir;
		try {
			Class<?>[] customMapClasses = new Class[] { CustomMapSource.class, CustomWmsMapSource.class,
					CustomMultiLayerMapSource.class, CustomCloudMade.class, CustomLocalTileFilesMapSource.class,
					CustomLocalTileZipMapSource.class, CustomLocalTileSQliteMapSource.class };
			JAXBContext context = JAXBContext.newInstance(customMapClasses);
			unmarshaller = context.createUnmarshaller();
			unmarshaller.setEventHandler(this);
		} catch (JAXBException e) {
			throw new RuntimeException("Unable to create JAXB context for custom map sources", e);
		}
	}

	public void loadCustomMapSources() {
		File[] customMapSourceFiles = mapSourcesDir.listFiles(new FileExtFilter(".xml"));
		Arrays.sort(customMapSourceFiles);
		for (File f : customMapSourceFiles) {
			try {
				MapSource customMapSource;
				Object o = unmarshaller.unmarshal(f);
				if (o instanceof WrappedMapSource)
					customMapSource = ((WrappedMapSource) o).getMapSource();
				else
					customMapSource = (MapSource) o;
				customMapSource.setLoaderInfo(new MapSourceLoaderInfo(LoaderType.XML, f));
				if (!(customMapSource instanceof FileBasedMapSource) && customMapSource.getTileImageType() == null)
					log.warn("A problem occured while loading \"" + f.getName()
							+ "\": tileType is null - some atlas formats will produce an error!");
				log.trace("Custom map source loaded: " + customMapSource + " from file \"" + f.getName() + "\"");
				mapSourcesManager.addMapSource(customMapSource);
			} catch (Exception e) {
				log.error("failed to load custom map source \"" + f.getName() + "\": " + e.getMessage(), e);
			}
		}
	}

	public MapSource loadCustomMapSource(InputStream in) throws MapSourceCreateException, JAXBException {
		MapSource customMapSource;
		Object o = unmarshaller.unmarshal(in);
		if (o instanceof WrappedMapSource)
			customMapSource = ((WrappedMapSource) o).getMapSource();
		else
			customMapSource = (MapSource) o;
		customMapSource.setLoaderInfo(new MapSourceLoaderInfo(LoaderType.XML, null));
		log.trace("Custom map source loaded: " + customMapSource);
		return customMapSource;
	}

	public boolean handleEvent(ValidationEvent event) {
		ValidationEventLocator loc = event.getLocator();
		String file = loc.getURL().getFile();
		try {
			file = URLDecoder.decode(file, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		int lastSlash = file.lastIndexOf('/');
		if (lastSlash > 0)
			file = file.substring(lastSlash + 1);

		String errorMsg = event.getMessage();
		if (errorMsg == null) {
			Throwable t = event.getLinkedException();
			while (t != null && errorMsg == null) {
				errorMsg = t.getMessage();
				t = t.getCause();
			}
		}

		JOptionPane
				.showMessageDialog(null, "<html><h3>Failed to load a custom map</h3><p><i>" + errorMsg
						+ "</i></p><br><p>file: \"<b>" + file + "</b>\"<br>line/column: <i>" + loc.getLineNumber()
						+ "/" + loc.getColumnNumber() + "</i></p>", "Error: custom map loading failed",
						JOptionPane.ERROR_MESSAGE);
		log.error(event.toString());
		return false;
	}

	public static class WrappedMap {

	}
}
