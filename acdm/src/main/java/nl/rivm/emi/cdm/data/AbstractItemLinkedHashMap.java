package nl.rivm.emi.cdm.data;

	/**
	 * Class for reading the configuration of groups of lists and 
	 * lists themselves into a HashMap.
	 */

	import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.helpers.DefaultHandler;

	abstract public class AbstractItemLinkedHashMap extends
			LinkedHashMap<String, String> {
		/**
		     * 
		     */
		    private static final long serialVersionUID = 23L;

		Log logger = LogFactory.getLog(getClass().getName());

		FileInputStream iStream;

		String itemElementName = null;

		String firstPropertyElementName = null;

		String secondPropertyElementName = null;

	/**
	 * 
	 * @param itemElementName
	 * 			The name of the element that contains the "datarecord".
	 * @param firstPropertyElementName
	 *			The name of the element that contains the key.
	 * @param secondPropertyElementName
	 * 			The name of the element that contains the value.
	 */
		public AbstractItemLinkedHashMap(String itemElementName,
				String firstPropertyElementName, String secondPropertyElementName) {
			super();
			logger.debug("Constructing...");
				this.itemElementName = itemElementName;
			this.firstPropertyElementName = firstPropertyElementName;
			this.secondPropertyElementName = secondPropertyElementName;
		}
		
		public void fill(String baseDirectory, String xmlFileName, String xmlSchemaFileName){		
			@SuppressWarnings("unused")
			boolean success = false;
			StringBuffer recordBuffer = new StringBuffer();
			if (XMLFileValidator.validate(xmlFileName, xmlSchemaFileName) == true) {
				logger.info("fill() file: " + xmlFileName + " validates OK.");
				recordBuffer.append(xmlFileName + " is valide.");
				File xmlNormFile = XMLFileValidator.checkFileExistsAndCanRead(xmlFileName);
				if (xmlNormFile != null) {
					logger.info("fill() DefaultHandler itemElementName: " + itemElementName
							+ " firstPropertyElementName: " + firstPropertyElementName
							+ " secondPropertyElementName: " + secondPropertyElementName);
					recordBuffer.append("Lees bestand met itemElementName: " + itemElementName
							+ " firstPropertyElementName: " + firstPropertyElementName
							+ " secondPropertyElementName: " + secondPropertyElementName);
					DefaultHandler handler = new ConfigurationDefaultHandler(
							itemElementName, firstPropertyElementName,
							secondPropertyElementName, baseDirectory, this);
					// Use the default (non-validating) parser
					SAXParserFactory factory = SAXParserFactory.newInstance();
					if (handler != null && factory != null) {
						logger.debug("fill() Initialization OK.");
						try {
							SAXParser saxParser = factory.newSAXParser();
							saxParser.parse(xmlNormFile, handler);
							success = true;
						} catch (Throwable t) {
							logger.error("fill() on file: " + xmlFileName
									+ " exception: " 
									+ t.getClass().getName() + " " + t.getMessage());
						}
					} else {
						logger.error("fill() on file: " + xmlFileName
								+ " initialization failed.");
					}
				} else {
					logger
							.error("fill() file: " + xmlFileName
									+ " does not open.");

				}
			} else {
				logger
						.error("fill() file: " + xmlFileName
								+ " does not validate.");
			}
		}
		
	abstract public void add(String key, String value, String physicalBaseDirectory);
	}

