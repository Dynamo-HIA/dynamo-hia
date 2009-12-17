package nl.rivm.emi.dynamo.data.writers;

import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
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
			RootElementNamesEnum.SIMULATION.getNodeLabel(), "hasnewborns",
			"startingYear", "numberOfYears", "simPopSize", "minAge", "maxAge",
			"timeStep", "randomSeed", "resultType", "popFileName", "scenarios",
			"diseases", "riskfactors", "RRs" };
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
			RootElementNamesEnum.NEWBORNS.getNodeLabel(),
			XMLTagEntityEnum.SEXRATIO.getElementName(),
			XMLTagEntityEnum.STARTINGYEAR.getElementName(),
			XMLTagEntityEnum.AMOUNTS.getElementName() };
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
			XMLTagEntityEnum.REFERENCEVALUE.getElementName(),
			XMLTagEntityEnum.CUTOFFS.getElementName() };
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
	static final String[] transitionMatrixZeroStrings = { RootElementNamesEnum.TRANSITIONMATRIX_ZERO
			.getNodeLabel() };
	static final String[] transitionMatrixNettoStrings = { RootElementNamesEnum.TRANSITIONMATRIX_NETTO
			.getNodeLabel() };
	/* W21TdId */
	/* W21TdFp */
	/* W21TdMA */
	static final String[] transitionDriftStrings = {
			RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel(), "transition",
			"age", "sex", "mean" };
	static final String[] transitionDriftNettoStrings = {
			RootElementNamesEnum.TRANSITIONDRIFT_NETTO.getNodeLabel(),
			XMLTagEntityEnum.TREND.getElementName() };
	static final String[] transitionDriftZeroStrings = { RootElementNamesEnum.TRANSITIONDRIFT_ZERO
			.getNodeLabel() };
	/* W22CatCom */
	static final String[] riskFactorPrevalenceCatStrings = {
			RootElementNamesEnum.RISKFACTORPREVALENCES_CATEGORICAL
					.getNodeLabel(), "prevalence", "age", "sex", "cat",
			"percent" };
	/* W22Con */
	static final String[] riskFactorPrevalenceConStrings = {
			RootElementNamesEnum.RISKFACTORPREVALENCES_CONTINUOUS
					.getNodeLabel(),
			XMLTagEntityEnum.DISTRIBUTIONTYPE.getElementName(),
			XMLTagEntityEnum.PREVALENCES.getElementName() };
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
			"relriskfordeath", "age", "sex", "cat", "begin", "alfa", "end" };
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
	static final String[] relRiskForDisabilityCompStrings = {
			RootElementNamesEnum.RELATIVERISKSFORDISABILITY_COMPOUND
					.getNodeLabel(), "relriskfordisability", "age", "sex",
			"cat", "begin", "alfa", "end" };
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
			RootElementNamesEnum.EXCESSMORTALITY.getNodeLabel(),
			"unittype",
			/* Alternative, non-conforming layout. */XMLTagEntityEnum.MORTALITIES
					.getElementName()
	/* XMLTagEntityEnum.MORTALITY.getElementName() */};
	/* W34Cat */
	static final String[] relRiskFromRiskfactorCatStrings = {
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL
					.getNodeLabel(), "relativerisk", "age", "sex", "cat",
			"value" };
	/* W34Comp */
	static final String[] relRiskFromRiskfactorCompStrings = {
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_COMPOUND
					.getNodeLabel(), "relativerisk", "age", "sex", "cat",
			"begin", "alfa", "end" };
	/* W34Con */
	static final String[] relRiskFromRiskfactorConStrings = {
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CONTINUOUS
					.getNodeLabel(), "relativerisk", "age", "sex", "value" };
	/* W34Cmp */
	/* W35 */
	static final String[] relRiskFromDiseaseStrings = {
			RootElementNamesEnum.RELATIVERISKSFROMDISEASE.getNodeLabel(),
			"relativerisk", "age", "sex", "value" };
	/* W?? */
	static final String[] dALYWeightsStrings = {
			RootElementNamesEnum.DALYWEIGHTS.getNodeLabel(), "weight", "age",
			"sex", "percent" };
	/* Subtrees. */
	static final String[] classesStrings = { "classes", "class", "flexdex",
			"name" };
	static final String[] scenariosStrings = {
			XMLTagEntityEnum.SCENARIOS.getElementName(),
			XMLTagEntityEnum.SCENARIO.getElementName(),
			XMLTagEntityEnum.UNIQUENAME.getElementName(),
			XMLTagEntityEnum.SUCCESSRATE.getElementName(),
			XMLTagEntityEnum.TARGETMINAGE.getElementName(),
			XMLTagEntityEnum.TARGETMAXAGE.getElementName(),
			XMLTagEntityEnum.TARGETSEX.getElementName(),
			XMLTagEntityEnum.TRANSFILENAME.getElementName(),
			XMLTagEntityEnum.PREVFILENAME.getElementName() };
	static final String[] diseasesStrings = { "diseases", "disease",
			"uniquename", "prevfilename", "incfilename", "excessmortfilename",
			"dalyweightsfilename" };
	static final String[] riskfactorsStrings = {
			XMLTagEntityEnum.RISKFACTORS.getElementName(),
			XMLTagEntityEnum.RISKFACTOR.getElementName(),
			XMLTagEntityEnum.UNIQUENAME.getElementName(),
			XMLTagEntityEnum.TRANSFILENAME.getElementName(),
			XMLTagEntityEnum.PREVFILENAME.getElementName() };
	static final String[] rrsStrings = { "RRs", "RR", "RRindex", "isRRfrom",
			"isRRto", "isRRFile" };
	static final String[] cutoffsStrings = {
			XMLTagEntityEnum.CUTOFFS.getElementName(),
			XMLTagEntityEnum.CUTOFF.getElementName(),
			XMLTagEntityEnum.FLEXDEX.getElementName(),
			XMLTagEntityEnum.STANDARDVALUE.getElementName() };
	static final String[] mortalityStrings = {
			XMLTagEntityEnum.MORTALITY.getElementName(),
			XMLTagEntityEnum.AGE.getElementName(),
			XMLTagEntityEnum.SEX.getElementName(),
			XMLTagEntityEnum.UNIT.getElementName(),
			XMLTagEntityEnum.ACUTELYFATAL.getElementName(),
			XMLTagEntityEnum.CUREDFRACTION.getElementName() };
	// Alternative, non-conforming structure.
	static final String[] mortalitiesStrings = {
			XMLTagEntityEnum.MORTALITIES.getElementName(),
			XMLTagEntityEnum.MORTALITY.getElementName(),
			XMLTagEntityEnum.AGE.getElementName(),
			XMLTagEntityEnum.SEX.getElementName(),
			XMLTagEntityEnum.UNIT.getElementName(),
			XMLTagEntityEnum.ACUTELYFATAL.getElementName(),
			XMLTagEntityEnum.CUREDFRACTION.getElementName() };
	static final String[] prevalencesContinuousStrings = {
			XMLTagEntityEnum.PREVALENCES.getElementName(),
			XMLTagEntityEnum.PREVALENCE.getElementName(),
			XMLTagEntityEnum.AGE.getElementName(),
			XMLTagEntityEnum.SEX.getElementName(),
			XMLTagEntityEnum.MEAN.getElementName(),
			XMLTagEntityEnum.STANDARDDEVIATION.getElementName(),
			XMLTagEntityEnum.SKEWNESS.getElementName() };
	static final String[] amountsStrings = {
			XMLTagEntityEnum.AMOUNTS.getElementName(),
			XMLTagEntityEnum.AMOUNT.getElementName(),
			XMLTagEntityEnum.YEAR.getElementName(),
			XMLTagEntityEnum.NUMBER.getElementName() };
	// Alternative, non-conforming structure.
	static final String[] trendsStrings = {
			XMLTagEntityEnum.TRENDS.getElementName(),
			XMLTagEntityEnum.TREND.getElementName() };
	/* Estimated parameters */
	static final String[] attributableMortalitiesStrings = {
			RootElementNamesEnum.ATTRIBUTABLEMORTALITIES.getNodeLabel(),
			"attributableMortality", "age", "sex", "value" };
	static final String[] baselineOtherMortalitiesStrings = {
		RootElementNamesEnum.BASELINEOTHERMORTALITIES.getNodeLabel(),
		"baselineOtherMortality", "age", "sex", "value" };
	static final String[] baselineFatalIncidencesStrings = {
			RootElementNamesEnum.BASELINEFATALINCIDENCES.getNodeLabel(),
			"baselineFatalIncidence", "age", "sex", "value" };
	static final String[] baselineIncidencesStrings = {
		RootElementNamesEnum.BASELINEINCIDENCES.getNodeLabel(),
		"baselineIncidence", "age", "sex", "value" };
	static final String[] relativeRisksStrings = {
		RootElementNamesEnum.RELATIVERISKS.getNodeLabel(),
		"baselineIncidence", "age", "sex", "value" };
}
