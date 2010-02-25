package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.DurationDistributionObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractFlexibleUpperLimitInteger;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.verify.PercentVerifyListener;
import nl.rivm.emi.dynamo.ui.main.DurationDistributionModal;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DurationDistributionParameterDataPanel extends Composite {
	Log log = LogFactory.getLog(this.getClass().getName());
	DurationDistributionObject lotsOfData;
	final Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;
	AtomicTypeBase<?> myType;
	int durationClassIndex;

	@SuppressWarnings("unchecked")
	public DurationDistributionParameterDataPanel(Composite parent,
			int genderIndex, DurationDistributionObject lotsOfData,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		log.debug("Constructing DurationDistributionParameterDataPanel");
		this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		myType = (AtomicTypeBase<?>) XMLTagEntitySingleton.getInstance().get(
				"value");
		// this.durationClassIndex = durationClassIndex;
		GridLayout layout = new GridLayout();
		layout.numColumns = 0;
		layout.makeColumnsEqualWidth = true;
		setLayout(layout);
		// Top line.
		createHeader();
		createAgeLine();
		log.debug("Number of columns: " + Integer.toString(layout.numColumns));
		for (int ageCount = 0; ageCount < lotsOfData.size(); ageCount++) {
			GridData gridData = new GridData();
			gridData.horizontalAlignment = SWT.FILL;
			final Label ageLabel = new Label(this, SWT.NONE);
			ageLabel.setText(Integer.toString(ageCount));
			ageLabel.setLayoutData(gridData);
			log.debug("Handling Age: " + Integer.toString(ageCount));
			TypedHashMap<?> oneAgeMap = (TypedHashMap<?>) lotsOfData
					.get(ageCount);
			TypedHashMap<ArrayList<AtomicTypeObjectTuple>> oneGenderMap = (TypedHashMap<ArrayList<AtomicTypeObjectTuple>>) oneAgeMap
					.get(genderIndex);
			for (int durationCount = 1; durationCount <= oneGenderMap.size(); durationCount++) {
				ArrayList<AtomicTypeObjectTuple> genderList = (ArrayList<AtomicTypeObjectTuple>) oneGenderMap
						.get(durationCount);
				// log.debug("Aantal durations: " + genderList.size());
				bindDurationValue(genderList, durationCount);
			}
		}
	}

	private void bindDurationValue(ArrayList<AtomicTypeObjectTuple> tupleList, int durationValue) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) tupleList.get(0);
		bindTuple(gridData, tuple, durationValue);
	}

	private void bindTuple(GridData gridData, AtomicTypeObjectTuple tuple, int durationValue) {
		WritableValue modelObservableValue = (WritableValue) tuple.getValue();
		AtomicTypeBase<?> modelType = (AtomicTypeBase<?>) tuple.getType();
		final Text text = new Text(this, SWT.NONE);
		text.setLayoutData(gridData);
		if ((durationValue == 1) && ((DurationDistributionModal) theHelpGroup.getTheModal())
				.isHasDefaultObject()) {
			Object defaultValue = modelType.getDefaultValue();
			if (defaultValue instanceof Float) {
				Float oneHundred = new Float(100F);
				modelObservableValue.doSetValue(oneHundred);
			} else {
				log
						.error("The type is not a Float value and not updated to 100.");
			}
		}
		String convertedText = modelType.convert4View(modelObservableValue
				.getValue());
		text.setText(convertedText);
		HelpTextListenerUtil.addHelpTextListeners(text, myType);
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				modelType.getModelUpdateValueStrategy(), modelType
						.getViewUpdateValueStrategy());
		text.addVerifyListener(new PercentVerifyListener(theHelpGroup
				.getTheModal()));
	}

	private void createHeader() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		final Label ageHeader = new Label(this, SWT.NONE);
		ageHeader.setText("Duration");
		ageHeader.setLayoutData(gridData);
		((GridLayout) getLayout()).numColumns++;
		int maxDuration = ((AbstractFlexibleUpperLimitInteger) XMLTagEntityEnum.DURATION
				.getTheType()).getMAX_VALUE();
		log.debug("maxDuration: " + maxDuration);
		for (int durationCount = 1; durationCount <= maxDuration; durationCount++) {
			final Label durationHeader = new Label(this, SWT.NONE);
			String durationHeaderText = Integer.toString(durationCount);
			log.debug("DurationHeaderText: " + durationHeaderText);
			durationHeader.setText(durationHeaderText);
			((GridLayout) getLayout()).numColumns++;
		}
	}

	private void createAgeLine() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		final Label ageHeader = new Label(this, SWT.NONE);
		ageHeader.setText("Age");
		ageHeader.setLayoutData(gridData);
		int columnsToFill = ((GridLayout) getLayout()).numColumns - 1;
		for (int columnsCount = 1; columnsCount <= columnsToFill; columnsCount++) {
			final Label fillLabel = new Label(this, SWT.NONE);
		}
	}
}
