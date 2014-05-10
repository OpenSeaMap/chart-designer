package osmcbdef.mapsources.loader;

import java.io.File;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import osmcbdef.mapsources.MapSourcesManager;
import osmcbdef.mapsources.custom.BeanShellHttpMapSource;
import osmcbdef.program.model.MapSourceLoaderInfo;
import osmcbdef.program.model.MapSourceLoaderInfo.LoaderType;
import osmcbdef.utilities.file.FileExtFilter;

public class BeanShellMapSourceLoader {

	private final Logger log = Logger.getLogger(BeanShellMapSourceLoader.class);
	private final MapSourcesManager mapSourcesManager;
	private final File mapSourcesDir;

	public BeanShellMapSourceLoader(MapSourcesManager mapSourceManager, File mapSourcesDir) {
		this.mapSourcesManager = mapSourceManager;
		this.mapSourcesDir = mapSourcesDir;
	}

	public void loadBeanShellMapSources() {
		File[] customMapSourceFiles = mapSourcesDir.listFiles(new FileExtFilter(".bsh"));
		for (File f : customMapSourceFiles) {
			try {
				BeanShellHttpMapSource mapSource = BeanShellHttpMapSource.load(f);
				log.trace("BeanShell map source loaded: " + mapSource + " from file \"" + f.getName() + "\"");
				mapSource.setLoaderInfo(new MapSourceLoaderInfo(LoaderType.BSH, f));
				mapSourcesManager.addMapSource(mapSource);
			} catch (Exception e) {
				String errorMsg = "Failed to load custom BeanShell map source \"" + f.getName() + "\": "
						+ e.getMessage();
				log.error(errorMsg, e);
				JOptionPane.showMessageDialog(null, errorMsg, "Failed to load custom BeanShell map source",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
