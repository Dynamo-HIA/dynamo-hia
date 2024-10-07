package nl.rivm.emi.dynamo.ui.panels.simulation.listeners;

import nl.rivm.emi.dynamo.ui.panels.simulation.RelativeRiskTabDataManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;

/**
 * ModifyListener that is hooked up to all three dropdown-boxes in the active
 * relative risk tab.
 * 
 * @author mondeelr
 * 
 */
public class RelativeRiskComboDebugModifyListener implements ModifyListener {

	private Log log = LogFactory.getLog(this.getClass().getSimpleName());
	private String comboLabel;
	private RelativeRiskTabDataManager dataManager;

	/**
	 * @param relativeRiskTab
	 *            - The tab the listener is working for.
	 * @param helpGroup
	 */
	public RelativeRiskComboDebugModifyListener(
			RelativeRiskTabDataManager dataManager, String comboLabel) {
		super();
		this.dataManager = dataManager;
		this.comboLabel = comboLabel;
	}

	/**
	 * 
	 */
	public void modifyText(ModifyEvent event) {
		synchronized (this) {
			Combo myCombo = (Combo) event.widget;
			try {
				int selectionIndex = myCombo.getSelectionIndex();
				if (selectionIndex != -1) {
					String comboLabel = dataManager.findComboLabel(myCombo);
					log.debug("modifyText from combo with label: " + comboLabel
							+ " selectionindex: " + selectionIndex);
					if (comboLabel != null) {
						String newText = myCombo.getText();
						log.debug("newText: " + newText);
					}
				}
			} catch (Exception e) {
				log.debug(e.getClass().getSimpleName() + " caught, message: "
						+ e.getMessage());
			}
		}
	}
}