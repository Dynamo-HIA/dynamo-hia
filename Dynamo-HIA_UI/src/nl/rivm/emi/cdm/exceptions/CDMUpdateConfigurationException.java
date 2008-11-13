package nl.rivm.emi.cdm.exceptions;

public class CDMUpdateConfigurationException 	
	 extends Exception {

		public static final String wrongConfigurationFile = "Configuration file for update rule %1$s cannot be read for characteristic  %2$s";

		public static final String noParameterFileMessage = "Updaterule %1$s: no filename for parameter data is found for characteristic %2$s.";

		public CDMUpdateConfigurationException(String message) {
			super(message);
		}

		public CDMUpdateConfigurationException 	(String updateRuleName,
				String parameterClassName) {
			super(String.format(
					"Updaterule %1$s cannot be used on parameter of type %2$s.",
					updateRuleName, parameterClassName));
		}
	}


