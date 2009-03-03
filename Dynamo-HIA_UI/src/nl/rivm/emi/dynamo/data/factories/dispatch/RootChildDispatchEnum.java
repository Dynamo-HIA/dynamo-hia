package nl.rivm.emi.dynamo.data.factories.dispatch;

/**
 * Enumeration mapping the relations between the name of a root-element in a configuration 
 * file and the Factory Object to turn it into a Configuration Model Object.
 */

import nl.rivm.emi.dynamo.data.factories.AgnosticRootChildFactoryBase;

public enum RootChildDispatchEnum {
	REFERENCECLASS("referenceclass", null), // TODO
	REFERENCEVALUE("referencevalue", null), // TODO
	DURATIONCLASS("durationclass", null), // TODO
	HASNEWBORNS("hasnewborns", null);// TODO


	private final String rootChildNodeName;
	private final AgnosticRootChildFactoryBase theFactory;

	private RootChildDispatchEnum(String rootNodeName, AgnosticRootChildFactoryBase theFactory) {
		this.theFactory = theFactory;
		this.rootChildNodeName = rootNodeName;
	}

	public String getRootNodeName() {
		return rootChildNodeName;
	}

	public AgnosticRootChildFactoryBase getTheFactory() {
		return theFactory;
	}
}