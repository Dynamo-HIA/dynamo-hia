package nl.rivm.emi.dynamo.data.objects.layers;

import nl.rivm.emi.dynamo.data.factories.XMLHandlingEntryPoint;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;

public abstract class StaxWriterEntryPoint extends XMLHandlingEntryPoint {

	protected StaxWriterEntryPoint(RootElementNamesEnum rootElement, boolean observable){
		super(rootElement, observable);
	}

	// read
	// write
}
