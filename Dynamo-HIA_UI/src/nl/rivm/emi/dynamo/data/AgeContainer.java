package nl.rivm.emi.dynamo.data;

import java.util.TreeMap;

public class AgeContainer {
	TreeMap<Integer, Object> theContainer;

	public AgeContainer() {
		super();
		theContainer = new TreeMap<Integer, Object>();
	}

	private Object get(Integer arg0) {
		return theContainer.get(arg0);
	}

	public Integer put(Integer arg0, Object arg1) {
		return (Integer) theContainer.put(arg0, arg1);
	}

}