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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package osmcd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import osmb.mapsources.IfInitializableMapSource;
import osmb.mapsources.IfMapSource;
import osmb.program.WgsGrid.WgsDensity;
import osmb.program.WgsGridSettings;
import osmb.program.catalog.Catalog;
import osmb.program.map.Layer;
import osmb.program.tiles.TileImageParameters;
import osmb.utilities.GBC;
import osmb.utilities.GUIExceptionHandler;
import osmb.utilities.OSMBUtilities;
import osmb.utilities.geo.CoordinateTileFormat;
import osmb.utilities.image.MercatorPixelCoordinate;
import osmcd.OSMCDSettings;
import osmcd.OSMCDStrs;
import osmcd.externaltools.ExternalToolDef;
import osmcd.externaltools.ExternalToolsLoader;
import osmcd.gui.actions.AddMapLayer;
import osmcd.gui.actions.DebugSetLogLevel;
import osmcd.gui.actions.DebugShowLogFile;
import osmcd.gui.actions.DebugShowMapSourceNames;
import osmcd.gui.actions.DebugShowMapTileGrid;
import osmcd.gui.actions.DebugShowReport;
import osmcd.gui.actions.HelpLicenses;
import osmcd.gui.actions.PanelShowHide;
import osmcd.gui.actions.RefreshCustomMapsources;
import osmcd.gui.actions.SelectionModeCircle;
import osmcd.gui.actions.SelectionModePolygon;
import osmcd.gui.actions.SelectionModeRectangle;
import osmcd.gui.actions.ShowAboutDialog;
import osmcd.gui.actions.ShowHelpAction;
import osmcd.gui.actions.ShowReadme;
import osmcd.gui.catalog.CatalogNew;
import osmcd.gui.catalog.JCatalogTree;
import osmcd.gui.catalog.JCatalogsPanel;
import osmcd.gui.components.FilledLayeredPane;
import osmcd.gui.components.JCollapsiblePanel;
import osmcd.gui.components.JCoordinatesPanel;
import osmcd.gui.components.JMenuItem2;
import osmcd.gui.components.JTileImageParametersPanel;
import osmcd.gui.components.JTileStoreCoveragePanel;
import osmcd.gui.components.JZoomCheckBox;
import osmcd.gui.gpxtree.GpxEntry;
import osmcd.gui.gpxtree.JGpxPanel;
import osmcd.gui.mapview.ACMapController;
import osmcd.gui.mapview.GridZoom;
import osmcd.gui.mapview.IfMapEventListener;
import osmcd.gui.mapview.JMapViewer;
import osmcd.gui.mapview.PolygonCircleSelectionMapController;
import osmcd.gui.mapview.PolygonSelectionMapController;
import osmcd.gui.mapview.PreviewMap;
import osmcd.gui.mapview.RectangleSelectionMapController;
import osmcd.gui.settings.SettingsGUI;
import osmcd.program.MapSelection;
import osmcd.program.ProgramInfo;
import osmcd.program.SelectedZoomLevels;

public class MainFrame extends JFrame implements IfMapEventListener
{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(MainFrame.class);

	private static Color labelBackgroundColor = new Color(0, 0, 0, 127);
	private static Color checkboxBackgroundColor = new Color(0, 0, 0, 40);
	private static Color labelForegroundColor = Color.WHITE;
	private static MainFrame mainGUI = null;
	public static final ArrayList<Image> OSMCD_ICONS = new ArrayList<Image>(3);

	// static
	// {
	// OSMCD_ICONS.add(OSMBUtilities.loadResourceImageIcon("osmb_48.png").getImage());
	// OSMCD_ICONS.add(OSMBUtilities.loadResourceImageIcon("osmb_32.png").getImage());
	// OSMCD_ICONS.add(OSMBUtilities.loadResourceImageIcon("osmb_16.png").getImage());
	// }
	//
	static
	{
		OSMCD_ICONS.add(OSMBUtilities.loadResourceImageIcon("osmcd48.png").getImage());
		OSMCD_ICONS.add(OSMBUtilities.loadResourceImageIcon("osmcd32.png").getImage());
		OSMCD_ICONS.add(OSMBUtilities.loadResourceImageIcon("osmcd16.png").getImage());
	}

	protected JMenuBar menuBar;
	protected JMenu toolsMenu = null;
	// private JMenu bookmarkMenu = null; // /W #unused

	public final PreviewMap previewMap = new PreviewMap();
	// public final JCatalogTree jCatalogTree = new JCatalogTree(previewMap);

	private JCheckBox wgsGridCheckBox;
	private JComboBox<WgsDensity> wgsGridCombo; // /W <WgsDensity>

	private JLabel zoomLevelText;
	private JComboBox<GridZoom> gridZoomCombo; // /W <GridZoom>
	private JSlider zoomSlider;
	// private JComboBox mapSourceCombo;
	private JButton settingsButton;
	// public JCatalogNameField catalogNameTextField;
	// private JButton createAtlasButton;
	private JZoomCheckBox[] cbZoom = new JZoomCheckBox[0];
	private JLabel amountOfTilesLabel;

	// the MainGUI has three Panels: left panel, right panel, middle panel
	// the middle panel is layered of the previewMap and the mapControlsPanel
	private JPanel mapControlsPanel = new JPanel(new BorderLayout());

	// * there are seven panels and one button in the left (catalog) panel
	// * - selection coordinates
	// * - map source
	// * - zoom levels
	// * - layer settings | custom tile processing
	// * - catalog content
	// * - saved catalogs
	// * o button settings
	// * - tile store coverage
	private JPanel leftPane = new JPanel(new GridBagLayout());
	private JPanel leftPanelContent = null;

	// /W initialization of members in createLeftPanelControls()
	private JCoordinatesPanel mCoordinatesPanel; // selection coordinates
	private JMapSourcePanel mMapSourcePanel; // map source
	private JPanel mZoomLevelPanel; // zoom levels
	private JTileImageParametersPanel mTileImageParametersPanel; // /W #--- // layer settings | custom tile processing
	// /W +++ private JCatalogContentPanel mCatalogContentPanel; // catalog content
	private JCatalogsPanel mCatalogsPanel; // saved catalogs
	private JTileStoreCoveragePanel mTileStoreCoveragePanel; // tile store coverage

