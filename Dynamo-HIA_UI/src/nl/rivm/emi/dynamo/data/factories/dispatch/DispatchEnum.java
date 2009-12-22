package nl.rivm.emi.dynamo.data.factories.dispatch;

import nl.rivm.emi.dynamo.data.factories.AlphasFactory;
import nl.rivm.emi.dynamo.data.factories.AlphasOtherMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.AttributableMortalitiesFactory;
import nl.rivm.emi.dynamo.data.factories.BaselineFatalIncidencesFactory;
import nl.rivm.emi.dynamo.data.factories.BaselineIncidencesFactory;
import nl.rivm.emi.dynamo.data.factories.BaselineOtherMortalitiesFactory;
import nl.rivm.emi.dynamo.data.factories.DALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.DiseaseIncidencesFactory;
import nl.rivm.emi.dynamo.data.factories.DiseasePrevalencesFactory;
import nl.rivm.emi.dynamo.data.factories.DummyPlaceholderFactory;
import nl.rivm.emi.dynamo.data.factories.DurationDistributionFactory;
import nl.rivm.emi.dynamo.data.factories.DynamoSimulationFactory;
import nl.rivm.emi.dynamo.data.factories.ExcessMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.NewbornsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallDALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.PopulationSizeFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDisabilityCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDisabilityCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDisabilityContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromOtherDiseaseFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromRiskFactorCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromRiskFactorCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromRiskFactorContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RelativeRisksClusterFactory;
import nl.rivm.emi.dynamo.data.factories.RelativeRisksFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorPrevalencesCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorPrevalencesContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RootLevelFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionDriftFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionDriftNettoFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionMatrixFactory;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.main.base.DataAndFileContainer;

/**
 * @author mondeelr
 * 
 *         Enumeration mapping the relations between the name of a root-element
 *         in a configuration file and the Factory Object to turn it into a
 *         Model Object.
 * 
 *         The instances of the enumeration are put into the DispatchMap to be
 *         able to find them by rootelementname.
 */
public enum DispatchEnum {
	/** Create Object to be edited through W01 */
	SIMULATION(RootElementNamesEnum.SIMULATION.getNodeLabel(),
			new DynamoSimulationFactory(), null),
	/** Create Object to be edited through W11 */
	POPULATIONSIZE(RootElementNamesEnum.POPULATIONSIZE.getNodeLabel(),
			new PopulationSizeFactory(), null),
	/** Create Object to be edited through W12 */
	OVERALLMORTALITY(RootElementNamesEnum.OVERALLMORTALITY.getNodeLabel(),
			new OverallMortalityFactory(), null),
	/** Create Object to be edited through W13 */
	NEWBORNS(RootElementNamesEnum.NEWBORNS.getNodeLabel(),
			new NewbornsFactory(), null),
	/** Create Object to be edited through W14 */
	OVERALLDALYWEIGHTS(RootElementNamesEnum.OVERALLDALYWEIGHTS.getNodeLabel(),
			new OverallDALYWeightsFactory(), null), // TODO
	/** Create Object to be edited through W20Cat */
	RISKFACTOR_CATEGORICAL(RootElementNamesEnum.RISKFACTOR_CATEGORICAL
			.getNodeLabel(), new RiskFactorCategoricalFactory(), null),
	/** Create Object to be edited through W20Con */
	RISKFACTOR_CONTINUOUS(RootElementNamesEnum.RISKFACTOR_CONTINUOUS
			.getNodeLabel(), new RiskFactorContinuousFactory(), null),
	/** Create Object to be edited through W20Cmp */
	RISKFACTOR_COMPOUND(
			RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel(),
			new RiskFactorCompoundFactory(), null),
	/** Create Object to be edited through W21TdMA */
	TRANSITIONDRIFT(RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel(),
			new TransitionDriftFactory(), null), // TODO
	/** Create Object to be edited through W21TdId */
	// TODO
	/** Create Object to be edited through W21TdFp */
	// TODO
	TRANSITIONDRIFT_NETTO(RootElementNamesEnum.TRANSITIONDRIFT_NETTO
			.getNodeLabel(), new TransitionDriftNettoFactory(), null), // 
	/** Create Object to be edited through W21TmMA */

