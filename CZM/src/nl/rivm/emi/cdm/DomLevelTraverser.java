package nl.rivm.emi.cdm;

import java.util.TreeSet;

import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.simulation.CZMConfigurationException;

import org.w3c.dom.Node;

public abstract class DomLevelTraverser extends TreeSet<Individual>{
 
	protected String myElementName = "Skblzz";
	
	protected Node traverseTreeLevel(Node node) throws CZMConfigurationException {
		while (node != null) {
			if (isMyElementName(node)) {
				Population population = new Population(node);
				setPopulation(population);
			}
			node = node.getNextSibling();
		}
		return node;
	}

	public boolean isMyElementName(Node node) {
		return myElementName.equals(node.getNodeName());
	}



}
