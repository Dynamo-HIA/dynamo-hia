package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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

public class RiskFactorResultGroup {

	private static final String RISK_FACTOR_PREVALENCE = "Risk Factor Prevalence";
	private static final String TRANSITION = "Transition";
	
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
		formData.bottom = new FormAttachment(65, -5);
		group.setLayoutData(formData);
		
		Set<String> prevSet = new LinkedHashSet();
		prevSet.add("Prev-BMI");
		prevSet.add("Prev-BMA");
		prevSet.add("Prev-BMB");
		
		Set<String> prevSet2 = new LinkedHashSet();
		prevSet2.add("Prev2-BMI");
		prevSet2.add("Prev2-BMA");
		prevSet2.add("Prev2-BMB");		
		
		Set<String> prevSet3 = new LinkedHashSet();
		prevSet3.add("Prev3-BMI");
		prevSet3.add("Prev3-BMA");
		prevSet3.add("Prev3-BMB");
		
		Map<Combo, Map<String, Set<String>>> nestedComboMapsContents = new LinkedHashMap<Combo, Map<String, Set<String>>>();
		Map<String, Set<String>> nestedPrevContents = new LinkedHashMap<String, Set<String>>();	
		nestedPrevContents.put("BMI1", prevSet);
		nestedPrevContents.put("BMI2", prevSet2);
		nestedPrevContents.put("BMI3", prevSet3);
		
		GenericDropDownPanel riskFactorPrevalenceDropDownPanel = 
			createDropDown(RISK_FACTOR_PREVALENCE, prevSet);
		this.riskDropDownModifyListener.
			registerDropDown(riskFactorPrevalenceDropDownPanel.getDropDown());
		nestedComboMapsContents.put(riskFactorPrevalenceDropDownPanel.getDropDown(), nestedPrevContents);

		Set<String> transitionSet = new LinkedHashSet();
		transitionSet.add("Trans-BMI");
		transitionSet.add("Trans-BMA");
		transitionSet.add("Trans-BMB");

		Set<String> transitionSet2 = new LinkedHashSet();
		transitionSet2.add("Trans-BMI2");
		transitionSet2.add("Trans-BMA2");
		transitionSet2.add("Trans-BMB2");
		
		Set<String> transitionSet3 = new LinkedHashSet();
		transitionSet3.add("Trans-BMI3");
		transitionSet3.add("Trans-BMA3");
		transitionSet3.add("Trans-BMB3");		
		
		Map<String, Set<String>> nestedTransitionContents = new LinkedHashMap<String, Set<String>>();
		nestedTransitionContents.put("BMI1", transitionSet);
		nestedTransitionContents.put("BMI2", transitionSet2);
		nestedTransitionContents.put("BMI3", transitionSet3);

		GenericDropDownPanel transitionDropDownPanel = 
			createDropDown(TRANSITION, transitionSet);
		this.riskDropDownModifyListener.
			registerDropDown(transitionDropDownPanel.getDropDown());
		nestedComboMapsContents.put(transitionDropDownPanel.getDropDown(), nestedTransitionContents);

		// Set the nested contents
		this.riskDropDownModifyListener.setNestedContents(nestedComboMapsContents);				
	}

	private GenericDropDownPanel createDropDown(String label, Set<String> selectablePropertiesSet) {
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, null);		
	}
}
