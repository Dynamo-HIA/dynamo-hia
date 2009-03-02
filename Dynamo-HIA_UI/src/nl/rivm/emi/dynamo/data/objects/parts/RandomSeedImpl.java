package nl.rivm.emi.dynamo.data.objects.parts;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.data.interfaces.IRandomSeed;
import nl.rivm.emi.dynamo.data.types.atomic.RandomSeedType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class RandomSeedImpl extends RandomSeedType implements IRandomSeed {
	Log log = LogFactory.getLog(this.getClass().getName());
	Float randomSeed = null;

	WritableValue observableRandomSeed = null;

	boolean isObservable = false;

	/**
	 * Block default construction.
	 */
	@SuppressWarnings("unused")
	private RandomSeedImpl() throws ConfigurationException {

	}

	public RandomSeedImpl(boolean isObservable) throws ConfigurationException {
		this.isObservable = isObservable;
	}

	protected String streamValue() {
		Float result = null;
		if (!isObservable) {
			result = randomSeed;
		} else {
			result = ((Float) observableRandomSeed.doGetValue());
		}
		return result.toString();
	}

	@Override
	public void setDefault() {
		setRandomSeed(0F);
	}

	public Float handle(ConfigurationNode node) throws ConfigurationException {
		Float result = (Float)super.handle(node);
		setRandomSeed(result);
		return result;
	}
	
	public void streamEvents(Float value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
	}

	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (randomSeed != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
			if (observableRandomSeed.doGetValue() != null) {
				result = true;
			} else {
				log.error("Observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		}
		return result;
	}

	public Float getRandomSeed() {
		Float result = null;
		if (!isObservable) {
			result = randomSeed;
		} else {
			result = ((Float) observableRandomSeed.doGetValue());
		}
		return result;
	}

	public void setRandomSeed(Float randomSeed) {
		if (!isObservable) {
			this.randomSeed = randomSeed;
		} else {
			if (observableRandomSeed == null) {
				observableRandomSeed = new WritableValue(randomSeed,
						Integer.class);
			} else {
				observableRandomSeed.doSetValue(randomSeed);
			}
		}
	}

	@Override
	public WritableValue getObservableValue() {
		return observableRandomSeed;
	}


}