	TRANSITIONMATRIX(RootElementNamesEnum.TRANSITIONMATRIX.getNodeLabel(),
			new TransitionMatrixFactory(), null),
	/** Create Object to be edited through W21TmId */
	TRANSITIONMATRIX_ZERO(RootElementNamesEnum.TRANSITIONMATRIX_ZERO
			.getNodeLabel(), /*
							 * new TransitionMatrixFactory()
							 */
	new DummyPlaceholderFactory(), null), // TODO
	/** Create Object to be edited through W21TmFp */
	TRANSITIONMATRIX_NETTO(RootElementNamesEnum.TRANSITIONMATRIX_NETTO
			.getNodeLabel(), /*
							 * new TransitionMatrixFactory()
							 */
	new DummyPlaceholderFactory(), null), // TODO
	/** Create Object to be edited through W22CatCom */
	RISKFACTORPREVALENCES_CATEGORICAL(
			RootElementNamesEnum.RISKFACTORPREVALENCES_CATEGORICAL
					.getNodeLabel(),
			new RiskFactorPrevalencesCategoricalFactory(), null), // TODO
	/** Create Object to be edited through W22Con */
	RISKFACTORPREVALENCES_CONTINUOUS(
			RootElementNamesEnum.RISKFACTORPREVALENCES_CONTINUOUS
					.getNodeLabel(),
			new RiskFactorPrevalencesContinuousFactory(), null), // TODO
	/** Create Object to be edited through W22ComDur */
	RISKFACTORPREVALENCES_DURATION(
			RootElementNamesEnum.RISKFACTORPREVALENCES_DURATION.getNodeLabel(),
			new DurationDistributionFactory(), null),
	/** Create Object to be edited through W23Cat */
	RELRISKFORDEATH_CATEGORICAL(
			RootElementNamesEnum.RELATIVERISKSFORDEATH_CATEGORICAL
					.getNodeLabel(), new RelRiskForDeathCategoricalFactory(),
			null),
	/** Create Object to be edited through W23Con */
	RELRISKFORDEATH_CONTINUOUS(
			RootElementNamesEnum.RELATIVERISKSFORDEATH_CONTINUOUS
					.getNodeLabel(), new RelRiskForDeathContinuousFactory(),
			null),
	/** Create Object to be edited through W23Cmp */
	RELRISKFORDEATH_COMPOUND(
			RootElementNamesEnum.RELATIVERISKSFORDEATH_COMPOUND.getNodeLabel(),
			new RelRiskForDeathCompoundFactory(), null),
	/** Create Object to be edited through W23Cat */
	RELRISKFORDISABLITY_CATEGORICAL(
			RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CATEGORICAL
					.getNodeLabel(),
			new RelRiskForDisabilityCategoricalFactory(), null),
	/** Create Object to be edited through W23Con */
	RELRISKFORDISABLITY_CONTINUOUS(
			RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CONTINUOUS
					.getNodeLabel(),
			new RelRiskForDisabilityContinuousFactory(), null),
	/** Create Object to be edited through W23Cmp */
	RELRISKFORDISABLITY_COMPOUND(
			RootElementNamesEnum.RELATIVERISKSFORDISABILITY_COMPOUND
					.getNodeLabel(), new RelRiskForDisabilityCompoundFactory(),
			null), // TODO
	DISEASEPREVALENCES(RootElementNamesEnum.DISEASEPREVALENCES.getNodeLabel(),
			new DiseasePrevalencesFactory(), null), // TODO
	/** Create Object to be edited through W32 */
	DISEASEINCIDENCES(RootElementNamesEnum.DISEASEINCIDENCES.getNodeLabel(),
			new DiseaseIncidencesFactory(), null),
	/** Create Object to be edited through W33 */
	EXCESSMORTALITY(XMLTagEntityEnum.EXCESSMORTALITY.getElementName(),
			new ExcessMortalityFactory(), null),
	/** Create Object to be edited through W34Cat */
	RRISKFORRISKFACTOR_CATEGORICAL(
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL
					.getNodeLabel(),
			new RelRiskFromRiskFactorCategoricalFactory(), null),
	/** Create Object to be edited through W34Con */
	RRISKFORRISKFACTOR_CONTINUOUS(
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CONTINUOUS
					.getNodeLabel(),
			new RelRiskFromRiskFactorContinuousFactory(), null),
	/** Create Object to be edited through W34Cmp */
	RRISKFORRISKFACTOR_COMPOUND(
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_COMPOUND
					.getNodeLabel(),
			new RelRiskFromRiskFactorCompoundFactory(), null), // TODO
	/** Create Object to be edited through W35 */
	RRISKFROMDISEASE(RootElementNamesEnum.RELATIVERISKSFROMDISEASE
			.getNodeLabel(), new RelRiskFromOtherDiseaseFactory(), null),
	/** Create Object to be edited through W?? */
	DALYWEIGHTS(RootElementNamesEnum.DALYWEIGHTS.getNodeLabel(),
			new DALYWeightsFactory(), null),
	/* Estimated parameters. */
	ATTRIBUTABLEMORTALITIES(RootElementNamesEnum.ATTRIBUTABLEMORTALITIES
			.getNodeLabel(), new AttributableMortalitiesFactory(), null), //
	BASELINEFATALINCIDENCES(RootElementNamesEnum.BASELINEFATALINCIDENCES
			.getNodeLabel(), new BaselineFatalIncidencesFactory(), null), //
	BASELINEINCIDENCES(RootElementNamesEnum.BASELINEINCIDENCES.getNodeLabel(),
			new BaselineIncidencesFactory(), null), //
	BASELINEOTHERMORTALITIES(RootElementNamesEnum.BASELINEOTHERMORTALITIES
			.getNodeLabel(), new BaselineOtherMortalitiesFactory(), null), //
	RELATIVERISKS(RootElementNamesEnum.RELATIVERISKS.getNodeLabel(),
			new RelativeRisksFactory(), null), //
	RELATIVERISKSCLUSTER(RootElementNamesEnum.RELATIVERISKSCLUSTER
			.getNodeLabel(), new RelativeRisksClusterFactory(), null), //
	RELATIVERISKSFROMRISKFACTOR_CATEGORICAL4P(
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL4P
					.getNodeLabel(),
			new RelRiskFromRiskFactorCategoricalFactory(), null), //
	RELATIVERISKSFROMRISKFACTOR_CONTINUOUS4P(
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CONTINUOUS4P
					.getNodeLabel(),
			new RelRiskFromRiskFactorContinuousFactory(), null), //
	RELATIVERISKS_OTHERMORT_CATEGORICAL(
			RootElementNamesEnum.RELATIVERISKS_OTHERMORT_CATEGORICAL
					.getNodeLabel(),
			new RelRiskFromRiskFactorCategoricalFactory(), null), //
	RELATIVERISKS_OTHERMORT_CONTINUOUS(
			RootElementNamesEnum.RELATIVERISKS_OTHERMORT_CONTINUOUS
					.getNodeLabel(),
			new RelRiskFromRiskFactorContinuousFactory(), null), //
	ALPHAS(RootElementNamesEnum.ALPHAS.getNodeLabel(), new AlphasFactory(),
			null), //
	ALPHASOTHERMORTALITY(RootElementNamesEnum.ALPHASOTHERMORTALITY
			.getNodeLabel(), new AlphasOtherMortalityFactory(), null), //
	RELATIVERISKS_OTHERMORT_BEGIN(
			RootElementNamesEnum.RELATIVERISKS_OTHERMORT_BEGIN.getNodeLabel(),
			new RelRiskFromRiskFactorContinuousFactory(), null), //
	RELATIVERISKS_OTHERMORT_END(
			RootElementNamesEnum.RELATIVERISKS_OTHERMORT_END.getNodeLabel(),
			new RelRiskFromRiskFactorContinuousFactory(), null), //
	;

