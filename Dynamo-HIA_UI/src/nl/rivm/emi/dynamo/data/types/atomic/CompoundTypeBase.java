package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.Set;
import java.util.TreeSet;

public abstract class CompoundTypeBase {
	final protected String XMLElementNames[];
	final protected String signature;

	protected CompoundTypeBase(String[] tagNames) {
		XMLElementNames = tagNames;
		String workSignature = makeSignature(tagNames);
		if (workSignature.length() != 0) {
			signature = workSignature;
		} else {
			signature = null;
		}
	}

	public boolean areMyElements(String[] elementNames) {
		boolean result = false;
		if (signature != null) {
			if (signature.equals(makeSignature(elementNames))) {
				result = true;
			}
		}
		return result;
	}

	public String[] getElementNames() {
		return XMLElementNames;
	}

	String makeSignature(String[] names) {
		Set<String> sortedSet = new TreeSet<String>();
		for (int count = 0; count < names.length; count++) {
			sortedSet.add(names[count]);
		}
		StringBuffer signature = new StringBuffer();
		for (String string : sortedSet) {
			signature.append(string.toLowerCase());
		}
		return signature.toString();
	}
}
