package nl.rivm.emi.dynamo.data.types.functional;
/**
 * Example interface for API users.
 * @author mondeelr
 *
 */
public interface IDiseaseData {

	public String getName();

	public float[][][] getPrevalenceData();

	public abstract float[][] getIncidenceData();

	public abstract float[][][] getExcessMortalityData();

	public abstract String getDalyWeightsData();
}