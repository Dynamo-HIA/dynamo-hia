package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;

import java.util.Iterator;
import java.util.List;

import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
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

	public ArraysFromXMLFactory() {
		super();
		
	}
	/**
	 * the method produces a two dimensional array from flat XML 
	 * containing  particular model parameters by age, sex .<p>
	 * tag of the element containing the value to be read is assumed to be (optional)</p>
	 * @param fileName: name of xml.file to be read (including the extension .xml)
	 * @param globalTagName: root tag in the file
	 * @param tagName: tag of the individual items in the file
	 * @return two dimensional array (float[96][2]) of parameters by age and sex
	 * @throws ConfigurationException
	 */
	public float[][] manufactureOneDimArray(String fileName,
			String globalTagName, String tagName) throws ConfigurationException {

		float[][] arrayToBeFilled = manufactureOneDimArray(fileName,
				globalTagName, tagName, "value");
		return arrayToBeFilled;
	}

	/**
	 * the method produces a two dimensional array from flat XML 
	 * containing  particular model parameters by age, sex .
	 * @param fileName: name of xml.file to be read (including the extension .xml)
	 * @param globalTagName: root tag in the file
	 * @param tagName: tag of the individual items in the file
	 * @param valueTagName : tag of the element containing the value to be read (optional)
	 * @return two dimensional array (float[96][2]) of parameters by age and sex
	 * @throws ConfigurationException
	 */
	public float[][] manufactureOneDimArray(String fileName,
			String globalTagName, String tagName, String valueTagName)
			throws ConfigurationException {
		float[][] returnArray = new float[96][2];
		checkArray = new float[96][2][1][1];

		for (int sex = 0; sex < 2; sex++)
			for (int age = 0; age < 96; age++) {

				returnArray[age][sex] = 0;
				checkArray[age][sex][0][0] = 0;
			}
		File configurationFile = new File(fileName);

		log.debug("Starting manufacture from file "+fileName);

		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);
			ConfigurationNode rootNode = configurationFromFile.getRootNode();

			if (configurationFromFile.getRootElementName() != globalTagName)
				throw new DynamoConfigurationException(" Tagname "
						+ globalTagName + " expected in file " + fileName
						+ " but found tag "
						+ configurationFromFile.getRootElementName());

			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();

			for (ConfigurationNode rootChild : rootChildren) {
				log.debug("Handle rootChild: " + rootChild.getName());
				if (rootChild.getName() != tagName)
					throw new DynamoConfigurationException(" Tagname "
							+ tagName + " expected in file " + fileName
							+ " but found tag " + rootChild.getName());

				returnArray = handleRootChild(rootChild, returnArray,
						valueTagName);
			} // end loop for rootChildren
			
