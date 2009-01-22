package nl.rivm.emi.dynamo.estimation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.DynamoUpdateRuleConfigurationException;
import nl.rivm.emi.cdm.rules.update.dynamo.ArraysFromXMLFactory;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InputDataFactory {

	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.estimation.ConfigurationFromXMLFactory");

	/**
	 * Currently implemented structure:
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?> <newborns>no</newborns>
	 * <startingYear>2008</startingYear> <numberOfYears>10</numberOfYears>
	 * <simPopSize> 1000</simPopSize> <minAge> 0</minAge> <maxAge>100 </maxAge>
	 * <timeStep>1 </timeStep> <randomSeed>12345 </randomSeed>
	 * <resultType>aggregated</resultType> <popFileName>NL-2008 </popFileName>
	 * <scenarios> <scenario label="1"> <scenName>scenario 1 </scenName>
	 * <successRate> 100 </successRate>
	 * <targetMinAge>0</targetMinAge><targetMaxAge>100</targetMaxAge>
	 * <targetGender> 2</targetGender>
	 * <alternativeTransFile>none</alternativeTransFile>
	 * <alternativePrevFile>Prev-scen1 </alternativePrevFile> </scenario>
	 * <scenario label="2"> <scenName>scenario 2 </scenName> <successRate> 50
	 * </successRate>
	 * <targetMinAge>0</targetMinAge><targetMaxAge>100</targetMaxAge>
	 * <targetGender> 2</targetGender>
	 * <alternativeTransFile>none</alternativeTransFile>
	 * <alternativePrevFile>Prev-scen1 </alternativePrevFile> </scenario>
	 * </scenarios> <diseases> <disease label="1"> <diseaseName> lung
	 * cancer</diseaseName> <diseasePrevFile>lung
	 * cancer-prev-NL</diseasePrevFile> <diseaseIncFile>lung
	 * cancer-inc-NL</diseaseIncFile>
	 * <diseaseExcessMortFile>lungcancer-excess-mort</diseaseExcessMortFile>
	 * <diseaseDalyWeights>lungcancer-Daly weights </diseaseDalyWeights>
	 * </disease > <disease label="2"> <diseaseName> HVD</diseaseName>
	 * <diseasePrevFile>HVD-prev-NL</diseasePrevFile>
	 * <diseaseIncFile>HVD-inc-NL</diseaseIncFile>
	 * <diseaseExcessMortFile>HVD-excess-mort</diseaseExcessMortFile>
	 * <diseaseDalyWeights>HVD-Daly weights </diseaseDalyWeights> </disease >
	 * <disease label="3"> <diseaseName> diabetes</diseaseName>
	 * <diseasePrevFile>diabetes-prev-NL</diseasePrevFile>
	 * <diseaseIncFile>diabetes-inc-NL</diseaseIncFile>
	 * <diseaseExcessMortFile>diabetes-excess-mort</diseaseExcessMortFile>
	 * <diseaseDalyWeights>diabetes-Daly weights </diseaseDalyWeights> </disease
	 * > </diseases> <riskfactor> <riskFactorName>BMI </riskFactorName>
	 * <riskFactorType>compound</riskFactorType>
	 * <riskFactorPrevFile>BMI-NL</riskFactorPrevFile>; <riskFactorTransFile>
	 * </riskFactorTransFile>
	 * <riskFactorRRforDeathFile></riskFactorRRforDeathFile>; </riskfactor>
	 * <RRs> <RR label="1">
	 * 
	 * <isRRfrom>CVD</isRRfrom> <isRRto>lung cancer</isRRto>
	 * <isRRFile>RR-CVD-lung cancer</isRRFile> </RR > <RR label="2">
	 * 
	 * <isRRfrom>diabetes</isRRfrom> <isRRto>CVD</isRRto> <isRRFile>RR-CVD-lung
	 * cancer</isRRFile> </RR > </RR> </RRs> </xml>
	 * 
	 */

	/* Field containing the name of the base directory */
	// temporary for testing;
	public String baseDir = "c:\\hendriek\\java\\dynamohome";;

	/*
	 * fields describing the labels of the XML configuration file (as made by
	 * window W01)
	 */

	/* first the taglabels for the main structure */
	private static final String mainTagLabel = "simulation";
	private static final String scenTagLabel = "scenarios";
	private static final String disTagLabel = "diseases";
	private static final String riskfactorTagLabel = "riskfactor";
	private static final String rrTagLabel = "RRs";
	private static final String scenTagSubLabel = "scenario";
	private static final String disTagSubLabel = "disease";
	private static final String rrSubTagLabel = "RR";

	private static final String newbornLabel = "newborns";
	private static final String startingYearLabel = "startingYear";
	private static final String numberOfYearsLabel = "numberOfYears";
	private static final String simPopSizeLabel = "simPopSize";
	private static final String minAgeLabel = "minAge";
	private static final String maxAgeLabel = "maxAge";
	private static final String timeStepLabel = "timeStep";
	private static final String randomSeedLabel = "randomSeed";
	private static final String resultTypeLabel = "resultType";
	private static final String popFileNameLabel = "popFileName";
	private static final String diseasesLabel = "diseases";
	private static final String singleDiseaseLabel = "disease";
	private static final String diseaseNameLabel = "diseaseName";
	private static final String diseasePrevFileLabel = "diseasePrevFile";
	private static final String diseaseIncFileLabel = "diseaseIncFile";
	private static final String diseaseExcessMortFileLabel = "diseaseExcessMortFile";
	private static final String diseaseDalyWeightLabel = "diseaseDalyWeights";
	private static final String riskFactorLabel = "riskfactor";
	private static final String riskFactorNameLabel = "riskFactorName";
	private static final String riskFactorTypeLabel = "riskFactorType";
	private static final String riskFactorPrevFileLabel = "riskFactorPrevFile";
	private static final String riskFactorTransFileLabel = "riskFactorTransFile";
	private static final String riskFactorRRforDeathFileLabel = "riskFactorRRforDeathFile";

	private static final String isRRfromLabel = "isRRfrom";
	private static final String isRRtoLabel = "isRRto";
	private static final String isRRfileLabel = "isRRFile";
	private static final String scenarioSeriesLabel = "scenarios";
	private static final String scenarioLabel = "scenario";
	private static final String scenarioNameLabel = "scenName";
	private static final String scenarioSuccessRateLabel = "successRate";
	private static final String targetMinAgeLabel = "targetMinAge";
	private static final String targetMaxAgeLabel = "targetMaxAge";
	private static final String targetGenderLabel = "targetGender";
	private static final String alternativeTransFileLabel = "alternativeTransFile";
	private static final String alternativePrevFileLabel = "alternativePrevFile";

	/* the object containing the content of the XML configuration file */
	private HierarchicalConfiguration configuration;

	private ArraysFromXMLFactory factory = new ArraysFromXMLFactory();

	private int riskFactorType;

	private String riskFactorName;

	private String riskFactorPrevFileName;

	private String riskFactorTransDirName;

	private String riskFactorRRforDeathFileName;

	private String TransitionName;

	/* this are the public fields that contain the information from the XML file */
	public static boolean newborn = false;
	public static int startingYear = 2000;
	public static int numberOfYears = 10;
	public static int simPopSize = 10;
	public static int minAge = 0;
	public static int maxAge = 95;
	public static int timeStep = 1;
	public static int randomSeed = 9;
	public static boolean resultType = false;
	public static String popFileName = "popFileName";
	/* now scenario info read */
	public static ArrayList<ScenInfo> scenInfo;

	public class ScenInfo {

		String name;
		int minAge;
		int maxAge;
		int gender;
		String transFileName;
		String prevFileName;
		public float rate;

		public void ScenInfo() {
		}
	};

	public static ArrayList<DisInfo> disInfo;

	public class DisInfo {
		int number;
		String name;
		String prevFileName;
		String incFileName;
		String emFileName;
		String dalyFileName;

		public void DisInfo() {
		}
	};

	public static ArrayList<RRInfo> rrInfo;

	public class RRInfo {
		int number;
		String from;
		String to;
		Boolean fromIsDisease = false;
		String rrFileName;
		float[][] rrDataDis = null;
		float[][] rrDataCont = null;
		float[][][] rrDataCat = null;
		float[][] rrDataBegin = null;
		float[][] rrDataEnd = null;
		float[][] rrDataAlfa = null;

		public void RRInfo() {
		}
	};

	/*
	 * the names of the directories en standard filenames containing the
	 * information on resp. riskfactors, diseases and relative risks
	 */
	private static final String riskFactorDir = "riskfactors";
	private static final String diseasesDir = "diseases";
	private static final String populationDir = "populations";
	private static final String simulationDir = "Simulations";
	private static final String referenceDataDir = "Reference_Data";

	private static final String sizeXMLname = "size.xml";
	private static final String allcauseXMLname = "overallmortality.xml";
	private static final String newbornXMLname = "newborns.xml";
	private static final String totDalyXMLname = "overalldalyweights.xml";
	private static final String riskfactorXMLname = "configuration.xml";
	private static final String classesXMLname = "classes.xml";
	private static final String transMatXMLname = "transitionmatrix.xml";
	private static final String transDriftXMLname = "transitiondrift.xml";
	private static final String catPrevXMLname = "categoricalprevalence.xml";
	private static final String contPrevXMLname = "continuousprevalence.xml";
	private static final String durationXMLname = "durationdistribution.xml";
	private static final String allcauseRRcatXMLname = "categoricalrelriskofdeath.xml";
	private static final String allcauseRRcontXMLname = "continuousrelriskofdeath.xml";
	private static final String allcauseRRcompoundXMLname = "compoundrelriskofdeath.xml";
	private static final String prefixRRcont = "continuousrelriskfrom";
	private static final String prefixRRcat = "categoricalrelriskfrom";
	private static final String prefixRRcompound = "compoundrelriskfrom.xml";
	private static final String prefixRRdis = "relriskfrom";

	public InputDataFactory(String simName) throws DynamoConfigurationException {
		;
		// temporary for testing //;

		try {
			configuration = new XMLConfiguration(baseDir + File.separator
					+ simulationDir + File.separator + simName + File.separator
					+ "configuration.xml");
		} catch (ConfigurationException e) {

			e.printStackTrace();
			throw new DynamoConfigurationException("error reading file: "
					+ baseDir + File.separator + simulationDir + File.separator
					+ simName + File.separator + "configuration.xml"
					+ " with message: " + e.getMessage());
		}
		/* now read in all the data in the sequence of the user data document */
		String yesno = getName(newbornLabel);
		if (yesno.compareToIgnoreCase("no") == 0)
			newborn = false;
		else
			newborn = true;

		startingYear = getInteger(startingYearLabel);
		numberOfYears = getInteger(numberOfYearsLabel);
		simPopSize = getInteger(simPopSizeLabel);
		minAge = getInteger(minAgeLabel);
		maxAge = getInteger(maxAgeLabel);
		timeStep = getInteger(timeStepLabel);
		if (timeStep != 1)
			log.fatal("timestep given in configuration is " + timeStep
					+ ". In Dynamo timestep should be "
					+ " 1, thus it is changed to 1 ");
		timeStep = 1;
		randomSeed = getInteger(randomSeedLabel);
		String resultTypeS = getName(resultTypeLabel);
		if (resultTypeS.compareToIgnoreCase("aggregated") == 0)
			resultType = false;
		else
			resultType = true;

		popFileName = getName(popFileNameLabel);

		/*
		 * now read the parts that are from the subtabs (scenario, disease,
		 * riskfactor,RR)
		 */

		ConfigurationNode rootNode = configuration.getRootNode();

		if (((XMLConfiguration) configuration).getRootElementName() != mainTagLabel)
			throw new DynamoConfigurationException(" Tagname " + mainTagLabel
					+ " expected in main simulation configuration file "
					+ "but found tag "
					+ ((XMLConfiguration) configuration).getRootElementName());
		List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
				.getChildren();
		boolean scenPresent = false;
		boolean disPresent = false;
		boolean riskfactorPresent = false;
		boolean rrPresent = false;
		for (ConfigurationNode rootChild : rootChildren) {

			if (rootChild.getName() == scenTagLabel)
				scenPresent = handleScenarioInfo(rootChild);
			if (rootChild.getName() == disTagLabel)
				disPresent = handleDiseaseInfo(rootChild);
			if (rootChild.getName() == riskfactorTagLabel)
				riskfactorPresent = handleRiskfactorInfo(rootChild);
			if (rootChild.getName() == rrTagLabel)
				rrPresent = handleRRInfo(rootChild);
		}

		if (!scenPresent)
			throw new DynamoConfigurationException(
					" no valid information present " + "for scenarios ");
		if (!disPresent)
			throw new DynamoConfigurationException(
					" no valid information present " + "for diseases ");

		if (!riskfactorPresent)
			throw new DynamoConfigurationException(
					" no valid information present " + "for riskfactors ");

		if (!rrPresent)
			throw new DynamoConfigurationException(
					" no valid information present " + "for relative risks ");

	}

	/**
	 * @param node
	 * @return flag whether the info was read successfully
	 * @throws DynamoConfigurationException
	 */
	@SuppressWarnings("unchecked")
	private boolean handleScenarioInfo(ConfigurationNode node)
			throws DynamoConfigurationException {

		boolean flag = false;

		scenInfo = new ArrayList<ScenInfo>();

		/* loop over scenarios */
		List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) node
				.getChildren();
		for (ConfigurationNode rootChild : rootChildren) {
			if (rootChild.getName() != scenTagSubLabel)
				throw new DynamoConfigurationException("no " + scenTagSubLabel
						+ " where expected in XML file from window 1");
			/* loop over elements within scenario */
			List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
					.getChildren();
			ScenInfo scenData = new ScenInfo();
			/*
			 * obsolete: to read labels, not needed anymore List attributes =
			 * rootChild.getAttributes("label");
			 * 
			 * 
			 * 
			 * scenData.number = Integer .parseInt((String) ((ConfigurationNode)
			 * attributes .get(0)).getValue()); else throw new
			 * DynamoConfigurationException(
			 * "no label with number for scenario in window 1");
			 */
			boolean namePresent = false;
			boolean ratePresent = false;
			boolean minPresent = false;
			boolean maxPresent = false;
			boolean gPresent = false;
			boolean transPresent = false;
			boolean prevPresent = false;

			for (ConfigurationNode leafChild : leafChildren) {

				if (leafChild.getName() == scenarioNameLabel) {
					scenData.name = getString(leafChild, scenarioNameLabel);
					namePresent = true;
				}
				if (leafChild.getName() == scenarioSuccessRateLabel) {
					scenData.rate = getFloat(leafChild,
							scenarioSuccessRateLabel);
					ratePresent = true;
				}
				if (leafChild.getName() == targetMinAgeLabel) {
					scenData.minAge = getInteger(leafChild, targetMinAgeLabel);
					minPresent = true;
				}
				if (leafChild.getName() == targetMaxAgeLabel) {
					scenData.maxAge = getInteger(leafChild, targetMaxAgeLabel);
					maxPresent = true;
				}
				if (leafChild.getName() == targetGenderLabel) {
					scenData.gender = getInteger(leafChild, targetGenderLabel);
					gPresent = true;
				}
				if (leafChild.getName() == alternativeTransFileLabel) {
					scenData.transFileName = getString(leafChild,
							alternativeTransFileLabel);
					transPresent = true;
				}
				if (leafChild.getName() == alternativePrevFileLabel) {
					scenData.prevFileName = getString(leafChild,
							alternativePrevFileLabel);
					prevPresent = true;
				}

			}

			if (namePresent && ratePresent && minPresent && maxPresent
					&& gPresent && transPresent && prevPresent)
				scenInfo.add(scenData);
			else
				throw new DynamoConfigurationException(
						"incomplete information for scenario in configuration file");

			flag = true; /* at least one scenario has been success full read */
		}// end loop over scenarios

		return flag;
	}

	/**
	 * 
	 * @param node
	 * @return flag whether the info was read successfully
	 * @throws DynamoConfigurationException
	 */
	private boolean handleDiseaseInfo(ConfigurationNode node)
			throws DynamoConfigurationException {

		boolean flag = false;
		disInfo = new ArrayList<DisInfo>();

		/* loop over scenarios */
		List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) node
				.getChildren();
		for (ConfigurationNode rootChild : rootChildren) {
			if (rootChild.getName() != disTagSubLabel)
				throw new DynamoConfigurationException("no " + disTagSubLabel
						+ " where expected in XML file from window 1");
			/* loop over elements within scenario */
			List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
					.getChildren();
			DisInfo disData = new DisInfo();
			/*
			 * obsolete: to read labels, not needed anymore List attributes =
			 * rootChild.getAttributes("label"); // TODO als er geen valid
			 * attribute is if (!attributes.isEmpty()) disData.number = Integer
			 * .parseInt((String) ((ConfigurationNode) attributes
			 * .get(0)).getValue());
			 * 
			 * else throw new DynamoConfigurationException(
			 * "no label with number for diseases in overall configuration file"
			 * );
			 */
			boolean namePresent = false;
			boolean emPresent = false;
			boolean incPresent = false;
			boolean prevPresent = false;
			boolean dalyPresent = false;

			for (ConfigurationNode leafChild : leafChildren) {

				if (leafChild.getName() == diseaseNameLabel) {
					disData.name = getString(leafChild, diseaseNameLabel);
					namePresent = true;
				}
				if (leafChild.getName() == diseasePrevFileLabel) {
					disData.prevFileName = getString(leafChild,
							diseasePrevFileLabel);
					prevPresent = true;
				}

				if (leafChild.getName() == diseaseIncFileLabel) {
					disData.incFileName = getString(leafChild,
							diseaseIncFileLabel);
					incPresent = true;
				}
				if (leafChild.getName() == diseaseExcessMortFileLabel) {
					disData.emFileName = getString(leafChild,
							diseaseExcessMortFileLabel);
					emPresent = true;
				}
				if (leafChild.getName() == diseaseDalyWeightLabel) {
					disData.dalyFileName = getString(leafChild,
							diseaseDalyWeightLabel);
					dalyPresent = true;
				}

			}// end loop over disease elements

			if (namePresent && prevPresent && incPresent && emPresent
					&& dalyPresent)
				disInfo.add(disData);
			else
				throw new DynamoConfigurationException(
						"incomplete information for disease in overall configuration file");

			flag = true; /* at least one scenario has been success full read */

		} // end loop over disease

		return flag;
	}

	/**
	 * @param node
	 * @return flag whether the info was read successfully
	 * @throws DynamoConfigurationException
	 */
	private boolean handleRiskfactorInfo(ConfigurationNode node)
			throws DynamoConfigurationException {
		// TODO Auto-generated method stub
		boolean flag = false;
		List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) node
				.getChildren();
		String type = null;
		boolean namePresent = false;
		boolean typePresent = false;
		boolean prevPresent = false;
		boolean transPresent = false;
		boolean rrPresent = false;
		for (ConfigurationNode rootChild : rootChildren) {
			if (rootChild.getName() == riskFactorNameLabel) {
				riskFactorName = getString(rootChild, riskFactorNameLabel);
				namePresent = true;
			}
			if (rootChild.getName() == riskFactorTypeLabel) {
				type = getString(rootChild, riskFactorTypeLabel);
				if (type.compareToIgnoreCase("compound") == 0)
					riskFactorType = 3;
				else if (type.compareToIgnoreCase("continuous") == 0)
					riskFactorType = 2;
				else if (type.compareToIgnoreCase("categorical") == 0)
					riskFactorType = 1;
				else
					throw new DynamoConfigurationException(
							"no valid riskFactorType found but found  " + type
									+ " in XML file from window 1");
				typePresent = true;

			}
			/*
			 * redundant and deleted; if (rootChild.getName() ==
			 * riskFactorPrevFileLabel) { riskFactorPrevFileName =
			 * getString(rootChild, riskFactorPrevFileLabel); prevPresent =
			 * true; }
			 */
			if (rootChild.getName() == riskFactorTransFileLabel) {
				riskFactorTransDirName = getString(rootChild,
						riskFactorTransFileLabel);
				transPresent = true;
			}
			/*
			 * redundant and thus deleted if (rootChild.getName() ==
			 * riskFactorRRforDeathFileLabel) { riskFactorRRforDeathFileName =
			 * getString(rootChild, riskFactorRRforDeathFileLabel); rrPresent =
			 * true; }
			 */
		}
		if (namePresent && typePresent /* && prevPresent */&& transPresent
		/* && rrPresent */)
			flag = true;
		return flag;
	}

	/**
	 * @param node
	 * @return flag whether the info was read successfully
	 * @throws DynamoConfigurationException
	 */
	private boolean handleRRInfo(ConfigurationNode node)
			throws DynamoConfigurationException {

		boolean flag = false;

		rrInfo = new ArrayList<RRInfo>();

		/* loop over scenarios */
		List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) node
				.getChildren();
		for (ConfigurationNode rootChild : rootChildren) {
			if (rootChild.getName() != rrSubTagLabel)
				throw new DynamoConfigurationException(
						"no "
								+ rrSubTagLabel
								+ " where expected in configuration.XML file from window 1");
			/* loop over elements within scenario */
			List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
					.getChildren();
			RRInfo rrData = new RRInfo();

			/*
			 * obsolete: to read labels, not needed anymore
			 * 
			 * List attributes = rootChild.getAttributes("label");
			 * 
			 * if (!attributes.isEmpty()) rrData.number = Integer
			 * .parseInt((String) ((ConfigurationNode) attributes
			 * .get(0)).getValue());
			 * 
			 * else throw new DynamoConfigurationException(
			 * "no label with number for relative risks in configuration.XML file from window 1"
			 * );
			 */
			boolean fromPresent = false;
			boolean toPresent = false;
			boolean filePresent = false;

			for (ConfigurationNode leafChild : leafChildren) {

				if (leafChild.getName() == isRRfromLabel) {
					rrData.from = getString(leafChild, isRRfromLabel);
					fromPresent = true;
				}
				if (leafChild.getName() == isRRtoLabel) {
					rrData.to = getString(leafChild, isRRtoLabel);
					toPresent = true;
				}
				if (leafChild.getName() == isRRfileLabel) {
					rrData.rrFileName = getString(leafChild, isRRfileLabel);
					filePresent = true;
				}

			}// end loop over RRelements

			if (fromPresent && toPresent && filePresent)
				rrInfo.add(rrData);
			else
				throw new DynamoConfigurationException(
						"incomplete information for relative risk in configuration.XML file");

			flag = true; /* at least one relative risk has been success full read */

		} // end loop over RR's

		return flag;
	}

	public String getName(String Label)

	throws DynamoConfigurationException {

		try {
			String name = configuration.getString(Label);
			if (name == null) {
				throw new DynamoConfigurationException("empty " + Label
						+ " in XML file from window 1");
			}
			return name;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("no " + Label
					+ " in XML file from window 1");
		}

	}

	public String getName(String Label, HierarchicalConfiguration config)

	throws DynamoConfigurationException {

		try {
			String name = config.getString(Label);
			if (name == null) {
				throw new DynamoConfigurationException("empty " + Label
						+ " in XML file from window 1");
			}
			return name;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("no " + Label
					+ " in XML file from window 1");
		}

	}

	public String getString(ConfigurationNode node, String Label)

	throws DynamoConfigurationException {

		try {
			String returnString;
			String value = (String) node.getValue();

			if (value == null) {
				throw new DynamoConfigurationException("empty " + Label
						+ " in XML file from window 1");
			}

			returnString = (String) value;
			return returnString;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("unreadable " + Label
					+ " in XML file from window 1");
		}

	}

	public float getFloat(ConfigurationNode node, String Label)

	throws DynamoConfigurationException {

		try {
			float returnFloat;
			String value = (String) node.getValue();

			if (value == null) {
				throw new DynamoConfigurationException("empty " + Label
						+ " in XML file from window 1");
			}
			if (value.toString() == null)
				throw new DynamoConfigurationException("no String value for  "
						+ Label + " in XML file from window 1");
			returnFloat = Float.parseFloat(value);
			return returnFloat;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("unreadable " + Label
					+ " in XML file from window 1");
		}

	}

	public int getInteger(ConfigurationNode node, String Label)

	throws DynamoConfigurationException {

		try {
			int returnInt;
			String value = (String) node.getValue();

			if (value == null) {
				throw new DynamoConfigurationException("empty " + Label
						+ " in XML file from window 1");
			}
			if (value.toString() == null)
				throw new DynamoConfigurationException("no String value for  "
						+ Label + " in XML file from window 1");
			returnInt = Integer.parseInt(value);
			return returnInt;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("unreadable " + Label
					+ " in XML file from window 1");
		}

	}

	public int getInteger(String Label)

	throws DynamoConfigurationException {

		try {
			int value = configuration.getInt(Label);
			if (value < 0) {
				throw new DynamoConfigurationException("zero or negative "
						+ Label + " in XML file from window 1");
			}

			return value;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("no " + Label
					+ " in XML file from window 1");
		}

	}

	public int getInteger(String Label, HierarchicalConfiguration config)

	throws DynamoConfigurationException {

		try {
			int value = config.getInt(Label);
			if (value <= 0) {
				throw new DynamoConfigurationException("zero or negative "
						+ Label + " in XML file from window 1");
			}

			return value;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("no " + Label
					+ " in XML file from window 1");
		}

	}

	public float getFloat(String Label, HierarchicalConfiguration config)

	throws DynamoConfigurationException {

		try {
			float value = config.getFloat(Label);
			if (value <= 0) {
				throw new DynamoConfigurationException("zero or negative "
						+ Label + " in XML file from window 1");
			}

			return value;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("no " + Label
					+ " in XML file from window 1");
		}

	}

	public void addPopulationInfoToInputData(String simName,
			InputData inputData, ScenarioInfo scenarioInfo)
			throws DynamoConfigurationException {
		// add general information

		scenarioInfo.yearsInRun = numberOfYears;
		scenarioInfo.startYear = startingYear;

		scenarioInfo.details = resultType;
		;

		String sizeName = baseDir + File.separator + referenceDataDir
				+ File.separator + populationDir + File.separator + popFileName
				+ File.separator + sizeXMLname;
		String newbornName = baseDir + File.separator + referenceDataDir
				+ File.separator + populationDir + File.separator + popFileName
				+ File.separator + newbornXMLname;
		String dalyName = baseDir + File.separator + referenceDataDir
				+ File.separator + populationDir + File.separator + popFileName
				+ File.separator + totDalyXMLname;
		String mortName = baseDir + File.separator + referenceDataDir
				+ File.separator + populationDir + File.separator + popFileName
				+ File.separator + allcauseXMLname;

		scenarioInfo.populationSize = factory.manufactureOneDimArray(sizeName,
				"populationsize", "size", "number", false);
		scenarioInfo.overallDalyWeight = factory.manufactureOneDimArray(
				dalyName, "overalldalyweights", "weight", "percent", false);
		inputData.mortTot = factory.manufactureOneDimArray(mortName,
				"overallmortality", "mortality", false);
		// to do: newborns

	}

	// TODO inlezen info voor continue: refClassCont,
	// riskDistribution,meanRisk,stdDevRisk,skewnessRisk
	// TODO inlezen info voor compound : indexDuurClass, prevRisk

	public void addScenarioInfoToScenarioData(String simName,
			ScenarioInfo scenarioInfo) throws DynamoConfigurationException {
		/* initialize the arrays in scenarioInfo */
		scenarioInfo.setNScenarios(scenInfo.size());
		scenarioInfo.scenarioNames = new String[scenInfo.size()];
		scenarioInfo.minAge = new float[scenInfo.size()];
		scenarioInfo.maxAge = new float[scenInfo.size()];
		scenarioInfo.succesrate = new float[scenInfo.size()];
		scenarioInfo.inMen = new boolean[scenInfo.size()];
		scenarioInfo.inWomen = new boolean[scenInfo.size()];
		scenarioInfo.setTransitionType(new boolean[scenInfo.size()]);
		scenarioInfo.setInitialPrevalenceType(new boolean[scenInfo.size()]);
		scenarioInfo.zeroTransition = new boolean[scenInfo.size()];
		scenarioInfo.newPrevalence = new float[scenInfo.size()][96][2][];
		
		scenarioInfo.alternativeTransitionMatrix = new float[scenInfo.size()][][][][];
		
		//TODO continue
		
		for (int scen = 0; scen < scenInfo.size(); scen++) {

			scenarioInfo.scenarioNames[scen] = scenInfo.get(scen).name;
			scenarioInfo.minAge[scen] = scenInfo.get(scen).minAge;
			scenarioInfo.maxAge[scen] = scenInfo.get(scen).maxAge;
			scenarioInfo.succesrate[scen] = scenInfo.get(scen).rate;
			scenarioInfo.inMen[scen] = true;
			scenarioInfo.inWomen[scen] = true;
			if (scenInfo.get(scen).gender == 0)
				scenarioInfo.inWomen[scen] = false;
			if (scenInfo.get(scen).gender == 1)
				scenarioInfo.inMen[scen] = false;
			
			/* reading and handling alternative prevalence information */
			
			if (scenInfo.get(scen).prevFileName.compareToIgnoreCase("none") == 0)
				scenarioInfo.getInitialPrevalenceType()[scen] = false;
			else {
				scenarioInfo.getInitialPrevalenceType()[scen] = true;

				String completePrevFileName = baseDir + File.separator
						+ referenceDataDir + File.separator + riskFactorDir
						+ File.separator + riskFactorName + File.separator
						+ scenInfo.get(scen).prevFileName + ".xml";
				scenarioInfo.newPrevalence[scen] = factory
						.manufactureTwoDimArray(completePrevFileName,
								"riskfactorprevalences_categorical",
								"prevalence", "cat", "percent");
			}
			/* reading and handling transition matrix info */
			
			if (scenInfo.get(scen).transFileName.compareToIgnoreCase("none") == 0)
				scenarioInfo.getTransitionType()[scen] = false;
			else {
				scenarioInfo.getTransitionType()[scen] = true;
				String completeTransFileName = baseDir + File.separator
						+ referenceDataDir + File.separator + riskFactorDir
						+ File.separator + riskFactorName + File.separator
						+ riskFactorTransDirName + File.separator
						+ scenInfo.get(scen).transFileName + ".xml";
				readTransitionData(completeTransFileName, null, scenarioInfo, 0);
			}

		}

	}

	/**
	 * Method that adds the riskfactor information to the objects InputData and
	 * ScenarioInfo, including reading the data from the user XML files
	 * 
	 * @param inputData
	 *            : object InputData to which the information is added
	 * @param scenarioInfo
	 *            : object ScenarioInfo to which the informaiton is added
	 * @throws DynamoConfigurationException
	 */

	public void addRiskFactorInfoToInputData(InputData inputData,
			ScenarioInfo scenarioInfo) throws DynamoConfigurationException {

		// read and add risktype

		if (riskFactorType == 1 || riskFactorType == 2 || riskFactorType == 3)
			inputData.riskType = riskFactorType;
		else
			throw new DynamoConfigurationException(
					"riskType not equal to 1,2 or 3, but  " + riskFactorType);

		// make file name for riskfactor configurationfile
		scenarioInfo.riskType = riskFactorType;
		String configFileName;

		configFileName = baseDir + File.separator + referenceDataDir
				+ File.separator + riskFactorDir + File.separator
				+ riskFactorName + File.separator + riskfactorXMLname;
		HierarchicalConfiguration config;
		try {
			config = new XMLConfiguration(configFileName);
		} catch (ConfigurationException e) {

			e.printStackTrace();
			throw new DynamoConfigurationException(
					"XML error encountered with message: " + e.getMessage());
		}

		/* now read in all the data in the sequence of the user data document */

		String type = getName("type", config);
		if ((riskFactorType == 1 && type.compareToIgnoreCase("categorical") != 0)
				|| (riskFactorType == 2 && type
						.compareToIgnoreCase("continous") != 0)
				|| (riskFactorType == 3 && type.compareToIgnoreCase("compound") != 0))
			throw new DynamoConfigurationException(
					"riskfactor Type in overall configuration file ("
							+ riskFactorType
							+ ") does not agree with type given in riskfactor configuration file ("
							+ type + ")");
		if (riskFactorType == 2)
			inputData.refClassCont = getFloat("referencevalue", config);
		else
			inputData.refClassCont = 0;
		if (riskFactorType == 3)
			inputData.indexDuurClass = getInteger("durationclass", config);
		else
			inputData.indexDuurClass = 0;
		if (riskFactorType != 2)
			scenarioInfo
					.setReferenceClass(getInteger("referenceclass", config));
		else
			scenarioInfo.setReferenceClass(0);

		/*
		 * now read the class names
		 */

		ConfigurationNode rootNode = config.getRootNode();

		if (((XMLConfiguration) config).getRootElementName() != "riskfactor")
			throw new DynamoConfigurationException(" Tagname " + "riskfactor"
					+ " expected in main simulation configuration file "
					+ "but found tag "
					+ ((XMLConfiguration) config).getRootElementName());

		if (riskFactorType != 2) {
			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();

			for (ConfigurationNode rootChild : rootChildren) {
				// level: classes
				if (rootChild.getName() == "classes") {
					int currentClass = 0;
					List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
							.getChildren();// level: class
					scenarioInfo.riskClassnames = new String[leafChildren
							.size()];
					int[] index = new int[leafChildren.size()];
					int[] index2 = new int[leafChildren.size()];
					String[] names = new String[leafChildren.size()];

					for (ConfigurationNode leafChild : leafChildren) {
						List<ConfigurationNode> elements = (List<ConfigurationNode>) leafChild
								.getChildren();
						for (ConfigurationNode element : elements) {
							if (element.getName() == "index")
								index[currentClass] = getInteger(element,
										"index");
							index2[currentClass] = index[currentClass]; // make
							// copy
							// to
							// sort
							// later
							if (element.getName() == "name")
								names[currentClass] = getString(element, "name");
						}// end class
						currentClass++;
					}
					// now make a sorted array from this;
					// also make the duration class /reference class number
					// equal to the new numbering
					// TODO test of dit werkt
					Arrays.sort(index2);
					for (int i = 0; i < index.length; i++) {
						if (i > 0)
							if (index2[i] == index2[i - 1])
								throw new DynamoConfigurationException(
										"double class index "
												+ +i
												+ " in riskfactor configuration file  ");
						for (int j = 0; j < index.length; j++) {
							if (index2[i] == index[j]) {
								scenarioInfo.riskClassnames[i] = names[j];
								if (inputData.indexDuurClass == index[j])
									inputData.indexDuurClass = i;
								if (scenarioInfo.getReferenceClass() == index[j])
									scenarioInfo.setReferenceClass(i);

								break;
							}
						}
					}

					if (index2[index.length - 1] - index2[0] != index.length - 1)
						throw new DynamoConfigurationException(
								" class numbers missing "
										+ " in riskfactor configuration file  ");
				}

			}
			//
			/* read transition information */
			//
			//
			if (riskFactorType != 2)
				configFileName = baseDir + File.separator + referenceDataDir
						+ File.separator + riskFactorDir + File.separator
						+ riskFactorName + File.separator
						+ riskFactorTransDirName + File.separator
						+ transMatXMLname;
			else
				configFileName = baseDir + File.separator + referenceDataDir
						+ File.separator + riskFactorDir + File.separator
						+ riskFactorName + File.separator + transDriftXMLname;

			readTransitionData(configFileName, inputData, null, 0);

			//
			/* read prevalence information */
			//
			//
			//
			/* first for categorical/compound */
			//
			if (riskFactorType != 2) {
				configFileName = baseDir + File.separator + referenceDataDir
						+ File.separator + riskFactorDir + File.separator
						+ riskFactorName + File.separator + catPrevXMLname;

				inputData.setPrevRisk(factory.manufactureTwoDimArray(
						configFileName, "riskfactorprevalences_categorical",
						"prevalence", "cat", "percent"), true);
				scenarioInfo.oldPrevalence = inputData.getPrevRisk();
			} else {
				configFileName = baseDir + File.separator + referenceDataDir
						+ File.separator + riskFactorDir + File.separator
						+ riskFactorName + File.separator + contPrevXMLname;

				inputData.setMeanRisk(factory.manufactureOneDimArray(
						configFileName, "riskfactorprevalences_continuous",
						"prevalence", "mean", true));
				inputData.setStdDevRisk(factory.manufactureOneDimArray(
						configFileName, "riskfactorprevalences_continuous",
						"prevalence", "standarddeviation", true));
				inputData.setSkewnessRisk(factory.manufactureOneDimArray(
						configFileName, "riskfactorprevalences_continuous",
						"prevalence", "skewness", true));
				float[][] skewness = inputData.getSkewnessRisk();
				boolean normal = true;
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++)
						if (skewness[a][g] != 0)
							normal = false;

				if (normal)
					inputData.setRiskDistribution("Normal");
				else
					inputData.setRiskDistribution("LogNormal");
			}
			;
			if (riskFactorType == 3) {
				configFileName = baseDir + File.separator + referenceDataDir
						+ File.separator + riskFactorDir + File.separator
						+ riskFactorName + File.separator + durationXMLname;

				inputData.setPrevRisk(factory.manufactureTwoDimArray(
						configFileName, "riskfactorprevalences_duration",
						"prevalence", "duration", "percent"));
			}
		}

		//
		/* now read RR for all cause mortality */
		//
		//
		//
		/* first for categorical/compound */

		float[][][] data3dim = new float[96][2][1];
		float[][] data2dim = new float[96][2];
		for (int a = 0; a < 96; a++)
			for (int g = 0; g < 2; g++) {
				data3dim[a][g][0] = 1;
				data2dim[a][g] = 1;

			}

		if (riskFactorType == 1) {
			configFileName = baseDir + File.separator + referenceDataDir
					+ File.separator + riskFactorDir + File.separator
					+ riskFactorName + File.separator + allcauseRRcatXMLname;

			inputData.setRelRiskMortCat(factory.manufactureTwoDimArray(
					configFileName, "relrisksfordeath_categorical",
					"relriskfordeath", "cat", "value"));
			inputData.setRelRiskMortCont(data2dim);

		}
		if (riskFactorType == 2) {
			configFileName = baseDir + File.separator + referenceDataDir
					+ File.separator + riskFactorDir + File.separator
					+ riskFactorName + File.separator + allcauseRRcontXMLname;

			inputData.setRelRiskMortCont(factory.manufactureOneDimArray(
					configFileName, "relrisksfordeath_continuous",
					"relrisksfordeath", "value", false));
			inputData.setRelRiskMortCat(data3dim);
		}
		if (riskFactorType == 3) {
			configFileName = baseDir + File.separator + referenceDataDir
					+ File.separator + riskFactorDir + File.separator
					+ riskFactorName + File.separator
					+ allcauseRRcompoundXMLname;
			// TODO: other RR's for compound
			;
			inputData.setRelRiskMortCont(data2dim);
		}

	}

	/**
	 * @param inputData
	 *            : object with input data to which the transition matrix is
	 *            added this parameter should be null if transition matrix
	 *            should be added to the scenario info
	 * @param scenInfo
	 *            object with scenario info to which the transition matrix
	 *            should be added * this parameter should be null if transition
	 *            matrix should be added to the input data (= transtionmatrix
	 *            for reference scenario
	 * 
	 * @throws DynamoConfigurationException
	 */
	private void readTransitionData(String configFileName, InputData inputData,
			ScenarioInfo scenInfo, int scenNumber)
			throws DynamoConfigurationException {

		HierarchicalConfiguration config;
		ConfigurationNode rootNode;
		if (riskFactorType != 2) {
			try {
				config = new XMLConfiguration(configFileName);
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DynamoConfigurationException(
						"reading error encountered when reading file: "
								+ configFileName + " with message: "
								+ e.getMessage());
			}
			rootNode = config.getRootNode();
			if (((XMLConfiguration) config).getRootElementName() == "transitionmatrix_zero") {
				if (inputData != null)
					inputData.transType = 0;
				else if (scenInfo != null)
					scenInfo.zeroTransition[scenNumber] = true;
				else
					throw new DynamoConfigurationException(
							"Both scenInfo and Input data are null in method"
									+ "readTransitionData, so information can not be added to anything");
			}

			else if (((XMLConfiguration) config).getRootElementName() == "transitionmatrix_netto") {
				if (inputData != null)
					inputData.transType = 1;

				else
					throw new DynamoConfigurationException(
							"netto transition file not possbile for alternative scenario."
									+ "For alternative scenario's one should enter an explicite file");

			}

			else if (((XMLConfiguration) config).getRootElementName() == "transitionmatrix") {
				if (inputData != null) {
					inputData.transType = 2;

					inputData.transitionMatrix = factory
							.manufactureThreeDimArray(configFileName,
									"transitionmatrix", "transition", "from",
									"to", "percent");
				}

				else if (scenInfo != null)
					scenInfo.alternativeTransitionMatrix[scenNumber] = factory
							.manufactureThreeDimArray(configFileName,
									"transitionmatrix", "transition", "from",
									"to", "percent");
				else
					throw new DynamoConfigurationException(
							"Both scenInfo and Input data are null in method"
									+ "readTransitionData, so information can not be added to anything");

			}

			else
				throw new DynamoConfigurationException(" Tagname "
						+ "transitionmatrix (_zero,_netto) "
						+ " expected in main simulation configuration file "
						+ "but found tag "
						+ ((XMLConfiguration) config).getRootElementName());
		}
		/* second for continuous */
		//
		else {

			try {
				config = new XMLConfiguration(configFileName);
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DynamoConfigurationException(
						"error encountered while reading file: "
								+ configFileName + " with message: "
								+ e.getMessage());
			}
			rootNode = config.getRootNode();

			if (((XMLConfiguration) config).getRootElementName() == "transitiondrift_zero") {
				inputData.transType = 0;
			}

			else if (((XMLConfiguration) config).getRootElementName() == "transitiondrift_netto") {
				inputData.transType = 1;
			}

			else if (((XMLConfiguration) config).getRootElementName() == "transitiondrift") {
				inputData.transType = 2;
				inputData.meanDrift = factory.manufactureOneDimArray(
						configFileName, "transitionmatrix", "transition",
						"mean", true);
				inputData.stdDrift = factory.manufactureOneDimArray(
						configFileName, "transitionmatrix", "transition",
						"standarddevivation", true);
				inputData.offsetDrift = factory.manufactureOneDimArray(
						configFileName, "transitionmatrix", "transition",
						"skewness", true);
			}

			else
				throw new DynamoConfigurationException(" Tagname "
						+ "transitionmatrix (_zero,_netto) "
						+ " expected in main simulation configuration file "
						+ "but found tag "
						+ ((XMLConfiguration) config).getRootElementName());
		}
	}

	/**
	 * This method adds the disease info from the input XML files to the objects
	 * "InputData" and "ScenarioInfo" that are InputData is needed by
	 * "ModelParameters", and Scenario info is needed by
	 * "DynamoSimulationConfigurationFactory and the objects that do the
	 * post-processing (to be written)
	 * 
	 * @param inputData
	 *            : inputData object to be filled. NB: this object already
	 *            should be filled with risk factor data first, if this does not
	 *            seem to be so, the method addRiskFactorInfoToInputData is
	 *            invoked.
	 * @param scenarioInfo
	 *            : object with scenario information
	 * @throws DynamoInconsistentDataException
	 * @throws nl.rivm.emi.cdm.rules.update.dynamo.DynamoConfigurationException
	 * @throws DynamoConfigurationException
	 */

	public void addDiseaseInfoToInputData(InputData inputData,
			ScenarioInfo scenarioInfo) throws DynamoInconsistentDataException,
			DynamoConfigurationException {

		/*
		 * first set the number of diseases NB: diseases with cured fraction are
		 * regarded as a single disease here They are split up into two
		 * "diseases" in ModelParameters
		 */
		if ((riskFactorType == 1 || riskFactorType == 3)
				&& inputData.getPrevRisk() == null) {
			addRiskFactorInfoToInputData(inputData, scenarioInfo);
			if (inputData.getPrevRisk() == null)
				throw new DynamoInconsistentDataException(
						"no valid riskfactor information availlable in configuration ");
		}
		if ((riskFactorType == 2) && inputData.getMeanRisk() == null) {
			addRiskFactorInfoToInputData(inputData, scenarioInfo);
			if (inputData.getMeanRisk() == null)
				throw new DynamoInconsistentDataException(
						"no valid riskfactor information availlable in configuration ");
		}

		int nDiseases = disInfo.size();
		inputData.setNDisease(nDiseases);
		/*
		 * identification of diseases throughout is through their name as the
		 * order is changed by creating clusters therefore, put disease names in
		 * a array to simplify coding in this method
		 */
		String[] diseaseName = new String[nDiseases];
		for (int d = 0; d < nDiseases; d++) {
			diseaseName[d] = disInfo.get(d).name;
			// put disease names in a array to simplify coding in this method
		}

		/*
		 * flag which relative risks are for disease on disease, and put them in
		 * an array array size to large, but does not matter because temporary
		 */

		String[] isRRfrom = new String[nDiseases * nDiseases];
		String[] isRRto = new String[nDiseases * nDiseases];
		int[] rrNumber = new int[nDiseases * nDiseases];
		RRInfo info;
		int currentRR = 0;
		for (int rr = 0; rr < rrInfo.size(); rr++) {
			info = rrInfo.get(rr);

			/*
			 * check if "from" in the RR is a disease
			 */

			for (int d = 0; d < nDiseases; d++) {
				if (diseaseName[d].compareToIgnoreCase(info.from) == 0) {
					isRRfrom[currentRR] = info.from;
					isRRto[currentRR] = info.to;
					rrNumber[currentRR] = rr;
					info.fromIsDisease = true;
					rrInfo.set(rr, info);
					currentRR++;
					break;
				}
			}
		}
		int nRRsDisease = currentRR;
		/* extract disease structure */

		/*
		 * NB cancers must be split up in two diseases this is done elsewhere
		 * 
		 * identification of diseases throughout is through their name as the
		 * order is changed by creating clusters
		 */

		boolean[] causalDisease = new boolean[nDiseases];
		boolean[] dependentDisease = new boolean[nDiseases];

		int[] clusternumber = new int[nDiseases];
		for (int d = 0; d < nDiseases; d++)
			clusternumber[d] = d;
		// check which diseases are causal or dependent (=RR present);
		for (int d = 0; d < nDiseases; d++)
			for (int rr = 0; rr < nRRsDisease; rr++) {
				if (diseaseName[d].compareToIgnoreCase(isRRfrom[rr]) == 0) {
					causalDisease[d] = true;
					for (int d2 = 0; d2 < nDiseases; d2++) {
						if (diseaseName[d2].compareToIgnoreCase(isRRto[rr]) == 0) {
							dependentDisease[d2] = true;
							// now give dependent and causal disease the same
							// (lowest) cluster number;
							if (clusternumber[d] < clusternumber[d2])
								clusternumber[d2] = clusternumber[d];
							if (clusternumber[d2] < clusternumber[d])
								clusternumber[d] = clusternumber[d2];
						}
					}
				}
			}
		// check if not both causal and dependent disease
		for (int d = 0; d < nDiseases; d++) {
			if (causalDisease[d] && dependentDisease[d])
				throw new DynamoInconsistentDataException(
						"Disease "
								+ diseaseName[d]
								+ " is both a cause of another disease and is caused itself by another disease. This is not allowed. Please change this");
		}// now determine clusters ;

		int clusterSum = 0;
		int prevClusterSum = 10000;
		int niter = 0;
		while (clusterSum != prevClusterSum && niter <= nDiseases) {
			prevClusterSum = 0;
			clusterSum = 0;
			for (int d = 0; d < nDiseases; d++) {
				prevClusterSum += clusternumber[d];
				for (int rr = 0; rr < nRRsDisease; rr++) {
					if (diseaseName[d].compareToIgnoreCase(isRRfrom[rr]) == 0) {
						for (int d2 = 0; d2 < nDiseases; d2++) {
							if (isRRto[rr] == diseaseName[d2]) {
								// now give dependent and causal disease the
								// same
								// (lowest) cluster number;
								if (clusternumber[d] < clusternumber[d2])
									clusternumber[d2] = clusternumber[d];
								if (clusternumber[d2] < clusternumber[d])
									clusternumber[d] = clusternumber[d2];
							}
						}
					}
				}// end loop over all rr's related to d
				clusterSum += clusternumber[d];
			}
		}
		;
		// now each cluster has a unique cluster number , but not necessarily
		// aaneensluitend;

		// count clusters and make index;
		int clusterIndex[] = new int[nDiseases]; // clusterIndex gives for each
		// disease the number of the
		// cluster it belongs too;

		clusterIndex[0] = 0;
		int currentIndex = 0;
		boolean hasSameNumber = false;
		/*
		 * first disease keeps old number search the number for the next
		 * diseases d=1 to end
		 */
		for (int d = 1; d < nDiseases; d++) {
			for (int d2 = 0; d2 < d; d2++) {

				if (clusternumber[d] == clusternumber[d2]) {
					clusterIndex[d] = clusterIndex[d2];
					hasSameNumber = true;
					break;
				}

			}
			if (!hasSameNumber) {
				currentIndex++;
				clusterIndex[d] = currentIndex;
			}
		}
		int nClusters = currentIndex + 1;
		/*
		 * count number of diseases in each cluster and number of independent
		 * (=causal) diseases
		 */
		int[] nInCluster = new int[nClusters];
		int[] nCausalInCluster = new int[nClusters];
		for (int c = 0; c < nClusters; c++) {
			nInCluster[c] = 0;
			for (int d = 0; d < nDiseases; d++) {
				if (clusterIndex[d] == c)
					nInCluster[c]++;
				// NB: if disease is not related to any other disease then both
				// causalDisease and dependent disease
				// are false; in this case we make it a causal disease;
				if (clusterIndex[d] == c && !dependentDisease[d])
					nCausalInCluster[c]++;
			}
		}

		// make structure class
		DiseaseClusterStructure[] clusterStructure = new DiseaseClusterStructure[nClusters];
		int nStart = 0;
		for (int c = 0; c < nClusters; c++) {

			String[] DiseaseNamesForCluster = new String[nInCluster[c]];
			int[] indexIndependentDiseasesForCluster = new int[nCausalInCluster[c]];
			/* make array with names of diseases */
			int withinClusterNumber = 0;
			int withinClusterIndependentNumber = 0;
			{
				for (int d = 0; d < nDiseases; d++)
					if (clusterIndex[d] == c) {
						DiseaseNamesForCluster[withinClusterNumber] = diseaseName[d];
						if (!dependentDisease[d]) {
							indexIndependentDiseasesForCluster[withinClusterNumber] = withinClusterIndependentNumber;
							withinClusterIndependentNumber++;
						}
						withinClusterNumber++;
					}
			}

			/*
			 * public DiseaseClusterStructure(String clusterName, int startN,
			 * int N, String[] diseaseNames, int[] NRIndependent)
			 */
			clusterStructure[c] = new DiseaseClusterStructure("cluster" + c,
					nStart, nInCluster[c], DiseaseNamesForCluster,
					indexIndependentDiseasesForCluster);
			nStart += nInCluster[c];
		}
		/*
		 * put data into inputData
		 */
		inputData.setNCluster(nClusters);
		inputData.setClusterStructure(clusterStructure);
		scenarioInfo.structure = clusterStructure;

		/*
		 * now read the data for the diseases
		 * 
		 * class DisInfo { int number; String name; String prevFileName; String
		 * incFileName; String emFileName; String dalyFileName;
		 */
		DisInfo info2;
		float pData[][][];
		float iData[][][];
		float eData[][][];
		float dData[][][];
		float fData[][][];
		float cData[][][];
		double log2 = Math.log(2);

		// first read in the relative risks
		for (int rr = 0; rr < rrInfo.size(); rr++) {

			info = rrInfo.get(rr);

			if (info.fromIsDisease) {
				String configFileName = baseDir + File.separator
						+ referenceDataDir + File.separator + diseasesDir
						+ File.separator + info.to + File.separator
						+ "Relative_Risks" + File.separator + info.rrFileName
						+ info.from + ".xml";
				info.rrDataDis = factory.manufactureOneDimArray(configFileName,
						"rrisksfromdisease", "relativerisk", "value", false);

			} else {
				if (riskFactorType == 2) {
					String configFileName = baseDir + File.separator
							+ referenceDataDir + File.separator + diseasesDir
							+ File.separator + info.to + File.separator
							+ "Relative_Risks" + File.separator
							+ info.rrFileName + info.from + ".xml";
					info.rrDataCont = factory.manufactureOneDimArray(
							configFileName, "rrisksforriskfactor_continuous",
							"relativerisk", "value", false);
				} else if (riskFactorType == 1) {

					String configFileName = baseDir + File.separator
							+ referenceDataDir + File.separator + diseasesDir
							+ File.separator + info.to + File.separator
							+ "Relative_Risks" + File.separator
							+ info.rrFileName + info.from + ".xml";
					info.rrDataCat = factory.manufactureTwoDimArray(
							configFileName, "rrisksforriskfactor_categorical",
							"relativerisk", "cat", "value");
				} else { // TODO if compound type

					String configFileName = baseDir + File.separator
							+ referenceDataDir + File.separator + diseasesDir
							+ File.separator + info.to + File.separator
							+ "Relative_Risks" + File.separator
							+ info.rrFileName + info.from + ".xml";
					info.rrDataCat = factory.manufactureTwoDimArray(
							configFileName, "rrisksforriskfactor_compound",
							"relativerisk", "value", "");
				}

			}
		}// end loop over rr's

		DiseaseClusterData[][][] clusterData = new DiseaseClusterData[96][2][nClusters];

		for (int c = 0; c < nClusters; c++) {
			float[][][][] RRdis = new float[96][2][clusterStructure[c]
					.getNinCluster()][clusterStructure[c].getNinCluster()];

			pData = new float[clusterStructure[c].getNinCluster()][96][2];
			iData = new float[clusterStructure[c].getNinCluster()][96][2];
			eData = new float[clusterStructure[c].getNinCluster()][96][2];
			fData = new float[clusterStructure[c].getNinCluster()][96][2];
			cData = new float[clusterStructure[c].getNinCluster()][96][2];
			dData = new float[clusterStructure[c].getNinCluster()][96][2];

			for (int d = 0; d < clusterStructure[c].getNinCluster(); d++) {
				String thisDisease = clusterStructure[c].diseaseName.get(d);

				/* find the DisInfo element for this disease */

				for (int dTot = 0; dTot < nDiseases; dTot++) {
					info2 = disInfo.get(dTot);
					if (thisDisease == info2.name) {
						// prevalence
						String configFileName = baseDir + File.separator
								+ referenceDataDir + File.separator
								+ diseasesDir + File.separator + thisDisease
								+ File.separator + "Prevalences"
								+ File.separator + info2.prevFileName + ".xml";
						pData[d] = factory.manufactureOneDimArray(
								configFileName, "diseaseprevalences",
								"prevalence", "percent", false);
						configFileName = baseDir + File.separator
								+ referenceDataDir + File.separator
								+ diseasesDir + File.separator + thisDisease
								+ File.separator + "Incidences"
								+ File.separator + info2.incFileName + ".xml";
						iData[d] = factory.manufactureOneDimArray(
								configFileName, "diseaseincidences",
								"incidence", "value", false);
						configFileName = baseDir + File.separator
								+ referenceDataDir + File.separator
								+ diseasesDir + File.separator + thisDisease
								+ File.separator + "Excess_mortalities"
								+ File.separator + info2.emFileName + ".xml";
						eData[d] = factory.manufactureOneDimArray(
								configFileName, "excessmortality", "mortality",
								"unit", true);
						fData[d] = factory.manufactureOneDimArray(
								configFileName, "excessmortality", "mortality",
								"acutelyfatal", true);
						cData[d] = factory.manufactureOneDimArray(
								configFileName, "excessmortality", "mortality",
								"curedfraction", true);
						XMLConfiguration config;
						try {
							config = new XMLConfiguration(configFileName);
						} catch (ConfigurationException e) {

							e.printStackTrace();
							throw new DynamoConfigurationException(
									"error encountered when reading file: "
											+ configFileName
											+ " with message: "
											+ e.getMessage());
						}
						String unitType = getName("unittype", config);
						if (unitType.compareToIgnoreCase("Median survival") == 0)
							for (int a = 0; a < 96; a++)
								for (int g = 0; g < 2; g++)
									eData[d][a][g] = (float) (log2 / ((double) eData[d][a][g]));
						// TODO nog een keer checken of omrekening klopt
						configFileName = baseDir + File.separator
								+ referenceDataDir + File.separator
								+ diseasesDir + File.separator + thisDisease
								+ File.separator + "DALY_Weights"
								+ File.separator + info2.dalyFileName + ".xml";
						dData[d] = factory.manufactureOneDimArray(
								configFileName, "dalyweights", "weight",
								"percent", false);

					}

				}
			}
			// end loop over diseases within cluster
			/*
			 * all data for the cluster now have been read now restructure to
			 * fit into inputData, that takes data structured per age and gender
			 * category
			 */

			float[][] RRdisExtended = new float[clusterStructure[c]
					.getNinCluster()][clusterStructure[c].getNinCluster()];

			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++) {
					/*
					 * first make RRdis
					 */
					for (int d = 0; d < clusterStructure[c].getNinCluster(); d++) {
						if (clusterStructure[c].dependentDisease[d]) {
							for (int d2 = 0; d2 < clusterStructure[c]
									.getNinCluster(); d2++) {
								// TODO fill RRDis
								RRdisExtended[d2][d] = 1;
								for (int rrDis = 0; rrDis < nRRsDisease; rrDis++) {
									if (isRRto[rrDis]
											.compareToIgnoreCase(clusterStructure[c].diseaseName
													.get(d)) == 0
											&& isRRfrom[rrDis]
													.compareToIgnoreCase(clusterStructure[c].diseaseName
															.get(d2)) == 0)

										RRdisExtended[d2][d] = rrInfo
												.get(rrNumber[rrDis]).rrDataDis[a][g];
									break;
								}

							}

						}

						else
							for (int d2 = 0; d2 < clusterStructure[c]
									.getNinCluster(); d2++)
								RRdisExtended[d2][d] = 1;

					}

					clusterData[a][g][c] = new DiseaseClusterData(
							clusterStructure[c],
							inputData.getPrevRisk()[0][0].length, RRdisExtended);

					/* enter the data from diseases */
					for (int d = 0; d < clusterStructure[c].getNinCluster(); d++) {
						clusterData[a][g][c].setPrevalence(
								pData[d][a][g] / 100, d);
						clusterData[a][g][c].setIncidence(iData[d][a][g], d);
						clusterData[a][g][c].setExcessMortality(eData[d][a][g],
								d);
						clusterData[a][g][c].setCuredFraction(cData[d][a][g],
								d, clusterStructure[c]);
						clusterData[a][g][c].setCaseFatality(fData[d][a][g], d);

						// initialize all rr values to 1 in case no rr's are
						// read
						clusterData[a][g][c].setRelRiskCont(1, d);
						clusterData[a][g][c].setRelRiskCat(1, d);
						clusterData[a][g][c].setRelRiskDuurBegin(1, d);
						clusterData[a][g][c].setRelRiskDuurBegin(1, d);
						clusterData[a][g][c].setRrAlpha(0, d);

						/* loop through all RR's to find the RR to add */

						for (int rr = 0; rr < rrInfo.size(); rr++) {
							info = rrInfo.get(rr);
							if (info.to
									.compareToIgnoreCase(clusterStructure[c].diseaseName
											.get(d)) == 0) {
								if (info.rrDataCat != null)
									clusterData[a][g][c].setRelRiskCat(
											info.rrDataCat[a][g], d);
								if (info.rrDataCont != null)
									clusterData[a][g][c].setRelRiskCont(
											info.rrDataCont[a][g], d);
								if (info.rrDataBegin != null)
									clusterData[a][g][c].setRelRiskDuurBegin(
											info.rrDataBegin[a][g], d);
								if (info.rrDataEnd != null)
									clusterData[a][g][c].setRelRiskDuurEnd(
											info.rrDataEnd[a][g], d);
								if (info.rrDataAlfa != null)
									clusterData[a][g][c].setRrAlpha(
											info.rrDataAlfa[a][g], d);

							}
						}// end loop over rr's
					} // end loop over diseases within cluster and entering data

				}// end loop over age and sex
			/* add clusterdata to inputData */

		}// end loop over clusters
		inputData.setClusterData(clusterData);
		;
	}// end method

	public int getRandomSeed() {
		return randomSeed;
	}

	public static void setRandomSeed(int randomSeed) {
		InputDataFactory.randomSeed = randomSeed;
	}

	public int getSimPopSize() {
		return simPopSize;
	}

	public static void setSimPopSize(int simPopSize) {
		InputDataFactory.simPopSize = simPopSize;
	}

} // end class

