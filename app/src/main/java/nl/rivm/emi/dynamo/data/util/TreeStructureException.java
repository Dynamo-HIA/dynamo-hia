package nl.rivm.emi.dynamo.data.util;

/**
 * @author mondeelr<br/>
 * Thrown when during navigation of the tree the structure encountered
 *  was different from expected.
 *
 */
public class TreeStructureException extends Exception {
	private static final long serialVersionUID = -8139636897729624729L;

	public TreeStructureException(String string) {
		super(string);
	}
}
