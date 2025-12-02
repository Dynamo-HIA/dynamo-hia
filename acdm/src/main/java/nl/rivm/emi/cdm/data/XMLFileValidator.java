package nl.rivm.emi.cdm.data;

/**
 * Verifies whether the xml-file and its schema-file can be used and
 * whether the xml-file is valid against its schema file.
 */

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class XMLFileValidator {
	static final Log logger = LogFactory.getLog("nl.rivm.sec.xmlhandling.XMLFileValidator");

	static final String sl = XMLConstants.W3C_XML_SCHEMA_NS_URI;

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("unused")
	static public File checkFileExistsAndCanRead(String fileName) {
		File theFile = new File(fileName);
		if (theFile == null) {
			logger.error("checkFileExistsAndCanRead() could not intantiate File object for: " + fileName + ".");
		} else {
			if (!theFile.exists()) {
				logger.error("checkFileExistsAndCanRead(): " + fileName + " does not exist.");
				theFile = null;
			} else {
				if (!theFile.canRead()) {
					logger
							.error("checkFileExistsAndCanRead(): " + fileName
									+ " cannot be read.");
					theFile = null;
				}
			}
		}
		return theFile;
	}

	static public boolean validate(String xmlFileName, String xmlSchemaFileName) {
		boolean valid = false;
		File xmlFile = checkFileExistsAndCanRead(xmlFileName);
		File xmlSchemaFile = checkFileExistsAndCanRead(xmlSchemaFileName);
		if ((xmlFile != null) && (xmlSchemaFile != null)) {
			SchemaFactory factory = SchemaFactory.newInstance(sl);
			StreamSource ss = new StreamSource(xmlSchemaFile);
			try {
				Schema schema = null;
				schema = factory.newSchema(ss);
				Validator v = schema.newValidator();
				v.validate(new StreamSource(xmlFile));
				valid = true;
			} catch (IOException e) {
				logger.error("validate(): " + e.getClass().getName() + " "
						+ e.getMessage());

			} catch (SAXException e) {
				logger.error("validate(): " + e.getClass().getName() 
						+ " >" + xmlFileName + "< "
						+ e.getMessage());
			}
		}
		return valid;
	}
}
