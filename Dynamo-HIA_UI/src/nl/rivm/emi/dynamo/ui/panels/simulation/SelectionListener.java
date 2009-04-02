package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabItem;

public class SelectionListener implements Listener {

	private Map<String, NestedTab> nestedTabs = null;
	
	public SelectionListener(Map<String, NestedTab> nestedTabs) {
		this.nestedTabs = nestedTabs;
	}

	@Override
    public void handleEvent(Event event) {
        TabItem item = (TabItem) event.item;
        String tabId=item.getText();			    
        Set<String> keys = nestedTabs.keySet();
        for (String key :  keys) {	            	
        	if (tabId == key) nestedTabs.get(key).redraw();
        }
    }	
}
