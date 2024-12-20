/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;


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

	synchronized static public BaseDirectory getInstance() {
		return instance;
	}
	
	synchronized static public BaseDirectory getInstance(String name) {
		if (instance == null) {
			instance = new BaseDirectory(name);
		} else {
			BaseDir = name;
		}
		return instance;
	}


}
