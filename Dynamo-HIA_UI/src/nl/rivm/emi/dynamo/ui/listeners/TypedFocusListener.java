package nl.rivm.emi.dynamo.ui.listeners;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

public class TypedFocusListener implements FocusListener {
	Log log = LogFactory.getLog(this.getClass().getName());
	XMLTagEntity myType;
	HelpGroup helpGroup;

	public TypedFocusListener(XMLTagEntity myType, HelpGroup helpGroup) {
		super();
		this.myType = myType;
		this.helpGroup = helpGroup;
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		String helpText = myType.getXMLElementName();
log.debug("focusGained for: " + helpText);
		helpGroup.getFieldHelpGroup().setHelpText(helpText);
		}

	@Override
	public void focusLost(FocusEvent arg0) {
		helpGroup.getFieldHelpGroup().setHelpText("Blank");
	}

}