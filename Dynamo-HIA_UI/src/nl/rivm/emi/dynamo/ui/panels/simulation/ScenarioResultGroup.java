package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class ScenarioResultGroup {


	private static final String RISK_FACTOR_PREVALENCE = "Risk Factor Prevalence";
	private static final String TRANSITION = "Transition";
	private static final String CHANGE_WITH_RESPECT_BASELINE_SIMULATION = 
		"Change with respect to baseline simulation";
	
	protected Group group;
	private Composite plotComposite;
	private GenericComboModifyListener scenarioDropDownModifyListener;

	public ScenarioResultGroup(Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup,
			Composite topNeighbour, 
			GenericComboModifyListener scenarioDropDownModifyListener
			) {
		this.plotComposite = plotComposite;
		this.scenarioDropDownModifyListener = scenarioDropDownModifyListener;
		group = new Group(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = -15;
		group.setLayout(gridLayout);			
		createDropDownArea(topNeighbour);
	}

	private void createDropDownArea(Composite topNeighbour) {
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 0);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(97, 5);
		group.setLayoutData(formData);
		
		Label label = new Label(group, SWT.LEFT);		
		label.setText(CHANGE_WITH_RESPECT_BASELINE_SIMULATION);
		Label emptyLabel = new Label(group, SWT.LEFT);
		emptyLabel.setText("");
		Label emptyLabel2 = new Label(group, SWT.LEFT);
		emptyLabel2.setText("");
				
		Map<Combo, Map<String, Set<String>>> nestedComboMapsContents = new LinkedHashMap<Combo, Map<String, Set<String>>>();
		Set<String> transitionSet = null; /* new LinkedHashMap();
		transitionMap.put("Trans-BMI", "Trans-BMI");
		transitionMap.put("Trans-BMA", "Trans-BMA");
		transitionMap.put("Trans-BMB", "Trans-BMB");*/

		Set<String> transitionSet2 = null; /* new LinkedHashMap();
		transitionMap2.put("Trans-BMI2", "Trans-BMI2");
		transitionMap2.put("Trans-BMA2", "Trans-BMA2");
		transitionMap2.put("Trans-BMB2", "Trans-BMB2");*/
		
		Set<String> transitionSet3 = null; /* new LinkedHashMap();
		transitionMap3.put("Trans-BMI3", "Trans-BMI3");
		transitionMap3.put("Trans-BMA3", "Trans-BMA3");
		transitionMap3.put("Trans-BMB3", "Trans-BMB3");*/			
		
		Map<String, Set<String>> nestedTransitionContents = new LinkedHashMap<String, Set<String>>();
		nestedTransitionContents.put("BMI1", transitionSet);
		nestedTransitionContents.put("BMI2", transitionSet2);
		nestedTransitionContents.put("BMI3", transitionSet3);

		GenericDropDownPanel transitionDropDownPanel = 
			createDropDown(TRANSITION, transitionSet);
		this.scenarioDropDownModifyListener.
			registerDropDown(transitionDropDownPanel.getDropDown());
		nestedComboMapsContents.put(transitionDropDownPanel.getDropDown(), nestedTransitionContents);		
		
		Set<String> prevSet = null;/* new LinkedHashSet();
		prevSet.put("Prev-BMI", "Prev-BMI");
		prevSet.put("Prev-BMA", "Prev-BMA");
		prevSet.put("Prev-BMB", "Prev-BMB");*/
		
		Set<String> prevSet2 = null; /* new LinkedHashSet();
		prevSet2.put("Prev2-BMI", "Prev2-BMI");
		prevSet2.put("Prev2-BMA", "Prev2-BMA");
		prevSet2.put("Prev2-BMB", "Prev2-BMB");*/		
		
		Set<String> prevSet3 = null; /* new LinkedHashSet();
		prevSet3.put("Prev3-BMI", "Prev3-BMI");
		prevSet3.put("Prev3-BMA", "Prev3-BMA");
		prevSet3.put("Prev3-BMB", "Prev3-BMB");*/
		
		Map<String, Set<String>> nestedPrevContents = new LinkedHashMap<String, Set<String>>();
		nestedPrevContents.put("BMI1", prevSet);
		nestedPrevContents.put("BMI2", prevSet2);
		nestedPrevContents.put("BMI3", prevSet3);
		
		GenericDropDownPanel riskFactorPrevalenceDropDownPanel = 
			createDropDown(RISK_FACTOR_PREVALENCE, prevSet);
		this.scenarioDropDownModifyListener.
			registerDropDown(riskFactorPrevalenceDropDownPanel.getDropDown());
		nestedComboMapsContents.put(riskFactorPrevalenceDropDownPanel.getDropDown(), nestedPrevContents);

		// Set the nested contents
		this.scenarioDropDownModifyListener.setNestedContents(nestedComboMapsContents);				
	}

	private GenericDropDownPanel createDropDown(String label, Set<String> selectablePropertiesSet) {
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, null);		
	}
}
