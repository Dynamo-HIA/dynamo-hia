package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class UniqueName extends AbstractString implements ContainerType<String> {

	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());

	static final protected String XMLElementName = "uniquename";

	
	public UniqueName() {
		super(XMLElementName);
	}

	
	@Override
	protected UpdateValueStrategy assembleModelStrategy() {
		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
		resultStrategy.setConverter(new ValueModelConverter(
				"ValueModelConverter"));
		return resultStrategy;
	}

	public class ValueModelConverter extends AbstractString.ValueModelConverter {

		private Log log = LogFactory.getLog(this.getClass().getName());

		public ValueModelConverter(String debugString) {
			super(debugString);
		}

	
	}
}
