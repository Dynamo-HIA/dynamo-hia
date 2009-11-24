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
	POPULATIONSIZEFILE("size"), //
	POPULATIONOVERALLMORTALITYFILE("overallmortality"), //
	POPULATIONNEWBORNSFILE("newborns"), //
	POPULATIONOVERALLDALYWEIGHTSFILE("overalldalyweights"), //
	RISKFACTORS("Risk_Factors"), //
	DELETE("Delete"), //
	DISEASES("Diseases"), //
	PREVALENCES("Prevalences"), //
	INCIDENCES("Incidences"), //
	DALYWEIGHTS("DALY_Weights"), //
	// Not in the tree, but anyway.
	RELATIVERISKS("Relative_Risks"), //
	RELATIVERISKSFROMRISKFACTOR("Relative_Risks_From_Risk_Factor"), //
	RELATIVERISKSFROMDISEASES("Relative_Risks_From_Diseases"), //
	EXCESSMORTALITIES("Excess_Mortalities"), //
	MODELCONFIGURATION("Modelconfiguration"), //
	PARAMETERS("Parameters"), //
	RESULTS("Results"), //
	CONFIGURATIONFILE("configuration"), //
	RESULTSOBJECTFILE("resultsObject"), //
	TRANSITIONS("Transitions"), //
	PREVALENCEFILE("prevalence"), //
	DURATIONDISTRIBUTIONSDIRECTORY("DurationDistributions"), //
// 20090402 Not used anymore	RELRISKFORDEATHFILE("relriskfordeath"), //
	RELRISKFORDEATHDIR("Relative_Risks_For_Death"), //
// 20090402 RELRISKFORDISABILITYFILE("relriskfordisability"), //
	RELRISKFORDISABILITYDIR("Relative_Risks_For_Disability"), //
	TRANSITIONSDIR("Transitions") //
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
