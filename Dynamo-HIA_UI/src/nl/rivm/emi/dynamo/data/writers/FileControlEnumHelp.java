package nl.rivm.emi.dynamo.data.writers;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;

/**
 * Class that for the moment contains items nescessary to be able to write out
 * XML files containing only repetative parameters contained below a rootelement
 * and a rootchildelement only.
 * 
 * Example: <rootelement> <rootchild> <age>0</age> <sex>0</sex>
 * <value>4.5</value> </rootchild> </rootelement>
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
	/* W01 */
	static final String[] dynamoSimulationStrings = {
			RootElementNamesEnum.SIMULATION.getNodeLabel(), "newborns",
			"startingYear", "numberOfYears", "simPopSize", "minAge", "maxAge",
			"timeStep", "randomSeed", "resultType", "popFileName", "scenarios",
			"diseases", "riskfactor", "RRs" };
	/* W11 */
	static final String[] populationSizeStrings = {
			RootElementNamesEnum.POPULATIONSIZE.getNodeLabel(), "size", "age",
			"sex", "number" };
	/* W12 */
	static final String[] overallMortalityStrings = {
			RootElementNamesEnum.OVERALLMORTALITY.getNodeLabel(), "mortality",
			"age", "sex", "value" };
	/* W13 */
	static final String[] newbornsStrings = {
			RootElementNamesEnum.NEWBORNS.getNodeLabel(), "sexratio", "amount",
			"year", "number" };
	/* W14 */
	static final String[] overallDALYWeightsStrings = {
			RootElementNamesEnum.OVERALLDALYWEIGHTS.getNodeLabel(), "weight",
			"age", "sex", "percent" };
	/* W20Cat */
	static final String[] riskFactorCategoricalStrings = {
			RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel(),
			"classes", "referenceclass" };
	/* W20Con */
	static final String[] riskFactorContinuousStrings = {
			RootElementNamesEnum.RISKFACTOR_CONTINUOUS.getNodeLabel(),
			"referencevalue"};
	/* W20Com */
	static final String[] riskFactorCompoundStrings = {
			RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel(), "classes",
			"referenceclass", "durationclass" };
	/* W21TmId */
	/* W21TmFp */
	/* W21TmMA */
	static final String[] transitionMatrixStrings = {
			RootElementNamesEnum.TRANSITIONMATRIX.getNodeLabel(), "transition",
			"age", "sex", "from", "to", "percent" };
	/* W21TdId */
	/* W21TdFp */
	/* W21TdMA */
	static final String[] transitionDriftStrings = {
			RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel(), "transition",
			"age", "sex", "mean" };
	static final String[] transitionDriftNettoStrings = {
			RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel(), "transition",
			"trend" };
	/* W22CatCom */
	static final String[] riskFactorPrevalenceCatStrings = {
			RootElementNamesEnum.RISKFACTORPREVALENCES_CATEGORICAL
					.getNodeLabel(), "prevalence", "age", "sex", "cat",
			"percent" };
	/* W22Con */
	/* W22ComDur */
	static final String[] riskFactorPrevalenceDurStrings = {
			RootElementNamesEnum.RISKFACTORPREVALENCES_DURATION.getNodeLabel(),
			"prevalence", "age", "sex", "duration", "percent" };
	/* W23Cat */
	static final String[] relRiskForDeathCatStrings = {
			RootElementNamesEnum.RELATIVERISKSFORDEATH_CATEGORICAL
					.getNodeLabel(), "relriskfordeath", "age", "sex", "cat",
			"value" };
	/* W23Con */
	static final String[] relRiskForDeathContStrings = {
			RootElementNamesEnum.RELATIVERISKSFORDEATH_CONTINUOUS
					.getNodeLabel(), "relriskfordeath", "age", "sex", "value" };
	/* W23Cmp */
	static final String[] relRiskForDeathCompStrings = {
			RootElementNamesEnum.RELATIVERISKSFORDEATH_COMPOUND.getNodeLabel(),
			"relriskfordeath", "age", "sex", "cat", "begin", "alpha", "end" };
	/* W23Cat */
	static final String[] relRiskForDisabilityCatStrings = {
			RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CATEGORICAL
					.getNodeLabel(), "relriskfordisability", "age", "sex",
			"cat", "value" };
	/* W23Con */
	static final String[] relRiskForDisabilityContStrings = {
			RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CONTINUOUS
					.getNodeLabel(), "relriskfordisability", "age", "sex",
			"value" };
	/* W23Cmp */
	/* W31 */
	static final String[] diseasePrevalencesStrings = {
			RootElementNamesEnum.DISEASEPREVALENCES.getNodeLabel(),
			"prevalence", "age", "sex", "percent" };
	/* W32 */
	static final String[] incidencesStrings = {
			RootElementNamesEnum.DISEASEINCIDENCES.getNodeLabel(), "incidence",
			"age", "sex", "value" };
	/* W33 */
	static final String[] excessMortalityStrings = {
		RootElementNamesEnum.EXCESSMORTALITY.getNodeLabel(), "unittype", 
			"mortality"	};
	/* W34Cat */
	static final String[] relRiskFromRiskfactorCatStrings = {
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL
					.getNodeLabel(), "relativerisk", "age", "sex", "cat",
			"value" };
	/* W34Con */
	static final String[] relRiskFromRiskfactorConStrings = {
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CONTINUOUS
					.getNodeLabel(), "relativerisk", "age", "sex", "value" };
	/* W34Cmp */
	/* W35 */
	static final String[] relRiskFromDiseaseStrings = {
			RootElementNamesEnum.RELATIVERISKSFROMDISEASES.getNodeLabel(),
			"relativerisk", "age", "sex", "value" };
	/* W?? */
	static final String[] dALYWeightsStrings = {
			RootElementNamesEnum.DALYWEIGHTS.getNodeLabel(), "weight", "age",
			"sex", "percent" };
	/* Subtrees. */
	static final String[] classesStrings = { "classes", "class", "index",
			"name" };
	// TODO(mondeelr) Change "name" and "sex" tags to unique values.
	static final String[] scenariosStrings = { "scenarios", "scenario",
			"uniquename", "successrate", "minage", "maxage", "sex",
			"transfilename", "prevfilename" };
	static final String[] diseasesStrings = { "diseases", "disease",
			"uniquename", "prevfilename", "incfilename", "excessmortfilename",
			"dalyweightsfilename" };
	static final String[] riskfactorStrings = { "riskfactor", "uniquename", "riskfactortransfilename"};
	static final String[] rrsStrings = { "RRs", "RR",
			"isRRfrom", "isRRto", "isRRfile" };
// TODO(mondeelr) For version 1.1.
	static final String[] cutoffsStrings = { "cutoffs", "cutoff" };
	static final String[] mortalityStrings = { "mortality", "age", "sex", "unit",
		"acutelyfatal", "curedfraction" };
}
