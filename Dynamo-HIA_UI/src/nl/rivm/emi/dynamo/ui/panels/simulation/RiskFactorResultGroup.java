package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.HashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class RiskFactorResultGroup {

	private static final String RISK_FACTOR = "Risk Factor";
	protected Group group;
	Composite plotComposite;

	public RiskFactorResultGroup(Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup) {
		// TODO Auto-generated constructor stub
		this.plotComposite = plotComposite;
		group = new Group(plotComposite, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);		
		createDropDownArea();
	}

	private void createDropDownArea() {			
		Map contentsMap = new HashMap();
		contentsMap.put("RiskTest1", 
				"RiskTest2");
		GenericDropDownPanel riskFactorprevalenceDownPanel = 
			createDropDown(RISK_FACTOR, contentsMap);
		riskFactorprevalenceDownPanel.handleFirstInContainer(30);
		
		Map contentsMap2 = new HashMap();
		contentsMap.put("RiskTest1", 
				"RiskTest2");
		GenericDropDownPanel transitionDropDownPanel = 
			createDropDown(RISK_FACTOR, contentsMap2);		
		transitionDropDownPanel.
			handleNextInContainer(riskFactorprevalenceDownPanel.group, 30);
		
		Map contentsMap3 = new HashMap();
		contentsMap.put("RiskTest1", 
				"RiskTest2");
		GenericDropDownPanel relRiskForDeathDropDownPanel = 
			createDropDown(RISK_FACTOR, contentsMap3);		
		relRiskForDeathDropDownPanel.
			handleNextInContainer(riskFactorprevalenceDownPanel.group, 30);		
	}

	private GenericDropDownPanel createDropDown(String label, Map selectablePropertiesMap) {
		// TODO Auto-generated method stub		
		return new GenericDropDownPanel(group, RISK_FACTOR,
				selectablePropertiesMap, null);
		
	}

	public void putNextInContainer(Group group, int i) {
		// TODO Auto-generated method stub
		
	}

}