	// In the right panel is currently only the gpx panel
	private JPanel rightPane = new JPanel(new GridBagLayout());
	private JGpxPanel mGpxPanel;

	public JMenu logLevelMenu;
	private JMenuItem smRectangle;
	private JMenuItem smPolygon;
	private JMenuItem smCircle;

	private MercatorPixelCoordinate mapSelectionMax = null;
	private MercatorPixelCoordinate mapSelectionMin = null;

	public static void createMainGui()
	{
		if (mainGUI != null)
			return;

		mainGUI = new MainFrame();
		mainGUI.setVisible(true);
		log.trace("MainFrame now visible");
	}

	/**
	 * Forces the program to start with Settings.Directories dialog if settings are uninitiated
	 */
	public static void runFirstStart() // /W #firstStart // /W #??? compare to FIRST_START in EnvironmentSetup
	{
		if (OSMCDSettings.getInstance().getSettingsTabSelected() == -2) // /W -2: firstStart
		{
			SettingsGUI.showSettingsDialog(mainGUI, 0);
		}
	}

	public static MainFrame getMainGUI()
	{
		return mainGUI;
	}

	// MP: get custom font
	static Font sCustomFont = null;

	public static Font customFont()
	{
		if (sCustomFont == null)
		{
			// force to use Chinese font
			sCustomFont = new Font("宋体", 9, 13);
		}
		return sCustomFont;
	}

