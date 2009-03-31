package nl.rivm.emi.dynamo.estimation;

import java.io.File;
import java.io.IOException;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Hendriek The class manufactures all the steering files needed by the
 *         CDM ("SOR") program Together with the initial population and the
 *         population of newborns that are manufactured by the class
 *         "InitialPopulationFactory" they form the input needed by the CDM
 *         ("SOR") model
 * 
 */
public class SimulationConfigurationFactory {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String charFileName = null;
	private String simFileName = null;
	private String popFileName = null;
	private String directoryName = null;
	private String newbornsFileName = null;
	private int nRules;
	private String simulationName;
	
	/* incidenceDebug= true means that the update rules will also store incidence information */
	private boolean incidenceDebug=true;
	/*
	 * temporary some info needed for proper working of the class this
	 * information should be supplied by the user interface in the final version
	 */
	// private boolean isNullTransition = true;

	/**
	 * this method writes the characteristicsConfigurationFile that is one of
	 * the inputs of the SOR CDM model
	 * 
	 * @param simName
	 *            the name of the simulation
	 * 
	 */

	public SimulationConfigurationFactory(String simName) {

		directoryName = BaseDirectory.getBaseDir() + File.separator  
					+ "Simulations" + File.separator + simName;
		charFileName = directoryName + File.separator + "modelconfiguration" + File.separator
					+ "charconfig.XML";
		simFileName = directoryName + File.separator + "modelconfiguration" + File.separator + "simulation"; // no
		// .xml
		// because
		// different
		// variants
		// are
		// needed
		popFileName = directoryName + File.separator + "modelconfiguration" + File.separator + "population";// no
		// .xml
		// because
		// different
		// variants
		// are
		// needed
		newbornsFileName = directoryName + File.separator + "modelconfiguration"
					+ File.separator+ "newborns.XML";
		;
	}

	// public void manufactureSimulationConfigurationFile(ModelParameters
	// parameters, DynamoConfigurationData configData) throws
	// TransformerException, ParserConfigurationException
	// temporary

	/**
	 * this method writes the ConfigurationFiles for the update rules that are
	 * the inputs of the SOR CDM model
	 * 
	 * @param parameters
	 *            object with the model parameters
	 * @throws CDMConfigurationException
	 * @throws DynamoConfigurationException
	 * 
	 */

