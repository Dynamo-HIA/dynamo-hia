package nl.rivm.emi.dynamo.estimation;
//todo deze classe moet nog helemaal worden gemaakt maar kan pas als data beschikbaar zijn
import java.util.Random;

/* During programming the following conventions were followed (in principle)
 * index d = in loop over all diseases
 * index di = in loop  over all independent diseases
 * index dd = in loop over all dependent diseases
 * index c = in loop over disease clusters
 * index r = in loop over all risk factors (not used in DYNAMO HIA)
 * index rc =  in loop over all risk factor classes
 * index i = in loop over all persons in the simulated population
 * index a = in loop over age
 * index g = in loop over gender
 * j and k are used as loop variables in other cases
 */
public class ParameterEstimation {
ModelParameters[][] parameters = new ModelParameters[2][96];
double transmat [][][][];
double[][] curedFraction =new double [2][96];
double [][][] prev=new double[2][96][3]; // first index: gender,
// second:  is age  and third index is number of categories in risk factor
public ParameterEstimation (){
	
	//curedFraction[0]=parameters[0][95].calculateCuredPrevFraction(incidence, prevalence,curedFraction);
	//curedFraction[1]=parameters[0][95].calculateCuredPrevFraction(incidence, prevalence,curedFraction);

	        // TODO  volgorde van deze regelen; method moet verplaatst naar een static object want zo moet je eerst een instance maken
	 // TODO nog afmaken want er moet een incidentie worden ingelezen

	
	InputData Testdata = new InputData();
//	double [] prev = ;nog afmaken 
for (int age=0;age<96;age++)
			for (int g=0;g<2;g++){
		try {
			// todo echte data hier inlezen 
		
			parameters[age][g] = new ModelParameters(100, Testdata,0,0);
			prev[age][g]=Testdata.prevRisk[age][g]	;	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	//		NettTransitionRates(double[] oldPrev, double[] newPrev, double baselineMort, double [] RR) {
				
		double[] RRdis={1};		
       if (age>0) {NettTransitionRates transEst = new NettTransitionRates(prev[age-1][g],prev[age][g],0.001,RRdis);
        
       transmat[age][g]=transEst.transitionRates;
        }
			}}
		
double[][] CuredFraction= new double [96][2];
	


public static void main(String argv[]) {
		 
		 ParameterEstimation E=new ParameterEstimation();
		
		
		
		
		

// TODO: adapt estimationpop in order to deal with other combinations of risk factors (done but not tested
// TODO: what if prevalence get over 1?
//		
		
	}
}
