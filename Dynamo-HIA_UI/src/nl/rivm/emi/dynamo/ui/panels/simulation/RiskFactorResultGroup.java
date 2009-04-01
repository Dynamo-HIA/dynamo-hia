package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class RiskFactorResultGroup {

	private static final String RISK_FACTOR_PREVALENCE = "Risk Factor Prevalence";
	private static final String TRANSITION = "Transition";
	private static final String REL_RISK_FOR_DEATH = "Relative Risk for death";
	protected Group group;
	private Composite plotComposite;
	private GenericComboModifyListener riskDropDownModifyListener;

	public RiskFactorResultGroup(Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup,
			Composite topNeighbour, 
			GenericComboModifyListener riskDropDownModifyListener
			) {
		this.plotComposite = plotComposite;
		this.riskDropDownModifyListener = riskDropDownModifyListener;
		group = new Group(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		group.setLayout(gridLayout);			
		
		createDropDownArea(topNeighbour);
	}

	private void createDropDownArea(Composite topNeighbour) {
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(78, -5);
		group.setLayoutData(formData);
		
		Map prevMap = new LinkedHashMap();
		prevMap.put("Prev-BMI", "Prev-BMI");
		prevMap.put("Prev-BMA", "Prev-BMA");
		prevMap.put("Prev-BMB", "Prev-BMB");
		
		Map prevMap2 = new LinkedHashMap();
		prevMap2.put("Prev2-BMI", "Prev2-BMI");
		prevMap2.put("Prev2-BMA", "Prev2-BMA");
		prevMap2.put("Prev2-BMB", "Prev2-BMB");		
		
		Map prevMap3 = new LinkedHashMap();
		prevMap3.put("Prev3-BMI", "Prev3-BMI");
		prevMap3.put("Prev3-BMA", "Prev3-BMA");
		prevMap3.put("Prev3-BMB", "Prev3-BMB");
		
		Map<String, Map> nestedPrevContents = new HashMap<String, Map>();
		nestedPrevContents.put("BMI1", prevMap);
		nestedPrevContents.put("BMI2", prevMap2);
		nestedPrevContents.put("BMI3", prevMap3);
		
		GenericDropDownPanel riskFactorprevalenceDownPanel = 
			createDropDown(RISK_FACTOR_PREVALENCE, prevMap);
/*		
		GenericComboModifyListener prevalenceDropDownModifyListener =
			riskFactorprevalenceDownPanel.getGenericComboModifyListener();*/
		this.riskDropDownModifyListener.
			registerDropDown(riskFactorprevalenceDownPanel.getDropDown());
		this.riskDropDownModifyListener.setNestedContents(nestedPrevContents);

		
		
		
		
		/*
		
		Map contentsMap2 = new HashMap();
		contentsMap2.put("RiskTest3", 
				"RiskTest3");
		GenericDropDownPanel transitionDropDownPanel = 
			createDropDown(TRANSITION, contentsMap2);
		GenericComboModifyListener transitionDropDownModifyListener =
			transitionDropDownPanel.getGenericComboModifyListener();
		
		Map transitionMap = new HashMap();
		transitionMap.put("Trans-BMI", "Trans-BMI");
		transitionMap.put("Trans-BMA", "Trans-BMA");
		transitionMap.put("Trans-BMB", "Trans-BMB");
		transitionDropDownModifyListener.setNestedContents(transitionMap);
		
		transitionDropDownModifyListener.
			registerDropDown(transitionDropDownPanel.getDropDown());		
		
		Map contentsMap3 = new HashMap();
		contentsMap3.put("RiskTest1", 
				"RiskTest2");
		GenericDropDownPanel relRiskForDeathDropDownPanel = 
			createDropDown(REL_RISK_FOR_DEATH, contentsMap3);
		GenericComboModifyListener relRiskForDeathModifyListener =
			riskFactorprevalenceDownPanel.getGenericComboModifyListener();
		
		Map relRiskForDeathMap = new HashMap();
		relRiskForDeathMap.put("RRdeath-BMI", "RRdeath-BMI");
		relRiskForDeathMap.put("RRdeath-BMA", "RRdeath-BMA");
		relRiskForDeathMap.put("RRdeath-BMB", "RRdeath-BMB");
		relRiskForDeathModifyListener.setNestedContents(relRiskForDeathMap);
		
		relRiskForDeathModifyListener.
			registerDropDown(relRiskForDeathDropDownPanel.getDropDown());
			*/
	}

	private GenericDropDownPanel createDropDown(String label, Map selectablePropertiesMap) {
		return new GenericDropDownPanel(group, label,
				selectablePropertiesMap, null);		
	}
}
