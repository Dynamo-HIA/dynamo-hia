package nl.rivm.emi.dynamo.ui.parametercontrols;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AgeBiGenderModal implements Runnable {
	Shell shell;
	Composite composite;
	AgeSteppedContainer<BiGenderSteppedContainer<Integer>> lotsOfData;

	public AgeBiGenderModal(Shell parentShell,
			AgeSteppedContainer<BiGenderSteppedContainer<Integer>> lotsOfData) {
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		this.lotsOfData = lotsOfData;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
	}

	private void createControlButtons() {
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		Button okButton = new Button(composite, SWT.PUSH);
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		Button cancelButton = new Button(composite, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// values = null;
				shell.close();
			}
		});

		shell.setDefaultButton(okButton);
	}

	public void putAndHookData() {
		composite = new Composite(shell, SWT.NONE);
		DataBindingContext dataBindingContext = new DataBindingContext();
		Label ageLabel = new Label(composite, SWT.NONE);
		ageLabel.setText("Age");
		Label femaleLabel = new Label(composite, SWT.NONE);
		femaleLabel.setText("Female");
		Label maleLabel = new Label(composite, SWT.NONE);
		maleLabel.setText("Male");
		for (int count = 0; count < lotsOfData.size(); count++) {
			BiGenderSteppedContainer<Integer> bgsc = lotsOfData.get(count);
			Label label = new Label(composite, SWT.NONE);
			label.setText(new Float(count * lotsOfData.getAgeStepSize())
					.toString());
			bindValue(dataBindingContext, bgsc, BiGender.FEMALE_INDEX);
			bindValue(dataBindingContext, bgsc, BiGender.MALE_INDEX);
		}
	}

	private void bindValue(DataBindingContext dataBindingContext,
			BiGenderSteppedContainer<Integer> bgsc, int index) {
		Text text = new Text(composite, SWT.NONE);
		text.setText(bgsc.get(index).toString());
		IObservableValue textObservableValue = SWTObservables.observeText(text,
				SWT.Modify);
		Object theValue = bgsc.get(index);
		IObservableValue modelObservableValue = (IObservableValue) new WritableValue(
				theValue, theValue);
		dataBindingContext.bindValue(textObservableValue, modelObservableValue,
				ModelUpdateValueStrategies.getStrategy(theValue),
				ViewUpdateValueStrategies.getStrategy(theValue));
	}

	public void run() {
		putAndHookData();
	}
}