	public void manufactureUpdateRuleConfigurationFiles(
			ModelParameters parameters, ScenarioInfo scenInfo)
			throws DynamoConfigurationException {

		/*
		 * 
		 * write rule for riskfactor update
		 */

		String fileName = null;
		int ruleNumber = 3;

		/**
		 * implemented xml file for categorical risk factor: <?xml version="1.0"
		 * encoding="UTF-8"?> <updateRuleConfiguration> <charID>3</charID>
		 * <refValContinuousVariable>0</refValContinuousVariable> <nCat>4</nCat>
		 * <durationClass>2</durationClass> <nullTransition>1</nullTransition>
		 * <durationClass>2</durationClass> <transitionFile>c:/hendriek
		 * /java/workspace/dynamo/dynamodata/transdata.xml</transitionFile>
		 * <randomSeed>1234</randomSeed> </updateRuleConfiguration>
		 */

		// TODO : add rule class name to configuration files and check for this
		// in the rule to prevent using wrong configuration file with wrong rule
		// not essential
		int riskType = parameters.getRiskType();

		for (int scen = 0; scen <= scenInfo.getNScenarios(); scen++) {

			String ConfigXMLfileName = null;

			/*
			 * 
			 * write rule for categorical risk factor
			 */
			if (riskType == 1 || riskType == 3) {

				if (scen == 0) {
					ConfigXMLfileName = directoryName + File.separator 
							+ "modelconfiguration" + File.separator
							+ "rule" + ((Integer) ruleNumber).toString()
							+ ".xml";

				} else {

					ConfigXMLfileName = directoryName + File.separator 
							+ "modelconfiguration" + File.separator
							+ "rule" + ((Integer) ruleNumber).toString()
							+ "_scen_" + scen + ".xml";
				}

				Document document = newDocument(fileName);

				Element rootElement = document
						.createElement("updateRuleConfiguration");
				writeFinalElementToDom(rootElement, "charID",
						((Integer) ruleNumber).toString());

				writeFinalElementToDom(rootElement, "nCat",
						((Integer) parameters.getPrevRisk()[0][0].length)
								.toString());
				writeFinalElementToDom(rootElement, "durationClass",
						((Integer) parameters.getDurationClass()).toString());
				/*
				 * NB : if zerotransitions, then write nullTransition=1
				 */
				if (scen == 0) {
					if (!parameters.isZeroTransition()) {
						writeFinalElementToDom(rootElement, "nullTransition",
								"0");
						writeFinalElementToDom(rootElement, "transitionFile",
								null);
					} else {
						fileName = directoryName + File.separator
								+ "parameters" + File.separator 
								+ "transitionrates.xml";
						writeFinalElementToDom(rootElement, "nullTransition",
								"1");
						writeFinalElementToDom(rootElement, "transitionFile",
								fileName);
						writeThreeDimArray(parameters.getTransitionMatrix(),
								"transitionMatrix", "transitionRates", fileName);
					}
				} else {
					if (scenInfo.isZeroTransition(scen - 1)
							|| (!scenInfo.getTransitionType(scen - 1) && parameters
									.isZeroTransition())) {
						writeFinalElementToDom(rootElement, "nullTransition",
								"1");
						writeFinalElementToDom(rootElement, "transitionFile",
								null);
					}

					else

					{
						fileName = directoryName + File.separator
								+ "parameters" + File.separator
								+ "transitionrates_scen_" + scen
								+ ".xml";
						writeFinalElementToDom(rootElement, "nullTransition",
								"0");
						writeFinalElementToDom(rootElement, "transitionFile",
								fileName);
						if (scenInfo.getTransitionType(scen - 1))
							writeThreeDimArray(scenInfo
									.getTransitionMatrix(scen - 1),
									"transitionMatrix", "transitionRates",
									fileName);

						else
							writeThreeDimArray(
									parameters.getTransitionMatrix(),
									"transitionMatrix", "transitionRates",
									fileName);
						;
					}
				}

				// random seeds meegeven: dat gaat nu per persoon dus niet
				// meer nodig
				writeFinalElementToDom(rootElement, "randomSeed",
						((Integer) scenInfo.getRandomSeed()).toString());
				document.appendChild(rootElement);
				writeDomToXML(ConfigXMLfileName, document);

			} // end if for type is 1 or 3

			// TODO testing for duration
			// TODO testing continuous risk factor
			/*
			 * 
			 * write rule for continuous risk factor
			 */
			else {
				if (scen == 0) {
					fileName = directoryName + File.separator
							+ "parameters" + File.separator 
							+ "transitiondrift.xml";
					ConfigXMLfileName = directoryName + File.separator 
							+ "modelconfiguration" + File.separator
							+ "rule" + ((Integer) ruleNumber).toString()
							+ ".xml";

				} else {

					ConfigXMLfileName = directoryName + File.separator 
							+ "modelconfiguration" + File.separator
							+ "rule" + ((Integer) ruleNumber).toString()
							+ "_scen_" + scen + ".xml";
				}

				Document document = newDocument("rule3");

				Element rootElement = document
						.createElement("updateRuleConfiguration");
				writeFinalElementToDom(rootElement, "charID",
						((Integer) ruleNumber).toString());

				writeFinalElementToDom(rootElement, "refValContinuousVariable",
						((Float) parameters.getRefClassCont()).toString());
				if (parameters.getRiskType() == 2) {
					if (parameters.getRiskTypeDistribution() == "Normal"
							|| parameters.getRiskTypeDistribution() == "normal"
							|| parameters.getRiskTypeDistribution() == "NORMAL")

						writeFinalElementToDom(rootElement, "DistributionType",
								"normal");
					else if (parameters.getRiskTypeDistribution() == "LogNormal"
							|| parameters.getRiskTypeDistribution() == "lognormal"
							|| parameters.getRiskTypeDistribution() == "logNormal"
							|| parameters.getRiskTypeDistribution() == "LOGNORMAL")
						writeFinalElementToDom(rootElement, "DistributionType",
								"lognormal");
					else
						throw new DynamoConfigurationException(
								"unknown distribution type for continuous risk factor"
										+ parameters.getRiskTypeDistribution());
				}

				if (scen == 0) {
					/* for some reason nullTransition=0 for a nulltransition */
					if (!parameters.isZeroTransition()) {
						writeFinalElementToDom(rootElement, "nullTransition",
								"0");
						writeFinalElementToDom(rootElement,
								"meanDriftFileName", null);
					} else {
						writeFinalElementToDom(rootElement, "nullTransition",
								"1");
						fileName = directoryName + File.separator
								+ "parameters" + File.separator
								+ "meanDriftRiskFactor.xml";
						writeFinalElementToDom(rootElement,
								"meanDriftFileName", fileName);
						writeOneDimArray(parameters.getMeanDrift(),
								"meandrift", "meandrift", fileName);
						fileName = directoryName + File.separator
								+ "parameters" + File.separator
								+ "stdDriftRiskFactor.xml";
						writeFinalElementToDom(rootElement, "stdDriftFileName",
								fileName);
						writeOneDimArray(parameters.getStdDrift(), "stddrift",
								"stddrift", fileName);

						if (parameters.getRiskTypeDistribution()
								.compareToIgnoreCase("LogNormal") == 0) {
							fileName = this.directoryName + File.separator
									+ "parameters" + File.separator 
									+ "offsetDriftRiskFactor.xml";
							writeFinalElementToDom(rootElement,
									"offsetDriftFileName", fileName);
							writeOneDimArray(parameters.getOffsetDrift(),
									"offsetdrift", "offsetdrift", fileName);
							
							fileName = this.directoryName + File.separator
									+ "parameters" + File.separator
									+ "offsetRiskFactor.xml";
							writeFinalElementToDom(rootElement,
									"offsetFileName", fileName);
							writeOneDimArray(parameters.getOffsetRisk(),
									"offset", "offset", fileName);

						}

					}
				} else {
					if (scenInfo.isZeroTransition(scen - 1)
							|| (!scenInfo.getTransitionType(scen - 1) && parameters
									.isZeroTransition())) {
						writeFinalElementToDom(rootElement, "nullTransition",
								"1");
						writeFinalElementToDom(rootElement,
								"meanDriftFileName", null);

					} else {

						fileName = directoryName + File.separator
								+ "parameters" + File.separator
								+ "meanDriftRiskFactor_scen_"
								+ scen + ".xml";
						writeFinalElementToDom(rootElement, "nullTransition",
								"1");
						writeFinalElementToDom(rootElement,
								"meanDriftFileName", fileName);
						if (scenInfo.getTransitionType(scen - 1))
							writeOneDimArray(scenInfo.getMeanDrift(scen - 1),
									"meandrift", "meandrift", fileName);
						else
							writeOneDimArray(parameters.getMeanDrift(),
									"meandrift", "meandrift", fileName);
						
						fileName = directoryName + File.separator
								+ "parameters" + File.separator
								+ "stddriftRiskFactor"
								+ ".xml";
						writeFinalElementToDom(rootElement,
								"stddriftFileName", fileName);
						
							writeOneDimArray(parameters.getStdDrift(),
									"stddrift", "stddrift", fileName);
						
						
						if (parameters.getRiskTypeDistribution()
								.compareToIgnoreCase("LogNormal") == 0) {
							
							fileName = directoryName + File.separator
									+ "parameters" + File.separator
									+ "offsetRiskFactor"
									+ ".xml";
							writeFinalElementToDom(rootElement,
									"offsetFileName", fileName);
							writeOneDimArray(parameters.getMeanDrift(),
										"offset", "offset", fileName);
							
							fileName = directoryName + File.separator
									+ "parameters" + File.separator
									+ "offsetDrift"
		  						    + ".xml";
							writeFinalElementToDom(rootElement,
									"offsetDriftFileName", fileName);
							writeOneDimArray(parameters.getMeanDrift(),
										"offsetdrift", "offsetdrift", fileName);
						}
						
						
						
						
					}
				}

				/*
				 * Remark: making changes in the standard deviation in a
				 * deterministic way during simulation is all much more
				 * complicated than can be handled with a single characteristic
				 * as you need to have the population mean availlable, and the
				 * population mean for a particular age / sex combination
				 * changes during simulation so this is not implemented
				 */

				writeFinalElementToDom(rootElement, "durationClass",
						((Integer) parameters.getDurationClass()).toString());

				document.appendChild(rootElement);
				writeDomToXML(ConfigXMLfileName, document);

			}
		}
		ruleNumber = 4;

		/*
		 * write information for duration class
		 */

		if (riskType == 3) {
			/*
			 * the rule only reads charID (for checking purposes only) and
			 * durationClass
			 */
			/* so part of this is redundant */

			String ConfigXMLfileName = directoryName + File.separator
					+ "modelconfiguration" + File.separator
					+ "rule" + ((Integer) ruleNumber).toString() + ".xml";
			Document document = newDocument("rule4");

			Element rootElement = document
					.createElement("updateRuleConfiguration");
			writeFinalElementToDom(rootElement, "charID",
					((Integer) ruleNumber).toString());
			writeFinalElementToDom(rootElement, "nCat", ((Integer) parameters
					.getPrevRisk()[0][0].length).toString());
			writeFinalElementToDom(rootElement, "durationClass",
					((Integer) parameters.getDurationClass()).toString());

			document.appendChild(rootElement);
			writeDomToXML(ConfigXMLfileName, document);

			ruleNumber = 5;
		}

		/*
		 * 
		 * 
		 * write information necessary for updating of health state
		 */

		/*
		 * 
		 * nb: filename can not yet be constructed, so we pass string message
		 * here in stead of file name to the method "newDocument". This method
		 * only uses this string for the error message so it does not matter
		 */
		Document documentForHealthState = newDocument(" for health state update rule ");

		/**
		 */

		Element healthStateRootElement = documentForHealthState
				.createElement("updateRuleConfiguration");

		writeFinalElementToDom(healthStateRootElement, "name", "healthState");
		writeFinalElementToDom(healthStateRootElement, "riskType",
				((Integer) riskType).toString());
		if (this.incidenceDebug)
		writeFinalElementToDom(healthStateRootElement, "storeIncidence",
				((Integer) 1).toString());
		else
			writeFinalElementToDom(healthStateRootElement, "storeIncidence",
					((Integer) 0).toString());
		if (riskType != 2)
			writeFinalElementToDom(healthStateRootElement, "nCat",
					((Integer) parameters.getPrevRisk()[0][0].length)
							.toString());
		else
			writeFinalElementToDom(healthStateRootElement, "nCat", "1");
		writeFinalElementToDom(healthStateRootElement,
				"refValContinuousVariable", ((Float) parameters
						.getRefClassCont()).toString());
		if (riskType == 3)
			writeFinalElementToDom(healthStateRootElement, "durationClass",
					((Integer) parameters.getDurationClass()).toString());

		String ConfigXMLfileName = directoryName + File.separator 
				+ "modelconfiguration" + File.separator
				+ "rule" + ((Integer) ruleNumber).toString() + ".xml";

		writeFinalElementToDom(healthStateRootElement, "charID",
				((Integer) ruleNumber).toString());

		/* write disease structure data */

		writeFinalElementToDom(healthStateRootElement, "nclusters",
				((Integer) parameters.getNCluster()).toString());

		for (int c = 0; c < parameters.getNCluster(); c++) {
			Element clusterElement = documentForHealthState
					.createElement("clusterInformation");
			DiseaseClusterStructure structure = parameters
					.getClusterStructure()[c];
			writeFinalElementToDom(clusterElement, "clusterNumber",
					((Integer) c).toString());
			writeFinalElementToDom(clusterElement, "startsAtDiseaseNumber",
					((Integer) structure.getDiseaseNumber()[0]).toString());
			writeFinalElementToDom(clusterElement, "numberOfDiseasesInCluster",
					((Integer) structure.getNInCluster()).toString());
			if (structure.isWithCuredFraction())
				writeFinalElementToDom(clusterElement, "withCuredFraction", "1");
			else
				writeFinalElementToDom(clusterElement, "withCuredFraction", "0");

			fileName = directoryName + File.separator
					+ "parameters" + File.separator
					+ "relativeRiskDiseaseOnDisease_cluster"
					+ ((Integer) c).toString() + ".xml";
			if (structure.getNInCluster() > 1)
				writeFinalElementToDom(clusterElement,
						"diseaseOnDiseaseRelativeRiskFile", fileName);
			healthStateRootElement.appendChild(clusterElement);

			writeThreeDimArray(extractFromThreeDimArray(parameters
					.getRelRiskDiseaseOnDisease(), c), "relativeRisks",
					"relativeRisk", fileName);

		}
		/* write the other mortality data */

		fileName = directoryName + File.separator 
					+ "parameters" + File.separator
					+ "baselineOtherMort.xml";
		writeFinalElementToDom(healthStateRootElement, "baselineOtherMortFile",
				fileName);
		writeOneDimArray(parameters.getBaselineOtherMortality(),
				"baselineOtherMortalities", "baselineOtherMortality", fileName);
		fileName = directoryName + File.separator 
					+ "parameters" + File.separator
					+ "relativeRisk_OtherMort.xml";

		if (parameters.getRiskType() == 1 || parameters.getRiskType() == 3) {
			writeFinalElementToDom(healthStateRootElement,
					"relativeRiskOtherMortFile", fileName);
			writeTwoDimArray(parameters.getRelRiskOtherMort(), "relativeRisks",
					"relativeRisk", fileName);
		} else {
			writeFinalElementToDom(healthStateRootElement,
					"relativeRiskOtherMortFile", fileName);
			writeOneDimArray(parameters.getRelRiskOtherMortCont(),
					"relativeRisks", "relativeRisk", fileName);
		}
		if (parameters.getRiskType() == 3) {

			/* end RR */
			fileName = directoryName + File.separator
					+ "parameters" + File.separator
					+ "endRelativeRisk_OtherMort.xml";
			writeFinalElementToDom(healthStateRootElement,
					"endRelativeRiskOtherMortFile", fileName);
			writeOneDimArray(parameters.getRelRiskOtherMortEnd(),
					"relativeRisks", "relativeRisk", fileName);

			/* begin RR */
			fileName = directoryName + File.separator
					+ "parameters" + File.separator
					+ "beginRelativeRisk_OtherMort.xml";
			writeFinalElementToDom(healthStateRootElement,
					"beginRelativeRiskOtherMortFile", fileName);
			writeOneDimArray(parameters.getRelRiskOtherMortBegin(),
					"relativeRisks", "relativeRisk", fileName);

			/* alfa */
			fileName = directoryName + File.separator 
					+ "parameters" + File.separator
					+ "alfa_OtherMort.xml";
			writeFinalElementToDom(healthStateRootElement,
					"alfaRelRiskOtherMortFile", fileName);
			writeOneDimArray(parameters.getAlfaOtherMort(), "alfa", "alfa",
					fileName);

		}

		/* now start the writing of disease-specific information */

		Element healthStateDiseaseElement;

		for (int c = 0; c < parameters.getNCluster(); c++) {
			DiseaseClusterStructure structure = parameters
					.getClusterStructure()[c];

			for (int d = 0; d < structure.getNInCluster(); d++) {

				/*
				 * write away information per disease
				 */

				/* first the general info */

				String name = structure.getDiseaseName().get(d);
				healthStateDiseaseElement = documentForHealthState
						.createElement("disease");
				writeFinalElementToDom(healthStateDiseaseElement,
						"clusterNumber", ((Integer) c).toString());
				writeFinalElementToDom(healthStateDiseaseElement,
						"diseaseNumberWithinCluster", ((Integer) d).toString());
				writeFinalElementToDom(healthStateDiseaseElement,
						"diseaseName", name);

				/* baseline (non-fatal) incidence */
				fileName = directoryName + File.separator 
						+ "parameters" + File.separator
						+ "baselineIncidence_"
						+ ((Integer) c).toString() + "_"
						+ ((Integer) d).toString() + "_" + name + ".xml";

				writeFinalElementToDom(healthStateDiseaseElement,
						"baselineIncidenceFile", fileName);
				writeOneDimArray(extractFromOneDimArray(parameters
						.getBaselineIncidence(),
						structure.getDiseaseNumber()[d]), "baselineIncidences",
						"baselineIncidence", fileName);

				/* baseline fatal incidence */
				fileName = directoryName + File.separator
						+ "parameters" + File.separator
						+ "baselineFatalIncidence_"
						+ ((Integer) c).toString() + "_"
						+ ((Integer) d).toString() + "_" + name + ".xml";

				writeFinalElementToDom(healthStateDiseaseElement,
						"baselineFatalIncidenceFile", fileName);
				writeOneDimArray(extractFromOneDimArray(parameters
						.getBaselineFatalIncidence(), structure
						.getDiseaseNumber()[0]), "baselineFatalIncidences",
						"baselineFatalIncidence", fileName);

				/* attributable mortality */
				fileName = directoryName + File.separator 
						+ "parameters" + File.separator
						+ "attributableMort_"
						+ ((Integer) c).toString() + "_"
						+ ((Integer) d).toString() + "_" + name + ".xml";

				writeFinalElementToDom(healthStateDiseaseElement,
						"attributableMortFile", fileName);
				writeOneDimArray(extractFromOneDimArray(parameters
						.getAttributableMortality(), structure
						.getDiseaseNumber()[d]), "attributableMortalities",
						"attributableMortality", fileName);

				/* relative risks of risk factor on the disease */
				if (parameters.getRiskType() != 2) {
					fileName = directoryName + File.separator
							+ "parameters" + File.separator
							+ "relativeRisk_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";

					writeFinalElementToDom(healthStateDiseaseElement,
							"relativeRiskFile", fileName);
					writeTwoDimArray(
							extractFromTwoDimArray(
									parameters.getRelRiskClass(), structure
											.getDiseaseNumber()[d]),
							"relativeRisks", "relativeRisk", fileName);

				} else {
					fileName = directoryName + File.separator
							+ "parameters" + File.separator
							+ "relativeRisk_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";
					writeFinalElementToDom(healthStateDiseaseElement,
							"relativeRiskFile", fileName);
					writeOneDimArray(extractFromOneDimArray(parameters
							.getRelRiskContinue(),
							structure.getDiseaseNumber()[0]), "relativeRisks",
							"relativeRisk", fileName);

				}
				/*
				 * write rr's for duration
				 */
				if (parameters.getRiskType() == 3) {
					fileName = directoryName + File.separator
							+ "parameters" + File.separator
							+ "relativeRisk_end_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";

					writeFinalElementToDom(healthStateDiseaseElement,
							"endRelativeRiskFile", fileName);
					writeOneDimArray(extractFromOneDimArray(parameters
							.getRelRiskDuurEnd(),
							structure.getDiseaseNumber()[0]), "relativeRisks",
							"relativeRisk", fileName);

					fileName = directoryName + File.separator
							+ "parameters" + File.separator
							+ "relativeRisk_begin_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";

					writeFinalElementToDom(healthStateDiseaseElement,
							"beginRelativeRiskFile", fileName);
					writeOneDimArray(extractFromOneDimArray(parameters
							.getRelRiskDuurBegin(), structure
							.getDiseaseNumber()[0]), "relativeRisks",
							"relativeRisk", fileName);
					fileName = directoryName + File.separator
							+ "parameters" + File.separator + "alfa_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";

					writeFinalElementToDom(healthStateDiseaseElement,
							"alfaFile", fileName);
					writeOneDimArray(extractFromOneDimArray(parameters
							.getAlfaDuur(), structure.getDiseaseNumber()[0]),
							"alfa", "alfa", fileName);

				}

				healthStateRootElement.appendChild(healthStateDiseaseElement);

			} // end loop over diseases for clusters with multiple diseases

		} // end loop over clusters

		/* now write the configuration for health State rule to xml */
		documentForHealthState.appendChild(healthStateRootElement);
		writeDomToXML(ConfigXMLfileName, documentForHealthState);

	}

