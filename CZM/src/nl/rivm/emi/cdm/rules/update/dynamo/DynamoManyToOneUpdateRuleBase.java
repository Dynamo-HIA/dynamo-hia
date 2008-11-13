package nl.rivm.emi.cdm.rules.update.dynamo;


import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.base.UpdateRuleMarker;
import nl.rivm.emi.cdm.simulation.Simulation;



/**
 * 
 * @author mondeelr
 * 
 */
public abstract class DynamoManyToOneUpdateRuleBase extends ManyToOneUpdateRuleBase implements UpdateRuleMarker {
	private static String charIDLabel="charID";
	int characteristicIndex = -1;
	int ageIndex = 1;
	int sexIndex = 2;
	int riskFactorIndex1 = 3;
	int riskFactorIndex2 = 4;
	float timeStep=1;
	int riskType = -1;
	int durationClass = -1;
			
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
	public abstract Object update(Object[] currentValues) throws  CDMUpdateRuleException;
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
}
