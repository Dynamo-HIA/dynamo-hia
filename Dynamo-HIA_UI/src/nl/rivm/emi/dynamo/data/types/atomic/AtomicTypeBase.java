package nl.rivm.emi.dynamo.data.types.atomic;

import org.eclipse.core.databinding.UpdateValueStrategy;

public abstract class AtomicTypeBase<T> extends XMLTagEntity{
//	final protected String XMLElementName;
	final protected T aValue;
	protected UpdateValueStrategy modelUpdateValueStrategy;
	protected UpdateValueStrategy viewUpdateValueStrategy;

	/**
	 * Default constructor, only use to initialize array.
	 */
	public AtomicTypeBase(){
		super(null);
		aValue = null;
	}
	
	/**
	 * 
	 * @param tagName
	 * @param aValue is needed to be able to return the type.
	 */
	protected AtomicTypeBase(String tagName, T aValue) {
		super(tagName);
		this.aValue = aValue;
	}

//	public boolean isMyElement(String elementName) {
//		boolean result = true;
//		if (!XMLElementName.equalsIgnoreCase(elementName)) {
//			result = false;
//		}
//		return result;
//	}

//	public String getXMLElementName() {
//		return XMLElementName;
//	}
//
	public T getType() {
		return (T) aValue.getClass();
	}

	public T getValue() {
		return aValue;
	}

	abstract public String convert4View(Object modelValue);
	
	abstract Object convert4Model(String viewString);
	
	abstract public UpdateValueStrategy getModelUpdateValueStrategy();

	abstract public UpdateValueStrategy getViewUpdateValueStrategy();
}
