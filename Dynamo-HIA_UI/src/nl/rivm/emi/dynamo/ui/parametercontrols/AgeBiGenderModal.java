package nl.rivm.emi.dynamo.ui.parametercontrols;

import java.io.File;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;
import nl.rivm.emi.dynamo.data.factories.notinuse.SomethingPerAgeDataFromXMLFactory;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;
import nl.rivm.emi.dynamo.ui.panels.CharacteristicGroup;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.button.LotsOfButtonsPanel;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AgeBiGenderModal implements Runnable {
	Shell shell;
	// Composite composite = null;
	AgeSteppedContainer<BiGenderSteppedContainer<Integer>> lotsOfData;
	DataBindingContext dataBindingContext = null;
	boolean open = false;

	public AgeBiGenderModal(Shell parentShell, String configurationFilePath) {
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL |SWT.RESIZE);
		this.lotsOfData = manufactureModel(configurationFilePath);
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
	}

	public synchronized void open() {
		if (!open) {
			open = true;
//			putAndHookData();
			Composite buttonPanel = LotsOfButtonsPanel.generate(shell);
			Composite helpPanel = HelpGroup.generate(shell, buttonPanel);
			Composite characteristicPanel = CharacteristicGroup.generate(shell, helpPanel, buttonPanel);
			shell.pack();
			shell.open();
			Display display = shell.getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		}
	}

	public void setLotsOfData(
			AgeSteppedContainer<BiGenderSteppedContainer<Integer>> lotsOfData) {
		this.lotsOfData = lotsOfData;
	}

	public AgeSteppedContainer<BiGenderSteppedContainer<Integer>> manufactureModel(
			String configurationFilePath) {
		File configurationFile = new File(configurationFilePath);
		// log.fatal(configurationFile.getAbsolutePath());
		AgeSteppedContainer<BiGenderSteppedContainer<Integer>> testModel = SomethingPerAgeDataFromXMLFactory
				.manufacture(configurationFile);
		return testModel;
	}

	public void run() {

		// putAndHookData();
		dataBindingContext = new DataBindingContext();
		open();
	}

	public void putAndHookData() {
		Composite composite = new Composite(shell, SWT.NONE);
		// DataBindingContext dataBindingContext = new DataBindingContext();
		handlePlacementInContainer(composite);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
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
			bindValue(composite, dataBindingContext, bgsc,
					BiGender.FEMALE_INDEX);
			bindValue(composite, dataBindingContext, bgsc, BiGender.MALE_INDEX);
		}
	}

	static private void handlePlacementInContainer(Composite myComposite) {
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.top = new FormAttachment(0, -5);
		myComposite.setLayoutData(formData);
	}

	private void bindValue(Composite composite,
			DataBindingContext dataBindingContext,
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
}
