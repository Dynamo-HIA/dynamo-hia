package nl.rivm.emi.dynamo.data.objects.layers;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;

/**
 * BaseClass for all Dynamo-HIA configuration Objects. Each type is based on a
 * unique rootElementName.
 * 
 * @author mondeelr
 * 
 */
public abstract class ConfigurationObjectBase extends DualModeObjectBase{
	protected RootElementNamesEnum rootElement;

	protected ConfigurationObjectBase(RootElementNamesEnum rootElement,
			boolean observable) {
		super(observable, rootElement.getNodeLabel());
		this.rootElement = rootElement;
	}

	/**
	 * @return The name of the rootelement in the corresponding file.
	 */
	public RootElementNamesEnum getRootElement() {
		return rootElement;
	}
}
