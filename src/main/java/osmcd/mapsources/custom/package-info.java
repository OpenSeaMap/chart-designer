@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters({
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(
		type = osmcd.program.model.TileImageType.class, 
		value = osmcd.program.jaxb.TileImageTypeAdapter.class
	),
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(
			type = java.awt.Color.class, 
			value = osmcd.program.jaxb.ColorAdapter.class
		)
	
})
package osmcd.mapsources.custom;