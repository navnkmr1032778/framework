package com.solutionstar.swaftee.utils;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.solutionstar.swaftee.CustomExceptions.MyCoreExceptions;
import com.solutionstar.swaftee.constants.WebDriverConstants;

public class CSVParserUtils {
	
	protected static Logger logger = LoggerFactory.getLogger(CSVParserUtils.class.getName());
	
	public HashMap<String, String[]> csvDataHash;
	public HashMap<String, String> csvColumnIndexHash;
	public Object[][] csvDataArray;
	public boolean headerRow = true;
	
	CommonUtils utils ;
	
	public void initializeConstans()
	{
		utils = new CommonUtils();
		csvDataHash = new HashMap<String, String[]>();
		csvColumnIndexHash = new HashMap<String,String>();
		headerRow = true;
	}

	public HashMap<String, String[]> getCSVDataHash(String fileName)
	{
		try{
			initializeConstans();
			 if(utils == null)
				 logger.warn("Utils obj is null");
			 
			 CSVReader reader = new CSVReader(new FileReader(utils.getCurrentWorkingDirectory() + WebDriverConstants.PATH_TO_TEST_DATA_FILE + fileName + ".csv"));
			 List<String[]> rowEntries = reader.readAll();
			 for(String[] row : rowEntries)
			 {
				 if(headerRow)
				 {
					 createCSVHeaderHash(row);
					 headerRow = false;
				 }
				 else
					 csvDataHash.put(row[0], row);
			 }
			 reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return csvDataHash;
	}

	@SuppressWarnings("resource")
	public HashMap<String, String[]> getCSVDataHash(String fileName, int columnNumber)
	{
		try{
			 initializeConstans();
			 CSVReader reader = new CSVReader(new FileReader(utils.getCurrentWorkingDirectory() + WebDriverConstants.PATH_TO_TEST_DATA_FILE + fileName + ".csv"));
			 List<String[]> rowEntries = reader.readAll();
			 
			 if(rowEntries.get(0).length < columnNumber)
				 throw new MyCoreExceptions("Column Number Provided is out of data range in the file given");
			 
			 for(String[] row : rowEntries)
			 { 
				 if(headerRow)
				 {
					 createCSVHeaderHash(row);
					 headerRow = false;
				 }
				 else
					 csvDataHash.put(row[columnNumber], row);
			 }
			 reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return csvDataHash;
	}
	
	@SuppressWarnings("resource")
	public HashMap<String, String[]> getCSVDataHash(String fileName, String columnName)
	{
		try{
			 initializeConstans();
			 int columnNumber = -1;
			 CSVReader reader = new CSVReader(new FileReader(utils.getCurrentWorkingDirectory() + WebDriverConstants.PATH_TO_TEST_DATA_FILE + fileName + ".csv"));
			 List<String[]> rowEntries = reader.readAll();
			 
			 columnNumber = Arrays.asList(rowEntries.get(0)).indexOf(columnName);
			 
			 if(rowEntries.get(0).length < columnNumber)
				 throw new MyCoreExceptions("Column Number Provided is out of data range in the file given");
			 
			 for(String[] row : rowEntries)
			 {
				 if(headerRow)
				 {
					 createCSVHeaderHash(row);
					 headerRow = false;
				 }
				 else
					 csvDataHash.put(row[columnNumber], row);
			 }
			 reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return csvDataHash;
	}
	
	@SuppressWarnings("resource")
	public HashMap<String, String[]> getCSVDataHash(String fileName, String[] keyArray)
	{
		try{
			 initializeConstans();
			 Integer[] columnNumber = new Integer[keyArray.length] ;
			 CSVReader reader = new CSVReader(new FileReader(utils.getCurrentWorkingDirectory() + WebDriverConstants.PATH_TO_TEST_DATA_FILE + fileName + ".csv"));
			 List<String[]> rowEntries = reader.readAll();
			 
			 for(int i=0; i< keyArray.length; i++)
			 {
				 columnNumber[i] = Arrays.asList(rowEntries.get(0)).indexOf(keyArray[i]);
				 if(rowEntries.get(0).length < columnNumber[i])
					 throw new MyCoreExceptions("Column Number Provided is out of data range in the file given");
			 }		
			 
			 for(String[] row : rowEntries)
			 {
				 if(headerRow)
				 {
					 createCSVHeaderHash(row);
					 headerRow = false;
				 }
				 else
				 {
					 String hashKey = "";
					 hashKey =  row[Arrays.asList(rowEntries.get(0)).indexOf(keyArray[0])];
					 for(int i=1; i < keyArray.length; i++)
					 {
						 hashKey = hashKey + "-"+ row[Arrays.asList(rowEntries.get(0)).indexOf(keyArray[i])];
					 }
					 csvDataHash.put(hashKey, row); 
				 }					 					 
			 }
			 reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return csvDataHash;
	}
	
	public HashMap<String, String[]> getCSVDataHash(String fileName, Integer[] keyArray)
	{
		try{
			 initializeConstans();
			 CSVReader reader = new CSVReader(new FileReader(utils.getCurrentWorkingDirectory() + WebDriverConstants.PATH_TO_TEST_DATA_FILE + fileName + ".csv"));
			 List<String[]> rowEntries = reader.readAll();
			
			 for(String[] row : rowEntries)
			 {
				 if(headerRow)
				 {
					 createCSVHeaderHash(row);
					 headerRow = false;
				 }
				 else
				 {
					 String hashKey = "";
					 hashKey =  row[keyArray[0]];
					 for(int i=1; i < keyArray.length; i++)
					 {
						 hashKey = hashKey + "-"+ row[keyArray[i]-1];
					 }
					 csvDataHash.put(hashKey, row); 
				 }					 					 
			 }
			 reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return csvDataHash;
	}
	
	private void createCSVHeaderHash(String[] row)
	{
		try{
			for(int i=0; i<row.length; i++)
			{
				csvColumnIndexHash.put(row[i], String.valueOf(i));
			}
//			printHash(csvColumnIndexHash);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public HashMap<String,String> getCSVHeaderHash() throws MyCoreExceptions
	{
		try{
			if(csvColumnIndexHash != null)
				return csvColumnIndexHash;
			else
				throw new MyCoreExceptions("CSV Column header hash is Null");
		}catch(Exception e){
			throw new MyCoreExceptions("Exception while getting the CSV Column header hash");
		}
	}		
	
	public String getCSVData(String[] rowArray, String index)
	{
		String cellData = null;
		try{
			cellData = rowArray[Integer.parseInt(index)];
		}catch(Exception e){
			e.printStackTrace();
		}
		return cellData;
	}
	
	public String getCSVData(String index, String[] rowArray)
	{
		String cellData = null;
		try{
			cellData = rowArray[Integer.parseInt(index)];
		}catch(Exception e){
			e.printStackTrace();
		}
		return cellData;
	}
	
	private void printHash(HashMap<?,?> hashmap)
	{
		for(Object key : hashmap.keySet())
		{
			logger.info("Key : " + key.toString() + "- Value : "+ hashmap.get(key));
		}
	}
	
	public Object[][] getCSVArray(String fileName)
	{
		try{
			initializeConstans();
			 if(utils == null)
				 logger.warn("Utils obj is null");
			 
			 CSVReader reader = new CSVReader(new FileReader(utils.getCurrentWorkingDirectory() + WebDriverConstants.PATH_TO_TEST_DATA_FILE + fileName + ".csv"));
			 List<String[]> rowEntries = reader.readAll();
			 csvDataArray = new String[rowEntries.size()][];
			 csvDataArray = rowEntries.toArray(csvDataArray);
			 reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return csvDataArray;	
	}
	
	public Object[][] getCSVArray(String fileName,int rowNum)
	{
		try{
			initializeConstans();
			 if(utils == null)
				 logger.warn("Utils obj is null");
			 
			 CSVReader reader = new CSVReader(new FileReader(utils.getCurrentWorkingDirectory() + WebDriverConstants.PATH_TO_TEST_DATA_FILE + fileName + ".csv"));
			 List<String[]> rowEntries = new ArrayList<String[]>();
			 for(int i=0;i<rowNum;i++)
			 {
				 rowEntries.add(reader.readNext());
			 }
			 csvDataArray = new String[rowEntries.size()][];
			 csvDataArray = rowEntries.toArray(csvDataArray);
			 reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return csvDataArray;	
	}
	
	public Object[][] getCSVArray(String fileName,int rangeMin, int rangeMax)
	{
		try{
			initializeConstans();
			 if(utils == null)
				 logger.warn("Utils obj is null");
			 
			 CSVReader reader = new CSVReader(new FileReader(utils.getCurrentWorkingDirectory() + WebDriverConstants.PATH_TO_TEST_DATA_FILE + fileName + ".csv"));
			 List<String[]> allRows = reader.readAll();
			 reader.close();
			 List<String[]> rowEntries = new ArrayList<String[]>();
			 if(allRows.size()<rangeMin || allRows.size()<rangeMax) {
				 logger.error("FATAL : getCSVArray - CSV has fewer elements.. Change range specified "+ rangeMin + " "+ rangeMax+ "for file "+fileName);
			 }
			 else
			 {
				 for(int i=rangeMin;i<=rangeMax;i++)
				 {
					 rowEntries.add(allRows.get(i));
				 }
				 csvDataArray = new String[rowEntries.size()][];
				 csvDataArray = rowEntries.toArray(csvDataArray);
			 }
			 
		}catch(Exception e){
			e.printStackTrace();
		}
		return csvDataArray;	
	}
	
	public List<String[]> getCSVStringArray(String fileName)
	{
		List<String[]> rowEntries = new ArrayList<String[]>();
		try{
			initializeConstans();
			 if(utils == null)
				 logger.warn("Utils obj is null");
			 
			 CSVReader reader = new CSVReader(new FileReader(utils.getCurrentWorkingDirectory() + WebDriverConstants.PATH_TO_TEST_DATA_FILE + fileName + ".csv"));
			 rowEntries = reader.readAll();
		}catch(Exception e){
			e.printStackTrace();
		}
		return rowEntries;	
	}
}
