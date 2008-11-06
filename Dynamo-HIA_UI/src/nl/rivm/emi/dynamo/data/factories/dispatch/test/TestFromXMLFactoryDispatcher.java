package nl.rivm.emi.dynamo.data.factories.dispatch.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.dynamo.data.factories.dispatch.FromXMLFactoryDispatcher;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFromXMLFactoryDispatcher {
	Log log = LogFactory.getLog(getClass().getName());
	Display display = null;
	Shell shell = null;
	Composite container = null;
	boolean killScreen = false;
String configurationFilePath = "D:\\eclipse_workspaces\\rcp\\Dynamo-HIA_UI\\data\\dynamobase\\ReferenceData\\Lung Cancer\\Incidences\\incidence1.xml"; 
	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testManufacturing() {
		try {
			Object theObject = FromXMLFactoryDispatcher.makeDataObject(configurationFilePath);
			assertNotNull(theObject);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				TestFromXMLFactoryDispatcher.class);
	}
}
