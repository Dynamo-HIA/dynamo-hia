package nl.rivm.emi.dynamo.ui.panels;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IContinuousPrevalencesObject;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.verify.ValueVerifyListener;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.DistributionTypeComboModifyListener;

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

public class RiskFactorContinuousPrevalencesDataPanel extends Composite /*
																		 * implements
																		 * Runnable
																		 */{
	Log log = LogFactory.getLog(this.getClass().getName());
	IContinuousPrevalencesObject myPrevalencesObject;
	Composite myParent = null;
	boolean open = false;
	DataBindingContext dataBindingContext = null;
	HelpGroup theHelpGroup;

	// AtomicTypeBase myType = new Unit();

	public RiskFactorContinuousPrevalencesDataPanel(Composite parent,
			Text topNeighbour,
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
		makeGenderLine();
		makeParameterNameLine();
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

	private void makeParameterNameLine() {
		GridData labelGridData = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		GridData dataGridData = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		dataGridData.widthHint = 50;

		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		ageLabel.setLayoutData(labelGridData);
		Label meanLabel = new Label(this, SWT.NONE);
		meanLabel.setText("Mean");
		meanLabel.setLayoutData(dataGridData);
		Label standardDeviationLabel = new Label(this, SWT.NONE);
		standardDeviationLabel.setText("Std.");
		standardDeviationLabel.setLayoutData(dataGridData);
		Label skewnessLabel = new Label(this, SWT.NONE);
		skewnessLabel.setText("Skewness");
		skewnessLabel.setLayoutData(dataGridData);
		Label maleMeanLabel = new Label(this, SWT.NONE);
		maleMeanLabel.setText("Mean");
		maleMeanLabel.setLayoutData(dataGridData);
		Label maleStandardDeviationLabel = new Label(this, SWT.NONE);
		maleStandardDeviationLabel.setText("Std.");
		maleStandardDeviationLabel.setLayoutData(dataGridData);
		Label maleSkewnessLabel = new Label(this, SWT.NONE);
		maleSkewnessLabel.setText("Skewness");
		maleSkewnessLabel.setLayoutData(dataGridData);
	}

	private void makeGenderLine() {
		GridData labelGridData = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		GridData dataGridData = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		dataGridData.widthHint = 50;

		Label spaceLabel1 = new Label(this, SWT.NONE);
		spaceLabel1.setLayoutData(labelGridData);
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
		maleLabel.setLayoutData(dataGridData);
		Label spaceLabel2 = new Label(this, SWT.NONE);
		spaceLabel2.setLayoutData(dataGridData);
		Label spaceLabel3 = new Label(this, SWT.NONE);
		spaceLabel3.setLayoutData(dataGridData);
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		femaleLabel.setLayoutData(dataGridData);
		Label spaceLabel4 = new Label(this, SWT.NONE);
		spaceLabel4.setLayoutData(dataGridData);
		Label spaceLabel5 = new Label(this, SWT.NONE);
		spaceLabel5.setLayoutData(dataGridData);
	}

	private void bindValue(WritableValue observableClassName,
			AtomicTypeBase<Float> theType) {
		Text text = createAndPlaceTextField();
		text.setText(theType.convert4View(observableClassName.doGetValue()));
		HelpTextListenerUtil.addHelpTextListeners(text, theType);
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		WritableValue modelObservableValue = (WritableValue) observableClassName;
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				theType.getModelUpdateValueStrategy(), theType
						.getViewUpdateValueStrategy());
		text.addVerifyListener(new ValueVerifyListener(theHelpGroup
				.getTheModal()));
	}

	private Text createAndPlaceTextField() {
		Text text = new Text(this, SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 50;
		// gridData.horizontalAlignment = GridData.END;
		text.setLayoutData(gridData);
		return text;
	}
}