	/**
	 * this method writes the ConfigurationFile that is one of the inputs of the
	 * SOR CDM model
	 * 
	 * @param parameters
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 * @throws CDMConfigurationException
	 */
	public void manufactureSimulationConfigurationFile(
			ModelParameters parameters, ScenarioInfo scenInfo)
			throws DynamoConfigurationException {
		// temporary name for testing

		/* set general info for simulation */

		String fileName = simFileName + ".xml";

		for (int scen = 0; scen <= scenInfo.getNScenarios(); scen++) {
			if (scen > 0)
				fileName = simFileName + "_scen_" + scen + ".xml";
			Document document = newDocument(fileName);

			Element rootElement = document.createElement("sim");
			writeFinalElementToDom(rootElement, "lb", simulationName);
			/*
			 * an alternative would be to use scenInfo.getStepSize()
			 */
			writeFinalElementToDom(rootElement, "timestep", "1");
			writeFinalElementToDom(rootElement, "runmode", "longitudinal");
			/* this is not implemented yet */
			writeFinalElementToDom(rootElement, "stepsbetweensaves", "1");
			writeFinalElementToDom(rootElement, "stepsinrun",
					((Integer) scenInfo.getYearsInRun()).toString());
			writeFinalElementToDom(rootElement, "stoppingcondition", null);
			if (scen > 0 && scenInfo.getInitialPrevalenceType()[scen - 1])
				writeFinalElementToDom(rootElement, "pop", popFileName
						+ "_scen_" + scen + ".xml");
			else
				writeFinalElementToDom(rootElement, "pop", popFileName + ".xml");
			if (scenInfo.isWithNewBorns()) {
				if (scen > 0 && scenInfo.getInitialPrevalenceType()[scen - 1])
					writeFinalElementToDom(rootElement, "pop", newbornsFileName
							+ "_scen_" + scen + ".xml");
				else
					writeFinalElementToDom(rootElement, "pop", newbornsFileName
							+ ".xml");
			}
			int riskType = parameters.getRiskType();
			// age+sex+riskfactor+diseasestate
			int nRules = 4;
			if (riskType == 3)
				nRules++;

			setNRules(nRules);

			// TODO get this from parameter estimation ???? what did I mean by
			// that???

			Element updateRuleElement = document.createElement("updaterules");

			for (int charID = 1; charID <= getNRules(); charID++) {
				Element rule = document.createElement("updaterule");
				updateRuleElement.appendChild(rule);
				String ruleName = null;
				switch (charID) {
				case 1:
					ruleName = "nl.rivm.emi.cdm.rules.update.dynamo.AgeOneToOneUpdateRule";
					break;
				case 2:
					ruleName = "nl.rivm.emi.cdm.rules.update.dynamo.SexOneToOneUpdateRule";
					break;
				case 3:
					if (riskType != 2)
						ruleName = "nl.rivm.emi.cdm.rules.update.dynamo.CategoricalRiskFactorMultiToOneUpdateRule";
					else
						ruleName = "nl.rivm.emi.cdm.rules.update.dynamo.ContinuousRiskFactorMultiToOneUpdateRule";

					break;
				case 4:
					if (riskType == 3) {
						ruleName = "nl.rivm.emi.cdm.rules.update.dynamo.RiskFactorDurationMultiToOneUpdateRule";
						break;
					}
				default:

					if (riskType == 1)
						ruleName = "nl.rivm.emi.cdm.rules.update.dynamo.HealthStateCatManyToManyUpdateRule";
					else if (riskType == 2)
						ruleName = "nl.rivm.emi.cdm.rules.update.dynamo.HealthStateContManyToManyUpdateRule";
					else if (riskType == 3)
						ruleName = "nl.rivm.emi.cdm.rules.update.dynamo.HealthStateDurationManyToManyUpdateRule";

				}

				writeFinalElementToDom(rule, "characteristicid",
						((Integer) charID).toString());
				writeFinalElementToDom(rule, "classname", ruleName);
				String ConfigXMLfileName = null;
				if (scen == 0 || charID != 3)
					ConfigXMLfileName = directoryName + File.separator 
							+ "modelconfiguration" + File.separator
							+ "rule" + ((Integer) charID).toString() + ".xml";
				else
					ConfigXMLfileName = directoryName + File.separator 
							+ "modelconfiguration" + File.separator
							+ "rule" + ((Integer) charID).toString()
							+ "_scen_" + scen + ".xml";
				if (charID > 2)
					writeFinalElementToDom(rule, "configurationfile",
							ConfigXMLfileName);

			}
			;
			rootElement.appendChild(updateRuleElement);

			document.appendChild(rootElement);

			writeDomToXML(fileName, document);

		}

	}

