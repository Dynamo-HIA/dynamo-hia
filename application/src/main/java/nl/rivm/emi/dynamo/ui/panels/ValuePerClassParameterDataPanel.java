package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Percent;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.verify.PercentVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ValuePerClassParameterDataPanel extends Composite {
	Log log = LogFactory.getLog(this.getClass().getName());
	TypedHashMap lotsOfData;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase myType;
	int ageCount;
	int genderCount;

	public ValuePerClassParameterDataPanel(Composite parent, Text topNeighbour,
			TypedHashMap lotsOfData, DataBindingContext dataBindingContext,
			HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		myType = (AtomicTypeBase) XMLTagEntitySingleton.getInstance().get(
				"percent");
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		GridData labelLayoutData = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		GridData dataLayoutData = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		dataLayoutData.widthHint = 50;

		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		ageLabel.setLayoutData(labelLayoutData);
		Label classLabel = new Label(this, SWT.NONE);
		classLabel.setText("Class");
		classLabel.setLayoutData(labelLayoutData);
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
		maleLabel.setLayoutData(dataLayoutData);
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		femaleLabel.setLayoutData(dataLayoutData);
		log.fatal("start loop");
		// Made ageCount Object scope for debugging;
		for (ageCount = 0; ageCount < lotsOfData.size(); ageCount++) {
			TypedHashMap oneAgeMap = (TypedHashMap) lotsOfData.get(ageCount);
			TypedHashMap femaleClassHMap = (TypedHashMap) oneAgeMap
					.get(BiGender.FEMALE_INDEX);
			TypedHashMap maleClassHMap = (TypedHashMap) oneAgeMap
					.get(BiGender.MALE_INDEX);
			for (int classCount = 1; classCount <= femaleClassHMap.size(); classCount++) {
				// log.debug("Going to bind fields for age: " + ageCount
				// + " and category: " + classCount);
				Label ageCellLabel = new Label(this, SWT.NONE);
				if (classCount == 1) {
					ageCellLabel.setText(new Integer(ageCount).toString());
				}
				Label classCellLabel = new Label(this, SWT.NONE);
				classCellLabel.setText(new Integer(classCount).toString());
				genderCount = BiGender.MALE_INDEX;
				log.fatal("agecount: "+ ageCount + " classCount: "+ classCount + " voor bindValue");
				bindValue(maleClassHMap, classCount);
				log.fatal(" na bindValue male");
				genderCount = BiGender.FEMALE_INDEX;
				bindValue(femaleClassHMap, classCount);
			}
		}
	}

	public void handlePlacementInContainer(
			ValuePerClassParameterDataPanel panel, Label topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 10);
		formData.right = new FormAttachment(100, -10);
		formData.bottom = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		panel.setLayoutData(formData);
	}

	private void bindValue(TypedHashMap typedHashMap, int index) {
		try {
			Text text = new Text(this, SWT.NONE);
			GridData gridData = new GridData();
			gridData.horizontalAlignment = SWT.FILL;
			text.setLayoutData(gridData);
			ArrayList<AtomicTypeObjectTuple> list = (ArrayList<AtomicTypeObjectTuple>) typedHashMap
					.get(index);
			WritableValue modelObservableValue = (WritableValue) ((AtomicTypeObjectTuple) list
					.get(0)).getValue();
			AtomicTypeBase<Float> theType = (AtomicTypeBase<Float>) list.get(0)
					.getType();
			String convertedText = theType.convert4View(modelObservableValue
					.doGetValue());
			text.setText(convertedText);
			// FocusListener focusListener = new TypedFocusListener(theType,
			// theHelpGroup);
			// text.addFocusListener(
			// focusListener);
			HelpTextListenerUtil.addHelpTextListeners(text, theType);
			// ND: Deprecated IObservableValue textObservableValue = SWTObservables.observeText(text, SWT.Modify);
			IObservableValue textObservableValue = WidgetProperties.text(SWT.Modify).observe(text);
			dataBindingContext.bindValue(textObservableValue,
					modelObservableValue, ((Percent) myType)
							.getModelUpdateValueStrategy(), ((Percent) myType)
							.getViewUpdateValueStrategy());
			text.addVerifyListener(new PercentVerifyListener(theHelpGroup
					.getTheModal()));
		} catch (NullPointerException e) {
			log.error(e.getClass().getName() + " age: " + ageCount + " sex: "
					+ genderCount + " class: " + index);
			// Do not change functionality.
			throw e;
		}
	}

	private void bindTestValue(TypedHashMap sexMap, int index) {
		Text text = new Text(this, SWT.NONE);
		text.setText(sexMap.get(index).toString());
		// ND: Deprecated IObservableValue textObservableValue = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue textObservableValue = WidgetProperties.text(SWT.Modify).observe(text);
		WritableValue modelObservableValue = (WritableValue) sexMap.get(index);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				ModelUpdateValueStrategies.getStrategy(modelObservableValue
						.getValueType()), ViewUpdateValueStrategies
						.getStrategy(modelObservableValue.getValueType()));
	}
}
