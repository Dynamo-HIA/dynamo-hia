package nl.rivm.emi.dynamo.data.types.atomic.base;

/**
 * @author boshuizh
 * DIT IS Een speciale versie van abstracted ranged integer bedoelt voor RelativeRiskIndex, die er alleen is om er voor te zorgen
 * dat er in het simulatieobject bij de default waarden een leeg xml file wordt aangemaakt In versie 2.06 werkte dat niet meer goed.
 * Reden onduidelijk, heeft mogelijk te maken met toevoegen van de reference scenario naam
 * 
 *
 */
public class AbstractEmptyIndicator extends AbstractRangedInteger {

	public AbstractEmptyIndicator(String XMLElementName, Integer lowerLimit,
			Integer upperLimit) {
		super(XMLElementName, lowerLimit, upperLimit);
		
	}

}