	/**
	 * Name of the rootelement of the corresponding configurationfile.
	 */
	private final String rootNodeName;
	/**
	 * The factory to use to instantiate the modelobject from the
	 * configurationfile.
	 */
	private final RootLevelFactory theFactory;
	/**
	 * Window to use for editing the modelobject.
	 */
	private final DataAndFileContainer theModalClass;

	/**
	 * @param rootNodeName
	 *            Name of the rootelement of the corresponding
	 *            configurationfile.
	 * @param theFactory
	 *            The factory to use to instantiate the modelobject from the
	 *            configurationfile.
	 * @param modalClass
	 *            Window to use for editing the modelobject.
	 */
	private DispatchEnum(String rootNodeName, RootLevelFactory theFactory,
			DataAndFileContainer modalClass) {
		this.theFactory = theFactory;
		this.rootNodeName = rootNodeName;
		theModalClass = modalClass;
	}

	/**
	 * @return Name of the rootelement of the corresponding configurationfile.
	 */
	public String getRootNodeName() {
		return rootNodeName;
	}

	/**
	 * @return The factory to use to instantiate the modelobject from the
	 *         configurationfile.
	 */
	public RootLevelFactory getTheFactory() {
		return theFactory;
	}

	/**
	 * @return Window to use for editing the modelobject.
	 */
	public DataAndFileContainer getTheModalClass() {
		return theModalClass;
	}
}