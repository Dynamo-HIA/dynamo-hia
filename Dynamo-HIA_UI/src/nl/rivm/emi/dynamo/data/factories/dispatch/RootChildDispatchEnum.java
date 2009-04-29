package nl.rivm.emi.dynamo.data.factories.dispatch;

/**
 * Enumeration mapping the relations between the name of a root-element in a configuration 
 * file and the Factory Object to turn it into a Configuration Model Object.
 */

import nl.rivm.emi.dynamo.data.factories.rootchild.AgnosticHierarchicalRootChildFactory;
import nl.rivm.emi.dynamo.data.factories.rootchild.AgnosticSingleRootChildFactory;
import nl.rivm.emi.dynamo.data.factories.rootchild.RootChildFactory;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;

public enum RootChildDispatchEnum {
	AMOUNTS("amounts", new AgnosticHierarchicalRootChildFactory()), //
	CLASSES("classes", new AgnosticHierarchicalRootChildFactory()), //
	CUTOFFS(XMLTagEntityEnum.CUTOFFS.getElementName(), new AgnosticHierarchicalRootChildFactory()), //
	DISTRIBUTIONTYPE("distributiontype", new AgnosticSingleRootChildFactory()), //
	DURATIONCLASS("durationclass", new AgnosticSingleRootChildFactory()), //
	HASNEWBORNS("hasnewborns", new AgnosticSingleRootChildFactory()), //
	REFERENCECLASS("referenceclass", new AgnosticSingleRootChildFactory()), //
	REFERENCEVALUE("referencevalue", new AgnosticSingleRootChildFactory()), //
	UNITTYPE("unittype", new AgnosticSingleRootChildFactory()), //
	MORTALITIES("mortalities", new AgnosticHierarchicalRootChildFactory()), //
	PREVALENCES("prevalences", new AgnosticHierarchicalRootChildFactory()), //
	STARTINGYEAR(XMLTagEntityEnum.STARTINGYEAR.getElementName(),
			new AgnosticSingleRootChildFactory()), // 
	NUMBEROFYEARS(XMLTagEntityEnum.NUMBEROFYEARS.getElementName(),
			new AgnosticSingleRootChildFactory()), // 
	SIMPOPSIZE(XMLTagEntityEnum.SIMPOPSIZE.getElementName(),
			new AgnosticSingleRootChildFactory()), // 
	MINAGE(XMLTagEntityEnum.MINAGE.getElementName(),
			new AgnosticSingleRootChildFactory()), // 
	MAXAGE(XMLTagEntityEnum.MAXAGE.getElementName(),
			new AgnosticSingleRootChildFactory()), // 
	TIMESTEP(XMLTagEntityEnum.TIMESTEP.getElementName(),
			new AgnosticSingleRootChildFactory()), // 
	RANDOMSEED(XMLTagEntityEnum.RANDOMSEED.getElementName(),
			new AgnosticSingleRootChildFactory()), // 
	RESULTTYPE(XMLTagEntityEnum.RESULTTYPE.getElementName(),
			new AgnosticSingleRootChildFactory()), // 
	POPFILENAME(XMLTagEntityEnum.POPFILENAME.getElementName(),
			new AgnosticSingleRootChildFactory()), //
	SCENARIOS(XMLTagEntityEnum.SCENARIOS.getElementName(),
			new AgnosticHierarchicalRootChildFactory()), //
	DISEASES(XMLTagEntityEnum.DISEASES.getElementName(),
			new AgnosticHierarchicalRootChildFactory()), //
	RISKFACTORS(XMLTagEntityEnum.RISKFACTORS.getElementName(),
			new AgnosticHierarchicalRootChildFactory()), //
	RRS(XMLTagEntityEnum.RRS.getElementName(),
			new AgnosticHierarchicalRootChildFactory()),
	SEXRATIO(XMLTagEntityEnum.SEXRATIO.getElementName(), 
			new AgnosticSingleRootChildFactory()); //

	private final String rootChildNodeName;
	private final RootChildFactory theFactory;

	private RootChildDispatchEnum(String rootNodeName,
			RootChildFactory theFactory) {
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