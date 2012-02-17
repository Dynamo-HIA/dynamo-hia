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

	

	public void update();

	public boolean isDisposed();

	public int getSelection();

	public void setSelection(int newValue);

	void setIndeterminate(String text);

}
