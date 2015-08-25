<<<<<<< HEAD
package osmcd;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class OSMCDStrs
{
	private static final Logger log = Logger.getLogger(OSMCDStrs.class);

	private static final String BUNDLE_NAME = "osmcd.resources.text.loc-nls";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private OSMCDStrs()
	{
		// log.info("RSC bundle='" + RESOURCE_BUNDLE.getBaseBundleName() + "'");
		log.info("RSC bundle='" + BUNDLE_NAME + "'");
	}

	public static String RStr(String key)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e)
		{
			// log.info("RSC bundle='" + RESOURCE_BUNDLE.getBaseBundleName() + "' missing");
			log.info("RSC key='" + key + "' from bundle='" + BUNDLE_NAME + "' missing");
			return '!' + key + '!';
		}
	}
}
=======
package osmcd;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class OSMCDStrs
{
	private static final String BUNDLE_NAME = "osmcd.resources.text.loc-nls";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private OSMCDStrs() {
	}

	public static String RStr(String key)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}
}
>>>>>>> f8aa735da6b335186129503e00a72e25e428f318
