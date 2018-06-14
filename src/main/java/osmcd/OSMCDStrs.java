package osmcd;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class OSMCDStrs
{
	private static final Logger sLog = Logger.getLogger(OSMCDStrs.class);

	private static final String BUNDLE_NAME = "osmcd.resources.text.loc-nls";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private OSMCDStrs()
	{
		// sLog.info("RSC bundle='" + RESOURCE_BUNDLE.getBaseBundleName() + "'");
		sLog.info("RSC bundle='" + BUNDLE_NAME + "'");
	}

	public static String RStr(String key)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e)
		{
			// sLog.info("RSC bundle='" + RESOURCE_BUNDLE.getBaseBundleName() + "' missing");
			sLog.info("RSC key='" + key + "' from bundle='" + BUNDLE_NAME + "' missing");
			return '!' + key + '!';
		}
	}
}
