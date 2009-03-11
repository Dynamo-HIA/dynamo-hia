package nl.rivm.emi.dynamo.data.objects.parts;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.data.interfaces.IStartingYear;
import nl.rivm.emi.dynamo.data.types.atomic.StartingYearType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class StartingYearImpl extends StartingYearType implements IStartingYear {
	Log log = LogFactory.getLog(this.getClass().getName());
	Integer startingYear = null;

	WritableValue observableStartingYear = null;

	boolean isObservable = false;

	/**
	 * Block default construction.
	 */
	@SuppressWarnings("unused")
	private StartingYearImpl()  throws ConfigurationException{

	}

	public StartingYearImpl(boolean isObservable)  throws ConfigurationException{
		this.isObservable = isObservable;
	}

	public void setStartingYear(Integer startingYear) {
		if (!isObservable) {
			this.startingYear = startingYear;
		} else {
			if (observableStartingYear == null) {
				observableStartingYear = new WritableValue(startingYear,
						Integer.class);
			} else {
				observableStartingYear.doSetValue(startingYear);
			}
		}
	}

	@Override
	protected String streamValue() {
		Integer result = null;
		if (!isObservable) {
			result = startingYear;
		} else {
			result = ((Integer) observableStartingYear.doGetValue());
		}
		return result.toString();
	}

	@Override
	public void setDefault() {
		setStartingYear(0);
	}

	public Integer handle(ConfigurationNode node) throws ConfigurationException {
		Integer result = (Integer)super.handle(node);
		setStartingYear(result);
		return result;
	}

	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (startingYear != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
			if (observableStartingYear.doGetValue() != null) {
				result = true;
			} else {
				log.error("Observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		}
		return result;
	}

	public Integer getStartingYear() {
		Integer result = null;
		if (!isObservable) {
			result = startingYear;
		} else {
			result = ((Integer) observableStartingYear.doGetValue());
		}
		return result;
	}

	@Override
	public WritableValue getObservableValue() {
		return observableStartingYear;
	}

	public void streamEvents(Integer value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}
}
