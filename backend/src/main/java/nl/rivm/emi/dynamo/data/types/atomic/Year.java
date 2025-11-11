package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractYear;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

/**
 * @author mondeelr
 * Bla
 */
@SuppressWarnings("rawtypes")
public class Year extends AbstractYear implements ContainerType{
	static final protected String XMLElementName = "year";	

	public Year(){
		super(XMLElementName, Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(Integer.MAX_VALUE));
	}
	
}
