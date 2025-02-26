package com.solutionstar.swaftee.utils.dataarchive;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.solutionstar.swaftee.utils.*;

/**
 * Data archive base class for archiving to any kind of delimited files.
 * 
 * @author Allen Godfrey
 */
public class DelimitedDataArchiveBase extends DataArchiveBase {

	/**
	 * logging object
	 */
	private static Logger logger = Logger.getLogger(DelimitedDataArchiveBase.class);

	private CSVParserUtils csvUtils = new CSVParserUtils();

	/**
	 * Save the data to a file.
	 * 
	 * @param filename  The filename to save the data to
	 * @param append    To append to existing file or create a new file
	 * @param delimiter the delimiter (text separator) to use
	 * 
	 * @throws Exception
	 */
	public void saveData(String filename, boolean append, String delimiter) throws Exception {

		PrintWriter writer = new PrintWriter(new FileWriter(new File(filename), append));

		for (int i = 0; i < list.size(); i++) {

			String[] data = list.get(i);

			for (int j = 0; j < data.length; j++)
				writer.print(data[j] + delimiter);

			writer.print(System.lineSeparator());

		}

		writer.close();
		clearData();

		writer = null;

	}

	/**
	 * Save the data to a file. Creates a new file, overwriting any pre-existing
	 * file
	 * 
	 * @param filename
	 * @param delimiter the delimiter (text separator) to use
	 * 
	 * @throws Exception
	 */
	public void saveDataCreateNewFile(String filename, String delimiter) throws Exception {

		logger.debug("Attempting save data to filename in csv format (create new file): " + filename);

		saveData(filename, false, delimiter);

	}

	/**
	 * Save the data to a file. Appends to the pre-existing file.
	 * 
	 * @param filename
	 * @param delimiter the delimiter (text separator) to use
	 * 
	 * @throws Exception
	 */
	public void saveDataAppendToFile(String filename, String delimiter) throws Exception {

		logger.debug("Attempting save data to filename in csv format (append to file): " + filename);

		saveData(filename, true, delimiter);

	}

	public List<HashMap<String, String>> retrieveData(String fileName, boolean... val) {
		return csvUtils.getDataFromCSV(fileName);
	}

}
