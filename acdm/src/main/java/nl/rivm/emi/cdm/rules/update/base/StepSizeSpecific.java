package nl.rivm.emi.cdm.rules.update.base;

import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;

/**
 * Interface to be implemented by UpdateRules that are only valid for a certain
 * simulationstepsize.
 * 
 * @author mondeelr
 * 
 */
public interface StepSizeSpecific {
	public float getStepSize();
	public void setStepSize(float stepSize) throws CDMUpdateRuleException;
}