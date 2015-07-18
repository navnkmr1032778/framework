package com.solutionstar.swaftee.webdriverFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.lightbody.bmp.core.har.Har;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.TestListenerAdapter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;

import com.opencsv.CSVReader;
import com.solutionstar.swaftee.CustomExceptions.MyCoreExceptions;
import com.solutionstar.swaftee.jira.Jira;
import com.solutionstar.swaftee.jira.ZephyrUtils;
import com.solutionstar.swaftee.utils.CSVParserUtils;
import com.solutionstar.swaftee.utils.CommonUtils;
import com.solutionstar.swaftee.webdriverhelpers.BaseDriverHelper;


public class AppDriver extends TestListenerAdapter {

	protected static Logger logger = LoggerFactory.getLogger(AppDriver.class.getName());
	
	static String PASS = "1";
	static String FAIL = "2";
	static String UNEXECUTED = "-1";
	static String BLOCKED = "4";
	
	private final static String SKIP_EXCEPTION_MESSAGE = "Expected skip.";
	
	BaseDriverHelper baseDriverHelper = new BaseDriverHelper();
	CSVParserUtils csvParser = new CSVParserUtils();
	CommonUtils utils = new CommonUtils();
	ZephyrUtils zUtils = new ZephyrUtils();
	
	Set<String> skippedMethods = new HashSet<String>();
	
	public WebDriver getDriver()
	{ 
	    try 
	    {
	    	logger.info("Checking driver..");	
	    	if(baseDriverHelper.getDriver() == null)
	    	{ 
	    		baseDriverHelper.startServer();
	    		baseDriverHelper.startDriver();
	    	}else
	    	{
	    		logger.info("Driver already running..");
	    	}
		} catch (Exception e) {
			logger.info("Checking driver exception..");	
			e.printStackTrace();
		}
		return baseDriverHelper.getDriver();
	}
	
	public WebDriver getSecondaryDriver() 
	{
		logger.info("Starting Secondary Driver");	 
		try 
		{
			if(baseDriverHelper.getSecondaryDriver() == null)
				baseDriverHelper.startSecondaryDriver();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return baseDriverHelper.getSecondaryDriver();
	}

	public boolean hasDriver()
	{
		return baseDriverHelper.getDriver() == null? false : true;
	}
	public boolean hasSecondaryDriver()
	{
		return baseDriverHelper.getSecondaryDriver() == null? false : true;
	}

	public String getPrimaryWinhandle() throws MyCoreExceptions
	{
		return baseDriverHelper.getPrimaryWinhandle();
	}
	
	public String getSecondaryWinhandle() throws MyCoreExceptions
	{
		return baseDriverHelper.getSecondaryWinhandle();
	}
	
	public Logger getLogger()
	{
			return logger;
	}
	
	public Logger getLogger(Class<?> className)
	{
		Logger newLogger =baseDriverHelper.getLogger(className);
		if(newLogger != null)
			return newLogger;
		else
		{
			logger.warn("Logger initialization with class name provided failed. Returning default logger");
			return logger;
		}
	}
	
	public void setHar(String harName)
	{
		baseDriverHelper.startHar(harName);
	}
	
	protected Har getHar()
	{
		return baseDriverHelper.getHar();
	}
	
	public HashMap<String, String[]> getCSVDataHash(String fileName)
	{
		return csvParser.getCSVDataHash(fileName);
	}
	
	public HashMap<String, String[]> getCSVDataHash(String fileName, Integer columnNumber)
	{
		return csvParser.getCSVDataHash(fileName, columnNumber);
	}
	
	public HashMap<String, String[]> getCSVDataHash(String fileName, String columnName)
	{
		return csvParser.getCSVDataHash(fileName, columnName);
	}
	
	public HashMap<String, String[]> getCSVDataHash(String fileName, String[] rowArray)
	{
		return csvParser.getCSVDataHash(fileName, rowArray);
	}
	
	public HashMap<String, String[]> getCSVDataHash(String fileName, Integer[] rowArray)
	{
		return csvParser.getCSVDataHash(fileName, rowArray);
	}
	
	public HashMap<String,String> getCSVHeaderHash() throws MyCoreExceptions
	{
		return csvParser.getCSVHeaderHash();
	}
	
	public String getCSVData(String index, String[] rowArray)
	{
		return csvParser.getCSVData(index, rowArray);
	}
	
	public String getCSVData(String[] rowArray, String index)
	{
		return csvParser.getCSVData(rowArray, index);
	}
	
	public String getBrowserName() 
	{
	    return getDriver() != null ? ((RemoteWebDriver) getDriver()).getCapabilities().getBrowserName() : null;
	}
	
	@SuppressWarnings("resource")
	@DataProvider(name = "GenericDataProvider")
	public Object[][] genericDataProvider(Method methodName) throws IOException {
		logger.info("Method Name :" + methodName.getName());
		Reader reader = new FileReader("./resources/Testdata/"+ methodName.getName() + ".csv");
		List<String[]> scenarioData = new CSVReader(reader).readAll();
		Object[][] data = new Object[scenarioData.size() - 1][1];
		Iterator<String[]> it = scenarioData.iterator();
		String[] header = it.next();
		int CSV_cnt = 0;
		while (it.hasNext()) {
			HashMap<String, String> hashItem = new HashMap<String, String>();
			String[] line = it.next();
			for (int i = 0; i < line.length; i++)
				hashItem.put(header[i], line[i]);
			data[CSV_cnt][0] = hashItem;
			CSV_cnt++;
		}
		return data;
	}

	
	@Override
	public void onTestFailure(ITestResult testResult) 
	{
	   try 
	   {
		   logger.info("Test " + testResult.getName() + "' FAILED");
		   processResults(testResult,true);
		   System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		   Throwable thr = testResult.getThrowable();
			if(thr != null) {
				System.out.println("Printing from custom code:");
				System.out.println(thr.getMessage());
			}
		   if(jiraUpdate())
		   {
			   String[] testCases = getJiraTestCases(testResult);
			   if(testCases!= null && testCases.length>0)
				   zUtils.updateExecutionStatusOfTests(getJiraTestCases(testResult), FAIL);
			   else
				   logger.info("No JIRA test cases to update");
		   }
	   } 
	   catch (MyCoreExceptions e) 
	   {
			e.printStackTrace();
	   }
	}
	
	@Override
	public void onTestSuccess(ITestResult testResult) 
	{
		 try 
		   {
				logger.info("Test : " + testResult.getName() + "' PASSED");
				processResults(testResult,false);
				System.out.println("---------------------------------------------------");
				Throwable thr = testResult.getThrowable();
				if(thr != null) {
					System.out.println("Printing from custom code:");
					System.out.println(thr.getMessage());
				}
				if(jiraUpdate())
				{
					String[] testCases = getJiraTestCases(testResult);
					if(testCases!= null && testCases.length>0)
						zUtils.updateExecutionStatusOfTests(getJiraTestCases(testResult), PASS);
					else
						logger.info("No JIRA test cases to update");
				}
		   } 
		   catch (MyCoreExceptions e) 
		   {
				e.printStackTrace();
		   }
	}

	@Override
	public void onTestSkipped(ITestResult testResult) 
	{
		 try 
		   {
				logger.info("Test : " + testResult.getName() + "' SKIPPED");
				processResults(testResult,false);
				if(jiraUpdate() && !isExpectedSkip(testResult))
				{
					String[] testCases = getJiraTestCases(testResult);
					if(testCases!= null && testCases.length>0)
						zUtils.updateExecutionStatusOfTests(getJiraTestCases(testResult), BLOCKED);
					else
						logger.info("No JIRA test cases to update");
				}
		   } 
		   catch (MyCoreExceptions e) 
		   {
				e.printStackTrace();
		   }
	}

	private void processResults(ITestResult testResult,boolean takeScreenShot) throws MyCoreExceptions
	{
		 Map<String,WebDriver> drivers = getDriverfromResult(testResult);
		 for(String driverType : drivers.keySet())
		 {
			   baseDriverHelper.ExtractJSLogs(drivers.get(driverType),driverType);
			   if(takeScreenShot)  utils.captureBrowserScreenShot(testResult.getName(), drivers.get(driverType));
		 }
	}
	
	public Map<String, WebDriver> getDriverfromResult(ITestResult testResult)
	{
		Map<String, WebDriver> driverList = new HashMap<String,WebDriver>();
		if(getAppDriver(testResult).hasDriver())
			driverList.put("primary",getAppDriver(testResult).getDriver());
		if(getAppDriver(testResult).hasSecondaryDriver())
			driverList.put("secondary",getAppDriver(testResult).getSecondaryDriver());
		return driverList;
	}
	
	protected AppDriver getAppDriver(ITestResult testResult)
	{
		  Object currentClass = testResult.getInstance();
	      return ((AppDriver) currentClass);
	}
	
	protected String[] getJiraTestCases(ITestResult testResult)
	{
		Annotation a = testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Jira.class);
		if(a!=null)
			return ((Jira) a).TC();
		else
			return null;
	}
	
