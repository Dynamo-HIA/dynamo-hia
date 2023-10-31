package nl.rivm.emi.cdm.data.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRegex {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void regExercise() {
		String integerRegex = "\\A\\d+\\z";
		Pattern pattern = Pattern.compile(integerRegex);
		Matcher matcher = pattern.matcher("ab123");
		assertFalse(matcher.matches());
		matcher = pattern.matcher("54123");
		assertTrue(matcher.matches());
	}

	@Test
	public void regExercise2() {
		String floatRegex = "[0-9]+[\\.]?[0-9]*";
		Pattern pattern = Pattern.compile(floatRegex);
		Matcher matcher = pattern.matcher("1,23");
		assertFalse(matcher.matches());
		matcher = pattern.matcher("5.4123");
		assertTrue(matcher.matches());
		matcher = pattern.matcher("12.3");
		assertTrue(matcher.matches());
		matcher = pattern.matcher("1.23");
		assertTrue(matcher.matches());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(nl.rivm.emi.cdm.data.test.TestRegex.class);
	}

}
