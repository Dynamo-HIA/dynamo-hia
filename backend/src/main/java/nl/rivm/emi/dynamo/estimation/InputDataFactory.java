package  nl.rivm.emi.dynamo.estimation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.cdm.rules.update.dynamo.ArraysFromXMLFactory;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfigurationToo;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author boshuizh
 * 
 */

public class InputDataFactory {

	Log log = LogFactory.getLog(getClass().getName());
    int AGE_ARRAYSIZE=96;
	/**
	 * Currently implemented structure: see Dynamo-Hia User data document of
	 * Rene Mondeel version 0.93
	 * 
	 * 
	 */

	/* Field containing the name of the base directory */
	public String baseDir;

	/*
	 * fields describing the labels of the XML configuration file (as made by
	 * window W01)
	 */

	/* first the taglabels for the main structure */
	private static final String mainTagLabel = "simulation";
	private static final String scenTagLabel = "scenarios";
	private static final String disTagLabel = "diseases";
	private static final String riskfactorTagLabel = "riskfactors";
	private static final String rrTagLabel = "RRs";
	private static final String scenTagSubLabel = "scenario";
	private static final String disTagSubLabel = "disease";
	@SuppressWarnings("unused")
	private static final String riskfactorTagSubLabel = "disease";
	private static final String rrSubTagLabel = "RR";

	private static final String newbornLabel = "hasnewborns";
	private static final String startingYearLabel = "startingYear";
	@SuppressWarnings("unused")
	private static final String startingYearLabel2 = "startingYear";
	private static final String numberOfYearsLabel = "numberOfYears";
	private static final String simPopSizeLabel = "simPopSize";
	private static final String minAgeLabel = "minAge";
	private static final String maxAgeLabel = "maxAge";
	private static final String timeStepLabel = "timeStep";
	private static final String refScenNameLabel = "refScenarioName";
	private static final String randomSeedLabel = "randomSeed";
	private static final String resultTypeLabel = "resultType";
	private static final String popFileNameLabel = "popFileName";
	private static final String diseaseNameLabel = "uniquename";
	private static final String diseasePrevFileLabel = "prevfilename";
	private static final String diseaseIncFileLabel = "incfilename";
	private static final String diseaseExcessMortFileLabel = "excessmortfilename";
	private static final String diseaseDalyWeightLabel = "dalyweightsfilename";
	private static final String riskFactorNameLabel = "uniquename";
	private static final String riskFactorTransFileLabel = "transfilename";
	private static final String riskFactorPrevFileLabel = "prevfilename";
	private static final String RRindexLabel = "RRindex";
	private static final String isRRfromLabel = "isRRfrom";
	private static final String isRRtoLabel = "isRRto";
	private static final String isRRfileLabel = "isRRFile";
	private static final String scenarioNameLabel = "uniquename";
	private static final String scenarioSuccessRateLabel = "successRate";
	private static final String targetMinAgeLabel = "targetMinAge";
	private static final String targetMaxAgeLabel = "targetMaxAge";
	private static final String targetGenderLabel = "targetSex";
	private static final String alternativeTransFileLabel = "transfilename";
	private static final String alternativePrevFileLabel = "prevfilename";

	/* the object containing the content of the XML configuration file */
	// private HierarchicalConfiguration configuration;
	// The XMLConfiguration instance contains XML configuration file contents
	// and offers methods for validation
	private XMLConfigurationToo configuration;

	private ArraysFromXMLFactory factory = new ArraysFromXMLFactory();

	private int riskFactorType;

	private String riskFactorName;

	private String riskFactorTransFileName;
	private String riskFactorPrevFileName;
	private String durationPrevFileName = "durationprevalence";

	private int originalNumberDurationClass;

	@SuppressWarnings("unused")
	private String disabilityFileName;
	/* this are the public fields that contain the information from the XML file */
	/**
	 * whether to include newborns
	 */

	public static boolean newborn = false;
	/**
	 * start year of simulation
	 */
	public static int startingYear = 2000;
	/**
	 * number of years to run the simulation for the whole population (including
	 * newborns) in order to calculate life expectancy, all persons in the
	 * simulation at the starting year are simulated until age 105, irrespective
	 * of the value of numberOfyears
	 */
	public static int numberOfYears = 10;
	/**
	 * simulated population size for each age/gender combination. This will be
	 * increased if not all risk factor categories can be covered with this
	 * number
	 */
	public static int simPopSize = 10;
	/**
	 * minimum age in the simulated population
	 */
	public static int minAge = 0;
	/**
	 * maximum age in the simulated population
	 */
	public static int maxAge = 95;
	/**
	 * time steps used in simulation. In DynamoHia this is fixed to 1. Other
	 * values have not been tested
	 */
	public static int timeStep = 1;
	
	/**
	 * time steps used in simulation. In DynamoHia this is fixed to 1. Other
	 * values have not been tested
	 */
	public static String refScenName = "reference scenario";
	
	/**
	 * randomSeed starting the simulation
	 */
	public static int randomSeed = 9;
	/**
	 * not used at moment. For future use
	 */
	public static boolean details = false;

	/**
	 * object containing the information of each scenario
	 */
	/* now scenario info read */
	public static ArrayList<ScenInfo> scenInfo;

	/**
	 * @author boshuizh object containing the information of each scenario
	 */
	public class ScenInfo {

		String name;
		int minAge;
		int maxAge;
		int gender;
		String transFileName;
		String prevFileName;

		float rate;

		/**
		 * 
		 */
		public ScenInfo() {
			// no action necessary
		}
	};

	/**
	 * object containing the information of each disease
	 */
	public static ArrayList<DisInfo> disInfo;

	/**
	 * @author boshuizh
	 * 
	 */
	public class DisInfo {
		int number;
		String name;
		String prevFileName;
		String incFileName;
		String emFileName;
		String dalyFileName;

		/**
		 * constructor
		 */
		public DisInfo() {
			// no action necessary
		}
	};

	/**
	 * object containing the information of each relative risk
	 */
	public static ArrayList<RRInfo> rrInfo;

	/**
	 * @author boshuizh * object containing the information of each relative
	 *         risk
	 */
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

