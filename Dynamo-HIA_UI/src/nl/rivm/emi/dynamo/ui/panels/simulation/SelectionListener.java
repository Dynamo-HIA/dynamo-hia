package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabItem;


public class SelectionListener implements Listener {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private Map<Integer, NestedTab> nestedTabs = null;
	
	public SelectionListener(Map<Integer, NestedTab> nestedTabs) {
		this.nestedTabs = nestedTabs;
	}

	@Override
    public void handleEvent(Event event) {
        TabItem item = (TabItem) event.item;
        String tabId = item.getText();
        log.debug("tabId" + tabId);
        Set<Integer> keys = nestedTabs.keySet();
        log.debug("nestedTabs.size()" + nestedTabs.size());
        for (Integer key :  keys) {	            	
        	//if (tabId.substring(beginIndex). == key) nestedTabs.get(key).redraw();
        }
    }	
}
