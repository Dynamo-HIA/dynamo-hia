package nl.rivm.emi.dynamo.data.objects;

/**
 * Explicit Dynamo-SimulationObject because the underlying CZM (aka. SOR) project has 
 * a different Simulation configuration.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.XMLHandlingEntryPoint;
import nl.rivm.emi.dynamo.data.interfaces.IDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.IDynamoSimulationConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.IRelativeRiskConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.IRiskFactorConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.IScenarioConfiguration;
import nl.rivm.emi.dynamo.data.objects.parts.RelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.parts.DiseaseConfigurationData;
import nl.rivm.emi.dynamo.data.objects.parts.HasNewBornsImpl;
import nl.rivm.emi.dynamo.data.objects.parts.MaxAgeImpl;
import nl.rivm.emi.dynamo.data.objects.parts.MinAgeImpl;
import nl.rivm.emi.dynamo.data.objects.parts.NumberOfYearsImpl;
import nl.rivm.emi.dynamo.data.objects.parts.PopFileNameImpl;
import nl.rivm.emi.dynamo.data.objects.parts.RandomSeedImpl;
import nl.rivm.emi.dynamo.data.objects.parts.ResultTypeImpl;
import nl.rivm.emi.dynamo.data.objects.parts.ScenarioConfigurationData;
import nl.rivm.emi.dynamo.data.objects.parts.SimPopSizeImpl;
import nl.rivm.emi.dynamo.data.objects.parts.StartingYearImpl;
import nl.rivm.emi.dynamo.data.objects.parts.TimeStepImpl;
import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DynamoSimObject_NG extends XMLHandlingEntryPoint implements
		IDynamoSimulationConfiguration {

	Log log = LogFactory.getLog(this.getClass().getName());

	static final RootElementNamesEnum rootElement = RootElementNamesEnum.SIMULATION;

	HasNewBornsImpl hasNewBorns;
	StartingYearImpl startingYear;
	NumberOfYearsImpl numberOfYears;
	SimPopSizeImpl simPopSize;
	MinAgeImpl minAge;
	MaxAgeImpl maxAge;
	TimeStepImpl timeStep;
	RandomSeedImpl randomSeed;
	ResultTypeImpl resultType;
	PopFileNameImpl populationFileName;
	IRiskFactorConfiguration riskFactor;

	TypedHashMap<IDiseaseConfiguration> diseases = new TypedHashMap<IDiseaseConfiguration>(new DiseaseConfigurationData());

	TypedHashMap<IRelativeRiskConfiguration> relativeRisks = new TypedHashMap<IRelativeRiskConfiguration>(new RelativeRiskConfigurationData());

	TypedHashMap<IScenarioConfiguration> scenarios = new TypedHashMap<IScenarioConfiguration>(new ScenarioConfigurationData());

	public DynamoSimObject_NG(boolean observable) throws ConfigurationException {
		super(rootElement, observable);
		fillHandlers(observable);
		}

	protected void fillHandlers(boolean observable) throws ConfigurationException {
		hasNewBorns = new HasNewBornsImpl(observable);
		theHandlers.put(hasNewBorns.getXMLElementName(), hasNewBorns);
		startingYear = new StartingYearImpl(observable);
		theHandlers.put(startingYear.getXMLElementName(), startingYear);
		numberOfYears = new NumberOfYearsImpl(observable);
		theHandlers.put(numberOfYears.getXMLElementName(), numberOfYears);
		simPopSize = new SimPopSizeImpl(observable);
		theHandlers.put(simPopSize.getXMLElementName(), simPopSize);
		minAge = new MinAgeImpl(observable);
		theHandlers.put(minAge.getXMLElementName(), minAge);
		maxAge = new MaxAgeImpl(observable);
		theHandlers.put(maxAge.getXMLElementName(), maxAge);
		timeStep = new TimeStepImpl(observable);
		theHandlers.put(timeStep.getXMLElementName(), timeStep);
		randomSeed = new RandomSeedImpl(observable);
		theHandlers.put(randomSeed.getXMLElementName(), randomSeed);
		resultType = new ResultTypeImpl(observable, new String[]{"aap", "noot", "mies", "abstract"});
		theHandlers.put(resultType.getXMLElementName(), resultType);
		populationFileName = new PopFileNameImpl(observable, "d:\\");
		theHandlers.put(populationFileName.getXMLElementName(), populationFileName);
	}

	@Override
	protected void handleRootChildren(List<ConfigurationNode> rootChildren)
			throws ConfigurationException {
		if (rootChildren != null) {
			for (ConfigurationNode rootChild : rootChildren) {
				String childName = rootChild.getName();
				log.debug("Handle rootChild: " + childName);
				IXMLHandlingLayer<?> aHandler = theHandlers.get(childName);
				if ((aHandler != null) && (aHandler instanceof IXMLHandlingLayer)) {
					((IXMLHandlingLayer<?>) aHandler).handle(rootChild);
				} else {
					throw new ConfigurationException(
							"Unhandled rootChild element: " + childName);
				}
			}
		} else {
			for (Entry<String, IXMLHandlingLayer<?>> anEntry : theHandlers.entrySet()) {
				IXMLHandlingLayer<?> aHandler = anEntry.getValue();
				aHandler.setDefault();
			}
		}
	}

	// write
	public void streamEvents(String value, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartDocument();
		writer.add(event);
		event = eventFactory.createStartElement("", "", rootElement
				.getNodeLabel());
		writer.add(event);
		for (Entry<String, IXMLHandlingLayer<?>> anEntry : theHandlers.entrySet()) {
			IXMLHandlingLayer<?> aHandler = anEntry.getValue();
			aHandler.streamEvents(null, writer, eventFactory);
		}
		event = eventFactory.createEndElement("", "", rootElement
				.getNodeLabel());
		writer.add(event);
		event = eventFactory.createEndDocument();
		writer.add(event);
	}

	public boolean isConfigurationOK() {
		for (Entry<String, IXMLHandlingLayer<?>> anEntry : theHandlers.entrySet()) {
			IXMLHandlingLayer<?> aHandler = anEntry.getValue();
			if(!aHandler.isConfigurationOK()){
				return false;
			}
		}
		return true;
	}

	public boolean isHasNewborns() {
		return hasNewBorns.isHasNewborns();
	}

	public void setHasNewborns(boolean newborns) {
		hasNewBorns.setHasNewborns(newborns);
	}

	public Integer getStartingYear() {
		return startingYear.getStartingYear();
	}

	public void setStartingYear(Integer startingYear) {
		this.startingYear.setStartingYear(startingYear);
	}

	public Integer getNumberOfYears() {
		return numberOfYears.getNumberOfYears();
	}

	public void setNumberOfYears(Integer numberOfYears) {
		 this.numberOfYears.setNumberOfYears(numberOfYears);
	}

	public Integer getSimPopSize() {
		return simPopSize.getSimPopSize();
	}

	public void setSimPopSize(Integer populationSize) {
		this.simPopSize.setSimPopSize(populationSize);
	}

	public Integer getMinAge() {
		return minAge.getMinAge();
	}

	public void setMinAge(Integer minAge) {
		this.minAge.setMinAge(minAge);
	}

	public Integer getMaxAge() {
		return maxAge.getMaxAge();
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge.setMaxAge(maxAge);
	}

	public Float getTimeStep() {
		return timeStep.getTimeStep();
	}

	public void setTimeStep(Float timeStep) {
		this.timeStep.setTimeStep(timeStep);
	}

	public Float getRandomSeed() {
		return randomSeed.getRandomSeed();
	}

	public void setRandomSeed(Float randomSeed) {
		this.randomSeed.setRandomSeed(randomSeed);
	}

	public String getResultType() {
		return resultType.getResultType();
	}

	public void setResultType(String resultType) {
		this.resultType.setResultType(resultType);
	}

	public String getPopulationFileName() {
		return populationFileName.getPopFileName();
	}

	public void setPopulationFileName(String populationFileName) {
		this.populationFileName.setPopFileName(populationFileName);
	}

	public TypedHashMap<IDiseaseConfiguration> getDiseases() {
		return diseases;
	}

	public IRiskFactorConfiguration getRiskFactor() {
		return riskFactor;
	}

	public void setRiskFactor(IRiskFactorConfiguration riskFactor) {
		this.riskFactor = riskFactor;
	}



	public void setDiseases(ArrayList<IDiseaseConfiguration> diseases) {
		// TODO Auto-generated method stub
		
	}

	public TypedHashMap<RelativeRiskConfigurationData> getRelativeRisks() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRelativeRisks(
			TypedHashMap<RelativeRiskConfigurationData> relativeRisks) {
		// TODO Auto-generated method stub
		
	}

	public TypedHashMap<ScenarioConfigurationData> getScenarios() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setScenarios(
			TypedHashMap<ScenarioConfigurationData> scenarios) {
		// TODO Auto-generated method stub
		
	}

	public void setDiseases(TypedHashMap<IDiseaseConfiguration> diseases) {
		// TODO Auto-generated method stub
		
	}


}
