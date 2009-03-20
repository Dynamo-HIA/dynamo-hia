package nl.rivm.emi.dynamo.data.xml.structure;
/**
 * Map to find the type by its corresponding tagname.
 */
import java.util.HashMap;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;

public class RootElementNamesSingleton extends HashMap<String,RootElementNamesEnum>{
	private static RootElementNamesSingleton instance = null;
	
	private RootElementNamesSingleton(){
		super();
		for(RootElementNamesEnum type: RootElementNamesEnum.values()){
		put(type.getNodeLabel(),type);
		}
	}
	
	synchronized static public RootElementNamesSingleton getInstance(){
		if(instance == null){
			instance = new RootElementNamesSingleton();
		}
		return instance;
	}
}
