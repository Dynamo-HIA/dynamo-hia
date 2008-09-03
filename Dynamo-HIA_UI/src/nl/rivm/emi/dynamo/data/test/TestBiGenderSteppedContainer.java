package nl.rivm.emi.dynamo.data.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import junit.framework.JUnit4TestAdapter;

import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestBiGenderSteppedContainer {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testPut() {
		BiGenderSteppedContainer<String> testContainer = new BiGenderSteppedContainer<String>();
		assertNull(testContainer.put(1, null));
		String testObject1 = new String("Een");
		assertEquals(testObject1, testContainer.put(0, testObject1));
		String testObject2 = new String("Twee");
		assertEquals(testObject1, testContainer.put(0, testObject2));
		assertEquals(testObject2, testContainer.get(0));
		assertEquals(testObject2, testContainer.put(0, testObject1));
		assertEquals(testObject1, testContainer.get(0));
	}

	@Test
	public void testPutOutOfBounds1() {
		BiGenderSteppedContainer<String> testContainer = new BiGenderSteppedContainer<String>();
		String testObject1 = new String("Een");
		try{
		testContainer.put(-1, testObject1);
		assertNull(testObject1); // Force error.
		} catch(ArrayIndexOutOfBoundsException e){
			assertNotNull(e);
		}
	}

	@Test
	public void testPutOutOfBounds2() {
		BiGenderSteppedContainer<String> testContainer = new BiGenderSteppedContainer<String>();
		String testObject1 = new String("Een");
		try{
		testContainer.put(5, testObject1);
		assertNull(testObject1); // Force error.
		} catch(ArrayIndexOutOfBoundsException e){
			assertNotNull(e);
		}
	}

	@Test
	public void testGetOutOfBounds() {
		BiGenderSteppedContainer<String> testContainer = new BiGenderSteppedContainer<String>();
		String testObject1 = new String("Een");
		try{
		assertEquals(testObject1, testContainer.put(3, testObject1));
		testContainer.get(5);
		assertNull(testObject1); // Force error.
		} catch(ArrayIndexOutOfBoundsException e){
			assertNotNull(e);
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				TestBiGenderSteppedContainer.class);
	}
}
