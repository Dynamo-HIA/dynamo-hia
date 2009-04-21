package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * 
 * Defines the simulation panel parameter group for the tabs
 * 
 * @author schutb
 *
 */
public class DynamoTabsParameterGroup {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public Group group;

	public DynamoTabsParameterGroup(Composite parent, BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) throws ConfigurationException {
		group = new Group(parent, SWT.NONE);

		FillLayout fillLayout = new FillLayout();
		group.setLayout(fillLayout);
		//group.setBackground(new Color(null, 0xbb, 0xbb,0xbb)); //gray
		
		DynamoTabsDataPanel dynamoTabsDataPanel = new DynamoTabsDataPanel(group,
				selectedNode, dynamoSimulationObject, dataBindingContext,
				helpGroup);		
	}

	/**
	 * 
	 * Place the last (and second) group in the container
	 * 
	 * @param topNeighbour
	 */
	public void putLastInContainer(Composite topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}

	
	public void handleNextInContainer(Group topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		group.setLayoutData(formData);		
	}
	
}
