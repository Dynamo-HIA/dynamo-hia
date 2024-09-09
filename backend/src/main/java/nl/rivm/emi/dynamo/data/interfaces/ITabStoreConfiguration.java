package nl.rivm.emi.dynamo.data.interfaces;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

public interface ITabStoreConfiguration {

	public void initialize(Object name, ArrayList<AtomicTypeObjectTuple> list);

	public TypedHashMap<? extends XMLTagEntity> putInTypedHashMap(TypedHashMap<? extends XMLTagEntity> theMap);
}