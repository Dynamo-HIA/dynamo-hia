package nl.rivm.emi.dynamo.data.factories.dispatch;

/**
 * Enumeration mapping the relations between the name of a root-element in a configuration 
 * file and the Factory Object to turn it into a Configuration Model Object.
 */

import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.DALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.DiseaseIncidencesFactory;
import nl.rivm.emi.dynamo.data.factories.DiseasePrevalencesFactory;
import nl.rivm.emi.dynamo.data.factories.DummyPlaceholderFactory;
import nl.rivm.emi.dynamo.data.factories.OverallDALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.PopulationSizeFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForRiskFactorCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForRiskFactorContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromOtherDiseaseFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorPrevalencesDurationFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionMatrixFactory;

public enum DispatchEntry {
	/* W01 */
	SIMULATION("simulation", /* new SimulationFactory() */
	new DummyPlaceholderFactory()), // TODO
	/* W11 */
	POPULATIONSIZE("populationsize", new PopulationSizeFactory()),
	/* W12 */
	OVERALLMORTALITY("overallmortality", new OverallMortalityFactory()),
	/* W13 */
	NEWBORNS("newborns", /* new NewBornsFactory() */
	new DummyPlaceholderFactory()), // TODO
	/* W14 */
	OVERALLDALYWEIGHTS("overalldalyweights", new OverallDALYWeightsFactory()), // TODO
	/* W20Cat */
	/* W20Con */
	/* W20Cmp */
	RISKFACTOR("riskfactor", /* new RiskFactorFactory() */
	new DummyPlaceholderFactory()), // TODO
	/* W21TmId */
	TRANSITIONMATRIX_ZERO("transitionmatrix_zero", /*
													 * new
													 * TransitionMatrixFactory()
													 */
	new DummyPlaceholderFactory()), // TODO
	/* W21TmFp */
	TRANSITIONMATRIX_NETTO("transitionmatrix_netto", /*
													 * new
													 * TransitionMatrixFactory()
													 */
	new DummyPlaceholderFactory()), // TODO
	/* W21TmMA */
	TRANSITIONMATRIX("transitionmatrix", new TransitionMatrixFactory()),
	/* W21TdId */
	/* W21TdFp */
	/* W21TdMA */
	TRANSITIONDRIFT("transitiondrift", /* new TransitionDriftFactory() */
	new DummyPlaceholderFactory()), // TODO
	/* W22CatCom */

	RISKFACTORPREVALENCES_CATEGORICAL("riskfactorprevalences_categorical",
			new DummyPlaceholderFactory()), // TODO
	/* W22Con */
	RISKFACTORPREVALENCES_CONTINUOUS("riskfactorprevalences_continuous",
			new DummyPlaceholderFactory()), // TODO
	/* W22ComDur */
	RISKFACTORPREVALENCES_DURATION("riskfactorprevalences_duration",
			new RiskFactorPrevalencesDurationFactory()),
	/* W23Cat */
	RELRISKFORDEATH_CATEGORICAL("relriskfordeath_categorical",
			new RelRiskForDeathCategoricalFactory()),
	/* W23Con */
	RELRISKFORDEATH_CONTINUOUS("relriskfordeath_continuous",
			new RelRiskForDeathContinuousFactory()),
	/* W23Cmp */
	RELRISKFORDEATH_COMPOUND("relriskfordeath_categorical",
			new DummyPlaceholderFactory()), // TODO
	DISEASEPREVALENCES("diseaseprevalences", new DiseasePrevalencesFactory()), // TODO
	/* W32 */
	DISEASEINCIDENCES("diseaseincidences", new DiseaseIncidencesFactory()),
	/* W33 */
	EXCESSMORTALITY("excessmortality", new DummyPlaceholderFactory()), // TODO
	/* W34Cat */
	RRISKFORRISKFACTOR_CATEGORICAL("rriskforriskfactor_categorical",
			new RelRiskForRiskFactorCategoricalFactory()),
	/* W34Con */
	RRISKFORRISKFACTOR_CONTINUOUS("rriskforriskfactor_continuous",
			new RelRiskForRiskFactorContinuousFactory()),
	/* W34Cmp */
	RRISKFORRISKFACTOR_COMPOUND("rriskforriskfactor_compound",
			new DummyPlaceholderFactory()), // TODO
	/* W35 */
	RRISKFROMDISEASE("rriskfromdisease", new RelRiskFromOtherDiseaseFactory()),
	/* W?? */
	DALYWEIGHTS("dalyweights", new DALYWeightsFactory());

	private final String rootNodeName;
	private final AgnosticFactory theFactory;

	private DispatchEntry(String rootNodeName, AgnosticFactory theFactory) {
		this.theFactory = theFactory;
		this.rootNodeName = rootNodeName;
	}

	public String getRootNodeName() {
		return rootNodeName;
	}

	public AgnosticFactory getTheFactory() {
		return theFactory;
	}
}