	/**
	 * * this method writes the configurationFile that is one of the inputs of
	 * the SOR CDM model
	 * 
	 * @param parameters
	 *            : the object containing the modelparameters
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 * @throws CDMConfigurationException
	 */
	public void manufactureCharacteristicsConfigurationFile(
			ModelParameters parameters /* , DynamoConfigurationData config */)
			throws DynamoConfigurationException {

		String fileName = charFileName;

		Document document = newDocument(fileName);

		Element rootElement = document.createElement("characteristics");
		int firstDiseaseNo = 4;

		/* write age info */
		Element charElement = document.createElement("ch");
		rootElement.appendChild(charElement);
		writeFinalElementToDom(charElement, "id", "1");
		writeFinalElementToDom(charElement, "lb", "age");
		writeFinalElementToDom(charElement, "type", "numericalcontinuous");

		/* write sex info */
		charElement = document.createElement("ch");
		rootElement.appendChild(charElement);
		writeFinalElementToDom(charElement, "id", "2");
		writeFinalElementToDom(charElement, "lb", "sex");
		writeFinalElementToDom(charElement, "type", "categorical");
		Element element = document.createElement("possiblevalues");
		charElement.appendChild(element);
		writeFinalElementToDom(element, "vl", "0");
		writeFinalElementToDom(element, "vl", "1");

		/* write risk factor info */
		charElement = document.createElement("ch");
		rootElement.appendChild(charElement);
		writeFinalElementToDom(charElement, "id", "3");
		writeFinalElementToDom(charElement, "lb", "risk factor");

		if (parameters.getRiskType() == 1 || parameters.getRiskType() == 3)
			writeFinalElementToDom(charElement, "type", "categorical");
		else
			writeFinalElementToDom(charElement, "type", "numericalcontinuous");

		if (parameters.getRiskType() == 1 || parameters.getRiskType() == 3) {
			element = document.createElement("possiblevalues");
			charElement.appendChild(element);
			for (int r = 0; r < parameters.getPrevRisk()[0][0].length; r++) {
				writeFinalElementToDom(element, "vl", ((Integer) r).toString());
			}
		}

		if (parameters.getRiskType() == 3) {
			firstDiseaseNo++;
			charElement = document.createElement("ch");
			rootElement.appendChild(charElement);

			writeFinalElementToDom(charElement, "id", "4");
			writeFinalElementToDom(charElement, "lb", "duration");
			writeFinalElementToDom(charElement, "type", "numericalcontinuous");

		}

		int index = firstDiseaseNo;

		charElement = document.createElement("ch");
		rootElement.appendChild(charElement);

		writeFinalElementToDom(charElement, "id", ((Integer) index).toString());

		writeFinalElementToDom(charElement, "lb", "healthState");
		writeFinalElementToDom(charElement, "type", "compound");
		int numberOfElements = 1;
		int ndiseases=0;

		for (int c = 0; c < parameters.getNCluster(); c++) {
			DiseaseClusterStructure structure = parameters
					.getClusterStructure()[c];
			ndiseases+=structure.getNInCluster();
			if (structure.getNInCluster() == 1)
				numberOfElements++;
			else if (structure.isWithCuredFraction())
				numberOfElements += 2;
			else
				numberOfElements += Math.pow(2, structure.getNInCluster()) - 1;
		}
		if (!this.incidenceDebug)
		writeFinalElementToDom(charElement, "numberofelements",
				((Integer) numberOfElements).toString());

		else writeFinalElementToDom(charElement, "numberofelements",
				((Integer) (numberOfElements+ndiseases)).toString());
		
		/* write to document */

		document.appendChild(rootElement);
		/* write document to xml-file */
		writeDomToXML(fileName, document);
	}

