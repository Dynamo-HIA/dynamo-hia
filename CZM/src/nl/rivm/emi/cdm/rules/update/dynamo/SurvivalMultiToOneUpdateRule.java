package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * 
 * 
 * 
 * 
 * @author boshuizh configuration file should look like: <?xml version="1.0"
 *         encoding="UTF-8" standalone="no" ?> - <updateRuleConfiguration>
 *         <name>survival</name> <riskType>1</riskType> <nCat>2</nCat>
 *         <refValContinuousVariable>0.0</refValContinuousVariable> - <disease>
 *         <ClusterNumber>0</ClusterNumber>
 *         <diseaseNumberWithinCluster>0</diseaseNumberWithinCluster>
 *         <diseaseName>ziekte1</diseaseName>
 *         <baselineIncidenceFile>c:\hendriek\
 *         java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineIncidence_4_ziekte1.xml</baselineIncidenceFile>
 *         <baselineFatalIncidenceFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineFatalIncidence_4_ziekte1
 *         .xml</baselineFatalIncidenceFile>
 *         <attributableMortFile>c:\hendriek\java
 *         \dynamohome\Simulations\testsimulation
 *         \parameters\attributableMort_4_ziekte1.xml</attributableMortFile>
 *         <relativeRiskFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\relativeRisk_4_ziekte1.xml</relativeRiskFile> </disease>
 *         - <disease> <ClusterNumber>0</ClusterNumber>
 *         <diseaseNumberWithinCluster>1</diseaseNumberWithinCluster>
 *         <diseaseName>ziekte2</diseaseName>
 *         <baselineIncidenceFile>c:\hendriek\
 *         java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineIncidence_4_ziekte2.xml</baselineIncidenceFile>
 *         <baselineFatalIncidenceFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineFatalIncidence_4_ziekte2
 *         .xml</baselineFatalIncidenceFile>
 *         <attributableMortFile>c:\hendriek\java
 *         \dynamohome\Simulations\testsimulation
 *         \parameters\attributableMort_4_ziekte2.xml</attributableMortFile>
 *         <relativeRiskFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\relativeRisk_4_ziekte2.xml</relativeRiskFile> </disease>
 *         <charID>7</charID> <nclusters>1</nclusters> - <clusterInformation>
 *         <clusterNumber>0</clusterNumber>
 *         <startsAtDiseaseNumber>0</startsAtDiseaseNumber>
 *         <numberOfDiseasesInCluster>2</numberOfDiseasesInCluster>
 *         <diseaseOnDiseaseRelativeRiskFile
 *         >c:\hendriek\java\dynamohome\Simulations
 *         \testsimulation\parameters\relativeRiskDiseaseOnDisease_cluster0
 *         .xml</diseaseOnDiseaseRelativeRiskFile> </clusterInformation>
 *         <baselineOtherMortFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\baselineOtherMort.xml</baselineOtherMortFile>
 *         <relativeRiskOtherMortFile
 *         >c:\hendriek\java\dynamohome\Simulations\testsimulation
 *         \parameters\relativeRisk_OtherMort.xml</relativeRiskOtherMortFile>
 *         </updateRuleConfiguration>
 */
