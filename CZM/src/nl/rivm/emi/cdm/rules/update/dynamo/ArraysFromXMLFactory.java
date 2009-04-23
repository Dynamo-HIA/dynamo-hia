package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Hendriek; variant of IncidencesFactory from Rene Mondeel
 * <p>
 * The Factory produces arrays from "flat" XML files each containing only a particular model parameters 
 * by age, sex and zero,one or two other dimensions. </p><p>
 * The XML files then should have three, four or five different tags on the inner level. 
 * The first two tags always should be "age" and "sex". 
 * Default values for the last tag is "value", for the third in case of reading a three dimensional array: "cat"
 * and for the third and fourth in case of reading a four dimensional array: "to" and "from".
 * </p>
 */

/**
 * @author Hendriek
 *
 */
/**
 * @author Hendriek
 * 
 */
public class ArraysFromXMLFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());
	private float[][][][] checkArray;
	public boolean detailedDebug = false;

	public ArraysFromXMLFactory() {
		super();

	}
    
	
	
	/**
	 * the method produces a two dimensional array from flat XML containing
	 * particular model parameters by age, sex .
	 * <p>
	 * tag of the element containing the value to be read is assumed to be
	 * (optional)
	 * </p>
	 * 
	 * @param fileName
	 *            : name of xml.file to be read (including the extension .xml)
	 * @param globalTagName
	 *            : root tag in the file
	 * @param tagName
	 *            : tag of the individual items in the file
	 * @param otherTags
	 *            : boolean indicating (if true) that other tags are allowed to be present            
	 * @return two dimensional array (float[96][2]) of parameters by age and sex
	 * @throws DynamoConfigurationException
	 */
	public float[][] manufactureOneDimArray(String fileName,
			String globalTagName, String tagName, boolean otherTags)
			throws DynamoConfigurationException {

		float[][] arrayToBeFilled = manufactureOneDimArray(fileName,
				globalTagName, tagName, "value", otherTags);
		return arrayToBeFilled;
	}

	/**
	 * the method produces a two dimensional array from flat XML containing
	 * particular model parameters by age, sex .
	 * 
	 * @param fileName
	 *            : name of xml.file to be read (including the extension .xml)
	 * @param globalTagName
	 *            : root tag in the file
	 * @param tagName
	 *            : tag of the individual items in the file
	 * @param valueTagName
	 *            : tag of the element containing the value to be read
	 *            (optional)
	 * @return two dimensional array (float[96][2]) of parameters by age and sex
	 * @throws ConfigurationException 
	 */
	public float[][] manufactureOneDimArray(String fileName,
			String globalTagName, String tagName, String valueTagName,
			boolean otherTags) throws DynamoConfigurationException {
		float[][] returnArray = new float[96][2];
		checkArray = new float[96][2][1][1];

		for (int sex = 0; sex < 2; sex++)
			for (int age = 0; age < 96; age++) {

				returnArray[age][sex] = 0;
				checkArray[age][sex][0][0] = 0;
			}
		File configurationFile = new File(fileName);

		log.debug("Starting manufacture from file " + fileName);

		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);
						
			/**
			TODO: VALIDATION IS FOR FUTURE USE 
			NICE TO HAVE FEATURE
			KEEP IT IN THE CODE
			
			The following schemas are not validated:
			baselineOtherMortalities.xsd
			baselineIncidences.xsd
			baselineFatalIncidences.xsd
			attributableMortalities.xsd
			*/
			
			if (!"baselineOtherMortalities".equals(configurationFromFile.getRootElementName())
					&& !"baselineIncidences".equals(configurationFromFile.getRootElementName())
					&& !"baselineFatalIncidences".equals(configurationFromFile.getRootElementName())
					&& !"attributableMortalities".equals(configurationFromFile.getRootElementName())
			
			) {
				// Validate the xml by xsd schema
				// WORKAROUND: clear() is put after the constructor (also calls load()). 
				// The config cannot be loaded twice,
				// because the contents will be doubled.
				configurationFromFile.clear();
	
				// Validate the xml by xsd schema
				configurationFromFile.setValidating(true);			
				configurationFromFile.load();
			}
			
			ConfigurationNode rootNode = configurationFromFile.getRootNode();

			if (configurationFromFile.getRootElementName() != globalTagName)
				throw new DynamoConfigurationException(" Tagname "
						+ globalTagName + " expected in file " + fileName
						+ " but found tag "
						+ configurationFromFile.getRootElementName());

			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();

			for (ConfigurationNode rootChild : rootChildren) {
				if (detailedDebug)
					log.debug("Handle rootChild: " + rootChild.getName());
				if (rootChild.getName() != tagName && !otherTags)
					throw new DynamoConfigurationException(" Tagname "
							+ tagName + " expected in file " + fileName
							+ " but found tag " + rootChild.getName());

				if (rootChild.getName() == tagName)
					returnArray = handleRootChild(rootChild, returnArray,
							valueTagName, otherTags);
			} // end loop for rootChildren

			/* check if the input file was complete */
			for (int sex = 0; sex < 2; sex++)
				for (int age = 0; age < 96; age++)
					if (checkArray[age][sex][0][0] != 1)
						throw new DynamoConfigurationException(
								"no value read in parameter file for age="
										+ age + " sex=" + sex);

			return returnArray;
		} catch (ConfigurationException e) {
			String cdmErrorMessage = "Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage() + "from file "
					+ fileName;
			ErrorMessageUtil.handleErrorMessage(this.log, cdmErrorMessage, e, fileName);
		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage() + "from file " + fileName);
			exception.printStackTrace();
			throw new DynamoConfigurationException("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage() + "from file " + fileName);

		}
		return returnArray;
	}
	/**
	 * the method produces a two dimensional array from flat XML containing
	 * particular model parameters by age, sex . In this case there are three tags "above" 
	 * the data to read (in stead of two)
	 * 
	 * @param fileName
	 *            : name of xml.file to be read (including the extension .xml)
	 * @param globalTagName
	 *            : root tag in the file
	 * @param tagName1  : first nested  tag of the individual items in the file
	 * @param tagName2 
	
	 *            : second nested tag of the individual items in the file
	 * @param valueTagName
	 *            : tag of the element containing the value to be read
	 *            (optional)
	 * @param otherTags 
	 * @return two dimensional array (float[96][2]) of parameters by age and sex
	 * @throws DynamoConfigurationException 
	 * @throws ConfigurationException 
	 */
	@SuppressWarnings("unchecked")
	
	public float[][] manufactureOneDimArrayFromTreeLayeredXML(String fileName,
			String globalTagName, String tagName1, String tagName2, String valueTagName,
			boolean otherTags) throws DynamoConfigurationException {
		float[][] returnArray = new float[96][2];
		this.checkArray = new float[96][2][1][1];

		for (int sex = 0; sex < 2; sex++)
			for (int age = 0; age < 96; age++) {

				returnArray[age][sex] = 0;
				this.checkArray[age][sex][0][0] = 0;
			}
		File configurationFile = new File(fileName);

		this.log.debug("Starting manufacture from file " + fileName);

		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);
						
			/**
			TODO: VALIDATION IS FOR FUTURE USE 
			NICE TO HAVE FEATURE
			KEEP IT IN THE CODE
			
			The following schemas are not validated:
			baselineOtherMortalities.xsd
			baselineIncidences.xsd
			baselineFatalIncidences.xsd
			attributableMortalities.xsd
			*/
			
			if (!"baselineOtherMortalities".equals(configurationFromFile.getRootElementName())
					&& !"baselineIncidences".equals(configurationFromFile.getRootElementName())
					&& !"baselineFatalIncidences".equals(configurationFromFile.getRootElementName())
					&& !"attributableMortalities".equals(configurationFromFile.getRootElementName())
			
			) {
				// Validate the xml by xsd schema
				// WORKAROUND: clear() is put after the constructor (also calls load()). 
				// The config cannot be loaded twice,
				// because the contents will be doubled.
				configurationFromFile.clear();
	
				// Validate the xml by xsd schema
		// TODO: weer aanzetten		configurationFromFile.setValidating(true);			
				configurationFromFile.load();
			
			
			ConfigurationNode rootNode = configurationFromFile.getRootNode();

			if (configurationFromFile.getRootElementName() != globalTagName)
				throw new DynamoConfigurationException(" Tagname "
						+ globalTagName + " expected in file " + fileName
						+ " but found tag "
						+ configurationFromFile.getRootElementName());
			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
			.getChildren();
			for (ConfigurationNode rootChild : rootChildren) {
			if (rootChild.getName() == tagName1  )
			{
			List<ConfigurationNode> childChildren = (List<ConfigurationNode>) rootChild.getChildren();

			for (ConfigurationNode childChild : childChildren) {
				if (this.detailedDebug)
					this.log.debug("Handle rootChild: " + childChild.getName());
				if (childChild.getName() != tagName2 && !otherTags)
					throw new DynamoConfigurationException(" Tagname "
							+ tagName2 + " expected in file " + fileName
							+ " but found tag " + childChild.getName());

				if (childChild.getName() == tagName2)
					returnArray = handleRootChild(childChild, returnArray,
							valueTagName, otherTags);
			} // end loop for childChildren
			}
			}// end loop for rootChildren
			}
			/* check if the input file was complete */
			for (int sex = 0; sex < 2; sex++)
				for (int age = 0; age < 96; age++)
					if (this.checkArray[age][sex][0][0] != 1)
						throw new DynamoConfigurationException(
								"no value read in parameter file for age="
										+ age + " sex=" + sex);

			return returnArray;
		} catch (ConfigurationException e) {
			String cdmErrorMessage = "Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage() + "from file "
					+ fileName;
			ErrorMessageUtil.handleErrorMessage(this.log, cdmErrorMessage, e, fileName);
		} catch (Exception exception) {
			this.log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage() + "from file " + fileName);
			exception.printStackTrace();
			throw new DynamoConfigurationException("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage() + "from file " + fileName);

		}
		return returnArray;
	}
			
	
	
	
	/**
	 * the method reads in the most inner group of values when containing three
	 * values (for making a two dimensional array)
	 * 
	 * @param rootChild
	 *            : the element contain the inner group of values
	 * @param arrayToBeFilled
	 *            : the array to be filled (often already partly filled)
	 * @param valueTagName
	 *            : the tag name of the value to should be put into the array
	 * @param otherTags
	 *            : boolean indicating if other tags are allow to be present
	 * @return array to which the newly read value has been added
	 * @throws DynamoConfigurationException
	 */
	private float[][] handleRootChild(ConfigurationNode rootChild,
			float[][] arrayToBeFilled, String valueTagName, boolean otherTags)
			throws DynamoConfigurationException {
		// String rootChildName = rootChild.getName();
		// Object rootChildValueObject = rootChild.getValue();
		Integer age = null;
		Integer sex = null;
		Float value = null;
		boolean sexRead = false;
		boolean ageRead = false;

		boolean valueRead = false;
		List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
				.getChildren();
		for (ConfigurationNode leafChild : leafChildren) {
			// log.debug("Handle leafChild: " + leafChild.getName());
			String leafName = leafChild.getName();
			Object valueObject = leafChild.getValue();
			if (valueObject instanceof String) {
				String valueString = (String) valueObject;
				if ("age".equalsIgnoreCase(leafName)) {

					age = getIntegerValue(valueString, "age");
					ageRead = true;
				} else {
					if ("sex".equalsIgnoreCase(leafName)) {

						sex = getIntegerValue(valueString, "sex");
						;
						sexRead = true;
					} else {
						if (valueTagName.equalsIgnoreCase(leafName)) {

							value = getFloatValue(valueString, valueTagName);
							valueRead = true;
						} else {
							if (!otherTags)
								throw new DynamoConfigurationException(
										"Unexpected tag: " + leafName);
						}
					}
				}
			} else {
				throw new DynamoConfigurationException("Value is no String!");
			}
		} // for leafChildren
		if (!(ageRead && sexRead && valueRead))
			throw new DynamoConfigurationException(
					"Tag missing when processing value for age " + age
							+ " sex: " + sex + "\nPresentValue: " + value);
		if (arrayToBeFilled[age][sex] != 0) {
			throw new DynamoConfigurationException("Duplicate value for age: "
					+ age + " sex: " + sex + "\nPresentValue: "
					+ arrayToBeFilled[age][sex] + " newValue: " + value);
		} else {
			if (age >= arrayToBeFilled.length)
				throw new DynamoConfigurationException(
						"Value for age is to large: " + age + "for sex: " + sex);
			else
				arrayToBeFilled[age][sex] = value;
			checkArray[age][sex][0][0]++;
			if (detailedDebug)
				log.debug("Processing value for age: " + age + " sex: " + sex
						+ " value: " + value);

		}
		return arrayToBeFilled;
	}

	/**
	 * the method produces a three dimensional array from flat XML containing
	 * particular model parameters by age, sex and a third dimension. the tag of
	 * the element containing the value to be read is set to "value", that of
	 * the third dimension is set to "cat"
	 * 
	 * @param fileName
	 *            : name of xml.file to be read (including the extension .xml)
	 * @param globalTagName
	 *            : root tag in the file
	 * @param tagName
	 *            : tag of the individual items in the file
	 * @param otherTags
	 *            : boolean indicating if other tags are allow to be present
	 * 
	 * @return three dimensional array (float[96][2][]) of parameters by age and
	 *         sex
	 * @throws ConfigurationException
	 */

	public float[][][] manufactureTwoDimArray(String fileName,
			String globalTagName, String tagName, boolean otherTags)
			throws DynamoConfigurationException {

		float[][][] arrayToBeFilled = manufactureTwoDimArray(fileName,
				globalTagName, tagName, "cat", "value", otherTags);
		return arrayToBeFilled;
	}

	/**
	 * the method produces a three dimensional array from flat XML containing
	 * particular model parameters by age, sex and a third dimension .
	 * 
	 * @param fileName
	 *            : name of xml.file to be read (including the extension .xml)
	 * @param globalTagName
	 *            : root tag in the file
	 * @param tagName
	 *            : tag of the individual items in the file
	 * @param value1TagName
	 *            : tag of the element indicating the third dimension
	 * @param value2TagName
	 *            : tag of the element containing the value to be read
	 * @param otherTags
	 *            : (boolean) true if other tags are allowed to be present in the file
	 * @return three dimensional array (float[96][2][]) of parameters by age and
	 *         sex
	 *         
	 * @throws ConfigurationException
	 */

	public float[][][] manufactureTwoDimArray(String fileName,
			String globalTagName, String tagName, String value1TagName,
			String value2TagName, Boolean otherTags) throws DynamoConfigurationException {

		File configurationFile = new File(fileName);

		log.debug("Starting manufacturing four Dimensional array from file "
				+ fileName);

		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);
			/**
			TODO: VALIDATION IS FOR FUTURE USE 
			NICE TO HAVE FEATURE
			KEEP IT IN THE CODE
			
			The following schemas are not be validated:
			relativeRisks.xsd
			
			*/

			if (!"relativeRisks".equals(configurationFromFile.getRootElementName())) {
				// Validate the xml by xsd schema
				// WORKAROUND: clear() is put after the constructor (also calls load()). 
				// The config cannot be loaded twice,
				// because the contents will be doubled.
				configurationFromFile.clear();
				
				// Validate the xml by xsd schema
				configurationFromFile.setValidating(true);			
				configurationFromFile.load();							
			}			
			
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			if (configurationFromFile.getRootElementName() != globalTagName)
				throw new ConfigurationException(" Tagname "
						+ globalTagName + " expected in file " + fileName
						+ " but found tag "
						+ configurationFromFile.getRootElementName());

			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();

			/* first find out how many elements there are */

			List<Integer> indexL = configurationFromFile.getList(tagName + "."
					+ value1TagName);
			Iterator it = indexL.iterator();
			int maxIndex = 0;
			int minIndex = 1000;
			while (it.hasNext()) {
				int curVar = Integer.parseInt((String) it.next());
				if (curVar > maxIndex)
					maxIndex = curVar;
				if (curVar < minIndex)
					minIndex = curVar;
			}
			// TODO: program this in a way that gaps are allowed in the
			// numbering of categories, and check that they are consistent with the
			// class numbering as given in the configurationfiles
			
			/*
			 * this can be done by not only storing minimum and maximum, but all
			 * possible values,
			 */
			/* now initialize the arrays */
			float[][][] returnArray1 = new float[96][2][maxIndex + 1];
			checkArray = new float[96][2][maxIndex + 1][1];

			for (int sex = 0; sex < 2; sex++)
				for (int age = 0; age < 96; age++)
					for (int cat = 0; cat <= maxIndex; cat++) {

						returnArray1[age][sex][cat] = 0;
						checkArray[age][sex][cat][0] = 0;
					}

			for (ConfigurationNode rootChild : rootChildren) {
				if (detailedDebug)
					log.debug("Handle rootChild: " + rootChild.getName());
				if (!otherTags & rootChild.getName() != tagName)
					throw new ConfigurationException(" Tagname "
							+ tagName + " expected in file " + fileName
							+ " but found tag " + rootChild.getName()
							+ "in file " + fileName);
			}

			for (ConfigurationNode rootChild : rootChildren) {
				returnArray1 = handleRootChild(rootChild, returnArray1,
						value1TagName, value2TagName, otherTags);

			} // end loop for rootChildren

			/*
			 * check whether the data are complete assuming that the numbering
			 * of categories is allowed to start at minIndex (not necessarily
			 * zero) but then there should be no gaps
			 */

			for (int cat = minIndex; cat <= maxIndex; cat++)
				for (int sex = 0; sex < 2; sex++)
					for (int age = 0; age < 96; age++) {
						if (checkArray[age][sex][cat][0] != 1)
							throw new DynamoConfigurationException(
									"no value read in parameter file "
											+ fileName + " for age=" + age
											+ " sex=" + sex
											+ " and category nr " + cat);

					}
			/* if minIndex>0 we need to shift the array to null */
			float[][][] returnArray = new float[96][2][maxIndex - minIndex + 1];

			if (minIndex > 0) {

				for (int cat = minIndex; cat <= maxIndex; cat++)
					for (int sex = 0; sex < 2; sex++)
						for (int age = 0; age < 96; age++) {
							returnArray[age][sex][cat-minIndex]=returnArray1[age][sex][cat];
						}
			} else
				returnArray = returnArray1;

			return returnArray;
		//} catch (DynamoConfigurationException exception) {
		//	log
		//			.error("Caught Exception of type: Dynamo XML-file configuration Exception"
		//					+ " with message: "
		//					+ exception.getMessage()
		//					+ "from file " + fileName);
		//	exception.printStackTrace();
		//	return null;
		} catch (ConfigurationException e) {			
			String dynamoErrorMessage = "Caught Exception of type: "
				+ e.getClass().getName() + " with message: "
				+ e.getMessage() + "from file " + fileName;
			log.debug(e.getMessage() + e.getCause());
			ErrorMessageUtil.handleErrorMessage(this.log, dynamoErrorMessage, e, fileName);
		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage() + "from file " + fileName);
			exception.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * the method reads in the most inner group of values when containing four
	 * values (for making a three dimensional array)
	 * 
	 * @param rootChild
	 *            : the element contain the inner group of values
	 * @param arrayToBeFilled
	 *            : the array to be filled (often already partly filled)
	 * @param value1TagName
	 *            : tag for third dimension
	 * @param value2TagName
	 *            the tag name of the value to should be put into the array
	 * @param otherTags
	 *            : boolean indicating if other tags are allow to be present
	 
	 * @return array to which the newly read value has been added
	 * @throws ConfigurationException
	 */
	public float[][][] handleRootChild(ConfigurationNode rootChild,
			float[][][] arrayToBeFilled, String value1TagName,
			String value2Tagname, boolean otherTags) throws DynamoConfigurationException {
		// String rootChildName = rootChild.getName();
		// Object rootChildValueObject = rootChild.getValue();
		Integer age = null;
		Integer sex = null;
		Float value = null;
		Integer index = null;
		boolean sexRead = false;
		boolean ageRead = false;
		boolean indexRead = false;
		boolean valueRead = false;
		List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
				.getChildren();
		for (ConfigurationNode leafChild : leafChildren) {
			// log.debug("Handle leafChild: " + leafChild.getName());
			String leafName = leafChild.getName();
			Object valueObject = leafChild.getValue();

			if (valueObject instanceof String) {
				String valueString = (String) valueObject;
				if ("age".equalsIgnoreCase(leafName)) {

					age = getIntegerValue(valueString, "age");
					ageRead = true;

				} else {
					if ("sex".equalsIgnoreCase(leafName)) {

						sex = getIntegerValue(valueString, "sex");
						sexRead = true;

					} else {
						if (value1TagName.equalsIgnoreCase(leafName)) {

							index = getIntegerValue(valueString, value1TagName);
							indexRead = true;
						} else

						if (value2Tagname.equalsIgnoreCase(leafName)) {

							value = getFloatValue(valueString, value2Tagname);
							valueRead = true;
						} else

						{
							if (!otherTags) throw new DynamoConfigurationException(
									"Unexpected tag: " + leafName);
						}
					}
				}
			} else {
				throw new DynamoConfigurationException("Value is no String!");
			}
		} // for leafChildren
		if (!(ageRead && sexRead && valueRead && indexRead))
			throw new DynamoConfigurationException(
					"Tag missing when processing value for age " + age
							+ " sex: " + sex + " index: " + index
							+ "\nPresentValue: " + value);
		if (arrayToBeFilled[age][sex][index] != 0) {
			throw new DynamoConfigurationException("Duplicate value for age: "
					+ age + " sex: " + sex + " index: " + index
					+ "\nPresentValue: " + arrayToBeFilled[age][sex][index]
					+ " newValue: " + value);
		} else {
			if (age >= arrayToBeFilled.length)
				throw new DynamoConfigurationException(
						"Value for age is to large: " + age + "for sex: " + sex
								+ " index: " + index);
			else
				arrayToBeFilled[age][sex][index] = value;
			checkArray[age][sex][index][0]++;
			if (detailedDebug)
				log.debug("Processing value for age: " + age + " sex: " + sex
						+ " index: " + index + " value: " + value);
		}
		return arrayToBeFilled;

	}

	
	

	/**
	 * the method produces a two dimensional array from flat XML containing
	 * particular model parameters by age, sex and a third dimension .
	 * Data are selected for one particular value of another tag of the third dimension (given by the selectionTag name)
	 * When this has the vale selectionValue, the value of valueTag is read and put into the array 
	 * 
	 * @param fileName
	 *            : name of xml.file to be read (including the extension .xml)
	 * @param globalTagName
	 *            : root tag in the file
	 * @param tagName
	 *            : tag of the individual items in the file
	 * @param valueTagName
	 *            : tag of the element indicating the third dimension
	 * @param selectionTagName
	 *            : tag of the element containing the value to be read
	 * @param selectionValue
	 *            : value of selection Tag for which to read the data
	 * @return two dimensional array (float[96][2][]) of parameters by age and
	 *         sex
	 *         
	 * @throws ConfigurationException
	 */

	public float[][] selectOneDimArray(String fileName,
			String globalTagName, String tagName, String valueTagName,
			String selectionTagName, int selectionValue) throws DynamoConfigurationException {

		File configurationFile = new File(fileName);

		log.debug("Starting manufacturing two Dimensional array from file "
				+ fileName);

		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);

			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls load()). 
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			configurationFromFile.clear();
			
			// Validate the xml by xsd schema
			configurationFromFile.setValidating(true);			
			configurationFromFile.load();
			
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			if (configurationFromFile.getRootElementName() != globalTagName)
				throw new DynamoConfigurationException(" Tagname "
						+ globalTagName + " expected in file " + fileName
						+ " but found tag "
						+ configurationFromFile.getRootElementName());

			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();

			/* first find out how many elements there are */

			
			float[][]returnArray1 = new float[96][2];
			checkArray = new float[96][2][1][1];

			for (int sex = 0; sex < 2; sex++)
				for (int age = 0; age < 96; age++)
					 {

						returnArray1[age][sex] = 0;
						checkArray[age][sex][0][0] = 0;
					}

		
			for (ConfigurationNode rootChild : rootChildren) {
				returnArray1 = handleRootChild(rootChild, returnArray1,
						valueTagName, selectionTagName, selectionValue);

			} // end loop for rootChildren

			/*
			 * check whether the data are complete assuming that the numbering
			 * of categories is allowed to start at minIndex (not necessarily
			 * zero) but then there should be no gaps
			 */

			
				for (int sex = 0; sex < 2; sex++)
					for (int age = 0; age < 96; age++) {
						if (checkArray[age][sex][0][0] != 1)
							throw new DynamoConfigurationException(
									"no value read in parameter file "
											+ fileName + " for age=" + age
											+ " sex=" + sex
										);

					}
			
			return returnArray1;
			
		} catch (DynamoConfigurationException exception) {
			log
					.error("Caught Exception of type: Dynamo XML-file configuration Exception"
							+ " with message: "
							+ exception.getMessage()
							+ "from file " + fileName);
			exception.printStackTrace();
			return null;
		} catch (ConfigurationException e) {
			log.error("Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage() + "from file "
					+ fileName);
			e.printStackTrace();
			throw new DynamoConfigurationException("Caught Exception of type: "
					+ e.getClass().getName() + " with message: "
					+ e.getMessage() + "from file " + fileName);

		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage() + "from file " + fileName);
			exception.printStackTrace();
			return null;
		}
	}


	
	
	
	/**
	 * the method reads in the most inner group of values when containing four
	 * values and selecting only one element for reading into a two dimensional array
	 * 
	 * @param rootChild
	 *            : the element contain the inner group of values
	 * @param arrayToBeFilled
	 *            : the array to be filled (often already partly filled)
	 * @param valueTagName
	 *            : tag for value to read
	 * @param selectionTagName
	 *            the tag name of the value used for the selection
	 * @param selectionValue
	 *            : value to be selected
	 
	 * @return array to which the newly read value has been added
	 * @throws ConfigurationException
	 */
	public float[][] handleRootChild(ConfigurationNode rootChild,
			float[][] arrayToBeFilled, String valueTagName,
			String selectionTagName, int selectionValue) throws DynamoConfigurationException {
		// String rootChildName = rootChild.getName();
		// Object rootChildValueObject = rootChild.getValue();
		Integer age = null;
		Integer sex = null;
		Float value = null;
		Integer index = null;
		boolean sexRead = false;
		boolean ageRead = false;
		boolean indexRead = false;
		boolean valueRead = false;
		List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
				.getChildren();
		for (ConfigurationNode leafChild : leafChildren) {
			// log.debug("Handle leafChild: " + leafChild.getName());
			String leafName = leafChild.getName();
			Object valueObject = leafChild.getValue();

			if (valueObject instanceof String) {
				String valueString = (String) valueObject;
				if ("age".equalsIgnoreCase(leafName)) {

					age = getIntegerValue(valueString, "age");
					ageRead = true;

				} else {
					if ("sex".equalsIgnoreCase(leafName)) {

						sex = getIntegerValue(valueString, "sex");
						sexRead = true;

					} else {
						if (selectionTagName.equalsIgnoreCase(leafName)) {

							index = getIntegerValue(valueString, selectionTagName);
							indexRead = true;
							if (index!=selectionValue) break;
						} else

						if (valueTagName.equalsIgnoreCase(leafName)) {
                        try{
							value = getFloatValue(valueString, valueTagName);
							valueRead = true;}
                        /*
                         * here the value can be empty, so ignore the exception thrown in this case
                         */
                        catch (DynamoConfigurationException e){}
							
						} 
						
					}
				}
			} else {
				throw new DynamoConfigurationException("Value: "  + value +
						" is no String!");
			}
		} // for leafChildren
		if (index==selectionValue&&!(ageRead && sexRead && valueRead && indexRead))
			throw new DynamoConfigurationException(
					"No value found when selecting values for class " + index
					+" when processing value for age " + age
							+ " sex: " + sex 
							+ "\nValue found: " + value);
		if (index==selectionValue&& arrayToBeFilled[age][sex] != 0) {
			throw new DynamoConfigurationException("Duplicate value for age: "
					+ age + " sex: " + sex + " index: " + index
					+ "\nPresentValue: " + arrayToBeFilled[age][sex]
					+ " newValue: " + value);
		} else {
			if (index==selectionValue&& age >= arrayToBeFilled.length)
				throw new DynamoConfigurationException(
						"Value for age is to large: " + age + "for sex: " + sex
								+ " index: " + index);
			else
				if (index==selectionValue) {arrayToBeFilled[age][sex] = value;
			checkArray[age][sex][0][0]++;
			if (detailedDebug)
				log.debug("Processing value for age: " + age + " sex: " + sex
						+ " selecting category: " + index + " value: " + value);}
		}
		return arrayToBeFilled;

	}

	
	

	
	
	
	
	
	
	
	
	
	/**
	 * the method produces a four dimensional array from flat XML containing
	 * particular model parameters by age, sex and two other dimensions.
	 * <p>
	 * the tag of the element containing the value to be read is set to "value",
	 * that of the third dimension is set to "to" and that of the fourth
	 * dimension is set to "from".
	 * </p>
	 * 
	 * @param fileName
	 *            : name of xml file to be read (including the extension .xml)
	 * @param globalTagName
	 *            : root tag in the file
	 * @param tagName
	 *            : tag of the individual items in the file
	 * @return four dimensional array (float[96][2][][]) of parameters by age
	 *         and sex
	 * @throws ConfigurationException
	 */
	public float[][][][] manufactureThreeDimArray(String fileName,
			String globalTagName, String tagName)
			throws DynamoConfigurationException {

		float[][][][] arrayToBeFilled = manufactureThreeDimArray(fileName,
				globalTagName, tagName, "from", "to", "value");
		return arrayToBeFilled;
	}

	/**
	 * the method produces a four dimensional array from flat XML containing
	 * particular model parameters by age, sex and two other dimensions .
	 * 
	 * @param fileName
	 *            : name of xml.file to be read (including the extension .xml)
	 * @param globalTagName
	 *            : root tag in the file
	 * @param tagName
	 *            : tag of the individual items in the file
	 * @param value1TagName
	 *            : tag of the element indicating the third dimension
	 * @param value2TagName
	 *            : tag of the element indicating the fourth dimension
	 * @param value3TagName
	 *            : tag of the element containing the value to be read
	 * @return four dimensional array (float[96][2][][]) of parameters by age
	 *         and sex
	 * @throws DynamoConfigurationException 
	 * @throws ConfigurationException
	 */
	public float[][][][] manufactureThreeDimArray(String fileName,
			String globalTagName, String tagName, String value1TagName,
			String value2TagName, String value3TagName)
			throws DynamoConfigurationException {

		File configurationFile = new File(fileName);
		float[][][][] returnArray = null;
		
		log.debug("Starting manufacturing five Dimensional array from file "
				+ fileName);

		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);
			
			/**
			TODO: VALIDATION IS FOR FUTURE USE 
			NICE TO HAVE FEATURE
			KEEP IT IN THE CODE
			The following schemas are not be validated:
			relativeRisks.xsd
			*/
			
			if (!"relativeRisks".equals(configurationFromFile.getRootElementName())) {
				// Validate the xml by xsd schema
				// WORKAROUND: clear() is put after the constructor (also calls load()). 
				// The config cannot be loaded twice,
				// because the contents will be doubled.
				configurationFromFile.clear();
				
				// Validate the xml by xsd schema
			//TODO: weer terugzetten	configurationFromFile.setValidating(true);			
				configurationFromFile.load();
			}
			
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			if (configurationFromFile.getRootElementName() != globalTagName)
				throw new DynamoConfigurationException(" Tagname "
						+ globalTagName + " expected in file " + fileName
						+ " but found tag "
						+ configurationFromFile.getRootElementName());

			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();

			/* first find out how many elements there are */
			/* the third and fourth dimension should be equal */
			List<Integer> indexL1 = configurationFromFile.getList(tagName + "."
					+ value1TagName);
			List<Integer> indexL2 = configurationFromFile.getList(tagName + "."
					+ value2TagName);
			Iterator it = indexL1.iterator();
			int maxIndex1 = 0;
			while (it.hasNext()) {
				int curVar = Integer.parseInt((String) it.next());
				if (curVar > maxIndex1)
					maxIndex1 = curVar;
			}
			Iterator it2 = indexL2.iterator();
			int maxIndex2 = 0;
			while (it2.hasNext()) {
				int curVar = Integer.parseInt((String) it2.next());
				if (curVar > maxIndex2)
					maxIndex2 = curVar;
			}
			int maxIndex;
			if (maxIndex1 == maxIndex2)
				maxIndex = maxIndex1;
			else
				throw new DynamoConfigurationException(
						" number of values for tag " + value1TagName
								+ " not equal to that in tag " + value1TagName
								+ " in file " + fileName);

			/* now initialize the arrays */
			returnArray = new float[96][2][maxIndex + 1][maxIndex + 1];
			checkArray = new float[96][2][maxIndex + 1][maxIndex + 1];

			for (int sex = 0; sex < 2; sex++)
				for (int age = 0; age < 96; age++)
					for (int cat = 0; cat <= maxIndex; cat++)
						for (int cat2 = 0; cat2 <= maxIndex; cat2++) {

							returnArray[age][sex][cat][cat2] = 0;
							checkArray[age][sex][cat][cat2] = 0;
						}

			for (ConfigurationNode rootChild : rootChildren) {
				if (detailedDebug)
					log.debug("Handle rootChild: " + rootChild.getName());
				if (rootChild.getName() != tagName)
					throw new DynamoConfigurationException(" Tagname "
							+ tagName + " expected in file " + fileName
							+ " but found tag " + rootChild.getName());
				returnArray = handleRootChild(rootChild, returnArray,
						value1TagName, value2TagName, value3TagName);
			}

			// end loop for rootChildren

			for (int cat = 0; cat <= maxIndex; cat++)
				for (int cat2 = 0; cat2 <= maxIndex; cat2++)
					for (int sex = 0; sex < 2; sex++)
						for (int age = 0; age < 96; age++) {
							if (checkArray[age][sex][cat][cat2] != 1)
								throw new DynamoConfigurationException(
										"no value read in parameter file "
												+ fileName + " for age=" + age
												+ " sex=" + sex
												+ " and category nrs " + cat
												+ " and " + cat2);
						}
			return returnArray;
		} catch (ConfigurationException e) {
			/*
			log.error("Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage() + "from file "
					+ fileName);
			e.printStackTrace();
			throw new DynamoConfigurationException("Caught Exception of type: "
					+ e.getClass().getName() + " with message: "
					+ e.getMessage() + "from file " + fileName);*/
			ErrorMessageUtil.handleErrorMessage(log, "Caught Exception of type: ", e, fileName);
			
		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage() + "from file " + fileName);
			exception.printStackTrace();
			return null;
		}
		return returnArray;
	}

	/**
	 * the method reads in the most inner group of values when containing five
	 * values (for making a four dimensional array)
	 * 
	 * @param rootChild
	 *            : the element contain the inner group of values
	 * @param arrayToBeFilled
	 *            : the array to be filled (often already partly filled)
	 * @param value1TagName
	 *            : tag for third dimension
	 * @param value2TagName
	 *            : tag for fourth dimension
	 * @param value3TagName
	 *            the tag name of the value to should be put into the array
	 * @return array to which the newly read value has been added
	 * @throws ConfigurationException
	 * @throws DynamoConfigurationException
	 */
	public float[][][][] handleRootChild(ConfigurationNode rootChild,
			float[][][][] arrayToBeFilled, String value1TagName,
			String value2TagName, String value3TagName)
			throws DynamoConfigurationException {
		// String rootChildName = rootChild.getName();
		// Object rootChildValueObject = rootChild.getValue();
		Integer age = null;
		Integer sex = null;
		Float value = null;
		Integer index1 = null;
		Integer index2 = null;
		boolean sexRead = false;
		boolean ageRead = false;
		boolean index1Read = false;
		boolean index2Read = false;
		boolean valueRead = false;

		List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
				.getChildren();
		for (ConfigurationNode leafChild : leafChildren) {
			// log.debug("Handle leafChild: " + leafChild.getName());
			String leafName = leafChild.getName();
			Object valueObject = leafChild.getValue();
			if (valueObject instanceof String) {
				String valueString = (String) valueObject;
				if ("age".equalsIgnoreCase(leafName)) {

					age = getIntegerValue(valueString, "age");
					;
					ageRead = true;
				} else {
					if ("sex".equalsIgnoreCase(leafName)) {

						sex = getIntegerValue(valueString, "sex");
						sexRead = true;
					} else {
						if (value1TagName.equalsIgnoreCase(leafName)) {

							
							index1 = getIntegerValue(valueString,
									value1TagName);
							
							index1Read = true;
						} else {

							if (value2TagName.equalsIgnoreCase(leafName)) {

								index2 = getIntegerValue(valueString,
										value2TagName);
								;
								index2Read = true;
							} else

							if (value3TagName.equalsIgnoreCase(leafName)) {

								value = getFloatValue(valueString,
										value3TagName);
								valueRead = true;
							} else

							{
								throw new DynamoConfigurationException(
										"Unexpected tag: " + leafName);
							}
						}
					}
				}
			} else {
				throw new DynamoConfigurationException("Value is no String!");
			}
		} // for leafChildren
		if (!(ageRead && sexRead && valueRead && index1Read && index2Read))
			throw new DynamoConfigurationException(
					"Tag missing when processing value for age " + age
							+ " sex: " + sex + " index1: " + index1
							+ " index2: " + index2 + "\nPresentValue: " + value);
		if (arrayToBeFilled[age][sex][index1][index2] != 0) {
			throw new DynamoConfigurationException("Duplicate value for age: "
					+ age + " sex: " + sex + " index: " + index1
					+ "\nPresentValue: "
					+ arrayToBeFilled[age][sex][index1][index2] + " newValue: "
					+ value);
		} else {

			if (age >= arrayToBeFilled.length)
				throw new DynamoConfigurationException(
						"Value for age is to large: " + age + "for sex: " + sex
								+ " index1: " + index1 + " index2: " + index2);
			else
				arrayToBeFilled[age][sex][index1][index2] = value;
			checkArray[age][sex][index1][index2]++;
			if (detailedDebug)
				log.debug("Processing value for age: " + age + " sex: " + sex
						+ " index: " + index1 + " and " + index2 + " value: "
						+ value);
		}
		return arrayToBeFilled;

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

	public float getFloatValue(String value, String tag)
			throws DynamoConfigurationException {
		float returnvalue = 0;

		if (value == null|| value==""||value.length()==0)
			throw new DynamoConfigurationException("no value found with " + tag);
		else
			
			returnvalue = Float.parseFloat(value);
		return returnvalue;

	}
}
