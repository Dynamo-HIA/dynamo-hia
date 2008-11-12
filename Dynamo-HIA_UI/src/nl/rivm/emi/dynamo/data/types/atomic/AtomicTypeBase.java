package nl.rivm.emi.dynamo.data.types.atomic;

public /* abstract */ class AtomicTypeBase<T> {
	final protected String XMLElementName;
	final protected T aValue;

	/**
	 * Default constructor, only use to initialize array.
	 */
	public AtomicTypeBase(){
		XMLElementName = null;
		aValue = null;
	}
	
	/**
	 * 
	 * @param tagName
	 * @param aValue is needed to be able to return the type.
	 */
	protected AtomicTypeBase(String tagName, T aValue) {
		XMLElementName = tagName;
		this.aValue = aValue;
	}

	public boolean isMyElement(String elementName) {
		boolean result = true;
		if (!XMLElementName.equalsIgnoreCase(elementName)) {
			result = false;
		}
		return result;
	}

	public String getElementName() {
		return XMLElementName;
	}

	public T getType() {
		return (T) aValue.getClass();
	}
}
