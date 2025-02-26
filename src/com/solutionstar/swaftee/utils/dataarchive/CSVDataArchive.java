package com.solutionstar.swaftee.utils.dataarchive;

import java.util.*;

import org.apache.log4j.*;

/**
 * Data archive interface for archiving data in CSV (comma separated value)
 * output.
 * 
 * This class always creates a new file, overwriting any pre-existing file.
 * 
 * @author Allen Godfrey
 */
public class CSVDataArchive extends DelimitedDataArchiveBase implements DataArchive {

	/**
	 * The comma is used as the delimiter for csv files
	 */
	protected final String DELIMITER = ",";

	/**
	 * logging object
	 */
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(CSVDataArchive.class);

	/**
	 * Default constructor.
	 */
	public CSVDataArchive() {
	}

	/**
	 * Save the data to a file. Creates a new file, overwriting any pre-existing
	 * file
	 * 
	 * @param filename
	 * 
	 * @throws Exception
	 */
	public void saveData(String filename) throws Exception {
		saveDataCreateNewFile(filename, DELIMITER);
	}

	public void saveData(String filename, boolean forceNumbersAsString) throws Exception {
		saveDataCreateNewFile(filename, DELIMITER);
	}

	public void writeDataToFile(String filename, List<HashMap<String, String>> data) throws Exception {
		if (data.size() == 0) {
			return;
		}

		HashMap<String, String> map = data.get(0);
		Set<String> header = map.keySet();
		writeDataToFile(filename, data, header.toArray(new String[header.size()]));
	}

	public void writeDataToFile(String filename, List<HashMap<String, String>> data, String[] header) throws Exception {
		writeDataToFile(filename, data, header, false);
	}

	public void writeDataToFile(String filename, List<HashMap<String, String>> data, String[] header,
			boolean forceNumbersAsString) throws Exception {
		addData(header);
		for (HashMap<String, String> map : data) {
			String[] row = new String[header.length];
			for (int i = 0; i < header.length; i++) {
				row[i] = "\"" + map.get(header[i]) + "\"";
			}
			addData(row);
		}
		saveData(filename, forceNumbersAsString);
	}

}
