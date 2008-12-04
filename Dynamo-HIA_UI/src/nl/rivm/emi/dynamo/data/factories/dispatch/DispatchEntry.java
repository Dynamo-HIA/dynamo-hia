package nl.rivm.emi.dynamo.data.factories.dispatch;

import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.DALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallDALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.PopulationSizeFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromOtherDiseaseFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionMatrixFactory;
import nl.rivm.emi.dynamo.data.factories.notinuse.IncidencesFactory_NonRecurs;
import nl.rivm.emi.dynamo.data.factories.notinuse.PrevalencesProxy;
import nl.rivm.emi.dynamo.data.factories.notinuse.RiskFactorFactory;

public enum DispatchEntry {
	SIMULATION("simulation", new SimulationFactory()), // TODO Factory, Model.
	POPULATIONSIZE("populationsize", new PopulationSizeFactory()), // TODO Model.
	OVERALLMORTALITY("overallmortality", new OverallMortalityFactory()), // TODO Model.
	NEWBORNS("newborns", new NewBornsFactory()), // TODO Factory, Model.
	OVERALLDALYWEIGHTS("overalldalyweights", new OverallDALYWeightsFactory()), // TODO Model.
	RISKFACTOR("riskfactor", new RiskFactorFactory()), // TODO Factory, Model.
	TRANSITIONMATRIX("transitionmatrix", new TransitionMatrixFactory()), // TODO Model.
	TRANSITIONDRIFT("transitiondrift", new TransitionDriftFactory()), // TODO Factory, Model.
	PREVALENCES("prevalences", new PrevalencesProxy()), // TODO Factory, Model.
	RELRISKFORDEATH("relriskfordeath", new RelRiskForDeathFactory()), // TODO Factory, Model.
	DISEASEPREVALENCES("diseaseprevalences", new DiseasePrevalencesFactory()), // TODO Factory, Model.
	INCIDENCES("incidences", new IncidencesFactory_NonRecurs()),
	EXCESSMORTALITY("excessmortality", new ExcessMortalityFactory()), // TODO Factory, Model.
	RRISKFORRISKFACTOR("rriskforriskfactor", new RRiskForRiskFactorFactory()), // TODO Factory, Model.
	RRISKFROMDISEASE("rriskfromdisease", new RelRiskFromOtherDiseaseFactory()), // TODO Model.
	DALYWEIGHTS("dalyweights", new DALYWeightsFactory()); // TODO Model.
	
	private final String rootNodeName;
	private final AgnosticFactory  theFactory;

	private DispatchEntry(String rootNodeName, AgnosticFactory theFactory) {
		this.theFactory = theFactory;
		this.rootNodeName = rootNodeName;
	}

	public String getRootNodeName() {
		return rootNodeName;
	}

	public AgnosticFactory  getTheFactory() {
		return theFactory;
	}
}