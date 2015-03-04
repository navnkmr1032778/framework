package com.solutionstar.swaftee.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solutionstar.swaftee.constants.WebDriverConstants;

public class DriverUtils 
{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	CommonUtils utils = new CommonUtils();
	private static DriverUtils instance = null;
   
	protected DriverUtils() {
      // Exists only to defeat instantiation.
    }
    protected static DriverUtils getInstance() 
    {
      if(instance == null) {
         instance = new DriverUtils();
      }
      return instance;
    }
	   
	public void downloadFile(String DriverName,String osName)
	{
		String dirName = utils.getCurrentWorkingDirectory()+ WebDriverConstants.PATH_TO_BROWSER_EXECUTABLE;		 
		try {
		 
			saveFileFromUrlWithJavaIO(
			dirName + "\\"+ DriverName +".zip",WebDriverConstants.getDiverDownloadMapping(osName).get(DriverName));
			unZipIt(dirName + "\\"+ DriverName +".zip",dirName);
			File file = new File(dirName + "\\"+ DriverName +".zip");
			file.delete();
		 
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}		 
	}
	
	// Using Java IO
	public static void saveFileFromUrlWithJavaIO(String fileName, String fileUrl)
	throws MalformedURLException, IOException {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(new URL(fileUrl).openStream());
			fout = new FileOutputStream(fileName);
			 
			byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) 
				fout.write(data, 0, count);
		} 
		finally {
			if (in != null)
			in.close();
			if (fout != null)
			fout.close();
		}
	}
	 
	// Using Commons IO library
	// Available at http://commons.apache.org/io/download_io.cgi
	public static void saveFileFromUrlWithCommonsIO(String fileName,
	String fileUrl) throws MalformedURLException, IOException 
	{
		FileUtils.copyURLToFile(new URL(fileUrl), new File(fileName));
	}
	
	 public void unZipIt(String zipFile, String outputFolder)
	 {	 
	     byte[] buffer = new byte[1024];	 
	     try{
	 
	    	//create output directory is not exists
	    	File folder = new File(outputFolder);
	    	if(!folder.exists()){
	    		folder.mkdir();
	    	}
	 
	    	//get the zip file content
	    	ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
	    	//get the zipped file list entry
	    	ZipEntry ze = zis.getNextEntry();
	 
	    	while(ze!=null)
	    	{	 
	    	   String fileName = ze.getName();
	           File newFile = new File(outputFolder + File.separator + fileName);
	 
	           logger.info("file unzip : "+ newFile.getAbsoluteFile());
	 
	            //create all non exists folders
	            //else you will hit FileNotFoundException for compressed folder
	            new File(newFile.getParent()).mkdirs();
	 
	            FileOutputStream fos = new FileOutputStream(newFile);             
	 
	            int len;
	            while ((len = zis.read(buffer)) > 0)
	            {
	            	fos.write(buffer, 0, len);
	            }	 
	            fos.close();   
	            ze = zis.getNextEntry();
	    	}
	 
	        zis.closeEntry();
	    	zis.close();	 
	    }catch(IOException ex){
	       ex.printStackTrace(); 
	    }
	  }  
}