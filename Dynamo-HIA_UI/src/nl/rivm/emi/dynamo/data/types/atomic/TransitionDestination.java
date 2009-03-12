package nl.rivm.emi.dynamo.data.types.atomic;
/**
 * TODO Instantiation with known upperlimit?
 */
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

/**
 * Nonnegative Integer without fixed upper limit. 
 * This to enable adjustment to the range of categories the transitions can cover. 
 */
public class TransitionDestination extends
		AbstractClassIndex implements ContainerType {
	static final public String XMLElementName = "to";

	public TransitionDestination(){
		super(XMLElementName, new Integer(1), new Integer(Integer.MAX_VALUE));
	}
}
