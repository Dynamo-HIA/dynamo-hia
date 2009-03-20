package nl.rivm.emi.dynamo.data.util;

import java.util.ArrayList;
import java.util.List;

import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LeafNodeList extends ArrayList<AtomicTypeObjectTuple> {
	private static final long serialVersionUID = 4381230502193758915L;
	private Log log = LogFactory.getLog(this.getClass().getName());
private int theLastContainer = -1;

	public int fill(ConfigurationNode rootChild) throws ConfigurationException {
		theLastContainer = 0;
		List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
				.getChildren();
		XMLTagEntitySingleton atomicTypesSingleton = XMLTagEntitySingleton
				.getInstance();
		for (ConfigurationNode leafChild : leafChildren) {
			log.debug("Handle leafChild: " + leafChild.getName());
			String leafName = leafChild.getName();
			String valueString = (String) leafChild.getValue();
			AtomicTypeBase leafAtomicType = (AtomicTypeBase) atomicTypesSingleton
					.get(leafName);
			if (leafAtomicType != null) {
				Object leafDataType = leafAtomicType.getType();
				Number valueNumber;
				if (leafDataType.equals(Integer.class)) {
					valueNumber = Integer.parseInt(valueString);
				} else {
					if (leafDataType.equals(Float.class)) {
						valueNumber = Float.parseFloat(valueString);
					} else {
						if (leafDataType.equals(String.class)) {

							valueNumber = null;
						} else {
							throw new ConfigurationException(
									"Unsupported data type "
											+ leafDataType.getClass().getName());
						}
					}
				}
				if (leafAtomicType instanceof ContainerType) {
					theLastContainer++;
				}
				if (valueNumber != null) {
					log.debug("Adding type: "
							+ leafAtomicType.getClass().getName()
							+ " with value: " + valueNumber);
					add(new AtomicTypeObjectTuple(leafAtomicType, valueNumber));
				} else {
					log.debug("Adding type: "
							+ leafAtomicType.getClass().getName()
							+ " with string: " + valueString);
					add(new AtomicTypeObjectTuple(leafAtomicType, valueString));
				}
			} else {
				throw new ConfigurationException("Unexpected tag: " + leafName);
			}

		} // for leafChildren
		theLastContainer = checkContents();
		return theLastContainer;
	}

	public String report() {
		StringBuffer resultStringBuffer = new StringBuffer(", listlength "
				+ size() + " Values ");
		for (int count = 0; count < size(); count++) {
			resultStringBuffer.append((get(count)).getValue() + " * ");
		}
		return resultStringBuffer.toString();
	}

	public int checkContents() throws ConfigurationException {
		int theLastContainer = 0;
		log.debug("leafNodeList.LeafNodeList.size()" + this.size());
		for (; theLastContainer < this.size(); theLastContainer++) {
			if (!(get(theLastContainer).getType() instanceof ContainerType)) {
				log.debug("theLastContainer" + theLastContainer);
				break;
			}
		}
		if (theLastContainer == 0) {
			throw new ConfigurationException(
					"Supporting only XML with at least one dimension (eg. age) for now. LastContainer "
							+ theLastContainer + report());
		} else {
//			if (theLastContainer != size() - 1) {
//				throw new ConfigurationException(
//						"Supporting XML with single value only for now. LastContainer "
//								+ theLastContainer + report());
//			} else {
				log.debug("Handling rootchild. LastContainer "
						+ theLastContainer + report());
			}
//		}
		return theLastContainer;
	}

	public int getTheLastContainer() {
		return theLastContainer;
	}
}
