package nl.rivm.emi.dynamo.data.writers;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;


/**
 * Class that for the moment contains items nescessary to be able to write out
 * XML files containing only repetative parameters contained below a rootelement
 * and a rootchildelement only.
 * 
 * Example: <rootelement> <rootchild> <age>0</age> <sex>0</sex> <value>4.5</value>
 * </rootchild> </rootelement>
 * 
 * The parameters each have a type that contains among others its elementname.
 * 
 * @author mondeelr
 * 
 */

public class FileControlEnumHelp {
	/**
	 * Static constants for configuration.
	 */
	/* W11 */
	static final String[] populationSizeStrings = { RootElementNamesEnum.POPULATIONSIZE.getNodeLabel(), "size",
			"age", "sex", "number" };
	/* W12 */
	static final String[] overallMortalityStrings = { RootElementNamesEnum.OVERALLMORTALITY.getNodeLabel(),
			"mortality", "age", "sex", "value" };
	/* W13 */
	static final String[] newbornsStrings = { RootElementNamesEnum.NEWBORNS.getNodeLabel(),
		"sexratio", "amount", "year", "number" };	
	/* W14 */
	static final String[] overallDALYWeightsStrings = {RootElementNamesEnum.OVERALLDALYWEIGHTS.getNodeLabel(),
			"weight", "age", "sex", "percent" };
	/* W20Cat */
	/* TODO add referenceclass. */
	static final String[] riskFactorCategoricalStrings = { RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel(),
		"classes", "class", "index", "name" };
	/* W20Con */
	/* W20Com */
	/* TODO add referenceclass, durationclass. */
	static final String[] riskFactorCompoundStrings = { RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel(),
		"classes", "class", "index", "name" };
	/* W21TmId */
	/* W21TmFp */
	/* W21TmMA */
	static final String[] transitionMatrixStrings = { RootElementNamesEnum.TRANSITIONMATRIX.getNodeLabel(),
			"transition", "age", "sex", "from", "to", "percent" };
	/* W21TdId */
	/* W21TdFp */
	/* W21TdMA */
	static final String[] transitionDriftStrings = { RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel(),
		"transition", "age", "sex", "mean" };
	static final String[] transitionDriftNettoStrings = { RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel(),
		"transition", "trend" };
	/* W22CatCom */
	static final String[] riskFactorPrevalenceCatStrings = {RootElementNamesEnum.RISKFACTORPREVALENCES_CATEGORICAL.getNodeLabel(),
			"prevalence", "age", "sex", "cat", "percent" };
	/* W22Con */
	/* W22ComDur */
	static final String[] riskFactorPrevalenceDurStrings = { RootElementNamesEnum.RISKFACTORPREVALENCES_DURATION.getNodeLabel(),
			"prevalence", "age", "sex", "duration", "percent" };
	/* W23Cat */
	static final String[] relRiskForDeathCatStrings = {RootElementNamesEnum.RELATIVERISKSFORDEATH_CATEGORICAL.getNodeLabel(),
			"relriskfordeath", "age", "sex", "cat", "value" };
	/* W23Con */
	static final String[] relRiskForDeathContStrings = {RootElementNamesEnum.RELATIVERISKSFORDEATH_CONTINUOUS.getNodeLabel(),
			"relriskfordeath", "age", "sex", "value" };
	/* W23Cmp */
	static final String[] relRiskForDeathCompStrings = {RootElementNamesEnum.RELATIVERISKSFORDEATH_COMPOUND.getNodeLabel(),
		"relriskfordeath", "age", "sex", "cat", "begin", "alpha", "end" };
	/* W23Cat */
	static final String[] relRiskForDisabilityCatStrings = {RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CATEGORICAL.getNodeLabel(),
			"relriskfordisability", "age", "sex", "cat", "value" };
	/* W23Con */
	static final String[] relRiskForDisabilityContStrings = {RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CONTINUOUS.getNodeLabel(),
			"relriskfordisability", "age", "sex", "value" };
	/* W23Cmp */
	/* W31 */
	static final String[] diseasePrevalencesStrings = {RootElementNamesEnum.DISEASEPREVALENCES.getNodeLabel(),
			"prevalence", "age", "sex", "percent" };
	/* W32 */
	static final String[] incidencesStrings = {RootElementNamesEnum.DISEASEINCIDENCES.getNodeLabel(), "incidence",
			"age", "sex", "value" };
	/* W33 */
	/* W34Cat */
	static final String[] relRiskForRiskfactorCatStrings = {RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL.getNodeLabel(),
			"relativerisk", "age", "sex", "cat", "value" };
	/* W34Con */
	static final String[] relRiskForRiskfactorConStrings = {RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CONTINUOUS.getNodeLabel(),
			"relativerisk", "age", "sex", "value" };
	/* W34Cmp */
	/* W35 */
	static final String[] relRiskFromDiseaseStrings = {RootElementNamesEnum.RELATIVERISKSFROMDISEASES.getNodeLabel(),
			"relativerisk", "age", "sex", "value" };
	/* W?? */
	static final String[] dALYWeightsStrings = {RootElementNamesEnum.DALYWEIGHTS.getNodeLabel(),
			"weight", "age", "sex", "percent" };
	/* Subtrees. */
	static final String[] classesStrings = {"classes", "class", "index", "name" };
}
