package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.types.atomic.SuccessRate;
import nl.rivm.emi.dynamo.data.types.atomic.UniqueName;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractRangedIntegerVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
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

/**
 * 
 * Shows the selection drop downs of the disease tab
 * 
 * @author schutb
 *
 */
public class ScenarioSelectionGroup { //extends Composite {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public static final String NAME = "Name";
	public static final String SUCCESS_RATE = "Success Rate";
	public static final String MIN_AGE = "Min. Age";
	public static final String MAX_AGE = "Max. Age";
	public static final String GENDER = "Gender";

	private static final String TARGET_OF_INTERVENTION = 
		"Target of Intervention";

	
	private static final String SEMICOLON = ":";	
	private static final String PERCENTAGE = "(%)";
	
	protected Group scenarioDefGroup;
	private Composite plotComposite;
	private DynamoSimulationObject dynamoSimulationObject;
	private BaseNode selectedNode;	
	private Set<String> selections;
	private DynamoTabDataManager dynamoTabDataManager;
	private GenericComboModifyListener dropDownModifyListener;
	private GenericDropDownPanel minAgeDropDownPanel;
	private GenericDropDownPanel maxAgeDropDownPanel;
	private GenericDropDownPanel genderDropDownPanel;
	private HelpGroup helpGroup;
	private DataBindingContext dataBindingContext;
	private String tabName;
	
	public ScenarioSelectionGroup(String tabName, Set<String> selections, Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup, 
			DynamoTabDataManager dynamoTabDataManager,
			DataBindingContext dataBindingContext,
			DynamoSimulationObject dynamoSimulationObject) 
			throws ConfigurationException {
		this.selections = selections;
		this.plotComposite = plotComposite;
		this.dynamoTabDataManager = dynamoTabDataManager;
		this.dynamoSimulationObject= dynamoSimulationObject;
		this.selectedNode = selectedNode;
		this.helpGroup = helpGroup;
		this.dataBindingContext = dataBindingContext;
		this.tabName = tabName;
		
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

	private void createDropDownArea() throws ConfigurationException {		
		
		FormData scenarioFormData = new FormData();
		scenarioFormData.top = new FormAttachment(0, -5);
		scenarioFormData.left = new FormAttachment(0, 5);
		scenarioFormData.right = new FormAttachment(100, -5);
		scenarioFormData.bottom = new FormAttachment(53, 0);
		scenarioDefGroup.setLayoutData(scenarioFormData);
			
		// Needed as initialization of existing values
		String chosenScenarioName = null;
		if (this.selections != null) {
			for (String chosenName : this.selections) {
				chosenScenarioName = chosenName;		
			}			
		}		
		if (dynamoTabDataManager.getCurrentWritableValue(NAME) == null) {
			// In case a new tab is created, set the tabName as scenario Name			
			log.debug("this.tabName" + this.tabName);
			dynamoTabDataManager.updateObjectState(NAME, this.tabName);
			chosenScenarioName = this.tabName;
			
			// In case a new tab is created, set the 100 as initial Success Rate
			dynamoTabDataManager.updateObjectState(SUCCESS_RATE, "100");
		}
		String labelValue = NAME;
		WritableValue observable = 
			(WritableValue) dynamoTabDataManager.getCurrentWritableValue(NAME);
		bindHeaderValue(observable, labelValue,  
				new UniqueName(this.dynamoTabDataManager, NAME,
						this.plotComposite.getShell()));
		
		labelValue = SUCCESS_RATE;
		observable = 
			(WritableValue) dynamoTabDataManager.getCurrentWritableValue(SUCCESS_RATE);
		bindHeaderValue(observable, labelValue, new SuccessRate());
		
		Label label = new Label(scenarioDefGroup, SWT.LEFT);
		GridData ld = new GridData();
		ld.horizontalSpan = 6;
		ld.verticalIndent = 4;
		label.setText(TARGET_OF_INTERVENTION);
		label.setLayoutData(ld);
		
		this.minAgeDropDownPanel = 
			createDropDown(MIN_AGE, 
					dynamoTabDataManager.getDropDownSet(MIN_AGE, chosenScenarioName), 1, 
					dynamoTabDataManager);
		this.dropDownModifyListener =
			minAgeDropDownPanel.getGenericComboModifyListener();
		
		this.maxAgeDropDownPanel = 
			createDropDown(MAX_AGE, 
					dynamoTabDataManager.getDropDownSet(MAX_AGE, chosenScenarioName), 1, 
					dynamoTabDataManager);
		this.dropDownModifyListener =
			maxAgeDropDownPanel.getGenericComboModifyListener();
		
		this.genderDropDownPanel = 
			createDropDown(GENDER, 
					dynamoTabDataManager.getDropDownSet(GENDER, chosenScenarioName), 1, 
					dynamoTabDataManager);
		this.dropDownModifyListener =
			genderDropDownPanel.getGenericComboModifyListener();
			
	}

	private GenericDropDownPanel createDropDown(String label, 
			DropDownPropertiesSet selectablePropertiesSet, 
			int columnSpan,
			DynamoTabDataManager dynamoTabDataManager) throws ConfigurationException {
		ScenarioFactorDataAction updateScenarioFactorDataAction = 
			new ScenarioFactorDataAction();
		return new GenericDropDownPanel(scenarioDefGroup, 
				label, columnSpan,
				selectablePropertiesSet,	
				updateScenarioFactorDataAction, dynamoTabDataManager);		
	}
	
	public GenericComboModifyListener getDropDownModifyListener() {
		return this.dropDownModifyListener;
	}
	
	private void bindHeaderValue(WritableValue observable, String labelValue,
			AtomicTypeBase myType) throws ConfigurationException {
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
	
	private void bindValue(WritableValue observable, AtomicTypeBase myType) throws ConfigurationException {
		if (myType instanceof AbstractRangedInteger) {
			bindAbstractRangedInteger(observable, myType);
		} else if (myType instanceof AbstractString) {
			bindAbstractString(observable, myType);
		}		
	}

	protected void bindAbstractRangedInteger(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = getTextBinding(observableObject, myType);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(layoutData);
		text.addVerifyListener(new AbstractRangedIntegerVerifyListener(myType));
	}

	// Binds values that are subclass types of AbstractString
	protected void bindAbstractString(WritableValue observableObject,
			AtomicTypeBase myType) throws ConfigurationException {
		Text text = getTextBinding(observableObject, myType);
		//text.addVerifyListener(new AbstractStringVerifyListener(myType));
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
	
	public void refreshSelectionDropDown() throws ConfigurationException {
		this.minAgeDropDownPanel.refresh();
		this.maxAgeDropDownPanel.refresh();
		this.genderDropDownPanel.refresh();		
	}
		
}
