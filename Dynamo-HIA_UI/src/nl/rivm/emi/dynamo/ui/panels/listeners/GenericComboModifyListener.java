package nl.rivm.emi.dynamo.ui.panels.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;

public class GenericComboModifyListener implements ModifyListener {
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private Set<Combo> registeredDropDowns = new HashSet<Combo>();						
	private Map<Combo, Map> nestedContents = new HashMap<Combo, Map>();

	public void setNestedContents(Map nestedContents) {
		this.nestedContents = nestedContents;
	}

	public void registerDropDown(Combo dropdown) {
		registeredDropDowns.add(dropdown);
	}

	public void unRegisterDropDown(Combo dropdown) {
		registeredDropDowns.remove(dropdown);
	}

	public void modifyText(ModifyEvent event) {
		Combo myCombo = (Combo) event.widget;
		String newText = myCombo.getText();		

		log.debug("newText" + newText);
		
		// Iterate through the registered drop downs of this 
		for (Combo registerdCombo : registeredDropDowns) {
			log.debug("registerdCombo" + registerdCombo);						
			// Clear the old contents first
			registerdCombo.removeAll();
			
			// Get the appropriate map for this Combo
			Map<String, Map> selectableContentsMap = (Map<String, Map>) nestedContents.get(registerdCombo);
			log.debug("selectableContentsMap" + selectableContentsMap);
			Map<String, Map> selectablePropertiesMap = (Map<String, Map>) selectableContentsMap.get(newText);			
			Set<String> keys = selectablePropertiesMap.keySet();						
			int index = 0;
			// Add the new contents
			for (String item : keys) {
				registerdCombo.add(item, index);
				index ++;
			}				
			registerdCombo.select(0);
		}
	}

}
