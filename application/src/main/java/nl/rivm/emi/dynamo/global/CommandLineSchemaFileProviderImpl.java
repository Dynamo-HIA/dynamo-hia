package nl.rivm.emi.dynamo.global;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.SchemaFileProviderInterface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommandLineSchemaFileProviderImpl implements
		SchemaFileProviderInterface {
	Log log = LogFactory.getLog(this.getClass().getName());

	private static final String SCHEMAS_LOCATION = ((String) System
			.getProperties().get("user.dir"))
			+ File.separator + "schemas" + File.separator;

	private static final String XSD_EXTENSION = ".xsd";

	public CommandLineSchemaFileProviderImpl() {
		super();
		log.debug("Instantiating : " + this.getClass().getSimpleName());
	}

	@Override
	public File getSchemaFile(String rootElementName) {
		File schemaFile = null;
		schemaFile = new File(SCHEMAS_LOCATION + rootElementName
				+ XSD_EXTENSION);
		log.debug("RootElementName: " + rootElementName + " Schemafile-path: "
				+ schemaFile.getAbsolutePath());
		return schemaFile;
	}

	static public void main(String[] args) {
		Map<String, String> environment = System.getenv();
		Set<String> environmentKeys = environment.keySet();
		System.out.println(">>>Environment:");
		for (String key : environmentKeys) {
			System.out.println(key + " : " + environment.get(key));
		}
		System.out.flush();
		Properties systemProperties = System.getProperties();
		Set<Object> propertyKeys = systemProperties.keySet();
		System.out.println(">>>Properties:");
		for (Object key : propertyKeys) {
			System.out.println(key + " : " + systemProperties.get(key));
		}
	}

}