		/**
		 * constructor
		 */
		public RRInfo() {
			// no action necessary
		}
	};

	/*
	 * the names of the directories en standard filenames containing the
	 * information on resp. riskfactors, diseases and relative risks
	 */
	private static final String riskFactorDir = "Risk_Factors";
	// CDM uses "riskfactors"; document uses "risk factors"; dynamo uses
	// "Risk_Factors"!!!!
	private static final String diseasesDir = "Diseases"; // OK //CDM used
	// "diseases"
	private static final String populationDir = "Populations"; // OK //CDM used
	// "populations"
	private static final String simulationDir = "Simulations"; // OK //CDM used
	// "simulation"
	private static final String RRriskDir = "Relative_Risks_From_Risk_Factor"; // OK
	private static final String RRdiseaseDir = "Relative_Risks_From_Diseases"; // OK
	private static final String DALYWeightsDir = "disability"; // OK
	private static final String prevalencesDir = "Prevalences"; // OK
	private static final String incidencesDir = "Incidences"; // OK
	private static final String excessMoratalitiesDir = "Excess_Mortalities"; // OK
	private static final String referenceDataDir = "Reference_Data"; // OK
	@SuppressWarnings("unused")
	private static final String riskFactorPrevalencesDir = "Prevalences"; // OK
	private static final String riskFactorTransitionDir = "Transitions"; // OK
	private static final String RelriskForDeathDir = "Relative_Risks_For_Death"; //
	private static final String RelriskForDisabilityDir = "Odds_Ratios_For_Disability"; //

	/* standard names of files */

	/**
	 * population name
	 */
	public static String populationName = "popFileName";
	private static final String sizeXMLname = "size.xml";
	private static final String allcauseXMLname = "overallmortality.xml";
	private static final String newbornXMLname = "newborns.xml";
	private static final String totDalyXMLname = "overalldisability.xml";

	private static final String riskfactorXMLname = "configuration.xml";

	private static final String riskfactorPrevDir = "prevalences";
	private static final String durationPrevDir = "DurationDistributions";
	@SuppressWarnings("unused")
	private static final String durationXMLname = "durationdistribution.xml";
	@SuppressWarnings("unused")
	private static final String allcauseRRXMLname = "relriskfordeath.xml";
	@SuppressWarnings("unused")
	private static final String dalyRRXMLname = "relriskfordisability.xml";

	private static final String rrDiseaseTagName = "relrisksfromdisease"; // "rrisksfromdisease"
	private static final String rrContinuousTagName = "relrisksfromriskfactor_continuous";// "rrisksforriskfactor_continuous"
	private static final String rrCategoricalTagName = "relrisksfromriskfactor_categorical";// "rrisksforriskfactor_categorical"
	private static final String rrCompoundTagName = "relrisksfromriskfactor_compound";// "rrisksforriskfactor_compound"

	/**
	 * @param simName
	 * @param baseDir
	 * @throws DynamoConfigurationException
	 */
	public InputDataFactory(String simName, String baseDir)
			throws DynamoConfigurationException {
		this.baseDir = baseDir;
		loadConfigurationFile(simName);

	}

	/**
	 * @param simName
	 * @throws DynamoConfigurationException
	 */
	@SuppressWarnings("unchecked")
	private void loadConfigurationFile(String simName)
			throws DynamoConfigurationException {
		;

		String fileName = this.baseDir + File.separator + simulationDir
				+ File.separator + simName + File.separator
				+ "configuration.xml";
		try {
			this.configuration = new XMLConfigurationToo(fileName);

			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls
			// load()).
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			this.configuration.clear();

			// Validate the xml by xsd schema
			// TODO remove again this.configuration.setValidating(true);
			this.configuration.load();

		} catch (ConfigurationException e) {
			String dynamoErrorMessage = "error reading file: " + fileName
					+ " with message: " + e.getMessage() + "\n"
					+ "Root cause: ";
			ErrorMessageUtil.handleErrorMessage(this.log, dynamoErrorMessage,
					e, fileName);
		}
		/* now read in all the data in the sequence of the user data document */
		String yesno = getName(newbornLabel);
		if (yesno.compareToIgnoreCase("no") == 0
				|| yesno.compareToIgnoreCase("0") == 0
				|| yesno.compareToIgnoreCase("n") == 0
				|| yesno.compareToIgnoreCase("false") == 0
				|| yesno.compareToIgnoreCase("f") == 0

		)
			newborn = false;
		else
			newborn = true;

		startingYear = getInteger(startingYearLabel);
		numberOfYears = getInteger(numberOfYearsLabel);
		simPopSize = getInteger(simPopSizeLabel);
		minAge = getInteger(minAgeLabel);
		maxAge = getInteger(maxAgeLabel);
		timeStep = getInteger(timeStepLabel);
		refScenName = getRefScenName(refScenNameLabel);

		/*
		 * put this information in the scenario object
		 * 
		 * This is kind of arbitrary, as this is needed for setting up the
		 * configuration, but not for the post-processing and for the parameter
		 * estimation. however, to be save we put it in the scenario object, as
		 * this is also availlable in the postprocessing stage
		 */
		if (minAge > maxAge)
			throw new DynamoConfigurationException(" the minimum age " + minAge
					+ " for simulation" + " is larger than the maximum age of "
					+ maxAge + "\nPlease change these values");

		if (minAge > 0 && newborn)
			throw new DynamoConfigurationException(
					" adding Newborns to the population is only possible"
							+ " when the minimum age in the population is 0. Minimum age is now set to "
							+ minAge + "\nPlease change these values");

		if (timeStep != 1)
			this.log.warn("timestep given in configuration is " + timeStep
					+ ". In Dynamo timestep should be "
					+ " 1, thus it is changed to 1 ");
		timeStep = 1;
		randomSeed = getInteger(randomSeedLabel);
		
		String resultTypeS = getName(resultTypeLabel);
		if (resultTypeS.compareToIgnoreCase("aggregated") == 0)
			details = false;
		else
			details = true;

		populationName = getName(popFileNameLabel);

		/*
		 * now read the parts that are from the subtabs (scenario, disease,
		 * riskfactor,RR)
		 */

		ConfigurationNode rootNode = this.configuration.getRootNode();

		if (((XMLConfigurationToo) this.configuration).getRootElementName() != mainTagLabel)
			throw new DynamoConfigurationException(" Tagname "
					+ mainTagLabel
					+ " expected in main simulation configuration file "
					+ "but found tag "
					+ ((XMLConfigurationToo) this.configuration)
							.getRootElementName());
		List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
				.getChildren();
		@SuppressWarnings("unused")
		boolean scenPresent = false;
		@SuppressWarnings("unused")
		boolean disPresent = false;
		@SuppressWarnings("unused")
		boolean riskfactorPresent = false;
		@SuppressWarnings("unused")
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

		// if (!scenPresent)
		// throw new DynamoConfigurationException(
		// " no valid information present " + "for scenarios ");
		// if (!disPresent)
		// throw new DynamoConfigurationException(
		// " no valid information present " + "for diseases ");

		// if (!riskfactorPresent)
		// throw new DynamoConfigurationException(
		// " no valid information present " + "for riskfactors ");

		// if (!rrPresent)
		// throw new DynamoConfigurationException(
		// " no valid information present " + "for relative risks ");
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

			// HashMap<key, value> = <Local key, error message representation>
			Map<String, String> missingParameters = new HashMap<String, String>();
			missingParameters.put("namePresent", " name |");
			missingParameters.put("ratePresent", " rate |");
			missingParameters.put("minPresent", " min age |");
			missingParameters.put("maxPresent", " max age |");
			missingParameters.put("gPresent", " gender |");
			missingParameters.put("transPresent", " trans file |");
			missingParameters.put("prevPresent", " prev file |");

			for (ConfigurationNode leafChild : leafChildren) {

				if (leafChild.getName() == scenarioNameLabel) {
					scenData.name = getString(leafChild, scenarioNameLabel);
					namePresent = true;
					missingParameters.remove("namePresent");
				}
				if (leafChild.getName() == scenarioSuccessRateLabel) {
					scenData.rate = getFloat(leafChild,
							scenarioSuccessRateLabel);
					ratePresent = true;
					missingParameters.remove("ratePresent");
				}
				if (leafChild.getName() == targetMinAgeLabel) {
					scenData.minAge = getInteger(leafChild, targetMinAgeLabel);
					minPresent = true;
					missingParameters.remove("minPresent");
				}
				if (leafChild.getName() == targetMaxAgeLabel) {
					scenData.maxAge = getInteger(leafChild, targetMaxAgeLabel);
					maxPresent = true;
					missingParameters.remove("maxPresent");
				}
				if (leafChild.getName() == targetGenderLabel) {
					scenData.gender = getInteger(leafChild, targetGenderLabel);
					gPresent = true;
					missingParameters.remove("gPresent");
				}
				if (leafChild.getName() == alternativeTransFileLabel) {
					scenData.transFileName = getString(leafChild,
							alternativeTransFileLabel);
					transPresent = true;
					missingParameters.remove("transPresent");
				}
				if (leafChild.getName() == alternativePrevFileLabel) {
					scenData.prevFileName = getString(leafChild,
							alternativePrevFileLabel);
					prevPresent = true;
					missingParameters.remove("prevPresent");
				}

			}

			if (namePresent && ratePresent && minPresent && maxPresent
					&& gPresent && transPresent && prevPresent)
				scenInfo.add(scenData);
			else
				throw new DynamoConfigurationException(
						"incomplete information for scenario in configuration file"
								+ missingParameters.toString());

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
	@SuppressWarnings("unchecked")
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

			/*
			 * check if there are no double disease names, otherwise the program
			 * will not work
			 */

			flag = true; /* at least one disease has been successfully read */

			int nDis = disInfo.size();
			for (int i = 0; i < nDis; i++)
				for (int j = i + 1; j < nDis; j++) {
					if

					(disInfo.get(i).name
							.compareToIgnoreCase(disInfo.get(j).name) == 0) {
						throw new DynamoConfigurationException(
								"two disease, "
										+ disInfo.get(i).name
										+ " and "
										+ disInfo.get(j).name
										+ " have the same name. This is not allowed. Please change one of"
										+ " the names.");

					}
				}

		} // end loop over disease

		return flag;
	}

	/**
	 * @param node
	 * @return flag whether the info was read successfully
	 * @throws DynamoConfigurationException
	 */
	@SuppressWarnings("unchecked")
	private boolean handleRiskfactorInfo(ConfigurationNode node)
			throws DynamoConfigurationException {

		boolean flag = false;
		List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) node
				.getChildren();
		List<ConfigurationNode> nodeChildren = (List<ConfigurationNode>) rootChildren
				.get(0).getChildren();

		boolean namePresent = false;
		boolean transPresent = false;
		boolean prevPresent = false;
		for (ConfigurationNode rootChild : nodeChildren) {
			if (rootChild.getName() == riskFactorNameLabel) {
				this.riskFactorName = getString(rootChild, riskFactorNameLabel);
				namePresent = true;
			}

			/*
			 * redundant and deleted; if (rootChild.getName() ==
			 * riskFactorPrevFileLabel) { riskFactorPrevFileName =
			 * getString(rootChild, riskFactorPrevFileLabel); prevPresent =
			 * true; }
			 */
			if (rootChild.getName() == riskFactorTransFileLabel) {
				this.riskFactorTransFileName = getString(rootChild,
						riskFactorTransFileLabel);
				transPresent = true;
			}

			if (rootChild.getName() == riskFactorPrevFileLabel) {
				this.riskFactorPrevFileName = getString(rootChild,
						riskFactorPrevFileLabel);
				prevPresent = true;
			}
			/*
			 * redundant and thus deleted if (rootChild.getName() ==
			 * riskFactorRRforDeathFileLabel) { riskFactorRRforDeathFileName =
			 * getString(rootChild, riskFactorRRforDeathFileLabel); rrPresent =
			 * true; }
			 */
		}
		if (namePresent && prevPresent && transPresent
		/* && rrPresent */)
			flag = true;
		return flag;
	}

	/**
	 * @param node
	 * @return flag whether the info was read successfully
	 * @throws DynamoConfigurationException
	 */
	@SuppressWarnings("unchecked")
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
				if (leafChild.getName() == RRindexLabel) {
					rrData.number = Integer.parseInt(getString(leafChild,
							RRindexLabel));
					fromPresent = true;
				}
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

	/**
	 * @param tag
	 * @return string value of xml with given tag
	 * @throws DynamoConfigurationException
	 */
	public String getName(String tag)

	throws DynamoConfigurationException {

		try {
			String name = this.configuration.getString(tag);
			
			if (name == null) {
				throw new DynamoConfigurationException("empty " + tag
						+ " in XML file from simulation configuration window ");
			}
			return name;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("no " + tag
					+ " in XML file from window 1");
		}

	}

	
	/** This is a variant of getName specifically made for getting the reference scenario name, as this is a new addition
	 * in DYNAMO-2, and thus old files do not have this. If absent, the reference scenario gets a default name
	 * @param tag
	 * @return string value of xml with given tag
	 * @throws DynamoConfigurationException
	 */
	public String getRefScenName(String tag)

	throws DynamoConfigurationException {

		try {
			String name = this.configuration.getString(tag);
			
			if (name == null) {
				name="Reference Scenario";
			}
			return name;
		} catch (NoSuchElementException e) {
			return("Reference Scenario");
		}

	}

	/**
	 * @param tag
	 * @param config
	 * @return string value of given tag in configuration
	 * @throws DynamoConfigurationException
	 */
	public String getName(String tag, HierarchicalConfiguration config)

	throws DynamoConfigurationException {

		try {
			String name = config.getString(tag);
			if (name == null) {
				throw new DynamoConfigurationException("empty " + tag
						+ " in XML file from window 1");
			}
			return name;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("no " + tag
					+ " in XML file from window 1");
		}

	}

	/**
	 * @param node
	 * @param tag
	 * @return string value of given tag in node
	 * @throws DynamoConfigurationException
	 */
	public String getString(ConfigurationNode node, String tag)

	throws DynamoConfigurationException {

		try {
			String returnString;
			String value = (String) node.getValue();

			if (value == null) {
				throw new DynamoConfigurationException("empty " + tag
						+ " in XML file from window 1");
			}

			returnString = (String) value;
			return returnString;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("unreadable " + tag
					+ " in XML file from window 1");
		}

	}

	/**
	 * @param node
	 * @param tag
	 * @return float value in given tag in XML node
	 * @throws DynamoConfigurationException
	 */
	public float getFloat(ConfigurationNode node, String tag)

	throws DynamoConfigurationException {

		try {
			float returnFloat;
			String value = (String) node.getValue();

			if (value == null) {
				throw new DynamoConfigurationException("empty " + tag
						+ " in XML file from window 1");
			}
			if (value.toString() == null)
				throw new DynamoConfigurationException("no String value for  "
						+ tag + " in XML file from window 1");
			returnFloat = Float.parseFloat(value);
			return returnFloat;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("unreadable " + tag
					+ " in XML file from window 1");
		}

	}

	/**
	 * @param node
	 * @param tag
	 * @return Node with given tage
	 * @throws DynamoConfigurationException
	 */
	public int getInteger(ConfigurationNode node, String tag)

	throws DynamoConfigurationException {

		try {
			int returnInt;
			String value = (String) node.getValue();

			if (value == null) {
				throw new DynamoConfigurationException("empty " + tag
						+ " in XML file from window 1");
			}
			if (value.toString() == null)
				throw new DynamoConfigurationException("no String value for  "
						+ tag + " in XML file from window 1");
			returnInt = Integer.parseInt(value);
			return returnInt;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("unreadable " + tag
					+ " in XML file from window 1");
		}

	}

	/**
	 * @param tag
	 * @return integer value with given tag
	 * @throws DynamoConfigurationException
	 */
	public int getInteger(String tag)

	throws DynamoConfigurationException {

		try {
			int value = this.configuration.getInt(tag);
			if (value < 0) {
				throw new DynamoConfigurationException("zero or negative "
						+ tag + " in XML file from simulation window");
			}

			return value;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("no " + tag
					+ " in XML file from simulation window");
		}

	}

	/**
	 * @param tag
	 * @param config
	 * @return integer value with given tag
	 * @throws DynamoConfigurationException
	 */
	public int getInteger(String tag, HierarchicalConfiguration config)

	throws DynamoConfigurationException {

		try {
			int value = config.getInt(tag);
			if (value < 0) {
				throw new DynamoConfigurationException("negative " + tag
						+ " in XML in risk factor configuration file");
			}

			return value;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("no " + tag
					+ " in XML  risk factor configuration file");
		}

	}

	/**
	 * @param tag
	 * @param config
	 * @return float value with given tag
	 * @throws DynamoConfigurationException
	 */
	public float getFloat(String tag, HierarchicalConfiguration config)

	throws DynamoConfigurationException {

		try {
			float value = config.getFloat(tag);
			/* there is no reason why the reference value should not be zero or -1 */
		//	if (value <= 0) {
		//		throw new DynamoConfigurationException("zero or negative "
		//				+ tag + " in XML file from window");
		//	}

			return value;
		} catch (NoSuchElementException e) {
			throw new DynamoConfigurationException("no " + tag
					+ " in XML  risk factor configuration file");
		}

	}

	/**
	 * @param simName
	 * @param inputData
	 * @param scenarioInfo
	 * @throws DynamoConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	public void addPopulationInfoToInputData(String simName,
			InputData inputData, ScenarioInfo scenarioInfo)
			throws DynamoConfigurationException,
			DynamoInconsistentDataException {
		// add general information

		;

		String sizeName = this.baseDir + File.separator + referenceDataDir
				+ File.separator + populationDir + File.separator
				+ populationName + File.separator + sizeXMLname;
		log.debug("PopulationSize filename: " + sizeName);
		String newbornName = this.baseDir + File.separator + referenceDataDir
				+ File.separator + populationDir + File.separator
				+ populationName + File.separator + newbornXMLname;
		log.debug("NewBorns filename: " + newbornName);
		String dalyName = this.baseDir + File.separator + referenceDataDir
				+ File.separator + populationDir + File.separator
				+ populationName + File.separator + totDalyXMLname;
		log.debug("DALY filename: " + dalyName);
		String mortName = this.baseDir + File.separator + referenceDataDir
				+ File.separator + populationDir + File.separator
				+ populationName + File.separator + allcauseXMLname;
		log.debug("Mortality filename: " + mortName);

		scenarioInfo.setPopulationSize(this.factory.manufactureOneDimArray(
				sizeName, "populationsize", "size", "number", false));
		inputData.setOverallDalyWeight(this.factory.manufactureOneDimArray(
				dalyName, "overalldisability", "weight", "percent", false),
				false);

		inputData.setMortTot(this.factory.manufactureOneDimArray(mortName,
				"overallmortality", "mortality", false));
		readNewbornData(newbornName, scenarioInfo);

	}

	/**
	 * @param simName
	 * @param scenarioInfo
	 * @throws DynamoConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	public void addScenarioInfoToScenarioData(String simName,
			ScenarioInfo scenarioInfo) throws DynamoConfigurationException,
			DynamoInconsistentDataException {

		/* copy general information */
		scenarioInfo.setYearsInRun(numberOfYears);
		scenarioInfo.setStartYear(startingYear);
		scenarioInfo.setMinSimAge(minAge);
		scenarioInfo.setMaxSimAge(maxAge);
		scenarioInfo.setWithNewBorns(newborn);
		scenarioInfo.setStepsize(timeStep);
		scenarioInfo.setRefScenName(refScenName);
		scenarioInfo.setRandomSeed(randomSeed);
		scenarioInfo.setSimPopSize(simPopSize);
		/* initialize the arrays in scenarioInfo */
		scenarioInfo.setNScenarios(scenInfo.size());
		scenarioInfo.scenarioNames = new String[scenInfo.size()];
		scenarioInfo.setMinAge(new float[scenInfo.size()]);
		scenarioInfo.setMaxAge(new float[scenInfo.size()]);
		scenarioInfo.setSuccesrate(new float[scenInfo.size()]);
		scenarioInfo.setInMen(new boolean[scenInfo.size()]);
		scenarioInfo.setInWomen(new boolean[scenInfo.size()]);
		scenarioInfo.setTransitionType(new boolean[scenInfo.size()]);
		scenarioInfo.setInitialPrevalenceType(new boolean[scenInfo.size()]);
		scenarioInfo.setZeroTransition(new boolean[scenInfo.size()]);
		scenarioInfo.setNewPrevalence(new float[scenInfo.size()][AGE_ARRAYSIZE][2][]);
		scenarioInfo.setNewOffset(new float[scenInfo.size()][AGE_ARRAYSIZE][2]);
		scenarioInfo.setNewMean(new float[scenInfo.size()][AGE_ARRAYSIZE][2]);
		scenarioInfo.setNewStd(new float[scenInfo.size()][AGE_ARRAYSIZE][2]);
		scenarioInfo.setIsNormal(new boolean[scenInfo.size()]);
		scenarioInfo
				.setAlternativeTransitionMatrix(new float[scenInfo.size()][][][][]);

		for (int scen = 0; scen < scenInfo.size(); scen++) {

			scenarioInfo.setScenarioNames(scenInfo.get(scen).name, scen);
			scenarioInfo.setMinAge(scenInfo.get(scen).minAge, scen);
			scenarioInfo.setMaxAge(scenInfo.get(scen).maxAge, scen);
			scenarioInfo.setSuccesrate(scenInfo.get(scen).rate, scen);
			scenarioInfo.setInMen(true, scen);
			scenarioInfo.setInWomen(true, scen);
			if (scenInfo.get(scen).gender == 0)
				scenarioInfo.setInWomen(false, scen);
			if (scenInfo.get(scen).gender == 1)
				scenarioInfo.setInMen(false, scen);

			/* reading and handling alternative prevalence information */

			if (scenInfo.get(scen).prevFileName.compareToIgnoreCase("none") == 0
					|| scenInfo.get(scen).prevFileName
							.compareToIgnoreCase(this.riskFactorPrevFileName) == 0)
				scenarioInfo.setInitialPrevalenceType(false, scen);
			else
				scenarioInfo.setInitialPrevalenceType(true, scen);
			if (scenInfo.get(scen).prevFileName.compareToIgnoreCase("none") != 0) {
				String completePrevFileName = this.baseDir + File.separator
						+ referenceDataDir + File.separator + riskFactorDir
						+ File.separator + this.riskFactorName + File.separator
						+ InputDataFactory.riskfactorPrevDir + File.separator
						+ scenInfo.get(scen).prevFileName + ".xml";
				if (this.riskFactorType != 2)
					scenarioInfo.setNewPrevalence(this.factory
							.manufactureTwoDimArray(completePrevFileName,
									"riskfactorprevalences_categorical",
									"prevalence", "cat", "percent", false),
							scen);
				else {
					scenarioInfo.setNewMeanSTD(this.factory
							.manufactureOneDimArrayFromTreeLayeredXML(
									completePrevFileName,
									"riskfactorprevalences_continuous",
									"prevalences", "prevalence", "mean", true),
							this.factory
									.manufactureOneDimArrayFromTreeLayeredXML(
											completePrevFileName,
											"riskfactorprevalences_continuous",
											"prevalences", "prevalence",
											"standarddeviation", true),
							this.factory
									.manufactureOneDimArrayFromTreeLayeredXML(
											completePrevFileName,
											"riskfactorprevalences_continuous",
											"prevalences", "prevalence",
											"skewness", true), scen);

				}
			}
			/* reading and handling transition matrix info */

			if (scenInfo.get(scen).transFileName.compareToIgnoreCase("none") == 0
					|| scenInfo.get(scen).transFileName
							.compareToIgnoreCase(this.riskFactorTransFileName) == 0)
				scenarioInfo.setTransitionType(false, scen);

			else {
				scenarioInfo.setTransitionType(true, scen);

				String completeTransFileName = this.baseDir + File.separator
						+ referenceDataDir + File.separator + riskFactorDir
						+ File.separator + this.riskFactorName + File.separator
						+ InputDataFactory.riskFactorTransitionDir
						+ File.separator + scenInfo.get(scen).transFileName
						+ ".xml";
				readTransitionData(completeTransFileName, null, scenarioInfo,
						scen);
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
	 * @throws DynamoInconsistentDataException
	 */

	@SuppressWarnings("unchecked")
	public void addRiskFactorInfoToInputData(InputData inputData,
			ScenarioInfo scenarioInfo) throws DynamoConfigurationException,
			DynamoInconsistentDataException {

		// read and add risktype

		String configFileName;

		configFileName = this.baseDir + File.separator + referenceDataDir
				+ File.separator + riskFactorDir + File.separator
				+ this.riskFactorName + File.separator + riskfactorXMLname;
		XMLConfigurationToo config = null;
		try {

			config = new XMLConfigurationToo(configFileName);

			// WORKAROUND: clear() is put after the constructor (also calls
			// load()).
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			config.clear();
			// Validate the xml by xsd schema
			// config.setValidating(true);
			config.load();

		} catch (ConfigurationException e) {
			String dynamoErrorMessage = "XML error encountered with message: "
					+ e.getMessage();
			ErrorMessageUtil.handleErrorMessage(this.log, dynamoErrorMessage,
					e, configFileName);
		}
		String type = ((XMLConfigurationToo) config).getRootElementName();
		boolean givenAsNormal = false;
		if (type == "riskfactor_categorical") {
			this.riskFactorType = 1;
		} else {
			if (type == "riskfactor_continuous") {
				// TODO: Temporary build message: not yet implemented
				// TODO: Reactivate code below for version 1.1
				// ErrorMessageUtil
				// .handleErrorMessage(
				// this.log,
				// "The component Riskfactor Continuous has not yet been implemented",
				// new DynamoConfigurationException(
				// "The component Riskfactor Continuous has not yet been implemented"),
				// configFileName);
				// TODO: Reactivate code below for version 1.1
				this.riskFactorType = 2;

				// String testString = ((XMLConfiguration)
				// config).getString("distributiontype");
				// if ((testString != null) &&
				// testString.equalsIgnoreCase("Normal")){
				// givenAsNormal=true;
				// } else {
				// log.fatal("No distributionType found.");
				// throw new DynamoConfigurationException(
				// "No distributionType found in XML file: " + configFileName);
				// }

			} else {
				if (type == "riskfactor_compound") {
					// TODO: Temporary build message: not yet implemented
					// TODO: Reactivate code below for version 1.1
					// ErrorMessageUtil
					// .handleErrorMessage(
					// this.log,
					// "The component Riskfactor Continuous has not yet been implemented",
					// new DynamoConfigurationException(
					// "The component Riskfactor Compound has not yet been implemented"),
					// configFileName);
					// TODO: Reactivate code below for version 1.1
					this.riskFactorType = ModelParameters.COMPOUND;
				} else
					throw new DynamoConfigurationException(
							"no valid main tag (riskfactor_type) found but found  "
									+ type + " in XML file " + configFileName);
			}

		}

		inputData.setRiskType(this.riskFactorType);

		scenarioInfo.setRiskType(this.riskFactorType);
		// make file name for riskfactor configurationfile

		/* now read in all the data in the sequence of the user data document */

		if (this.riskFactorType == 2) {
			inputData.setRefClassCont(getFloat("referencevalue", config));
			scenarioInfo.setReferenceRiskFactorValue(getFloat("referencevalue",
					config));
		} else
			inputData.setRefClassCont(0);
		if (this.riskFactorType == 3) {
			inputData.setIndexDuurClass(getInteger("durationclass", config));

			/*
			 * keep the original number as this is needed later to read from
			 * this number the relative risks for duration
			 */

			this.originalNumberDurationClass = inputData.getIndexDuurClass();
		} else
			inputData.setIndexDuurClass(0);
		if (this.riskFactorType != 2)
			scenarioInfo
					.setReferenceClass(getInteger("referenceclass", config));
		else
			scenarioInfo.setReferenceClass(0);

		/*
		 * now read the class names
		 */

		ConfigurationNode rootNode = config.getRootNode();
		if (this.riskFactorType != 2) {
			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();

			for (ConfigurationNode rootChild : rootChildren) {
				// level: classes
				if (rootChild.getName() == "classes") {
					int currentClass = 0;
					List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
							.getChildren();// level: class
					scenarioInfo.setRiskClassnames(new String[leafChildren
							.size()]);
					int[] index = new int[leafChildren.size()];
					int[] index2 = new int[leafChildren.size()];
					String[] names = new String[leafChildren.size()];

					for (ConfigurationNode leafChild : leafChildren) {
						List<ConfigurationNode> elements = (List<ConfigurationNode>) leafChild
								.getChildren();
						for (ConfigurationNode element : elements) {
							if (element.getName() == "flexdex")
								index[currentClass] = getInteger(element,
										"flexdex");
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
												+ i
												+ " in riskfactor configuration file  ");
						for (int j = 0; j < index.length; j++) {
							if (index2[i] == index[j]) {
								scenarioInfo.getRiskClassnames()[i] = names[j];
								if (inputData.getIndexDuurClass() == index[j])
									inputData.setIndexDuurClass(i);
								if (scenarioInfo.getReferenceClass() == index[j])
									scenarioInfo.setReferenceClass(i);

								break;
							}
						}
					}
					scenarioInfo.setIndexDurationClass(inputData
							.getIndexDuurClass());
					if (index2[index.length - 1] - index2[0] != index.length - 1)
						throw new DynamoConfigurationException(
								" class numbers missing "
										+ " in riskfactor configuration file  ");
				}

			}
		} else {
			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();

			for (ConfigurationNode rootChild : rootChildren) {
				// level: classes
				if (rootChild.getName() == "cutoffs") {
					int currentClass = 0;
					List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
							.getChildren();// level: class
					scenarioInfo.setRiskClassnames(new String[leafChildren
							.size()]);
					int[] index = new int[leafChildren.size()];
					int[] index2 = new int[leafChildren.size()];
					float[] cutoffs = new float[leafChildren.size()];

					for (ConfigurationNode leafChild : leafChildren) {
						List<ConfigurationNode> elements = (List<ConfigurationNode>) leafChild
								.getChildren();
						for (ConfigurationNode element : elements) {
							if (element.getName() == "flexdex")
								index[currentClass] = getInteger(element,
										"flexdex");
							index2[currentClass] = index[currentClass]; // make
							// copy
							// to
							// sort
							// later
							if (element.getName() == "value")
								cutoffs[currentClass] = getFloat(element,
										"value");
						}// end class
						currentClass++;
					}
					scenarioInfo.setCutoffs(cutoffs);

				}
			}

		}
		//
		/* read transition information */
		//
		//
		configFileName = this.baseDir + File.separator + referenceDataDir
				+ File.separator + riskFactorDir + File.separator
				+ this.riskFactorName + File.separator
				+ riskFactorTransitionDir + File.separator
				+ this.riskFactorTransFileName + ".xml";

		readTransitionData(configFileName, inputData, null, 0);

		//
		/* read prevalence information */
		//
		//
		configFileName = this.baseDir + File.separator + referenceDataDir
				+ File.separator + riskFactorDir + File.separator
				+ this.riskFactorName + File.separator + riskfactorPrevDir
				+ File.separator + this.riskFactorPrevFileName + ".xml";

		//
		/* first for categorical/compound */
		//
		if (this.riskFactorType != 2) {

		/*
		 * for the future
		 * 
		 * 	inputData.setPrevRisk(takeValueAtNextBirthDay(this.factory.manufactureTwoDimArray(
		

					configFileName, "riskfactorprevalences_categorical",

					"prevalence", "cat", "percent", false))); */ 
			
			inputData.setPrevRisk((this.factory.manufactureTwoDimArray(
					
					configFileName, "riskfactorprevalences_categorical",
					"prevalence", "cat", "percent", false))); 

			scenarioInfo.setOldPrevalence(inputData.getPrevRisk());

		} else {

			/*
			 * for the future
			 * 
			inputData.setMeanRisk(takeValueAtNextBirthDay(this.factory

					.manufactureOneDimArrayFromTreeLayeredXML(configFileName,
							"riskfactorprevalences_continuous", "prevalences",
							"prevalence", "mean", true)));
			inputData.setStdDevRisk(takeValueAtNextBirthDay(this.factory
					.manufactureOneDimArrayFromTreeLayeredXML(configFileName,
							"riskfactorprevalences_continuous", "prevalences",
							"prevalence", "standarddeviation", true)));
			inputData.setSkewnessRisk(takeValueAtNextBirthDay(this.factory
					.manufactureOneDimArrayFromTreeLayeredXML(configFileName,
							"riskfactorprevalences_continuous", "prevalences",

							"prevalence", "skewness", true)));*/
			
			inputData.setMeanRisk((this.factory
					.manufactureOneDimArrayFromTreeLayeredXML(configFileName,
							"riskfactorprevalences_continuous", "prevalences",
							"prevalence", "mean", true)));
			inputData.setStdDevRisk((this.factory
					.manufactureOneDimArrayFromTreeLayeredXML(configFileName,
							"riskfactorprevalences_continuous", "prevalences",
							"prevalence", "standarddeviation", true)));
			inputData.setSkewnessRisk((this.factory
					.manufactureOneDimArrayFromTreeLayeredXML(configFileName,
							"riskfactorprevalences_continuous", "prevalences",
							"prevalence", "skewness", true)));

			float[][] skewness = inputData.getSkewnessRisk();
			boolean normal = true;
			for (int a = 0; a < AGE_ARRAYSIZE; a++)
				for (int g = 0; g < 2; g++)
					if (skewness[a][g] != 0)
						normal = false;
			if (normal && !givenAsNormal)
				log.warn("log-normal distribution asked, but as all skewness"
						+ " are zero, normal distribution is used");
			if (!normal && givenAsNormal)
				log
						.warn("normal distribution asked, but as skewness"
								+ " is not equal to zero, lognormal distribution is used");
			if (normal)
				inputData.setRiskDistribution("Normal");
			else
				inputData.setRiskDistribution("LogNormal");
		}
		;
		if (this.riskFactorType == 3) {
			configFileName = this.baseDir + File.separator + referenceDataDir
					+ File.separator + riskFactorDir + File.separator
					+ this.riskFactorName + File.separator + durationPrevDir
					+ File.separator + this.durationPrevFileName + ".xml";

			inputData.setDuurFreq(this.factory.manufactureTwoDimArray(
					configFileName, "riskfactorprevalences_duration",
					"prevalence", "duration", "percent", false));
			scenarioInfo.setOldDurationClasses(inputData.getDuurFreq());
		}

		RRInfo info;
		String deathFileName = null;
		String disabilityFileName = null;
		@SuppressWarnings("unused")
		int currentRR = 0;
		for (int rr = 0; rr < rrInfo.size(); rr++) {
			info = rrInfo.get(rr);

			/*
			 * check if "from" in the RR is a disease
			 */
			if (info.to.compareToIgnoreCase("death") == 0
					&& info.from.compareToIgnoreCase(this.riskFactorName) == 0)
				deathFileName = info.rrFileName;
			if (info.to.compareToIgnoreCase("disability") == 0
					&& info.from.compareToIgnoreCase(this.riskFactorName) == 0)
				disabilityFileName = info.rrFileName;

		}

		readRRForDeath(inputData, deathFileName);

		readRRForDisability(inputData, disabilityFileName);
	}

	private void readRRForDeath(InputData inputData, String fileName)
			throws DynamoInconsistentDataException,
			DynamoConfigurationException {
		String configFileName;
		int nClasses;
		if (this.riskFactorType != 2)
			nClasses = inputData.getPrevRisk()[0][0].length;
		else
			nClasses = 1;
		float[][][] data3dim = new float[AGE_ARRAYSIZE][2][nClasses];
		float[][] data2dim = new float[AGE_ARRAYSIZE][2];
		for (int a = 0; a < AGE_ARRAYSIZE; a++)
			for (int g = 0; g < 2; g++) {
				Arrays.fill(data3dim[a][g], 1);
				data2dim[a][g] = 1;

			}
		if (fileName == null) {
			inputData.setWithRRForMortality(false);

			inputData.setRelRiskMortCont(data2dim);
			inputData.setRelRiskMortCat(data3dim);
			inputData.setRelRiskDuurMortBegin(data2dim);
			inputData.setRelRiskDuurMortEnd(data2dim);
			inputData.setAlphaMort(data2dim);
		} else {
			inputData.setWithRRForMortality(true);
			//
			/* read RR for all cause mortality or disability */
			//
			//
			//
			/* for categorical/compound */

			/* for categorical */
			if (this.riskFactorType == 1) {

				configFileName = this.baseDir + File.separator
						+ referenceDataDir + File.separator + riskFactorDir
						+ File.separator + this.riskFactorName + File.separator
						+ RelriskForDeathDir + File.separator + fileName
						+ ".xml";

				inputData.setRelRiskMortCat(this.factory
						.manufactureTwoDimArray(configFileName,
								"relrisksfordeath_categorical",
								"relriskfordeath", "cat", "value", false));
				inputData.setRelRiskMortCont(data2dim);
				inputData.setRelRiskDuurMortBegin(data2dim);
				inputData.setRelRiskDuurMortEnd(data2dim);
				inputData.setAlphaMort(data2dim);

			}
			/* for continuous */

			if (this.riskFactorType == 2) {
				configFileName = this.baseDir + File.separator
						+ referenceDataDir + File.separator + riskFactorDir
						+ File.separator + this.riskFactorName + File.separator
						+ RelriskForDeathDir + File.separator + fileName
						+ ".xml";

				inputData.setRelRiskMortCont(this.factory
						.manufactureOneDimArray(configFileName,
								"relrisksfordeath_continuous",
								"relriskfordeath", "value", false));
				inputData.setRelRiskMortCat(data3dim);
				inputData.setRelRiskDuurMortBegin(data2dim);
				inputData.setRelRiskDuurMortEnd(data2dim);
				inputData.setAlphaMort(data2dim);
			}
			/*
			 * for compound
			 */
			if (this.riskFactorType == 3) {
				configFileName = this.baseDir + File.separator
						+ referenceDataDir + File.separator + riskFactorDir
						+ File.separator + this.riskFactorName + File.separator
						+ RelriskForDeathDir + File.separator + fileName
						+ ".xml";

				inputData.setRelRiskMortCat(this.factory
						.manufactureTwoDimArray(configFileName,
								"relrisksfordeath_compound", "relriskfordeath",
								"cat", "begin", true));

				inputData.setRelRiskDuurMortBegin(this.factory
						.selectOneDimArray(configFileName,
								"relrisksfordeath_compound", "relriskfordeath",
								"begin", "cat",
								this.originalNumberDurationClass));
				inputData.setRelRiskDuurMortEnd(this.factory.selectOneDimArray(
						configFileName, "relrisksfordeath_compound",
						"relriskfordeath", "end", "cat",
						this.originalNumberDurationClass));
				inputData.setAlphaMort(this.factory.selectOneDimArray(
						configFileName, "relrisksfordeath_compound",
						"relriskfordeath", "alfa", "cat",
						this.originalNumberDurationClass));

				;
				inputData.setRelRiskMortCont(data2dim);
			}
		}
	}

	private void readRRForDisability(InputData inputData, String fileName)
			throws DynamoInconsistentDataException,
			DynamoConfigurationException {
		String configFileName;

		float[][][] data3dim = new float[AGE_ARRAYSIZE][2][1];
		float[][] data2dim = new float[AGE_ARRAYSIZE][2];
		for (int a = 0; a < AGE_ARRAYSIZE; a++)
			for (int g = 0; g < 2; g++) {
				data3dim[a][g][0] = 1;
				data2dim[a][g] = 1;

			}
		if (fileName == null) {
			inputData.setWithRRForDisability(false);

			inputData.setRRforDisabilityCont(data2dim);
			inputData.setRRforDisabilityCat(data3dim);
			inputData.setRRforDisabilityBegin(data2dim);
			inputData.setRRforDisabilityEnd(data2dim);
			inputData.setAlfaForDisability(data2dim);
		} else {

			inputData.setWithRRForDisability(true);

			//
			/* read RR for all cause mortality or disability */
			//
			//
			//
			/* for categorical/compound */

			/* for categorical */
			if (this.riskFactorType == 1) {

				configFileName = this.baseDir + File.separator
						+ referenceDataDir + File.separator + riskFactorDir
						+ File.separator + this.riskFactorName + File.separator
						+ RelriskForDisabilityDir + File.separator + fileName
						+ ".xml";

				inputData.setRRforDisabilityCat(this.factory
						.manufactureTwoDimArray(configFileName,
								"relrisksfordisability_categorical",
								"relriskfordisability", "cat", "value", false));
				inputData.setRRforDisabilityCont(data2dim);
				inputData.setRRforDisabilityBegin(data2dim);
				inputData.setRRforDisabilityEnd(data2dim);
				inputData.setAlfaForDisability(data2dim);

			}
			/* for continuous */

			if (this.riskFactorType == 2) {
				configFileName = this.baseDir + File.separator
						+ referenceDataDir + File.separator + riskFactorDir
						+ File.separator + this.riskFactorName + File.separator
						+ RelriskForDisabilityDir + File.separator + fileName
						+ ".xml";

				inputData.setRRforDisabilityCont(this.factory
						.manufactureOneDimArray(configFileName,
								"relrisksfordisability_continuous",
								"relriskfordisability", "value", false));
				inputData.setRRforDisabilityCat(data3dim);
				inputData.setRRforDisabilityBegin(data2dim);
				inputData.setRRforDisabilityEnd(data2dim);
				inputData.setAlfaForDisability(data2dim);
			}
			/*
			 * for compound
			 */
			if (this.riskFactorType == 3) {
				configFileName = this.baseDir + File.separator
						+ referenceDataDir + File.separator + riskFactorDir
						+ File.separator + this.riskFactorName + File.separator
						+ RelriskForDisabilityDir + File.separator + fileName
						+ ".xml";

				inputData.setRRforDisabilityCat(this.factory
						.manufactureTwoDimArray(configFileName,
								"relrisksfordisability_compound",
								"relriskfordisability", "cat", "begin", true));

				inputData.setRRforDisabilityBegin(this.factory
						.selectOneDimArray(configFileName,
								"relrisksfordisability_compound",
								"relriskfordisability", "begin", "cat",
								this.originalNumberDurationClass));
				inputData.setRRforDisabilityEnd(this.factory.selectOneDimArray(
						configFileName, "relrisksfordisability_compound",
						"relriskfordisability", "end", "cat",
						this.originalNumberDurationClass));
				inputData.setAlfaForDisability(this.factory.selectOneDimArray(
						configFileName, "relrisksfordisability_compound",
						"relriskfordisability", "alfa", "cat",
						this.originalNumberDurationClass));

				;
				inputData.setRRforDisabilityCont(data2dim);
			}
		}
	}

	/**
	 * @param inputData
	 *            : object with input data to which the transition matrix is
	 *            added.
	 *            this parameter should be null if the transition matrix
	 *            should be added to the scenario info
	 * @param scenInfo
	 *            object with scenario info to which the transition matrix
	 *            should be added 
	 *            this parameter should be null if the transition
	 *            matrix should be added to the input data (= transtionmatrix
	 *            for reference scenario)
	 * 
	 * @throws DynamoConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	private void readTransitionData(String configFileName, InputData inputData,
			ScenarioInfo scenInfo, int scenNumber)
			throws DynamoConfigurationException,
			DynamoInconsistentDataException {

		XMLConfigurationToo config = null;
		if (this.riskFactorType != 2) {
			try {
				config = new XMLConfigurationToo(configFileName);

				// Validate the xml by xsd schema
				// WORKAROUND: clear() is put after the constructor (also calls
				// load()).
				// The config cannot be loaded twice,
				// because the contents will be doubled.
				config.clear();

				// Validate the xml by xsd schema
				// config.setValidating(true);
				config.load();
			} catch (ConfigurationException e) {
				String dynamoErrorMessage = "reading error encountered when reading file: "
						+ configFileName + " with message: " + e.getMessage();
				ErrorMessageUtil.handleErrorMessage(this.log,
						dynamoErrorMessage, e, configFileName);
			}
			if (((XMLConfigurationToo) config).getRootElementName() == "transitionmatrix_zero") {
				if (inputData != null)
					inputData.setTransType(0);
				else if (scenInfo != null)
					scenInfo.setZeroTransition(true, scenNumber);
				else
					throw new DynamoConfigurationException(
							"Both scenInfo and Input data are null in method"
									+ "readTransitionData, so information can not be added to anything");
			}

			else if (((XMLConfigurationToo) config).getRootElementName() == "transitionmatrix_netto") {
				if (inputData != null)
					inputData.setTransType(1);

				else scenInfo.setNettoTransition(scenNumber, true);
					
					//throw new DynamoConfigurationException(
					//		"Nett transitions are defined as nett transitions for the distribution"
					//				+ " of the reference scenario. They can only be used in an alternative scenario when"
					//				+ " they are also used in the reference scenario."
					//				+ "\nPlease alter the input to comply with this requirement");//

			}

			else if (((XMLConfigurationToo) config).getRootElementName() == "transitionmatrix") {
				if (inputData != null) {
					inputData.setTransType(2);

					inputData.setTransitionMatrix(this.factory
							.manufactureThreeDimArray(configFileName,
									"transitionmatrix", "transition", "from",
									"to", "percent"));

				}

				else if (scenInfo != null)
					scenInfo.setAlternativeTransitionMatrix(this.factory
							.manufactureThreeDimArray(configFileName,
									"transitionmatrix", "transition", "from",
									"to", "percent"), scenNumber);
				else
					throw new DynamoConfigurationException(
							"Both scenInfo and Input data are null in method"
									+ "readTransitionData, so information can not be added to anything");

			}

			else
				throw new DynamoConfigurationException("when reading file " + configFileName +" Tagname "
						+ "transitionmatrix (_zero,_netto) "
						+ " expected "
						+ "but found tag "
						+ ((XMLConfigurationToo) config).getRootElementName()
						+ ". Note that continuous and categorical risk factors have different tags (transitiondrift and transitionmatrix respectively)");
		}
		/* second for continuous */
		//
		else {

			try {
				config = new XMLConfigurationToo(configFileName);

				// Validate the xml by xsd schema
				// WORKAROUND: clear() is put after the constructor (also calls
				// load()).
				// The config cannot be loaded twice,
				// because the contents will be doubled.
				config.clear();

				// Validate the xml by xsd schema
				// config.setValidating(true);
				config.load();
			} catch (ConfigurationException e) {
				String dynamoErrorMessage = "error encountered while reading file: "
						+ configFileName + " with message: " + e.getMessage();
				ErrorMessageUtil.handleErrorMessage(this.log,
						dynamoErrorMessage, e, configFileName);
			}
			if (((XMLConfigurationToo) config).getRootElementName() == "transitiondrift_zero") {
				if (inputData != null) {
					inputData.setTransType(0);
				} else if (scenInfo != null)
					scenInfo.setZeroTransition(true, scenNumber);

			}

			else if (((XMLConfigurationToo) config).getRootElementName() == "transitiondrift_netto") {
				if (inputData != null) {
					inputData.setTransType(1);
					inputData.setTrendInDrift(config.getFloat("trend"));
				} else
					throw new DynamoConfigurationException(
							"netto transition file not possbile for alternative scenario."
									+ "For alternative scenario's one should enter an explicite file");
			}

			else if (((XMLConfigurationToo) config).getRootElementName() == "transitiondrift") {
				if (inputData != null) {
					inputData.setTransType(2);
					inputData.setMeanDrift(this.factory.manufactureOneDimArray(
							configFileName, "transitiondrift", "transition",
							"mean", true));

				} else if (scenInfo != null)
					scenInfo.setMeanDrift(this.factory.manufactureOneDimArray(
							configFileName, "transitiondrift", "transition",
							"mean", true), scenNumber);
				/*
				 * obsolete but kept for potential future use inputData
				 * .setStdDrift(factory.manufactureOneDimArray( configFileName,
				 * "transitionmatrix", "transition", "standarddevivation",
				 * true)); inputData.setOffsetDrift
				 * (factory.manufactureOneDimArray( configFileName,
				 * "transitionmatrix", "transition", "skewness", true));
				 */
			}

			else
				throw new DynamoConfigurationException("when reading file " + configFileName 
						+ " (transitions for a continuous riskfactor) a Tag transitiondrift(_zero,_netto) is"
						+ " expected but found tag "
						+ ((XMLConfigurationToo) config).getRootElementName()
						+ ". Note that continuous and categorical riskfactors have different tags (transitiondrift and transitionmatrix respectively)");
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
		if ((this.riskFactorType == 1 || this.riskFactorType == 3)
				&& inputData.getPrevRisk() == null) {
			addRiskFactorInfoToInputData(inputData, scenarioInfo);
			if (inputData.getPrevRisk() == null)
				throw new DynamoInconsistentDataException(
						"no valid riskfactor information availlable in configuration ");
		}
		if ((this.riskFactorType == 2) && inputData.getMeanRisk() == null) {
			addRiskFactorInfoToInputData(inputData, scenarioInfo);
			if (inputData.getMeanRisk() == null)
				throw new DynamoInconsistentDataException(
						"no valid riskfactor information availlable in configuration ");
		}

		int nDiseases = disInfo.size();
		inputData.setNDisease(nDiseases);
		if (nDiseases == 0)
			inputData.setNCluster(0);
		if (nDiseases > 0) {
			/*
			 * identification of diseases throughout is through their name as
			 * the order is changed by creating clusters therefore, put disease
			 * names in a array to simplify coding in this method
			 */
			String[] diseaseName = new String[nDiseases];
			for (int d = 0; d < nDiseases; d++) {
				diseaseName[d] = disInfo.get(d).name;
				// put disease names in a array to simplify coding in this
				// method
			}

			/*
			 * flag which relative risks are for disease on disease, and put
			 * them in an array
			 */

			String[] isRRfrom = new String[rrInfo.size()];
			String[] isRRto = new String[rrInfo.size()];
			int[] rrNumber = new int[rrInfo.size()];

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
			 * NB cancers must be split up in two diseases this is done
			 * elsewhere
			 * 
			 * identification of diseases throughout is through their name as
			 * the order is changed by creating clusters
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
								if (isRRto[rr].equalsIgnoreCase(diseaseName[d2])) {
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
				/*		if (diseaseName[d].compareToIgnoreCase(isRRto[rr]) == 0) {
							for (int d2 = 0; d2 < nDiseases; d2++) {
								if (isRRfrom[rr] == diseaseName[d2]) {
									// now give dependent and causal disease the
									// same
									// (lowest) cluster number;
									if (clusternumber[d] < clusternumber[d2])
										clusternumber[d2] = clusternumber[d];
									if (clusternumber[d2] < clusternumber[d])
										clusternumber[d] = clusternumber[d2];
								}
							}
						} */
					}// end loop over all rr's related to d
					clusterSum += clusternumber[d];
				}
			}
			;
			// now each cluster has a unique cluster number , but not
			// necessarily
			// aaneensluitend;

			// count clusters and make index;
			int clusterIndex[] = new int[nDiseases]; // clusterIndex gives for
			// each
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
				hasSameNumber = false;
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
			/* this does not work try again below */
			/* find number of clusters */

			/*
			 * count number of diseases in each cluster and number of
			 * independent (=causal) diseases
			 */
			int[] nInCluster = new int[nClusters];
			int[] nCausalInCluster = new int[nClusters];
			for (int c = 0; c < nClusters; c++) {
				nInCluster[c] = 0;
				for (int d = 0; d < nDiseases; d++) {
					if (clusterIndex[d] == c)
						nInCluster[c]++;
					// NB: if disease is not related to any other disease then
					// both
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
								indexIndependentDiseasesForCluster[withinClusterIndependentNumber] = withinClusterNumber;
								withinClusterIndependentNumber++;
							}
							withinClusterNumber++;
						}
				}

				/*
				 * public DiseaseClusterStructure(String clusterName, int
				 * startN, int N, String[] diseaseNames, int[] NRIndependent)
				 */
				clusterStructure[c] = new DiseaseClusterStructure(
						"cluster" + c, nStart, nInCluster[c],
						DiseaseNamesForCluster,
						indexIndependentDiseasesForCluster);
				nStart += nInCluster[c];
			}
			/*
			 * put data into inputData
			 */
			inputData.setNCluster(nClusters);
			inputData.setClusterStructure(clusterStructure);
			scenarioInfo.setStructure(clusterStructure);

			/*
			 * now read the data for the diseases
			 * 
			 * class DisInfo { int number; String name; String prevFileName;
			 * String incFileName; String emFileName; String dalyFileName;
			 */
			DisInfo info2;
			float pData[][][];
			float iData[][][];
			float eData[][][];
			float dData[][][];
			float fData[][][];
			float cData[][][];
			@SuppressWarnings("unused")
			double log2 = Math.log(2);

			// first read in the relative risks
			for (int rr = 0; rr < rrInfo.size(); rr++) {

				info = rrInfo.get(rr);

				if (info.fromIsDisease) {
					String configFileName = this.baseDir + File.separator
							+ referenceDataDir + File.separator + diseasesDir
							+ File.separator + info.to + File.separator
							+ RRdiseaseDir + File.separator + info.rrFileName
							+ ".xml";

					info.rrDataDis = this.factory.manufactureOneDimArray(
							configFileName, rrDiseaseTagName, /*
															 * XMLTagEntityEnum.RELATIVERISK
															 * .getElementName()
															 */"relativerisk",
							"value", false);

				} else {
					/*
					 * only read in rr's for diseases, not for death or
					 * disability, and also only when from a riskfactor (ignore relative risks from
					 * non configured diseases as they have a different format so will cause errors)
					 */
					if (info.to.compareToIgnoreCase("death") != 0
							&& info.to.compareToIgnoreCase("disability") != 0 && info.from.equals(this.riskFactorName))
						if (this.riskFactorType == 2) {
							String configFileName = this.baseDir
									+ File.separator + referenceDataDir
									+ File.separator + diseasesDir
									+ File.separator + info.to + File.separator
									+ RRriskDir + File.separator
									+ info.rrFileName + ".xml";
							info.rrDataCont = this.factory
									.manufactureOneDimArray(configFileName,
											rrContinuousTagName,
											/*
											 * XMLTagEntityEnum.RELATIVERISK.getElementName
											 * ()
											 */"relativerisk", "value", false);
						} else if (this.riskFactorType == 1) {

							String configFileName = this.baseDir
									+ File.separator + referenceDataDir
									+ File.separator + diseasesDir
									+ File.separator + info.to + File.separator
									+ RRriskDir + File.separator
									+ info.rrFileName + ".xml";
							info.rrDataCat = this.factory
									.manufactureTwoDimArray(configFileName,
											rrCategoricalTagName,
											/*
											 * XMLTagEntityEnum.RELATIVERISK.getElementName
											 * ()
											 */"relativerisk", "cat", "value",
											false);
						} else {
							String configFileName = this.baseDir
									+ File.separator + referenceDataDir
									+ File.separator + diseasesDir
									+ File.separator + info.to + File.separator
									+ RRriskDir + File.separator
									+ info.rrFileName + ".xml";

							info.rrDataCat = this.factory
									.manufactureTwoDimArray(configFileName,
											rrCompoundTagName, /*
																 * XMLTagEntityEnum.
																 * RELATIVERISK
																 * .getElementName
																 * ()
																 */
											"relativerisk", "cat", "begin",
											true);
							info.rrDataBegin = this.factory.selectOneDimArray(
									configFileName, rrCompoundTagName,
									/*
									 * XMLTagEntityEnum.RELATIVERISK.getElementName
									 * ()
									 */"relativerisk", "begin", "cat",
									this.originalNumberDurationClass);
							info.rrDataEnd = this.factory.selectOneDimArray(
									configFileName, rrCompoundTagName,
									/*
									 * XMLTagEntityEnum.RELATIVERISK.getElementName
									 * ()
									 */"relativerisk", "end", "cat",
									this.originalNumberDurationClass);
							info.rrDataAlfa = this.factory.selectOneDimArray(
									configFileName, rrCompoundTagName,
									/*
									 * XMLTagEntityEnum.RELATIVERISK.getElementName
									 * ()
									 */"relativerisk", "alfa", "cat",
									this.originalNumberDurationClass);

						}

				}
			}// end loop over rr's

			DiseaseClusterData[][][] clusterData = new DiseaseClusterData[AGE_ARRAYSIZE][2][nClusters];

			for (int c = 0; c < nClusters; c++) {

				pData = new float[clusterStructure[c].getNInCluster()][AGE_ARRAYSIZE][2];
				iData = new float[clusterStructure[c].getNInCluster()][AGE_ARRAYSIZE][2];
				eData = new float[clusterStructure[c].getNInCluster()][AGE_ARRAYSIZE][2];
				fData = new float[clusterStructure[c].getNInCluster()][AGE_ARRAYSIZE][2];
				cData = new float[clusterStructure[c].getNInCluster()][AGE_ARRAYSIZE][2];
				dData = new float[clusterStructure[c].getNInCluster()][AGE_ARRAYSIZE][2];

				for (int d = 0; d < clusterStructure[c].getNInCluster(); d++) {
					String thisDisease = clusterStructure[c].getDiseaseName()
							.get(d);

					/* find the DisInfo element for this disease */
/* weggehaald: take value at next birthday want dat slaat nergens op */
					
					
					for (int dTot = 0; dTot < nDiseases; dTot++) {
						info2 = disInfo.get(dTot);
						if (thisDisease == info2.name) {
							// prevalence
							String configFileName = this.baseDir
									+ File.separator + referenceDataDir
									+ File.separator + diseasesDir
									+ File.separator + thisDisease
									+ File.separator + prevalencesDir
									+ File.separator + info2.prevFileName
									+ ".xml";
							pData[d] = this.factory.manufactureOneDimArray(
									configFileName, "diseaseprevalences",
									"prevalence", "percent", false);
							configFileName = this.baseDir + File.separator
									+ referenceDataDir + File.separator
									+ diseasesDir + File.separator
									+ thisDisease + File.separator
									+ incidencesDir + File.separator
									+ info2.incFileName + ".xml";
							iData[d] = (this.factory.manufactureOneDimArray(
									configFileName, "diseaseincidences",
									"incidence", "value", false));
							configFileName = this.baseDir + File.separator
									+ referenceDataDir + File.separator
									+ diseasesDir + File.separator
									+ thisDisease + File.separator
									+ excessMoratalitiesDir + File.separator
									+ info2.emFileName + ".xml";
							eData[d] = (this.factory
									.manufactureOneDimArrayFromTreeLayeredXML(
											configFileName, "excessmortality",
											"mortalities", "mortality", "unit",
											true));
							fData[d] = (this.factory
									.manufactureOneDimArrayFromTreeLayeredXML(
											configFileName, "excessmortality",
											"mortalities", "mortality",
											"acutelyfatal", true));
							cData[d] = (this.factory
									.manufactureOneDimArrayFromTreeLayeredXML(
											configFileName, "excessmortality",
											"mortalities", "mortality",
											"curedfraction", true));
							XMLConfigurationToo config = null;
							try {
								config = new XMLConfigurationToo(configFileName);

								// Validate the xml by xsd schema
								// WORKAROUND: clear() is put after the
								// constructor
								// (also calls load()).
								// The config cannot be loaded twice,
								// because the contents will be doubled.
								config.clear();

								// Validate the xml by xsd schema
								// TODO weer aanzetten
								// config.setValidating(true);
								config.load();
							} catch (ConfigurationException e) {
								String dynamoErrorMessage = "error encountered when reading file: "
										+ configFileName
										+ " with message: "
										+ e.getMessage();
								ErrorMessageUtil.handleErrorMessage(this.log,
										dynamoErrorMessage, e, configFileName);
							}
							String unitType = getName("unittype", config);
							if (unitType.compareToIgnoreCase("Median survival") == 0)

								eData[d] = makeExcessRate(eData[d], thisDisease);
							// TODO nog een keer checken of omrekening klopt
							configFileName = this.baseDir + File.separator
									+ referenceDataDir + File.separator
									+ diseasesDir + File.separator
									+ thisDisease + File.separator
									+ DALYWeightsDir + File.separator
									+ info2.dalyFileName + ".xml";
							dData[d] = this.factory.manufactureOneDimArray(
									configFileName, "dalyweights", "weight",
									"percent", false);

						}

					}
				}
				// end loop over diseases within cluster
				/*
				 * all data for the cluster now have been read now restructure
				 * to fit into inputData, that takes data structured per age and
				 * gender category
				 */

				float[][] RRdisExtended = new float[clusterStructure[c]
						.getNInCluster()][clusterStructure[c].getNInCluster()];

				for (int a = 0; a < AGE_ARRAYSIZE; a++)
					for (int g = 0; g < 2; g++) {
						/*
						 * first make RRdis
						 */
						for (int d = 0; d < clusterStructure[c].getNInCluster(); d++) {
							if (clusterStructure[c].getDependentDisease()[d]) {
								for (int d2 = 0; d2 < clusterStructure[c]
										.getNInCluster(); d2++) {
									// TODO fill RRDis
									RRdisExtended[d2][d] = 1;
									for (int rrDis = 0; rrDis < nRRsDisease; rrDis++) {
										if (isRRto[rrDis]
												.compareToIgnoreCase(clusterStructure[c]
														.getDiseaseName()
														.get(d)) == 0
												&& isRRfrom[rrDis]
														.compareToIgnoreCase(clusterStructure[c]
																.getDiseaseName()
																.get(d2)) == 0)

											RRdisExtended[d2][d] = rrInfo
													.get(rrNumber[rrDis]).rrDataDis[a][g];

									}

								}

							}

							else
								for (int d2 = 0; d2 < clusterStructure[c]
										.getNInCluster(); d2++)
									RRdisExtended[d2][d] = 1;

						}
						int nclasses = 1;
						if (this.riskFactorType != 2)
							nclasses = inputData.getPrevRisk()[0][0].length;

						clusterData[a][g][c] = new DiseaseClusterData(
								clusterStructure[c], nclasses, RRdisExtended);

						/* enter the data from diseases */
						for (int d = 0; d < clusterStructure[c].getNInCluster(); d++) {
							clusterData[a][g][c].setPrevalence(pData[d][a][g],
									d);
							clusterData[a][g][c]
									.setIncidence(iData[d][a][g], d);
							clusterData[a][g][c].setExcessMortality(
									eData[d][a][g], d);
							// TODO checken of dit inderdaad datgene is wat
							// wordt ingevoerd
							clusterData[a][g][c].setAbility(
									(1 - dData[d][a][g]), d);

							/* divide by 100 as this is not done automatically */
							/*
							 * divide by 100 as this is not done automatically
							 * because these do not have the xml tag percent
							 */
							clusterData[a][g][c].setCuredFraction(
									cData[d][a][g] / 100, d,
									clusterStructure[c]);
							if (clusterStructure[c].isWithCuredFraction() && clusterStructure[c].getNInCluster()>1)
								throw new DynamoInconsistentDataException(
										"Disease "
												+ diseaseName[d]
												+ " is related to another disease and has a cured fraction. This combination is not allowed. Please change this");
						

							clusterData[a][g][c].setCaseFatality(
									fData[d][a][g] / 100, d);

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
										.compareToIgnoreCase(clusterStructure[c]
												.getDiseaseName().get(d)) == 0) {
									if (info.rrDataCat != null)
										clusterData[a][g][c].setRelRiskCat(
												info.rrDataCat[a][g], d);
									if (info.rrDataCont != null)
										clusterData[a][g][c].setRelRiskCont(
												info.rrDataCont[a][g], d);
									if (info.rrDataBegin != null)
										clusterData[a][g][c]
												.setRelRiskDuurBegin(
														info.rrDataBegin[a][g],
														d);
									if (info.rrDataEnd != null)
										clusterData[a][g][c].setRelRiskDuurEnd(
												info.rrDataEnd[a][g], d);
									if (info.rrDataAlfa != null)
										clusterData[a][g][c].setRrAlpha(
												info.rrDataAlfa[a][g], d);

								}
							}// end loop over rr's
						} // end loop over diseases within cluster and entering
						// data

					}// end loop over age and sex
				/* add clusterdata to inputData */

			}// end loop over clusters
			inputData.setClusterData(clusterData);
			;
		} // END IF NDISEASE>0
	}// end method

	/**
	 * @param medianSurvival
	 * @param diseaseLabel
	 * @return excess rate (array) calculated from a list with median survival
	 * @throws DynamoInconsistentDataException
	 */
	public float[][] makeExcessRate(float[][] medianSurvival,
			String diseaseLabel) throws DynamoInconsistentDataException {
		float[][] rate = new float[AGE_ARRAYSIZE][2];
		double log2 = Math.log(2);
		double[][] drate = new double[AGE_ARRAYSIZE][2];
		double medianAge;
		double aaRate;
		double yearOfMedian;
		double sumRate;

		/* 2013 addition: median survival is the median survival of those have this age of diagnosis */
		/* Survival of these persons can be expressed as the exp of the cummulative rate  during follow-up */
		/* at the median survival survival = 1/2, thus -log(2)=-sum rates.  
		 * As the median survival is for the average x year old person, so with age x+1/2 year, 
		 * in the original version of DYNAMO the remaining rate*time was for only 0.5 year, so the remaining
		 * rate*time needed to be multiplied by 2. This might have gone wrong because 
		 * brackets were omitted from this formulae when inspected again
		 * 
		 * However, this approach is very sensitive to data that are not increasing
		 * with age in the way that is expected, due to the multiplication with 2
		 * that makes remaining rates sometimes very high
		 * 
		 * Therefore for DYNAMO-2 we first calculate the median survival at exact age x (by averaging that of age x and x-1)
		 * this make the multiplication with 2 unnecessary
		 * for age 0 with maintain the old method
		 */
		
		
		
		 
			/* the excess mortality at a certain age is the average excess mortality rate of all who survive to this age
		 */
		 
		for (int g = 0; g < 2; g++) {
			if (medianSurvival[95][g] == 0)
				throw new DynamoInconsistentDataException(
						"median survival of zero for disease "
								+ diseaseLabel
								+ " is not"
								+ " possible: use the option 100% fatal fraction to model acutely fatal diseases");
			drate[95][g] = (log2 / medianSurvival[AGE_ARRAYSIZE-1][g]);
			rate[95][g] = (float) drate[95][g];
			for (int a = 94; a >= 0; a--) {
				if (g==0 && a==63)
				{
					@SuppressWarnings("unused")
					int hhh=0;
					hhh++;
				}
				if (medianSurvival[a][g] == 0)
					throw new DynamoInconsistentDataException(
							"median survival of zero for disease "
									+ diseaseLabel
									+ " is not"
									+ " possible: use the option 100% fatal fraction to model acutely fatal diseases");
				if (a==0) medianAge = a + medianSurvival[a][g] + 0.5; else medianAge = a + 0.5*(medianSurvival[a][g]+ medianSurvival[a-1][g]) ;
				yearOfMedian = Math.floor(medianAge);
				sumRate = 0;
				int upper;
				if (Math.floor(medianAge) == Math.ceil(medianAge))
					upper = (int) Math.floor(medianAge) + 1;
				else
					upper = (int) Math.ceil(medianAge);

				for (int aa = a + 1; aa < upper; aa++) {
					if (aa > 95)
						aaRate = drate[95][g];
					else
						aaRate = drate[aa][g];
					if (aa == yearOfMedian)
						sumRate += (medianAge - yearOfMedian) * aaRate;
					else
						sumRate += aaRate;
				}
				/* sumrate is over median duration +0.5, dus de resterende rate is over een half jaar */
				/* dus rest = 0.5*rate, en dus is de rate 2*rest */
				if (a==0) drate[a][g] = 2*(log2 - sumRate);else drate[a][g] = (log2 - sumRate);
				if (drate[a][g] < 0)
					throw new DynamoInconsistentDataException(
							"median survival rates for age "
									+ a
									+ " and sex "
									+ g
									+ " for disease "
									+ diseaseLabel
									+ " are inconsistent with the median survival at the next higher agegroups. Please "
									+ "check the data and make sure that survival does not decrease abruptly over age");
				rate[a][g] = (float) drate[a][g];
			}

		}

		return rate;

	}

	@SuppressWarnings("unchecked")
	private void readNewbornData(String configFileName, ScenarioInfo scenInfo)
			throws DynamoConfigurationException,
			DynamoInconsistentDataException {

		XMLConfigurationToo config = null;
		/* make an array large enough for all cases */

		int[] year = new int[200];
		int[] number = new int[200];
		int currentChild = 0;
		try {
			config = new XMLConfigurationToo(configFileName);

			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls
			// load()).
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			config.clear();
			// config.setValidating(true);
			config.load();
		} catch (ConfigurationException e) {
			String dynamoErrorMessage = "Reading error encountered when reading file: "
					+ configFileName + " with message: " + e.getMessage();
			ErrorMessageUtil.handleErrorMessage(this.log, dynamoErrorMessage,
					e, configFileName);
		}

		if (((XMLConfigurationToo) config).getRootElementName() == "newborns") {
			if (scenInfo != null) {

				try {

					this.log.debug("config.getFloat(sexratio)"
							+ config.getFloat("sexratio"));
					scenInfo.setMaleFemaleRatio(config.getFloat("sexratio"));
					scenInfo.setNewbornStartYear(config.getInt("startingYear"));

					ConfigurationNode rootNode = config.getRootNode();

					List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
							.getChildren();

					for (ConfigurationNode rootChild : rootChildren) {
						if (rootChild.getName() != "amounts"
								&& rootChild.getName() != "sexratio"
								&& rootChild.getName() != "startingYear")
							throw new DynamoConfigurationException(
									" Tagname "
											+ "amounts or sexratio or startingyear  expected in file "
											+ configFileName
											+ " but found tag "
											+ rootChild.getName());

						if (rootChild.getName() == "amounts") {
							List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
									.getChildren();
							for (ConfigurationNode leafChild : leafChildren) {
								List<ConfigurationNode> leafChildElements = (List<ConfigurationNode>) leafChild
										.getChildren();

								for (ConfigurationNode leafElement : leafChildElements) {
									Object valueObject = leafElement.getValue();
									String leafName = leafElement.getName();
									if (valueObject instanceof String) {
										String valueString = (String) valueObject;
										if ("year".equalsIgnoreCase(leafName)) {

											year[currentChild] = getIntegerValue(
													valueString, "year");

											if (currentChild > 0
													&& year[currentChild] != year[currentChild - 1] + 1)
												throw new DynamoInconsistentDataException(
														" years in file with newborns"
																+ " have a gap between year "
																+ year[currentChild - 1]
																+ " and "
																+ year[currentChild]
																+ " this is not allowed");
										}
										if ("number".equalsIgnoreCase(leafName)) {

											number[currentChild] = getIntegerValue(
													valueString, "number");
											// TODO checken data completeness

										}
									}

								}
								currentChild++;
							}
						}
					}

					/* ready with reading, now make arrays to store */

					;

				} catch (Exception e) {
					String dynamoErrorMessage = "Reading error encountered when reading file: "
							+ configFileName
							+ " with message: "
							+ e.getMessage();
					ErrorMessageUtil.handleErrorMessage(this.log,
							dynamoErrorMessage, e, configFileName);

				}
			}

		} else
			throw new DynamoConfigurationException(
					"label newborns expected but found label: "
							+ ((XMLConfigurationToo) config).getRootElementName()
							+ " in file : " + configFileName);

		int startYear = year[0];
		scenInfo.setStartYearNewborns(startYear);
		int newborns[] = new int[currentChild];

		for (int i = 0; i < currentChild; i++)
			newborns[i] = number[i];
		scenInfo.setNewborns(newborns);

	}

	/**
	 * @param value
	 * @param tag
	 * @return integer with the value of the XML element with the given tag
	 * @throws DynamoConfigurationException
	 */
	public int getIntegerValue(String value, String tag)
			throws DynamoConfigurationException {
		int returnvalue = 0;
		if (value == null)
			throw new DynamoConfigurationException("no value found with " + tag);
		else
			returnvalue = Integer.parseInt(value);
		return returnvalue;

	}

	/**
	 * @return randomSeed of simulation
	 */
	public int getRandomSeed() {
		return randomSeed;
	}

	/**
	 * @param randomSeed
	 */
	public static void setRandomSeed(int randomSeed) {
		InputDataFactory.randomSeed = randomSeed;
	}

	/**
	 * @return size of simulated population
	 */
	public int getSimPopSize() {
		return simPopSize;
	}

	/**
	 * @param simPopSize
	 */
	public static void setSimPopSize(int simPopSize) {
		InputDataFactory.simPopSize = simPopSize;
	}

	@SuppressWarnings("unused")
	private float[][] takeValueAtNextBirthDay( float[][] inputArray){
		
		float[][] returnArray;
		returnArray= new float [AGE_ARRAYSIZE][2];
		for(int g=0 ; g<2;g++){
		for(int a=0 ; a<AGE_ARRAYSIZE-1;a++)
			
				returnArray[a][g]=0.5F*(inputArray[a][g]+inputArray[a+1][g]);
		/* extrapolate the highest age */
		returnArray[AGE_ARRAYSIZE-1][g]=inputArray[AGE_ARRAYSIZE-1][g];}
			
		return returnArray;
		
	}
/** average the prevalence with that of the next category: this is strange, should be with the last as we need the
 * prevalence at the last birthday
 * @param inputArray
 * @return
 */

@SuppressWarnings("unused")
private float[][][] takeValueAtNextBirthDay( float[][][] inputArray){
		
		float[][][] returnArray;
		returnArray= new float [AGE_ARRAYSIZE][2][];
		
		for(int g=0 ; g<2;g++){
		for(int a=0 ; a<AGE_ARRAYSIZE-1;a++){
			returnArray[a][g]=new float [inputArray[a][g].length];
			for(int r=0 ; r<inputArray[a][g].length;r++)	
				returnArray[a][g][r]=0.5F*(inputArray[a][g][r]+inputArray[a+1][g][r]);}
		/* extrapolate the highest age */
		returnArray[AGE_ARRAYSIZE-1][g]=new float [inputArray[AGE_ARRAYSIZE-1][g].length];
		for(int r=0 ; r<inputArray[AGE_ARRAYSIZE-1][g].length;r++)
			returnArray[AGE_ARRAYSIZE-1][g][r]=inputArray[AGE_ARRAYSIZE-1][g][r];}
			
		return returnArray;
		
	}
} // end class