	/**
	 * this method write the lowest node element to the dom document
	 * 
	 * @param document
	 *            : document to which the element should be attached
	 * @param parent
	 *            : parent node
	 * @param tag
	 *            : tag of the element
	 * @param content
	 *            : content of the element
	 */
	private void writeFinalElementToDom(Element parent, String tag,
			String content) {
		Element element;
		element = parent.getOwnerDocument().createElement(tag);
		if (content != null)
			element.setTextContent(content);
		parent.appendChild(element);
	}

	private void writeOneDimArray(float[][] arrayToWrite, String globalTag,
			String tag, String fileName) throws DynamoConfigurationException {

		int dim2 = arrayToWrite[0].length;
		int dim1 = arrayToWrite.length;
		if (dim2 != 2 || dim1 != 96)
			log.fatal("array size not equal to 96:2, but " + dim1 + ":" + dim2);

		Document document = newDocument(fileName);
		Element rootElement = document.createElement(globalTag);

		for (int a = 0; a < dim1; a++)
			for (int g = 0; g < dim2; g++) {
				Element element = document.createElement(tag);
				rootElement.appendChild(element);
				writeFinalElementToDom(element, "age", ((Integer) a).toString());
				writeFinalElementToDom(element, "sex", ((Integer) g).toString());
				writeFinalElementToDom(element, "value",
						((Float) arrayToWrite[a][g]).toString());
			}
		document.appendChild(rootElement);
		/* write document to xml-file */
		writeDomToXML(fileName, document);
	}

