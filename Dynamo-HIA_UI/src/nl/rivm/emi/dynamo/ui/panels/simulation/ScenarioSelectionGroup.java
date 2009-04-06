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

public class ScenarioSelectionGroup { //extends Composite {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static final String NAME = "Name";
	private static final String MIN_AGE = "Min. Age";
	private static final String MAX_AGE = "Max. Age";
	private static final String GENDER = "Gender";

	private static final String TARGET_OF_INTERVENTION = 
		"Target of Intervention";

	private static final String SUCCESS_RATE = "Success Rate";

	private static final String SEMICOLON = ":";	

	private static final String PERCENTAGE = "(%)";
	
	protected Group scenarioDefGroup;
	private Composite plotComposite;
	private DynamoSimulationObject dynamoSimulationObject;
	private BaseNode selectedNode;	
	private GenericComboModifyListener dropDownModifyListener;



	public ScenarioSelectionGroup(Composite plotComposite,
			DynamoSimulationObject dynamoSimulationObject,
			BaseNode selectedNode, HelpGroup helpGroup) throws DynamoConfigurationException {
		//super(plotComposite, SWT.NONE);
		this.plotComposite = plotComposite;
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.selectedNode = selectedNode;
		
		log.debug("scenarioFactorSelectionGroup::this.plotComposite: " + plotComposite);
		scenarioDefGroup = new Group(plotComposite, SWT.FILL);		
		GridLayout scenarioGridLayout = new GridLayout();
		scenarioGridLayout.makeColumnsEqualWidth = true;
		scenarioGridLayout.numColumns = 6;
		scenarioGridLayout.marginHeight = -3;
		scenarioDefGroup.setLayout(scenarioGridLayout);	
		//scenarioDefGroup.setBackground(new Color(null, 0xee, 0xee,0xee)); // ???		
		log.debug("scenarioFactorSelectionGroup" + scenarioDefGroup);
		
		createDropDownArea();
	}

	private void createDropDownArea() throws DynamoConfigurationException {		
		
		FormData scenarioFormData = new FormData();
		scenarioFormData.top = new FormAttachment(0, -5);
		scenarioFormData.left = new FormAttachment(0, 5);
		scenarioFormData.right = new FormAttachment(100, -5);
		scenarioFormData.bottom = new FormAttachment(53, 0);
		scenarioDefGroup.setLayoutData(scenarioFormData);
		
		// Follow the reading order (columns first)
		Label nameLabel = new Label(scenarioDefGroup, SWT.NONE);
		// Get the name value from the file node (i.e. parent)
		nameLabel.setText(NAME + ":");
		Label nameStringLabel = new Label(scenarioDefGroup, SWT.NONE);
		GridData nameLd = new GridData();
		nameLd.horizontalSpan = 3;
		nameStringLabel.setLayoutData(nameLd);
		// Get the name value from the file node (i.e. parent)
		////String[] entityArray = Util
			////	.deriveEntityLabelAndValueFromRiskSourceNode(selectedNode);
		nameStringLabel.setText("BMI scen1");
		
		Label successRateLabel = new Label(scenarioDefGroup, SWT.NONE);
		successRateLabel.setText(SUCCESS_RATE + ":");
		////WritableValue observable = dynamoSimulationObject
			////	.getObservableSimPopSize();
		////TODO REACTIVATE bindHeaderValue(observable, labelValue + PERCENTAGE + SEMICOLON, new SuccessRate());
		Label successRateStringLabel = new Label(scenarioDefGroup, SWT.NONE);
		GridData successRateLd = new GridData();
		successRateLd.horizontalSpan = 1;
		successRateStringLabel.setLayoutData(successRateLd);
		successRateStringLabel.setText("20");

		/*
		log.debug("scenarioFactorSelectionGroup::this.plotComposite: " + plotComposite);
		group = new Group(plotComposite, SWT.FILL);		
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 6;
		gridLayout.marginHeight = -13;
		group.setLayout(gridLayout);	
		group.setBackground(new Color(null, 0xff, 0xff,0xff)); // ???		
		log.debug("scenarioFactorSelectionGroup" + group);
				
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(scenarioDefGroup, -5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(50, 0);
		group.setLayoutData(formData);		
	*/
	
		Label label = new Label(scenarioDefGroup, SWT.LEFT);
		GridData ld = new GridData();
		ld.horizontalSpan = 6;
		ld.verticalIndent = 4;
		label.setText(TARGET_OF_INTERVENTION);
		label.setLayoutData(ld);
		
		// TODO: Replace with real content
		Map ageMap = new LinkedHashMap();
		for  (int age = 0; age < 96; age++) {
			String showAge = age + "";
			ageMap.put(showAge, showAge);	
		}

		GenericDropDownPanel minAgeDropDownPanel = 
			createDropDown(MIN_AGE, ageMap, 1);
		this.dropDownModifyListener =
			minAgeDropDownPanel.getGenericComboModifyListener();		
		
		// TODO: Replace with real content
		GenericDropDownPanel maxAgeDropDownPanel = 
			createDropDown(MAX_AGE, ageMap, 1);
		this.dropDownModifyListener =
			maxAgeDropDownPanel.getGenericComboModifyListener();

		Map genderMap = new LinkedHashMap();
		genderMap.put("Male", "Male");
		genderMap.put("Female", "Female");
		genderMap.put("Male and Female", "Male and Female");
		
		// TODO: Replace with real content
		GenericDropDownPanel genderDropDownPanel = 
			createDropDown(GENDER, genderMap, 1);
		this.dropDownModifyListener =
			genderDropDownPanel.getGenericComboModifyListener();
			
	}

	private GenericDropDownPanel createDropDown(String label, Map selectablePropertiesMap, int columnSpan) {
		ScenarioFactorDataAction updateScenarioFactorDataAction = 
			new ScenarioFactorDataAction();
		return new GenericDropDownPanel(scenarioDefGroup, label, columnSpan,
				selectablePropertiesMap, updateScenarioFactorDataAction);		
	}
	
	public GenericComboModifyListener getDropDownModifyListener() {
		// TODO Replace with getXXXXDropDownPanel to ask for the corresponding listener (not directly!!!)
		return this.dropDownModifyListener;
	}
}
