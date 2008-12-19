package nl.rivm.emi.dynamo.data.writers.test;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.writers.obsolete.StAXWriterEntryPoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestStaxWriterEntryPoint {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testNullModel() {
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + "staxwriternulloutput.xml";
		File configurationFile = new File(configurationFilePath);
		try {
			StAXWriterEntryPoint.produceFile(null, configurationFile);
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

	@Test
	public void testModel() {
//		RunWrappedStaxIncidenceWriting rwsiw = new RunWrappedStaxIncidenceWriting();
//		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
	//			rwsiw);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestStaxWriterEntryPoint.class);
	}
}
