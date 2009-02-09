package nl.rivm.emi.dynamo.estimation;

/**
 * @author Hendriek DiseaseClusterData contains inputdata pertaining to a group
 *         of clustered diseases Fields are:
 * @relRiskCont relative risk of continuous risk factor on diseases in cluster
 *              (counted within the cluster)
 * @relRiskCat relative risk of categorical risk factor on diseases in cluster
 *             (counted within the cluster) NB: the first index is the categorie
 *             of the risk factor (from), the second the diseases to which the
 *             RR applies 8 TODO other names
 * @RRdisExtended same as RRdis but now the indexes range over all diseases in
 *                the cluster the value is 1 if there is no equivalent RRdis
 *                value
 *@Incidence incidence rates of the diseases
 *@Prevalence prevalence rates of the diseases
 */
public class DiseaseClusterData {

	// float[][] RRdis; // relative risk of independent disease number di
	// (counted within the cluster) [first index] on
	// on dependent disease dd [second index]
	private float[][] RRdisExtended; // same as RRdis but now the indexes range
	// over all diseases in the cluster
	// the value is 1 if there is no equivalent RRdis value
	private float[] incidence;
	private float[] prevalence;
	private float[] relRiskCont = { 1 };
	private float[] excessMortality = { 0 };
	private float caseFatality[] = { 0 };
	private float curedFraction[] = { 0 };
	private float[] relRiskDuurBegin = { 1 };
	private float[] relRiskDuurEnd = { 1 };
	private float[] rrAlpha = { 5 };
	private float[][] relRiskCat = { { 1 } };
	private float[] disability;

	/**
	 * @param Name
	 *            : name of disease cluster
	 * @param StartN
	 *            : disease number of first disease
	 * @param N
	 *            : Number of diseases in the cluster
	 * @param Name2
	 *            : names of the diseases in the cluster
	 * @param NRIndependent
	 *            : indexes (within this cluster) of the independent diseases
	 * @param RRdis
	 *            table giving RR's of independent disease (first index) on
	 *            dependent diseases (second index)
	 * 
	 * 
	 */
	public DiseaseClusterData(DiseaseClusterStructure Structure, int nClasses,
			float[][] RRdis) {

		setIncidence(new float[Structure.getNInCluster()]);

		prevalence = new float[Structure.getNInCluster()];
		disability = new float[Structure.getNInCluster()];
		relRiskCont = new float[Structure.getNInCluster()];
		excessMortality = new float[Structure.getNInCluster()];
		caseFatality = new float[Structure.getNInCluster()];
		curedFraction = new float[Structure.getNInCluster()];
		relRiskDuurBegin = new float[Structure.getNInCluster()];
		relRiskDuurEnd = new float[Structure.getNInCluster()];
		rrAlpha = new float[Structure.getNInCluster()];
		relRiskCat = new float[nClasses][Structure.getNInCluster()];

		// this.RRdis = RRdis;
		// RRdisExtended is a RR matrix for all diseases, as this make looking
		// up the
		// right RR much easier;

		if (RRdis.length == Structure.getNInCluster())
			RRdisExtended = RRdis;
		else {
			RRdisExtended = new float[Structure.getNInCluster()][Structure
					.getNInCluster()];
			int dd = 0;// dd contains number of current dependent disease;
			int di = 0;// di contains number of current independent disease;

			for (int d1 = 0; d1 < Structure.getNInCluster(); d1++)
				if (Structure.getDependentDisease()[d1] == false) {
					dd = 0; // dd contains number of current dependent disease;
					for (int d2 = 0; d2 < Structure.getNInCluster(); d2++)
						if (Structure.getDependentDisease()[d2] == true) {
							RRdisExtended[d1][d2] = RRdis[di][dd];
							dd++;
						} else {
							RRdisExtended[d1][d2] = 1;
						}
					di++;
				} else
					for (int d2 = 0; d2 < Structure.getNInCluster(); d2++) {
						RRdisExtended[d1][d2] = 1;
					}
		}
	}

	public DiseaseClusterData() {
		setIncidence(new float[1]);
		prevalence = new float[1];
		// RRdis = new float [1][1];
		RRdisExtended = new float[1][1];
		// RRdis[0][0]=1;
		RRdisExtended[0][0] = 1;
	}

	/**
	 * @param Input
	 *            array with prevalence rates for this cluster
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
	 * @param Input
	 *            prevalence rate for single disease
	 */
	public void setPrevalence(float Input) {
		if (prevalence.length != 1)
			System.out.println("unequal length in initialisation "
					+ "of prevalences in DiseaseCluster " + prevalence.length
					+ "instead of 1");
		prevalence[0] = Input;
	}

