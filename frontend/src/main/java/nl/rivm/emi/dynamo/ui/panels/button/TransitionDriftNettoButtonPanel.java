package nl.rivm.emi.dynamo.ui.panels.button;

import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.selection.ImportSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.TransitionDriftNettoSaveSelectionListener;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftNettoModal;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Shell;

public class TransitionDriftNettoButtonPanel extends GenericButtonPanel {

	TransitionDriftNettoModal modalParent;

	/**
	 * 
	 * Constructs the panel with buttons
	 * 
	 * @param shell
	 */
	public TransitionDriftNettoButtonPanel(Shell shell) {
		super(shell);
	}

	@SuppressWarnings("unused")
	private void setFormData() {
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		setLayoutData(formData);
	}

	/**
	 * 
	 * Sets the parent of the modal
	 * 
	 * @param theParent
	 */
	public void setModalParent(TransitionDriftNettoModal theParent) {
		this.modalParent = theParent;
		this.saveButton
				.addSelectionListener(new TransitionDriftNettoSaveSelectionListener(
						this.modalParent));
		HelpTextListenerUtil.addHelpTextListeners(saveButton);
		this.importButton.addSelectionListener(new ImportSelectionListener(
				this.modalParent));
		HelpTextListenerUtil.addHelpTextListeners(importButton);
	}

}
