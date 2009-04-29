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

	private DataAndFileContainer modalParent;

	public StartingYearModifyListener(DataAndFileContainer modalParent) {
		this.modalParent = modalParent;
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
		// Destroy the existing data screen with 'old' data
		this.modalParent.getShell().dispose();
		
		log.debug("newbornsObject::BEFORE:: " + newbornsObject);		
		// TODO Modify the Number list, depending on the changed Starting Year
		int numberOfNumbers = newbornsObject.getNumberOfNumbers();

		WritableValue observableObject = newbornsObject.getObservableStartingYear();
		int previousStartingYear = newbornsObject.getPreviousStartingYear();
		log.debug("previousStartingYear" + previousStartingYear);
		int newStartingYear = ((Integer) observableObject.doGetValue()).intValue();		
		
		Integer lastYear = new Integer(numberOfNumbers + previousStartingYear - 1);
		Integer lastYearNumber = (Integer) newbornsObject.getObservableNumber(lastYear).doGetValue();
		
		if (previousStartingYear == newStartingYear) {
			return;
		} else if (previousStartingYear < newStartingYear) {			
			
			int minYears = previousStartingYear + numberOfNumbers;
			if ((previousStartingYear + numberOfNumbers) < newStartingYear) {
				minYears = newStartingYear;	
			}
			
			//  Remove old prefix values 
			for (int yearCount = previousStartingYear; yearCount < newStartingYear; yearCount++) {
				newbornsObject.removeNumber(yearCount);
			}
			
			// Add new postfix values as default, taken from the last year of the old newbornsObject
			for (int yearCount = (minYears); 
					yearCount < (numberOfNumbers + newStartingYear); yearCount++) {
				newbornsObject.addNumber(yearCount, lastYearNumber, false);
			}
		} else if (previousStartingYear > newStartingYear) {
			
			int maxYears = previousStartingYear - 1;
			if ((previousStartingYear -1) > newStartingYear + numberOfNumbers) {
				maxYears = newStartingYear + numberOfNumbers;
			}

			// Remove old postfix values
			for (int yearCount = (numberOfNumbers + newStartingYear); 
					yearCount < (numberOfNumbers + previousStartingYear); yearCount++) {
				newbornsObject.removeNumber(yearCount);
			}
						
			// Add new prefix values as default, value 0				
			for (int yearCount = maxYears; yearCount >= newStartingYear; yearCount--) {
				newbornsObject.addNumber(yearCount, new Integer(0), true);
			}
		}
		log.debug("newbornsObject::AFTER:: " + newbornsObject);
		// Recreate the newborns page
		String rootElementName = (String) this.modalParent.getRootElementName();
		XMLFileAction action = new XMLFileAction(this.modalParent.getParentShell(), 
				TreeViewerPlusCustomMenu.getTreeViewerInstance(),
				this.modalParent.getBaseNode(),
				rootElementName, rootElementName);
		// Set the updated object model before the screen is recreated
		action.setModelObject(newbornsObject);
		File filePath = new File(this.modalParent.getConfigurationFilePath());		
		action.processThroughModal(filePath, filePath);		
	}
	
}
