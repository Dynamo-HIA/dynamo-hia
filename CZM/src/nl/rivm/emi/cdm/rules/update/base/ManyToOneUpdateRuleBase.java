package nl.rivm.emi.cdm.rules.update.base;

import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;


/**
 * 
 * @author mondeelr
 * 
 */
public abstract class ManyToOneUpdateRuleBase implements UpdateRuleMarker {

	protected ManyToOneUpdateRuleBase() {
		super();
	}

	/**
	 * @param currentValues
	 * @param seed for generation of random number (added by Hendriek)
	 * @return The Result when AOK, null when a parameter is missing, a
	 *         ConfigurationException when the types of the parameters do not
	 *         match.
	 * @throws CDMUpdateRuleException 
	 * @throws UpdateRuleException 
	 */
	
	/* changed by Hendriek */
	public abstract Object update(Object[] currentValues, Long seed) throws CDMUpdateRuleException, CDMUpdateRuleException;
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
}
