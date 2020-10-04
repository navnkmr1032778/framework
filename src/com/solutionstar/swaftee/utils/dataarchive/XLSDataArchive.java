package com.solutionstar.swaftee.utils.dataarchive;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Data archive interface for archiving to XLS (Excel) files. Do not use this
 * class to save to XLSX format.
 * 
 * @author Allen Godfrey
 */
public class XLSDataArchive extends SpreadsheetDataArchiveBase implements DataArchive {

	/**
	 * logging object
	 */
	private static Logger log = Logger.getLogger(XLSDataArchive.class);

	/**
	 * Default constructor.
	 */
	public XLSDataArchive() {
	}

	/**
	 * Save the data to a file.
	 * 
	 * @param filename
	 * 
	 * @throws Exception
	 */
	public void saveData(String filename) throws Exception {
		log.info("Attempting save data to filename: " + filename);

		saveData(new HSSFWorkbook(), filename);

	}

	public void saveData(String filename, boolean forceNumbersAsString) throws Exception {
		log.info("Attempting save data to filename: " + filename);

		saveData(new HSSFWorkbook(), filename, forceNumbersAsString);

	}

	/**
	 * Retrieve data from the given excel file
	 * 
	 * @param excel filename
	 * @return List<HashMap<String, String>> - All the rows in excel, each row as a
	 *         hashmap
	 * @throws Exception
	 */
	public List<HashMap<String, String>> retrieveData(String filename, boolean... value) throws Exception {
		return retrieveData(new HSSFWorkbook(new FileInputStream(filename)), value);
	}

	public void writeDataToFile(String filename, List<HashMap<String, String>> data) throws Exception {
		writeDataToFile(new HSSFWorkbook(), filename, data);
	}

	@Override
	public void writeDataToFile(String filename, List<HashMap<String, String>> data, String[] header) throws Exception {
		writeDataToFile(new HSSFWorkbook(), filename, data, header);
	}

	@Override
	public void writeDataToFile(String filename, List<HashMap<String, String>> data, String[] header,
			boolean forceNumbersAsString) throws Exception {
		writeDataToFile(new HSSFWorkbook(), filename, data, header, forceNumbersAsString);

	}

}
