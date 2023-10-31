package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.selection.RunSelectionListener;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class RunButtonPanel {

	// Nowhere else to Run. private final DataAndFileContainer modalParent;
	private final SimulationModal modalParent;
	Group group;
	private Button runButton;
	/**
	 * Height of runbutton panel in pixels.
	 */
	int height = 50;

	public RunButtonPanel(Composite parent, SimulationModal theModal) {
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		modalParent = theModal;
		runButton = putRunButton(group);
		runButton.addSelectionListener(new RunSelectionListener(theModal));
		HelpTextListenerUtil.addHelpTextListeners(runButton);
		}

	private Button putRunButton(Composite parent) {
		FormData formData = new FormData();
		Button runButton = new Button(parent, SWT.PUSH);
		if(!modalParent.isConfigurationFileReadOnly()){
		runButton.setText("Save and Run");
		} else {
			runButton.setText("Run (cannot Save)");
		}
		// Subscribe for changed-callbacks.
// Subscription delegated to the instantiator.		modalParent.setRunButtonPanel(this);
		// And initialize the initial state.
		runButton.setEnabled(true);
		formData = new FormData();
		formData.left = new FormAttachment(0, 100);
		formData.bottom = new FormAttachment(100, /*-200 */-5);
		runButton.setLayoutData(formData);
		return runButton;
	}
	
	public void enableButton(boolean doIt){
		if(!runButton.isDisposed()){
		runButton.setEnabled(doIt);
		}
	}

	/**
	 * 
	 * Place the last (and second) group in the container
	 * 
	 * @param topNeighbour
	 */
	public void putLastInContainer(Composite topNeighbour) {
		FormData formData = new FormData();
		// formData.top = new FormAttachment(topNeighbour, 5);
		formData.top = new FormAttachment(100, -(5 + height));
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}
}
