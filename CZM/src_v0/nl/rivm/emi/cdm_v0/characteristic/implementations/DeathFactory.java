package nl.rivm.emi.cdm_v0.characteristic.implementations;

import java.io.IOException;
import java.util.ArrayList;

import nl.rivm.emi.cdm_v0.characteristic.Characteristic;
import nl.rivm.emi.cdm_v0.characteristic.CharacteristicFactory;
import nl.rivm.emi.cdm_v0.characteristic.SymptomRange;
import nl.rivm.emi.cdm_v0.characteristic.SymptomRangesForAGender;
import nl.rivm.emi.cdm_v0.inputdata.cbs.MortalityCSVImporter;
import nl.rivm.emi.cdm_v0.parameter.ParameterInTimeArray;
import nl.rivm.emi.cdm_v0.parameter.Parameter_0_105plus_step1_ones;
import nl.rivm.emi.cdm_v0.parameter.Parameter_0_105plus_step1_zeroes;
import nl.rivm.emi.cdm_v0.state.IndividualConfiguration;
import nl.rivm.emi.cdm_v0.time.TimeBase_0_105plus_step1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DeathFactory extends CharacteristicFactory {

	Log log = LogFactory.getLog(IndividualConfiguration.class.getName());

	@Override
	public Characteristic create() {
		log.info("Creating death.");
		Characteristic death = new Characteristic();
		SymptomRangesForAGender femaleSymptomRanges = new SymptomRangesForAGender();
		death.addSymptomRangeForAGender(Characteristic.FEMALE_INDEX,
				femaleSymptomRanges);
		SymptomRangesForAGender maleSymptomRanges = new SymptomRangesForAGender();
		death.addSymptomRangeForAGender(Characteristic.MALE_INDEX,
				maleSymptomRanges);
		// Same for both.
		SymptomRange deadRange = createDeadRange();
		deadRange.setIndex(1);
		deadRange.setLabel("Dead");
		femaleSymptomRanges.addSymptomRange(deadRange);
		maleSymptomRanges.addSymptomRange(deadRange);
		SymptomRange femaleAliveRange = createAliveRange(Characteristic.FEMALE_INDEX);
		femaleAliveRange.setIndex(0);
		femaleAliveRange.setLabel("Alive");
		femaleSymptomRanges.addSymptomRange(femaleAliveRange);
		SymptomRange maleAliveRange = createAliveRange(Characteristic.MALE_INDEX);
		maleAliveRange.setIndex(0);
		maleAliveRange.setLabel("Alive");
		maleSymptomRanges.addSymptomRange(maleAliveRange);
		return death;
	}

	private SymptomRange createAliveRange(int genderIndex) {
		SymptomRange aliveRange = new SymptomRange(0, TimeBase_0_105plus_step1
				.getInstance());
		ArrayList<ArrayList> dataContainer;
		// TODO
		aliveRange.setPrevalenceRate(null);

		try {
			dataContainer = MortalityCSVImporter
					.importFile("C:\\eclipse321\\workspace\\CZM\\data\\CBS_download_sterftequotienten-leeftijd-geslacht.csv");
			ArrayList<Float> parameters = dataContainer.get(genderIndex + 1);
			ParameterInTimeArray transitionRates = new ParameterInTimeArray(
					parameters);
			ParameterInTimeArray complementaryTransitionRates = ParameterInTimeArray
					.generateComplement(transitionRates);
			aliveRange.setTransitionRate(0, Parameter_0_105plus_step1_zeroes
					.getInstance());
			aliveRange.setTransitionRate(1, Parameter_0_105plus_step1_ones
					.getInstance());
			return aliveRange;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private SymptomRange createDeadRange() {
		SymptomRange deadRange = new SymptomRange(1, TimeBase_0_105plus_step1
				.getInstance());
		// TODO
		deadRange.setPrevalenceRate(null);
		// No zombies.
		deadRange.setTransitionRate(0, Parameter_0_105plus_step1_zeroes
				.getInstance());
		deadRange.setTransitionRate(1, Parameter_0_105plus_step1_ones
				.getInstance());
		return deadRange;
	}

}
