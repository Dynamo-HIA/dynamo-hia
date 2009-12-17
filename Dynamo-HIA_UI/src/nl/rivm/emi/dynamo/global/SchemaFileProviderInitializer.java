package nl.rivm.emi.dynamo.global;

import org.apache.commons.configuration.SchemaFileProviderInterface;
import org.apache.commons.configuration.SchemaFileProviderStaticAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Plugin;

public class SchemaFileProviderInitializer {
	static public Log log = LogFactory.getLog("SchemaFileProviderInitializer");

	private SchemaFileProviderInitializer() {
	}

	static public void initialize(Plugin plugin) {
		SchemaFileProviderInitializer.log.debug("Entering initialize.");
		SchemaFileProviderInterface sfpi = null;
		if (plugin == null) {
			sfpi = new CommandLineSchemaFileProviderImpl();
			SchemaFileProviderInitializer.log.debug("Assigned new instance: " + sfpi);
		} else {
			sfpi = new RCPSchemaFileProviderImpl();
			SchemaFileProviderInitializer.log.debug("Assigned new instance: " + sfpi);
		}
		SchemaFileProviderStaticAccess.setProvider(sfpi);
		SchemaFileProviderInitializer.log
				.debug("Retrieving configured instance: "
						+ SchemaFileProviderStaticAccess.getProvider());
		// SchemaFileProviderInitializer.log.debug("Flush.");
		// SchemaFileProviderInitializer.log.debug("Flush.");
		// SchemaFileProviderInitializer.log
		// .debug("Retrieving schemaFile for: \"simulation\": "
		// + SchemaFileProviderStaticAccess
		// .getSchemaFile("simulation"));
	}
}
