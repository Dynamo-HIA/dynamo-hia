package nl.rivm.emi.cdm.obsolete;

import java.util.HashMap;

import nl.rivm.emi.cdm.individual.IndividualStAXEventsConsumer;

public class StAXFactoryMap extends HashMap<String, NopStAXEventConsumerBase> {
	
	static private StAXFactoryMap instance = null;

	private StAXFactoryMap() {
		super();
		PopulationFromStAXEventsFactory popFactory = new PopulationFromStAXEventsFactory();
//		put(popFactory.getXmlElementName(), popFactory);
//		IndividualStAXEventsConsumer indFactory = new IndividualStAXEventsConsumer();
//		put(indFactory.getXmlElementName(), indFactory);
//		CharacteristicValueFromStAXEventsFactory chFactory = new CharacteristicValueFromStAXEventsFactory();
//		put(chFactory.getXmlElementName(), chFactory);
	}

	synchronized static public NopStAXEventConsumerBase findFactory(String elementName){
		if(instance == null){
			instance = new StAXFactoryMap();
		}
		return instance.get(elementName);
	}
}
