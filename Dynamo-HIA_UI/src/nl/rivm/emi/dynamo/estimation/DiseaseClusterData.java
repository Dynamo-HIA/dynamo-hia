package nl.rivm.emi.dynamo.estimation;

/**
 * @author Hendriek
 * DiseaseClusterData contains inputdata pertaining to a group of clustered diseases
 * Fields are:
 * @relRiskCont  relative risk of continuous risk factor on diseases in cluster (counted within the cluster)
 *  @relRiskCat  relative risk of categorical risk factor on diseases in cluster (counted within the cluster)
 8 TODO other names
 * @RRdisExtended same as RRdis but now the indexes range over all diseases in the cluster
*	 the value is 1 if there is no equivalent RRdis value
*@Incidence incidence rates of the diseases
*@Prevalence prevalence rates of the diseases
	 
 */
public class DiseaseClusterData {

	
	//float[][] RRdis; // relative risk of independent disease number di (counted within the cluster) [first index] on 
	// on dependent disease dd [second index]
	float[][] RRdisExtended; // same as RRdis but now the indexes range over all diseases in the cluster
	// the value is 1 if there is no equivalent RRdis value
	float[] incidence;
	float[] prevalence;
	
	public float[] relRiskCont={1};

	
	public float[] excessMortality={0};
	public float caseFatality[]={0};
	public float curedFraction[]={0};
	public float[] relRiskDuurBegin={1};
	public float[] relRiskDuurEnd={1};
	public float[] halfTime={5};
	public float[][] relRiskCat={{1}};
	
	
	
	/**
	 * @param Name:
	 *            name of disease cluster
	 * @param StartN:
	 *            disease number of first disease
	 * @param N:
	 *            Number of diseases in the cluster
	 * @param Name2:
	 *            names of the diseases in the cluster
	 * @param NRIndependent:
	 *            indexes (within this cluster) of the independent diseases
	 * @param RRdis
	 *            table giving RR's of independent disease (first index) on
	 *            dependent diseases (second index)
	 * 			  
	 *          
	 */
	public DiseaseClusterData( DiseaseClusterStructure Structure,  float[][] RRdis ) {
		
		incidence = new float[Structure.nInCluster];
		
		prevalence = new float[Structure.nInCluster];
		
	//	this.RRdis = RRdis;
		// RRdisExtended is a RR matrix for all diseases, as this make looking up the
		// right RR much easier;
		

		RRdisExtended = new float[Structure.nInCluster][Structure.nInCluster];
		int dd =0;// dd contains number of current dependent disease;
		int di = 0;// di contains number of current independent disease;

		for (int d1 = 0; d1 < Structure.nInCluster; d1++)
			if (Structure.dependentDisease[d1] == false) {
				dd = 0; // dd contains number of current dependent disease;
				for (int d2 = 0; d2 < Structure.nInCluster; d2++)
					if (Structure.dependentDisease[d2] == true) {
						RRdisExtended[d1][d2] = RRdis[di][dd];
						dd++;
					} else {
						RRdisExtended[d1][d2] = 1;
					}
				di++;
			} else
				for (int d2 = 0; d2 < Structure.nInCluster; d2++) {
					RRdisExtended[d1][d2] = 1;
				}
	}

	public DiseaseClusterData() {
		incidence = new float[1];
		prevalence = new float[1];
	//	RRdis = new float [1][1];
		RRdisExtended= new float [1][1];
	//	RRdis[0][0]=1;
		RRdisExtended[0][0]=1;
	}

	/**
	 * @param Input array with prevalence rates for this cluster
	 */
	public void setPrevalence(float[] Input) {
		if (Input.length != prevalence.length)
			System.out.println("unequal length in initialisation "
					+ "of prevalences in DiseaseCluster");
		for (int i = 0; i < Input.length; i++) {
			prevalence[i] = Input[i];
		}
	}
	/**
	 * @param Input  prevalence rate for single disease
	 */
	public void setPrevalence(float Input) {
		if (prevalence.length != 1)
			System.out.println("unequal length in initialisation "
					+ "of prevalences in DiseaseCluster " + prevalence.length
					+ "instead of 1");
		prevalence[0] = Input;
	}
	/**
	 * @param Input array with incidence rates for this cluster
	 */
	public void setIncidence(float[] Input) {
		if (Input.length != incidence.length)
			System.out.println("unequal length in initialisation "
					+ "of incidences in DiseaseCluster");
		for (int i = 0; i < Input.length; i++) {
			incidence[i] = Input[i];
		}
	}
	/**
	 * @param Input: incidence rate for this cluster
	 */
	public void setIncidence(float Input) {
		if (incidence.length != 1)
			System.out.println("unequal length in initialisation "
					+ "of incidences in DiseaseCluster " + incidence.length
					+ "instead of 1");
		incidence[0] = Input;
	}

	public float[][] getRRdisExtended() {
		return RRdisExtended;
	}

	public void setRRdisExtended(float[][] rdisExtended) {
		RRdisExtended = rdisExtended;
	}
	public void setRRdisExtended( float rdisExtended) 
	{
		RRdisExtended[0][0] = rdisExtended;
	}
	
	public void setRRdisExtended( float rdisExtended, int index1, int index2) 
	{
		RRdisExtended[index1][index1] = rdisExtended;
	}

	public float[] getPrevalence() {
		return prevalence;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public float[] getRelRiskDuurBegin() {
		return relRiskDuurBegin;
	}

	public void setRelRiskDuurBegin(float[] relRiskDuurBegin) {
		this.relRiskDuurBegin = relRiskDuurBegin;
	}

	public float[] getRelRiskDuurEnd() {
		return relRiskDuurEnd;
	}

	public void setHalfTime(float[] halfTime) {
		this.halfTime = halfTime;
	}
	public float[] getHalfTime() {
		return halfTime;
	}
	public void setRelRiskDuurEnd(float[] relRiskDuurEnd) {
		this.relRiskDuurEnd = relRiskDuurEnd;
	}

	public float[] getRelRiskCont() {
		return relRiskCont;
	}

	public void setRelRiskCont(float[] relRiskCont) {
		this.relRiskCont = relRiskCont;
	}


	public float[] getExcessMortality() {
		return excessMortality;
	}

	public void setExcessMortality(float[] f) {
		this.excessMortality = f;
	}
	public void setExcessMortality(float Input) {
		if (excessMortality.length != 1)
			System.out.println("unequal length in initialisation "
					+ "of excess mortality in DiseaseCluster; " + excessMortality.length
					+ "instead of 1");
		excessMortality[0] = Input;
	}
	public float[] getCaseFatality() {
		return caseFatality;
	}

	public void setCaseFatality(float[] caseFatality) {
		this.caseFatality = caseFatality;
	}

}
