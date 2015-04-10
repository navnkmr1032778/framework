package com.solutionstar.swaftee.utils.dataarchive;

import org.apache.log4j.Logger;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Data archive interface for archiving to XLSX (Excel) files. Do not use this 
 * class to save to XLS format.
 * 
 * @author Allen Godfrey
 */
public class XLSXDataArchive extends XLSDataArchiveBase implements DataArchive {

	/**
	 *  logging object
	 */
	private static Logger log = Logger.getLogger(XLSXDataArchive.class);

	/**
	 * Default constructor.
	 */
	public XLSXDataArchive() { }


	/**
	 * Save the data to a file.
	 * 
	 * @param filename
	 * 
	 * @throws Exception 
	 */
	public void saveData(String filename) throws Exception {
		log.debug("Attempting save data to filename: " + filename);

		saveData(new XSSFWorkbook(), filename);

	}

}


