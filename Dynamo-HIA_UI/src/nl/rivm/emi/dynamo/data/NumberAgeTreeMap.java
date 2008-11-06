package nl.rivm.emi.dynamo.data;

import java.util.TreeMap;

abstract public class NumberAgeTreeMap extends TreeMap<Number, Object>{

	abstract protected Object get(Number arg0);

}