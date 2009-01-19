package nl.rivm.emi.dynamo.data.types;
/**
 * Map to find the type by its corresponding tagname.
 */
import java.util.HashMap;

import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;

public class XMLTagEntitySingleton extends HashMap<String,XMLTagEntity>{
	private static final long serialVersionUID = 3111726151725007942L;
	private static XMLTagEntitySingleton instance = null;
	
	private XMLTagEntitySingleton(){
		super();
		for(XMLTagEntityEnum type: XMLTagEntityEnum.values()){
		put(type.getElementName(),type.getTheType());
		}
	}
	
	synchronized static public XMLTagEntitySingleton getInstance(){
		if(instance == null){
			instance = new XMLTagEntitySingleton();
		}
		return instance;
	}
}
