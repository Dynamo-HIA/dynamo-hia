package nl.rivm.emi.dynamo.data.objects.layers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.XMLHandlingEntryPoint;
<<<<<<< .mine
import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceClass;
=======
>>>>>>> .r294
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;

public abstract class StaxWriterEntryPoint extends XMLHandlingEntryPoint {

	protected StaxWriterEntryPoint(RootElementNamesEnum rootElement, boolean observable){
		super(rootElement, observable);
	}

	// read
	// write
}
