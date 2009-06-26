package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IContinuousPrevalencesObject;
import nl.rivm.emi.dynamo.data.interfaces.IMortalityObject;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.listeners.TypedFocusListener;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.listeners.DistributionTypeComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.listeners.UnitTypeComboModifyListener;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RiskFactorContinuousPrevalencesDataPanel extends Composite /*
															 * implements
															 * Runnable
															 */{
	Log log = LogFactory
			.getLog(this.getClass().getName());
	IContinuousPrevalencesObject myPrevalencesObject;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;

	// AtomicTypeBase myType = new Unit();

	public RiskFactorContinuousPrevalencesDataPanel(Composite parent, Text topNeighbour,
			IContinuousPrevalencesObject continuousPrevalencesObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup,
			DistributionTypeComboModifyListener distributionTypeModifyListener) {
		super(parent, SWT.NONE);
		this.myPrevalencesObject = continuousPrevalencesObject;
		this.dataBindingContext = dataBindingContext;
		theHelpGroup = helpGroup;
		GridLayout layout = new GridLayout();
		layout.numColumns = 7;
		layout.makeColumnsEqualWidth = false;
		setLayout(layout);
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label meanLabel = new Label(this, SWT.NONE);
		meanLabel.setText("Mean");
		Label standardDeviationLabel = new Label(this, SWT.NONE);
		standardDeviationLabel.setText("Std.");
		Label skewnessLabel = new Label(this, SWT.NONE);
		skewnessLabel.setText("Skewness");
		Label maleMeanLabel = new Label(this, SWT.NONE);
		maleMeanLabel.setText("Mean");
		Label maleStandardDeviationLabel = new Label(this, SWT.NONE);
		maleStandardDeviationLabel.setText("Std.");
		Label maleSkewnessLabel = new Label(this, SWT.NONE);
		maleSkewnessLabel.setText("Skewness");
		// int numberOfAges = iMortalityObject.getNumberOfMortalities();
		TypedHashMap<Age> ageMap = continuousPrevalencesObject.getPrevalences();
		int numberOfAges = ageMap.size();
		for (int AgeCount = 0; AgeCount < numberOfAges; AgeCount++) {
			Label label = new Label(this, SWT.NONE);
			label.setText(new Integer(AgeCount).toString());
			TypedHashMap<Sex> sexMap = (TypedHashMap<Sex>) ageMap.get(AgeCount);
			int numberOfSexes = sexMap.size();
			for (int sexCount = 0; sexCount < numberOfSexes; sexCount++) {
				ArrayList<AtomicTypeObjectTuple> arrayList = (ArrayList<AtomicTypeObjectTuple>) sexMap
						.get(sexCount);
				for (int paramCount = 0; paramCount < arrayList.size(); paramCount++) {
					AtomicTypeObjectTuple tuple = arrayList.get(paramCount);
					WritableValue observableClassName = (WritableValue) tuple
							.getValue();
					XMLTagEntity theType = tuple.getType();
					bindValue(observableClassName,
							(AtomicTypeBase<Float>) theType);
				}
			}
		}
	}

	private void bindValue(WritableValue observableClassName,
			AtomicTypeBase<Float> theType) {
		Text text = createAndPlaceTextField();
		text.setText(theType.convert4View(observableClassName.doGetValue()));
		FocusListener focusListener = new TypedFocusListener(theType,theHelpGroup);
		text.addFocusListener(
		focusListener);
		// Too early, see below. text.addVerifyListener(new
		// StandardValueVerifyListener());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) observableClassName;
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				theType.getModelUpdateValueStrategy(), theType
						.getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener(theHelpGroup.getTheModal()));
	}

	private Text createAndPlaceTextField() {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 35;
		gridData.horizontalAlignment = GridData.END;
		text.setLayoutData(gridData);
		return text;
	}
}
