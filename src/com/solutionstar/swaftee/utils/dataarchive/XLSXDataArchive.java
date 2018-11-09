package com.solutionstar.swaftee.utils.dataarchive;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Data archive interface for archiving to XLSX (Excel) files. Do not use this 
 * class to save to XLS format.
 * 
 * @author Allen Godfrey
 */
public class XLSXDataArchive extends SpreadsheetDataArchiveBase implements DataArchive {

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
		log.info("Attempting save data to filename: " + filename);

		saveData(new XSSFWorkbook(), filename);

	}
	
	public void saveData(String filename, boolean forceNumbersAsString) throws Exception {
		log.info("Attempting save data to filename: " + filename);

		saveData(new XSSFWorkbook(), filename, forceNumbersAsString);

	}
	
	/**
	 * Retrieve data from the given excel file
	 * 
	 * @param excel filename
	 * @return List<HashMap<String, String>> - All the rows in excel, each row as a hashmap
	 * @throws Exception
	 */
	public List<HashMap<String, String>> retrieveData(String filename,boolean...val) throws Exception
	{
		return retrieveData(new XSSFWorkbook(new FileInputStream(filename)),val);
	}
	
	/**
	 * Retrieve data from the given excel file
	 * 
	 * @param excel filename
	 * @return List<HashMap<String, String>> - All the columns in excel, each column as a hashmap
	 * @throws Exception
	 */
	public List<HashMap<String,String>> retrieveDataInverse(String filename,String... sheetName) throws Exception
	{
		return retrieveDataInverse(new XSSFWorkbook(new FileInputStream(filename)),sheetName);
	}
	
	/**
	 * Retrieve data from the given excel file
	 * 
	 * @param excel filename
	 * @return List<String> - All the rows in excel, each row as a List
	 * @throws Exception
	 */
	public List<String> retrieveHeaderData(String filename) throws Exception {
		// TODO Auto-generated method stub
		return retrieveHeaderData(new XSSFWorkbook(new FileInputStream(filename)));
	}


	public void writeDataToFile(String filename, List<HashMap<String, String>> data) throws Exception
	{
		 writeDataToFile(new XSSFWorkbook(), filename, data);
	}


	@Override
	public void writeDataToFile(String filename, List<HashMap<String, String>> data, String[] header) throws Exception {
		writeDataToFile(new XSSFWorkbook(), filename, data,header);
	}


	@Override
	public void writeDataToFile(String filename, List<HashMap<String, String>> data, String[] header,
			boolean forceNumbersAsString) throws Exception {
		writeDataToFile(new XSSFWorkbook(), filename, data,header,forceNumbersAsString);
		
	}



}


