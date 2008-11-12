package nl.rivm.emi.dynamo.ui.main;

import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.factories.dispatch.FromXMLFactoryDispatcher;
import nl.rivm.emi.dynamo.data.objects.ObservableIncidencesObject;
import nl.rivm.emi.dynamo.ui.panels.CharacteristicGroup;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DiseaseIncidenceModal implements Runnable, DataAndFileContainer {
	Log log = LogFactory.getLog(this.getClass().getName());
	Shell shell;
	AgeMap<SexMap<IObservable>> lotsOfData;
	DataBindingContext dataBindingContext = null;
	String configurationFilePath;
	HelpGroup helpPanel;

	public DiseaseIncidenceModal(Shell parentShell, String configurationFilePath)
			throws ConfigurationException {
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
		MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
		box.setText("Loading file ");
		box.setMessage(configurationFilePath);
		box.open();
		this.configurationFilePath = configurationFilePath;
	}

	public synchronized void open() throws ConfigurationException {
		dataBindingContext = new DataBindingContext();
		Composite buttonPanel = new GenericButtonPanel(shell);
		((GenericButtonPanel) buttonPanel)
				.setModalParent((DataAndFileContainer) this);
		helpPanel = new HelpGroup(shell, buttonPanel);
		try {
			lotsOfData = (ObservableIncidencesObject) FromXMLFactoryDispatcher
					.makeObservableDataObject(configurationFilePath);
		} catch (ClassCastException e) {
			throw new ConfigurationException(e.getClass().getName() + " "
					+ e.getMessage());
		}
		CharacteristicGroup characteristicGroup = new CharacteristicGroup(
				shell, lotsOfData, dataBindingContext, helpPanel);
		characteristicGroup.setFormData(helpPanel.getGroup(), buttonPanel);
		shell.pack();
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public void run(){
		try {
			open();
		} catch (ConfigurationException e) {
			MessageBox box = new MessageBox(shell, SWT.ERROR_UNSPECIFIED);
			StackTraceElement[] stackTraceElements = e.getStackTrace();
			StringBuffer theText = new StringBuffer();
			theText.append(e.getClass().getName() + "\n");
			theText.append(e.getMessage() + "\n\n");
			for (StackTraceElement stackTraceElement : stackTraceElements) {
				theText.append(stackTraceElement.getClassName() + "."
						+ stackTraceElement.getMethodName() + "("
						+ stackTraceElement.getLineNumber() + ")\n");
			}
			box.setText("Trouble in IncidenceModal run.");
			// box.setMessage(e.getMessage());
			box.setMessage(theText.toString());
			box.open();
}
	}

	static private void handlePlacementInContainer(Composite myComposite) {
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.top = new FormAttachment(0, -5);
		myComposite.setLayoutData(formData);
	}

	public Object getData() {
		return lotsOfData;
	}

	public String getFilePath() {
		return configurationFilePath;
	}
}
