package nl.rivm.emi.dynamo.data.objects.xml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.dynamo.data.objects.xml.StringXMLHandler;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestXMLHandlers {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testString() {
		try {
			String configurationFilePath = "data" + File.separator
					+ "development" + File.separator + "jaaponly.xml";
			File configurationFile = new File(configurationFilePath);
			if (!configurationFile.exists()) {
				throw new FileNotFoundException("File: "
						+ configurationFilePath + " not found.");
			} else {
				if (!configurationFile.isFile()) {
					throw new ConfigurationException("Configurationfile "
							+ configurationFile.getAbsolutePath()
							+ " is not a file.");
				} else {
					if (!configurationFile.canRead()) {
						throw new ConfigurationException("Configurationfile "
								+ configurationFile.getAbsolutePath()
								+ " cannot be read.");
					} else {
						XMLConfiguration configurationFromFile;
						try {
							configurationFromFile = new XMLConfiguration(
									configurationFile);
							ConfigurationNode rootNode = configurationFromFile
									.getRootNode();
							List children = rootNode.getChildren();
							ConfigurationNode childNode = (ConfigurationNode) children
									.get(0);
							StringXMLHandler handler = new StringXMLHandler(
									"jaap");
							String result = handler.handle(childNode );
							assertEquals("only", result);
						} catch (ConfigurationException e) {
							log.error("Caught Exception of type: "
									+ e.getClass().getName()
									+ " with message: " + e.getMessage());
							e.printStackTrace();
							throw e;
						} catch (Exception exception) {
							log.error("Caught Exception of type: "
									+ exception.getClass().getName()
									+ " with message: "
									+ exception.getMessage());
							exception.printStackTrace();
							throw new DynamoInconsistentDataException(
									"Caught Exception of type: "
											+ exception.getClass().getName()
											+ " with message: "
											+ exception.getMessage()
											+ " inside "
											+ this.getClass().getName());
						}
					}
				}
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e);
		} catch (DynamoInconsistentDataException e) {
			e.printStackTrace();
			assertNull(e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			assertNull(e);
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.dynamo.data.objects.xml.test.TestXMLHandlers.class);
	}
}
