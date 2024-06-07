/**
 * 
 */
package nl.rivm.emi.cdm.rules.update.base;

import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;

/**
 * @author Hendriek
 *
 */
public abstract class DynamoManyToManyUpdateRuleBase extends ManyToManyUpdateRuleBase {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.cdm.rules.update.base.ManyToManyUpdateRuleBase#update(java.lang.Object[])
	 */
	
	/**
	 * this abstract type contains the elements that are common to all DYNAMO updaterules
	 */
	
	private static String charIDLabel="charID";/* somehow setting this goes wrong */
	protected int characteristicIndexOfThisRule = -2;
	
	protected int ageIndex = 1;
	protected int sexIndex = 2;
	protected int riskFactorIndex1 = 3;
	protected int riskFactorIndex2 = 4;
	protected float timeStep=1;
	protected int riskType = -1;
	protected int durationClass = -1;
	protected float referenceValueContinous=-1;
	protected int nCat =0;
	
	static protected  String riskTypeLabel = "riskType";
	static protected  String nCatLabel = "nCat";
	static protected  String refValLabel="refValContinuousVariable"; 
	static protected String globalTagName="updateRuleConfiguration";		
	
	public int getCharacteristicIndex() {
		return characteristicIndexOfThisRule;
	}

	public  void setCharacteristicIndex(int characteristicIndex) {
		this.characteristicIndexOfThisRule = characteristicIndex;
	}

	protected DynamoManyToManyUpdateRuleBase() {
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
	
	
	protected void handleCharID(
		HierarchicalConfiguration simulationConfiguration
		) throws ConfigurationException {
	try {
		int charIndex   = simulationConfiguration.getInt(charIDLabel);
		
		this.setCharacteristicIndex (charIndex);
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

protected float getTimeStep() {
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



public static void setGlobalTagName(String globalTagName) {
	DynamoManyToOneUpdateRuleBase.globalTagName = globalTagName;
}

public float getReferenceValueContinous() {
	return referenceValueContinous;
}

public void setReferenceValueContinous(float referenceValueContinous) {
	this.referenceValueContinous = referenceValueContinous;
}

/* (non-Javadoc)
 * @see nl.rivm.emi.cdm.rules.update.base.ManyToManyUpdateRuleBase#update(java.lang.Object[])
 */

}