	/**
	 * the method takes a three dimensional array and extracts the two
	 * dimensional array for which the third dimension is equal to d
	 * 
	 * @param inArray
	 * @param d
	 *            : number of the third dimension to be extracted
	 * @return
	 */

	private float[][] extractFromOneDimArray(float[][][] inArray, int d) {
		int dim2 = inArray[0].length;
		int dim1 = inArray.length;
		if (dim2 != 2 || dim1 != 96)
			log.fatal("array size not equal to 96:2, but " + dim1 + ":" + dim2);
		float[][] newArray = new float[dim1][dim2];

		for (int a = 0; a < dim1; a++)
			for (int g = 0; g < dim2; g++) {
				newArray[a][g] = inArray[a][g][d];
			}
		return newArray;
	}

	/**
	 * the method takes a five dimensional array and extracts the four
	 * dimensional array for which the <b> third </b> dimension is equal to c
	 * 
	 * @param inArray
	 * @param c
	 *            : number of the third dimension to be extracted
	 * @return
	 */
	private float[][][][] extractFromThreeDimArray(float[][][][][] inArray,
			int c) {
		int dim1 = inArray.length;
		int dim2 = inArray[0].length;
		int dim3 = inArray[0][0].length;
		if (dim3 < c) {
			log.fatal("disease on disease relative risk matrix for cluster "
					+ c + " does not exist but"
					+ "the program tries to write it ");
			throw new RuntimeErrorException(null,
					"disease on disease relative risk matrix for cluster " + c
							+ " does not exist but"
							+ "the program tries to write it ");
		}
		int dim4 = inArray[0][0][c].length;
		int dim5 = inArray[0][0][c][0].length;
		if (dim2 != 2 || dim1 != 96)
			log.fatal("array size not equal to 96:2, but " + dim1 + ":" + dim2);
		if (dim4 != dim5)
			log.fatal(" matrix of relative risks on disease is not square but "
					+ dim4 + ":" + dim5);
		float[][][][] newArray = new float[dim1][dim2][dim4][dim5];

		for (int a = 0; a < dim1; a++)
			for (int g = 0; g < dim2; g++)
				for (int f = 0; f < dim4; f++)
					for (int t = 0; t < dim5; t++) {
						newArray[a][g][f][t] = inArray[a][g][c][f][t];
					}
		return newArray;
	}

