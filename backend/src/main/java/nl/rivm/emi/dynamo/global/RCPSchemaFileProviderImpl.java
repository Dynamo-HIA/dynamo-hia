package nl.rivm.emi.dynamo.global;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.commons.configuration.SchemaFileProviderInterface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;

public class RCPSchemaFileProviderImpl implements SchemaFileProviderInterface {
	Log log = LogFactory.getLog(this.getClass().getName());
	private static final String SCHEMAS_LOCATION = "schemas";

	private static final String XSD_EXTENSION = ".xsd";

	public RCPSchemaFileProviderImpl() {
		super();
		log.debug("Instantiating : " + this.getClass().getSimpleName());
	}

	@SuppressWarnings("finally")
	@Override
	public File getSchemaFile(String rootElementName) {
		File schemaFile = null;
		String filePathFromPluginRoot = SCHEMAS_LOCATION + File.separator
				+ rootElementName + XSD_EXTENSION;
		try {
			schemaFile = fileFromPluginRoot(filePathFromPluginRoot);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			log.debug("RootElementName: " + rootElementName
					+ " Schemafile-path: " + schemaFile.getAbsolutePath());
			return schemaFile;
		}
	}

	/**
	 * 
	 * Returns the file location absolute to the installation directory of the
	 * Standalone plugin
	 * 
	 * @param filePath
	 * @return File
	 * @throws URISyntaxException
	 */
	private File fileFromPluginRoot(String filePath) throws URISyntaxException {
		String pluginRootString = Platform.getInstallLocation().getURL()
				.getPath()
				+ filePath;
		File pluginRoot = new File(pluginRootString);
		return pluginRoot;
	}
}
