package nl.rivm.emi.dynamo.data.types;

/**
 * Map to find the type by its corresponding tagname.
 */
import java.util.HashMap;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XMLTagEntitySingleton extends HashMap<String, XMLTagEntity> {
	private static final long serialVersionUID = 3111726151725007942L;
	private Log log = LogFactory.getLog(this.getClass().getName());
	private static XMLTagEntitySingleton instance = null;

	private XMLTagEntitySingleton() {
		super();
		for (XMLTagEntityEnum type : XMLTagEntityEnum.values()) {
			XMLTagEntity presentXMLTagEntity = put(type.getElementName(), type
					.getTheType());
			if (presentXMLTagEntity != null) {
				log.fatal("Duplicate entry for tag \"" + type.getElementName()
						+ "\" present entity \""
						+ presentXMLTagEntity.getClass().getName()
						+ "\" new entity \"" + type.getTheType().getClass().getName()
						+ "\".");
			}
		}
	}

	synchronized static public XMLTagEntitySingleton getInstance() {
		if (instance == null) {
			instance = new XMLTagEntitySingleton();
		}
		return instance;
	}
	public XMLTagEntity get(String elementName){
		XMLTagEntity entity = super.get(elementName);
		if(entity == null){
			log.fatal("No XMLTagEntity found for \""
					+ elementName
					+ "\".");
		
		}
		return entity;
	}
}
