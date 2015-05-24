package com.solutionstar.swaftee.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.solutionstar.swaftee.constants.WebDriverConstants;

public class CommonUtils {
	
	Boolean driverFilefound = false;
	Session session = null;
	Channel channel = null;
	ChannelSftp channelSftp = null;
		  
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
		String path = WebDriverConstants.PATH_TO_TEST_DATA_FILE;
		if(OSCheck.getOperatingSystemType() == OSCheck.OSType.Windows)
			path = WebDriverConstants.WINDOWS_PATH_TO_TEST_DATA_DIR;
		return (getCurrentWorkingDirectory()+ path+ fileName);
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

	/**
	 * Copies file from SFTP
	 * 
	 * Provided a username, password, source location, destination, hostname, list of files and
	 * port copies them using sftp
	 * 
	 * @param hostname
	 *            hostname of sftp server
	 * @param port
	 *            port number of sftp server
	 * @param username
	 *            username to login to the server
	 * @param password
	 *            password to login
	 * @param sourceLocation
	 *            source location in the remote server
	 * @param files
	 *            List of files to be coiped from remote server
	 * @param destination
	 *            the location in the local machine
	 */
	public void copyViaSFTP(String hostname, int port, String username,
			String password, String sourceLocation, String destination,
			List<String> files) {

		try {

			channelSftp = getChannelSftp(hostname, port, username, password);
			for (String file : files) {
				channelSftp.get(sourceLocation + file, destination);
			}
			if (channelSftp != null)
				channelSftp.disconnect();
			if (session != null)
				session.disconnect();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	public List<String> listFilesInSFTPLocation(String hostname, int port,
			String username, String password, String sourceLocation) {

		List<String> list = new ArrayList<String>();
		try {
			channelSftp = getChannelSftp(hostname, port, username, password);
			Vector<ChannelSftp.LsEntry> v = channelSftp.ls(sourceLocation);
			for (ChannelSftp.LsEntry o : v) {
				list.add(o.getFilename());
			}
			if (channelSftp != null)
				channelSftp.disconnect();
			if (session != null)
				session.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}
	
	public boolean isFileExists(String fileName) {
		return new File(fileName).exists();
	}
	
	public ChannelSftp getChannelSftp(String hostname, int port, String username,
			String password){
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(username, hostname, port);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return channelSftp;
	}
	
	public String toJson(Object obj) {
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		return json;
	}
	
	public Object fromJson(String json, Class<?> classObj) {
		Gson gson = new Gson();
		Object obj = gson.fromJson(json, classObj);
		return obj;
	}
	
	public void saveOjectToFile(Object object, String fileName) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(fileName));
			String json = toJson(object);
			pw.write(json);
			pw.close();
		} catch (IOException ex) {
			logger.error("Exception occured when saving object to file. Messgae: " + ex.getMessage());
		}
	}
	
	public Object retriveOjectFromFile(Class<?> classObj, String fileName){
		Scanner scan = null;
		Object obj = null;
		try {
			scan = new Scanner(new File(fileName));
			StringBuilder json = new StringBuilder();
	        while(scan.hasNext()){
	            json.append(scan.next());
	        }
			scan.close();
			obj = fromJson(json.toString(), classObj);
		} catch (Exception ex) {
			logger.error("Exception occured when retriving object to file. Messgae: " + ex.getMessage());
		}
		return obj;
	}
}