	protected boolean jiraUpdate()
	{
		return Boolean.valueOf(System.getProperty("jira","false").toLowerCase(Locale.ENGLISH));
	}
	
	public void skipTest(String message)
	{
		throw new SkipException(SKIP_EXCEPTION_MESSAGE + message);
	}
	
	public void skipTest()
	{
		skipTest(" Note: No additional skip message was provided.\n");
	}
	
	protected boolean isExpectedSkip(ITestResult testResult)
	{
		Throwable thr = testResult.getThrowable();
		boolean flag = false;
		if (thr.getMessage().startsWith(SKIP_EXCEPTION_MESSAGE))
		{
			flag = true;
		}
		else
		{
			for (String methodDependentUpon : testResult.getMethod()
					.getMethodsDependedUpon())
			{
				if (skippedMethods.contains(methodDependentUpon))
				{
					flag = true;
					break;
				}
			}
		}
		if (flag)
		{
			skippedMethods.add(testResult.getMethod().getMethodName());
		}
		return flag;
	}
	
	@AfterClass
	public void afterMethod()
	{
		logger.info("Stopping BaseDrivers");
		baseDriverHelper.stopDriver();
		baseDriverHelper.stopServer();		
	}
	
	/*** TODO : needed when using log capturing
	public void printLogs()
	{
		try{
			Logs logs = getDriver().manage().logs();
			LogEntries logEntries = logs.get(LogType.DRIVER);

			for (LogEntry logEntry : logEntries) {
			    System.out.println(logEntry.getMessage());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	***/
	protected void stopDriver() 
	{
	    baseDriverHelper.stopDriver();
    }
	
	protected void stopSecondaryDriver()
	{
		baseDriverHelper.stopSecondaryDriver();
	}
	
	protected void stopPrimaryDriver()
	{
		baseDriverHelper.stopPrimaryDriver();
	}
	
	public void setDriver(WebDriver driver) 
	{
		baseDriverHelper.setDriver(driver);
	}
	  
	public void setSecondaryDriver(WebDriver driver) 
	{
		baseDriverHelper.setSecondaryDriver(driver);
	 }
 }