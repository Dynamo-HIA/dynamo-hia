package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractEmptyIndicator;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;


/**
 * Nonnegative Integer without fixed upper limit. This to enable adjustment to
 * the range of categories the transitions can cover.
 */
/* jan 2015 veranderd in abstractstring type omdat dit er voor zorgt dat er een lege RR wordt aangemaakt in een nieuwe simulatie
 * Bij AbstractRangedInteger worden er lege velden aangemaakt die vervolgens gevuld worden en foutmeldingen generenen
 * Onduidelijk waarom dit in eerdere versies van het model niet tot problemen heeft geleid
 * is toch niets veranderd aan de RR , alleen is er wel referencescenarionaam toegevoegd, maar dat kan toch geen
 * problemen hebben gegeven
 * 
 * 
 * dit werkte niet want dit type wordt gebruikt 
 * Daarom nu extra type bedacht om te gebruiken als type dat indicator is voor het aanmaken van een leeg object
 */
public class RelativeRiskIndex extends    AbstractEmptyIndicator implements ContainerType<Integer> {
	static final protected String XMLElementName = "RRindex";

	public RelativeRiskIndex(){
		super(XMLElementName, 0, Integer.MAX_VALUE);
	
	}

	/**
	 * No default instances for now.
	 */
	//public Integer getMaxNumberOfDefaultValues() throws ConfigurationException{
//		return getMIN_VALUE();
	//}
}
