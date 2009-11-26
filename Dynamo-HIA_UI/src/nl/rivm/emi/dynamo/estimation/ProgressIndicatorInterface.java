package nl.rivm.emi.dynamo.estimation;

public interface ProgressIndicatorInterface {

	/**
	 * 
	 * @return
	 */
	public void setMaximum(int percent);

	/**
	 * 
	 * @return
	 */
	public int getPosition();
	/**
	 * 
	 * @return
	 */
	public void update(int percent);

	/**
	 * 
	 * @return
	 */
	public void dispose();

}
