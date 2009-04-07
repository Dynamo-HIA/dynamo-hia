package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class RiskFactorSelectionGroup {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static final String RISK_FACTOR = "Risk Factor";
	protected Group group;
	Composite plotComposite;
	GenericComboModifyListener dropDownModifyListener;

	public RiskFactorSelectionGroup(Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup) {
		this.plotComposite = plotComposite;
		log.debug("RiskFactorSelectionGroup::this.plotComposite: " + plotComposite);
		group = new Group(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		group.setLayout(gridLayout);	
		//group.setBackground(new Color(null, 0xee, 0xee,0xee)); // ???
		
		log.debug("RiskFactorSelectionGroup" + group);
		
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
		Set<String> contentsSet = new LinkedHashSet();		
		contentsSet.add("BMI1");
		contentsSet.add("BMI2");
		contentsSet.add("BMI3");
		GenericDropDownPanel riskDropDownPanel = 
			createDropDown(RISK_FACTOR, contentsSet);
		this.dropDownModifyListener =
			riskDropDownPanel.getGenericComboModifyListener();		
	}

	private GenericDropDownPanel createDropDown(String label, Set<String> selectablePropertiesSet) {
		RiskFactorDataAction updateRiskFactorDataAction = 
			new RiskFactorDataAction();
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, updateRiskFactorDataAction);		
	}
	
	public GenericComboModifyListener getDropDownModifyListener() {
		return this.dropDownModifyListener;
	}
}
