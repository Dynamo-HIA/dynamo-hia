package nl.rivm.emi.dynamo.data.types.atomic;
/**
 * Index with flexible upper limit and nonstandard lowerlimit 1.
 * Currently in use as index for categories of RiskFactors.
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractFlexibleUpperLimitInteger;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

public class FlexDex extends AbstractFlexibleUpperLimitInteger implements
		ContainerType<Integer>{
	Log log = LogFactory.getLog(this.getClass().getName());

	static final protected String XMLElementName = "flexdex";

	public FlexDex() {
		super(XMLElementName, new Integer(1), new Integer(Integer.MAX_VALUE));
	}

	@Override
	public void setMAX_VALUE(Integer newMaxValue) {
		super.setMAX_VALUE(newMaxValue);
	}
}
