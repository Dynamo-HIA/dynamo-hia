package nl.rivm.emi.dynamo.data.factories.dispatch;

import nl.rivm.emi.dynamo.data.factories.rootchild.AgnosticHierarchicalRootChildFactory;
import nl.rivm.emi.dynamo.data.factories.rootchild.AgnosticSingleRootChildFactory;
import nl.rivm.emi.dynamo.data.factories.rootchild.RootChildFactory;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;

/**
 * @author mondeelr
 * 
 *         Enumeration mapping the relations between the name of a
 *         rootchild-element in a configuration file and the Factory Object to
 *         turn it into a configuration modelobject part.
 * 
 *         The instances of the enumeration are put into the
 *         RootChildDispatchMap to be able to find them by rootchildelementname.
 */
public enum RootChildDispatchEnum {
	AMOUNTS("amounts", new AgnosticHierarchicalRootChildFactory()), //
	CLASSES("classes", new AgnosticHierarchicalRootChildFactory()), //
	CUTOFFS(XMLTagEntityEnum.CUTOFFS.getElementName(),
			new AgnosticHierarchicalRootChildFactory()), //
	DISTRIBUTIONTYPE("distributiontype", new AgnosticSingleRootChildFactory()), //
	DURATIONCLASS("durationclass", new AgnosticSingleRootChildFactory()), //
	HASNEWBORNS("hasnewborns", new AgnosticSingleRootChildFactory()), //
	REFERENCECLASS("referenceclass", new AgnosticSingleRootChildFactory()), //
	REFERENCEVALUE("referencevalue", new AgnosticSingleRootChildFactory()), //
	UNITTYPE("unittype", new AgnosticSingleRootChildFactory()), //
	PARAMETERTYPE(XMLTagEntityEnum.PARAMETERTYPE.getElementName(),
			new AgnosticSingleRootChildFactory()), //
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
			new AgnosticHierarchicalRootChildFactory()), //
	SEXRATIO(XMLTagEntityEnum.SEXRATIO.getElementName(),
			new AgnosticSingleRootChildFactory()), //
	TREND(XMLTagEntityEnum.TREND.getElementName(),
			new AgnosticSingleRootChildFactory()); //

	/**
	 * Supported elementname.
	 */
	private final String rootChildNodeName;
	/**
	 * Factory able to process it.
	 */
	private final RootChildFactory theFactory;

	/**
	 * @param rootNodeName
	 *            The supported elementname.
	 * @param theFactory
	 *            Factory able to process the elementname (and subnodes).
	 */
	private RootChildDispatchEnum(String rootNodeName,
			RootChildFactory theFactory) {
		this.theFactory = theFactory;
		this.rootChildNodeName = rootNodeName;
	}

	/**
	 * @return The supported elementname.
	 */
	public String getRootNodeName() {
		return rootChildNodeName;
	}

	/**
	 * @return The factory able to process it.
	 */
	public RootChildFactory getTheFactory() {
		return theFactory;
	}
}