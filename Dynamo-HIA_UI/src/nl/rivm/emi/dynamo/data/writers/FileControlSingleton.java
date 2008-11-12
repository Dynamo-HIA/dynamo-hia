package nl.rivm.emi.dynamo.data.writers;
/**
 * Map to find the type by its corresponding tagname.
 */
import java.util.HashMap;

public class FileControlSingleton extends HashMap<String,FileControlEnum>{
	private static FileControlSingleton instance = null;
	
	private FileControlSingleton(){
		super();
		Object test = FileControlEnum.values();
		for(FileControlEnum type: FileControlEnum.values()){
		put(type.getRootElementName(),type);
		}
	}
	
	synchronized static public FileControlSingleton getInstance(){
		if(instance == null){
			instance = new FileControlSingleton();
		}
		return instance;
	}
}
