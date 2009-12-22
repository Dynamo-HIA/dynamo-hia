package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractYear;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

/**
 * @author mondeelr
 * Bla
 */
public class Year extends AbstractYear implements ContainerType{
	static final protected String XMLElementName = "year";	

	public Year(){
		super(XMLElementName, new Integer(Integer.MIN_VALUE), new Integer(Integer.MAX_VALUE));
	}
	
}
