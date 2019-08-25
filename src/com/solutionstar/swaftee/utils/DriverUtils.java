//package com.solutionstar.swaftee.utils;
//
//import java.io.File;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.solutionstar.swaftee.constants.WebDriverConstants;
//
//public class DriverUtils 
//{
//	Logger logger = LoggerFactory.getLogger(this.getClass());
//	FileDownloader fileDownloader = new FileDownloader();
//	CommonUtils utils = new CommonUtils();
//	private static DriverUtils instance = null;
//   
//	protected DriverUtils() {
//      // Exists only to defeat instantiation.
//    }
//    protected static DriverUtils getInstance() 
//    {
//      if(instance == null) {
//         instance = new DriverUtils();
//      }
//      return instance;
//    }
//	   
//	@SuppressWarnings("static-access")
//	public void downloadFile(String DriverName,OSCheck.OSType osType)
//	{
//		String dirName = utils.getCurrentWorkingDirectory()+ WebDriverConstants.PATH_TO_BROWSER_EXECUTABLE;		 
//		try {
//			File destDir = new File(dirName);
//			if (!destDir.exists()) 
//				destDir.mkdir();
//			String osName = null;
//			switch(osType)
//			{
//			case Linux:
//				osName = "linux";
//				break;
//			case MacOS:
//				osName = "mac";
//				break;
//			case Other:
//			case Windows:
//			default:
//				osName = "windows";
//				break;
//			
//			}
//			fileDownloader.saveFileFromUrlWithJavaIO(dirName + DriverName + ".zip",
//					WebDriverConstants.getDiverDownloadMapping(osName).get(DriverName.toLowerCase()));
//			fileDownloader.unZipIt(dirName + DriverName +".zip",dirName);
//			File file = new File(dirName + DriverName +".zip");
//			file.delete();
//		} 
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//		}		 
//	}
//}