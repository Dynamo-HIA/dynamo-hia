package nl.rivm.emi.dynamo.data.objects.parts;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.data.interfaces.ITimeStep;
import nl.rivm.emi.dynamo.data.types.atomic.TimeStepType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class TimeStepImpl extends TimeStepType implements ITimeStep {
	Log log = LogFactory.getLog(this.getClass().getName());
	Float timeStep = null;

	WritableValue observableTimeStep = null;

	boolean isObservable = false;

	/**
	 * Block default construction.
	 */
	@SuppressWarnings("unused")
	private TimeStepImpl() throws ConfigurationException {

	}

	public TimeStepImpl(boolean isObservable) throws ConfigurationException {
		this.isObservable = isObservable;
	}

	@Override
	protected String streamValue() {
		Float result = null;
		if (!isObservable) {
			result = timeStep;
		} else {
			result = ((Float) observableTimeStep.doGetValue());
		}
		return result.toString();
	}

	@Override
	public void setDefault() {
		setTimeStep(0F);
	}

	public Float handle(ConfigurationNode node) throws ConfigurationException {
		Float result = (Float)super.handle(node);
		setTimeStep(result);
		return result;
	}

	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (timeStep != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
			if (observableTimeStep.doGetValue() != null) {
				result = true;
			} else {
				log.error("Observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		}
		return result;
	}

	public Float getTimeStep() {
		Float result = null;
		if (!isObservable) {
			result = timeStep;
		} else {
			result = ((Float) observableTimeStep.doGetValue());
		}
		return result;
	}

	public void setTimeStep(Float timeStep) {
		if (!isObservable) {
			this.timeStep = timeStep;
		} else {
			if (observableTimeStep == null) {
				observableTimeStep = new WritableValue(timeStep,
						Integer.class);
			} else {
				observableTimeStep.doSetValue(timeStep);
			}
		}
	}

	@Override
	public WritableValue getObservableValue() {
		return observableTimeStep;
	}

	public void streamEvents(Float value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

}