public class SurvivalMultiToOneUpdateRule extends
		ClusterDiseaseMultiToOneUpdateRule {

	private String nDiseasesLabel = "Survival rule";

	private String clusterInformationLabel = "clusterInformation";

	private String clusterNumberLabel = "clusterNumber";

	private String clusterNameLabel = "ClusterNumber";
	private String diseaseNumberWithinClusterLabel = "diseaseNumberWithinCluster";

	private String startsAtDiseaseNumberLabel = "startsAtDiseaseNumber";
	private String numberOfDiseasesInClusterLabel = "numberOfDiseasesInCluster";

	private int[] numberOfDiseasesInCluster;
	private int[] clusterStartsAtDiseaseNumber;
	private int totalNumberOfDiseases;

	private String nClusterLabel = "nclusters";

	private int nCluster = 1;

	private int[] DiseaseNumberWithinCluster;
	/* indexes are: 1:cluster number, 2: age 3: sex 4: from 5: to */
	private float[][][][][] relativeRiskDiseaseOnDisease;

	private String[] diseaseOnDiseaseRelativeRiskFileName;

	private float[][][] relRiskOtherMortCategorical;

	private float[][] baselineOtherMort;

	public SurvivalMultiToOneUpdateRule() throws ConfigurationException,
			CDMUpdateRuleException {
		super();
	};

	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		float newValue = -1;

		try {
			int ageValue = (int) getFloat(currentValues, ageIndex);
			int sexValue = getInteger(currentValues, sexIndex);
			if (ageValue > 95)
				ageValue = 95;
			float oldValue = getFloat(currentValues, characteristicIndex);

			double otherMort = calculateOtherCauseMortality(currentValues,
					ageValue, sexValue);
			double[] incidence = new double[nDiseases];
			for (int d = 0; d < nDiseases; d++) {
				incidence[d] = calculateIncidence(currentValues, ageValue,
						sexValue, d);
			}
			double[] currentDiseaseValue;
			if (riskType == 1 || riskType == 2)
				currentDiseaseValue = getCurrentDiseaseValues(currentValues,
						ageValue, sexValue, riskFactorIndex1 + 1, nDiseases);
			else
				currentDiseaseValue = getCurrentDiseaseValues(currentValues,
						ageValue, sexValue, riskFactorIndex2 + 1, nDiseases);
			double survivalFraction = Math.exp(-otherMort * timeStep);
			for (int d = 0; d < nDiseases; d++) {
				survivalFraction *= (attributableMortality[d][ageValue][sexValue] * (1 - currentDiseaseValue[d])
						* Math.exp(-timeStep * incidence[d]) + (attributableMortality[d][ageValue][sexValue]
						* currentDiseaseValue[d] - incidence[d])
						* Math.exp(-timeStep * attributableMortality[d][ageValue][sexValue]))
						/ (attributableMortality[d][ageValue][sexValue] - incidence[d]);

			}

			newValue = (float) survivalFraction;

			return newValue;
		} catch (CDMUpdateRuleException e) {
			log.fatal(e.getMessage());
			log.fatal("this message was issued by SurvivalMultiToOneUpdateRule"
					+ " when updating characteristic number "
					+ "characteristicIndex");
			e.printStackTrace();
			throw e;

		}

	}

	public int getNDiseases() {
		return nDiseases;
	}

	private double calculateIncidence(Object[] currentValues, int ageValue,
			int sexValue, int diseaseNumber) throws CDMUpdateRuleException {
		double incidence = 0;
		if (riskType == 1) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);
			incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
					* relRiskCategorical[diseaseNumber][ageValue][sexValue][riskFactorValue];
		}

		if (riskType == 2) {

			float riskFactorValue = getFloat(currentValues, riskFactorIndex1);
			incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
					* Math
							.pow(
									(riskFactorValue - referenceValueContinous),
									relRiskContinous[diseaseNumber][ageValue][sexValue]);

		}
		if (riskType == 3) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);

			if (durationClass == riskFactorValue) {
				float riskDurationValue = getFloat(currentValues,
						riskFactorIndex2);

				incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
						* ((relRiskBegin[diseaseNumber] [ageValue][sexValue]- relRiskEnd[diseaseNumber][ageValue][sexValue])
								* Math
										.exp(-riskDurationValue
												* alfaDuur[diseaseNumber][ageValue][sexValue]) + relRiskEnd[diseaseNumber][ageValue][sexValue]);
			} else
				incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
						* relRiskCategorical[diseaseNumber][ageValue][sexValue][riskFactorValue];
		}
		return incidence;
	}

	private double calculateOtherCauseMortality(Object[] currentValues,
			int ageValue, int sexValue) throws CDMUpdateRuleException {
		double otherCauseMortality = 0;
		if (riskType == 1) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);
			otherCauseMortality = baselineOtherMort[ageValue][sexValue]
					* relRiskOtherMortCategorical[ageValue][sexValue][riskFactorValue];
		}

		if (riskType == 2) {

			float riskFactorValue = getFloat(currentValues, riskFactorIndex1);
			otherCauseMortality = baselineOtherMort[ageValue][sexValue]
					* Math.pow((riskFactorValue - referenceValueContinous),
							relRiskOtherMortContinous[ageValue][sexValue]);

		}
		if (riskType == 3) {

			int riskFactorValue = getInteger(currentValues, riskFactorIndex1);

			if (durationClass == riskFactorValue) {
				float riskDurationValue = getFloat(currentValues,
						riskFactorIndex2);

				otherCauseMortality = baselineOtherMort[ageValue][sexValue]
						* ((relRiskOtherMortBegin[ageValue][sexValue] - relRiskOtherMortEnd[ageValue][sexValue])
								* Math
										.exp(-riskDurationValue
												* alfaDuurOtherMort[ageValue][sexValue]) + relRiskOtherMortEnd[ageValue][sexValue]);
			} else
				otherCauseMortality = baselineOtherMort[ageValue][sexValue]
						* relRiskOtherMortCategorical[ageValue][sexValue][riskFactorValue];
		}
		return otherCauseMortality;
	}

	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException {
		boolean success = false;
		try {
			XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
					configurationFile);
			ConfigurationNode rootNode = configurationFileConfiguration
					.getRootNode();
			if (configurationFileConfiguration.getRootElementName() != globalTagName)

				throw new DynamoConfigurationException(" Tagname "
						+ globalTagName
						+ " expected in file for updaterule ClusterDisease"
						+ " but found tag " + rootNode.getName());

			/* first handle the general information (not disease dependent) */
			handleCharID(configurationFileConfiguration);
			handleRiskType(configurationFileConfiguration);
			handleNCat(configurationFileConfiguration);
			handleNClusters(configurationFileConfiguration);
			handleOtherMort(configurationFileConfiguration);

			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();

			/*
			 * handle cluster info, as this is needed to initialize the arrays
			 * that are to be filled for the individual diseases
			 */

			/*
			 * INFO to handle: - <clusterInformation>
			 * <clusterNumber>0</clusterNumber>
			 * <startsAtDiseaseNumber>0</startsAtDiseaseNumber>
			 * <numberOfDiseasesInCluster>2</numberOfDiseasesInCluster>
			 * <diseaseOnDiseaseRelativeRiskFile
			 * >c:\hendriek\java\dynamohome\Simulations
			 * \testsimulation\parameters
			 * \relativeRiskDiseaseOnDisease_cluster0.xml
			 * </diseaseOnDiseaseRelativeRiskFile> </clusterInformation>
			 */

			for (ConfigurationNode rootChild : rootChildren) {
				if (rootChild.getName() == clusterInformationLabel) {
					handleClusterInformation(rootChild);
					
				}

				/* also read the name of the info file (not important) */
				else if (rootChild.getName() == nameLabel) {
					String value = (String) rootChild.getValue();
					log.debug("Setting overall name to: " + value);
					setClusterName(value);
				}
			}
			/* count the total number of diseases and set it */
			int nTotDiseases = 0;
			for (int c = 0; c < getNCluster(); c++)
				nTotDiseases += getNdiseasesInCluster(c);

			/* handle the disease dependent information */
			setNDiseases(nTotDiseases);
			DiseaseNumberWithinCluster=new int [nTotDiseases];

			int diseaseRead = 0;
			for (ConfigurationNode rootChild : rootChildren) {

				if (rootChild.getName() == diseaseLabel) {
					diseaseRead = handleDiseaseInfo(rootChild, diseaseRead);
					/*
					 * reads per disease: <disease>
					 * <ClusterNumber>0</ClusterNumber>
					 * <diseaseNumberWithinCluster
					 * >1</diseaseNumberWithinCluster>
					 * <diseaseName>ziekte2</diseaseName>
					 * <baselineIncidenceFile>
					 * c:\hendriek\java\dynamohome\Simulations
					 * \testsimulation\parameters
					 * \baselineIncidence_4_ziekte2.xml</baselineIncidenceFile>
					 * <baselineFatalIncidenceFile>c:\hendriek\java\dynamohome\
					 * Simulations
					 * \testsimulation\parameters\baselineFatalIncidence_4_ziekte2
					 * .xml</baselineFatalIncidenceFile>
					 * <attributableMortFile>c:
					 * \hendriek\java\dynamohome\Simulations
					 * \testsimulation\parameters
					 * \attributableMort_4_ziekte2.xml</attributableMortFile>
					 * <relativeRiskFile
					 * >c:\hendriek\java\dynamohome\Simulations\
					 * testsimulation\parameters
					 * \relativeRisk_4_ziekte2.xml</relativeRiskFile> </disease>
					 */
				}

			}// end loop over diseases

			if (diseaseRead != getNDiseases())
				log
						.fatal("Number of disease read ("
								+ diseaseRead
								+ "does not agree with number of diseases as given in XML file"
								+ getNDiseases());
			// TODO gooi exception

			success = true;
			return success;
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noConfigurationTagMessage
							+ nDiseasesLabel);
		} catch (DynamoConfigurationException e) {
			log.fatal(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}

	private int handleDiseaseInfo(ConfigurationNode node, int diseaseNo)

	throws ConfigurationException {

		/*
		 * to read per disease <disease> <ClusterNumber>0</ClusterNumber>
		 * <diseaseNumberWithinCluster>1</diseaseNumberWithinCluster>
		 * <diseaseName>ziekte2</diseaseName>
		 * <baselineIncidenceFile>c:\hendriek\
		 * java\dynamohome\Simulations\testsimulation
		 * \parameters\baselineIncidence_4_ziekte2.xml</baselineIncidenceFile>
		 * <baselineFatalIncidenceFile
		 * >c:\hendriek\java\dynamohome\Simulations\testsimulation
		 * \parameters\baselineFatalIncidence_4_ziekte2
		 * .xml</baselineFatalIncidenceFile>
		 * <attributableMortFile>c:\hendriek\java
		 * \dynamohome\Simulations\testsimulation
		 * \parameters\attributableMort_4_ziekte2.xml</attributableMortFile>
		 * <relativeRiskFile
		 * >c:\hendriek\java\dynamohome\Simulations\testsimulation
		 * \parameters\relativeRisk_4_ziekte2.xml</relativeRiskFile> </disease>
		 */

		try {

			ArraysFromXMLFactory factory = new ArraysFromXMLFactory();

			List<ConfigurationNode> diseaseChildren = (List<ConfigurationNode>) node
					.getChildren();
			for (ConfigurationNode diseaseElement : diseaseChildren) {
				if (diseaseElement.getName() == diseaseNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting diseasename of disease" + diseaseNo
							+ " to: " + value);
					setDiseaseName(value, diseaseNo);
				}

				else if (diseaseElement.getName() == clusterNumberLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting clusterNumber of disease " + diseaseNo
							+ " to: " + value);
					// TODO check if OK with other info
				}

				else if (diseaseElement.getName() == diseaseNumberWithinClusterLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting diseaseNumber within Cluster of disease"
							+ diseaseNo + " to: " + value);
					setDiseaseNumberWithinCluster(value, diseaseNo);
				}

				else if (diseaseElement.getName() == attributableMortalityFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting AttributableMortalityFilename to: "
							+ value);
					setAttributableMortalityFileName(value, diseaseNo);
					float[][] inputData = new float[96][2];
					inputData = factory.manufactureOneDimArray(
							attributableMortalityFileName[diseaseNo],
							"attributableMortalities", "attributableMortality");
					setAttributableMortality(inputData, diseaseNo);
					log.debug("reading AttributableMortality data for disease "
							+ diseaseNumber);

				}

				else if (diseaseElement.getName() == baselineIncidenceFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting baselineIncidenceFilename to: " + value);
					setBaselineIncidenceFileName(value, diseaseNo);
					float[][] inputData = new float[96][2];
					inputData = factory.manufactureOneDimArray(
							baselineIncidenceFileName[diseaseNo],
							"baselineIncidences", "baselineIncidence");
					setBaselineIncidence(inputData, diseaseNo);
					log.debug("reading BaselineIncidence data for disease "
							+ diseaseNumber);

				}

				else if (diseaseElement.getName() == baselineFatalIncidenceFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log.debug("Setting baselineFatalIncidenceFilename to: "
							+ value);
					setBaselineFatalIncidenceFileName(value, diseaseNo);
					float[][] inputData = new float[96][2];
					inputData = factory.manufactureOneDimArray(
							baselineFatalIncidenceFileName[diseaseNo],
							"baselineFatalIncidences", "baselineFatalIncidence");
					setBaselineFatalIncidence(inputData, diseaseNo);
					log
							.debug("reading BaselineFatalIncidence data for disease "
									+ diseaseNumber);

				}

				else if (diseaseElement.getName() == relRiskCatFileNameLabel) {
					String value = (String) diseaseElement.getValue();
					log
							.debug("Setting baselineRelativeRiskFilename  (categorical) to: "
									+ value);
					setRelRiskCatFileName(value, diseaseNo);
					float[][][] inputData;
					inputData = factory.manufactureTwoDimArray(
							relRiskCatFileName[diseaseNo], "relativeRisks",
							"relativeRisk");
					setRelRiskCat(inputData, diseaseNo);
					log
							.debug("reading relative risks for disease "
									+ diseaseNo);

				}
			}

			// TODO checken of de nummering consistent is in de invoer file
			diseaseNo++;

			return diseaseNo;

		} catch (Exception e) {// TODO}

		}
		return diseaseNo;
	}

	private void handleNClusters(XMLConfiguration configurationFileConfiguration)
	/* <nclusters>1</nclusters> */
	throws ConfigurationException {
		try {
			int nClusters = configurationFileConfiguration
					.getInt(nClusterLabel);
			log.debug("Setting number of clusters to " + nClusters);

			diseaseOnDiseaseRelativeRiskFileName = new String[nClusters];
			relativeRiskDiseaseOnDisease = new float[nClusters][96][2][][];
			numberOfDiseasesInCluster = new int[nClusters];
			clusterStartsAtDiseaseNumber = new int[nClusters];

		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noConfigurationTagMessage
							+ " reading number of categories");
		}
	}

	private void setDiseaseNumberWithinCluster(String value, int i) {

		DiseaseNumberWithinCluster[i] = Integer.parseInt(value);

	}

	private void handleClusterInformation(ConfigurationNode rootChild) {

		try {
			List<ConfigurationNode> clusterChildren = (List<ConfigurationNode>) rootChild
					.getChildren();

			int clusterNumber = 0;
			int nDiseasesInCluster = 0;
			for (ConfigurationNode clusterElement : clusterChildren) {

				if (clusterElement.getName() == clusterNumberLabel) {
					String value = (String) clusterElement.getValue();
					log.debug("Setting clusterNumber to: " + value);
					clusterNumber = Integer.parseInt(value);
				}

				if (clusterElement.getName() == startsAtDiseaseNumberLabel) {
					String value = (String) clusterElement.getValue();
					log.debug("Setting number of first disease in cluster to: "
							+ value);
					setStartsAtDiseaseNumber(value, clusterNumber);
				}

				if (clusterElement.getName() == numberOfDiseasesInClusterLabel) {
					String value = (String) clusterElement.getValue();
					nDiseasesInCluster = Integer.parseInt(value);
					log.debug("Setting number of diseases in cluster to: "
							+ value);
					setNumberOfDiseasesInCluster(value, clusterNumber);
				}

				if (clusterElement.getName() == diseaseOnDiseaseRelativeRiskFileNameLabel) {
					String value = (String) clusterElement.getValue();
					log
							.debug("Setting name of diseaseOnDiseaseRelativeRiskFile to: "
									+ value);
					setDiseaseOnDiseaseRelativeRiskFile(value, clusterNumber);
				}
			}

			if (nDiseasesInCluster == 0)
				throw new CDMUpdateRuleException(
						"configuration file for survival update rule has no number of disease in cluster "
								+ clusterNumber);
			else {
				float[][][][] inputData = new float[96][2][nDiseasesInCluster][nDiseasesInCluster];
				ArraysFromXMLFactory factory = new ArraysFromXMLFactory();
				inputData = factory.manufactureThreeDimArray(
						getDiseaseOnDiseaseRelativeRiskFileName(clusterNumber),
						"relativeRisks", "relativeRisk");
				setRelativeRisksDiseaseOnDisease(inputData, clusterNumber);
				log
						.debug("reading DiseaseOnDiseaseRelativeRiskFile for cluster "
								+ clusterNumber);
			}
			totalNumberOfDiseases += nDiseasesInCluster;
		}

		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	private void setRelativeRisksDiseaseOnDisease(float[][][][] inputData,
			int clusterNumber) {
		relativeRiskDiseaseOnDisease[clusterNumber] = inputData;

	}

	private String getDiseaseOnDiseaseRelativeRiskFileName(int i) {

		return diseaseOnDiseaseRelativeRiskFileName[i];
	}

	private int getNCluster() {
		return nCluster;

	}

	private void setNumberOfDiseasesInCluster(String value, int i) {

		numberOfDiseasesInCluster[i] = Integer.parseInt(value);
		// TODO Auto-generated method stub

	}

	public int getNumberOfDiseasesInCluster(int i) {

		return numberOfDiseasesInCluster[i];
		// TODO Auto-generated method stub

	}

	private void setDiseaseOnDiseaseRelativeRiskFile(String value, int i) {
		diseaseOnDiseaseRelativeRiskFileName[i] = value;

		// TODO Auto-generated method stub

	}

	// TODO Auto-generated method stub

	private void setStartsAtDiseaseNumber(String value, int i) {
		clusterStartsAtDiseaseNumber[i] = Integer.parseInt(value);

	}

	private void setStartsAtDiseaseNumber(int value, int i) {
		clusterStartsAtDiseaseNumber[i] = value;

	}

	private int getNdiseasesInCluster(int i) {
		// TODO Auto-generated method stub
		return numberOfDiseasesInCluster[i];
	}

	// obsolete:
	protected void handleAttributableMortality(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			/*
			 * TODO String attributableMortalityFileName =
			 * simulationConfiguration
			 * .getString(attributableMortalityFileNameLabel);
			 * log.debug("Setting AttributableMortalityFilename to: " +
			 * attributableMortalityFileName );
			 * setAttributableMortalityFileName(attributableMortalityFileName);
			 */

			String attributableMortalityFileName = "not given";
			attributableMortality = new float[96][2][6];
			for (int d = 0; d < 6; d++) {
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++) {
						attributableMortality[a][g][d] = 0.01F;

					}
				;
			}
			loadOneDimData(attributableMortalityFileName,
					"attributableMortality", 0.01F);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	// obsolete:
	protected void handleBaselineIncidence(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			String FileName = simulationConfiguration
					.getString(attributableMortalityFileNameLabel);
			log.debug("Setting BaselineIncidenceFilename to: " + FileName);
			setBaselineIncidenceFileName(FileName);
			baselineIncidence = new float[96][2][6];

			for (int d = 0; d < 6; d++)
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++) {
						baselineIncidence[a][g][d] = 0.01F;

					}
			;
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	protected void handleOtherMort(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			String FileName = simulationConfiguration
					.getString(baselineOtherMortFileLabel);
			log.debug("Setting BaselineOtherMortalityFilename to: " + FileName);
			setBaselineOtherMortFileName(FileName);
			float[][] inputData = new float[96][2];
			ArraysFromXMLFactory factory = new ArraysFromXMLFactory();
			inputData = factory.manufactureOneDimArray(
					getBaselineOtherMortFileName(), "baselineOtherMortalities",
					"baselineOtherMortality");
			setBaselineOtherMort(inputData);
			log.debug("reading BaselineOtherMortality for disease "
					+ diseaseNumber);
			FileName = simulationConfiguration
					.getString(relativeRiskOtherMortFileLabel);
			log.debug("Setting RelRiskOtherMortalityFilename to: " + FileName);
			setRelRiskOtherMortCatFileName(FileName);
			float[][][] inputData2 = new float[96][2][];
			inputData2 = factory.manufactureTwoDimArray(
					getRelRiskOtherMortCatFileName(), "relativeRisks",
					"relativeRisk");
			setRelRiskOtherMortCategorical(inputData2);
			// TODO other risk types

			//
			// for (int a=0;a<96;a++) for(int g=0;g<2;g++)
			//	 

		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	private void setRelRiskOtherMortCategorical(float[][][] input) {
		
		relRiskOtherMortCategorical=input;
	}

	public float[][] getBaselineOtherMort() {
		return baselineOtherMort;
	}



	// obsolete: only used for testing
	protected void handleRelativeRisks(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			// String FileName =
			// simulationConfiguration.getString(attributableMortalityFileNameLabel);
			// log.debug("Setting BaselineIncidenceFilename to: " + FileName );
			// setattributableMortalityFileName(FileName);
			String attributableMortalityFileName = "not given";
			relRiskCategorical = new float[96][2][nDiseases][nCat];
			float[] fill = { 1, 1.1F, 1.2F, 1.5F };
			for (int d = 0; d < 6; d++)
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++) {
						relRiskCategorical[a][g][d] = fill;

					}
			;
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

}
