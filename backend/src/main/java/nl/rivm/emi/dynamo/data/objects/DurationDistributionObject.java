package nl.rivm.emi.dynamo.data.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.CatContainer;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;

import org.eclipse.core.databinding.observable.value.WritableValue;

public class DurationDistributionObject extends TypedHashMap<Age> implements StandardObjectMarker, ISanityCheck {
	private static final long serialVersionUID = 559843387625531336L;
	StringBuffer checkList = new StringBuffer("No check done.");

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public DurationDistributionObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)XMLTagEntityEnum.AGE.getTheType());
			 putAll(manufacturedMap);
		}
		@SuppressWarnings("unchecked")
		@Override
		public boolean dataChecksOut() {
			boolean soFarSoGood = true;
			checkList = new StringBuffer(
					"Warning, problems found:\n(Expected sums: 100 (+/- 0.1)).\n");
			boolean cutShort = false;
			Set<Object> ageSet = this.keySet();
			Iterator<Object> ageIterator = ageSet.iterator();
			while (ageIterator.hasNext()) {
				Object age = ageIterator.next();
				TypedHashMap<Sex> sexMap = (TypedHashMap<Sex>) get(age);
				Set<Object> sexSet = sexMap.keySet();
				Iterator<Object> sexIterator = sexSet.iterator();
				while (sexIterator.hasNext()) {
					Object sex = sexIterator.next();
					TypedHashMap<CatContainer> catMap = (TypedHashMap<CatContainer>) sexMap
							.get(sex);
					Set<Object> catSet = catMap.keySet();
					Iterator<Object> catIterator = catSet.iterator();
					float prevalenceSum = 0F;
					while (catIterator.hasNext()) {
						Object cat = catIterator.next();
						Object valueObject = catMap.get(cat);
						WritableValue writableValue = null;
						if (valueObject instanceof LeafNodeList) {
							LeafNodeList value = (LeafNodeList) valueObject;
							writableValue = (WritableValue) value.get(0).getValue();
						} else {
							ArrayList<AtomicTypeObjectTuple> arrayValue = (ArrayList<AtomicTypeObjectTuple>) valueObject;
							AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) arrayValue
									.get(0);
							writableValue = (WritableValue) tuple.getValue();
						}
						Float prev = (Float) writableValue.doGetValue();
						prevalenceSum += prev.floatValue();
					}
					if (!cutShort &&(Math.abs(100F - prevalenceSum) > 0.1F)){
						checkList.append("For age: "
								+ age
								+ " and sex: "
								+ ((((Integer) sex).intValue() == BiGender.MALE_INDEX) ? "Male"
										: "Female") + " the sum is: "
								+ prevalenceSum + "\n");
						soFarSoGood = false;
						if(checkList.length() > 1000){
							cutShort = true;
							checkList.append("More errors found......" + "\n");
						}
					}
				}
			}
			return soFarSoGood;
		}

		@Override
		public String getCheckList() {
			return checkList.toString();
		}

}
