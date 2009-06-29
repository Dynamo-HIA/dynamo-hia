package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class UniqueName extends AbstractString implements ContainerType{

	private Log log = LogFactory.getLog(this.getClass().getName());

	static final protected String XMLElementName = "uniquename";
	private DynamoTabDataManager dynamoTabDataManager;
	private String label;
	private Shell shell;
	
	public UniqueName() {
		super(XMLElementName);
	}
	
	public UniqueName(DynamoTabDataManager dynamoTabDataManager,
			String label,
			Shell shell
			) {
		super(XMLElementName);
		this.dynamoTabDataManager = dynamoTabDataManager;
		this.label = label;
		this.shell = shell;
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

		@Override
		public Object convert(Object arg0)  {
			log.debug("UniqueName: Override convert(Object) entered with:" +
			arg0.toString());
			Object result = null;
			if (arg0 instanceof String) {
				result = arg0;
				try {
					
					log.debug("result: MODELVIEW " + result);
					// This is a workaround created to set the model value of "Name"
					// in the Scenario tabs of Simulation Model
					UniqueName.this.dynamoTabDataManager.updateObjectState(UniqueName.this.label, 
							(String) result);
				} catch (ConfigurationException ce) {					
					this.handleErrorMessage(ce);
				} catch (NoMoreDataException e) {
					
					this.handleErrorMessage(e);
				}
			}
			return result;
		}
		
		private void handleErrorMessage(Exception e) {
			this.log.fatal(e);
			e.printStackTrace();
			MessageBox box = new MessageBox(UniqueName.this.shell,
					SWT.ERROR_UNSPECIFIED);
			box.setText("Error occured during creation of a new tab " + e.getMessage());
			box.setMessage(e.getMessage());
			box.open();
		}
	}
	

}

	
