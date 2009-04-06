package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class RelativeRiskSelectionGroup {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static final String FROM = "From";
	private static final String TO = "To";
	protected Group group;
	Composite plotComposite;
	GenericComboModifyListener dropDownModifyListener;

	public RelativeRiskSelectionGroup(Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup) {
		this.plotComposite = plotComposite;
		log.debug("relativeRiskFactorSelectionGroup::this.plotComposite: " + plotComposite);
		group = new Group(plotComposite, SWT.FILL);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = -3;
		group.setLayout(gridLayout);	
		group.setBackground(new Color(null, 0xee, 0xee,0xee)); // ???		
		log.debug("relativeRiskFactorSelectionGroup" + group);
		
		createDropDownArea();
	}

	private void createDropDownArea() {
				
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, -5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(44, 0);
		group.setLayoutData(formData);			
		
		// TODO: Replace with real content
		Map contentsMap = new LinkedHashMap();
		contentsMap.put("BMI1", "BMI1");
		contentsMap.put("BMI2", "BMI2");
		contentsMap.put("BMI3", "BMI3");
		GenericDropDownPanel fromDropDownPanel = 
			createDropDown(FROM, contentsMap);
		this.dropDownModifyListener =
			fromDropDownPanel.getGenericComboModifyListener();
		
		
		// TODO: Replace with real content
		GenericDropDownPanel toDropDownPanel = 
			createDropDown(TO, contentsMap);
		this.dropDownModifyListener =
			toDropDownPanel.getGenericComboModifyListener();
		
	}

	private GenericDropDownPanel createDropDown(String label, Map selectablePropertiesMap) {
		RelativeRiskFactorDataAction updateRelativeRiskFactorDataAction = 
			new RelativeRiskFactorDataAction();
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesMap, updateRelativeRiskFactorDataAction);		
	}
	
	public GenericComboModifyListener getDropDownModifyListener() {
		// TODO Replace with getXXXXDropDownPanel to ask for the corresponding listener (not directly!!!)
		return this.dropDownModifyListener;
	}
}
