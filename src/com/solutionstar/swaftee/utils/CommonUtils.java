package com.solutionstar.swaftee.utils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solutionstar.swaftee.constants.WebDriverConstants;

public class CommonUtils {
	
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
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return new File("tempfile");
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
	    	  e.printStackTrace();
	    	  logger.info("Error While taking Screen Shot");
	      }
	 }
}
