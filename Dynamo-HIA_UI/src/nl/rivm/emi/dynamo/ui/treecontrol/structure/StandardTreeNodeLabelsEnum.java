package nl.rivm.emi.dynamo.ui.treecontrol.structure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class that for the moment contains items nescessary to be able to write out
 * XML files containing only repetative parameters contained below a rootelement
 * and a rootchildelement only.
 * 
 * Example: <rootelement> <rootchild> <age>0</age> <sex>0</sex>
 * <value>4.5</value> </rootchild> </rootelement>
 * 
 * The parameters each have a type that contains among others its elementname.
 * 
 * 20090107 RLM Started adding support for different fileformats through
 * entities postfixed with "_MK2".
 * 
 * @author mondeelr
 * 
 */

public enum StandardTreeNodeLabelsEnum {

	SIMULATIONS("Simulations"), //
	REFERENCEDATA("Reference_Data"), //
	POPULATIONS("Populations"), //
	RISKFACTORS("Risk_Factors"), //
	DISEASES("Diseases"), //
	PREVALENCES("Prevalences"), //
	INCIDENCES("Incidences"), //
	DALYWEIGHTS("DALY_Weights"), //
	RELATIVERISKSFROMRISKFACTOR("Relative_Risks_From_Risk_Factor"), //
	RELATIVERISKSFROMDISEASES("Relative_Risks_From_Diseases"), //
	EXCESSMORTALITY("Excess_Mortality"), //
	MODELCONFIGURATION("Modelconfiguration"), //
	PARAMETERS("Parameters"), //
	RESULTS("Results"), //
	CONFIGURATIONFILE("configuration"), //
	TRANSITION("Transitions"), //
	PREVALENCEFILE("prevalence"), //
	DURATIONDISTRIBUTIONFILE("durationdistribution"), //
	RELRISKFORDEATHFILE("relriskfordeath"), //
	RELRISKFORDISABILITYFILE("relriskfordisability") //
	;

	Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * The enum
	 */
	private String nodeLabel;

	private StandardTreeNodeLabelsEnum(String nodeLabel) {
		this.nodeLabel = nodeLabel;
	}

	public String getNodeLabel() {
		return nodeLabel;
	}
}
