package nl.rivm.emi.dynamo.data.types.atomic.base;


import org.eclipse.core.databinding.UpdateValueStrategy;

/**
 * @author mondeelr
 *
 *Adds the layer that binds a parametrized type to the <code>XMLTagEntity</code>.
 
 * @param <T>
 */
/**
 * @author mondeelr
 *
 * @param <T>
 */
/**
 * @author mondeelr
 *
 * @param <T>
 */
public abstract class AtomicTypeBase<T> extends XMLTagEntity{
	final protected T aValue;
	@SuppressWarnings("rawtypes")
	protected UpdateValueStrategy modelUpdateValueStrategy;
	@SuppressWarnings("rawtypes")
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
	 * @param tagName Name of the XML element to which the Class is bound.
	 * @param aValue (super)Type of the value that is contained.
	 */
	protected AtomicTypeBase(String tagName, T aValue) {
		super(tagName);
		this.aValue = aValue;
	}

	
	/**
	 * @return The type for the value contained by the Object. 
	 */
	@SuppressWarnings("unchecked")
	public T getType() {
		return (T) aValue.getClass();
	}

	/**
	 * @return The value contained in the Object.
	 */
	public T getValue() {
		return aValue;
	}

	/**
	 * @return
	 */
	abstract public T getDefaultValue();

	/**
	 * @param modelValue
	 * @return
	 */
	abstract public String convert4View(Object modelValue);
	
	/**
	 * @param viewString
	 * @return
	 */
	abstract public Object convert4Model(String viewString);
	
	/**
	 * Like convert4View, but with WritableValue handling.
	 * @param modelValue The value for converting. 
	 * @return The <code>String</code> representation of the model-value.
	 */
	abstract public String convert4File(Object modelValue);

	/**
	 * The databinding conversion strategy from view to the backing Object.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	abstract public UpdateValueStrategy getModelUpdateValueStrategy();

	/**
	 * The databinding conversion strategy the backing Object to the view.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	abstract public UpdateValueStrategy getViewUpdateValueStrategy();
}

