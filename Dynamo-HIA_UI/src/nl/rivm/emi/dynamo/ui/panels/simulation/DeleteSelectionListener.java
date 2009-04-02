package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;

public class DeleteSelectionListener extends AbstractLoggingClass 
	implements SelectionListener {

	private TabPlatform tabPlatform = null;
	
	public DeleteSelectionListener(TabPlatform platform) {
		this.tabPlatform = platform;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");		
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		// TODO FIRST SELECT THE NESTED TAB NUMBER THAT YOU WENT 
		// TO REMOVE
		// TEST: remove the last tab every time		
		
		// TODO Auto-generated method stub
		this.tabPlatform.getTabManager().
		deleteNestedTab(this.tabPlatform.getTabManager().getNumberOfTabs());
	}

}