/* check if the input file was complete */			
				for (int sex = 0; sex < 2; sex++)
					for (int age = 0; sex < 96; age++) 
						if (checkArray[age][sex][0][0] != 1)
							throw new DynamoConfigurationException(
									"no value read in parameter file for age="
											+ age + " sex=" + sex
											);
			
			
			
			return returnArray;

		} catch (ConfigurationException e) {
			log.error("Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage()+  "from file "+fileName);
			e.printStackTrace();
			throw e;
		} catch (DynamoConfigurationException exception) {
			log
					.error("Caught Exception of type: Dynamo XML-file configuration Exception"
							+ " with message: " + exception.getMessage()+  "from file "+fileName);
			exception.printStackTrace();
			return null;
		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage()+  "from file "+fileName);
			exception.printStackTrace();
			return null;
		}
	}

	/**
	 * the method reads in the most inner group of values when 
	 * containing three values (for making a two dimensional array)
	 * 
	 * @param rootChild: the element contain the inner group of values
	 * @param arrayToBeFilled: the array to be filled (often already partly filled)
	 * @param valueTagName: the tag name of the value to should be put into the array 
	 * @return array to which the newly read value has been added
	 * @throws ConfigurationException
	 */
	private float[][] handleRootChild(ConfigurationNode rootChild,
			float[][] arrayToBeFilled, String valueTagName)
			throws ConfigurationException {
		// String rootChildName = rootChild.getName();
		// Object rootChildValueObject = rootChild.getValue();
		Integer age = null;
		Integer sex = null;
		Float value = null;

		List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
				.getChildren();
		for (ConfigurationNode leafChild : leafChildren) {
			log.debug("Handle leafChild: " + leafChild.getName());
			String leafName = leafChild.getName();
			Object valueObject = leafChild.getValue();
			if (valueObject instanceof String) {
				String valueString = (String) valueObject;
				if ("age".equalsIgnoreCase(leafName)) {
					if (age == null) {
						age = Integer.parseInt(valueString);
					} else {
						throw new ConfigurationException("Double age tag.");
					}
				} else {
					if ("sex".equalsIgnoreCase(leafName)) {
						if (sex == null) {
							sex = Integer.parseInt(valueString);
						} else {
							throw new ConfigurationException("Double sex tag.");
						}
					} else {
						if (valueTagName.equalsIgnoreCase(leafName)) {
							if (value == null) {
								value = Float.parseFloat(valueString);
							} else {
								throw new ConfigurationException(
										"Double value tag.");
							}
						} else {
							throw new ConfigurationException("Unexpected tag: "
									+ leafName);
						}
					}
				}
			} else {
				throw new ConfigurationException("Value is no String!");
			}
		} // for leafChildren

		if (arrayToBeFilled[age][sex] != 0) {
			throw new ConfigurationException("Duplicate value for age: " + age
					+ " sex: " + sex + "\nPresentValue: "
					+ arrayToBeFilled[age][sex] + " newValue: " + value);
		} else {
			arrayToBeFilled[age][sex] = value;
			checkArray[age][sex][0][0]++;
			log.debug("Processing value for age: " + age + " sex: " + sex
					+ " value: " + value);

			
		}
		return arrayToBeFilled;
	}
	/**
	 * the method produces a three dimensional array from flat XML 
	 * containing  particular model parameters by age, sex and a third dimension.
	 *  the tag of the element containing the value to be read is set to "value", that of the third dimension
	 *  is set to "cat"
	 * @param fileName: name of xml.file to be read (including the extension .xml)
	 * @param globalTagName: root tag in the file
	 * @param tagName: tag of the individual items in the file
	
	 * @return three dimensional array (float[96][2][]) of parameters by age and sex
	 * @throws ConfigurationException
	 */
	
	public float[][][] manufactureTwoDimArray(String fileName,
			String globalTagName, String tagName) throws ConfigurationException {

		float[][][] arrayToBeFilled = manufactureTwoDimArray(fileName,
				globalTagName, tagName, "cat", "value");
		return arrayToBeFilled;
	}

	
	  /**
	 * the method produces a three dimensional array from flat XML 
	 * containing  particular model parameters by age, sex and a third dimension .
	 *  
	 * @param fileName: name of xml.file to be read (including the extension .xml)
	 * @param globalTagName: root tag in the file
	 * @param tagName: tag of the individual items in the file
	 * @param value1TagName: tag of the element indicating the third dimension
	 * @param value2TagName: tag of the element containing the value to be read 
	 * @return three dimensional array (float[96][2][]) of parameters by age and sex
	 * @throws ConfigurationException
	 */
	
	 
	public float[][][] manufactureTwoDimArray(String fileName,
			String globalTagName, String tagName, String value1TagName,
			String value2TagName) throws ConfigurationException {

		File configurationFile = new File(fileName);

		log.debug("Starting manufacturing four Dimensional array from file "+fileName);

		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);

			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			if (configurationFromFile.getRootElementName() != globalTagName)
				throw new DynamoConfigurationException(" Tagname "
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
			while (it.hasNext()) {
				int curVar = Integer.parseInt((String) it.next());
				if (curVar > maxIndex)
					maxIndex = curVar;
			}

			/* now initialize the arrays */
			float[][][] returnArray = new float[96][2][maxIndex + 1];
			checkArray = new float[96][2][maxIndex + 1][0];

			for (int sex = 0; sex < 2; sex++)
				for (int age = 0; age < 96; age++)
					for (int cat = 0; cat <= maxIndex; cat++) {

						returnArray[age][sex][cat] = 0;
						checkArray[age][sex][cat][0] = 0;
					}

			for (ConfigurationNode rootChild : rootChildren) {
				log.debug("Handle rootChild: " + rootChild.getName());
				if (rootChild.getName() != tagName)
					throw new DynamoConfigurationException(" Tagname "
							+ tagName + " expected in file " + fileName
							+ " but found tag " + rootChild.getName()+  "in file "+fileName);
			}

			for (ConfigurationNode rootChild : rootChildren) {
				returnArray = handleRootChild(rootChild, returnArray,
						value1TagName, value2TagName);

			} // end loop for rootChildren

			for (int cat = 0; cat <= maxIndex; cat++)
				for (int sex = 0; sex < 2; sex++)
					for (int age = 0; sex < 96; age++) {
						if (checkArray[age][sex][cat][0] != 1)
							throw new DynamoConfigurationException(
									"no value read in parameter file "+fileName+" for age="
											+ age + " sex=" + sex
											+ " and category nr " + cat);
					}
			return returnArray;
		} catch (ConfigurationException e) {
			log.error("Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage()+  "from file "+fileName);
			e.printStackTrace();
			throw e;
		} catch (DynamoConfigurationException exception) {
			log
					.error("Caught Exception of type: Dynamo XML-file configuration Exception"
							+ " with message: " + exception.getMessage()+  "from file "+fileName);
			exception.printStackTrace();
			return null;
		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage()+  "from file "+fileName);
			exception.printStackTrace();
			return null;
		}
	}
	/**
	 * the method reads in the most inner group of values when 
	 * containing four values (for making a three dimensional array)
	 * 
	 * @param rootChild: the element contain the inner group of values
	 * @param arrayToBeFilled: the array to be filled (often already partly filled)
	 * @param value1TagName: tag for third dimension
	 * @param value2TagName the tag name of the value to should be put into the array 
	 * @return array to which the newly read value has been added
	 * @throws ConfigurationException
	 */
	public float[][][] handleRootChild(ConfigurationNode rootChild,
			float[][][] arrayToBeFilled, String value1TagName,
			String value2Tagname) throws ConfigurationException {
		// String rootChildName = rootChild.getName();
		// Object rootChildValueObject = rootChild.getValue();
		Integer age = null;
		Integer sex = null;
		Float value = null;
		Integer index = null;

		List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
				.getChildren();
		for (ConfigurationNode leafChild : leafChildren) {
			log.debug("Handle leafChild: " + leafChild.getName());
			String leafName = leafChild.getName();
			Object valueObject = leafChild.getValue();
			if (valueObject instanceof String) {
				String valueString = (String) valueObject;
				if ("age".equalsIgnoreCase(leafName)) {
					if (age == null) {
						age = Integer.parseInt(valueString);
					} else {
						throw new ConfigurationException("Double age tag.");
					}
				} else {
					if ("sex".equalsIgnoreCase(leafName)) {
						if (sex == null) {
							sex = Integer.parseInt(valueString);
						} else {
							throw new ConfigurationException("Double sex tag.");
						}
					} else {
						if (value1TagName.equalsIgnoreCase(leafName)) {
							if (value == null) {
								index = Integer.parseInt(valueString);
							} else {
								throw new ConfigurationException(
										"Double value tag.");
							}
						} else

						if (value2Tagname.equalsIgnoreCase(leafName)) {
							if (value == null) {
								value = Float.parseFloat(valueString);
							} else {
								throw new ConfigurationException(
										"Double value tag.");
							}
						} else

						{
							throw new ConfigurationException("Unexpected tag: "
									+ leafName);
						}
					}
				}
			} else {
				throw new ConfigurationException("Value is no String!");
			}
		} // for leafChildren

		if (arrayToBeFilled[age][sex][index] != 0) {
			throw new ConfigurationException("Duplicate value for age: " + age
					+ " sex: " + sex + " index: " + index + "\nPresentValue: "
					+ arrayToBeFilled[age][sex][index] + " newValue: " + value);
		} else {
			arrayToBeFilled[age][sex][index] = value;
			checkArray[age][sex][index][0]++;
			log.debug("Processing value for age: " + age + " sex: " + sex
					+ " index: " + index + " value: " + value);
		}
		return arrayToBeFilled;

	}

	/**
	 *  the method produces a four dimensional array from flat XML 
	 *  containing  particular model parameters by age, sex and two other dimensions.<p>
	 *  the tag of the element containing the value to be read is set to "value", that of the third dimension
	 *  is set to "to" and that of the fourth dimension is set to "from".</p>
	 * @param fileName: name of xml file to be read (including the extension .xml)
	 * @param globalTagName: root tag in the file
	 * @param tagName: tag of the individual items in the file
	 * @return four dimensional array (float[96][2][][]) of parameters by age and sex
	 * @throws ConfigurationException
	 */
	public float[][][][] manufactureThreeDimArray(String fileName,
			String globalTagName, String tagName) throws ConfigurationException {

		float[][][][] arrayToBeFilled = manufactureThreeDimArray(fileName,
				globalTagName, tagName, "to","from", "value");
		return arrayToBeFilled;
	}

	/**
	 * the method produces a four dimensional array from flat XML 
	 * containing  particular model parameters by age, sex and two other dimensions .
	 *  
	 * @param fileName: name of xml.file to be read (including the extension .xml)
	 * @param globalTagName: root tag in the file
	 * @param tagName: tag of the individual items in the file
	 * @param value1TagName: tag of the element indicating the third dimension
	 * @param value2TagName: tag of the element indicating the fourth dimension
	 * @param value3TagName: tag of the element containing the value to be read 
	 * @return four dimensional array (float[96][2][][]) of parameters by age and sex
	 * @throws ConfigurationException
	 
	 */
	public float[][][][] manufactureThreeDimArray(String fileName,
			String globalTagName, String tagName, String value1TagName,
			String value2TagName, String value3TagName) throws ConfigurationException {

		File configurationFile = new File(fileName);

		log.debug("Starting manufacturing five Dimensional array from file "+fileName);

		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);

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
            if (maxIndex1==maxIndex2) maxIndex=maxIndex1;
            else throw new DynamoConfigurationException(" number of values for tag "
					+ value1TagName +" not equal to that in tag " + value1TagName +" in file "  + fileName
					);
	
			/* now initialize the arrays */
			float[][][][] returnArray = new float[96][2][maxIndex + 1][maxIndex + 1];
			checkArray = new float[96][2][maxIndex + 1][maxIndex + 1];

			for (int sex = 0; sex < 2; sex++)
				for (int age = 0; age < 96; age++)
					for (int cat = 0; cat <= maxIndex; cat++)
						for (int cat2 = 0; cat2 <= maxIndex; cat2++) {

						returnArray[age][sex][cat][cat2] = 0;
						checkArray[age][sex][cat][cat2] = 0;
					}

			for (ConfigurationNode rootChild : rootChildren) {
				log.debug("Handle rootChild: " + rootChild.getName());
				if (rootChild.getName() != tagName)
					throw new DynamoConfigurationException(" Tagname "
							+ tagName + " expected in file " + fileName
							+ " but found tag " + rootChild.getName());
			}

			for (ConfigurationNode rootChild : rootChildren) {
				returnArray = handleRootChild(rootChild, returnArray,
						value1TagName, value2TagName,value3TagName);

			} // end loop for rootChildren

			for (int cat = 0; cat <= maxIndex; cat++)
				for (int cat2 = 0; cat2 <= maxIndex; cat2++)
				for (int sex = 0; sex < 2; sex++)
					for (int age = 0; sex < 96; age++) {
						if (checkArray[age][sex][cat][cat2] != 1)
							throw new DynamoConfigurationException(
									"no value read in parameter file "+fileName +" for age="
											+ age + " sex=" + sex
											+ " and category nrs " + cat+" and "+cat2);
					}
			return returnArray;
		} catch (ConfigurationException e) {
			log.error("Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage()+  "from file "+fileName);
			e.printStackTrace();
			throw e;
		} catch (DynamoConfigurationException exception) {
			log
					.error("Caught Exception of type: Dynamo XML-file configuration Exception"
							+ " with message: " + exception.getMessage()+  "from file "+fileName);
			exception.printStackTrace();
			return null;
		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage()+  "from file "+fileName);
			exception.printStackTrace();
			return null;
		}
	}

	/**
	 * the method reads in the most inner group of values when 
	 * containing five values (for making a four dimensional array)
	 * 
	 * @param rootChild: the element contain the inner group of values
	 * @param arrayToBeFilled: the array to be filled (often already partly filled)
	 * @param value1TagName: tag for third dimension
	 * @param value2TagName: tag for fourth dimension
	 * @param value3TagName the tag name of the value to should be put into the array 
	 * @return array to which the newly read value has been added
	 * @throws ConfigurationException
	 */
	public float[][][][] handleRootChild(ConfigurationNode rootChild,
			float[][][][] arrayToBeFilled, String value1TagName,
			String value2TagName, String value3TagName) throws ConfigurationException {
		// String rootChildName = rootChild.getName();
		// Object rootChildValueObject = rootChild.getValue();
		Integer age = null;
		Integer sex = null;
		Float value = null;
		Integer index1 = null;
		Integer index2 = null;

		List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
				.getChildren();
		for (ConfigurationNode leafChild : leafChildren) {
			log.debug("Handle leafChild: " + leafChild.getName());
			String leafName = leafChild.getName();
			Object valueObject = leafChild.getValue();
			if (valueObject instanceof String) {
				String valueString = (String) valueObject;
				if ("age".equalsIgnoreCase(leafName)) {
					if (age == null) {
						age = Integer.parseInt(valueString);
					} else {
						throw new ConfigurationException("Double age tag.");
					}
				} else {
					if ("sex".equalsIgnoreCase(leafName)) {
						if (sex == null) {
							sex = Integer.parseInt(valueString);
						} else {
							throw new ConfigurationException("Double sex tag.");
						}
					} else {
						if (value1TagName.equalsIgnoreCase(leafName)) {
							if (value == null) {
								index1 = Integer.parseInt(valueString);
							} else {
								throw new ConfigurationException(
										"Double value tag.");
							}
						} else {

						if (value2TagName.equalsIgnoreCase(leafName)) {
							if (value == null) {
								index2 =  Integer.parseInt(valueString);
							} else {
								throw new ConfigurationException(
										"Double value tag.");
							}
						} else

							if (value3TagName.equalsIgnoreCase(leafName)) {
								if (value == null) {
									value = Float.parseFloat(valueString);
								} else {
									throw new ConfigurationException(
											"Double value tag.");
								}
							} else

						{
							throw new ConfigurationException("Unexpected tag: "
									+ leafName);
						}
						}}
				}
			} else {
				throw new ConfigurationException("Value is no String!");
			}
		} // for leafChildren

		if (arrayToBeFilled[age][sex][index1][index2] != 0) {
			throw new ConfigurationException("Duplicate value for age: " + age
					+ " sex: " + sex + " index: " + index1 + "\nPresentValue: "
					+ arrayToBeFilled[age][sex][index1][index2] + " newValue: " + value);
		} else {
			arrayToBeFilled[age][sex][index1][index2] = value;
			checkArray[age][sex][index1][index2]++;
			log.debug("Processing value for age: " + age + " sex: " + sex
					+ " index: " + index1 + " and " + index2+  " value: " + value);
		}
		return arrayToBeFilled;

	}
}
