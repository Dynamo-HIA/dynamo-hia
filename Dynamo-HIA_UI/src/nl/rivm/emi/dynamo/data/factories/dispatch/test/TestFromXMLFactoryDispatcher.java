package nl.rivm.emi.dynamo.data.factories.dispatch.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FromXMLFactoryDispatcher;
import nl.rivm.emi.dynamo.data.factories.notinuse.IncidenceIntegerFactory;
import nl.rivm.emi.dynamo.data.objects.ObservableIncidencesObject;
import nl.rivm.emi.dynamo.data.objects.ObservableObjectMarker;
import nl.rivm.emi.dynamo.data.objects.StandardObjectMarker;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
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
	String incidenceConfigurationFilePath = "D:\\eclipse_workspaces\\KISFromSVN\\Dynamo-HIA_UI\\data\\dynamobase\\ReferenceData\\Lung Cancer\\Incidences\\incidence1.xml";
	String prevalenceConfigurationFilePath = "D:\\eclipse_workspaces\\KISFromSVN\\Dynamo-HIA_UI\\data\\development\\prevalencecategorical.xml";

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testManufacturingIncidenceObject() {
		try {
			Object theObject = FromXMLFactoryDispatcher
					.makeDataObject(incidenceConfigurationFilePath);
			assertNotNull(theObject);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void testManufacturingObservableIncidenceObject() {
		try {
			Object theObject = FromXMLFactoryDispatcher
					.makeObservableDataObject(incidenceConfigurationFilePath);
			assertNotNull(theObject);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void testManufacturing() {
		IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> theFactory;
		try {
			theFactory = FromXMLFactoryDispatcher.getRelevantFactory(incidenceConfigurationFilePath);
		RunWrappedContainerManufacturing conManRunner = new RunWrappedContainerManufacturing(incidenceConfigurationFilePath, theFactory);
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				conManRunner);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class RunWrappedContainerManufacturing implements Runnable {
		Log log = LogFactory.getLog(this.getClass().getName());
		IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> theFactory;
		String configurationFilePath;

		public RunWrappedContainerManufacturing(String configurationFilePath, IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> aFactory) {
			this.configurationFilePath = configurationFilePath;
			this.theFactory = aFactory;
		}

		public void run() {
			DataBindingContext dbc = new DataBindingContext();
			File configurationFile = new File(configurationFilePath);
			log.debug(configurationFile.getAbsolutePath());
			try {
				ObservableObjectMarker testContainer = theFactory
						.manufactureObservable(configurationFile);
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * WARNING Three different Object to test, Starting with categorical. TODO
	 * continuous TODO duration.
	 */
	// @Test
	// public void testManufacturingPrevalenceObjects() {
	// try {
	// Object theObject =
	// FromXMLFactoryDispatcher.makeDataObject(prevalenceConfigurationFilePath);
	// assertNotNull(theObject);
	// } catch (ConfigurationException e) {
	// e.printStackTrace();
	// assertNull(e); // Force error.
	// }
	// }

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestFromXMLFactoryDispatcher.class);
	}
}