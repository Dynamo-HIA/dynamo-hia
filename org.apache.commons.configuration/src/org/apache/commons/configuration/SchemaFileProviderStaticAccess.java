package org.apache.commons.configuration;

import java.io.File;

public class SchemaFileProviderStaticAccess {
	static private SchemaFileProviderInterface instance = null;

	/**
	 * Cannot be instantiated.
	 */
	private SchemaFileProviderStaticAccess() {
	}

	static public SchemaFileProviderInterface setProvider(
			SchemaFileProviderInterface provider) {
		SchemaFileProviderInterface priorProvider = null;
		if (SchemaFileProviderStaticAccess.instance != null) {
			priorProvider = SchemaFileProviderStaticAccess.instance;
		}
		SchemaFileProviderStaticAccess.instance = provider;
		return priorProvider;
	}

	static public SchemaFileProviderInterface getProvider() {
		return SchemaFileProviderStaticAccess.instance;
	}

	static public File getSchemaFile(String rootElementName) {
		return SchemaFileProviderStaticAccess.instance
				.getSchemaFile(rootElementName);
	}
}
