package nl.rivm.emi.dynamo.data.objects.parts;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.data.interfaces.ISimPopSize;
import nl.rivm.emi.dynamo.data.types.atomic.SimPopSizeType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class SimPopSizeImpl extends SimPopSizeType implements ISimPopSize {
	Log log = LogFactory.getLog(this.getClass().getName());
	Integer simPopSize = null;

	WritableValue observableSimPopSize = null;

	boolean isObservable = false;

	/**
	 * Block default construction.
	 */
	@SuppressWarnings("unused")
	private SimPopSizeImpl() throws ConfigurationException{

	}

	public SimPopSizeImpl(boolean isObservable)  throws ConfigurationException{
		this.isObservable = isObservable;
	}

	public Integer getSimPopSize() {
			Integer result = null;
			if (!isObservable) {
				result = simPopSize;
			} else {
				result = ((Integer) observableSimPopSize.doGetValue());
			}
			return result;
		}

	public void setSimPopSize(Integer simPopSize) {
		if (!isObservable) {
			this.simPopSize = simPopSize;
		} else {
			if (observableSimPopSize == null) {
				observableSimPopSize = new WritableValue(simPopSize,
						Integer.class);
			} else {
				observableSimPopSize.doSetValue(simPopSize);
			}
		}
	}

	@Override
	protected String streamValue() {
		Integer result = null;
		if (!isObservable) {
			result = simPopSize;
		} else {
			result = ((Integer) observableSimPopSize.doGetValue());
		}
		return result.toString();
	}

	@Override
	public void setDefault() {
		setSimPopSize(0);
	}

	public Integer handle(ConfigurationNode node) throws ConfigurationException {
		Integer result = (Integer)super.handle(node);
		setSimPopSize(result);
		return result;
	}

	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (simPopSize != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
			if (observableSimPopSize.doGetValue() != null) {
				result = true;
			} else {
				log.error("Observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		}
		return result;
	}

	@Override
	public WritableValue getObservableValue() {
		return observableSimPopSize;
	}

	public void streamEvents(Integer value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}
}
