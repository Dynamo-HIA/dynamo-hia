package nl.rivm.emi.dynamo.data.objects;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IMortalityObject;
import nl.rivm.emi.dynamo.data.interfaces.IParameterTypeObject;
import nl.rivm.emi.dynamo.data.interfaces.IUnitTypeObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.ParameterType;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

public class ExcessMortalityObject extends GroupConfigurationObjectServiceLayer
		implements IMortalityObject, IUnitTypeObject, IParameterTypeObject {
	public static class ParameterTypeHelperClass {
		static public final String CHOOSE = ParameterType.CHOOSE;
		static public final String ACUTELY_FATAL = ParameterType.ACUTELY_FATAL;
		static public final String CURED_FRACTION = ParameterType.CURED_FRACTION;
		// Add functionality without breaking other code.
		static public final String[] PARAMETERTYPES = { ACUTELY_FATAL,
				CURED_FRACTION };
		static public String chosenParameterName = null;
	}

	private static final long serialVersionUID = 3681891491526136721L;
	/**
	 * Initialize self and copy content.
	 * 
	 * @param manufacturedMap
	 */

	private Boolean notAllAcutelyFatalsAreZeroAtConstructionTime = null;
	private Boolean notAllCuredFractionsAreZeroAtConstructionTime = null;

	Log log = LogFactory.getLog(this.getClass().getName());

	public ExcessMortalityObject(LinkedHashMap<String, Object> content) {
		super();
		super.putAll(content);
		// If the Object has been constructed from "old" data...
		if (hasNoParameterType()) {
			checkObjectState();
		}
	}

	@Override
	public String getUnitType() {
		return getSingleRootChildStringValue(XMLTagEntityEnum.UNITTYPE
				.getElementName());
	}

	@Override
	public WritableValue getObservableUnitType() throws DynamoConfigurationException {
		return getSingleRootChildWritableValue(XMLTagEntityEnum.UNITTYPE
				.getElementName());
	}

	@Override
	public Object putUnitType(String unitType) {
		return putSingleRootChildStringValue(XMLTagEntityEnum.UNITTYPE
				.getElementName(), unitType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public TypedHashMap<Age> getMortalities() {
		TypedHashMap<Age> wrappedObject = (TypedHashMap<Age>) get(
		/*
		 * Alternative, non-conforming version.
		 */XMLTagEntityEnum.MORTALITIES.getElementName()
		/*
		 * XMLTagEntityEnum.MORTALITY.getElementName()
		 */);
		return wrappedObject;
	}

	@Override
	public WritableValue getObservableParameterType()throws DynamoConfigurationException {	
		WritableValue value=null;		 		
		
			value=getSingleRootChildWritableValue(XMLTagEntityEnum.PARAMETERTYPE
					.getElementName());
		 return value;
	}

	@Override
	public String getParameterType() {
		return getSingleRootChildStringValue(XMLTagEntityEnum.PARAMETERTYPE
				.getElementName());
	}

	@Override
	public Object putParameterType(String parameterType) {
		return putSingleRootChildStringValue(XMLTagEntityEnum.PARAMETERTYPE
				.getElementName(), parameterType);
	}

	public Boolean getNotAllAcutelyFatalsAreZeroAtConstructionTime() {
		return notAllAcutelyFatalsAreZeroAtConstructionTime;
	}

	public Boolean getNotAllCuredFractionsAreZeroAtConstructionTime() {
		return notAllCuredFractionsAreZeroAtConstructionTime;
	}

	public boolean hasNoParameterType() {
		return (this.getParameterType() == null);
	}

	public void insertParameterType(String parameterTypeName,
			boolean zapOtherColumn) {
		boolean stripMortalities = hasNoParameterType();
		TypedHashMap<Age> mortalitiesObject = (TypedHashMap<Age>) get(XMLTagEntityEnum.MORTALITIES
				.getElementName());
		if(zapOtherColumn){
			zapColumn(mortalitiesObject, parameterTypeName);
		}
		// To keep order, first remove last rootchild.
		if (stripMortalities) {
			remove(XMLTagEntityEnum.MORTALITIES.getElementName());
		}
		// Add parameterType
		WritableValue writableValue = new WritableValue(parameterTypeName,
				new String());
		AtomicTypeObjectTuple tuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.PARAMETERTYPE.getTheType(), writableValue);
		put(XMLTagEntityEnum.PARAMETERTYPE.getElementName(), tuple);
		// And add last rootchild.
		if (stripMortalities) {
			put(XMLTagEntityEnum.MORTALITIES.getElementName(),
					mortalitiesObject);
		}
	}

	private void checkObjectState() {
		final Float zeroFloat = new Float(0);
		notAllAcutelyFatalsAreZeroAtConstructionTime = Boolean.FALSE;
		notAllCuredFractionsAreZeroAtConstructionTime = Boolean.FALSE;
		TypedHashMap<Age> mortalitiesMap = getMortalities();
		final int numberOfAges = mortalitiesMap.size();
		for (int ageCount = 0; ageCount < numberOfAges; ageCount++) {
			TypedHashMap<Sex> sexMap = (TypedHashMap<Sex>) mortalitiesMap
					.get(ageCount);
			int numberOfSexes = sexMap.size();
			for (int sexCount = 0; sexCount < numberOfSexes; sexCount++) {
				ArrayList<AtomicTypeObjectTuple> arrayList = (ArrayList<AtomicTypeObjectTuple>) sexMap
						.get(sexCount);
				for (int paramCount = 0; paramCount < arrayList.size(); paramCount++) {
					// Check until one non-zero found.
					if ((paramCount == 1)
							&& (notAllAcutelyFatalsAreZeroAtConstructionTime
									.equals(Boolean.FALSE))) {
						AtomicTypeObjectTuple tuple = arrayList.get(paramCount);
						WritableValue observableClassName = (WritableValue) tuple
								.getValue();
						Float acutelyFatalFloatValue = (Float) observableClassName
								.doGetValue();
						if (zeroFloat.compareTo(acutelyFatalFloatValue) != 0) {
							notAllAcutelyFatalsAreZeroAtConstructionTime = Boolean.TRUE;
						}
					}
					// Check until one non-zero found.
					if ((paramCount == 2)
							&& (notAllCuredFractionsAreZeroAtConstructionTime
									.equals(Boolean.FALSE))) {
						AtomicTypeObjectTuple tuple = arrayList.get(paramCount);
						WritableValue observableClassName = (WritableValue) tuple
								.getValue();
						Float curedFractionFloatValue = (Float) observableClassName
								.doGetValue();
						if (zeroFloat.compareTo(curedFractionFloatValue) != 0) {
							notAllCuredFractionsAreZeroAtConstructionTime = Boolean.TRUE;
						}
					}
				}
			}
		}
	}

	private void zapColumn(TypedHashMap<Age> mortalitiesMap,
			String parameterTypeName2Zap) {
		final Float zeroFloat = new Float(0);
		final int numberOfAges = mortalitiesMap.size();
		for (int ageCount = 0; ageCount < numberOfAges; ageCount++) {
			TypedHashMap<Sex> sexMap = (TypedHashMap<Sex>) mortalitiesMap
					.get(ageCount);
			int numberOfSexes = sexMap.size();
			for (int sexCount = 0; sexCount < numberOfSexes; sexCount++) {
				ArrayList<AtomicTypeObjectTuple> arrayList = (ArrayList<AtomicTypeObjectTuple>) sexMap
						.get(sexCount);
				for (int paramCount = 0; paramCount < arrayList.size(); paramCount++) {
					if ((paramCount == 1)
							&& ParameterTypeHelperClass.CURED_FRACTION.equals(parameterTypeName2Zap)){
						AtomicTypeObjectTuple tuple = arrayList.get(paramCount);
						WritableValue observableClassName = (WritableValue) tuple
								.getValue();
						observableClassName
								.doSetValue(zeroFloat);
					}
					if ((paramCount == 2)
							&& ParameterTypeHelperClass.ACUTELY_FATAL.equals(parameterTypeName2Zap)){
						AtomicTypeObjectTuple tuple = arrayList.get(paramCount);
						WritableValue observableClassName = (WritableValue) tuple
								.getValue();
						observableClassName
								.doSetValue(zeroFloat);
					}
				}
			}
		}
	}

}
