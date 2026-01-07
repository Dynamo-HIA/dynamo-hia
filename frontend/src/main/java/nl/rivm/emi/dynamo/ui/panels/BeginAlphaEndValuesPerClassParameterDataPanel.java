package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.CatContainer;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.Value;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BeginAlphaEndValuesPerClassParameterDataPanel extends Composite /*
																			 * implements
																			 * Runnable
																			 */{
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.panels.ParameterDataPanel");
	TypedHashMap<?> lotsOfData;
	final Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase<?> myType;

	@SuppressWarnings("unchecked")
	public BeginAlphaEndValuesPerClassParameterDataPanel(Composite parent,
			Text topNeighbour, TypedHashMap<Age> lotsOfData,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		myType = (AtomicTypeBase<?>) XMLTagEntitySingleton.getInstance().get(
				"value");
		GridLayout layout = new GridLayout();
		layout.numColumns = 8;
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		// Top line.
		final Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
		final Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		// not enough room for label so window made larger
		// solutions below do not work
		//femaleLabel.requestLayout();
		//FormData formData = new FormData();
		//formData.width=58;
		//femaleLabel.setLayoutData(formData);
		
		// Just above the values.
		final Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		final Label classLabel = new Label(this, SWT.NONE);
		classLabel.setText("Class");
		final Label femaleBeginLabel = new Label(this, SWT.NONE);
		femaleBeginLabel.setText("Begin");
		final Label femaleAlphaLabel = new Label(this, SWT.NONE);
		femaleAlphaLabel.setText("Alpha");
		final Label femaleEndLabel = new Label(this, SWT.NONE);
		femaleEndLabel.setText("End");
		final Label maleBeginLabel = new Label(this, SWT.NONE);
		maleBeginLabel.setText("Begin");
		final Label maleAlphaLabel = new Label(this, SWT.NONE);
		maleAlphaLabel.setText("Alpha");
		final Label maleEndLabel = new Label(this, SWT.NONE);
		maleEndLabel.setText("End");
		for (int ageCount = 0; ageCount < lotsOfData.size(); ageCount++) {
			TypedHashMap<Sex> oneAgeMap = (TypedHashMap<Sex>) lotsOfData.get(ageCount);
			TypedHashMap<CatContainer> femaleClassHMap = (TypedHashMap<CatContainer>) oneAgeMap
					.get(BiGender.FEMALE_INDEX);
			TypedHashMap<Sex> maleClassHMap = (TypedHashMap<Sex>) oneAgeMap
					.get(BiGender.MALE_INDEX);
			for (int classCount = 1; classCount <= femaleClassHMap.size(); classCount++) {
				final Label ageCellLabel = new Label(this, SWT.NONE);
				if (classCount == 1) {
					ageCellLabel.setText(Integer.valueOf(ageCount).toString());
				}
				final Label classCellLabel = new Label(this, SWT.NONE);
				classCellLabel.setText(Integer.valueOf(classCount).toString());
				bindValues(femaleClassHMap, classCount);
				bindValues(maleClassHMap, classCount);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void bindValues(TypedHashMap<?> typedHashMap, int index) {
		ArrayList<AtomicTypeObjectTuple> data = (ArrayList<AtomicTypeObjectTuple>) typedHashMap
				.get(index);
		for (int count = 0; count < data.size(); count++) {
			final Text text = new Text(this, SWT.NONE);
			GridData gridData = new GridData();
			gridData.horizontalAlignment = SWT.FILL;
			text.setLayoutData(gridData);
			XMLTagEntity type = (data.get(count)).getType();
			Object value = (data.get(count)).getValue();
			String convertedText = ((AtomicTypeBase<?>) type)
					.convert4View(value);
			text.setText(convertedText);
			HelpTextListenerUtil.addHelpTextListeners(text, myType);
			// Too early, see below. text.addVerifyListener(new
			// StandardValueVerifyListener());
			//ND: Deprecated IObservableValue textObservableValue = SWTObservables.observeText(text, SWT.Modify);
			@SuppressWarnings("rawtypes")
			IObservableValue textObservableValue = WidgetProperties.text(SWT.Modify).observe(text);
			@SuppressWarnings("rawtypes")
			WritableValue modelObservableValue = (WritableValue) data
					.get(count).getValue();
			dataBindingContext.bindValue(textObservableValue,
					modelObservableValue, ((Value) myType)
							.getModelUpdateValueStrategy(), ((Value) myType)
							.getViewUpdateValueStrategy());
			text.addVerifyListener(new ValueVerifyListener(theHelpGroup.getTheModal()));
		}
	}

//	private void bindTestValue(TypedHashMap<?> sexMap, int index) {
//		final Text text = new Text(this, SWT.NONE);
//		text.setText(sexMap.get(index).toString());
//		IObservableValue textObservableValue = SWTObservables.observeText(text,
//				SWT.Modify);
//		WritableValue modelObservableValue = (WritableValue) sexMap.get(index);
//		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
//				ModelUpdateValueStrategies.getStrategy(modelObservableValue
//						.getValueType()), ViewUpdateValueStrategies
//						.getStrategy(modelObservableValue.getValueType()));
//	}
}
