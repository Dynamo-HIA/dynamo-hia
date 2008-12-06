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
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.dynamo.datahandling.BaseDirectory;

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

	/*
	 * temporary some info needed for proper working of the class this
	 * information should be supplied by the user interface in the final version
	 */
	private boolean isNullTransition = true;
	private int randomSeed = 1111;
	private Integer stepsInRun = 100;

	/**
	 * this method writes the characteristicsConfigurationFile that is one of
	 * the inputs of the SOR CDM model
	 * 
	 * @param simName
	 *            the name of the simulation
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */

	public SimulationConfigurationFactory(String simName)
			throws CDMConfigurationException {

		directoryName = BaseDirectory.getBaseDir() + "Simulations\\" + simName;
		charFileName = directoryName + "\\modelconfiguration"
				+ "\\charconfig.XML";
		simFileName = directoryName + "\\modelconfiguration"
				+ "\\simulation"; //no .xml because different variants are needed
		popFileName = directoryName + "\\modelconfiguration"
				+ "\\population";//no .xml because different variants are needed
		newbornsFileName = directoryName + "\\modelconfiguration"
				+ "\\newborns.XML";
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
	 * 
	 */

	public void manufactureUpdateRuleConfigurationFiles(
			ModelParameters parameters, ScenarioInfo scenInfo)
			throws CDMConfigurationException {
		// temporary name for testing

		String fileName;
		int ruleNumber = 3;

		/* write rule for categorical risk factor */
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
		// in the rule
		int riskType = parameters.getRiskType();

		for (int scen = 0; scen <= scenInfo.nScenarios; scen++) {
			if (riskType == 1 || riskType == 3){
				

					String ConfigXMLfileName = null;

					if (scen == 0) {
						fileName = directoryName
								+ "\\parameters\\transitionrates.xml";
						ConfigXMLfileName = directoryName
								+ "\\modelconfiguration" + "\\rule"
								+ ((Integer) ruleNumber).toString() + ".xml";
					} else {
						fileName = directoryName
								+ "\\parameters\\transitionrates_scen_" + scen
								+ ".xml";
						ConfigXMLfileName = directoryName
								+ "\\modelconfiguration" + "\\rule"
								+ ((Integer) ruleNumber).toString() + "_scen_"
								+ scen + ".xml";
					}

					Document document = newDocument(fileName);

					Element rootElement = document
							.createElement("updateRuleConfiguration");
					writeFinalElementToDom(rootElement, "charID",
							((Integer) ruleNumber).toString());
					writeFinalElementToDom(rootElement,
							"refValContinuousVariable", ((Float) parameters
									.getRefClassCont()).toString());
					writeFinalElementToDom(rootElement, "nCat",
							((Integer) parameters.getPrevRisk()[0][0].length)
									.toString());
					writeFinalElementToDom(rootElement, "durationClass",
							((Integer) parameters.getDurationClass())
									.toString());
					if (isNullTransition) {
						writeFinalElementToDom(rootElement, "nullTransition",
								"1");
						writeFinalElementToDom(rootElement, "transitionFile",
								null);
					} else {
						writeFinalElementToDom(rootElement, "nullTransition",
								"0");
						writeFinalElementToDom(rootElement, "transitionFile",
								fileName);
						if (scen == 0)
							writeThreeDimArray(
									parameters.getTransitionMatrix(),
									"transitionMatrix", "transitionRates",
									fileName);
						else
							writeThreeDimArray(scenInfo
									.getTransitionMatrix(scen-1),
									"transitionMatrix", "transitionRates",
									fileName);
					}
					// random seeds meegeven: dat gaat nu per persoon dus niet
					// meer nodig
					writeFinalElementToDom(rootElement, "randomSeed",
							((Integer) randomSeed).toString());
					document.appendChild(rootElement);
					writeDomToXML(ConfigXMLfileName, document);
					
			}

			// TODO for categorical risk factor and duration 
		}
		
	   ruleNumber = 4;
	  if ( riskType == 3) ruleNumber=5;
		/*
		 * first make a second document for survival, so this can be filled
		 * simultaneously with that of the diseases
		 */
		/*
		 * nb: filename can not yet be constructed, so we pass string message
		 * here in stead of file name the method new document only uses this
		 * string for the error message so it does not matter
		 */
		Document documentForHealthState = newDocument(" for health state update rule ");
		/**
		 * configuration file for the rule for health state is: <?xml
		 * version="1.0" encoding="UTF-8"?> <updateRuleConfiguration>
		 * <charID>9</charID> <name>HealthState</name> <riskType>1</riskType>
		 * <nCat>4</nCat>
		 * <baselineOtherMortFile>otherMort.xml</baselineOtherMortFile>
		 * <relativeRiskOtherMortFile
		 * >relriskOtherMort.xml</relativeRiskOtherMortFile> <disease>
		 * <baselineIncidenceFile>incidenceDis1.xml</baselineIncidenceFile>
		 * <attributableMortFile>catMortDis1.xml</attributableMortFile>
		 * <relativeRiskFile>relriskDis1.xml</relativeRiskFile> </disease>
		 */
		Element healthStateRootElement = documentForHealthState
				.createElement("updateRuleConfiguration");

		writeFinalElementToDom(healthStateRootElement, "name", "healthState");
		writeFinalElementToDom(healthStateRootElement, "riskType",
				((Integer) riskType).toString());
		writeFinalElementToDom(healthStateRootElement, "nCat",
				((Integer) parameters.getPrevRisk()[0][0].length).toString());
		writeFinalElementToDom(healthStateRootElement,
				"refValContinuousVariable", ((Float) parameters
						.getRefClassCont()).toString());

		String ConfigXMLfileName = directoryName + "\\modelconfiguration"
				+ "\\rule" + ((Integer) ruleNumber).toString() + ".xml";

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
					((Integer) structure.getNinCluster()).toString());
			fileName = directoryName
					+ "\\parameters\\relativeRiskDiseaseOnDisease_cluster"
					+ ((Integer) c).toString() + ".xml";
			if (structure.getNinCluster() > 1)
				writeFinalElementToDom(clusterElement,
						"diseaseOnDiseaseRelativeRiskFile", fileName);
			healthStateRootElement.appendChild(clusterElement);
		}
		/* write the other mortality data */

		fileName = directoryName + "\\parameters\\baselineOtherMort.xml";
		writeFinalElementToDom(healthStateRootElement, "baselineOtherMortFile",
				fileName);
		writeOneDimArray(parameters.getBaselineOtherMortality(),
				"baselineOtherMortalities", "baselineOtherMortality", fileName);
		fileName = directoryName + "\\parameters\\relativeRisk_OtherMort.xml";

		writeFinalElementToDom(healthStateRootElement,
				"relativeRiskOtherMortFile", fileName);
		writeTwoDimArray(parameters.getRelRiskOtherMort(), "relativeRisks",
				"relativeRisk", fileName);

		/* now start the writing of disease-specific information */

		Element healthStateDiseaseElement;

		for (int c = 0; c < parameters.getNCluster(); c++) {
			DiseaseClusterStructure structure = parameters
					.getClusterStructure()[c];
			if (structure.getNinCluster() == 1 || structure.withCuredFraction)

				for (int d = 0; d < structure.getNinCluster(); d++) {

					healthStateDiseaseElement = documentForHealthState
							.createElement("disease");
					healthStateRootElement
							.appendChild(healthStateDiseaseElement);

					String name = structure.getDiseaseName().get(d);

					writeFinalElementToDom(healthStateDiseaseElement,
							"ClusterNumber", ((Integer) c).toString());
					writeFinalElementToDom(healthStateDiseaseElement,
							"diseaseNumberWithinCluster", ((Integer) structure
									.getDiseaseNumber()[d]).toString());
					writeFinalElementToDom(healthStateDiseaseElement,
							"diseaseName", name);

					fileName = directoryName
							+ "\\parameters\\baselineIncidence_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";
					writeFinalElementToDom(healthStateDiseaseElement,
							"baselineIncidenceFile", fileName);
					writeOneDimArray(extractFromOneDimArray(parameters
							.getBaselineIncidence(), structure
							.getDiseaseNumber()[0]), "baselineIncidences",
							"baselineIncidence", fileName);

					fileName = directoryName
							+ "\\parameters\\baselineFatalIncidence_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";
					/*
					 * not needed by update rule for disease, only for mortality
					 * rule
					 */
					writeFinalElementToDom(healthStateDiseaseElement,
							"baselineFatalIncidenceFile", fileName);
					writeOneDimArray(extractFromOneDimArray(parameters
							.getBaselineFatalIncidence(), structure
							.getDiseaseNumber()[0]), "baselineFatalIncidences",
							"baselineFatalIncidence", fileName);

					fileName = directoryName
							+ "\\parameters\\attributableMort_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";
					writeFinalElementToDom(healthStateDiseaseElement,
							"attributableMortFile", fileName);
					writeOneDimArray(extractFromOneDimArray(parameters
							.getAttributableMortality(), structure
							.getDiseaseNumber()[0]), "attributableMortalities",
							"attributableMortality", fileName);

					fileName = directoryName + "\\parameters\\relativeRisk_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";
					writeFinalElementToDom(healthStateDiseaseElement,
							"relativeRiskFile", fileName);
					writeTwoDimArray(
							extractFromTwoDimArray(
									parameters.getRelRiskClass(), structure
											.getDiseaseNumber()[0]),
							"relativeRisks", "relativeRisk", fileName);

					if (structure.withCuredFraction) {

						// TODO bovenstaande is nog niet goed voor ziekten met
						// cured fractions.
					}
				} // end if single or cured fraction disease

			else {

				fileName = directoryName
						+ "\\parameters\\relativeRiskDiseaseOnDisease_cluster"
						+ ((Integer) c).toString() + ".xml";
				writeThreeDimArray(extractFromThreeDimArray(parameters
						.getRelRiskDiseaseOnDisease(), c), "relativeRisks",
						"relativeRisk", fileName);
				/*
				 * the diseaseOnDiseaseRelativeRiskFile filename for the
				 * survival rule is written later, as it should be included in a
				 * cluster element
				 */

				for (int d = 0; d < structure.getNinCluster(); d++) {

					/*
					 * write away information per disease, both for each disease
					 * combination rule, and for the survival rule survival rule
					 * info and data need to be written only once
					 */

					/* first the general info */

					String name = structure.getDiseaseName().get(d);
					healthStateDiseaseElement = documentForHealthState
							.createElement("disease");
					writeFinalElementToDom(healthStateDiseaseElement,
							"clusterNumber", ((Integer) c).toString());
					writeFinalElementToDom(healthStateDiseaseElement,
							"diseaseNumberWithinCluster", ((Integer) d)
									.toString());
					writeFinalElementToDom(healthStateDiseaseElement,
							"diseaseName", name);

					/* baseline (non-fatal) incidence */
					fileName = directoryName
							+ "\\parameters\\baselineIncidence_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";

					writeFinalElementToDom(healthStateDiseaseElement,
							"baselineIncidenceFile", fileName);
					writeOneDimArray(extractFromOneDimArray(parameters
							.getBaselineIncidence(), structure
							.getDiseaseNumber()[d]), "baselineIncidences",
							"baselineIncidence", fileName);

					/* baseline fatal incidence */
					fileName = directoryName
							+ "\\parameters\\baselineFatalIncidence_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";

					writeFinalElementToDom(healthStateDiseaseElement,
							"baselineFatalIncidenceFile", fileName);
					writeOneDimArray(extractFromOneDimArray(parameters
							.getBaselineFatalIncidence(), structure
							.getDiseaseNumber()[0]), "baselineFatalIncidences",
							"baselineFatalIncidence", fileName);

					/* attributable mortality */
					fileName = directoryName
							+ "\\parameters\\attributableMort_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";

					writeFinalElementToDom(healthStateDiseaseElement,
							"attributableMortFile", fileName);
					writeOneDimArray(extractFromOneDimArray(parameters
							.getAttributableMortality(), structure
							.getDiseaseNumber()[d]), "attributableMortalities",
							"attributableMortality", fileName);

					/* relative risks of risk factor on the disease */
					fileName = directoryName + "\\parameters\\relativeRisk_"
							+ ((Integer) c).toString() + "_"
							+ ((Integer) d).toString() + "_" + name + ".xml";

					writeFinalElementToDom(healthStateDiseaseElement,
							"relativeRiskFile", fileName);
					writeTwoDimArray(
							extractFromTwoDimArray(
									parameters.getRelRiskClass(), structure
											.getDiseaseNumber()[d]),
							"relativeRisks", "relativeRisk", fileName);

					healthStateRootElement
							.appendChild(healthStateDiseaseElement);

				} // end loop over diseases for clusters with multiple diseases
			}// end if statement for clusters with multiple diseases
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
			throws CDMConfigurationException {
		// temporary name for testing

		String fileName = simFileName+".xml";

		for (int scen = 0; scen <= scenInfo.nScenarios; scen++) {
			if (scen > 0)
				fileName = simFileName + "_scen_" + scen+".xml";
			Document document = newDocument(fileName);

			Element rootElement = document.createElement("sim");
			writeFinalElementToDom(rootElement, "lb", simulationName);
			writeFinalElementToDom(rootElement, "timestep", "1");
			writeFinalElementToDom(rootElement, "runmode", "longitudinal");
			writeFinalElementToDom(rootElement, "stepsbetweensaves", "1");
			writeFinalElementToDom(rootElement, "stepsinrun",
					((Integer) stepsInRun).toString());
			writeFinalElementToDom(rootElement, "stoppingcondition", null);
			if (scen > 0 && scenInfo.initialPrevalenceType[scen-1])
				writeFinalElementToDom(rootElement, "pop", popFileName
						+ "_scen_" + scen+".xml");
			else
				writeFinalElementToDom(rootElement, "pop", popFileName+".xml");

			int riskType = parameters.getRiskType();
			// age+sex+riskfactor+diseasestate
			int nRules = 4;
			if (riskType == 3)
				nRules++;

			setNRules(nRules);

			// TODO get this from parameter estimation

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
					ruleName = "nl.rivm.emi.cdm.rules.update.dynamo.CategoricalRiskFactorMultiToOneUpdateRule";
					break;
				case 4:
					if (riskType == 3)
						ruleName = "nl.rivm.emi.cdm.rules.update.dynamo.RiskFactorDurationMultiToOneUpdateRule";

				default:
					if (riskType == 3)
						break;
					else
						ruleName = "nl.rivm.emi.cdm.rules.update.dynamo.HealthStateManyToManyUpdateRule";

				}

				writeFinalElementToDom(rule, "characteristicid",
						((Integer) charID).toString());
				writeFinalElementToDom(rule, "classname", ruleName);
				String ConfigXMLfileName = null;
				if (scen == 0 || charID != 3)
					ConfigXMLfileName = directoryName + "\\modelconfiguration"
							+ "\\rule" + ((Integer) charID).toString() + ".xml";
				else
					ConfigXMLfileName = directoryName + "\\modelconfiguration"
							+ "\\rule" + ((Integer) charID).toString()
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
			throws CDMConfigurationException {

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
		// TODO inlezen en wegschrijven naar van risk factor
		writeFinalElementToDom(charElement, "lb", "risk factor");

		if (parameters.getRiskType() == 1 || parameters.getRiskType() == 3)
			writeFinalElementToDom(charElement, "type", "categorical");
		else
			writeFinalElementToDom(charElement, "type", "numericalcontinuous");

		if (parameters.getRiskType() == 1 || parameters.getRiskType() == 3) {
			element = document.createElement("possiblevalues");
			charElement.appendChild(element);
			for (int r = 0; r < parameters.prevRisk[0][0].length; r++) {
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

		for (int c = 0; c < parameters.getNCluster(); c++) {
			DiseaseClusterStructure structure = parameters
					.getClusterStructure()[c];
			if (structure.getNinCluster() == 1)
				numberOfElements++;
			else if (structure.isWithCuredFraction())
				numberOfElements += 2;
			else
				numberOfElements += Math.pow(2, structure.getNinCluster()) - 1;
		}
		writeFinalElementToDom(charElement, "numberofelements",
				((Integer) numberOfElements).toString());

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
			String tag, String fileName) throws CDMConfigurationException {

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
			String tag, String fileName) throws CDMConfigurationException {

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
			throws CDMConfigurationException {

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
			throws CDMConfigurationException {
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
			throw new CDMConfigurationException("File exception when writing "
					+ fileName + " : " + e.getClass().getName() + " message: "
					+ e.getMessage());
		} catch (TransformerConfigurationException e) {
			log.fatal("TransformerConfigurationException when writing "
					+ fileName + ": " + e.getClass().getName() + " message: "
					+ e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CDMConfigurationException(
					"TransformerConfigurationException when writing "
							+ fileName + ": " + e.getClass().getName()
							+ " message: " + e.getMessage());
		} catch (TransformerException e) {
			log.fatal("TransformerException when writing " + fileName + " "
					+ e.getClass().getName() + " message: " + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CDMConfigurationException(
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
			throws CDMConfigurationException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = (DocumentBuilder) dbfac.newDocumentBuilder();
			Document document = docBuilder.newDocument();
			return document;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.fatal(e.getMessage());

			throw new CDMConfigurationException(
					"error building document for parameterfile " + fileName);
		}
	}
}
