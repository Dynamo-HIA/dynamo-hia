package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.types.atomic.TimeStep;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractRangedIntegerVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

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
	private HelpGroup helpGroup;
	private DataBindingContext dataBindingContext;

	
	public ScenarioSelectionGroup(Composite plotComposite,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext,
			BaseNode selectedNode, HelpGroup helpGroup) throws DynamoConfigurationException {
		//super(plotComposite, SWT.NONE);
		this.plotComposite = plotComposite;
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.selectedNode = selectedNode;
		this.helpGroup = helpGroup;
		this.dataBindingContext = dataBindingContext;
		
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
				
		String labelValue = SUCCESS_RATE;
		WritableValue observable = dynamoSimulationObject.getObservableTimeStep();
		bindHeaderValue(observable, labelValue, new TimeStep());		
		
		Label successRateStringLabel = new Label(scenarioDefGroup, SWT.NONE);
		GridData successRateLd = new GridData();
		successRateLd.horizontalSpan = 1;
		successRateStringLabel.setLayoutData(successRateLd);
		successRateStringLabel.setText("20");
	
		Label label = new Label(scenarioDefGroup, SWT.LEFT);
		GridData ld = new GridData();
		ld.horizontalSpan = 6;
		ld.verticalIndent = 4;
		label.setText(TARGET_OF_INTERVENTION);
		label.setLayoutData(ld);
		
		// TODO: Replace with real content
		DropDownPropertiesSet ageSet = new DropDownPropertiesSet();
		for  (int age = 0; age < 96; age++) {
			String showAge = age + "";
			ageSet.add(showAge);	
		}

		GenericDropDownPanel minAgeDropDownPanel = 
			createDropDown(MIN_AGE, ageSet, 1, null);
		this.dropDownModifyListener =
			minAgeDropDownPanel.getGenericComboModifyListener();		
		
		// TODO: Replace with real content
		GenericDropDownPanel maxAgeDropDownPanel = 
			createDropDown(MAX_AGE, ageSet, 1, null);
		this.dropDownModifyListener =
			maxAgeDropDownPanel.getGenericComboModifyListener();

		DropDownPropertiesSet genderSet = new DropDownPropertiesSet();
		genderSet.add("Male");
		genderSet.add("Female");
		genderSet.add("Male and Female");
		
		// TODO: Replace with real content
		GenericDropDownPanel genderDropDownPanel = 
			createDropDown(GENDER, genderSet, 1, null);
		this.dropDownModifyListener =
			genderDropDownPanel.getGenericComboModifyListener();
			
	}

	private GenericDropDownPanel createDropDown(String label, 
			DropDownPropertiesSet selectablePropertiesSet, 
			int columnSpan,
			AtomicTypeObjectTuple tuple) {
		ScenarioFactorDataAction updateScenarioFactorDataAction = 
			new ScenarioFactorDataAction();
		return new GenericDropDownPanel(scenarioDefGroup, label, columnSpan,
				selectablePropertiesSet,				
				tuple,
				updateScenarioFactorDataAction);		
	}
	
	public GenericComboModifyListener getDropDownModifyListener() {
		// TODO Replace with getXXXXDropDownPanel to ask for the corresponding listener (not directly!!!)
		return this.dropDownModifyListener;
	}
	
	private void bindHeaderValue(WritableValue observable, String labelValue,
			AtomicTypeBase myType) {
		if (observable != null) {			
			Label successRateLabel = new Label(scenarioDefGroup, SWT.NONE);
			successRateLabel.setText(labelValue + ":");			
			bindValue(observable, myType);
		} else {
			MessageBox box = new MessageBox(this.scenarioDefGroup.getShell());
			box.setText("Class name error");
			box.setMessage("Value for " + labelValue + " should not be empty.");
			box.open();
		}
	}
	
	private void bindValue(WritableValue observable, AtomicTypeBase myType) {
		if (myType instanceof AbstractRangedInteger) {
			bindAbstractRangedInteger(observable, myType);
		}
	}

	protected void bindAbstractRangedInteger(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = getTextBinding(observableObject, myType);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(layoutData);
		text.addVerifyListener(new AbstractRangedIntegerVerifyListener(myType));
	}
	
	private Text getTextBinding(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = createAndPlaceTextField();
		text.setText((String) myType
				.convert4View(observableObject.doGetValue()));
		text.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				helpGroup.getFieldHelpGroup().putHelpText(1);
			}

			public void focusLost(FocusEvent arg0) {
				helpGroup.getFieldHelpGroup().putHelpText(48); // Out of
				// range.
			}
		});
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		dataBindingContext.bindValue(textObservableValue, observableObject,
				myType.getModelUpdateValueStrategy(), myType
						.getViewUpdateValueStrategy());
		return text;
	}
	
	private Text createAndPlaceTextField() {
//		Text text = new Text(this, SWT.NONE);
		final Text text = new Text(scenarioDefGroup, SWT.SINGLE|SWT.FILL|SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gridData);
		return text;
	}
	
}