	// MP: update all UI components' default font to custom font
	public static void setDefaultFontOfAllUIComponents(Font defaultFont)
	{
		if (defaultFont != null)
		{
			// register custom font to application，system font will return false
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(defaultFont);

			// update all UI's font settings
			javax.swing.plaf.FontUIResource fontRes = new javax.swing.plaf.FontUIResource(defaultFont);
			Enumeration<Object> keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements())
			{
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				if (value instanceof javax.swing.plaf.FontUIResource)
				{
					UIManager.put(key, fontRes);
				}
			}
		}
	}

	/**
	 * holds the whole main screen frame
	 */
	private MainFrame()
	{
		super();
		mainGUI = this;
		setIconImages(OSMCD_ICONS);

		GUIExceptionHandler.registerForCurrentThread();
		setTitle(ProgramInfo.getCompleteTitle());

		log.trace("Creating main dialog - " + getTitle());
		setResizable(true);
		Dimension dScreen = Toolkit.getDefaultToolkit().getScreenSize();
		setMinimumSize(new Dimension(Math.min(800, dScreen.width), Math.min(590, dScreen.height)));
		setSize(getMinimumSize());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowDestroyer());
		addComponentListener(new MainWindowListener());

		previewMap.addMapEventListener(this);

		// the main frame consists of three panes
		createMapControls();
		createLeftPanelControls();
		createRightPanelControls();

		calculateNrOfTilesToDownload();
		setLayout(new BorderLayout());
		add(leftPane, BorderLayout.WEST);
		// /W #--- add(rightPane, BorderLayout.EAST);
		JLayeredPane layeredPane = new FilledLayeredPane();
		layeredPane.add(previewMap, Integer.valueOf(0));
		layeredPane.add(mapControlsPanel, Integer.valueOf(1));
		add(layeredPane, BorderLayout.CENTER);

		updateMapControlsPanel();
		updateLeftPanel();
		updateRightPanel();
		// updateZoomLevelCheckBoxes();
		// calculateNrOfTilesToDownload(); // /W #???

		menuBar = new JMenuBar();
		prepareMenuBar();
		setJMenuBar(menuBar);

		// the left pane consists of XX panels
		updateZoomLevelCheckBoxes(); // /W has to be called once before loadSettings()
		loadSettings();
		mCatalogsPanel.initialize();
		mapSourceChanged(previewMap.getMapSource());
		// updateZoomLevelCheckBoxes(); // /W again?
		updateGridSizeCombo();
		mTileImageParametersPanel.updateControlsState(); // /W #---
		zoomChanged(previewMap.getZoom());
		gridZoomChanged(previewMap.getGridZoom());
		previewMap.updateMapSelection();
		previewMap.grabFocus();
	}

	/**
	 * map controls
	 */
	private void createMapControls()
	{
		// general controls
		// zoom slider
		zoomSlider = new JSlider(JMapViewer.MIN_ZOOM, previewMap.getMapSource().getMaxZoom());
		zoomSlider.setOrientation(JSlider.HORIZONTAL);
		zoomSlider.setMinimumSize(new Dimension(50, 10));
		zoomSlider.setSize(50, zoomSlider.getPreferredSize().height);
		zoomSlider.addChangeListener(new ZoomSliderListener());
		zoomSlider.setOpaque(false);

		// zoom level text
		zoomLevelText = new JLabel(" 00 ");
		zoomLevelText.setOpaque(true);
		zoomLevelText.setBackground(labelBackgroundColor);
		zoomLevelText.setForeground(labelForegroundColor);
		zoomLevelText.setToolTipText(OSMCDStrs.RStr("map_ctrl_zoom_level_title_tips"));

		// grid zoom combo
		gridZoomCombo = new JComboBox<GridZoom>();
		gridZoomCombo.setEditable(false);
		gridZoomCombo.addActionListener(new GridZoomComboListener());
		gridZoomCombo.setToolTipText(OSMCDStrs.RStr("map_ctrl_zoom_grid_tips"));

		WgsGridSettings s = OSMCDSettings.getInstance().getWgsGrid();

		// WGS Grid label
		wgsGridCheckBox = new JCheckBox(OSMCDStrs.RStr("map_ctrl_wgs_grid_title"), s.enabled);
		// wgsGridCheckBox.setOpaque(true);
		wgsGridCheckBox.setOpaque(true);
		wgsGridCheckBox.setBackground(checkboxBackgroundColor);
		wgsGridCheckBox.setForeground(labelForegroundColor);
		wgsGridCheckBox.setToolTipText(OSMCDStrs.RStr("map_ctrl_wgs_grid_tips"));
		wgsGridCheckBox.setMargin(new Insets(0, 0, 0, 0));
		wgsGridCheckBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				boolean enabled = wgsGridCheckBox.isSelected();
				OSMCDSettings.getInstance().getWgsGrid().enabled = enabled;
				wgsGridCombo.setVisible(enabled);
				previewMap.repaint();
			}
		});

		// WGS Grid combo
		wgsGridCombo = new JComboBox<WgsDensity>(WgsDensity.values());
		wgsGridCombo.setMaximumRowCount(WgsDensity.values().length);
		wgsGridCombo.setVisible(s.enabled);
		wgsGridCombo.setSelectedItem(s.density);
		wgsGridCombo.setToolTipText(OSMCDStrs.RStr("map_ctrl_wgs_grid_density_tips"));
		wgsGridCombo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				WgsDensity d = (WgsDensity) wgsGridCombo.getSelectedItem();
				OSMCDSettings.getInstance().getWgsGrid().density = d;
				previewMap.repaint();
			}
		});

	}

	/**
	 * controls in left side panel
	 */
	private void createLeftPanelControls()
	{
		// map source combo
		mMapSourcePanel = new JMapSourcePanel();
		mMapSourcePanel.addComboActionListener(new MapSourceComboListener());

		// settings button
		settingsButton = new JButton(OSMCDStrs.RStr("Settings.Button"));
		settingsButton.addActionListener(new SettingsButtonListener());
		settingsButton.setToolTipText(OSMCDStrs.RStr("Settings.ButtonTips"));

		// /W #CatOverviev
		// saveCatalogOverviewButton = new JButton(OSMCDStrs.RStr("SaveCatalogOverview.Button"));
		// saveCatalogOverviewButton.addActionListener(new ActionListener()
		// {
		// @Override
		// public void actionPerformed(ActionEvent e)
		// {
		// CatalogOverviewMap overview = new CatalogOverviewMap();
		//
		// overview.callPaintComponent_TWICE();
		// }
		// });
		// saveCatalogOverviewButton.setToolTipText(OSMCDStrs.RStr("SaveCatalogOverview.ButtonTips"));

		// // catalog name text field
		// catalogNameTextField = new JCatalogNameField();
		// catalogNameTextField.setColumns(12);
		// catalogNameTextField.setActionCommand("bundleNameTextField");
		// catalogNameTextField.setToolTipText(OSMCDStrs.RStr("Catalog.NameFieldTip"));

		// zoom level check boxes
		mZoomLevelPanel = new JPanel();
		mZoomLevelPanel.setBorder(BorderFactory.createEmptyBorder());
		mZoomLevelPanel.setOpaque(false);

		// amount of tiles to download
		amountOfTilesLabel = new JLabel();
		amountOfTilesLabel.setToolTipText(OSMCDStrs.RStr("ZoomLevel.AmountOfTiles"));
		amountOfTilesLabel.setOpaque(true);
		amountOfTilesLabel.setBackground(labelBackgroundColor);
		amountOfTilesLabel.setForeground(labelForegroundColor);

		mCoordinatesPanel = new JCoordinatesPanel();
		mTileImageParametersPanel = new JTileImageParametersPanel(); // /W #---
		// /W #--- mCatalogContentPanel = new JCatalogContentPanel(previewMap);
		mCatalogsPanel = new JCatalogsPanel(previewMap);
		// /W +++ mCatalogsPanel = new JCatalogsPanel(mCatalogContentPanel.getCatalogTree());
		// /W +++ mCatalogsPanel = new JCatalogsPanel(new JCatalogTree(previewMap));
		// mCatalogsPanel.getLoadButton().addActionListener(new LoadCatalogListener()); // /W ???
		mTileStoreCoveragePanel = new JTileStoreCoveragePanel(previewMap);
	}

	private void createRightPanelControls()
	{
		// gpx panel ????
	}

	private void prepareMenuBar()
	{
		// Bundle menu // /W #---
		JMenu bundleMenu = new JMenu(OSMCDStrs.RStr("Menu.Catalog"));
		bundleMenu.setMnemonic(KeyEvent.VK_A);

		JMenuItem newCatalogs = new JMenuItem(OSMCDStrs.RStr("Menu.Catalog.New"));
		newCatalogs.setMnemonic(KeyEvent.VK_N);
		newCatalogs.addActionListener(new CatalogNew());
		bundleMenu.add(newCatalogs);

		bundleMenu.addSeparator();

		// Maps menu
		JMenu mapsMenu = new JMenu(OSMCDStrs.RStr("menu_maps"));
		mapsMenu.setMnemonic(KeyEvent.VK_M);
		JMenu selectionModeMenu = new JMenu(OSMCDStrs.RStr("menu_maps_selection"));
		selectionModeMenu.setMnemonic(KeyEvent.VK_M);
		// /W #--- mapsMenu.add(selectionModeMenu);

		smRectangle = new JRadioButtonMenuItem(OSMCDStrs.RStr("menu_maps_selection_rect"));
		smRectangle.addActionListener(new SelectionModeRectangle());
		smRectangle.setSelected(true);
		selectionModeMenu.add(smRectangle);

		smPolygon = new JRadioButtonMenuItem(OSMCDStrs.RStr("menu_maps_selection_polygon"));
		smPolygon.addActionListener(new SelectionModePolygon());
		selectionModeMenu.add(smPolygon);

		smCircle = new JRadioButtonMenuItem(OSMCDStrs.RStr("menu_maps_selection_circle"));
		smCircle.addActionListener(new SelectionModeCircle());
		selectionModeMenu.add(smCircle);

		JMenuItem addSelection = new JMenuItem(OSMCDStrs.RStr("menu_maps_selection_add"));
		addSelection.addActionListener(AddMapLayer.INSTANCE);
		addSelection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		addSelection.setMnemonic(KeyEvent.VK_A);
		mapsMenu.add(addSelection);

		// /W #unused
		// JMenuItem addGpxTrackSelection = new JMenuItem2(OSMCDStrs.RStr("menu_maps_selection_add_around_gpx"), AddGpxTrackPolygonMap.class);
		// /W #--- mapsMenu.add(addGpxTrackSelection);
		// /W #unused
		// JMenuItem addGpxTrackAreaSelection = new JMenuItem2(OSMCDStrs.RStr("menu_maps_selection_add_by_gpx"), AddGpxTrackAreaPolygonMap.class);
		// /W #--- mapsMenu.add(addGpxTrackAreaSelection);

		// // Bookmarks menu
		// bookmarkMenu = new JMenu(OSMCDStrs.RStr("menu_bookmark"));
		// bookmarkMenu.setMnemonic(KeyEvent.VK_B);
		// JMenuItem addBookmark = new JMenuItem(OSMCDStrs.RStr("menu_bookmark_save"));
		// addBookmark.setMnemonic(KeyEvent.VK_S);
		// addBookmark.addActionListener(new BookmarkAdd(previewMap));
		// bookmarkMenu.add(addBookmark);
		// JMenuItem manageBookmarks = new JMenuItem2(OSMCDStrs.RStr("menu_bookmark_manage"), BookmarkManage.class);
		// manageBookmarks.setMnemonic(KeyEvent.VK_S);
		// bookmarkMenu.add(addBookmark);
		// bookmarkMenu.add(manageBookmarks);
		// bookmarkMenu.addSeparator();

		// Panels menu
		JMenu panelsMenu = new JMenu(OSMCDStrs.RStr("menu_panels"));
		panelsMenu.setMnemonic(KeyEvent.VK_P);
		JMenuItem showLeftPanel = new JMenuItem(OSMCDStrs.RStr("menu_show_hide_left_panel"));
		showLeftPanel.addActionListener(new PanelShowHide(leftPane));
		JMenuItem showRightPanel = new JMenuItem(OSMCDStrs.RStr("menu_show_hide_gpx_panel"));
		showRightPanel.addActionListener(new PanelShowHide(rightPane));
		panelsMenu.add(showLeftPanel);
		// /W #--- panelsMenu.add(showRightPanel);

		// /W #--- menuBar.add(bundleMenu);
		menuBar.add(mapsMenu);
		// menuBar.add(bookmarkMenu);
		menuBar.add(panelsMenu);

		loadToolsMenu();

		menuBar.add(Box.createHorizontalGlue());

		// Debug menu
		JMenu debugMenu = new JMenu(OSMCDStrs.RStr("menu_debug"));
		JMenuItem mapGrid = new JCheckBoxMenuItem(OSMCDStrs.RStr("menu_debug_show_hide_tile_border"), false);
		mapGrid.addActionListener(new DebugShowMapTileGrid());
		debugMenu.add(mapGrid);
		debugMenu.addSeparator();

		debugMenu.setMnemonic(KeyEvent.VK_D);
		JMenuItem mapSourceNames = new JMenuItem2(OSMCDStrs.RStr("menu_debug_show_all_map_source"), DebugShowMapSourceNames.class);
		mapSourceNames.setMnemonic(KeyEvent.VK_N);
		debugMenu.add(mapSourceNames);
		debugMenu.addSeparator();

		JMenuItem refreshCustomMapSources = new JMenuItem2(OSMCDStrs.RStr("menu_debug_refresh_map_source"), RefreshCustomMapsources.class);
		debugMenu.add(refreshCustomMapSources);
		debugMenu.addSeparator();
		JMenuItem showLog = new JMenuItem2(OSMCDStrs.RStr("menu_debug_show_log_file"), DebugShowLogFile.class);
		showLog.setMnemonic(KeyEvent.VK_S);
		debugMenu.add(showLog);

		logLevelMenu = new JMenu(OSMCDStrs.RStr("menu_debug_log_level"));
		logLevelMenu.setMnemonic(KeyEvent.VK_L);
		Level[] list = new Level[]
		{ Level.TRACE, Level.DEBUG, Level.INFO, Level.ERROR, Level.FATAL, Level.OFF };
		ActionListener al = new DebugSetLogLevel();
		Level rootLogLevel = Logger.getRootLogger().getLevel();
		for (Level level : list)
		{
			String name = level.toString();
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(name, (rootLogLevel.toString().equals(name)));
			item.setName(name);
			item.addActionListener(al);
			logLevelMenu.add(item);
		}
		debugMenu.add(logLevelMenu);
		debugMenu.addSeparator();
		JMenuItem report = new JMenuItem2(OSMCDStrs.RStr("menu_debug_system_report"), DebugShowReport.class);
		report.setMnemonic(KeyEvent.VK_R);
		debugMenu.add(report);
		menuBar.add(debugMenu);

		// Help menu
		JMenu help = new JMenu(OSMCDStrs.RStr("menu_help"));
		JMenuItem readme = new JMenuItem(OSMCDStrs.RStr("menu_help_readme"));
		JMenuItem howToMap = new JMenuItem(OSMCDStrs.RStr("menu_help_how_to_preview"));
		JMenuItem licenses = new JMenuItem(OSMCDStrs.RStr("menu_help_licenses"));
		JMenuItem about = new JMenuItem(OSMCDStrs.RStr("menu_help_about"));
		readme.addActionListener(new ShowReadme());
		about.addActionListener(new ShowAboutDialog());
		howToMap.addActionListener(new ShowHelpAction());
		licenses.addActionListener(new HelpLicenses());
		help.add(readme);
		help.add(howToMap);
		help.addSeparator();
		help.add(licenses);
		help.addSeparator();
		help.add(about);

		menuBar.add(help);
	}

	public void loadToolsMenu()
	{
		if (ExternalToolsLoader.load())
		{
			if (toolsMenu == null)
			{
				toolsMenu = new JMenu(OSMCDStrs.RStr("menu_tool"));
				toolsMenu.addMenuListener(new MenuListener()
				{
					@Override
					public void menuSelected(MenuEvent e)
					{
						loadToolsMenu();
						log.debug("Tools menu Loaded");
					}

					@Override
					public void menuDeselected(MenuEvent e)
					{
					}

					@Override
					public void menuCanceled(MenuEvent e)
					{
					}
				});
				menuBar.add(toolsMenu);
			}
			toolsMenu.removeAll();
			for (ExternalToolDef t : ExternalToolsLoader.tools)
			{
				JMenuItem m = new JMenuItem(t.name);
				m.addActionListener(t);
				toolsMenu.add(m);
			}
		}
	}

	/**
	 * 20140228 AH the sizing of the left side panel is done here
	 * there are seven panels and one button in the left (catalog) panel
	 * - selection coordinates
	 * - map source
	 * - zoom levels
	 * - layer settings | custom tile processing
	 * - catalog content
	 * - saved catalogs
	 * o settings
	 * - tile store coverage
	 */
	private void updateLeftPanel()
	{
		leftPane.removeAll();

		// /W #--- mCoordinatesPanel.addButtonActionListener(new ApplySelectionButtonListener());

		JCollapsiblePanel zoomLevelsPanel = new JCollapsiblePanel(OSMCDStrs.RStr("Zoomlevel.Title"), new GridBagLayout());
		zoomLevelsPanel.addContent(mZoomLevelPanel, GBC.eol().insets(2, 4, 2, 0));
		zoomLevelsPanel.addContent(amountOfTilesLabel, GBC.std().anchor(GBC.WEST).insets(0, 5, 0, 2));

		@SuppressWarnings("unused")
		// /W
		GBC gbc_std = GBC.std().insets(5, 2, 5, 3);
		GBC gbc_eol = GBC.eol().insets(5, 2, 5, 3);

		// mCatalogContentPanel = new JCatalogContentPanel(previewMap);
		// /W mCatalogContentPanel.addListener(mCatalogsPanel);
		// /W wird zu
		// mCatalogsPanel.addListener(); // /W +++ ?wieso hier -> JCatalogsPanel(PreviewMap previewMap)

		// // The catalog content panel hosts a collapsible tree
		// JCollapsiblePanel mCatalogContentPanel = new JCollapsiblePanel(OSMCDStrs.RStr("CatalogTree.Title"), new GridBagLayout());
		// JScrollPane treeScrollPane = new JScrollPane(jCatalogTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// jCatalogTree.getTreeModel().addTreeModelListener(new CatalogModelListener(jCatalogTree, mCatalogsPanel));
		//
		// treeScrollPane.setMinimumSize(new Dimension(100, 150));
		// treeScrollPane.setPreferredSize(new Dimension(100, 200));
		// treeScrollPane.setAutoscrolls(true);
		// mCatalogContentPanel.addContent(treeScrollPane, GBC.eol().fill().insets(0, 1, 0, 0));
		// JButton clearCatalog = new JButton(OSMCDStrs.RStr("CatalogTree.NewButton"));
		// mCatalogContentPanel.addContent(clearCatalog, GBC.std());
		// clearCatalog.addActionListener(new CatalogNew());
		// JButton addLayers = new JButton(OSMCDStrs.RStr("CatalogTree.AddSelected"));
		// mCatalogContentPanel.addContent(addLayers, GBC.eol());
		// addLayers.addActionListener(AddMapLayer.INSTANCE);
		// mCatalogContentPanel.addContent(new JLabel(OSMCDStrs.RStr("CatalogTree.NameLabel")), gbc_std);
		// mCatalogContentPanel.addContent(catalogNameTextField, gbc_eol.fill(GBC.HORIZONTAL));

		gbc_eol = GBC.eol().insets(5, 2, 10, 2).fill(GBC.HORIZONTAL);// /W alt (5, 2, 5, 2) wg Scrollbar

		leftPanelContent = new JPanel(new GridBagLayout());
		leftPanelContent.add(mCatalogsPanel, gbc_eol);
		// /W +++ leftPanelContent.add(mCatalogContentPanel, gbc_eol);
		leftPanelContent.add(mMapSourcePanel, gbc_eol);
		leftPanelContent.add(mTileStoreCoveragePanel, gbc_eol);
		leftPanelContent.add(zoomLevelsPanel, gbc_eol);
		leftPanelContent.add(mCoordinatesPanel, gbc_eol);// /W //weg => funktioniert
		// /W #--- leftPanelContent.add(mTileImageParametersPanel, gbc_eol);
		// leftPanelContent.add(createAtlasButton, gbc_eol);
		leftPanelContent.add(settingsButton, gbc_eol);
		leftPanelContent.add(Box.createVerticalGlue(), GBC.eol().fill(GBC.VERTICAL));

		// /W #CatOverviev
		// leftPanelContent.add(saveCatalogOverviewButton, gbc_eol);

		JScrollPane scrollPane = new JScrollPane(leftPanelContent);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		// Set the scroll pane width large enough so that the
		// scroll bar has enough space to appear right to it
		Dimension d = scrollPane.getPreferredSize();
		d.width += 0 + scrollPane.getVerticalScrollBar().getWidth();// /W old: += 5 + (to see more of scrollbar)
		// scrollPane.setPreferredSize(d);
		scrollPane.setMinimumSize(d);
		leftPane.add(scrollPane, GBC.std().fill());
		// leftPane.add(leftPanelContent, GBC.std().fill());
	}

	private void updateRightPanel()
	{
		GBC gbc_eol = GBC.eol().insets(5, 2, 5, 2).fill();
		mGpxPanel = new JGpxPanel(previewMap);
		rightPane.add(mGpxPanel, gbc_eol);
	}

	private JPanel updateMapControlsPanel()
	{
		mapControlsPanel.removeAll();
		mapControlsPanel.setOpaque(false);

		// zoom label
		JLabel zoomLabel = new JLabel(OSMCDStrs.RStr("map_ctrl_zoom_level_title"));
		zoomLabel.setOpaque(true);
		zoomLabel.setBackground(labelBackgroundColor);
		zoomLabel.setForeground(labelForegroundColor);

		// top panel
		JPanel topControls = new JPanel(new GridBagLayout());
		topControls.setOpaque(false);
		topControls.add(zoomLabel, GBC.std().insets(5, 5, 0, 0));
		topControls.add(zoomSlider, GBC.std().insets(0, 5, 0, 0));
		topControls.add(zoomLevelText, GBC.std().insets(0, 5, 0, 0));
		topControls.add(gridZoomCombo, GBC.std().insets(10, 5, 0, 0));
		topControls.add(wgsGridCheckBox, GBC.std().insets(10, 5, 0, 0));
		topControls.add(wgsGridCombo, GBC.std().insets(5, 5, 0, 0));
		topControls.add(Box.createHorizontalGlue(), GBC.std().fillH());
		mapControlsPanel.add(topControls, BorderLayout.NORTH);

		// bottom panel
		// JPanel bottomControls = new JPanel(new GridBagLayout());
		// bottomControls.setOpaque(false);
		// bottomControls.add(Box.createHorizontalGlue(),
		// GBC.std().fill(GBC.HORIZONTAL));
		// mapControlPanel.add(bottomControls, BorderLayout.SOUTH);

		return mapControlsPanel;
	}

	public void updateMapSourcesList()
	{
		IfMapSource ms = mMapSourcePanel.getSelectedMapSource();
		// /W
		mMapSourcePanel.updateMapSourceComboBox();
		IfMapSource ms2 = mMapSourcePanel.getSelectedMapSource();
		if (!ms.equals(ms2))
			handleNewMapSource();
	}

	// public void updateBookmarksMenu()
	// {
	// LinkedList<JMenuItem> items = new LinkedList<JMenuItem>();
	// for (int i = 0; i < bookmarkMenu.getMenuComponentCount(); i++)
	// {
	// JMenuItem item = bookmarkMenu.getItem(i);
	// if (!(item instanceof JBookmarkMenuItem))
	// items.add(item);
	// }
	// bookmarkMenu.removeAll();
	// for (JMenuItem item : items)
	// {
	// if (item != null)
	// bookmarkMenu.add(item);
	// else
	// bookmarkMenu.addSeparator();
	// }
	// for (Bookmark b : OSMCDSettings.getInstance().placeBookmarks)
	// {
	// bookmarkMenu.add(new JBookmarkMenuItem(b));
	// }
	// }

	/**
	 * 
	 */
	private void loadSettings()
	{
		// new CatalogNew().actionPerformed(null);

		OSMCDSettings settings = OSMCDSettings.getInstance();

		previewMap.settingsLoad();
		int nextZoom = 0;
		List<Integer> zoomList = settings.getSelectedZoomLevels();
		if (zoomList != null)
		{
			for (JZoomCheckBox currentZoomCb : cbZoom)
			{
				for (int i = nextZoom; i < zoomList.size(); i++)
				{
					int currentListZoom = zoomList.get(i);
					if (currentZoomCb.getZoomLevel() == currentListZoom)
					{
						currentZoomCb.setSelected(true);
						// nextZoom = 1; // /W Nutzen?: Fehler bei nicht festgelegter Reihenfolge in zoomList
						break;
					}
				}
			}
		}
		mCoordinatesPanel.setNumberFormat(settings.getCoordinateNumberFormat());

		mTileImageParametersPanel.loadSettings(); // /W #---
		// mTileImageParametersPanel.bundleFormatChanged(jBundleTree.getBundle().getOutputFormat());
		// mapSourceCombo
		// .setSelectedItem(MapSourcesManager.getSourceByName(settings.
		// mapviewMapSource));

		setSize(settings.getMainWindow().size);
		Point windowLocation = settings.getMainWindow().position;
		if (windowLocation.x == -1 && windowLocation.y == -1)
		{
			setLocationRelativeTo(null);
		}
		else
		{
			setLocation(windowLocation);
		}
		if (settings.getMainWindow().maximized)
			setExtendedState(Frame.MAXIMIZED_BOTH);

		leftPane.setVisible(settings.getMainWindow().leftPanelVisible);
		// /W #--- rightPane.setVisible(settings.getMainWindow().rightPanelVisible);

		if (leftPanelContent != null)
		{
			for (Component c : leftPanelContent.getComponents())
			{
				if (c instanceof JCollapsiblePanel)
				{
					JCollapsiblePanel cp = (JCollapsiblePanel) c;
					String name = cp.getName();
					if (name != null && settings.getMainWindow().collapsedPanels.contains(name))
						cp.setCollapsed(true);
				}
			}
		}
		// updateBookmarksMenu();
	}

	private void saveSettings()
	{
		try
		{
			// jCatalogTree.save()
			if (!mCatalogsPanel.getCatalog().isEmpty())
				mCatalogsPanel.getCatalogTree().save();
			// else: empty catalog -> do nothing

			OSMCDSettings s = OSMCDSettings.getInstance();
			previewMap.settingsSave();
			s.setMapviewMapSource(previewMap.getMapSource().getName());
			s.setSelectedZoomLevels(new SelectedZoomLevels(cbZoom).getZoomLevelList());
			s.setCoordinateNumberFormat(mCoordinatesPanel.getNumberFormat());
			mTileImageParametersPanel.saveSettings(); // /W #---
			boolean maximized = (getExtendedState() & Frame.MAXIMIZED_BOTH) != 0;
			s.getMainWindow().maximized = maximized;
			if (!maximized)
			{
				s.getMainWindow().size = getSize();
				s.getMainWindow().position = getLocation();
			}
			s.getMainWindow().collapsedPanels.clear();
			if (leftPanelContent != null)
			{
				for (Component c : leftPanelContent.getComponents())
				{
					if (c instanceof JCollapsiblePanel)
					{
						JCollapsiblePanel cp = (JCollapsiblePanel) c;
						if (cp.isCollapsed())
							s.getMainWindow().collapsedPanels.add(cp.getName());
					}
				}
			}
			s.getMainWindow().leftPanelVisible = leftPane.isVisible();
			// /W #--- s.getMainWindow().rightPanelVisible = rightPane.isVisible();
			checkAndSaveSettings();
		}
		catch (Exception e)
		{
			GUIExceptionHandler.showExceptionDialog(e);
			JOptionPane.showMessageDialog(null, OSMCDStrs.RStr("msg_settings_write_error"), OSMCDStrs.RStr("Error"), JOptionPane.ERROR_MESSAGE);
		}
	}

	public void checkAndSaveSettings() throws JAXBException
	{
		// if (OSMCDSettings.checkSettingsFileModified())
		// {
		// int x = JOptionPane.showConfirmDialog(this, OSMCDStrs.RStr("msg_setting_file_is_changed_by_other"),
		// OSMCDStrs.RStr("msg_setting_file_is_changed_by_other_title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		// if (x != JOptionPane.YES_OPTION)
		// return;
		// }
		OSMCDSettings.save();
	}

	// /W #---
	public JTileImageParametersPanel getParametersPanel()
	{
		return mTileImageParametersPanel;
	}

	// public String getUserTextCatalogName()
	// {
	// return catalogNameTextField.getText();
	// }
	//
	public void refreshPreviewMap()
	{
		previewMap.refreshMap();
	}

	private class ZoomSliderListener implements ChangeListener
	{
		@Override
		public void stateChanged(ChangeEvent e)
		{
			previewMap.setZoom(zoomSlider.getValue());
		}
	}

	private class GridZoomComboListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!gridZoomCombo.isEnabled())
				return;
			GridZoom g = (GridZoom) gridZoomCombo.getSelectedItem();
			if (g == null)
				return;
			log.debug("Selected grid zoom combo box item has changed: " + g.getZoom());
			previewMap.setGridZoom(g.getZoom());

			// /W #selCoord
			if (g.getZoom() < 0)
				CoordinateTileFormat.setActZoom(JMapViewer.MAX_ZOOM);
			else
				CoordinateTileFormat.setActZoom(g.getZoom());

			repaint();
			previewMap.updateMapSelection();
		}
	}

	private void updateGridSizeCombo()
	{
		int maxZoom = previewMap.getMapSource().getMaxZoom();
		int minZoom = previewMap.getMapSource().getMinZoom();
		GridZoom lastGridZoom = (GridZoom) gridZoomCombo.getSelectedItem();
		gridZoomCombo.setEnabled(false);
		gridZoomCombo.removeAllItems();
		gridZoomCombo.setMaximumRowCount(maxZoom - minZoom + 2);
		gridZoomCombo.addItem(new GridZoom(-1)
		{

			@Override
			public String toString()
			{
				return OSMCDStrs.RStr("map_ctrl_zoom_grid_disable");
			}

		});
		for (int i = maxZoom; i >= minZoom; i--)
		{
			gridZoomCombo.addItem(new GridZoom(i));
		}
		if (lastGridZoom != null)
			gridZoomCombo.setSelectedItem(lastGridZoom);
		gridZoomCombo.setEnabled(true);
	}

	@SuppressWarnings("unused") // /W #unused
	private class ApplySelectionButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			setSelectionByEnteredCoordinates();
		}
	}

	// /W auch in updateMapSourcesList() benötigt
	private void handleNewMapSource()
	{
		IfMapSource mapSource = mMapSourcePanel.getSelectedMapSource();
		// IfMapSource mapSource = (IfMapSource) mapSourceCombo.getSelectedItem();
		if (mapSource instanceof IfInitializableMapSource)
		  // initialize the map source e.g. detect available zoom levels
		  ((IfInitializableMapSource) mapSource).initialize();

		previewMap.setMapSource(mapSource);
		zoomSlider.setMinimum(mapSource.getMinZoom());
		zoomSlider.setMaximum(mapSource.getMaxZoom());
		updateGridSizeCombo();
		updateZoomLevelCheckBoxes();
		calculateNrOfTilesToDownload();
	}

	/**
	 * handles a newly selected map source from the list
	 * 
	 * @author humbach
	 *
	 */
	public class MapSourceComboListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			handleNewMapSource(); // /W
		}
	}

	// private class LoadCatalogListener implements ActionListener
	// {
	// @Override
	// public void actionPerformed(ActionEvent e)
	// {
	// IfCatalogProfile profile = mCatalogsPanel.getSelectedCatalog();
	// mCatalogsPanel.getDeleteButton().setEnabled(profile != null);
	// if (profile == null)
	// return;
	//
	// // jCatalogTree.load(profile);
	//
	// // /W mCatalogContentPanel.getCatalogTree().load(profile);
	// // /W +++ wird zu
	// mCatalogsPanel.getCatalogTree().load(profile);
	//
	//
	// previewMap.repaint();
	// // mTileImageParametersPanel.bundleFormatChanged(jBundleTree.getBundle().getOutputFormat());
	// }
	// }

	private class SettingsButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			SettingsGUI.showSettingsDialog(MainFrame.this, OSMCDSettings.getInstance().getSettingsTabSelected());
		}
	}

	private void updateZoomLevelCheckBoxes()
	{
		IfMapSource tileSource = previewMap.getMapSource();
		// int zoomLevels = tileSource.getMaxZoom() - tileSource.getMinZoom() + 1; // #zoom0-3
		int minZoom = Math.max(Catalog.MIN_CATALOG_ZOOMLEVEL, tileSource.getMinZoom());
		int zoomLevels = tileSource.getMaxZoom() - minZoom + 1;

		zoomLevels = Math.max(zoomLevels, 0);
		JCheckBox[] oldZoomLevelCheckBoxes = cbZoom;
		int oldMinZoom = 0;
		if (cbZoom.length > 0)
			oldMinZoom = cbZoom[0].getZoomLevel();
		cbZoom = new JZoomCheckBox[zoomLevels];
		mZoomLevelPanel.removeAll();

		mZoomLevelPanel.setLayout(new GridLayout(0, 10, 1, 2));
		ZoomLevelCheckBoxListener cbl = new ZoomLevelCheckBoxListener();

		for (int i = cbZoom.length - 1; i >= 0; i--)
		{
			int cbz = i + tileSource.getMinZoom();
			JZoomCheckBox cb = new JZoomCheckBox(cbz);
			cb.setPreferredSize(new Dimension(22, 11));
			cb.setMinimumSize(cb.getPreferredSize());
			cb.setOpaque(false);
			cb.setFocusable(false);
			cb.setName(Integer.toString(cbz));
			int oldCbIndex = cbz - oldMinZoom;
			if (oldCbIndex >= 0 && oldCbIndex < (oldZoomLevelCheckBoxes.length))
				cb.setSelected(oldZoomLevelCheckBoxes[oldCbIndex].isSelected());
			cb.addActionListener(cbl);
			// cb.setToolTipText("Select zoom level " + cbz + " for atlas");
			mZoomLevelPanel.add(cb);
			cbZoom[i] = cb;

			JLabel l = new JLabel(Integer.toString(cbz));
			mZoomLevelPanel.add(l);
		}
		amountOfTilesLabel.setOpaque(false);
		amountOfTilesLabel.setForeground(Color.black);
	}

	private class ZoomLevelCheckBoxListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			calculateNrOfTilesToDownload();
		}
	}

	@Override
	public void selectionChanged(MercatorPixelCoordinate max, MercatorPixelCoordinate min)
	{
		mapSelectionMax = max;
		mapSelectionMin = min;
		mCoordinatesPanel.setSelection(max, min);
		calculateNrOfTilesToDownload();
	}

	@Override
	public void zoomChanged(int zoomLevel)
	{
		zoomLevelText.setText(" " + zoomLevel + " ");
		zoomSlider.setValue(zoomLevel);
	}

	@Override
	public void gridZoomChanged(int newGridZoomLevel)
	{
		gridZoomCombo.setSelectedItem(new GridZoom(newGridZoomLevel));
	}

	public IfMapSource getSelectedMapSource()
	{
		return mMapSourcePanel.getSelectedMapSource();
	}

	public SelectedZoomLevels getSelectedZoomLevels()
	{
		return new SelectedZoomLevels(cbZoom);
	}

	@Override
	public void mapSourceChanged(IfMapSource newMapSource)
	{
		// TODO update selected area if new map source has different projectionCategory
		calculateNrOfTilesToDownload();
		if ((newMapSource != null) && (!newMapSource.equals(mMapSourcePanel.getSelectedMapSource())))
			mMapSourcePanel.selectMapSource(newMapSource);
	}

	@Override
	public void mapSelectionControllerChanged(ACMapController newMapController)
	{
		smPolygon.setSelected(false);
		smCircle.setSelected(false);
		smRectangle.setSelected(false);
		if (newMapController instanceof PolygonSelectionMapController)
			smPolygon.setSelected(true);
		else if (newMapController instanceof PolygonCircleSelectionMapController)
			smCircle.setSelected(true);
		else if (newMapController instanceof RectangleSelectionMapController)
			smRectangle.setSelected(true);
	}

	private void setSelectionByEnteredCoordinates()
	{
		mCoordinatesPanel.correctMinMax();
		MapSelection ms = mCoordinatesPanel.getMapSelection(previewMap.getMapSource());
		mapSelectionMax = ms.getBottomRightPixelCoordinate();
		mapSelectionMin = ms.getTopLeftPixelCoordinate();
		previewMap.setSelectionAndZoomTo(ms, false);
	}

	public MapSelection getMapSelectionCoordinates()
	{
		if (mapSelectionMax == null || mapSelectionMin == null)
			return null;
		return new MapSelection(previewMap.getMapSource(), mapSelectionMax, mapSelectionMin);
	}

	// /W #---
	public TileImageParameters getSelectedTileImageParameters()
	{
		return mTileImageParametersPanel.getSelectedTileImageParameters();
	}

	private void calculateNrOfTilesToDownload()
	{
		MapSelection ms = getMapSelectionCoordinates();
		String baseText;
		baseText = OSMCDStrs.RStr("lp_zoom_total_tile_title");
		if (ms == null || !ms.isAreaSelected())
		{
			amountOfTilesLabel.setText(String.format(baseText, "0"));
			amountOfTilesLabel.setToolTipText("");
		}
		else
		{
			try
			{
				SelectedZoomLevels sZL = new SelectedZoomLevels(cbZoom);
				int[] zoomLevels = sZL.getZoomLevels();
				long totalNrOfTiles = 0;
				StringBuilder hint = new StringBuilder(1024);
				hint.append(OSMCDStrs.RStr("lp_zoom_total_tile_hint_head"));
				for (int i = 0; i < zoomLevels.length; i++)
				{
					int zoom = zoomLevels[i];
					long[] info = ms.calculateNrOfTilesEx(zoom);
					totalNrOfTiles += info[0];
					hint.append(String.format(OSMCDStrs.RStr("lp_zoom_total_tile_hint_row"), zoomLevels[i], info[0], info[1], info[2]));
					// hint.append("<br>Level " + zoomLevels[i] + ": " + info[0] + " (" + info[1] + "*" + info[2] +
					// ")");
				}
				String hintText = "<html>" + hint.toString() + "</html>";
				amountOfTilesLabel.setText(String.format(baseText, Long.toString(totalNrOfTiles)));
				amountOfTilesLabel.setToolTipText(hintText);
			}
			catch (Exception e)
			{
				amountOfTilesLabel.setText(String.format(baseText, "?"));
				log.error("", e);
			}
		}
	}

	private class WindowDestroyer extends WindowAdapter
	{
		@Override
		public void windowOpened(WindowEvent e)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					previewMap.setEnabled(true);
				}
			});
		}

		@Override
		public void windowClosing(WindowEvent event)
		{
			saveSettings();
		}
	}

	/**
	 * Saves the window position and size when window is moved or resized. This is necessary because of the maximized state. If a window is maximized it is
	 * impossible to retrieve the window size & position of the non-maximized window - therefore we have to collect the information every time they change.
	 */
	private class MainWindowListener extends ComponentAdapter
	{
		@Override
		public void componentResized(ComponentEvent event)
		{
			// log.debug(event.paramString());
			updateValues();
		}

		@Override
		public void componentMoved(ComponentEvent event)
		{
			// log.debug(event.paramString());
			updateValues();
		}

		private void updateValues()
		{
			// only update old values while window is in NORMAL state
			// Note(Java bug): Sometimes getExtendedState() says the window is
			// not maximized but maximizing is already in progress and therefore
			// the window bounds are already changed.
			if ((getExtendedState() & MAXIMIZED_BOTH) != 0)
				return;
			OSMCDSettings s = OSMCDSettings.getInstance();
			s.getMainWindow().size = getSize();
			s.getMainWindow().position = getLocation();
		}
	}

	@Override
	public void selectNextMapSource()
	{
		mMapSourcePanel.selectNextMapSource();
	}

	@Override
	public void selectPreviousMapSource()
	{
		mMapSourcePanel.selectPreviousMapSource();
	}

	/**
	 * shouldn't give GUI object out, hide the access with some public methods
	 * 
	 * @return
	 */
	public JCatalogTree getCatalogTree()
	{
		return mCatalogsPanel.getCatalogTree(); // /W +++ mCatalogContentPanel ersetzt durch mCatalogsPanel
	}

	public String getCatalogName()
	{
		return mCatalogsPanel.getCatalog().getName(); // /W +++ mCatalogContentPanel ersetzt durch mCatalogsPanel
	}

	public GpxEntry getSelectedGpx()
	{
		return mGpxPanel.getSelectedEntry();
	}

	/**
	 * notify main frame that a new layer has to be inserted into the current catalog
	 * 
	 * @param layer
	 */
	public void notifyLayerInsert(Layer layer)
	{
		mCatalogsPanel.getCatalogTree().getTreeModel().notifyNodeInsert(layer); // /W +++ mCatalogContentPanel ersetzt durch mCatalogsPanel
	}

	public Catalog getCatalog()
	{
		return mCatalogsPanel.getCatalog(); // /W +++ mCatalogContentPanel ersetzt durch mCatalogsPanel
	}

	// /W #--- +++ Menü-Eintrag?
	public void newCatalog(String newName)
	{
		mCatalogsPanel.getCatalogTree().newCatalog(newName); // /W +++ mCatalogContentPanel ersetzt durch mCatalogsPanel
	}
}
