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
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.data.interfaces.INameType;
import nl.rivm.emi.dynamo.data.objects.xml.BaseXMLHandler;
import nl.rivm.emi.dynamo.data.objects.xml.XMLValueConverter;
import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class NameType extends AtomicTypeBase<String> implements
		INameType, IXMLHandlingLayer<String>, XMLValueConverter<String> {

	Log log = LogFactory.getLog(this.getClass().getName());
/**
 * Value handling.
 */
	String name = null;

	WritableValue observableName = null;

	boolean isObservable = false;

	public NameType(boolean isObservable) {
		super(XMLElementName, "aString");
		this.isObservable = isObservable;
		myHandler = new BaseXMLHandler<String>(XMLElementName, this);
	}

	public String getName() {
		String result = null;
		if (!isObservable) {
			result = name;
		} else {
			result = ((String) observableName.doGetValue());
		}
		return result;
	}

	public void setName(String name) {
		if (!isObservable) {
			this.name = name;
		} else {
			if (observableName == null) {
				observableName = new WritableValue(name,
						String.class);
			} else {
				observableName.doSetValue(name);
			}
		}
	}

	/**
	 * Databinding department.
	 * @return
	 */
	public WritableValue getObservableValue() {
		return observableName;
	}

	Object convert4Model(String viewString) {
		return viewString;
	}

	public String convert4View(Object modelValue) {
		return (String) modelValue;
	}

	public UpdateValueStrategy getModelUpdateValueStrategy() {
		return null;
	}

	public UpdateValueStrategy getViewUpdateValueStrategy() {
		return null;
	}

	/**
	 * XML department
	 */
	static final protected String XMLElementName = "name";
	protected final BaseXMLHandler<String> myHandler;

	public String handle(ConfigurationNode node) throws ConfigurationException {
		return myHandler.handle(node);
	}
	public void streamEvents(String value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		myHandler.streamEvents(value, writer, eventFactory);
	}

	public String streamValue() {
		String result = "";
		if (!isObservable) {
			result = name;
		} else {
			result = ((String) observableName.doGetValue());
		}
		return result.toString();
	}

	public void setDefault() {
		setName("standaard");
	}

	public String convert(String valueString) {
		return valueString;
	}

	public String streamValue(String value) {
		return value;
	}

	/**
	 * For the moment a very cursory check whether anything is there.
	 */
	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (name != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
			if (observableName.doGetValue() != null) {
				result = true;
			} else {
				log.error("Observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		}
		return result;
	}
}
