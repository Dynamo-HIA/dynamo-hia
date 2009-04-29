package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.Calendar;
import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractYear;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class Year extends AbstractYear implements ContainerType{
	static final protected String XMLElementName = "year";	

	public Year(){
		super(XMLElementName, new Integer(Integer.MIN_VALUE), new Integer(Integer.MAX_VALUE));
	}
	
}
