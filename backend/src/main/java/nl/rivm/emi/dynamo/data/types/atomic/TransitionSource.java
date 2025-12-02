package nl.rivm.emi.dynamo.data.types.atomic;
/**
 * TODO Instantiation with known upperlimit?
 */
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractClassIndex;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

/**
 * Nonnegative Integer without fixed upper limit. 
 * This to enable adjustment to the range of categories the transitions can cover. 
 */
@SuppressWarnings("rawtypes")
public class TransitionSource extends AbstractClassIndex implements ContainerType{
	static final protected String XMLElementName = "from";

	public TransitionSource(){
		super(XMLElementName , Integer.valueOf(1), Integer.valueOf(Integer.MAX_VALUE));
	}
}
