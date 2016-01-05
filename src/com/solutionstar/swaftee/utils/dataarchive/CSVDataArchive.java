package com.solutionstar.swaftee.utils.dataarchive;

import org.apache.log4j.Logger;

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
	 *  logging object
	 */
	private static Logger log = Logger.getLogger(CSVDataArchive.class);

	/**
	 * Default constructor.
	 */
	public CSVDataArchive() { }


	/**
	 * Save the data to a file. Creates a new file, overwriting any 
	 * pre-existing file
	 * 
	 * @param filename
	 * 
	 * @throws Exception 
	 */
	public void saveData(String filename) throws Exception 
	{    
		saveDataCreateNewFile(filename, DELIMITER); 
	}
	
	public void saveData(String filename, boolean forceNumbersAsString) throws Exception 
	{    
		saveDataCreateNewFile(filename, DELIMITER); 
	}
	
}

