package nl.rivm.emi.dynamo.ui.listeners.selection;

import java.io.File;

import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.Number;
import nl.rivm.emi.dynamo.ui.actions.XMLFileAction;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.treecontrol.TreeViewerPlusCustomMenu;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class StartingYearUpdateButtonSelectionListener extends
		AbstractLoggingClass implements SelectionListener {
	DataAndFileContainer modalParent;
	Text startingYearText;
	AtomicTypeBase<Integer> startingYearType;

	public StartingYearUpdateButtonSelectionListener(
			DataAndFileContainer modalParent, Text startingYearText,
			AtomicTypeBase<Integer> startingYearType) {
		this.modalParent = modalParent;
		this.startingYearText = startingYearText;
		this.startingYearType = startingYearType;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	@Override
	synchronized public void widgetSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetSelected callback.");
		Integer newStartingYearValue = (Integer) startingYearType
				.convert4Model(startingYearText.getText());
		NewbornsObject newbornsObject = (NewbornsObject) modalParent.getData();


		Integer oldStartingYearValue = newbornsObject.getStartingYear();
		// Only do something if changed.
		if (!newStartingYearValue.equals(oldStartingYearValue)) {
			modalParent.getShell().dispose();
			int newStartingYearInt = newStartingYearValue.intValue();
			int oldStartingYearInt = oldStartingYearValue.intValue();
			int numberOfAmounts = newbornsObject.getNumberOfAmounts();
			// Cleanup before.
			if (oldStartingYearInt < newStartingYearInt) {
				for (int yearCounter = oldStartingYearInt; yearCounter < newStartingYearInt; yearCounter++) {
					newbornsObject.removeNumber(yearCounter);
				}
			}
			// Add defaults when nescessary.
			for (int yearCounter = newStartingYearInt; yearCounter < newStartingYearInt
					+ numberOfAmounts; yearCounter++) {
				if (newbornsObject.getNumber(yearCounter) == null) {
					newbornsObject
							.addNumber(yearCounter, (Integer) ((Number)XMLTagEntityEnum.NUMBER.getTheType()).getDefaultValue(), false);
				}
			}
			// Cleanup after.
			if (oldStartingYearInt > newStartingYearInt) {
				for (int yearCounter = newStartingYearInt + numberOfAmounts; yearCounter < oldStartingYearInt
						+ numberOfAmounts; yearCounter++) {
					newbornsObject.removeNumber(yearCounter);
				}
			}
			newbornsObject.setStartingYear(newStartingYearValue);
			// Recreate the newborns page
			String rootElementName = (String) modalParent.getRootElementName();
			XMLFileAction action = new XMLFileAction(modalParent
					.getParentShell(), TreeViewerPlusCustomMenu
					.getTreeViewerInstance(), modalParent.getBaseNode(),
					rootElementName, rootElementName);
			// Set the updated object model before the screen is
			// recreated
			action.setModelObject(newbornsObject);
			File filePath = new File(modalParent.getConfigurationFilePath());
			action.processThroughModal(filePath, filePath);
		}
	}
}