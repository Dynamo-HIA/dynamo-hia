package nl.rivm.emi.dynamo.ui.panels.listeners;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

public class TypedFocusListener implements FocusListener {
	XMLTagEntity myType;
	HelpGroup helpGroup;

	public TypedFocusListener(XMLTagEntity myType, HelpGroup helpGroup) {
		super();
		this.myType = myType;
		this.helpGroup = helpGroup;
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		helpGroup.getFieldHelpGroup().setHelpText(myType.getXMLElementName());
		}

	@Override
	public void focusLost(FocusEvent arg0) {
		helpGroup.getFieldHelpGroup().setHelpText("Blank");
	}

}
