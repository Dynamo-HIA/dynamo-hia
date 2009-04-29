package nl.rivm.emi.dynamo.ui.listeners.selection;

import java.io.File;

import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.ui.actions.XMLFileAction;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.treecontrol.TreeViewerPlusCustomMenu;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;

public class StartingYearModifyListener extends AbstractLoggingClass implements
		SelectionListener {

	private WritableValue observable;
	private DataAndFileContainer modalParent;

	public StartingYearModifyListener(DataAndFileContainer modalParent, WritableValue observable) {
		this.modalParent = modalParent;
		this.observable = observable;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");	
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetSelected callback.");		
				
		// TODO: Define the actions to be taken
		// Retrieve the existing object model		
		NewbornsObject newbornsObject = (NewbornsObject) this.modalParent.getData();		
		
		// Update the object model
		//TODO for () {
			
			//TODO newbornsObject.putNumber(index, number);// MAY THIS BE USED???
			// Add prefix zero's if needed
			// Add postfix last number copies if needed
			
		//TODO }
				
		// Update action has finished, refresh the screen with the
		// new object model
		//TODO if () {
			this.modalParent.getShell().dispose();
			
			String rootElementName = (String) this.modalParent.getRootElementName();
			XMLFileAction action = new XMLFileAction(this.modalParent.getParentShell(), TreeViewerPlusCustomMenu.getTreeViewerInstance(),
					this.modalParent.getBaseNode(),
					rootElementName, rootElementName);
	
			
		//TODO }
			
		
		
	}
	
}
