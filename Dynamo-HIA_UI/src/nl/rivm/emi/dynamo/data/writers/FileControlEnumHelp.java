package nl.rivm.emi.dynamo.data.writers;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;

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
			"age", "sex", "value" };
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
	static final String[] riskFactorPrevalenceCatStrings = { "prevalences",
			"prevalence", "age", "sex", "cat", "percent" };
	/* W22Con */
	/* W22ComDur */
	static final String[] riskFactorPrevalenceDurStrings = { "prevalences",
			"prevalence", "age", "sex", "duration", "percent" };
	/* W23Cat */
	static final String[] relRiskForDeathCatStrings = { "relrisksfordeath",
			"relriskfordeath", "age", "sex", "cat", "value" };
	/* W23Cat */
	static final String[] relRiskForDeathContStrings = { "relrisksfordeath",
			"relriskfordeath", "age", "sex", "value" };
	/* W23Com */
	/* W31 */
	static final String[] diseasePrevalencesStrings = { "diseaseprevalences",
			"prevalence", "age", "sex", "percent" };
	/* W32 */
	static final String[] incidencesStrings = { "incidences", "incidence",
			"age", "sex", "value" };
	/* W33 */
	/* W34Cat */
	static final String[] relRiskForRiskfactorCatStrings = { "rrisksforriskfactor",
			"relativerisk", "age", "sex", "cat", "value" };
	/* W34Con */
	static final String[] relRiskForRiskfactorConStrings = { "rrisksforriskfactor",
			"relativerisk", "age", "sex", "value" };
	/* W34Com */
	/* W35 */
	static final String[] relRiskFromDiseaseStrings = { "rrisksfromdisease",
			"relativerisk", "age", "sex", "value" };
	/* W?? */
	static final String[] dALYWeightsStrings = { "dalyweights",
			"weight", "age", "sex", "percent" };
}
