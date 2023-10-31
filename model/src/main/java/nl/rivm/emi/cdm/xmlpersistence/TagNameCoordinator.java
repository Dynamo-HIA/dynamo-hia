package nl.rivm.emi.cdm.xmlpersistence;
/**
 * Data is stored to and retrieved from XML.
 * This will not be a complete serialization, but will only
 * contain properties that have to be persisted.
 *  
 */
import java.util.HashMap;

public class TagNameCoordinator {
private static TagNameCoordinator instance = null;
private HashMap<String, String> tagNameMap;

private TagNameCoordinator() {
	// TODO Auto-generated constructor stub
}

}
