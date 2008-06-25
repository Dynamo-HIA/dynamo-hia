package nl.rivm.emi.cdm;

/**
 * Subclass of <code>Exception</code> for use with exceptional circumstances
 * that are not tackled in other packages. Should be used for
 * <code>Exception</code>s during the run of a <code>Simulation</code>.
 * 
 * @author mondeelr
 * 
 */
public class CDMRunException extends Exception {

	private static final long serialVersionUID = 7803315390527952637L;

	/**
	 * Wrapper around the constructor of superclass <code>Exception</code>.
	 * 
	 * @param string Message to incorporate in the <code>Exception</code>.
	 */
	public CDMRunException(String string) {
		super(string);
	}

}
