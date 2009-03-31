package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.HashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class RiskFactorSelectionGroup {

	private static final String RISK_FACTOR = "Risk Factor";
	protected Group group;
	Composite plotComposite;

	public RiskFactorSelectionGroup(Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup) {
		// TODO Auto-generated constructor stub
		this.plotComposite = plotComposite;
		group = new Group(plotComposite, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);		
		createDropDownArea();
	}

	private void createDropDownArea() {			
		GenericDropDownPanel riskDropDownPanel = createDropDown();
		riskDropDownPanel.handleFirstInContainer(30);		
		
	}

	private GenericDropDownPanel createDropDown() {
		// TODO Auto-generated method stub		
		Map selectablePropertiesMap = new HashMap();
		selectablePropertiesMap.put("RiskTest1", 
				"RiskTest2");
		return new GenericDropDownPanel(group, RISK_FACTOR,
				selectablePropertiesMap, null);
		
	}

	public void handlePlacementInContainer() {
		// TODO Auto-generated method stub
		
	}

}