	/**
	 * @param Input
	 *            array with incidence rates for this cluster
	 */
	public void setIncidence(float[] input) {
		incidence = input;
	}

	/**
	 * @param input
	 *            array with single incidence rate for this cluster
	 * @param d
	 *            disease number to which this incidence applies
	 */
	public void setIncidence(float input, int d) {

		incidence[d] = input;

	}

	/**
	 * @param input
	 *            array with single prevalence rate for this cluster
	 * @param d
	 *            disease number to which this prevalence applies
	 */
	public void setPrevalence(float input, int d) {

		prevalence[d] = input;

	}

	/**
	 * @param input
	 *            array with single excess mortality rate for this cluster
	 * @param d
	 *            disease number to which this excess mortality applies
	 */
	public void setExcessMortality(float input, int d) {

		excessMortality[d] = input;

	}

	/**
	 * @param Input
	 *            : incidence rate for this cluster
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

	public void setRRdisExtended(float rdisExtended) {
		RRdisExtended[0][0] = rdisExtended;
	}

	public void setRRdisExtended(float rdisExtended, int index1, int index2) {
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

	public void setRelRiskDuurBegin(float relRiskDuurBegin, int d) {
		this.relRiskDuurBegin[d] = relRiskDuurBegin;
	}

	public float[] getRelRiskDuurEnd() {
		return relRiskDuurEnd;
	}

	public void setRrAlpha(float[] halfTime) {
		this.rrAlpha = halfTime;
	}

	public void setRrAlpha(float halfTime, int d) {
		this.rrAlpha[d] = halfTime;
	}

	public float[] getRrAlpha() {
		return rrAlpha;
	}

	public void setRelRiskDuurEnd(float[] relRiskDuurEnd) {
		this.relRiskDuurEnd = relRiskDuurEnd;
	}

	public void setRelRiskDuurEnd(float relRiskDuurEnd, int d) {
		this.relRiskDuurEnd[d] = relRiskDuurEnd;
	}

	public float[] getRelRiskCont() {
		return relRiskCont;
	}

	public void setRelRiskCont(float[] relRiskCont) {
		this.relRiskCont = relRiskCont;
	}

	public void setRelRiskCont(float relRiskCont, int d) {
		this.relRiskCont[d] = relRiskCont;
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
					+ "of excess mortality in DiseaseCluster; "
					+ excessMortality.length + "instead of 1");
		excessMortality[0] = Input;
	}

	public float[] getCaseFatality() {
		return caseFatality;
	}

	public void setCaseFatality(float[] caseFatality) {
		this.caseFatality = caseFatality;
	}

	public void setCaseFatality(float caseFatality, int d) {
		this.caseFatality[d] = caseFatality;
	}

	public float[][] getRelRiskCat() {
		return relRiskCat;
	}

	public void setRelRiskCat(float[][] relRiskCat) {
		this.relRiskCat = relRiskCat;
	}

	public void setRelRiskCat(float[] relRiskCat, int d) {
		for (int cat = 0; cat < relRiskCat.length; cat++)
			this.relRiskCat[cat][d] = relRiskCat[cat];
	}

	/**
	 * 
	 * @param i
	 *            : input value with which to fill all values of RelRiskCat
	 * @param d
	 *            : disease for which to fill in these values
	 */
	public void setRelRiskCat(float i, int d) {
		for (int cat = 0; cat < relRiskCat.length; cat++)
			this.relRiskCat[cat][d] = i;

	}

	/**
	 * @param f
	 * @param i
	 *            : input value with which to fill all values of RelRiskCat
	 * @param d
	 *            : disease for which to fill in these values
	 */
	public void setRelRiskCat(float f, int cat, int d) {

		this.relRiskCat[cat][d] = f;

	}

	public float[] getCuredFraction() {
		return curedFraction;
	}

	public void setCuredFraction(float[] curedFraction) {
		this.curedFraction = curedFraction;
	}

	public void setCuredFraction(float curedFraction, int d) {
		this.curedFraction[d] = curedFraction;

	}

	public void setCuredFraction(float curedFraction, int d,
			DiseaseClusterStructure structure) {
		this.curedFraction[d] = curedFraction;
		if (curedFraction > 0)
			structure.setWithCuredFraction(true);

	}

	public float[] getIncidence() {
		return incidence;
	}

	public float[] getDisability() {
		return disability;
	}

	public void setDisability(float[] disability) {
		this.disability = disability;
	}

	public void setDisability(float disability, int d) {
		this.disability[d] = disability;
	}

}
