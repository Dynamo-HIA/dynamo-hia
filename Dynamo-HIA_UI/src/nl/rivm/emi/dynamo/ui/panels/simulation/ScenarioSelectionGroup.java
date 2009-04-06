package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class ScenarioSelectionGroup extends Composite {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static final String NAME = "Name";
	private static final String MIN_AGE = "Minimum Age";
	private static final String MAX_AGE = "Maximum Age";
	private static final String GENDER = "Gender";

	private static final String TARGET_OF_INTERVENTION = 
		"Target of Intervention";

	private static final String SUCCESS_RATE = "Success Rate";

	private static final String SEMICOLON = ":";	

	private static final String PERCENTAGE = "(%)";
	
	protected Group group;
	private Composite plotComposite;
	private DynamoSimulationObject dynamoSimulationObject;
	private BaseNode selectedNode;	
	private GenericComboModifyListener dropDownModifyListener;



	public ScenarioSelectionGroup(Composite plotComposite,
			DynamoSimulationObject dynamoSimulationObject,
			BaseNode selectedNode, HelpGroup helpGroup) throws DynamoConfigurationException {
		super(plotComposite, SWT.NONE);
		this.plotComposite = plotComposite;
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.selectedNode = selectedNode;
		log.debug("scenarioFactorSelectionGroup::this.plotComposite: " + plotComposite);
		group = new Group(plotComposite, SWT.FILL);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = -3;
		group.setLayout(gridLayout);	
		group.setBackground(new Color(null, 0xee, 0xee,0xee)); // ???		
		log.debug("scenarioFactorSelectionGroup" + group);
		
		createDropDownArea();
	}

	private void createDropDownArea() throws DynamoConfigurationException {
				
		// Follow the reading order (columns first)
		Label nameLabel = new Label(this, SWT.NONE);
		// Get the name value from the file node (i.e. parent)
		nameLabel.setText(NAME + ":");
		Label nameStringLabel = new Label(this, SWT.NONE);
		GridData ld = new GridData();
		ld.horizontalSpan = 3;
		nameStringLabel.setLayoutData(ld);
		// Get the name value from the file node (i.e. parent)
		////String[] entityArray = Util
			////	.deriveEntityLabelAndValueFromRiskSourceNode(selectedNode);
		////nameStringLabel.setText(entityArray[1]);
		String labelValue = SUCCESS_RATE;
		////WritableValue observable = dynamoSimulationObject
			////	.getObservableSimPopSize();
		////TODO REACTIVATE bindHeaderValue(observable, labelValue + PERCENTAGE + SEMICOLON, new SuccessRate());
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, -5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(44, 0);
		group.setLayoutData(formData);					
		
		Label label = new Label(group, SWT.LEFT);
		label.setText(TARGET_OF_INTERVENTION);
		Label emptyLabel = new Label(group, SWT.RIGHT);
		label.setText("");
		
		// TODO: Replace with real content
		Map ageMap = new LinkedHashMap();
		for  (int age = 0; age < 96; age++) {
			String showAge = age + "";
			ageMap.put(showAge, showAge);	
		}

		GenericDropDownPanel minAgeDropDownPanel = 
			createDropDown(MIN_AGE, ageMap);
		this.dropDownModifyListener =
			minAgeDropDownPanel.getGenericComboModifyListener();		
		
		// TODO: Replace with real content
		GenericDropDownPanel maxAgeDropDownPanel = 
			createDropDown(MAX_AGE, ageMap);
		this.dropDownModifyListener =
			maxAgeDropDownPanel.getGenericComboModifyListener();

		Map genderMap = new LinkedHashMap();
		ageMap.put("Male", "Male");
		ageMap.put("Female", "Female");
		ageMap.put("Male and Female", "Male and Female");
		
		// TODO: Replace with real content
		GenericDropDownPanel genderDropDownPanel = 
			createDropDown(GENDER, genderMap);
		this.dropDownModifyListener =
			genderDropDownPanel.getGenericComboModifyListener();
	}

	private GenericDropDownPanel createDropDown(String label, Map selectablePropertiesMap) {
		ScenarioFactorDataAction updateScenarioFactorDataAction = 
			new ScenarioFactorDataAction();
		return new GenericDropDownPanel(group, label,
				selectablePropertiesMap, updateScenarioFactorDataAction);		
	}
	
	public GenericComboModifyListener getDropDownModifyListener() {
		// TODO Replace with getXXXXDropDownPanel to ask for the corresponding listener (not directly!!!)
		return this.dropDownModifyListener;
	}
}
