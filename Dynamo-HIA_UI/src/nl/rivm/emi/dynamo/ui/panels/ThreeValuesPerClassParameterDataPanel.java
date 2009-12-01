package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Value;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.TypedFocusListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ThreeValuesPerClassParameterDataPanel extends Composite /*
																	 * implements
																	 * Runnable
																	 */{
	Log log = LogFactory.getLog(this.getClass().getName());
	TypedHashMap lotsOfData;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase myType;
	int durationClassIndex;

	public ThreeValuesPerClassParameterDataPanel(Composite parent,
			Text topNeighbour, TypedHashMap lotsOfData,
			DataBindingContext dataBindingContext, HelpGroup helpGroup,
			int durationClassIndex) {
		super(parent, SWT.NONE);
		log.debug("Constructing ThreeValuesPerClassParameterDataPanel");
		this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		myType = (AtomicTypeBase) XMLTagEntitySingleton.getInstance().get(
				"value");
		this.durationClassIndex = durationClassIndex;
		GridLayout layout = new GridLayout();
		layout.numColumns = 8;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);

		// Top line.
		createHeader();
		for (int ageCount = 0; ageCount < lotsOfData.size(); ageCount++) {
			TypedHashMap oneAgeMap = (TypedHashMap) lotsOfData.get(ageCount);
			TypedHashMap femaleClassHMap = (TypedHashMap) oneAgeMap
					.get(BiGender.FEMALE_INDEX);
			TypedHashMap maleClassHMap = (TypedHashMap) oneAgeMap
					.get(BiGender.MALE_INDEX);
			log.debug("Aantal categorie\u00ebn: " + femaleClassHMap.size());
			for (int classCount = 1; classCount <= femaleClassHMap.size(); classCount++) {
				Label ageCellLabel = new Label(this, SWT.NONE);
				if (classCount == 1) {
					ageCellLabel.setText(new Integer(ageCount).toString());
				}
				boolean isDurationClass = (classCount == durationClassIndex);
				Label classCellLabel = new Label(this, SWT.NONE);
				classCellLabel.setText(new Integer(classCount).toString());
				ArrayList<AtomicTypeObjectTuple> maleList = (ArrayList<AtomicTypeObjectTuple>) maleClassHMap
						.get(classCount);
				bindGenderValues(maleList, isDurationClass);
				ArrayList<AtomicTypeObjectTuple> femaleList = (ArrayList<AtomicTypeObjectTuple>) femaleClassHMap
				.get(classCount);
		        bindGenderValues(femaleList, isDurationClass);

			}
		}
	}

	private void bindGenderValues(ArrayList<AtomicTypeObjectTuple> tupleList,
			boolean isDurationClass) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		for (int tupleIndex = 0; tupleIndex < tupleList.size(); tupleIndex++) {
			if (isDurationClass || (tupleIndex == 0)) {
				AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) tupleList
						.get(tupleIndex);
				bindTuple(gridData, tuple);
			} else {
				Label spaceLabel = new Label(this, SWT.NONE);
			}
		}
	}

	private void bindTuple(GridData gridData, AtomicTypeObjectTuple tuple) {
		WritableValue modelObservableValue = (WritableValue) tuple.getValue();
		AtomicTypeBase modelType = (AtomicTypeBase) tuple.getType();
		Text text = new Text(this, SWT.NONE);
		text.setLayoutData(gridData);
		String convertedText = modelType.convert4View(modelObservableValue
				.getValue());
		text.setText(convertedText);
		// FocusListener focusListener = new
		// TypedFocusListener(modelType,theHelpGroup);
		// text.addFocusListener(
		// // new FocusListener() {
		// // public void focusGained(FocusEvent arg0) {
		// // theHelpGroup.getFieldHelpGroup().setHelpText("1");
		// // }
		// //
		// // public void focusLost(FocusEvent arg0) {
		// // theHelpGroup.getFieldHelpGroup().setHelpText("48"); // Out of
		// // // range.
		// // }
		// //
		// // }
		// focusListener);
		HelpTextListenerUtil.addHelpTextListeners(text, modelType);
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				modelType.getModelUpdateValueStrategy(), modelType
						.getViewUpdateValueStrategy());
		// text.addVerifyListener(new ValueVerifyListener());
	}

	private void createHeader() {
		GridData labelLayoutData = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		GridData dataLayoutData = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		dataLayoutData.widthHint = 50;

		Label spaceLabel_1_1 = new Label(this, SWT.NONE);
		spaceLabel_1_1.setLayoutData(labelLayoutData);
		Label spaceLabel_1_2 = new Label(this, SWT.NONE);
		spaceLabel_1_2.setLayoutData(labelLayoutData);
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
		maleLabel.setLayoutData(dataLayoutData);
		Label spaceLabel_1_4 = new Label(this, SWT.NONE);
		spaceLabel_1_4.setLayoutData(dataLayoutData);
		Label spaceLabel_1_5 = new Label(this, SWT.NONE);
		spaceLabel_1_5.setLayoutData(dataLayoutData);
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		femaleLabel.setLayoutData(dataLayoutData);
		Label spaceLabel_1_7 = new Label(this, SWT.NONE);
		spaceLabel_1_7.setLayoutData(dataLayoutData);
		Label spaceLabel_1_8 = new Label(this, SWT.NONE);
		spaceLabel_1_8.setLayoutData(dataLayoutData);
		// Just above the values.
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label classLabel = new Label(this, SWT.NONE);
		classLabel.setText("Class");
		Label femaleBeginLabel = new Label(this, SWT.NONE);
		femaleBeginLabel.setText("Begin");
		Label femaleAlphaLabel = new Label(this, SWT.NONE);
		femaleAlphaLabel.setText("Alpha");
		Label femaleEndLabel = new Label(this, SWT.NONE);
		femaleEndLabel.setText("End");
		Label maleBeginLabel = new Label(this, SWT.NONE);
		maleBeginLabel.setText("Begin");
		Label maleAlphaLabel = new Label(this, SWT.NONE);
		maleAlphaLabel.setText("Alpha");
		Label maleEndLabel = new Label(this, SWT.NONE);
		maleEndLabel.setText("End");
	}
}
