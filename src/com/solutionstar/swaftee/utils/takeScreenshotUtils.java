package com.solutionstar.swaftee.utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.solutionstar.swaftee.constants.WebDriverConstants;
import com.solutionstar.swaftee.webdriverhelpers.BaseDriverHelper;

public class takeScreenshotUtils {

	BaseDriverHelper helper;
	String folderName,fileName;
	boolean isDryRun;
	List<String> methodFolderNames;
	
	public takeScreenshotUtils()
	{
		synchronized(this)
		{
			helper=new BaseDriverHelper();
			isDryRun=helper.getIsDryRun();
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			this.folderName=stackTraceElements[2].getFileName().replace(".java", "");
			if(!isDryRun)
			{
				this.folderName=this.folderName+"_"+System.nanoTime();
			}
			this.fileName=WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT_BASE;
			if(isDryRun)
			{
				fileName=helper.getBaseDirLocation();
			}
			else if(helper.getCompareImages() )
			{
				fileName=helper.getCurrentDirLocation();
			}
			fileName=fileName+"/"+folderName;
			File dir=new File(fileName);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			methodFolderNames=new ArrayList<String>();
		}
	}
	
	public void createMethodFolder(String name)
	{
		synchronized(this)
		{
			File methodFolder=new File(name);
			if(methodFolder.exists())
			{
				methodFolder.delete();
			}
			methodFolder.mkdirs();
			methodFolderNames.add(name);
		}
	}
	
	/***
	 * 
	 * @param index -> interger (index of screenshot within a method)
	 * name -> "nodata"
	 * screenshot will be saved as methodName_index inside folder with name methodName_nodata
	 */

	public void takeScreenShot(WebDriver driver,int index) 
	{
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String methodName=stackTraceElements[2].getMethodName();
		String methodFolderName=fileName+"/"+methodName;
		String file;
		synchronized(this)
		{
			file=methodFolderName+"/"+methodName+"_"+index;
			if(!isDryRun)
			{
				file=file+"_"+System.nanoTime();
			}
			if(!methodFolderNames.contains(methodFolderName))
				createMethodFolder(methodFolderName);
		}
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try 
		{
			file=file+".png";
			System.out.println("screenshot file name: "+file);
			FileUtils.copyFile(scrFile, new File(file));	//Files.move(src,dest);
		} 
		catch (IOException e) 
		{
			System.out.println("Error While taking Screen Shot");
			e.printStackTrace();
		}
	}
	
	/***
	 * 		
	 * @param index -> interger (index of screenshot within a method)
	 * @param name ->   based on data from data provider
	 * 
	 * screenshot will base saved as methodName_index inside fold methodName_name
	 */
	
	public void takeScreenShot(WebDriver driver,int index,String name) 
	{
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String methodName=stackTraceElements[2].getMethodName()+"_"+name;
		String methodFolderName=fileName+"/"+methodName;
		synchronized(this)
		{
			if(!methodFolderNames.contains(methodFolderName))
				createMethodFolder(methodFolderName);
		}
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try 
		{
			String file=methodFolderName+"/"+methodName+"_"+index;
			file=file+".png";
			System.out.println("screenshot file name: "+file);
			FileUtils.copyFile(scrFile, new File(file));	//Files.move(src,dest);
		} 
		catch (IOException e) 
		{
			System.out.println("Error While taking Screen Shot");
			e.printStackTrace();
		}
	}
}
