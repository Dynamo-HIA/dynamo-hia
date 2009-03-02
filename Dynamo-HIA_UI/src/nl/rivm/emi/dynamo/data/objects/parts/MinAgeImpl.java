package nl.rivm.emi.dynamo.data.objects.parts;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.data.interfaces.IMinAge;
import nl.rivm.emi.dynamo.data.interfaces.IStartingYear;
import nl.rivm.emi.dynamo.data.types.atomic.MinAgeType;
import nl.rivm.emi.dynamo.data.types.atomic.StartingYearType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class MinAgeImpl extends MinAgeType implements IMinAge {
	Log log = LogFactory.getLog(this.getClass().getName());
	Integer minAge = null;

	WritableValue observableMinAge = null;

	boolean isObservable = false;

	/**
	 * Block default construction.
	 */
	@SuppressWarnings("unused")
	private MinAgeImpl() throws ConfigurationException{

	}

	public MinAgeImpl(boolean isObservable) throws ConfigurationException{
		this.isObservable = isObservable;
	}

	public void setMinAge(Integer minAge) {
		if (!isObservable) {
			this.minAge = minAge;
		} else {
			if (observableMinAge == null) {
				observableMinAge = new WritableValue(minAge,
						Integer.class);
			} else {
				observableMinAge.doSetValue(minAge);
			}
		}
	}

	@Override
	protected String streamValue() {
		Integer result = null;
		if (!isObservable) {
			result = minAge;
		} else {
			result = ((Integer) observableMinAge.doGetValue());
		}
		return result.toString();
	}

	@Override
	public void setDefault() {
		setMinAge(0);
	}

	public Integer handle(ConfigurationNode node) throws ConfigurationException {
		Integer result = (Integer)super.handle(node);
		setMinAge(result);
		return result;
	}

	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (minAge != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
			if (observableMinAge.doGetValue() != null) {
				result = true;
			} else {
				log.error("Observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		}
		return result;
	}

	public Integer getMinAge() {
		Integer result = null;
		if (!isObservable) {
			result = minAge;
		} else {
			result = ((Integer) observableMinAge.doGetValue());
		}
		return result;
	}

	@Override
	public WritableValue getObservableValue() {
		return observableMinAge;
	}

	public void streamEvents(Integer value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}
}
