package nl.rivm.emi.dynamo.estimation;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

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

	
	Log log = LogFactory.getLog(getClass().getName());
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
	private float[] ability;
    private ArrayList<String> diseaseNames;
	/**
	 * @param Structure : cluster structure
	 * @param nClasses: number of riskfactor classes 
	  * @param RRdis
	 *            table giving RR's of independent disease (first index) on
	 *            dependent diseases (second index)
	 * 
	 * 
	 */
	public DiseaseClusterData(DiseaseClusterStructure Structure, int nClasses,
			float[][] RRdis) {

		setIncidence(new float[Structure.getNInCluster()]);

		this.prevalence = new float[Structure.getNInCluster()];
		this.ability = new float[Structure.getNInCluster()];
		this.relRiskCont = new float[Structure.getNInCluster()];
		this.excessMortality = new float[Structure.getNInCluster()];
		this.caseFatality = new float[Structure.getNInCluster()];
		this.curedFraction = new float[Structure.getNInCluster()];
		this.relRiskDuurBegin = new float[Structure.getNInCluster()];
		this.relRiskDuurEnd = new float[Structure.getNInCluster()];
		this.rrAlpha = new float[Structure.getNInCluster()];
		this.relRiskCat = new float[nClasses][Structure.getNInCluster()];
        this.diseaseNames=Structure.getDiseaseName();
		// this.RRdis = RRdis;
		// RRdisExtended is a RR matrix for all diseases, as this make looking
		// up the
		// right RR much easier;

		if (RRdis.length == Structure.getNInCluster())
			this.RRdisExtended = RRdis;
		else {
			this.RRdisExtended = new float[Structure.getNInCluster()][Structure
					.getNInCluster()];
			int dd = 0;// dd contains number of current dependent disease;
			int di = 0;// di contains number of current independent disease;

			for (int d1 = 0; d1 < Structure.getNInCluster(); d1++)
				if (Structure.getDependentDisease()[d1] == false) {
					dd = 0; // dd contains number of current dependent disease;
					for (int d2 = 0; d2 < Structure.getNInCluster(); d2++)
						if (Structure.getDependentDisease()[d2] == true) {
							this.RRdisExtended[d1][d2] = RRdis[di][dd];
							dd++;
						} else {
							this.RRdisExtended[d1][d2] = 1;
						}
					di++;
				} else
					for (int d2 = 0; d2 < Structure.getNInCluster(); d2++) {
						this.RRdisExtended[d1][d2] = 1;
					}
		}
	}

	/**
	 * 
	 */
	public DiseaseClusterData() {
		setIncidence(new float[1]);
		this.prevalence = new float[1];
		// RRdis = new float [1][1];
		this.RRdisExtended = new float[1][1];
		// RRdis[0][0]=1;
		this.RRdisExtended[0][0] = 1;
	}

	/**
	 * @param Input
	 *            array with prevalence rates for this cluster
	 */
	public void setPrevalence(float[] Input) {
		if (Input.length != this.prevalence.length && this.curedFraction[0]==0)
			log.fatal("unequal length in initialisation "
					+ "of prevalences in DiseaseCluster");
		this.prevalence=new float[Input.length];
		for (int i = 0; i < Input.length; i++) {
			this.prevalence[i] = Input[i];
		}
	}

	/**
	 * @param Input
	 *            prevalence rate for single disease
	 */
	public void setPrevalence(float Input) {
		
		this.prevalence[0] = Input;
	}

	/**
	 
	 * @param input
	 *            array with incidence rates for this cluster
	 */
	public void setIncidence(float[] input) {
		this.incidence = input;
	}

	/**
	 * @param input
	 *            array with single incidence rate for this cluster
	 * @param d
	 *            disease number to which this incidence applies
	 */
	public void setIncidence(float input, int d) {

		this.incidence[d] = input;

	}

	/**
	 * @param input
	 *            array with single prevalence rate for this cluster
	 * @param d
	 *            disease number to which this prevalence applies
	 */
	public void setPrevalence(float input, int d) {

		this.prevalence[d] = input;

	}

	/**
	 * @param input
	 *            array with single excess mortality rate for this cluster
	 * @param d
	 *            disease number to which this excess mortality applies
	 */
	public void setExcessMortality(float input, int d) {

		this.excessMortality[d] = input;

	}

	/**
	 * @param Input
	 *            : incidence rate for this cluster
	 */
	public void setIncidence(float Input) {
		
		this.incidence[0] = Input;
	}

	/**
	 * @return array [][] with the RR's for diseases on other diseases in the cluster of dimension n-diseases by n-diseases
	 * indexes are from - to
	 */
	public float[][] getRRdisExtended() {
		return DynamoLib.deepcopy(this.RRdisExtended);
	}

	/**
	 * @param rdisExtended
	 */
	public void setRRdisExtended(float[][] rdisExtended) {
		this.RRdisExtended = rdisExtended;
	}

	/**
	 * @param rdisExtended
	 */
	public void setRRdisExtended(float rdisExtended) {
		this.RRdisExtended[0][0] = rdisExtended;
	}

	/**
	 * @param rdisExtended
	 * @param index1 : first index (from)
	 * @param index2 : second index (to)
	 */
	public void setRRdisExtended(float rdisExtended, int index1, int index2) {
		this.RRdisExtended[index1][index1] = rdisExtended;
	}

	/**
	 * @return array with prevalences of diseases
	 */
	public float[] getPrevalence() {
		return DynamoLib.deepcopy(this.prevalence);
	}

	/**
	 * @return array with relative risk for begin duration (array for different diseases in the cluster)
	 */
	public float[] getRelRiskDuurBegin() {
		return DynamoLib.deepcopy(this.relRiskDuurBegin);
	}

	/**
	 * @param relRiskDuurBegin
	 */
	public void setRelRiskDuurBegin(float[] relRiskDuurBegin) {
		this.relRiskDuurBegin = relRiskDuurBegin;
	}

	/**
	 * @param relRiskDuurBegin
	 * @param d
	 * @throws DynamoInconsistentDataException when equal to zero
	 */
	public void setRelRiskDuurBegin(float relRiskDuurBegin, int d) throws DynamoInconsistentDataException {
		this.relRiskDuurBegin[d] = relRiskDuurBegin;
		if (relRiskDuurBegin<0.000001) throw new DynamoInconsistentDataException("begin relative risk for disease " +
				diseaseNames.get(d)+" is zero. This is not allowed. Please change the input"); 
	}

	/**
	 * @return array with relative risk for end duration (array for different diseases in the cluster)
	 */
	public float[] getRelRiskDuurEnd() {
		return DynamoLib.deepcopy(this.relRiskDuurEnd);
	}

	/**
	 * @param halfTime
	 */
	public void setRrAlpha(float[] halfTime) {
		this.rrAlpha = halfTime;
	}

	/**
	 * @param halfTime
	 * @param d
	 */
	public void setRrAlpha(float halfTime, int d) {
		this.rrAlpha[d] = halfTime;
	}

	/**
	 * @return alfa (coefficient for the diminishing of the relative risk); array for all diseases in the cluster
	 */
	public float[] getAlpha() {
		return DynamoLib.deepcopy(this.rrAlpha);
	}

	/**
	 * @param relRiskDuurEnd
	 */
	public void setRelRiskDuurEnd(float[] relRiskDuurEnd) {
		this.relRiskDuurEnd = relRiskDuurEnd;
	}

	/**
	 * @param relRiskDuurEnd
	 * @param d
	 */
	public void setRelRiskDuurEnd(float relRiskDuurEnd, int d) {
		this.relRiskDuurEnd[d] = relRiskDuurEnd;
	}

	/**
	 * @return array of relative risks for a continuous risk factor; array for all diseases in the cluster
	 */
	public float[] getRelRiskCont() {
		return DynamoLib.deepcopy(this.relRiskCont);
	}

	/**
	 * @param relRiskContIn
	 * @throws DynamoInconsistentDataException
	 */
	public void setRelRiskCont(float[] relRiskContIn) throws DynamoInconsistentDataException {
		
		for (int d=0;d<relRiskContIn.length;d++)
			if (relRiskContIn[d]<0.000001) throw new DynamoInconsistentDataException("relative risk for disease " +
					diseaseNames.get(d)+" is zero. This is not allowed. Please change the input"); 
		this.relRiskCont = relRiskContIn;
	}

	/**
	 * @param relRiskCont
	 * @param d
	 * @throws DynamoInconsistentDataException 
	 */
	public void setRelRiskCont(float relRiskCont, int d) throws DynamoInconsistentDataException {
		if (relRiskCont<0.000001) throw  new DynamoInconsistentDataException("relative risk for disease " +
				diseaseNames.get(d)+" is zero. This is not allowed. Please change the input"); 
		this.relRiskCont[d] = relRiskCont;
	}

	/**
	 * @return array with excess mortalities; array for all diseases in the cluster
	 */
	
	public float[] getExcessMortality() {
		return DynamoLib.deepcopy(this.excessMortality);
	}

	/**
	 * @param f
	 */
	public void setExcessMortality(float[] f) {
		this.excessMortality = f;
	}

	/**
	 * @param Input
	 */
	/**
	 * @param Input
	 */
	public void setExcessMortality(float Input) {
		if (this.excessMortality.length != 1 )
			log.fatal("unequal length in initialisation "
					+ "of excess mortality in DiseaseCluster; "
					+ this.excessMortality.length + "instead of 1");
		this.excessMortality[0] = Input;
	}

	/**
	 * @return array of casefatalities for the diseases in the cluster
	 */
	public float[] getCaseFatality() {
		return DynamoLib.deepcopy(this.caseFatality);
	}

	/**
	 * @param caseFatality
	 */
	public void setCaseFatality(float[] caseFatality) {
		this.caseFatality = caseFatality;
	}

	/**
	 * @param caseFatality
	 * @param d
	 */
	public void setCaseFatality(float caseFatality, int d) {
		this.caseFatality[d] = caseFatality;
	}

	/**
	 * @return array [][] with relative risks. first index riskfactor class, second disease
	 */
	public float[][] getRelRiskCat() {
	
		return DynamoLib.deepcopy(this.relRiskCat);
	}

	/** Set the relative risks for diseases for a categorical riskfactor
	 * @param relRiskCatIn
	 * @throws DynamoInconsistentDataException
	 */
	public void setRelRiskCat(float[][] relRiskCatIn) throws DynamoInconsistentDataException {
		this.relRiskCat=new float [relRiskCatIn.length][relRiskCatIn[0].length];
	
			for (int d = 0; d < relRiskCatIn[0].length; d++){
				int checksum=0;
		for (int cat = 0; cat < relRiskCatIn.length; cat++){
			checksum+=relRiskCatIn[cat][d];
			 this.relRiskCat[cat][d]=relRiskCatIn[cat][d];
			}
		if (Math.abs(checksum)<0.00001) throw new DynamoInconsistentDataException("all relative risks for disease "+diseaseNames.get(d)+
				" are zero. This is not allowed.");
			}
		
		
		this.relRiskCat = relRiskCatIn;
	}

	/**
	 * @param relRiskCat
	 * @param d
	 * @throws DynamoInconsistentDataException when all relRiskCats are (practically) zero
	 */
	public void setRelRiskCat(float[] relRiskCat, int d) throws DynamoInconsistentDataException {
		double checksum=0;
		for (int cat = 0; cat < relRiskCat.length; cat++){checksum+=relRiskCat[cat];
			this.relRiskCat[cat][d] = relRiskCat[cat];}
		if (checksum<0.0000001)throw new DynamoInconsistentDataException("all relative risks for disease  "+diseaseNames.get(d)+
		" are zero. This is not allowed.");
	}

	/**
	 * sets all values of RelRiskCat to value i
	 * @param i
	 *            : input value with which to fill all values of RelRiskCat
	 * @param d
	 *            : disease for which to fill in these values
	 */
	public void setRelRiskCat(float i, int d) {
		for (int cat = 0; cat < this.relRiskCat.length; cat++)
			this.relRiskCat[cat][d] = i;

	}

	/**set the relative Risk for category cat of a categorical risk factor with value f
	 * @param f
	 * @param cat 
	 *            : input value with which to fill all values of RelRiskCat
	 * @param d
	 *            : disease for which to fill in these values
	 */
	public void setRelRiskCat(float f, int cat, int d) {

		this.relRiskCat[cat][d] = f;

	}

	/**
	 * @return array with cured fractions for the diseases in the cluster
	 */
	public float[] getCuredFraction() {
		return DynamoLib.deepcopy(this.curedFraction);
	}

	/**
	 * @param curedFraction
	 */
	public void setCuredFraction(float[] curedFraction) {
		this.curedFraction = curedFraction;
	}

	/**
	 * @param curedFraction
	 * @param d
	 */
	public void setCuredFraction(float curedFraction, int d) {
		this.curedFraction[d] = curedFraction;

	}

	/**
	 * @param curedFraction
	 * @param d
	 * @param structure
	 */
	public void setCuredFraction(float curedFraction, int d,
			DiseaseClusterStructure structure) {
		this.curedFraction[d] = curedFraction;
		if (curedFraction > 0)
			structure.setWithCuredFraction(true);

	}

	/**
	 * @return array of incidences of the diseases in the cluster
	 */
	public float[] getIncidence() {
		return DynamoLib.deepcopy(this.incidence);
	}

	/**
	 * @return array of disabilities percentages for the diseases in the cluster
	 */
	public float[] getAbility() {
		return DynamoLib.deepcopy(this.ability);
	}

	/**
	 * @param disability
	 */
	public void setAbility(float[] ability1) {
		this.ability = ability1;
	}

	/**
	 * @param disability
	 * @param d
	 */
	public void setAbility(float ability1, int d) {
		this.ability[d] = ability1;
	}

}
