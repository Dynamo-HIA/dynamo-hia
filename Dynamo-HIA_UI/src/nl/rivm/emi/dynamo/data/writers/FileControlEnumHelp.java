package nl.rivm.emi.dynamo.data.writers;


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
	static final String[] populationSizeStrings = { "populationsize", "size",
			"age", "sex", "number" };
	/* W12 */
	static final String[] overallMortalityStrings = { "overallmortality",
			"mortality", "age", "sex", "value" };
	/* W13 */
	/* W14 */
	static final String[] overallDALYWeightsStrings = { "overalldalyweights",
			"weight", "age", "sex", "percent" };
	/* W20Cat */
	/* W20Con */
	/* W20Com */
	/* W21TmId */
	/* W21TmFp */
	/* W21TmMA */
	static final String[] transitionMatrixStrings = { "transitionmatrix",
			"transition", "age", "sex", "from", "to", "percent" };
	/* W21TdId */
	/* W21TdFp */
	/* W21TdMA */
	/* W22CatCom */
	static final String[] riskFactorPrevalenceCatStrings = { "riskfactorprevalences_categorical",
			"prevalence", "age", "sex", "cat", "percent" };
	/* W22Con */
	/* W22ComDur */
	static final String[] riskFactorPrevalenceDurStrings = { "riskfactorprevalences_duration",
			"prevalence", "age", "sex", "duration", "percent" };
	/* W23Cat */
	static final String[] relRiskForDeathCatStrings = { "relrisksfordeath_categorical",
			"relriskfordeath", "age", "sex", "cat", "value" };
	/* W23Con */
	static final String[] relRiskForDeathContStrings = { "relrisksfordeath_continuous",
			"relriskfordeath", "age", "sex", "value" };
	/* W23Cmp */
	/* W31 */
	static final String[] diseasePrevalencesStrings = { "diseaseprevalences",
			"prevalence", "age", "sex", "percent" };
	/* W32 */
	static final String[] incidencesStrings = { "diseaseincidences", "incidence",
			"age", "sex", "value" };
	/* W33 */
	/* W34Cat */
	static final String[] relRiskForRiskfactorCatStrings = { "rrisksforriskfactor_categorical",
			"relativerisk", "age", "sex", "cat", "value" };
	/* W34Con */
	static final String[] relRiskForRiskfactorConStrings = { "rrisksforriskfactor_continuous",
			"relativerisk", "age", "sex", "value" };
	/* W34Cmp */
	/* W35 */
	static final String[] relRiskFromDiseaseStrings = { "rrisksfromdisease",
			"relativerisk", "age", "sex", "value" };
	/* W?? */
	static final String[] dALYWeightsStrings = { "dalyweights",
			"weight", "age", "sex", "percent" };
}
