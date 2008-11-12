package nl.rivm.emi.dynamo.data.factories.dispatch;

import nl.rivm.emi.dynamo.data.factories.DALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.DiseasePrevalencesFactory;
import nl.rivm.emi.dynamo.data.factories.ExcessMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.IncidencesFactory;
import nl.rivm.emi.dynamo.data.factories.NewBornsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallDALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.PopulationSizeFactory;
import nl.rivm.emi.dynamo.data.factories.PrevalencesProxy;
import nl.rivm.emi.dynamo.data.factories.RRiskForRiskFactorFactory;
import nl.rivm.emi.dynamo.data.factories.RRiskFromDiseaseFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorFactory;
import nl.rivm.emi.dynamo.data.factories.SimulationFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionDriftFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionMatrixFactory;
import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.objects.ObservableObjectMarker;
import nl.rivm.emi.dynamo.data.objects.StandardObjectMarker;

public enum DispatchEntry {
	SIMULATION("simulation", new SimulationFactory()), // TODO Factory, Model.
	POPULATIONSIZE("populationsize", new PopulationSizeFactory()), // TODO Factory, Model.
	OVERALLMORTALITY("overallmortality", new OverallMortalityFactory()), // TODO Factory, Model.
	NEWBORNS("newborns", new NewBornsFactory()), // TODO Factory, Model.
	OVERALLDALYWEIGHTS("overalldalyweights", new OverallDALYWeightsFactory()), // TODO Factory, Model.
	RISKFACTOR("riskfactor", new RiskFactorFactory()), // TODO Factory, Model.
	TRANSITIONMATRIX("transitionmatrix", new TransitionMatrixFactory()), // TODO Factory, Model.
	TRANSITIONDRIFT("transitiondrift", new TransitionDriftFactory()), // TODO Factory, Model.
	PREVALENCES("prevalences", new PrevalencesProxy()), // TODO Factory, Model.
	RELRISKFORDEATH("relriskfordeath", new RelRiskForDeathFactory()), // TODO Factory, Model.
	DISEASEPREVALENCES("diseaseprevalences", new DiseasePrevalencesFactory()), // TODO Factory, Model.
	INCIDENCES("incidences", new IncidencesFactory()),
	EXCESSMORTALITY("excessmortality", new ExcessMortalityFactory()), // TODO Factory, Model.
	RRISKFORRISKFACTOR("rriskforriskfactor", new RRiskForRiskFactorFactory()), // TODO Factory, Model.
	RRISKFROMDISEASE("rriskfromdisease", new RRiskFromDiseaseFactory()), // TODO Factory, Model.
	DALYWEIGHTS("dalyweights", new DALYWeightsFactory()); // TODO Factory, Model.
	
	private final String rootNodeName;
	private final IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> theFactory;

	private DispatchEntry(String rootNodeName, IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> theFactory) {
		this.theFactory = theFactory;
		this.rootNodeName = rootNodeName;
	}

	public String getRootNodeName() {
		return rootNodeName;
	}

	public IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> getTheFactory() {
		return theFactory;
	}
}