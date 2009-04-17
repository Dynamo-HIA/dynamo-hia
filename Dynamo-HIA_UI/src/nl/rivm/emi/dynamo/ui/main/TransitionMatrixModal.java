package nl.rivm.emi.dynamo.ui.main;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.ExcessMortalityObject;
import nl.rivm.emi.dynamo.data.objects.TransitionMatrixObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.ExcessMortalityGroup;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.TransitionMatrixAgeGroup;
import nl.rivm.emi.dynamo.ui.panels.TransitionMatrixOneAgeAndSexParameterGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Modal dialog to create and edit the population size XML files.
 * 
 */
public class TransitionMatrixModal extends AbstractDataModal {

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 */
	public TransitionMatrixModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode) {
		return "Transition Matrix";
	}

	@Override
	protected void open() {
		try {
			this.dataBindingContext = new DataBindingContext();
			this.modelObject = manufactureModelObject();

			GenericButtonPanel buttonPanel = new GenericButtonPanel(this.shell);
			((GenericButtonPanel) buttonPanel)
					.setModalParent((DataAndFileContainer) this);
			this.helpPanel = new HelpGroup(this.shell, buttonPanel);
			TransitionMatrixAgeGroup ageGroup = new TransitionMatrixAgeGroup(
					this.shell, SWT.NONE, selectedNode, modelObject,
					dataBindingContext, helpPanel, buttonPanel);
			this.shell.pack();
			// This is the first place this works.
			this.shell.setSize(1100, 400);
			this.shell.open();
			Display display = this.shell.getDisplay();
			while (!this.shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (ConfigurationException e) {
			MessageBox box = new MessageBox(this.shell, SWT.ERROR_UNSPECIFIED);
			box.setText("Processing " + this.configurationFilePath);
			box.setMessage(e.getMessage());
			box.open();
		} catch (DynamoInconsistentDataException e) {
			MessageBox box = new MessageBox(this.shell, SWT.ERROR_UNSPECIFIED);
			box.setText("Processing " + this.configurationFilePath);
			box.setMessage(e.getMessage());
			box.open();
		}
	}

}
