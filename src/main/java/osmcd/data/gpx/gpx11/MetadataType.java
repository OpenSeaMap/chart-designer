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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.08.04 at 03:45:03 PM MESZ 
//

package osmcd.data.gpx.gpx11;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * 
 * Information about the GPX file, author, and copyright restrictions goes in the metadata section. Providing rich, meaningful information about your GPX files
 * allows others to search for and use your GPS data.
 * 
 * 
 * <p>
 * Java class for metadataType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="metadataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="author" type="{http://www.topografix.com/GPX/1/1}personType" minOccurs="0"/>
 *         &lt;element name="copyright" type="{http://www.topografix.com/GPX/1/1}copyrightType" minOccurs="0"/>
 *         &lt;element name="link" type="{http://www.topografix.com/GPX/1/1}linkType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="keywords" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bounds" type="{http://www.topografix.com/GPX/1/1}boundsType" minOccurs="0"/>
 *         &lt;element name="extensions" type="{http://www.topografix.com/GPX/1/1}extensionsType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "metadataType", propOrder = {"name", "desc", "author", "copyright", "link", "time", "keywords", "bounds", "extensions"})
public class MetadataType
{

	protected String name;
	protected String desc;
	protected PersonType author;
	protected CopyrightType copyright;
	protected List<LinkType> link;
	protected XMLGregorianCalendar time;
	protected String keywords;
	protected BoundsType bounds;
	protected ExtensionsType extensions;

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *          allowed object is {@link String }
	 * 
	 */
	public void setName(String value)
	{
		this.name = value;
	}

	/**
	 * Gets the value of the desc property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDesc()
	{
		return desc;
	}

	/**
	 * Sets the value of the desc property.
	 * 
	 * @param value
	 *          allowed object is {@link String }
	 * 
	 */
	public void setDesc(String value)
	{
		this.desc = value;
	}

	/**
	 * Gets the value of the author property.
	 * 
	 * @return possible object is {@link PersonType }
	 * 
	 */
	public PersonType getAuthor()
	{
		return author;
	}

	/**
	 * Sets the value of the author property.
	 * 
	 * @param value
	 *          allowed object is {@link PersonType }
	 * 
	 */
	public void setAuthor(PersonType value)
	{
		this.author = value;
	}

	/**
	 * Gets the value of the copyright property.
	 * 
	 * @return possible object is {@link CopyrightType }
	 * 
	 */
	public CopyrightType getCopyright()
	{
		return copyright;
	}

	/**
	 * Sets the value of the copyright property.
	 * 
	 * @param value
	 *          allowed object is {@link CopyrightType }
	 * 
	 */
	public void setCopyright(CopyrightType value)
	{
		this.copyright = value;
	}

	/**
	 * Gets the value of the link property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside
	 * the JAXB object. This is why there is not a <CODE>set</CODE> method for the link property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getLink().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link LinkType }
	 * 
	 * 
	 */
	public List<LinkType> getLink()
	{
		if (link == null)
		{
			link = new ArrayList<LinkType>();
		}
		return this.link;
	}

	/**
	 * Gets the value of the time property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getTime()
	{
		return time;
	}

	/**
	 * Sets the value of the time property.
	 * 
	 * @param value
	 *          allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setTime(XMLGregorianCalendar value)
	{
		this.time = value;
	}

	/**
	 * Gets the value of the keywords property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getKeywords()
	{
		return keywords;
	}

	/**
	 * Sets the value of the keywords property.
	 * 
	 * @param value
	 *          allowed object is {@link String }
	 * 
	 */
	public void setKeywords(String value)
	{
		this.keywords = value;
	}

	/**
	 * Gets the value of the bounds property.
	 * 
	 * @return possible object is {@link BoundsType }
	 * 
	 */
	public BoundsType getBounds()
	{
		return bounds;
	}

	/**
	 * Sets the value of the bounds property.
	 * 
	 * @param value
	 *          allowed object is {@link BoundsType }
	 * 
	 */
	public void setBounds(BoundsType value)
	{
		this.bounds = value;
	}

	/**
	 * Gets the value of the extensions property.
	 * 
	 * @return possible object is {@link ExtensionsType }
	 * 
	 */
	public ExtensionsType getExtensions()
	{
		return extensions;
	}

	/**
	 * Sets the value of the extensions property.
	 * 
	 * @param value
	 *          allowed object is {@link ExtensionsType }
	 * 
	 */
	public void setExtensions(ExtensionsType value)
	{
		this.extensions = value;
	}

}
