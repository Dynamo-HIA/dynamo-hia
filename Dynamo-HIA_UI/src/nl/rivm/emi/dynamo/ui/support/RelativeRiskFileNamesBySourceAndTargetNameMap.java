package nl.rivm.emi.dynamo.ui.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelativeRiskFileNamesBySourceAndTargetNameMap extends
		HashMap<String, HashMap<String, Set<String>>> {
	private static final long serialVersionUID = -8043227940482790204L;

	private Log log = LogFactory.getLog(this.getClass().getName());

	public boolean add(String sourceName, String targetName,
			String relativeRiskFileName) {
		boolean hasBeenAdded = false;
		log.debug("Adding: " + sourceName + " - " + targetName + " - "
				+ relativeRiskFileName);
		Set<String> relRiskFileNameSet = null;
		HashMap<String, Set<String>> relRiskFileNameByTargetNameMap = get(sourceName);
		if (relRiskFileNameByTargetNameMap == null) {
			relRiskFileNameByTargetNameMap = new HashMap<String, Set<String>>();
			put(sourceName, relRiskFileNameByTargetNameMap);
			relRiskFileNameSet = new HashSet<String>();
			relRiskFileNameByTargetNameMap.put(targetName, relRiskFileNameSet);
		} else {
			relRiskFileNameSet = relRiskFileNameByTargetNameMap.get(targetName);
			if (relRiskFileNameSet == null) {
				relRiskFileNameSet = new HashSet<String>();
				relRiskFileNameByTargetNameMap.put(targetName,
						relRiskFileNameSet);
			}
		}
		hasBeenAdded = relRiskFileNameSet.add(relativeRiskFileName);
		return hasBeenAdded;
	}

	public String dump4Log() {
		StringBuffer dumpBuffer = new StringBuffer();
		Set<String> sourceNamesSet = keySet();
		if (sourceNamesSet.isEmpty()) {
			dumpBuffer.append("No relative risks present.");
		} else {
			for (String sourceName : sourceNamesSet) {
				dumpBuffer.append("\n\t" + "sourceName: " + sourceName);
				HashMap<String, Set<String>> relRiskFileNameByTargetNameMap = get(sourceName);
				Set<String> targetNamesSet = relRiskFileNameByTargetNameMap
						.keySet();
				if (targetNamesSet.isEmpty()) {
					dumpBuffer.append("No targetNames present.");
				} else {
					for (String targetName : targetNamesSet) {
						dumpBuffer.append("\n\t\t" + "targetName: "
								+ targetName);
						Set<String> relRiskFileNameSet = relRiskFileNameByTargetNameMap
								.get(targetName);
						for (String relRiskFileName : relRiskFileNameSet) {
							dumpBuffer.append("\n\t\t\t" + "relRiskFileName: "
									+ relRiskFileName);
						}
					}
				}
			}
		}
		return dumpBuffer.toString();
	}
}
