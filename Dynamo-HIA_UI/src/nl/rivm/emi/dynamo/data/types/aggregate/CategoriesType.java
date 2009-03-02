package nl.rivm.emi.dynamo.data.types.aggregate;

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
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.data.interfaces.ICategoriesType;
import nl.rivm.emi.dynamo.data.objects.xml.BaseXMLHandler;
import nl.rivm.emi.dynamo.data.objects.xml.XMLValueConverter;
import nl.rivm.emi.dynamo.data.types.atomic.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.data.types.interfaces.IPayloadHandler;
import nl.rivm.emi.dynamo.data.types.interfaces.IRecursiveXMLHandlingLayer;
import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class CategoriesType extends NumberRangeTypeBase<Integer> implements
		ICategoriesType, IPayloadHandler<String>, IRecursiveXMLHandlingLayer<Integer>, XMLValueConverter<Integer> {

	Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Value handling.
	 */
	LinkedHashMap<Integer, Object> theCategories = new LinkedHashMap<Integer, Object>();

	boolean isObservable = false;

	public CategoriesType(boolean isObservable, Integer maxIndex) {
		super(XMLElementName, new Integer(1), maxIndex);
		this.isObservable = isObservable;
		myHandler = new BaseXMLHandler<Integer>(XMLElementName, this);
	}

	public Integer getIndex() {
		Integer result = null;
		if (!isObservable) {
			result = index;
		} else {
			result = ((Integer) observableIndex.doGetValue());
		}
		return result;
	}

	public void setIndex(Integer name) {
		if (!isObservable) {
			this.index = index;
		} else {
			if (observableIndex == null) {
				observableIndex = new WritableValue(index, Integer.class);
			} else {
				observableIndex.doSetValue(index);
			}
		}
	}

	/**
	 * Databinding department.
	 * 
	 * @return
	 */
	public WritableValue getObservableValue() {
		return observableIndex;
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
	static final protected String XMLElementName = "index";
	protected final BaseXMLHandler<Integer> myHandler;

	public Integer handle(ConfigurationNode node) throws ConfigurationException {
		return myHandler.handle(node);
	}

	public void streamEvents(Integer value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		myHandler.streamEvents(value, writer, eventFactory);
	}

	public String streamValue() {
		Integer result = null;
		if (!isObservable) {
			result = index;
		} else {
			result = ((Integer) observableIndex.doGetValue());
		}
		return result.toString();
	}

	public void setDefault() {
		setIndex(0);
	}

	public Integer convert(String valueString) {
		return (Integer) convert4Model(valueString);
	}

	public String streamValue(Integer value) {
		return convert4View(value);
	}

	/**
	 * For the moment a very cursory check whether anything is there.
	 */
	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (index != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
			if (observableIndex.doGetValue() != null) {
				result = true;
			} else {
				log.error("Observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		}
		return result;
	}

	@Override
	public Integer fromString(String inputString) {
		return (Integer) convert4Model(inputString);
	}

	@Override
	public boolean inRange(Integer testValue) {
		boolean aboveMin = false;
		boolean underMax = false;
		if (testValue != null) {
			if (testValue.compareTo(MIN_VALUE) >= 0) {
				aboveMin = true;
			}
			if (testValue.compareTo(MAX_VALUE) <= 0) {
				underMax = true;
			}
		}
		return (aboveMin && underMax);
	}

	@Override
	public String toString(Integer inputValue) {
		return convert4View(inputValue);
	}

	public String getCategoryName(Integer index) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNumberOfCategories() {
		// TODO Auto-generated method stub
		return 0;
	}

	public WritableValue getObservableCategoryName(Integer index) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object putCategory(Integer index, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String manufacture(
			ArrayList<AtomicTypeObjectTuple> remainingNonContainerData)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

	public void streamEvents(String value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}
}
