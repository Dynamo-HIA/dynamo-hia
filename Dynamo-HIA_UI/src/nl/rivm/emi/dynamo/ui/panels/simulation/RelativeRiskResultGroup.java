package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class RelativeRiskResultGroup {


	private static final String RELATIVE_RISK = "Relative Risk";
	protected Group group;
	private Composite plotComposite;
	private GenericComboModifyListener relativeRiskDropDownModifyListener;

	public RelativeRiskResultGroup(Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup,
			Composite topNeighbour, 
			GenericComboModifyListener relativeRiskDropDownModifyListener
			) {
		this.plotComposite = plotComposite;
		this.relativeRiskDropDownModifyListener = relativeRiskDropDownModifyListener;
		group = new Group(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = -5;
		group.setLayout(gridLayout);			
		createDropDownArea(topNeighbour);
	}

	private void createDropDownArea(Composite topNeighbour) {
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 0);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(67, 0);
		group.setLayoutData(formData);
		
		Map relativeRiskMap = new LinkedHashMap();
		relativeRiskMap.put("Prev-BMI", "Prev-BMI");
		relativeRiskMap.put("Prev-BMA", "Prev-BMA");
		relativeRiskMap.put("Prev-BMB", "Prev-BMB");
		
		Map relativeRiskMap2 = new LinkedHashMap();
		relativeRiskMap2.put("Prev2-BMI", "Prev2-BMI");
		relativeRiskMap2.put("Prev2-BMA", "Prev2-BMA");
		relativeRiskMap2.put("Prev2-BMB", "Prev2-BMB");		
		
		Map relativeRiskMap3 = new LinkedHashMap();
		relativeRiskMap3.put("Prev3-BMI", "Prev3-BMI");
		relativeRiskMap3.put("Prev3-BMA", "Prev3-BMA");
		relativeRiskMap3.put("Prev3-BMB", "Prev3-BMB");
		
		Map<Combo, Map> nestedComboMapsContents = new LinkedHashMap<Combo, Map>();
		Map<String, Map> nestedRelativeRiskContents = new LinkedHashMap<String, Map>();
		nestedRelativeRiskContents.put("BMI1", relativeRiskMap);
		nestedRelativeRiskContents.put("BMI2", relativeRiskMap2);
		nestedRelativeRiskContents.put("BMI3", relativeRiskMap3);
		
		GenericDropDownPanel relativeRiskDropDownPanel = 
			createDropDown(RELATIVE_RISK, relativeRiskMap);
		this.relativeRiskDropDownModifyListener.
			registerDropDown(relativeRiskDropDownPanel.getDropDown());
		nestedComboMapsContents.put(relativeRiskDropDownPanel.getDropDown(), nestedRelativeRiskContents);
				
		// Set the nested contents
		this.relativeRiskDropDownModifyListener.setNestedContents(nestedComboMapsContents);				
	}

	private GenericDropDownPanel createDropDown(String label, Map selectablePropertiesMap) {
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesMap, null);		
	}
}
