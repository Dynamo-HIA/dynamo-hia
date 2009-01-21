package nl.rivm.emi.dynamo.data.objects.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.TransitionMatrixFactory;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCategoricalObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticWriter;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Manufacture_RiskFactorCategoricalObject {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void nonObservable() {
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + "riskfactor_categorical_config1.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "riskfactor_categorical_config1_after.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			RiskFactorCategoricalObject theModel = new RiskFactorCategoricalObject(
					false);
			Object result = theModel.manufacture(configurationFilePath);
			assertNotNull(result);
			try {
				((RiskFactorCategoricalObject) result).writeToFile(outputFile);
			} catch (XMLStreamException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNotNull(e); // Force error.
		} catch (DynamoInconsistentDataException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void observable() {
		Runnable4Manufacture_RiskFactorCategorical a = new Runnable4Manufacture_RiskFactorCategorical();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()), a);
	}

	public class Runnable4Manufacture_RiskFactorCategorical implements Runnable {
		Log log = LogFactory.getLog(getClass().getName());

		public void run() {
			DataBindingContext dbc = new DataBindingContext();
			String configurationFilePath = "data" + File.separator
					+ "development" + File.separator
					+ "riskfactor_categorical_config1.xml";
			File configurationFile = new File(configurationFilePath);
			log.debug(configurationFile.getAbsolutePath());
			String outputFilePath = "data" + File.separator + "development"
					+ File.separator
					+ "riskfactor_categorical_config1__observable_after.xml";
			File outputFile = new File(outputFilePath);
			try {
				RiskFactorCategoricalObject theModel = new RiskFactorCategoricalObject(
						true);
				Object result = theModel.manufacture(configurationFilePath);
				assertNotNull(result);
				try {
					((RiskFactorCategoricalObject) result)
							.writeToFile(outputFile);
				} catch (XMLStreamException e) {
					e.printStackTrace();
					assertNull(e); // Force error.
				} catch (UnexpectedFileStructureException e) {
					e.printStackTrace();
					assertNull(e); // Force error.
				} catch (IOException e) {
					e.printStackTrace();
					assertNull(e); // Force error.
				}
			} catch (ConfigurationException e) {
				e.printStackTrace();
				assertNotNull(e); // Force error.
			} catch (DynamoInconsistentDataException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				Manufacture_RiskFactorCategoricalObject.class);
	}
}
