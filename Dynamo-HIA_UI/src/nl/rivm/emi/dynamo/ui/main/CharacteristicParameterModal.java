package nl.rivm.emi.dynamo.ui.main;

import java.io.File;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;
import nl.rivm.emi.dynamo.data.factories.SomethingPerAgeDataFromXMLFactory;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ModelUpdateValueStrategies;
import nl.rivm.emi.dynamo.databinding.updatevaluestrategy.ViewUpdateValueStrategies;
import nl.rivm.emi.dynamo.ui.panels.CharacteristicGroup;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CharacteristicParameterModal implements Runnable {
	Shell shell;
	AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> lotsOfData;
	DataBindingContext dataBindingContext = null;
	String configurationFilePath;

	public CharacteristicParameterModal(Shell parentShell,
			String configurationFilePath) {
		this.configurationFilePath = configurationFilePath;
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
	}

	public synchronized void open() {
		dataBindingContext = new DataBindingContext();
		this.lotsOfData = manufactureModel(configurationFilePath);
		Composite buttonPanel = new GenericButtonPanel(shell);
		HelpGroup helpPanel = new HelpGroup(shell, buttonPanel);
		CharacteristicGroup characteristicGroup = new CharacteristicGroup(
				shell, lotsOfData, dataBindingContext);
		characteristicGroup.setFormData(helpPanel.getGroup(), buttonPanel);
		shell.pack();
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public void setLotsOfData(
			AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> lotsOfData) {
		this.lotsOfData = lotsOfData;
	}

	public AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> manufactureModel(
			String configurationFilePath) {
		File configurationFile = new File(configurationFilePath);
		AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> testModel = SomethingPerAgeDataFromXMLFactory
				.manufacture(configurationFile);
		return testModel;
	}

	public void run() {
		open();
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
			BiGenderSteppedContainer<IObservable> bgsc, int index) {
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
