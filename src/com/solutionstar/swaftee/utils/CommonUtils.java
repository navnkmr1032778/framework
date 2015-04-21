package com.solutionstar.swaftee.utils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solutionstar.swaftee.constants.WebDriverConstants;

public class CommonUtils {
	
	Boolean driverFilefound = false;
	
	protected static Logger logger = LoggerFactory.getLogger(CommonUtils.class.getName());

	public String getCurrentWorkingDirectory()
	{		
		String workingDir = null;
		try{			
			workingDir = System.getProperty("user.dir");
		}catch(Exception e){
			e.printStackTrace();
		}
		return workingDir;
	}
	
	public String getTestDataFullDirPath(String fileName)
	{
		return (getCurrentWorkingDirectory() + WebDriverConstants.WINDOWS_PATH_TO_TEST_DATA_DIR + fileName);
	}
	
	public File getBrowserExecutable(String path, String fileName)
	{
		 try{
			 File fileDirectory = new File(path);
			 File[] listOfFiles = fileDirectory.listFiles();
			 if(listOfFiles.length != 0)
			 {
				 for(int i = 0; i < listOfFiles.length; i++)
				 {
					 if(listOfFiles[i].getName().contains(fileName)) // && listOfFiles[i].canExecute()) TODO : can executable check failing in mac os, have to find a way to execute it in mac
						 return listOfFiles[i];
				 }
			 }
			 if(!driverFilefound)
			 {
				 logger.info("No driver file found under drivers folder. Trying to download driver executable file");				 
				 DriverUtils.getInstance().downloadFile(fileName, System.getProperty("os.name"));
				 driverFilefound = true;
				 return getBrowserExecutable(path,fileName);
			 }	
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return new File("temp file");
	 }
	 
	 public void captureBrowserScreenShot(String imageName, WebDriver webDriver)
	 {
		  DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		  Date date = new Date();
		  String curDate = dateFormat.format(date);
		  screenShot( WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT + imageName + curDate+".jpg", webDriver); 
	 }
	
	 public void screenShot(String fileName, WebDriver webDriver) 
	 {
	      File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
	      try 
	      {
	        FileUtils.copyFile(scrFile, new File(fileName));
	      } 
	      catch (IOException e) 
	      {
	    	  logger.info("Error While taking Screen Shot");
	    	  e.printStackTrace();
	      }
	 }
	 
	 public String getCurrentTimeString()
	 {
		 try{
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			 Date date = new Date();
			 return sdf.format(date); 		 
		 }catch(Exception e){
			 e.printStackTrace();
			return null;
		}
	 }
	 
	 public boolean isNumeric(String s) 
	 {
		    return s.matches("[-+]?\\d*\\.?\\d+");
	 }
	 
	 public Date getDateFromString(String dateString, SimpleDateFormat formatter)
	 {
		Date date = null;
		try {	 
			date = formatter.parse(dateString);

		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		return date;
	 }
	 
	 public String getStringFromDate(Date d, SimpleDateFormat formatter)
	 { 
		 return formatter.format(d);
	 }
	 
	 public String getDateToday()
	 {
		 return new SimpleDateFormat("MM/dd/yyyy").format(new Date());
	 }
	 
	 public String getDateTomorrow()
	 {
		 return getFutureDate(1);
	 }
	 
	 public String getDateYesterday()
	 {
		 return getPastDate(1);
	 }
	 
	 public String getFutureDate(int daysToAdd)
	 {
		 DateTime now = new DateTime();
		 DateTime futureDate = now.plusDays(daysToAdd);
		 DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
		 return formatter.print(futureDate);
	 }
	 
	 public String getPastDate(int daysToAdd)
	 {
		 DateTime now = new DateTime();
		 DateTime pastDate = now.minusDays(daysToAdd);
		 DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
		 return formatter.print(pastDate);
	 }
	 
	 
	 
}
