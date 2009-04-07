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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class DiseaseResultGroup {


	private static final String DISEASE_PREVALENCE = "Disease Prevalence";
	private static final String INCIDENCE = "Incidence";
	private static final String EXCESS_MORTALITY = "Excess Mortality";
	private static final String DALY_WEIGHTS = "DALY Weights";
	protected Group group;
	private Composite plotComposite;
	private GenericComboModifyListener diseaseDropDownModifyListener;

	public DiseaseResultGroup(Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup,
			Composite topNeighbour, 
			GenericComboModifyListener diseaseDropDownModifyListener
			) {
		this.plotComposite = plotComposite;
		this.diseaseDropDownModifyListener = diseaseDropDownModifyListener;
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
		formData.bottom = new FormAttachment(100, 0);
		group.setLayoutData(formData);
		
		Set<String> prevMap = null; /* new LinkedHashMap();
		prevMap.put("Prev-BMI", "Prev-BMI");
		prevMap.put("Prev-BMA", "Prev-BMA");
		prevMap.put("Prev-BMB", "Prev-BMB");*/
		
		Set<String> prevMap2 = null; /* new LinkedHashMap();
		prevMap2.put("Prev2-BMI", "Prev2-BMI");
		prevMap2.put("Prev2-BMA", "Prev2-BMA");
		prevMap2.put("Prev2-BMB", "Prev2-BMB");*/		
		
		Set<String> prevMap3 = null; /* new LinkedHashMap();
		prevMap3.put("Prev3-BMI", "Prev3-BMI");
		prevMap3.put("Prev3-BMA", "Prev3-BMA");
		prevMap3.put("Prev3-BMB", "Prev3-BMB");*/
		
		Map<Combo, Map<String, Set<String>>> nestedComboMapsContents = new LinkedHashMap<Combo, Map<String, Set<String>>>();
		Map<String, Set<String>> nestedPrevContents = new LinkedHashMap<String, Set<String>>();
		nestedPrevContents.put("BMI1", prevMap);
		nestedPrevContents.put("BMI2", prevMap2);
		nestedPrevContents.put("BMI3", prevMap3);
		
		GenericDropDownPanel diseasePrevalenceDropDownPanel = 
			createDropDown(DISEASE_PREVALENCE, prevMap);
		this.diseaseDropDownModifyListener.
			registerDropDown(diseasePrevalenceDropDownPanel.getDropDown());
		nestedComboMapsContents.put(diseasePrevalenceDropDownPanel.getDropDown(), nestedPrevContents);

		Set<String> transitionMap = null; /* = new LinkedHashMap();
		transitionMap.put("Trans-BMI", "Trans-BMI");
		transitionMap.put("Trans-BMA", "Trans-BMA");
		transitionMap.put("Trans-BMB", "Trans-BMB");*/

		Set<String> transitionMap2 = null; /* = new LinkedHashMap();
		transitionMap2.put("Trans-BMI2", "Trans-BMI2");
		transitionMap2.put("Trans-BMA2", "Trans-BMA2");
		transitionMap2.put("Trans-BMB2", "Trans-BMB2");*/
		
		Set<String> transitionMap3 = null; /* = new LinkedHashMap();
		transitionMap3.put("Trans-BMI3", "Trans-BMI3");
		transitionMap3.put("Trans-BMA3", "Trans-BMA3");
		transitionMap3.put("Trans-BMB3", "Trans-BMB3");*/			
		
		Map<String, Set<String>> nestedTransitionContents = new LinkedHashMap<String, Set<String>>();
		nestedTransitionContents.put("BMI1", transitionMap);
		nestedTransitionContents.put("BMI2", transitionMap2);
		nestedTransitionContents.put("BMI3", transitionMap3);

		GenericDropDownPanel incidenceDropDownPanel = 
			createDropDown(INCIDENCE, transitionMap);
		this.diseaseDropDownModifyListener.
			registerDropDown(incidenceDropDownPanel.getDropDown());
		nestedComboMapsContents.put(incidenceDropDownPanel.getDropDown(), nestedTransitionContents);
		
		
		GenericDropDownPanel excessMortalityDropDownPanel = 
			createDropDown(EXCESS_MORTALITY, transitionMap);
		this.diseaseDropDownModifyListener.
			registerDropDown(excessMortalityDropDownPanel.getDropDown());
		nestedComboMapsContents.put(excessMortalityDropDownPanel.getDropDown(), nestedTransitionContents);
		
		
		GenericDropDownPanel dalyWeightsDropDownPanel = 
			createDropDown(DALY_WEIGHTS, transitionMap);
		this.diseaseDropDownModifyListener.
			registerDropDown(dalyWeightsDropDownPanel.getDropDown());
		nestedComboMapsContents.put(dalyWeightsDropDownPanel.getDropDown(), nestedTransitionContents);
				
		// Set the nested contents
		this.diseaseDropDownModifyListener.setNestedContents(nestedComboMapsContents);				
	}

	private GenericDropDownPanel createDropDown(String label, Set<String> selectablePropertiesSet) {
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, null);		
	}
}
