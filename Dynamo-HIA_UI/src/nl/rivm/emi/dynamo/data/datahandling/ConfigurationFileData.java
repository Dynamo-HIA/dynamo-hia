package nl.rivm.emi.dynamo.datahandling;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

 public class ConfigurationFileData extends ArrayList<XMLBaseElement>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	Log log = LogFactory.getLog(getClass().getName());

	
	public String xmlGlobalUpdateRuleTagName = "updaterules";
	public String xmlGlobalTagName = null;
	public String getXmlGlobalTagName() {
		return xmlGlobalTagName;
	}

	public void setXmlGlobalTagName(String xmlGlobalTagName) {
		this.xmlGlobalTagName = xmlGlobalTagName;
	}

	public static final  String configFileNotWriteableMsg = "File %1$s could not be written.";

	private Iterator<XMLBaseElement> iterator = null;
	public ConfigurationFileData(String tagName) {
		super();
		setXmlGlobalTagName(tagName);
		
	}

	
	
	

	
	public  void writeToXMLFile(ConfigurationFileData configData,  File xmlFileName)
	throws ParserConfigurationException, TransformerException {
         DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = (DocumentBuilder) dbfac
		.newDocumentBuilder();
Document document = docBuilder.newDocument();
String elementName = configData.getXmlGlobalTagName();
Element element = document.createElement(elementName);
document.appendChild(element);
Iterator it=configData.iterator();
XMLBaseElement baseElement;
while (it.hasNext()) {baseElement=(XMLBaseElement) it.next();
	XMLBaseElement.generateDOM(baseElement, element);
}
boolean isDirectory = xmlFileName.isDirectory();
boolean canWrite = xmlFileName.canWrite();
try {
	boolean isNew = xmlFileName.createNewFile();
	if (!isDirectory && (canWrite || isNew)) {
		Source source = new DOMSource(document);
		StreamResult result = new StreamResult(xmlFileName);
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(source, result);
	}}catch 
		

(IOException e) {
	log.warn("File exception: " + e.getClass().getName() + " message: "
			+ e.getMessage());
	e.printStackTrace();
}
}

	

	public  void writeToXMLFile(ConfigurationFileData configData, XMLUpdateRuleConfiguration ruleData, File xmlFileName)
	throws ParserConfigurationException, TransformerException {
         DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = (DocumentBuilder) dbfac
		.newDocumentBuilder();
Document document = docBuilder.newDocument();
String elementName = configData.getXmlGlobalTagName();
Element element = document.createElement(elementName);
document.appendChild(element);
Iterator it=configData.iterator();
XMLBaseElement baseElement;
while (it.hasNext()) {baseElement=(XMLBaseElement) it.next();
	XMLBaseElement.generateDOM(baseElement, element);
}

Element parentUpdateRuleElement = element.getOwnerDocument().createElement(
		xmlGlobalUpdateRuleTagName);
element.appendChild(parentUpdateRuleElement);
ConfigurationFileData ruleConfig;
Iterator itRule=ruleData.iterator();
while (itRule.hasNext())
{ruleConfig=(ConfigurationFileData) itRule.next();
/* now read the update rule tag name from the update rule configuration file */
elementName=ruleConfig.getXmlGlobalTagName();
Element newUpdateRuleElement=parentUpdateRuleElement.getOwnerDocument().createElement(elementName);
parentUpdateRuleElement.appendChild(newUpdateRuleElement);
Iterator itInRule=ruleConfig.iterator();
while (itInRule.hasNext())
{baseElement=(XMLBaseElement) it.next();
XMLBaseElement.generateDOM(baseElement, newUpdateRuleElement);}
}

boolean isDirectory = xmlFileName.isDirectory();
boolean canWrite = xmlFileName.canWrite();
try {
	boolean isNew = xmlFileName.createNewFile();
	if (!isDirectory && (canWrite || isNew)) {
		Source source = new DOMSource(document);
		StreamResult result = new StreamResult(xmlFileName);
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(source, result);
	}}catch 
		

(IOException e) {
	log.warn("File exception: " + e.getClass().getName() + " message: "
			+ e.getMessage());
	e.printStackTrace();
}
}

	
}


