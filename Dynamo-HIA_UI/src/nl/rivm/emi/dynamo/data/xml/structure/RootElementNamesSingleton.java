package nl.rivm.emi.dynamo.data.xml.structure;
import java.util.HashMap;

/**
 * Map to find the type by its corresponding tagname.
 */
public class RootElementNamesSingleton extends HashMap<String,RootElementNamesEnum>{
	private static final long serialVersionUID = 6702123740265977929L;
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
