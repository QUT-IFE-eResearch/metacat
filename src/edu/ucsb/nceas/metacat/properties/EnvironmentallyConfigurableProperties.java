package edu.ucsb.nceas.metacat.properties;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import edu.ucsb.nceas.metacat.shared.ServiceException;
import edu.ucsb.nceas.utilities.FileUtil;
import edu.ucsb.nceas.utilities.GeneralPropertyException;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;
import edu.ucsb.nceas.utilities.SortedProperties;

/**
 * 
 * Metacat configuration class that allows configuration by environment.
 * Loads a second environment specific (via tomcat environment property) properties  file that will override the existing property found in metacat.properties class 
 *
 */
public class EnvironmentallyConfigurableProperties extends
		ConfigurableProperties {

	private static SortedProperties environmentMainProperties = null;

	protected EnvironmentallyConfigurableProperties() throws ServiceException {
		super();
		Context initCtx;
		Context envCtx;
		try {
			initCtx = new InitialContext();
			envCtx = (javax.naming.Context) initCtx.lookup("java:comp/env");
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
		try {
			String instanceName = (String) envCtx.lookup("instanceName");
			environmentMainProperties = new SortedProperties(
					PropertyService.CONFIG_FILE_DIR + FileUtil.getFS()
					+ instanceName + "-" + MAIN_CONFIG_FILE_NAME);
			environmentMainProperties.load();

		} catch (NamingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getProperty(String propertyName)
			throws PropertyNotFoundException {
		if (environmentMainProperties != null) {
			try {
				return environmentMainProperties.getProperty(propertyName);
			} catch (PropertyNotFoundException e) {
				//See if the property exists in the superclass' property file
			}
		}
		return super.getProperty(propertyName);
	}

	/**
	 * Utility method to set a property value both in memory and to the
	 * properties file
	 * 
	 * @param propertyName
	 *            the name of the property requested
	 * @param newValue
	 *            the new value for the property
	 */
	@Override
	public void setProperty(String propertyName, String newValue)
			throws GeneralPropertyException {
		if (environmentMainProperties == null) {
			super.setProperty(propertyName, newValue);
			return;
		}
		environmentMainProperties.setProperty(propertyName, newValue);
				environmentMainProperties.store();
			

	}

	/**
	 * Utility method to set a property value in memory. This will NOT cause the
	 * property to be written to disk. Use this method to set multiple
	 * properties in a row without causing excessive I/O. You must call
	 * persistProperties() once you're done setting properties to have them
	 * written to disk.
	 * 
	 * @param propertyName
	 *            the name of the property requested
	 * @param newValue
	 *            the new value for the property
	 */
	@Override
	public void setPropertyNoPersist(String propertyName, String newValue)
			throws GeneralPropertyException {
		if (environmentMainProperties == null) {
			super.setPropertyNoPersist(propertyName, newValue);
			return;
		}
		environmentMainProperties.setPropertyNoPersist(propertyName, newValue);
	}

	/**
	 * Save the properties to a properties file. Note, the order and comments
	 * will be preserved.
	 */
	@Override
	public void persistProperties() throws GeneralPropertyException {
		if (environmentMainProperties == null) {
			super.persistProperties();
			return;
		}
		environmentMainProperties.store();
	}
}
