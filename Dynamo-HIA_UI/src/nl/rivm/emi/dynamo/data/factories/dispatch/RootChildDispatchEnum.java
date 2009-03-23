package nl.rivm.emi.dynamo.data.factories.dispatch;

/**
 * Enumeration mapping the relations between the name of a root-element in a configuration 
 * file and the Factory Object to turn it into a Configuration Model Object.
 */

import nl.rivm.emi.dynamo.data.factories.rootchild.AgnosticHierarchicalRootChildFactory;
import nl.rivm.emi.dynamo.data.factories.rootchild.AgnosticSingleRootChildFactory;
import nl.rivm.emi.dynamo.data.factories.rootchild.RootChildFactory;

public enum RootChildDispatchEnum {
	REFERENCECLASS("referenceclass", new AgnosticSingleRootChildFactory()),
	REFERENCEVALUE("referencevalue", new AgnosticSingleRootChildFactory()),
	DURATIONCLASS("durationclass", new AgnosticSingleRootChildFactory()),
	HASNEWBORNS("hasnewborns", new AgnosticSingleRootChildFactory()),
	CLASSES("classes", new AgnosticHierarchicalRootChildFactory()),
	UNITTYPE("unittype", new AgnosticSingleRootChildFactory()),
	MORTALITY("mortality", new AgnosticHierarchicalRootChildFactory());

	private final String rootChildNodeName;
	private final RootChildFactory theFactory;

	private RootChildDispatchEnum(String rootNodeName, RootChildFactory theFactory) {
		this.theFactory = theFactory;
		this.rootChildNodeName = rootNodeName;
	}

	public String getRootNodeName() {
		return rootChildNodeName;
	}

	public RootChildFactory getTheFactory() {
		return theFactory;
	}
}