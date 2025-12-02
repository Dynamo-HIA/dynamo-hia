package nl.rivm.emi.cdm.rules.update;


/**
 * this is an example updaterule for the users of aCDM, not relevant for DYNAMO-HIA
 */
public class ExampleRule {
/*
	
	private  HashMap<String,ArrayList<ParameterInfo>>  parameters;

	ExampleRule (){super();}
	
	int update (double [] oldValues){
	
		/* fill the other Mortality with the baselineOtherMortality */
	/*double otherMortality=  this.parameters.get("baselineOtherMortality").get(0).getValue(oldValues);
		/* apply the relative risks for the continuous characteristics */
/*ArrayList<ParameterInfo> relativeRisks= this.parameters.get("relativeRisksOtherMortContinuous");
		for (ParameterInfo singleRelativeRisk:relativeRisks){
			/* get the index number of the characteristic to which the relative risk belongs */ 
	/*		 int applyTo=singleRelativeRisk.getApplyToIndex();
			 double referenceValue=singleRelativeRisk.getConstant();
			 double RR=singleRelativeRisk.getValue(oldValues);
			 
			 otherMortality=otherMortality*(Math.pow(RR,oldValues[applyTo]-referenceValue));
		}
		/* apply the relative risks for the categorical characteristics */
  /*      relativeRisks= this.parameters.get("relativeRisksotherMortCategorical");
		for (ParameterInfo singleRelativeRisk:relativeRisks){
			 
			otherMortality=otherMortality*singleRelativeRisk.getValue(oldValues);
		}	 
		/* apply the fatal diseases mortality
		 * This is not "generalized": for every fatal disease separate code is given
		 * reasons:
		 * - it gets more complex with a general form
		 * - the number of directly fatal diseases is usually limited
		 *  
		 *  */ 
/*		double  mortalityFromFatalIHD=  this.parameters.get("baselineFatalIHD").get(0).getValue(oldValues);;
		ArrayList<ParameterInfo>  relativeRiskFatalIHD=  this.parameters.get("relativeRisksFatalIHDcont");
		for (ParameterInfo singleRelativeRisk:relativeRiskFatalIHD){
			/* get the index number of the characteristic to which the relative risk belongs */ 
/*			 int applyTo=singleRelativeRisk.getApplyToIndex();
			 double referenceValue=singleRelativeRisk.getConstant();
			 double RR=singleRelativeRisk.getValue(oldValues);
			 
			 mortalityFromFatalIHD=mortalityFromFatalIHD*(Math.pow(RR,oldValues[applyTo]-referenceValue));
		}
		/* apply the relative risks for the categorical characteristics */
 /*       relativeRisks= this.parameters.get("relativeRisksFatalIHDCategorical");
		for (ParameterInfo singleRelativeRisk:relativeRisks){
			 
			mortalityFromFatalIHD=mortalityFromFatalIHD*singleRelativeRisk.getValue(oldValues);
		}	
		/* same for stroke */
/*		double mortalityFromFatalStroke=  this.parameters.get("baselineFatalIHD").get(0).getValue(oldValues);;
		ArrayList<ParameterInfo>  relativeRiskFatalStroke=  this.parameters.get("relativeRisksFatalStrokecont");
		for (ParameterInfo singleRelativeRisk:relativeRiskFatalStroke){
			/* get the index number of the characteristic to which the relative risk belongs */ 
/*			 int applyTo=singleRelativeRisk.getApplyToIndex();
			 double referenceValue=singleRelativeRisk.getConstant();
			 double RR=singleRelativeRisk.getValue(oldValues);
			 
			 mortalityFromFatalStroke=mortalityFromFatalStroke*(Math.pow(RR,oldValues[applyTo]-referenceValue));
		}
		/* apply the relative risks for the categorical characteristics */
 /*       relativeRisks= this.parameters.get("relativeRisksFatalStrokeCategorical");
		for (ParameterInfo singleRelativeRisk:relativeRisks){
			 
			mortalityFromFatalStroke=mortalityFromFatalIHD*singleRelativeRisk.getValue(oldValues);
		}	
		
		/* now attributable mortality for diseases with constant attributable mortality */
/*		double  sumAttributableMort=0;
		ArrayList<ParameterInfo> attributableMortalities =this.parameters.get("attributableMortconstant");
		for (ParameterInfo attribMort:attributableMortalities){
			sumAttributableMort=sumAttributableMort+attribMort.getValue(oldValues);
		}
		/* now attributable mortality for diseases with constant attributable mortality */
		
/*		ArrayList<ParameterInfo> attributableMortalities2 =this.parameters.get("attributableMortTimedep");
		for (ParameterInfo attribMort:attributableMortalities2){
			int applyTo= attribMort.getApplyToIndex();
			double alfa=attribMort.getValue2(oldValues);
			sumAttributableMort=sumAttributableMort+attribMort.getValue(oldValues)*Math.exp(-alfa*oldValues[applyTo]);
		}
		double mortality=otherMortality+ mortalityFromFatalIHD+ mortalityFromFatalStroke+sumAttributableMort;
		newValue=drawRandom(mortality);
	} */

}
