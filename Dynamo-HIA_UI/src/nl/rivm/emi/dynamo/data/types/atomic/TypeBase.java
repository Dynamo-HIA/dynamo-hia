package nl.rivm.emi.dynamo.data.types.atomic;

public abstract class TypeBase {
	final protected String XMLElementName;

	protected TypeBase(String tagName) {
		XMLElementName = tagName;
	}
	
	public boolean isMyElement(String elementName){
		boolean result = true;
		if (!XMLElementName.equalsIgnoreCase(elementName)){
			result = false;
		}
		return result;
	}

	public String isGetElementName(){
		return XMLElementName;
		}
}
