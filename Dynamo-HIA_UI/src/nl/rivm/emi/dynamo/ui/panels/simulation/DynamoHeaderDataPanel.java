package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.Util;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class DynamoHeaderDataPanel extends Composite {
	
	private DynamoSimulationObject dynamoSimulationObject;
	private Composite myParent = null;
	private boolean open = false;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup theHelpGroup;
	
	public DynamoHeaderDataPanel(Composite parent, Composite bottomNeighbour,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws DynamoConfigurationException {
		super(parent, SWT.NONE);
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();		
		layout.numColumns = 4;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		
		// Follow the reading order (columns first)
		Label indexLabel = new Label(this, SWT.NONE);
		indexLabel.setText( "Name: ");			
		// Get the name value from the file node (i.e. parent)
		String[] entityArray = Util.deriveEntityLabelAndValueFromRiskSourceNode(selectedNode);
				

		
		
		
		
	}

}
