/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

import nl.rivm.emi.dynamo.estimation.DynamoLib;

/**
 * @author Hendriek
 * BaseDirectory is a singleton that contains the name of the Base Directory (the root directory of the
 * DYNAMO-HIA data and simulation tree;
 *
 */
public class BaseDirectory {
	
private static String BaseDir="c:\\dynamo\\"	;
public static String getBaseDir() {
	return BaseDir;
}


private static BaseDirectory instance=null;
	
	private BaseDirectory(String name) {
		BaseDir=name;
	
}

	synchronized static public BaseDirectory getInstance(String name) {
		if (instance == null) {
			instance = new BaseDirectory(name);
		}
		return instance;
	}


}
