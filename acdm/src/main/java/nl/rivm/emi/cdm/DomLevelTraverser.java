package nl.rivm.emi.cdm;

import java.util.TreeSet;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.individual.Individual;

import org.w3c.dom.Node;

public abstract class DomLevelTraverser extends TreeSet<Individual>{
 
	/**
	 * serial number not used
	 */
	private static final long serialVersionUID = 21L;
	protected String myElementName = "Skblzz";
	
	protected Node traverseTreeLevel(Node node) throws CDMConfigurationException {
		while (node != null) {
			if (isMyElementName(node)) {
//				Population population = new Population(node);
//				setPopulation(population);
			}
			node = node.getNextSibling();
		}
		return node;
	}

	public boolean isMyElementName(Node node) {
		return myElementName.equals(node.getNodeName());
	}



}
