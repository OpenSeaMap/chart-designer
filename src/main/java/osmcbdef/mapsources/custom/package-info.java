@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters({
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(
		type = osmcbdef.program.model.TileImageType.class, 
		value = osmcbdef.program.jaxb.TileImageTypeAdapter.class
	),
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(
			type = java.awt.Color.class, 
			value = osmcbdef.program.jaxb.ColorAdapter.class
		)
	
})
package osmcbdef.mapsources.custom;