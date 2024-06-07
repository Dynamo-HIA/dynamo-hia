package nl.rivm.emi.cdm.rules.update.dynamo;


import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.base.UpdateRuleMarker;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;



/**
 * 
 * @author mondeelr
 * 
 */
public abstract class DynamoManyToOneUpdateRuleBase extends ManyToOneUpdateRuleBase implements UpdateRuleMarker {
	private static String charIDLabel="charID";
	protected int characteristicIndex = -1;
	protected int ageIndex = 1;
	protected int sexIndex = 2;
	protected int riskFactorIndex1 = 3;
	protected int riskFactorIndex2 = 4;
	float timeStep=1;
	protected int riskType = -1;
	protected int durationClass = -1;
	int nCat =0;
	static protected String globalTagName="updateRuleConfiguration";		
	
	public int getCharacteristicIndex() {
		return characteristicIndex;
	}

	public  void setCharacteristicIndex(int characteristicIndex) {
		this.characteristicIndex = characteristicIndex;
	}

	protected DynamoManyToOneUpdateRuleBase() {
		super();
	}

	/**
	 * @param currentValues
	 * @return The Result when AOK, null when a parameter is missing, a
	 *         ConfigurationException when the types of the parameters do not
	 *         match.
	 * 
	 * @throws CDMUpdateRuleException 
	 */
	public abstract Object update(Object[] currentValues, Long seed) throws  CDMUpdateRuleException;
	//this added by hendriek
	
	
	
	public static float getFloat(Object [] currentValues,int characteristicsIndex) throws CDMUpdateRuleException
	
	{float returnValue=-1; 
	if ((currentValues[characteristicsIndex] != null) ) {
			
		if (currentValues[characteristicsIndex] instanceof Float)

		 {
			returnValue =((Float) currentValues[characteristicsIndex])
					.floatValue();
			return  returnValue;
		 } else throw new CDMUpdateRuleException("Fatal error in update rule: characteristic nr. "+characteristicsIndex+ "is not a float");
			
			
		}else throw new CDMUpdateRuleException("Fatal error in update rule: characteristic nr. "+characteristicsIndex+ "does not exist");
	
	
	
	
	}
public static int getInteger(Object [] currentValues,int characteristicsIndex) throws CDMUpdateRuleException
	
	{int returnValue=-1; 
	if ((currentValues[characteristicsIndex] != null) ) {
			
		if (currentValues[characteristicsIndex] instanceof Integer)

		 { 
			returnValue =(int) ((Integer) currentValues[characteristicsIndex])
					.intValue();
			return  returnValue;
		 } else throw new CDMUpdateRuleException("Fatal error in update rule: characteristic nr. "+characteristicsIndex+ "is not an integer");
			
			
		}else throw new CDMUpdateRuleException("Fatal error in update rule: characteristic nr. "+characteristicsIndex+ "does not exist");
	
	
	
	
	}
protected void handleCharID(
		HierarchicalConfiguration simulationConfiguration
		) throws ConfigurationException {
	try {
		int characteristicIndex   = simulationConfiguration.getInt(charIDLabel);
		
		setCharacteristicIndex (characteristicIndex);
	} catch (NoSuchElementException e) {
		throw new ConfigurationException(
				CDMConfigurationException.noUpdateCharIDMessage);
	}
}

public int getNCat() {
	return nCat;
}

public void setNCat(int cat) {
	nCat = cat;
}

public static String getCharIDLabel() {
	return charIDLabel;
}

public static void setCharIDLabel(String charIDLabel) {
	DynamoManyToOneUpdateRuleBase.charIDLabel = charIDLabel;
}

public int getAgeIndex() {
	return ageIndex;
}

public void setAgeIndex(int ageIndex) {
	this.ageIndex = ageIndex;
}

public int getSexIndex() {
	return sexIndex;
}

public void setSexIndex(int sexIndex) {
	this.sexIndex = sexIndex;
}

public int getRiskFactorIndex1() {
	return riskFactorIndex1;
}

public void setRiskFactorIndex1(int riskFactorIndex1) {
	this.riskFactorIndex1 = riskFactorIndex1;
}

public int getRiskFactorIndex2() {
	return riskFactorIndex2;
}

public void setRiskFactorIndex2(int riskFactorIndex2) {
	this.riskFactorIndex2 = riskFactorIndex2;
}

public float getTimeStep() {
	return timeStep;
}

public void setTimeStep(float timeStep) {
	this.timeStep = timeStep;
}

public int getRiskType() {
	return riskType;
}

public void setRiskType(int riskType) {
	this.riskType = riskType;
}

public int getDurationClass() {
	return durationClass;
}

public void setDurationClass(int durationClass) {
	this.durationClass = durationClass;
}

public static String getGlobalTagName() {
	return globalTagName;
}

public static void setGlobalTagName(String globalTagName) {
	DynamoManyToOneUpdateRuleBase.globalTagName = globalTagName;
}
}