	/**
	 * the method takes a four dimensional array and extracts the three
	 * dimensional array for which the <b> third </b> dimension is equal to c
	 * 
	 * @param inArray
	 * @param d
	 *            : number of the third dimension to be extracted
	 * @return
	 */
	private float[][][] extractFromTwoDimArray(float[][][][] inArray, int d) {
		int dim1 = inArray.length;
		int dim2 = inArray[0].length;
		int dim3 = inArray[0][0].length;
		if (dim2 != 2 || dim1 != 96)
			log.fatal("array size not equal to 96:2, but " + dim1 + ":" + dim2);
		float[][][] newArray = new float[dim1][dim2][dim3];

		for (int a = 0; a < dim1; a++)
			for (int g = 0; g < dim2; g++)
				for (int c = 0; c < dim3; c++) {
					newArray[a][g][c] = inArray[a][g][c][d];
				}
		return newArray;
	}

	private void writeTwoDimArray(float[][][] arrayToWrite, String globalTag,
			String tag, String fileName) throws DynamoConfigurationException {

		int dim3 = arrayToWrite[0][0].length;
		int dim2 = arrayToWrite[0].length;
		int dim1 = arrayToWrite.length;
		if (dim1 != 96 || dim2 != 2)
			log.fatal("array size not equal to 96:2, but " + dim1 + ":" + dim2);

		Document document = newDocument(fileName);
		Element rootElement = document.createElement(globalTag);

		for (int a = 0; a < dim1; a++)
			for (int g = 0; g < dim2; g++)
				for (int c = 0; c < dim3; c++) {
					Element element = document.createElement(tag);
					rootElement.appendChild(element);
					writeFinalElementToDom(element, "age", ((Integer) a)
							.toString());
					writeFinalElementToDom(element, "sex", ((Integer) g)
							.toString());
					writeFinalElementToDom(element, "cat", ((Integer) c)
							.toString());
					writeFinalElementToDom(element, "value",
							((Float) arrayToWrite[a][g][c]).toString());
				}
		document.appendChild(rootElement);
		/* write document to xml-file */
		writeDomToXML(fileName, document);

	}

