package nl.rivm.emi.dynamo.data.types;
/**
 * Map to find the type by its corresponding tagname.
 */
import java.util.HashMap;

import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;

public class AtomicTypesSingleton extends HashMap<String,AtomicTypeBase>{
	private static final long serialVersionUID = 3111726151725007942L;
	private static AtomicTypesSingleton instance = null;
	
	private AtomicTypesSingleton(){
		super();
		for(AtomicTypeEnum type: AtomicTypeEnum.values()){
		put(type.getElementName(),type.getTheType());
		}
	}
	
	synchronized static public AtomicTypesSingleton getInstance(){
		if(instance == null){
			instance = new AtomicTypesSingleton();
		}
		return instance;
	}
}