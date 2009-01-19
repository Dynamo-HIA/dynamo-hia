package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public abstract class AggregateTypeBase extends LinkedHashSet<AtomicTypeBase> {
	final protected Signature signature;

	protected AggregateTypeBase(AtomicTypeBase[] theTypes) {
		super();
		signature = new Signature();
		for (AtomicTypeBase aType : theTypes) {
			add(aType);
			signature.add(aType.getXMLElementName());
		}
		signature.finish();
	}

	public boolean areMyElements(String[] elementNames) {
		boolean result = false;
		Signature testSignature = new Signature();
		for (String elementName : elementNames) {
			testSignature.add(elementName);
		}
		if (testSignature.get().equalsIgnoreCase(signature.get())) {
			result = true;
		}
		return result;
	}

	public String[] getElementNames() {
		String[] resultNames = new String[size()];
		int count = 0;
		for (AtomicTypeBase aType : this) {
			resultNames[count] = aType.getXMLElementName();
			count++;
		}
		return resultNames;
	}

	/**
	 * Object that contains a concatenation of all fields contained in the
	 * aggregate type.
	 */
	private class Signature extends TreeSet<String> {
		String resultSignature = null;

		public Signature() {
			super();
		}

		public boolean add(String name) {
			if (resultSignature == null) {
				add(name);
				return true;
			} else {
				return false;
			}
		}

		String finish() {
			StringBuffer workSignature = new StringBuffer();
			for (String string : this) {
				workSignature.append(string.toLowerCase());
			}
			resultSignature = workSignature.toString();
			return resultSignature;
		}

		String get() {
			return resultSignature;
		}
	}
}