	private void writeThreeDimArray(float[][][][] arrayToWrite,
			String globalTag, String tag, String fileName)
			throws DynamoConfigurationException {

		int dim1 = arrayToWrite.length;
		int dim2 = arrayToWrite[0].length;
		int dim3 = arrayToWrite[0][0].length;
		int dim4 = arrayToWrite[0][0][0].length;
		if (dim1 != 96 || dim2 != 2)
			log.fatal("array size not equal to 96:2, but " + dim1 + ":" + dim2);

		Document document = newDocument(fileName);

		Element rootElement = document.createElement(globalTag);

		for (int a = 0; a < dim1; a++)
			for (int g = 0; g < dim2; g++)
				for (int c = 0; c < dim3; c++)
					for (int d = 0; d < dim4; d++) {
						Element element = document.createElement(tag);
						rootElement.appendChild(element);
						writeFinalElementToDom(element, "age", ((Integer) a)
								.toString());
						writeFinalElementToDom(element, "sex", ((Integer) g)
								.toString());
						writeFinalElementToDom(element, "from", ((Integer) c)
								.toString());
						writeFinalElementToDom(element, "to", ((Integer) d)
								.toString());
						writeFinalElementToDom(element, "value",
								((Float) arrayToWrite[a][g][c][d]).toString());

					}
		document.appendChild(rootElement);
		/* write document to xml-file */
		writeDomToXML(fileName, document);

	}

	/**
	 * @param fileName
	 * @param document
	 * @throws CDMConfigurationException
	 * 
	 */
	private void writeDomToXML(String fileName, Document document)
			throws DynamoConfigurationException {
		File XMLfile = new File(fileName);
		String directoryName = XMLfile.getParent();
		File directory = new File(directoryName);
		boolean isDirectory = XMLfile.isDirectory();
		boolean canWrite = XMLfile.canWrite();
		try {
			boolean isNewDirectory = directory.mkdirs();
			boolean isNew = XMLfile.createNewFile();
			if (!isDirectory && (canWrite || isNew)) {
				Source source = new DOMSource(document);
				StreamResult result = new StreamResult(XMLfile);
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.transform(source, result);
			}
		} catch

		(IOException e) {
			log.fatal("File exception when writing " + fileName + " : "
					+ e.getClass().getName() + " message: " + e.getMessage());
			e.printStackTrace();
			throw new DynamoConfigurationException(
					"File exception when writing " + fileName + " : "
							+ e.getClass().getName() + " message: "
							+ e.getMessage());
		} catch (TransformerConfigurationException e) {
			log.fatal("TransformerConfigurationException when writing "
					+ fileName + ": " + e.getClass().getName() + " message: "
					+ e.getMessage());

			e.printStackTrace();
			throw new DynamoConfigurationException(
					"TransformerConfigurationException when writing "
							+ fileName + ": " + e.getClass().getName()
							+ " message: " + e.getMessage());
		} catch (TransformerException e) {
			log.fatal("TransformerException when writing " + fileName + " "
					+ e.getClass().getName() + " message: " + e.getMessage());

			e.printStackTrace();
			throw new DynamoConfigurationException(
					"TransformerException when writing " + fileName + " "
							+ e.getClass().getName() + " message: "
							+ e.getMessage());
		}
	}

	public void setNRules(int nRules) {
		this.nRules = nRules;
	}

	public int getNRules() {
		return nRules;
	}

	/**
	 * @param fileName
	 * @throws CDMConfigurationException
	 */
	protected Document newDocument(String fileName)
			throws DynamoConfigurationException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = (DocumentBuilder) dbfac.newDocumentBuilder();
			Document document = docBuilder.newDocument();
			return document;
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
			log.fatal(e.getMessage());

			throw new DynamoConfigurationException(
					"error building document for parameterfile " + fileName);
		}
	}
}
