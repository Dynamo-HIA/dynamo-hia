package nl.rivm.emi.cdm.csv.file.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.csvreader.CsvReader;

public class TestCsvReader {

	Log log = LogFactory.getLog(getClass().getName());

	String projectBaseDir = System.getProperty("user.dir");

	String testDataFileBasePath = projectBaseDir + File.separator
			+ "unittestdata" + File.separator + "csvreader" + File.separator;


	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void readProductsCsv() {
	CsvReader reader;
	try {
		reader = new CsvReader(testDataFileBasePath + "products.csv");

	reader.readHeaders();

	while (reader.readRecord())
	{
		String productID = reader.get("ProductID");
		String productName = reader.get("ProductName");
		String supplierID = reader.get("SupplierID");
		String categoryID = reader.get("CategoryID");
		String quantityPerUnit = reader.get("QuantityPerUnit");
		String unitPrice = reader.get("UnitPrice");
		String unitsInStock = reader.get("UnitsInStock");
		String unitsOnOrder = reader.get("UnitsOnOrder");
		String reorderLevel = reader.get("ReorderLevel");
		String discontinued = reader.get("Discontinued");
		
		log.info("ProductID: " + productID);
		log.info("ProductName: " + productName);
		log.info("SupplierID: " + supplierID);
		log.info("CategoryID: " + categoryID);
		log.info("QuantityPerUnit: " + quantityPerUnit);
		log.info("UnitPrice: " + unitPrice);
		log.info("UnitsInStock: " + unitsInStock);
		log.info("UnitsOnOrder: " + unitsOnOrder);
		log.info("ReorderLevel: " + reorderLevel);
		log.info("Discontinued: " + discontinued);
	}

	reader.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public static junit.framework.Test suite() {
	return new JUnit4TestAdapter(nl.rivm.emi.cdm.csv.file.test.TestCsvReader.class);
}
}
