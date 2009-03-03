package nl.rivm.emi.dynamo.data.types.atomic;


public class ReferenceClass extends Index {
	static final protected String XMLElementName = "index";

	static final protected Integer hardUpperLimit = new Integer(9);

	public ReferenceClass(){
		super("referenceclass", new Integer(1), hardUpperLimit);
	}

//	public ConfigurationObjectBase handle(ConfigurationObjectBase modelObject,
//			ConfigurationNode node)
//			throws ConfigurationException {
//		Integer index = null;
//		if (XMLElementName.equals(node.getName())) {
//			index = Integer.decode((String) node.getValue());
//		} else {
//			throw new ConfigurationException("Incorrect tag \""
//					+ node.getName() + "\" found, \"" + XMLElementName
//					+ "\" expected.");
//		}
//
//		if (index != null) {
//			((IReferenceClass) modelObject).putReferenceClass(index);
//		} else {
//			throw new ConfigurationException("Incorrect \"" + XMLElementName + "\" tag.");
//		}
//		return modelObject;
//	}
}
