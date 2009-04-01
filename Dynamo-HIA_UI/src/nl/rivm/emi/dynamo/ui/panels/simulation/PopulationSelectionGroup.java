package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class PopulationSelectionGroup {

	private static final String POPULATION = "Population";
	protected Group group;
	Composite plotComposite;

	public PopulationSelectionGroup(Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup) {
		this.plotComposite = plotComposite;
		group = new Group(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		group.setLayout(gridLayout);	
		group.setBackground(new Color(null, 0xee, 0xee,0xee)); // ???
		
		createDropDownArea();
	}

	private void createDropDownArea() {
				
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(27, -5);
		group.setLayoutData(formData);	
		
		// TODO: Replace with real content
		Map contentsMap = new LinkedHashMap();
		contentsMap.put("BMI1", "BMI1");
		contentsMap.put("BMI2", "BMI2");
		contentsMap.put("BMI3", "BMI3");
		GenericDropDownPanel riskDropDownPanel = 
			createDropDown(POPULATION, contentsMap);		
	}

	private GenericDropDownPanel createDropDown(String label, Map selectablePropertiesMap) {
		PopulationDataAction updatePopulationDataAction = PopulationDataAction();
		return new GenericDropDownPanel(group, label,
				selectablePropertiesMap, updatePopulationDataAction);		
	}

	private PopulationDataAction PopulationDataAction() {
		// TODO Auto-generated method stub
		return null;
	}
}
