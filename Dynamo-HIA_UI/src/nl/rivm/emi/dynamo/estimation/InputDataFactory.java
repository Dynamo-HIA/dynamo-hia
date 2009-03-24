package nl.rivm.emi.dynamo.estimation;

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
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InputDataFactory {

	Log log = LogFactory.getLog(getClass().getName());

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
	private static final String diseaseNameLabel = "name";
	private static final String diseasePrevFileLabel = "prevfilename";
	private static final String diseaseIncFileLabel = "incfilename";
	private static final String diseaseExcessMortFileLabel = "excessmortfilename";
	private static final String diseaseDalyWeightLabel = "dalyweightsfilename";
	private static final String riskFactorNameLabel = "name";
	private static final String riskFactorTransFileLabel = "riskfactortransfilename";

	private static final String isRRfromLabel = "isRRfrom";
	private static final String isRRtoLabel = "isRRto";
	private static final String isRRfileLabel = "isRRFile";
	private static final String scenarioNameLabel = "name";
	private static final String scenarioSuccessRateLabel = "successRate";
	private static final String targetMinAgeLabel = "targetMinAge";
	private static final String targetMaxAgeLabel = "targetMaxAge";
	private static final String targetGenderLabel = "targetSex";
	private static final String alternativeTransFileLabel = "transfilename";
	private static final String alternativePrevFileLabel = "prevfilename";

	/* the object containing the content of the XML configuration file */
	//private HierarchicalConfiguration configuration;

	// The XMLConfiguration instance contains XML configuration file contents
	// and offers methods for validation	
	private XMLConfiguration configuration;
	
	private ArraysFromXMLFactory factory = new ArraysFromXMLFactory();

	private int riskFactorType;

	private String riskFactorName;

	private String riskFactorTransDirName;

	private int originalNumberDurationClass;

	/* this are the public fields that contain the information from the XML file */
	public static boolean newborn = false;
	public static int startingYear = 2000;
	public static int numberOfYears = 10;
	public static int simPopSize = 10;
	public static int minAge = 0;
	public static int maxAge = 95;
	public static int timeStep = 1;
	public static int randomSeed = 9;
	public static boolean details = false;

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
	private static final String riskFactorDir = "Risk_Factors"; 
	//CDM uses "riskfactors"; document uses "risk factors"; dynamo uses "Risk_Factors"!!!!
	private static final String diseasesDir = "Diseases"; //OK //CDM used "diseases"
	private static final String populationDir = "Populations"; //OK //CDM used "populations"
	private static final String simulationDir = "Simulations"; //OK //CDM used "simulation"
	private static final String RRriskDir = "Relative_Risks_From_Risk_Factor"; //OK
	private static final String RRdiseaseDir = "Relative_Risks_From_Diseases"; //OK
	private static final String DALYWeightsDir = "DALY_Weights"; //OK
	private static final String prevalencesDir = "Prevalences"; //OK
	private static final String incidencesDir = "Incidences"; //OK
	private static final String excessMoratalitiesDir = "Excess_Mortalities"; //OK
	private static final String referenceDataDir = "Reference_Data"; //OK
	
	public static String populationName = "popFileName";
	private static final String sizeXMLname = "size.xml";
	private static final String allcauseXMLname = "overallmortality.xml";
	private static final String newbornXMLname = "newborns.xml";
	private static final String totDalyXMLname = "overalldalyweights.xml";

	private static final String riskfactorXMLname = "configuration.xml";
	private static final String classesXMLname = "classes.xml";
	private static final String transMatXMLname = "transitionmatrix.xml";
	private static final String transDriftXMLname = "transitiondrift.xml";
	private static final String riskfactorPrevXMLname = "prevalence.xml";
	private static final String durationXMLname = "durationdistribution.xml";
	private static final String allcauseRRXMLname = "relriskofdeath.xml";
	private static final String prefixRRcont = "continuousrelriskfrom";
	private static final String prefixRRcat = "categoricalrelriskfrom";
	private static final String prefixRRcompound = "compoundrelriskfrom.xml";
	private static final String prefixRRdis = "relriskfrom";

	private static final String rrDiseaseTagName = "relrisksfromdisease"; //"rrisksfromdisease"	
	private static final String rrContinuousTagName = "relrisksfromriskfactor_continous";//"rrisksforriskfactor_continuous"
	private static final String rrCategoricalTagName = "relrisksfromriskfactor_categorical";//"rrisksforriskfactor_categorical"
	private static final String rrCompoundTagName = "relrisksfromriskfactor_compound";//"rrisksforriskfactor_compound"	
	
	public InputDataFactory(String simName, String baseDir) throws DynamoConfigurationException {
		this.baseDir = baseDir;
		doIt(simName);

	}

	/**
	 * @param simName
	 * @throws DynamoConfigurationException
	 */
	private void doIt(String simName) throws DynamoConfigurationException {
		;
		

		String fileName = this.baseDir + File.separator
		+ simulationDir + File.separator + simName + File.separator
		+ "configuration.xml";
		try {
			this.configuration = new XMLConfiguration(fileName);
			
			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls load()). 
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			this.configuration.clear();
			
			// Validate the xml by xsd schema
			this.configuration.setValidating(true);			
			this.configuration.load();			
			
		} catch (ConfigurationException e) {
			String dynamoErrorMessage = "error reading file: "
				+ fileName
				+ " with message: " + e.getMessage() 
				+ "\n"
				+ "Root cause: ";
			ErrorMessageUtil.handleErrorMessage(this.log, dynamoErrorMessage,
					e, fileName);
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

		/*
		 * put this information in the scenario object
		 * 
		 * This is kind of arbitrary, as this is needed for setting up the
		 * configuration, but not for the post-processing and for the parameter
		 * estimation. however, to be save we put it in the scenario object, as
		 * this is also availlable in the postprocessing stage
		 */

		if (timeStep != 1)
			log.fatal("timestep given in configuration is " + timeStep
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
						"incomplete information for scenario in configuration file" + missingParameters.toString());

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
            
			/* check if there are no double disease names, otherwise the program will not work */
			
			
			
			flag = true; /* at least one disease has been successfully read */
			
            int nDis=disInfo.size();
            for (int i=0;i<nDis;i++)
            	for (int j=i+1; j<nDis;j++){
            		if 
			
					 (disInfo.get(i).name.compareToIgnoreCase(disInfo.get(j).name)==0) {
						 throw 
					new DynamoConfigurationException("two disease, "+disInfo.get(i).name+ " and "+
							disInfo.get(j).name+ " have the same name. This is not allowed. Please change one of"
							+" the names.");
						
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
	private boolean handleRiskfactorInfo(ConfigurationNode node)
			throws DynamoConfigurationException {

		boolean flag = false;
		List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) node
				.getChildren();
		String type = null;
		boolean namePresent = false;
		boolean transPresent = false;
		for (ConfigurationNode rootChild : rootChildren) {
			if (rootChild.getName() == riskFactorNameLabel) {
				riskFactorName = getString(rootChild, riskFactorNameLabel);
				namePresent = true;
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
		if (namePresent /* && prevPresent */&& transPresent
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
			if (value < 0) {
				throw new DynamoConfigurationException("negative " + Label
						+ " in XML file from window 1");
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
			throws DynamoConfigurationException,
			DynamoInconsistentDataException {
		// add general information

		;

		String sizeName = baseDir + File.separator + referenceDataDir
				+ File.separator + populationDir + File.separator
				+ populationName + File.separator + sizeXMLname;
		String newbornName = baseDir + File.separator + referenceDataDir
				+ File.separator + populationDir + File.separator
				+ populationName + File.separator + newbornXMLname;
		String dalyName = baseDir + File.separator + referenceDataDir
				+ File.separator + populationDir + File.separator
				+ populationName + File.separator + totDalyXMLname;
		String mortName = baseDir + File.separator + referenceDataDir
				+ File.separator + populationDir + File.separator
				+ populationName + File.separator + allcauseXMLname;

		scenarioInfo.setPopulationSize(factory.manufactureOneDimArray(sizeName,
				"populationsize", "size", "number", false));
		scenarioInfo.setOverallDalyWeight(factory.manufactureOneDimArray(
				dalyName, "overalldalyweights", "weight", "percent", false));
		/* put a copy in inputData */
		inputData.setOverallDalyWeight(scenarioInfo.getOverallDalyWeight());
		inputData.setMortTot(factory.manufactureOneDimArray(mortName,
				"overallmortality", "mortality", false));
		readNewbornData(newbornName, scenarioInfo);

	}

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
		scenarioInfo.setSimPopSize(simPopSize);
		scenarioInfo.setDetails(details);

		scenarioInfo.setDetails(details);
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
		scenarioInfo.setNewPrevalence(new float[scenInfo.size()][96][2][]);
		scenarioInfo.setNewOffset(new float[scenInfo.size()][96][2]);
		scenarioInfo.setNewMean(new float[scenInfo.size()][96][2]);
		scenarioInfo.setNewStd(new float[scenInfo.size()][96][2]);
		scenarioInfo.setIsNormal(new boolean[scenInfo.size()]);
		scenarioInfo
				.setAlternativeTransitionMatrix(new float[scenInfo.size()][][][][]);

		for (int scen = 0; scen < scenInfo.size(); scen++) {

			scenarioInfo.setScenarioNames(scenInfo.get(scen).name,scen);
			scenarioInfo.setMinAge( scenInfo.get(scen).minAge,scen);
			scenarioInfo.setMaxAge( scenInfo.get(scen).maxAge,scen);
			scenarioInfo.setSuccesrate(scenInfo.get(scen).rate,scen);
			scenarioInfo.setInMen( true,scen);
			scenarioInfo.setInWomen( true,scen);
			if (scenInfo.get(scen).gender == 0)
				scenarioInfo.setInWomen( false,scen);
			if (scenInfo.get(scen).gender == 1)
				scenarioInfo.setInMen( false,scen);

			/* reading and handling alternative prevalence information */

			if (scenInfo.get(scen).prevFileName.compareToIgnoreCase("none") == 0)
				scenarioInfo.setInitialPrevalenceType(false, scen);
			else {
				scenarioInfo.setInitialPrevalenceType(true, scen);

				String completePrevFileName = baseDir + File.separator
						+ referenceDataDir + File.separator + riskFactorDir
						+ File.separator + riskFactorName + File.separator
						+ scenInfo.get(scen).prevFileName + ".xml";
				if (riskFactorType != 2)
					scenarioInfo.setNewPrevalence(factory
							.manufactureTwoDimArray(completePrevFileName,
									"riskfactorprevalences_categorical",
									"prevalence", "cat", "percent", false),
							scen);
				else {
					scenarioInfo.setNewMeanSTD(factory.manufactureOneDimArray(
							completePrevFileName,
							"riskfactorprevalences_continuous", "prevalence",
							"standarddeviation", true), factory
							.manufactureOneDimArray(completePrevFileName,
									"riskfactorprevalences_continuous",
									"prevalence", "mean", true), factory
							.manufactureOneDimArray(completePrevFileName,
									"riskfactorprevalences_continuous",
									"prevalence", "skewness", true), scen);

				}
				/* reading and handling transition matrix info */

				if (scenInfo.get(scen).transFileName
						.compareToIgnoreCase("none") == 0)
				
					scenarioInfo.setTransitionType(false, scen);
					

				
				else {
					scenarioInfo.setTransitionType(true, scen);

					String completeTransFileName = baseDir + File.separator
							+ referenceDataDir + File.separator + riskFactorDir
							+ File.separator + riskFactorName + File.separator
							+ riskFactorTransDirName + File.separator
							+ scenInfo.get(scen).transFileName + ".xml";
					readTransitionData(completeTransFileName, null,
							scenarioInfo, 0);
				}

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

		String configFileName;

		configFileName = baseDir + File.separator + referenceDataDir
				+ File.separator + riskFactorDir + File.separator
				+ riskFactorName + File.separator + riskfactorXMLname;		
		XMLConfiguration config = null;
		try {

			config = new XMLConfiguration(configFileName);
			
			// WORKAROUND: clear() is put after the constructor (also calls load()). 
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			config.clear();
			// Validate the xml by xsd schema
			config.setValidating(true);			
			config.load();
			
		} catch (ConfigurationException e) {			
			String dynamoErrorMessage = "XML error encountered with message: " + e.getMessage();
			ErrorMessageUtil.handleErrorMessage(this.log, dynamoErrorMessage,
					e, configFileName);			
		}
		String type = ((XMLConfiguration) config).getRootElementName();
		if (type == "riskfactor_categorical") {
			riskFactorType = 1;
		} else {
			if (type == "riskfactor_continuous") {
				// TODO: Temporary build message: not yet implemented
				// TODO: Reactivate code below for version 1.1
				ErrorMessageUtil.handleErrorMessage(this.log, "The component Riskfactor Continuous has not yet been implemented",
						new DynamoConfigurationException("The component Riskfactor Continuous has not yet been implemented"), 
						configFileName);
				// TODO: Reactivate code below for version 1.1
				////riskFactorType = 2;
			} else {
				if (type == "riskfactor_compound") {
					// TODO: Temporary build message: not yet implemented
					// TODO: Reactivate code below for version 1.1
					ErrorMessageUtil.handleErrorMessage(this.log, "The component Riskfactor Continuous has not yet been implemented",
							new DynamoConfigurationException("The component Riskfactor Compound has not yet been implemented"), 
							configFileName);
					// TODO: Reactivate code below for version 1.1					
					riskFactorType = 3;
				} else
					throw new DynamoConfigurationException(
							"no valid main tag (riskfactor_type) found but found  "
									+ type + " in XML file " + configFileName);
			}

		}

		inputData.setRiskType(riskFactorType);

		scenarioInfo.setRiskType(riskFactorType);
		// make file name for riskfactor configurationfile

		/* now read in all the data in the sequence of the user data document */

		if (riskFactorType == 2)
			inputData.setRefClassCont(getFloat("referencevalue", config));
		else
			inputData.setRefClassCont(0);
		if (riskFactorType == 3) {
			inputData.setIndexDuurClass(getInteger("durationclass", config));
			
			/*
			 * keep the original number as this is needed later to read from
			 * this number the relative risks for duration
			 */

			originalNumberDurationClass = inputData.getIndexDuurClass();
		} else
			inputData.setIndexDuurClass(0);
		if (riskFactorType != 2)
			scenarioInfo
					.setReferenceClass(getInteger("referenceclass", config));
		else
			scenarioInfo.setReferenceClass(0);

		/*
		 * now read the class names
		 */

		ConfigurationNode rootNode = config.getRootNode();
		if (riskFactorType != 2) {
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
					scenarioInfo.setIndexDurationClass(inputData.getIndexDuurClass());
					if (index2[index.length - 1] - index2[0] != index.length - 1)
						throw new DynamoConfigurationException(
								" class numbers missing "
										+ " in riskfactor configuration file  ");
				}

			}
		}
		//
		/* read transition information */
		//
		//
		if (riskFactorType != 2)
			configFileName = baseDir + File.separator + referenceDataDir
					+ File.separator + riskFactorDir + File.separator
					+ riskFactorName + File.separator + riskFactorTransDirName
					+ File.separator + transMatXMLname;
		else
			configFileName = baseDir + File.separator + referenceDataDir
					+ File.separator + riskFactorDir + File.separator
					+ riskFactorName + File.separator + riskFactorTransDirName
					+ File.separator + transDriftXMLname;

		readTransitionData(configFileName, inputData, null, 0);

		//
		/* read prevalence information */
		//
		//
		//
		/* first for categorical/compound */
		//
		configFileName = baseDir + File.separator + referenceDataDir
				+ File.separator + riskFactorDir + File.separator
				+ riskFactorName + File.separator + riskfactorPrevXMLname;
		if (riskFactorType != 2) {
			inputData.setPrevRisk(factory.manufactureTwoDimArray(
					configFileName, "riskfactorprevalences_categorical",
					"prevalence", "cat", "percent", false), true);
			scenarioInfo.setOldPrevalence(inputData.getPrevRisk());
		} else {

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

			inputData.setDuurFreq(factory.manufactureTwoDimArray(
					configFileName, "riskfactorprevalences_duration",
					"prevalence", "duration", "percent", false), true);
			scenarioInfo.setOldDurationClasses(inputData.getDuurFreq());
		}

		//
		/* now read RR for all cause mortality */
		//
		//
		//
		/* for categorical/compound */

		float[][][] data3dim = new float[96][2][1];
		float[][] data2dim = new float[96][2];
		for (int a = 0; a < 96; a++)
			for (int g = 0; g < 2; g++) {
				data3dim[a][g][0] = 1;
				data2dim[a][g] = 1;

			}
		/* for categorical */
		if (riskFactorType == 1) {
			configFileName = baseDir + File.separator + referenceDataDir
					+ File.separator + riskFactorDir + File.separator
					+ riskFactorName + File.separator + allcauseRRXMLname;

			inputData.setRelRiskMortCat(factory.manufactureTwoDimArray(
					configFileName, "relrisksfordeath_categorical",
					"relriskfordeath", "cat", "value", false));
			inputData.setRelRiskMortCont(data2dim);

		}
		/* for continuous */

		if (riskFactorType == 2) {
			configFileName = baseDir + File.separator + referenceDataDir
					+ File.separator + riskFactorDir + File.separator
					+ riskFactorName + File.separator + allcauseRRXMLname;

			inputData.setRelRiskMortCont(factory.manufactureOneDimArray(
					configFileName, "relrisksfordeath_continuous",
					"relriskfordeath", "value", false));
			inputData.setRelRiskMortCat(data3dim);
		}
		/*
		 * for compound
		 */
		if (riskFactorType == 3) {
			configFileName = baseDir + File.separator + referenceDataDir
					+ File.separator + riskFactorDir + File.separator
					+ riskFactorName + File.separator + allcauseRRXMLname;

			inputData.setRelRiskMortCat(factory.manufactureTwoDimArray(
					configFileName, "relrisksfordeath_compound",
					"relriskfordeath", "cat", "value", true));
			inputData.setRelRiskDuurMortBegin(factory.selectOneDimArray(
					configFileName, "relrisksfordeath_compound",
					"relriskfordeath", "begin", "cat",
					originalNumberDurationClass));
			inputData.setRelRiskDuurMortEnd(factory.selectOneDimArray(
					configFileName, "relrisksfordeath_compound",
					"relriskfordeath", "end", "cat",
					originalNumberDurationClass));
			inputData.setAlphaMort(factory.selectOneDimArray(configFileName,
					"relrisksfordeath_compound", "relriskfordeath", "alfa",
					"cat", originalNumberDurationClass));

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

		XMLConfiguration config = null;
		if (riskFactorType != 2) {
			try {
				config = new XMLConfiguration(configFileName);

				// Validate the xml by xsd schema
				// WORKAROUND: clear() is put after the constructor (also calls load()). 
				// The config cannot be loaded twice,
				// because the contents will be doubled.
				config.clear();
				
				// Validate the xml by xsd schema
				config.setValidating(true);			
				config.load();			
			} catch (ConfigurationException e) {
				String dynamoErrorMessage = "reading error encountered when reading file: "
					+ configFileName + " with message: "
					+ e.getMessage(); 
				ErrorMessageUtil.handleErrorMessage(this.log, dynamoErrorMessage,
						e, configFileName);
			}
			if (((XMLConfiguration) config).getRootElementName() == "transitionmatrix_zero") {
				if (inputData != null)
					inputData.setTransType(0);
				else if (scenInfo != null)
					scenInfo.setZeroTransition(true, scenNumber);
				else
					throw new DynamoConfigurationException(
							"Both scenInfo and Input data are null in method"
									+ "readTransitionData, so information can not be added to anything");
			}

			else if (((XMLConfiguration) config).getRootElementName() == "transitionmatrix_netto") {
				if (inputData != null)
					inputData.setTransType(1);

				else
					throw new DynamoConfigurationException(
							"netto transition file not possbile for alternative scenario."
									+ "For alternative scenario's one should enter an explicite file");

			}

			else if (((XMLConfiguration) config).getRootElementName() == "transitionmatrix") {
				if (inputData != null) {
					inputData.setTransType(2);

					inputData.setTransitionMatrix(factory
							.manufactureThreeDimArray(configFileName,
									"transitionmatrix", "transition", "from",
									"to", "percent"));
				}

				else if (scenInfo != null)
					scenInfo.getAlternativeTransitionMatrix()[scenNumber] = factory
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

				// Validate the xml by xsd schema
				// WORKAROUND: clear() is put after the constructor (also calls load()). 
				// The config cannot be loaded twice,
				// because the contents will be doubled.
				config.clear();
				
				// Validate the xml by xsd schema
				config.setValidating(true);			
				config.load();			
			} catch (ConfigurationException e) {
				String dynamoErrorMessage = "error encountered while reading file: "
					+ configFileName + " with message: "
					+ e.getMessage(); 
				ErrorMessageUtil.handleErrorMessage(this.log, dynamoErrorMessage,
						e, configFileName);
			}
			if (((XMLConfiguration) config).getRootElementName() == "transitiondrift_zero") {
				inputData.setTransType(0);
			}

			else if (((XMLConfiguration) config).getRootElementName() == "transitiondrift_netto") {
				inputData.setTransType(1);
			}

			else if (((XMLConfiguration) config).getRootElementName() == "transitiondrift") {
				inputData.setTransType(2);
				inputData.setMeanDrift(factory.manufactureOneDimArray(
						configFileName, "transitionmatrix", "transition",
						"mean", true));
				/* obsolete but kept for potential future use
				inputData.setStdDrift(factory.manufactureOneDimArray(
						configFileName, "transitionmatrix", "transition",
						"standarddevivation", true));
				inputData.setOffsetDrift(factory.manufactureOneDimArray(
						configFileName, "transitionmatrix", "transition",
						"skewness", true));
						*/
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
		scenarioInfo.setStructure(clusterStructure);

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
						+ RRdiseaseDir + File.separator + info.rrFileName
						+ info.from + ".xml";
								
				info.rrDataDis = factory.manufactureOneDimArray(configFileName,
						rrDiseaseTagName, "relativerisk", "value", false);

			} else {
				if (riskFactorType == 2) {
					String configFileName = baseDir + File.separator
							+ referenceDataDir + File.separator + diseasesDir
							+ File.separator + info.to + File.separator
							+ RRriskDir + File.separator + info.rrFileName
							+ info.from + ".xml";
					info.rrDataCont = factory.manufactureOneDimArray(
							configFileName, rrContinuousTagName,
							"relativerisk", "value", false);
				} else if (riskFactorType == 1) {

					String configFileName = baseDir + File.separator
							+ referenceDataDir + File.separator + diseasesDir
							+ File.separator + info.to + File.separator
							+ RRriskDir + File.separator + info.rrFileName
							+ info.from + ".xml";
					info.rrDataCat = factory.manufactureTwoDimArray(
							configFileName, rrCategoricalTagName,
							"relativerisk", "cat", "value", false);
				} else {
					String configFileName = baseDir + File.separator
							+ referenceDataDir + File.separator + diseasesDir
							+ File.separator + info.to + File.separator
							+ RRriskDir + File.separator + info.rrFileName
							+ info.from + ".xml";

					info.rrDataCat = factory.manufactureTwoDimArray(
							configFileName, rrCompoundTagName,
							"relativerisk", "cat", "value", true);
					info.rrDataBegin = factory.selectOneDimArray(
							configFileName, rrCompoundTagName,
							"relativerisk", "begin", "cat",
							originalNumberDurationClass);
					info.rrDataEnd = factory.selectOneDimArray(configFileName,
							rrCompoundTagName, "relativerisk",
							"end", "cat", originalNumberDurationClass);
					info.rrDataAlfa = factory.selectOneDimArray(configFileName,
							rrCompoundTagName, "relativerisk",
							"alfa", "cat", originalNumberDurationClass);

				}

			}
		}// end loop over rr's

		DiseaseClusterData[][][] clusterData = new DiseaseClusterData[96][2][nClusters];

		for (int c = 0; c < nClusters; c++) {

			pData = new float[clusterStructure[c].getNInCluster()][96][2];
			iData = new float[clusterStructure[c].getNInCluster()][96][2];
			eData = new float[clusterStructure[c].getNInCluster()][96][2];
			fData = new float[clusterStructure[c].getNInCluster()][96][2];
			cData = new float[clusterStructure[c].getNInCluster()][96][2];
			dData = new float[clusterStructure[c].getNInCluster()][96][2];

			for (int d = 0; d < clusterStructure[c].getNInCluster(); d++) {
				String thisDisease = clusterStructure[c].getDiseaseName()
						.get(d);

				/* find the DisInfo element for this disease */

				for (int dTot = 0; dTot < nDiseases; dTot++) {
					info2 = disInfo.get(dTot);
					if (thisDisease == info2.name) {
						// prevalence
						String configFileName = baseDir + File.separator
								+ referenceDataDir + File.separator
								+ diseasesDir + File.separator + thisDisease
								+ File.separator + prevalencesDir
								+ File.separator + info2.prevFileName + ".xml";
						pData[d] = factory.manufactureOneDimArray(
								configFileName, "diseaseprevalences",
								"prevalence", "percent", false);
						configFileName = baseDir + File.separator
								+ referenceDataDir + File.separator
								+ diseasesDir + File.separator + thisDisease
								+ File.separator + this.incidencesDir
								+ File.separator + info2.incFileName + ".xml";
						iData[d] = factory.manufactureOneDimArray(
								configFileName, "diseaseincidences",
								"incidence", "value", false);
						configFileName = baseDir + File.separator
								+ referenceDataDir + File.separator
								+ diseasesDir + File.separator + thisDisease
								+ File.separator + this.excessMoratalitiesDir
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
						XMLConfiguration config = null;
						try {
							config = new XMLConfiguration(configFileName);
							
							// Validate the xml by xsd schema
							// WORKAROUND: clear() is put after the constructor (also calls load()). 
							// The config cannot be loaded twice,
							// because the contents will be doubled.
							config.clear();							
							
							// Validate the xml by xsd schema
							config.setValidating(true);			
							config.load();			
						} catch (ConfigurationException e) {
							String dynamoErrorMessage = "error encountered when reading file: "
								+ configFileName
								+ " with message: "
								+ e.getMessage();
							ErrorMessageUtil.handleErrorMessage(this.log, dynamoErrorMessage,
									e, configFileName);
						}
						String unitType = getName("unittype", config);
						if (unitType.compareToIgnoreCase("Median survival") == 0)

							eData[d] = excessRate(eData[d], thisDisease);
						// TODO nog een keer checken of omrekening klopt
						configFileName = baseDir + File.separator
								+ referenceDataDir + File.separator
								+ diseasesDir + File.separator + thisDisease
								+ File.separator + this.DALYWeightsDir
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
					.getNInCluster()][clusterStructure[c].getNInCluster()];

			for (int a = 0; a < 96; a++)
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
													.getDiseaseName().get(d)) == 0
											&& isRRfrom[rrDis]
													.compareToIgnoreCase(clusterStructure[c]
															.getDiseaseName()
															.get(d2)) == 0)

										RRdisExtended[d2][d] = rrInfo
												.get(rrNumber[rrDis]).rrDataDis[a][g];
									break;
								}

							}

						}

						else
							for (int d2 = 0; d2 < clusterStructure[c]
									.getNInCluster(); d2++)
								RRdisExtended[d2][d] = 1;

					}
					int nclasses = 1;
					if (riskFactorType != 2)
						nclasses = inputData.getPrevRisk()[0][0].length;

					clusterData[a][g][c] = new DiseaseClusterData(
							clusterStructure[c], nclasses, RRdisExtended);

					/* enter the data from diseases */
					for (int d = 0; d < clusterStructure[c].getNInCluster(); d++) {
						clusterData[a][g][c].setPrevalence(
								pData[d][a][g] / 100, d);
						clusterData[a][g][c].setIncidence(iData[d][a][g], d);
						clusterData[a][g][c].setExcessMortality(eData[d][a][g],
								d);
						clusterData[a][g][c].setDisability(dData[d][a][g]/100, d);
						clusterData[a][g][c].setCuredFraction(cData[d][a][g]/100,
								d, clusterStructure[c]);
						clusterData[a][g][c].setCaseFatality(fData[d][a][g]/100, d);

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
							if (info.to.compareToIgnoreCase(clusterStructure[c]
									.getDiseaseName().get(d)) == 0) {
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

	public float[][] excessRate(float[][] medianSurvival, String diseaseLabel)
			throws DynamoInconsistentDataException {
		float[][] rate = new float[96][2];
		double log2 = Math.log(2);
		double[][] drate = new double[96][2];
		double medianAge;
		double aaRate;
		double yearOfMedian;
		double sumRate;
		for (int g = 0; g < 2; g++) {
			drate[95][g] = (log2 / medianSurvival[95][g]);
			rate[95][g] = (float) drate[95][g];
			for (int a = 94; a >= 0; a--) {
				medianAge = a + medianSurvival[a][g];
				yearOfMedian = a + Math.floor(medianAge);
				sumRate = 0;
				{
					for (int aa = a + 1; aa < a + Math.ceil(medianAge); aa++) {
						if (aa > 95)
							aaRate = drate[95][g];
						else
							aaRate = drate[aa][g];
						if (aa == yearOfMedian)
							sumRate += (medianAge - yearOfMedian) * aaRate;
						else
							sumRate += aaRate;
					}
					drate[a][g] = log2 - sumRate;
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
		}

		return rate;

	}

	private void readNewbornData(String configFileName, ScenarioInfo scenInfo)
			throws DynamoConfigurationException,
			DynamoInconsistentDataException {

		XMLConfiguration config = null;
		/* make an array large enough for all cases */

		int[] year = new int[200];
		int[] number = new int[200];
		int currentChild = 0;
		try {
			config = new XMLConfiguration(configFileName);

			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls load()). 
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			config.clear();	
			config.setValidating(true);
			config.load();
		} catch (ConfigurationException e) {
			String dynamoErrorMessage = "Reading error encountered when reading file: "
				+ configFileName + " with message: "
				+ e.getMessage();
			ErrorMessageUtil.handleErrorMessage(this.log, dynamoErrorMessage,
					e, configFileName);			
		}

		if (((XMLConfiguration) config).getRootElementName() == "newborns") {
			if (scenInfo != null) {
				log.debug("config.getFloat(sexratio)" + config.getFloat("sexratio"));
				scenInfo.setMaleFemaleRatio(config.getFloat("sexratio"));

				ConfigurationNode rootNode = config.getRootNode();
				
				List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
						.getChildren();

				for (ConfigurationNode rootChild : rootChildren) {
					if (rootChild.getName() != "amount"
							&& rootChild.getName() != "sexratio")
						throw new DynamoConfigurationException(" Tagname "
								+ "amount or sexratio expected in file "
								+ configFileName + " but found tag "
								+ rootChild.getName());

					if (rootChild.getName() == "amount") {
						List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
								.getChildren();						
						for (ConfigurationNode leafChild : leafChildren) {
							Object valueObject = leafChild.getValue();
							String leafName = leafChild.getName();
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
														+ " this is not allowed");}
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

				/* ready with reading, now make arrays to store */

				;
			}
		} else
			throw new DynamoConfigurationException(
					"label newborns expected but found label: "
							+ ((XMLConfiguration) config).getRootElementName()
							+ " in file : " + configFileName);

		int startYear = year[0];
		scenInfo.setStartYear(startYear);
		int newborns[] = new int[currentChild];

		for (int i = 0; i < currentChild; i++)
			newborns[i] = number[i];
		scenInfo.setNewborns(newborns);

	}

	public int getIntegerValue(String value, String tag)
			throws DynamoConfigurationException {
		int returnvalue = 0;
		if (value == null)
			throw new DynamoConfigurationException("no value found with " + tag);
		else
			returnvalue = Integer.parseInt(value);
		return returnvalue;

	}

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

