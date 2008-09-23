package nl.rivm.emi.dynamo.ui.main;

import java.io.File;

import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.factories.AgeGenderIncidenceDataFactory;
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

	public DiseaseIncidenceModal(Shell parentShell,
			String configurationFilePath) {
		this.configurationFilePath = configurationFilePath;
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
	}

	public synchronized void open() {
		try {
			dataBindingContext = new DataBindingContext();
			File configurationFile = new File(configurationFilePath);
			if (configurationFile.exists()) {
				if (configurationFile.isFile() && configurationFile.canRead()) {
					lotsOfData = AgeGenderIncidenceDataFactory
							.manufactureFromFlatXML(configurationFile);
					if (lotsOfData == null) {
						throw new ConfigurationException(
								"DataModel could not be constructed.");
					}
				} else {
					throw new ConfigurationException(configurationFilePath
							+ " is no file or cannot be read.");
				}
			} else {
				lotsOfData = AgeGenderIncidenceDataFactory
						.constructAllZeroesModel();
			}
			Composite buttonPanel = new GenericButtonPanel(shell);
			((GenericButtonPanel)buttonPanel).setModalParent((DataAndFileContainer)this);
			helpPanel = new HelpGroup(shell, buttonPanel);
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
		} catch (ConfigurationException e) {
			MessageBox box = new MessageBox(shell, SWT.ERROR_UNSPECIFIED);
			box.setText("Processing " + configurationFilePath);
			box.setMessage(e.getMessage());
			box.open();
		}
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

	public Object getData() {
		return lotsOfData;
	}

	public String getFilePath() {
		return configurationFilePath;
	}
}
