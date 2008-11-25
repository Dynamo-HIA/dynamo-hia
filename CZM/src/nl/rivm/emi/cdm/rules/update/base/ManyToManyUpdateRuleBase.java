/**
 * 
 */
package nl.rivm.emi.cdm.rules.update.base;
import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.characteristic.values.CompoundCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
/**
 * @author Hendriek
 * made as copy from ManyToOneUpdateRuleBase
 *
 */
public abstract class ManyToManyUpdateRuleBase implements UpdateRuleMarker{



	protected ManyToManyUpdateRuleBase() {
		super();
	}

	/**
	 * @param currentValues
	 * @return The Result when AOK, null when a parameter is missing, a
	 *         ConfigurationException when the types of the parameters do not
	 *         match.
	 * @throws CDMUpdateRuleException 
	 * @throws UpdateRuleException 
	 */
	public abstract Object update(Object[] currentValues) throws CDMUpdateRuleException, CDMUpdateRuleException;
	
	
	

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
	
	/* this is not needed */
	public static float[] getValuesFromCompound(Object [] currentValues,int characteristicsIndex) throws CDMUpdateRuleException
	
	{float[] returnValue=null; 
	if ((currentValues[characteristicsIndex] != null) ) {
			
		if (currentValues[characteristicsIndex] instanceof CompoundCharacteristicValue)

		 {
			try {
				returnValue =	((CompoundCharacteristicValue) currentValues[characteristicsIndex]).getCurrentWrapperlessValue();
			} catch (CDMRunException e) {
				throw new CDMUpdateRuleException("Fatal error in update rule: characteristic nr. "+characteristicsIndex+ "can not be read");
				
				
			}
			((Float) currentValues[characteristicsIndex])
					.floatValue();
			return  returnValue;
		 } else throw new CDMUpdateRuleException("Fatal error in update rule: characteristic nr. "+characteristicsIndex+ "is not a compound characteristic");
			
			
		}else throw new CDMUpdateRuleException("Fatal error in update rule: characteristic nr. "+characteristicsIndex+ "does not exist");
	
	
	
	
	}
	

	public static float[] getValues(Object [] currentValues,int characteristicsIndex) throws CDMUpdateRuleException
	
	{float[] returnValue=null; 
	if ((currentValues[characteristicsIndex] != null) ) {
			
		if (currentValues[characteristicsIndex] instanceof Float[])

		 {  Float[] tempvar=(Float[]) currentValues[characteristicsIndex];
		 returnValue=new float[tempvar.length];
		 for (int i=0;i<tempvar.length;i++)
			 returnValue[i]= (float) tempvar[i];
			
			return  returnValue;
		 } else throw new CDMUpdateRuleException("Fatal error in update rule: characteristic nr. "+characteristicsIndex+ " can not be read");
			
			
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
