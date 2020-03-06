package com.solutionstar.swaftee.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.solutionstar.swaftee.constants.WebDriverConstants;
import com.solutionstar.swaftee.utils.ImageComparison.ImageCompareHelper;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

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

//	public File getBrowserExecutable(String path, String fileName)
//	{
//		try{
//			File fileDirectory = new File(path);
//			if (!fileDirectory.exists())
//			{
//				fileDirectory.mkdirs();
//			}
//
//			File[] listOfFiles = fileDirectory.listFiles();
//			if (listOfFiles != null && listOfFiles.length != 0) {
//				for (int i = 0; i < listOfFiles.length; i++) {
//					if (listOfFiles[i].getName().contains(fileName)) {
//						//check windows driver file "chromedriver.exe" and for Mac "chromedriver"
//						//check for file extension based on OS type and to pick the correct driver file even when both drivers are available
//						if (OSCheck.getOperatingSystemType() == OSCheck.OSType.Windows
//								&& Files.getFileExtension(listOfFiles[i].getName()).equalsIgnoreCase("exe")) {
//							listOfFiles[i].setExecutable(true);
//							return listOfFiles[i];
//						} else if ((OSCheck.getOperatingSystemType() == OSCheck.OSType.MacOS
//								|| OSCheck.getOperatingSystemType() == OSCheck.OSType.Linux)
//								&& !Files.getFileExtension(listOfFiles[i].getName()).equalsIgnoreCase("exe")) {
//							listOfFiles[i].setExecutable(true);
//							return listOfFiles[i];
//						} 
//					}
//
//				}
//			}
//			if(!driverFilefound)
//			{
//				logger.info("No driver file found under drivers folder. Trying to download driver executable file");
//				DriverUtils.getInstance().downloadFile(fileName, OSCheck.getOperatingSystemType());
//				driverFilefound = true;
//				return getBrowserExecutable(path,fileName);
//			}
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		return new File("temp file");
//	}
	
	public void captureFullBrowserScreenShot(String imageName, WebDriver webDriver)
	{
		try
		{
			Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(webDriver);
			File dir = new File(WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT);
			if (!dir.exists())
			{
				try
				{
					dir.mkdir();
					logger.info("creating directory: " + dir);
				}
				catch (Exception ex)
				{
					logger.info("Couldn't create Directory" + ExceptionUtils.getRootCauseStackTrace(ex));
				}
			}
			ImageIO.write(screenshot.getImage(),"PNG",new File(WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT + imageName  + ".png"));
		}
		catch (Exception ex)
		{
			logger.info("Couldn't take Screenshot" + ExceptionUtils.getRootCauseStackTrace(ex));
		}
	}

	public void captureBrowserScreenShot(String imageName, WebDriver webDriver)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		String curDate = dateFormat.format(date);
		File dir = new File(WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT);
		if (!dir.exists())
		{
			try
			{
				dir.mkdir();
				logger.info("creating directory: " + dir);
			}
			catch (Exception ex)
			{
				logger.info("Couldn't create Directory" + ExceptionUtils.getRootCauseStackTrace(ex));
			}
		}

		try
		{
			Set<String> handles = webDriver.getWindowHandles();
			String currentHandle = webDriver.getWindowHandle();
			int handleCount = 0;
			for (String handle : handles)
			{
				handleCount++;
				webDriver.switchTo().window(handle);
				webDriver.manage().window().maximize();
				try
				{
					Thread.sleep(500);
				}
				catch (Exception e)
				{
					logger.info(ExceptionUtils.getRootCauseStackTrace(e).toString());
				}
				screenShot(WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT + imageName + "_handle" + handleCount + "_"
						+ curDate + "_" + System.currentTimeMillis() + ".png", webDriver);
			}
			webDriver.switchTo().window(currentHandle);
		}
		catch (Exception ex)
		{
			logger.info("exception in taking Screenshot" + ExceptionUtils.getRootCauseStackTrace(ex));
		}
	}


	public void screenShot(String fileName, WebDriver webDriver)
	{
		try
		{
			File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
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
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
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

	public int getMonthNumberByName(String monthName)
	{
		try
		{
			Date date = new SimpleDateFormat("MMM").parse(monthName);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int monthNumber=cal.get(Calendar.MONTH);
			return monthNumber+1;
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public String changeDateFormat(String date,String format) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String sDate=sdf.format(new Date(date));
		return sDate;
	}

	public String compareDateWithToday(String d1) throws ParseException
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
			Date date1=sdf.parse(d1);
			Date date2 = new Date();
			String sDate= sdf.format(date2);
			date2= sdf.parse(sDate);
			if(date1.compareTo(date2)>0)
			{
				logger.info(date1+" date is future "+date2);
				return "future";
			}
			else if(date1.compareTo(date2)<0)
			{
				logger.info(date1+" date is past "+date2);
				return "past";
			}
			else
			{
				logger.info("present ongoing");
				return "present";
			}
		}
		catch(Exception ex)
		{
			logger.info(ExceptionUtils.getRootCauseStackTrace(ex).toString());
			return null;
		}
	}

	public int getMonthDiffBtwnDatesByMonthAndYear(String from,String to)
	{
		try
		{
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		DateTime date11=new DateTime(df.parse(from));
		DateTime date12=new DateTime(df.parse(to));
		return (Months.monthsBetween(
			     date11.toLocalDate().withDayOfMonth(1),
			     date12.toLocalDate().withDayOfMonth(1)).getMonths());
		}
		catch(Exception ex)
		{
			logger.info(ExceptionUtils.getRootCauseStackTrace(ex).toString());
			return -1;
		}
	}

	public String getDayForDate(String date)
	{
		try
		{
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
			String day = formatter.format(new Date(date));
			return day;
		}
		catch(Exception ex)
		{
			logger.info("exception in get day for date: "+ExceptionUtils.getRootCauseStackTrace(ex));
			return null;
		}
	}

	public String changeTimeFormat(String fromFormat,String toFormat,String time)
	{
		try
		{
			DateFormat formatter = new SimpleDateFormat(fromFormat);
			Date date = formatter.parse(time);

			SimpleDateFormat sdf = new SimpleDateFormat(toFormat);
			String formatTime = sdf.format(date);
			logger.info("after formatting: "+formatTime);
			return formatTime;
		}
		catch(Exception ex)
		{
			logger.info("change time format exception: "+ExceptionUtils.getRootCauseStackTrace(ex));
			return null;
		}
	}

	public long compareTwoTimes(String startTime,String endTime)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
			Date d1 = sdf.parse(startTime);
			Date d2 = sdf.parse(endTime);
			long elapsed = d2.getTime() - d1.getTime();
			logger.info(""+elapsed);
			return elapsed;
		}
		catch(Exception ex)
		{
			logger.info("exception in comparing two times: "+ExceptionUtils.getRootCauseStackTrace(ex));
			return -1;
		}
	}

	public Date getDateFromString(String dateString, SimpleDateFormat formatter)
	{
		Date date = null;
		try {
			date = formatter.parse(dateString);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getRootCauseStackTrace(e).toString());
		}
		return date;
	}

	public String getStringFromDate(Date d, SimpleDateFormat formatter)
	{
		return formatter.format(d);
	}

		public String getDateToday()
	{
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		return getDateToday(df);

	}

		public String getDateToday(SimpleDateFormat formatter)
	{
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatter.format(new Date());
	}


	public String getDateTomorrow()
	{
		return getFutureDate(1);
	}

	public String getDateYesterday()
	{
		return getPastDate(1);
	}

	public String getFutureDate(int daysToAdd, SimpleDateFormat sdf)
	{
		DateTime now = new DateTime(DateTimeZone.UTC);
		DateTime futureDate = now.plusDays(daysToAdd);
		return sdf.format(futureDate.toDate());
	}

	public String getFutureDate(int daysToAdd)
	{
		DateTime now = new DateTime(DateTimeZone.UTC);
		DateTime futureDate = now.plusDays(daysToAdd);
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
		return formatter.print(futureDate);
	}

	public String getPastDate(int daysToAdd)
	{
		DateTime now = new DateTime(DateTimeZone.UTC);
		DateTime pastDate = now.minusDays(daysToAdd);
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
		return formatter.print(pastDate);
	}

	public String getPastHour(int timeToSubtract)
	{
		DateTime now = new DateTime();
		DateTime pastTime = now.minusHours(timeToSubtract);
		DateTimeFormatter formatter=DateTimeFormat.forPattern("mm");
		String presentMin=formatter.print(pastTime);
		pastTime=pastTime.minusMinutes(Integer.parseInt(presentMin));
		formatter=DateTimeFormat.forPattern("h:mm aa");
		return formatter.print(pastTime);
	}

	public String addMinToTime(String time,int minToAdd)
	{
		DateTimeFormatter formatter = DateTimeFormat.forPattern("h:mm aa");
		DateTime now=formatter.parseDateTime(time);
		DateTime pastDate = now.plusMinutes(minToAdd);
		formatter = DateTimeFormat.forPattern("h:mm aa");
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

	public void copyToSFTPLocation(String hostname, int port, String username,
			String password, String sourceLocation, String destination,
			List<String> files) throws SftpException  {

			channelSftp = getChannelSftp(hostname, port, username, password);
			for (String file : files) {
				String fileName = file.substring(file.lastIndexOf("/") + 1);
				String source = sourceLocation + file;
				String desc = destination + fileName;
				channelSftp.put(source, desc, null);
			}
			if (channelSftp != null)
				channelSftp.disconnect();
			if (session != null)
				session.disconnect();
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

	public String objToJson(Object obj) {
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		return json;
	}

	public Object constructObjfromJson(String json, Class<?> classObj) {
		Gson gson = new Gson();
		Object obj = gson.fromJson(json, classObj);
		return obj;
	}

	public synchronized void saveObjectToFile(Object object, String fileName) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(fileName));
			String json = objToJson(object);
			pw.write(json);
			pw.close();
		} catch (IOException ex) {
			logger.error("Exception occured when saving object to file. Message: " + ex.getMessage());
		}
	}

	public Object retriveObjectFromFile(Class<?> classObj, String fileName) {
		BufferedReader br = null;
		Object obj = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			StringBuilder json = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				json.append(line);
				json.append(System.lineSeparator());
				line = br.readLine();
			}

			br.close();
			obj = constructObjfromJson(json.toString(), classObj);
		} catch (Exception ex) {
			logger.error("Exception occured when retriving object from file. Message: "
					+ ex.getMessage());
		}
		return obj;
	}

	public String changeDateFormat(String date, String sourceFormat, String destinationFormat) {

		Date d = getDateFromString(date, new SimpleDateFormat(sourceFormat));

		return getStringFromDate(d,new SimpleDateFormat(destinationFormat));
	}

	public String convertStringArraytoJSONArray(String[] values)
	{
		if (values.length > 0) {
			StringBuilder nameBuilder = new StringBuilder();

			for (String n : values) {
				nameBuilder.append("'").append(n.replace("'", "\\'")).append("',");
				// can also do the following
				// nameBuilder.append("'").append(n.replace("'", "''")).append("',");
			}

			nameBuilder.deleteCharAt(nameBuilder.length() - 1);

			return nameBuilder.toString();
		} else {
			return "";
		}
	}

	public Map<String,Object> convertJSONToMap(String jsonString)
	{
		Map<String, Object> retMap = new Gson().fromJson(jsonString, new TypeToken<HashMap<String, Object>>() {}.getType());
		return retMap;
	}

	public Collection<? extends String> listFilesInSFTPLocation(String hostname, int port, String username,
			String password, String sourceLocation, Date startTime,String fileExtension) {
		List<String> list = new ArrayList<String>();
		try {
			channelSftp = getChannelSftp(hostname, port, username, password);
			channelSftp.cd(sourceLocation);
			Vector<ChannelSftp.LsEntry> v = channelSftp.ls("*."+fileExtension);
			logger.info("Start time is "+startTime.toString());
			for (ChannelSftp.LsEntry o : v) {
				String ti = o.getAttrs().getMtimeString();
				int t = o.getAttrs().getMTime();
				Date createdDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(ti);

				if(startTime.before(createdDate))
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

	public Collection<? extends String> listFilesInSFTPLocation(String hostname, int port, String username,
			String password, String sourceLocation, Date startTime) {
		return listFilesInSFTPLocation(hostname, port,username,
				password,sourceLocation, startTime,"csv");
	}

	@SuppressWarnings("unchecked")
	public List<String> textFileToList(String filePath) {
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(new File(getTestDataFullDirPath(filePath)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

	public List<Map<String,Object>> convertJSONArrayToMap(String jsonString)
    {
        List<Map<String, Object>> retMap = new Gson().fromJson(jsonString, new TypeToken<List<HashMap<String, Object>>>() {}.getType());
        return retMap;
    }

	public String getSwafteeAbsolutePath() throws UnsupportedEncodingException
	{
		String path = ImageCompareHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = URLDecoder.decode(path, "UTF-8");
		decodedPath=decodedPath.substring(1, decodedPath.length());
		decodedPath=decodedPath.replace("target/classes/", "").trim();
		return decodedPath;
	}
}
