package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.HasNewborns;
import nl.rivm.emi.dynamo.data.types.atomic.MaxAge;
import nl.rivm.emi.dynamo.data.types.atomic.MinAge;
import nl.rivm.emi.dynamo.data.types.atomic.NumberOfYears;
import nl.rivm.emi.dynamo.data.types.atomic.RandomSeed;
import nl.rivm.emi.dynamo.data.types.atomic.SimPopSize;
import nl.rivm.emi.dynamo.data.types.atomic.StartingYear;
import nl.rivm.emi.dynamo.data.types.atomic.TimeStep;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractBoolean;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractFileName;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractFileNameVerifyListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractRangedIntegerVerifyListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractStringVerifyListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractValueVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.Util;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

public class DynamoHeaderDataPanel extends Composite {
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static final String NAME = "Name";
	private static final String SIM_POP_SIZE = "Simulated population size";
	private static final String RAND_SEED = "Random seed";
	private static final String HAS_NEW_BORNS = "Newborns";
	public static final String POP_FILE_NAME = "Population";
	private static final String STARTING_YEAR = "Starting year";
	private static final String NUMBER_OF_YEARS = "Number of years";
	private static final String MINIMUM_AGE = "Minimum age";
	private static final String MAXIMUM_AGE = "Maximum age";
	private static final String CALC_TIME_STEP = "Calculation time step";	

	protected DynamoSimulationObject dynamoSimulationObject;
	private Composite myParent = null;
	private boolean open = false;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup theHelpGroup;
	private GenericComboModifyListener dropDownModifyListener;
	private DynamoTabDataManager dynamoTabDataManager;
	
	/** Two radiobuttons */
	private Button[] radioButtons = new Button[2];

	public DynamoHeaderDataPanel(Composite parent, Composite bottomNeighbour,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup,
			DynamoTabDataManager dynamoTabDataManager
	) throws ConfigurationException {
		super(parent, SWT.NONE);
//		this.setBackground(new Color(null, 0xff, 0xff, 0xff));
		this.myParent = parent;
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		this.dynamoTabDataManager = dynamoTabDataManager;		
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.makeColumnsEqualWidth = /* false */true;
		setLayout(layout);

		// Follow the reading order (columns first)
		Label nameLabel = new Label(this, SWT.NONE);
		// Get the name value from the file node (i.e. parent)
		nameLabel.setText(NAME + ":");
		Label nameStringLabel = new Label(this, SWT.NONE);
		GridData ld = new GridData();
		ld.horizontalSpan = 3;
		nameStringLabel.setLayoutData(ld);
		// Get the name value from the file node (i.e. parent)
		String[] entityArray = Util
				.deriveEntityLabelAndValueFromRiskSourceNode(selectedNode);
		nameStringLabel.setText(entityArray[1]);
		String labelValue = SIM_POP_SIZE;
		WritableValue observable = dynamoSimulationObject
				.getObservableSimPopSize();
		bindHeaderValue(observable, labelValue, new SimPopSize());
		labelValue = RAND_SEED;
		observable = dynamoSimulationObject.getObservableRandomSeed();
		bindHeaderValue(observable, labelValue, new RandomSeed());

		labelValue = HAS_NEW_BORNS;
		// TODO: Create radioButtons here
		observable = dynamoSimulationObject.getObservableHasNewborns();
		bindHeaderValue(observable, labelValue, new HasNewborns());

		
		
		/*
		labelValue = POP_FILE_NAME;
		observable = dynamoSimulationObject.getObservablePopulationFileName();
		bindHeaderValue(observable, labelValue, new PopFileName());
		Label fillLabel = new Label(this, SWT.NONE);
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		fillLabel.setLayoutData(layoutData);*/
		
		
		//String populationFileName = dynamoSimulationObject.getObservablePopulationFileName().doGetValue().toString();
		TreeAsDropdownLists treeLists = TreeAsDropdownLists.getInstance(selectedNode); 
		DropDownPropertiesSet contentsSet = new DropDownPropertiesSet();
		contentsSet.addAll(treeLists.getPopulations());		
		log.debug("contentsSet" + contentsSet);
		
		//WritableValue writeble = (WritableValue) dynamoSimulationObject.getObservablePopulationFileName();
		
		
		GenericDropDownPanel populationDropDownPanel = 
			createDropDown(POP_FILE_NAME, 
					dynamoTabDataManager.getDropDownSet(POP_FILE_NAME, null), 1, 
					dynamoTabDataManager);
		this.dropDownModifyListener =
			populationDropDownPanel.getGenericComboModifyListener();
				
		labelValue = STARTING_YEAR;
		observable = dynamoSimulationObject.getObservableStartingYear();
		bindHeaderValue(observable, labelValue, new StartingYear());

		labelValue = NUMBER_OF_YEARS;
		observable = dynamoSimulationObject.getObservableNumberOfYears();
		bindHeaderValue(observable, labelValue, new NumberOfYears());

		labelValue = MINIMUM_AGE;
		observable = dynamoSimulationObject.getObservableMinAge();
		bindHeaderValue(observable, labelValue, new MinAge());

		labelValue = MAXIMUM_AGE;
		observable = dynamoSimulationObject.getObservableMaxAge();
		bindHeaderValue(observable, labelValue, new MaxAge());

		labelValue = CALC_TIME_STEP;
		observable = dynamoSimulationObject.getObservableTimeStep();
		bindHeaderValue(observable, labelValue, new TimeStep());
	}

