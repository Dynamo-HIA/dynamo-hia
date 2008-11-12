package nl.rivm.emi.dynamo.data.writers.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticWriter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestStaxAgnosticWriterEntryPoint {
	Log log = LogFactory.getLog(getClass().getName());
	HashMap<Integer, Object> theObject;

	@Before
	public void setup() {
		String configurationFilePath = "data" + File.separator + "development"
		+ File.separator + "incidence1.xml";
File configurationFile = new File(configurationFilePath);
log.debug(configurationFile.getAbsolutePath());
	try {
		theObject = (HashMap<Integer,Object>)new AgnosticFactory().manufacture(
				configurationFile, false);
		assertNotNull(theObject);
	} catch (ConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		assertNull(e); // Force error.
	}

	}

	@After
	public void teardown() {
	}

	@Test
	public void testNullModel() {
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + "staxagnosticwriterincidencesoutput.xml";
		File configurationFile = new File(configurationFilePath);
		try {
			FileControlSingleton fcs = FileControlSingleton.getInstance();
			StAXAgnosticWriter.produceFile(fcs.get("incidences"), theObject,
					configurationFile);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
//	public void testModel() {
//		RunWrappedStaxIncidenceWriting rwsiw = new RunWrappedStaxIncidenceWriting();
//		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
//				rwsiw);
//	}
//
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestStaxAgnosticWriterEntryPoint.class);
	}
}
