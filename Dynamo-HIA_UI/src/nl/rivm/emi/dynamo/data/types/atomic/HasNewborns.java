package nl.rivm.emi.dynamo.data.types.atomic;

/**
 * Handler for 
 * <classes>
 * 	<class>
 * 		<index>1</index>
 * 		<name>jan</name>
 * 	</class>
 * 	.......
 * </classes>
 * XML fragments.
 */
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class HasNewborns extends AtomicTypeBase<Boolean> {

	public HasNewborns() {
		super("hasnewborns", Boolean.FALSE);
	}

/**
 * Get the WritableValue for databinding purposes.
 * 
 * TODO Refactor it higher up the inheritence tree.
 *
 * @return
 */
	
	@Override
	public Object convert4Model(String viewString) {
		Boolean result = Boolean.FALSE;
		if ("1".equals(viewString)) {
			result = Boolean.TRUE;
		}
		return result;
	}

	@Override
	public String convert4View(Object modelValue) {
		String result = "0";
		if (Boolean.TRUE.equals(modelValue)) {
			result = "1";
		}
		return result;
	}

	@Override
	public UpdateValueStrategy getModelUpdateValueStrategy() {
		return null;
	}

	@Override
	public UpdateValueStrategy getViewUpdateValueStrategy() {
		return null;
	}

	public Boolean handle(ConfigurationNode node) throws ConfigurationException {
		Boolean result = null;
		if (getXMLElementName().equals(node.getName())) {
			Object valueObject = node.getValue();
			if (valueObject != null) {
				if (valueObject instanceof String) {
					String valueString = (String) valueObject;
					if (valueString != "") {
						if ("true".equalsIgnoreCase(valueString)
								|| "1".equals(valueString)) {
							result = Boolean.TRUE;
						} else {
							if ("false".equalsIgnoreCase(valueString)
									|| "0".equals(valueString)) {
								result = Boolean.FALSE;
							} else {
								throw new ConfigurationException(
										"Non supported tag value: "
												+ valueString);
							}
						}
					} else {
						throw new ConfigurationException("Tag has empty value.");
					}
				} else {
					throw new ConfigurationException(
							"Tag has non String value.");
				}
			} else {
				throw new ConfigurationException("Tag has null value.");
			}
		} else {
			throw new ConfigurationException("Incorrect tag for this handler.");
		}
		return result;
	}

	@Override
	public Boolean getDefaultValue() {
		return Boolean.FALSE;
	}
}
