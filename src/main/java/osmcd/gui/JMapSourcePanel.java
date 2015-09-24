package osmcd.gui;

import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import osmb.mapsources.ACMapSourcesManager;
import osmb.mapsources.IfMapSource;
import osmb.utilities.GBC;
import osmcd.OSMCDStrs;
import osmcd.gui.MainFrame.MapSourceComboListener;
import osmcd.gui.components.JCollapsiblePanel;

@SuppressWarnings("serial") // /W
public class JMapSourcePanel extends JCollapsiblePanel
{
	private JComboBox<IfMapSource> mapSourceCombo;

// /W
//	public JMapSourcePanel(Container container, String title)
//	{
//		super(container, title);
//		// TODO Auto-generated constructor stub
//	}
//
//	public JMapSourcePanel(Container container)
//	{
//		super(container);
//		// TODO Auto-generated constructor stub
//	}
//
//	public JMapSourcePanel(String title, LayoutManager layout)
//	{
//		super(title, layout);
//		// TODO Auto-generated constructor stub
//	}
//
//	public JMapSourcePanel(String title)
//	{
//		super(title);
//		JCollapsiblePanel mapSourcePanel = new JCollapsiblePanel(title, new GridBagLayout());
//		mapSourcePanel.addContent(mapSourceCombo, GBC.std().insets(2, 2, 2, 2).fill());
//	}

	public JMapSourcePanel()
	{
		super(OSMCDStrs.RStr("MapSources.Title"), new GridBagLayout());
		// map source combo
		mapSourceCombo = new JComboBox<IfMapSource>(ACMapSourcesManager.getInstance().getEnabledOrderedMapSources());
		mapSourceCombo.setMaximumRowCount(20);
		mapSourceCombo.setToolTipText(OSMCDStrs.RStr("MapSourceCombo.ToolTips"));
		addContent(mapSourceCombo, GBC.std().insets(2, 2, 2, 2).fill());
		// /W addSccollBar horizontal ?
	}

	public IfMapSource getSelectedMapSource()
	{
//		IfMapSource ms = (IfMapSource) mapSourceCombo.getSelectedItem();
//		// mapSourceCombo.setSelectedItem(ms);
//		if (ms == null)
//		{
//			mapSourceCombo.setModel(new DefaultComboBoxModel<IfMapSource>(ACMapSourcesManager.getInstance().getEnabledOrderedMapSources()));
//			mapSourceCombo.setSelectedIndex(0);
//			ms = (IfMapSource) mapSourceCombo.getSelectedItem();
//		}
		// /W ? code above? => //
		return (IfMapSource) mapSourceCombo.getSelectedItem();
	}
	
	// /W
	public void updateMapSourceComboBox()
	{
		IfMapSource ms = getSelectedMapSource();
		mapSourceCombo.setModel(new DefaultComboBoxModel<IfMapSource>(ACMapSourcesManager.getInstance().getEnabledOrderedMapSources()));
		mapSourceCombo.setSelectedItem(ms);
	}

	public IfMapSource selectMapSource(IfMapSource newMapSource)
	{
		mapSourceCombo.setSelectedItem(newMapSource);
		return (IfMapSource) mapSourceCombo.getSelectedItem();
	}

	public IfMapSource selectNextMapSource()
	{
		if (mapSourceCombo.getSelectedIndex() == mapSourceCombo.getItemCount() - 1)
		{
			Toolkit.getDefaultToolkit().beep();
		}
		else
		{
			mapSourceCombo.setSelectedIndex(mapSourceCombo.getSelectedIndex() + 1);
		}
		return (IfMapSource) mapSourceCombo.getSelectedItem();
	}

	public IfMapSource selectPreviousMapSource()
	{
		if (mapSourceCombo.getSelectedIndex() == 0)
		{
			Toolkit.getDefaultToolkit().beep();
		}
		else
		{
			mapSourceCombo.setSelectedIndex(mapSourceCombo.getSelectedIndex() - 1);
		}
		return (IfMapSource) mapSourceCombo.getSelectedItem();
	}

	public void addComboActionListener(MapSourceComboListener mapSourceComboListener)
	{
		mapSourceCombo.addActionListener(mapSourceComboListener);
	}
}
