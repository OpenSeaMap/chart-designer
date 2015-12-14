package osmcd;

import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

//import osmcb.OSMCBApp;

public class OSMCDRsc extends osmb.utilities.OSMBRsc
{
	public static Locale currLocale;
	public static String localeLanguage;
	public static String localeCountry;
	@SuppressWarnings("unused") // W #unused
	private static ResourceBundle STRING_RESOURCE = null;

	public static void initializeRSTRs()
	{
		try
		{
			currLocale = getLocale();
			STRING_RESOURCE = ResourceBundle.getBundle("osmcd.resources.text.loc-nls");
		}
		catch (MissingResourceException e)
		{
		}
	}

	public static void initializeRSTRs(Locale newLocale)
	{
		try
		{
			STRING_RESOURCE = ResourceBundle.getBundle("osmcd.resources.text.loc-nls", newLocale);
			currLocale = newLocale;
		}
		catch (MissingResourceException e)
		{
		}
	}

	public static synchronized void updateLocalizedStrings()
	{
		try
		{
			Locale locale = new Locale(localeLanguage, localeCountry);
			// STRING_RESOURCE = ResourceBundle.getBundle("resources", locale, new UTF8Control());
			STRING_RESOURCE = ResourceBundle.getBundle("loc-nls", locale);
		}
		catch (Exception e)
		{
		}
	}

	public static InputStream getResourceAsStream(String name, String extension)
	{
		getLocale();
		InputStream in;
		in = OSMCDRsc.class.getResourceAsStream(String.format("%s_%s_%s.%s", name, localeLanguage, localeCountry, extension));
		if (in != null)
			return in;
		in = OSMCDRsc.class.getResourceAsStream(String.format("%s_%s.%s", name, localeLanguage, extension));
		if (in != null)
			return in;
		in = OSMCDRsc.class.getResourceAsStream(String.format("%s.%s", name, extension));
		return in;
	}
}