	private void bindHeaderValue(WritableValue observable, String labelValue,
			AtomicTypeBase myType) {
		if (observable != null) {
			Label label = new Label(this, SWT.NONE);
			label.setText(labelValue + ": ");
			bindValue(observable, myType);
		} else {
			MessageBox box = new MessageBox(this.myParent.getShell());
			box.setText("Class name error");
			box.setMessage("Value for " + labelValue + " should not be empty.");
			box.open();
		}
	}

	protected void bindValue(WritableValue observable, AtomicTypeBase myType) {
		if (myType instanceof AbstractRangedInteger) {
			bindAbstractRangedInteger(observable, myType);
		} else if (myType instanceof AbstractValue) {
			bindAbstractValue(observable, myType);
		} else if (myType instanceof AbstractString) {
			bindAbstractString(observable, myType);
		} else if (myType instanceof AbstractBoolean) {
			bindAbstractBoolean(observable, myType);
		} else if (myType instanceof AbstractFileName) {
			bindAbstractFileName(observable, myType);
		}
	}

	protected void bindAbstractRangedInteger(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = getTextBinding(observableObject, myType);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(layoutData);
		text.addVerifyListener(new AbstractRangedIntegerVerifyListener(myType));
	}

	protected void bindAbstractValue(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = getTextBinding(observableObject, myType);
		text.addVerifyListener(new AbstractValueVerifyListener(myType));
	}

	// Binds values that are subclass types of AbstractString
	protected void bindAbstractString(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = getTextBinding(observableObject, myType);
		text.addVerifyListener(new AbstractStringVerifyListener(myType));
	}

	// Binds values that are subclass types of AbstractBoolean
	protected void bindAbstractBoolean(WritableValue observableObject,
			AtomicTypeBase myType) {
		this.getBooleanBinding(observableObject, myType);
	}

	// FileName
	// Binds values that are subclass types of AbstractString
	protected void bindAbstractFileName(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = getTextBinding(observableObject, myType);
		text.addVerifyListener(new AbstractFileNameVerifyListener(myType));
	}

	// Year is already covered by AbstractRangedInteger

	// Age is already covered by AbstractRangedInteger

	private Text createAndPlaceTextField() {
//		Text text = new Text(this, SWT.NONE);
		final Text text = new Text(this, SWT.SINGLE|SWT.FILL|SWT.BORDER);
			// Eerst ff zonder.
		 GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		 text.setLayoutData(gridData);
		return text;
	}

	private Text getTextBinding(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = createAndPlaceTextField();
		text.setText((String) myType
				.convert4View(observableObject.doGetValue()));
		text.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(1);
			}

			public void focusLost(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(48); // Out of
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

	public void getCreateRadioButtonsBinding() {
		// this.radioButtons[0] = new Button(this.myParent, SWT.RADIO);
		this.radioButtons[0] = new Button(this, SWT.RADIO);
		this.radioButtons[0].addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event arg0) {
				Button myWidget = (Button) arg0.widget;
				if (myWidget.getSelection()) {
					// radioButtons[0].setSelection(true);
					radioButtons[1].setSelection(false);
				}
			}
		});

		radioButtons[1] = new Button(this, SWT.RADIO);
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		radioButtons[1].setLayoutData(layoutData);
		radioButtons[1].addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event arg0) {
				Button myWidget = (Button) arg0.widget;
				if (myWidget.getSelection()) {
					radioButtons[0].setSelection(false);
					// radioButtons[1].setSelection(true);
				}
			}
		});
		// Default.
		radioButtons[0].setSelection(true);
	}

	private void getBooleanBinding(WritableValue observableObject,
			AtomicTypeBase myType) {

		// Create the radio buttons
		this.getCreateRadioButtonsBinding();

		// Set the selection
		radioButtons[0].setSelection(new Boolean(myType
				.convert4View(observableObject.doGetValue())).booleanValue());
		radioButtons[1].setSelection(!new Boolean(myType
				.convert4View(observableObject.doGetValue())).booleanValue());

		// Add the helpgroups
		radioButtons[0].addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(1);
			}

			public void focusLost(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(48); // Out of
				// range.
			}

		});
		radioButtons[1].addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(1);
			}

			public void focusLost(FocusEvent arg0) {
				theHelpGroup.getFieldHelpGroup().putHelpText(48); // Out of
				// range.
			}

		});
		// Only the value of one radio button (the first one) is reminded
		IObservableValue textObservableValue = SWTObservables
				.observeSelection(this.radioButtons[0]);
		dataBindingContext.bindValue(textObservableValue, observableObject,
		/* KISS first/ myType.getModelUpdateValueStrategy() */null, /*
																	 * KISS
																	 * first
																	 * myType.
																	 * getViewUpdateValueStrategy
																	 * ()
																	 */null);

	}
	
	private GenericDropDownPanel createDropDown(String label, 
			DropDownPropertiesSet selectablePropertiesSet, 
			int columnSpan,
			DynamoTabDataManager dynamoTabDataManager) throws ConfigurationException {
		ScenarioFactorDataAction updateScenarioFactorDataAction = 
			new ScenarioFactorDataAction();
		return new GenericDropDownPanel(this, label, columnSpan,
				selectablePropertiesSet,				
				null, dynamoTabDataManager);
	}

	public GenericComboModifyListener getDropDownModifyListener() {
		return this.dropDownModifyListener;
	}
}
