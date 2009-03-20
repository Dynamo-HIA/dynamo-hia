package nl.rivm.emi.dynamo.data.types.atomic;

import org.eclipse.core.databinding.UpdateValueStrategy;

public abstract class AtomicTypeBase<T> extends XMLTagEntity{
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

	public T getType() {
		return (T) aValue.getClass();
	}

	public T getValue() {
		return aValue;
	}

	abstract public T getDefaultValue();

	abstract public String convert4View(Object modelValue);
	
	abstract public Object convert4Model(String viewString);
	
	/**
	 * Like convert4View, but with WritableValue handling.
	 * @param modelValue
	 * @return
	 */
	abstract public String convert4File(Object modelValue);

	abstract public UpdateValueStrategy getModelUpdateValueStrategy();

	abstract public UpdateValueStrategy getViewUpdateValueStrategy();


}

