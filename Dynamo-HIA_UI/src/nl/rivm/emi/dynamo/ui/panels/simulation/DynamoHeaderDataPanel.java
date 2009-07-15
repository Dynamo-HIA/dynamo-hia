package nl.rivm.emi.dynamo.ui.panels.simulation;

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
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.TypedFocusListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractFileNameVerifyListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractRangedIntegerVerifyListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractStringVerifyListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.AbstractValueVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.listeners.PopulationFileNameComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.listeners.UnitTypeComboModifyListener;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Defines the header of the simulation panel
 * 
 * @author schutb
 * 
 */
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
	private PopulationFileNameComboModifyListener dropDownModifyListener;
	private DynamoTabDataManager dynamoTabDataManager;

	/** Two radiobuttons */
	private final Button[] radioButtons = new Button[2];

	public DynamoHeaderDataPanel(Composite parent, Composite bottomNeighbour,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup, DynamoTabDataManager dynamoTabDataManager)
			throws ConfigurationException {
		super(parent, SWT.NONE);
		// this.setBackground(new Color(null, 0xff, 0xff, 0xff));
		this.myParent = parent;
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		this.dynamoTabDataManager = dynamoTabDataManager;
		try {
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

			TreeAsDropdownLists treeLists = TreeAsDropdownLists
					.getInstance(selectedNode);
			DropDownPropertiesSet contentsSet = new DropDownPropertiesSet();
			contentsSet.addAll(treeLists.getPopulations());
			log.debug("contentsSet" + contentsSet);

			Label label = new Label(this, SWT.NONE);
			label.setText(POP_FILE_NAME + ":");

			WritableValue observablePopFileName = dynamoSimulationObject
					.getObservablePopulationFileName();
			PopFileNameDropDownPanel populationFileNameDropDownPanel;

			try {
				populationFileNameDropDownPanel = new PopFileNameDropDownPanel(
						this, observablePopFileName, dynamoTabDataManager
								.getDropDownSet(POP_FILE_NAME, null), helpGroup);

				this.dropDownModifyListener = populationFileNameDropDownPanel
						.getPopulationFileNameComboModifyListener();

			} catch (DynamoNoValidDataException e) {
				throw new ConfigurationException(e.getMessage());
			}

			String labelValue = SIM_POP_SIZE;
			WritableValue observable = dynamoSimulationObject
					.getObservableSimPopSize();
			bindHeaderValue(observable, labelValue, new SimPopSize());

			labelValue = HAS_NEW_BORNS;
			observable = dynamoSimulationObject.getObservableHasNewborns();
			bindHeaderValue(observable, labelValue, new HasNewborns());

			Label spaceLabel = new Label(this, SWT.NONE);
			GridData spaceLabelData = new GridData();
			spaceLabelData.horizontalSpan = 2;
			spaceLabel.setLayoutData(spaceLabelData);

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
			// bindHeaderValue(observable, labelValue, new TimeStep());
			calcTimeStepDummy(observable, labelValue, new TimeStep());

			labelValue = RAND_SEED;
			observable = dynamoSimulationObject.getObservableRandomSeed();
			bindHeaderValue(observable, labelValue, new RandomSeed());
		} catch (NoMoreDataException e) {
			Shell messageShell = new Shell(parent.getDisplay());
			MessageBox messageBox = new MessageBox(messageShell, SWT.OK);
			messageBox.setMessage("no valid population data"
					+ "/n no configuration can be made");

			if (messageBox.open() == SWT.OK) {
				messageShell.dispose();
			}

			messageShell.open();

		}
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

	private void calcTimeStepDummy(WritableValue observable, String labelValue,
			AtomicTypeBase myType) {
		Label label = new Label(this, SWT.NONE);
		label.setText(labelValue + ": ");
		Label valueLabel = new Label(this, SWT.NONE);
		valueLabel.setText(" 1 ");
	}

	protected void bindAbstractRangedInteger(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = getTextBinding(observableObject, myType);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(layoutData);
		text.addVerifyListener(new AbstractRangedIntegerVerifyListener(
				theHelpGroup.getTheModal(), myType));
	}

	protected void bindAbstractValue(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = getTextBinding(observableObject, myType);
		text.addVerifyListener(new AbstractValueVerifyListener(theHelpGroup
				.getTheModal(), myType));
	}

	// Binds values that are subclass types of AbstractString
	protected void bindAbstractString(WritableValue observableObject,
			AtomicTypeBase myType) {
		Text text = getTextBinding(observableObject, myType);
		text.addVerifyListener(new AbstractStringVerifyListener(theHelpGroup
				.getTheModal(), myType));
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
		text.addVerifyListener(new AbstractFileNameVerifyListener(theHelpGroup
				.getTheModal(), myType));
	}

	// Year is already covered by AbstractRangedInteger

	// Age is already covered by AbstractRangedInteger

	private Text createAndPlaceTextField() {
		// Text text = new Text(this, SWT.NONE);
		final Text text = new Text(this, SWT.SINGLE | SWT.FILL | SWT.BORDER);
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
		HelpTextListenerUtil.addHelpTextListeners(text, myType);
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		dataBindingContext.bindValue(textObservableValue, observableObject,
				myType.getModelUpdateValueStrategy(), myType
						.getViewUpdateValueStrategy());
		return text;
	}

	private void getBooleanBinding(WritableValue observableObject,
			AtomicTypeBase myType) {
		Composite radioButtonsContainer = new Composite(this, SWT.NONE);
		radioButtonsContainer.setBackground(new Color(null, 0xcc, 0xcc, 0xcc));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		radioButtonsContainer.setLayoutData(gridData);
		FormLayout rbContainerLayout = new FormLayout();
		radioButtonsContainer.setLayout(rbContainerLayout);

		// Set the selection
		Object value = observableObject.doGetValue();
		log.debug("Got value from observableObject, type: "
				+ value.getClass().getName() + " value: " + value);
		Boolean initialValue = (Boolean) observableObject.doGetValue();
		// Create the radio buttons
		this
				.getPutAndRigBinaryRadioButtons(radioButtonsContainer,
						initialValue);

		// Add the helpgroups
		// FocusListener focusListener = new TypedFocusListener(myType,
		// theHelpGroup);
		// radioButtons[0].addFocusListener(
		// // new FocusListener() {
		// // public void focusGained(FocusEvent arg0) {
		// // theHelpGroup.getFieldHelpGroup().setHelpText("1");
		// // }
		// //
		// // public void focusLost(FocusEvent arg0) {
		// // theHelpGroup.getFieldHelpGroup().setHelpText("48"); // Out of
		// // // range.
		// // }
		// // }
		// focusListener);
		HelpTextListenerUtil.addHelpTextListeners(radioButtons[0], myType);
		// FocusListener focusListener2 = new TypedFocusListener(myType,
		// theHelpGroup);
		// radioButtons[1].addFocusListener(
		// // new FocusListener() {
		// // public void focusGained(FocusEvent arg0) {
		// // theHelpGroup.getFieldHelpGroup().setHelpText("1");
		// // }
		// //
		// // public void focusLost(FocusEvent arg0) {
		// // theHelpGroup.getFieldHelpGroup().setHelpText("48"); // Out of
		// // // range.
		// // }
		// // }
		// focusListener2);
		HelpTextListenerUtil.addHelpTextListeners(radioButtons[1], myType);
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
		radioButtonsContainer.update();
	}

	public void getPutAndRigBinaryRadioButtons(Composite radioButtonsContainer,
			Boolean initialValue) {
		radioButtons[0] = new Button(radioButtonsContainer, SWT.RADIO);
		radioButtons[0].setText("Yes");
		radioButtons[0].setSelection(initialValue);
		FormData button0LayoutData = new FormData();
		button0LayoutData.left = new FormAttachment(0, 2);
		button0LayoutData.right = new FormAttachment(50, -2);
		radioButtons[0].setLayoutData(button0LayoutData);
		radioButtons[0].addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0) {
				Button myWidget = (Button) arg0.widget;
				if (myWidget.getSelection()) {
					// radioButtons[0].setSelection(true);
					radioButtons[1].setSelection(false);
				}
			}
		});

		radioButtons[1] = new Button(radioButtonsContainer, SWT.RADIO);
		radioButtons[1].setText("No");
		radioButtons[1].setSelection(!initialValue);
		FormData button1LayoutData = new FormData();
		button1LayoutData.left = new FormAttachment(50, 2);
		button1LayoutData.right = new FormAttachment(100, -2);
		radioButtons[1].setLayoutData(button1LayoutData);
		radioButtons[1].addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event arg0) {
				Button myWidget = (Button) arg0.widget;
				if (myWidget.getSelection()) {
					radioButtons[0].setSelection(false);
					// radioButtons[1].setSelection(true);
				}
			}
		});
	}

	private GenericDropDownPanel createDropDown(String label,
			DropDownPropertiesSet selectablePropertiesSet, int columnSpan,
			DynamoTabDataManager dynamoTabDataManager)
			throws ConfigurationException {
		ScenarioFactorDataAction updateScenarioFactorDataAction = new ScenarioFactorDataAction();
		return new GenericDropDownPanel(this, label, columnSpan,
				selectablePropertiesSet, null, dynamoTabDataManager);
	}

	public class PopFileNameDropDownPanel {

		Log log = LogFactory.getLog(this.getClass().getName());

		private Combo dropDown;
		private HelpGroup theHelpGroup;
		private DropDownPropertiesSet selectablePopulationFileNamePropertiesSet;
		private PopulationFileNameComboModifyListener populationFileNameModifyListener;
		private int selectedIndex;

		public PopFileNameDropDownPanel(Composite parent,
				WritableValue writableValue, DropDownPropertiesSet theSet,
				HelpGroup theHelpGroup) {
			selectablePopulationFileNamePropertiesSet = theSet;
			this.theHelpGroup = DynamoHeaderDataPanel.this.theHelpGroup;
			dropDown = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
			// dropDown.addFocusListener(new TypedFocusListener(
			// XMLTagEntityEnum.POPFILENAME.getTheType(), theHelpGroup));
			HelpTextListenerUtil.addHelpTextListeners(dropDown,
					(AtomicTypeBase<?>) XMLTagEntityEnum.POPFILENAME.getTheType());
			GridData dropDownGridData = new GridData(GridData.FILL_HORIZONTAL);
			dropDown.setLayoutData(dropDownGridData);
			this.fill(selectablePopulationFileNamePropertiesSet);
			int initialIndex = 0;
			if (writableValue != null) {
				String initialValue = (String) writableValue.doGetValue();
				if (selectablePopulationFileNamePropertiesSet
						.contains(initialValue)) {
					initialIndex = selectablePopulationFileNamePropertiesSet
							.getSelectedIndex(initialValue);
				}
			}
			this.populationFileNameModifyListener = new PopulationFileNameComboModifyListener(
					writableValue);
			dropDown.addModifyListener(populationFileNameModifyListener);
			dropDown.select(initialIndex);
		}

		public void fill(DropDownPropertiesSet set) {
			int index = 0;
			for (String item : set) {
				dropDown.add(item, index);
				index++;
			}
		}

		public String getUnitType() {
			return (String) selectablePopulationFileNamePropertiesSet
					.getSelectedString(this.selectedIndex);
		}

		public void setHelpGroup(HelpGroup helpGroup) {
			theHelpGroup = helpGroup;
		}

		/**
		 * 
		 * Place the first group in the container
		 * 
		 * @param height
		 */

		public PopulationFileNameComboModifyListener getPopulationFileNameComboModifyListener() {
			return populationFileNameModifyListener;
		}

		public Combo getDropDown() {
			return dropDown;
		}

		private void layoutDropDown(Label label) {
			FormData comboFormData = new FormData();
			comboFormData.left = new FormAttachment(label, 5);
			comboFormData.right = new FormAttachment(100, -5);
			comboFormData.top = new FormAttachment(0, 2);
			comboFormData.bottom = new FormAttachment(100, -2);
			dropDown.setLayoutData(comboFormData);
		}
	}

}
