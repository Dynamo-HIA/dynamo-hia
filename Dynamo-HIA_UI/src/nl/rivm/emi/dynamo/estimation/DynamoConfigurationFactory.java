package nl.rivm.emi.dynamo.estimation;

import org.apache.commons.configuration.HierarchicalConfiguration;

public interface DynamoConfigurationFactory {

	public String getName(HierarchicalConfiguration W, String tagname);

	public int getIntegervalue(HierarchicalConfiguration W, String tagname);

	public float getFloatvalue(HierarchicalConfiguration W, String tagname);

	public float[][] getAgeSexArray(HierarchicalConfiguration W, String tagname);

	public float[][][] getDoubleAgeSexArray(HierarchicalConfiguration W,
			String tagname1, String tagname2);
}
