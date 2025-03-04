package com.solutionstar.swaftee.webdriverFactory;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.atomic.*;

import org.jenkinsci.testinprogress.messagesender.*;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.remote.*;
import org.slf4j.*;
import org.testng.*;
import org.testng.annotations.*;
import org.testng.internal.*;

import com.opencsv.*;
import com.opencsv.exceptions.*;
import com.solutionstar.swaftee.CustomExceptions.*;
import com.solutionstar.swaftee.constants.*;
import com.solutionstar.swaftee.jira.*;
import com.solutionstar.swaftee.utils.*;
import com.solutionstar.swaftee.webdriverhelpers.*;

import io.github.bonigarcia.wdm.*;
import io.github.bonigarcia.wdm.config.*;
import net.lightbody.bmp.core.har.*;

public class AppDriver extends TestListenerAdapter {

	protected static Logger logger = LoggerFactory.getLogger(AppDriver.class.getName());

	private final static String SKIP_EXCEPTION_MESSAGE = "Expected skip.";

	BaseDriverHelper baseDriverHelper = new BaseDriverHelper();
	CSVParserUtils csvParser = new CSVParserUtils();
	CommonUtils utils = new CommonUtils();
	private static boolean zephyrStarted = false;
	ZephyrUtils zUtils = new ZephyrUtils();
	Set<String> skippedMethods = new HashSet<String>();

	private Map<String, String> testKIds = new HashMap<String, String>();
	private AtomicLong atomicLong = new AtomicLong(0);

	private MessageSender messageSender;
	private IMessageSenderFactory messageSenderFactory = new SocketMessageSenderFactory();
	private long startTime;
	private DriverManagerType browserType;

	public void setDriverExecutable() {
		for (DriverManagerType browser : DriverManagerType.values()) {
			if (browser.toString().toLowerCase().contains(
					System.getProperty("gridbrowser", WebDriverConstants.DEFAULT_BROWSER_NAME).toLowerCase())) {
				browserType = browser;
				break;
			} else if (System.getProperty("gridbrowser", WebDriverConstants.DEFAULT_BROWSER_NAME).toLowerCase()
					.equalsIgnoreCase("ie")
					|| System.getProperty("gridbrowser", WebDriverConstants.DEFAULT_BROWSER_NAME).toLowerCase()
							.equalsIgnoreCase("internetexplorer")) {
				browserType = DriverManagerType.IEXPLORER;
				break;
			}
		}
		String workingDir = utils.getCurrentWorkingDirectory();
		logger.info("CURRENT WORKING DIRECTORY::" + workingDir);
		System.setProperty("wdm.targetPath", workingDir + "/resources/drivers/");
		if (System.getProperty("cleardriver", "false").equalsIgnoreCase("true")) {
			WebDriverManager.getInstance(browserType).clearDriverCache();
			WebDriverManager.getInstance(browserType).clearResolutionCache();
			logger.info("DRIVER CACHE CLEARED");
		}
		if (browserType.equals(DriverManagerType.IEXPLORER))
			WebDriverManager.getInstance(browserType).arch32();
		logger.info("DRIVER CACHE CLEARED");
		WebDriverManager.getInstance(browserType).setup();

	}

	public WebDriver getDriver() {
		try {
			logger.info("Checking driver..");
			if (baseDriverHelper.getDriver() == null) {
				baseDriverHelper.startServer();
				baseDriverHelper.startDriver();
			} else {
				logger.info("Driver already running..");
			}
		} catch (Exception e) {
			logger.info("Checking driver exception..");
			e.printStackTrace();
		}
		return baseDriverHelper.getDriver();
	}

	public WebDriver getSecondaryDriver() {
		logger.info("Starting Secondary Driver");
		try {
			if (baseDriverHelper.getSecondaryDriver() == null)
				baseDriverHelper.startSecondaryDriver();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baseDriverHelper.getSecondaryDriver();
	}

	public boolean hasDriver() {
		return baseDriverHelper.getDriver() == null ? false : true;
	}

	public boolean hasSecondaryDriver() {
		return baseDriverHelper.getSecondaryDriver() == null ? false : true;
	}

	public String getPrimaryWinhandle() throws MyCoreExceptions {
		return baseDriverHelper.getPrimaryWinhandle();
	}

	public String getSecondaryWinhandle() throws MyCoreExceptions {
		return baseDriverHelper.getSecondaryWinhandle();
	}

	public Logger getLogger() {
		return logger;
	}

	public Logger getLogger(Class<?> className) {
		Logger newLogger = baseDriverHelper.getLogger(className);
		if (newLogger != null)
			return newLogger;
		else {
			logger.warn("Logger initialization with class name provided failed. Returning default logger");
			return logger;
		}
	}

	public void setHar(String harName) {
		baseDriverHelper.startHar(harName);
	}

	protected Har getHar() {
		return baseDriverHelper.getHar();
	}

	public HashMap<String, String[]> getCSVDataHash(String fileName) {
		return csvParser.getCSVDataHash(fileName);
	}

	public HashMap<String, String[]> getCSVDataHash(String fileName, Integer columnNumber) {
		return csvParser.getCSVDataHash(fileName, columnNumber);
	}

	public HashMap<String, String[]> getCSVDataHash(String fileName, String columnName) {
		return csvParser.getCSVDataHash(fileName, columnName);
	}

	public HashMap<String, String[]> getCSVDataHash(String fileName, String[] rowArray) {
		return csvParser.getCSVDataHash(fileName, rowArray);
	}

	public HashMap<String, String[]> getCSVDataHash(String fileName, Integer[] rowArray) {
		return csvParser.getCSVDataHash(fileName, rowArray);
	}

	public HashMap<String, String> getCSVHeaderHash() throws MyCoreExceptions {
		return csvParser.getCSVHeaderHash();
	}

	public String getCSVData(String index, String[] rowArray) {
		return csvParser.getCSVData(index, rowArray);
	}

	public String getCSVData(String[] rowArray, String index) {
		return csvParser.getCSVData(rowArray, index);
	}

	public String getBrowserName() {
		return getDriver() != null ? ((RemoteWebDriver) getDriver()).getCapabilities().getBrowserName() : null;
	}

	@SuppressWarnings("resource")
	@DataProvider(name = "GenericDataProvider")
	public Object[][] genericDataProvider(Method methodName) throws IOException {
		logger.info("Method Name :" + methodName.getName());
		Reader reader = new FileReader("./resources/testdata/" + methodName.getName() + ".csv");
		List<String[]> scenarioData = null;
		try {
			scenarioData = new CSVReader(reader).readAll();

		} catch (CsvException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public void onStart(ITestContext context) {

		logger.info("Executing the Test in XML: " + context.getName());
		messageSender = messageSenderFactory.getMessageSender();

		try {
			messageSender.init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String parentName = getRunId(context);
		String runId = parentName;
		Map<String, ArrayList<String>> classMap = null;
		try {
			messageSender.testRunStarted(runId);// deprecated context.getAllTestMethods().length,
			classMap = processTestContext(context);
			String testId = getTestId(parentName);
			messageSender.testTree(testId, context.getCurrentXmlTest().getName(), parentName, true);// deprected
																									// classMap.keySet().size()
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		sendTestTree(classMap, context);
		startTime = System.currentTimeMillis();

	}

	@Override
	public void onFinish(ITestContext context) {

		long stopTime = System.currentTimeMillis();
		try {
			messageSender.testRunEnded(stopTime - startTime);// deprecated getRunId(context)
			messageSender.shutdown();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onTestStart(ITestResult result) {
		ITestNGMethod testMethod = result.getMethod();
		String testId = getIdForMethod(result.getTestContext(), testMethod);
		String mthdKey = getMessageSenderNameForMethod(testMethod);

		logger.info(
				"Starting the test : " + result.getMethod().getMethodName() + " - " + result.getTestClass().getName());

		try {
			messageSender.testStarted(testId, mthdKey, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // deprecated getRunId(result)

	}

	@Override
	public void onTestFailure(ITestResult testResult) {
		try {
			logger.info("Test : " + testResult.getName() + "' FAILED");
			if (!(testResult.getThrowable() instanceof NoSuchWindowException
					|| testResult.getThrowable() instanceof NoSuchFrameException)) {
				processResults(testResult, Boolean.valueOf(System.getProperty("takeScreenShot", "true")));
			}
			if (jiraUpdate()) {
				String[] testCases = getJiraTestCases(testResult);
				if (testCases != null && testCases.length > 0)
					ZephyrUtils.updateExecutionStatusOfTests(getJiraTestCases(testResult), ZephyrUtils.FAIL);
				else
					logger.info("No JIRA test cases to update");
			}
			if (testInProgressEnabled()) {
				ITestNGMethod testMethod = testResult.getMethod();
				String testId = getIdForMethod(testResult.getTestContext(), testMethod);
				String mthdKey = getMessageSenderNameForMethod(testMethod);

				String trace = getTrace(testResult.getThrowable());
				messageSender.testError(testId, mthdKey, trace);// deprecated , getRunId(testResult)
			}
		} catch (MyCoreExceptions | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestSuccess(ITestResult testResult) {
		try {
			logger.info("Test : " + testResult.getName() + "' PASSED");
			processResults(testResult, false);
			if (jiraUpdate()) {
				String[] testCases = getJiraTestCases(testResult);
				if (testCases != null && testCases.length > 0)
					ZephyrUtils.updateExecutionStatusOfTests(getJiraTestCases(testResult), ZephyrUtils.PASS);
				else
					logger.info("No JIRA test cases to update");
			}
			if (testInProgressEnabled()) {
				ITestNGMethod testMethod = testResult.getMethod();
				String testId = getIdForMethod(testResult.getTestContext(), testMethod);
				String mthdKey = getMessageSenderNameForMethod(testMethod);

				messageSender.testEnded(testId, mthdKey, false);// deprecated , getRunId(testResult)
			}
		} catch (MyCoreExceptions | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestSkipped(ITestResult testResult) {
		try {
			logger.info("Test : " + testResult.getName() + "' SKIPPED");
			if (jiraUpdate() && !isExpectedSkip(testResult)) {
				String[] testCases = getJiraTestCases(testResult);
				if (testCases != null && testCases.length > 0)
					ZephyrUtils.updateExecutionStatusOfTests(getJiraTestCases(testResult), ZephyrUtils.BLOCKED);
				else
					logger.info("No JIRA test cases to update");
			}
			if (testInProgressEnabled()) {
				ITestNGMethod testMethod = testResult.getMethod();
				String testId = getIdForMethod(testResult.getTestContext(), testMethod);
				String mthdKey = getMessageSenderNameForMethod(testMethod);

				messageSender.testStarted(testId, mthdKey, true);// , getRunId(testResult)
				messageSender.testEnded(testId, mthdKey, true);// deprecated , getRunId(testResult)
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processResults(ITestResult testResult, boolean takeScreenShot) throws MyCoreExceptions {
		boolean captureJsErrors = Boolean.valueOf(System.getProperty("captureJsErrors", "false"));
		Map<String, WebDriver> drivers = getDriverfromResult(testResult);
		for (String driverType : drivers.keySet()) {
			if (captureJsErrors)
				baseDriverHelper.ExtractJSLogs(drivers.get(driverType), driverType);

			long threadId = Thread.currentThread().getId();
			if (takeScreenShot) {
				utils.captureBrowserScreenShot(testResult.getName(), drivers.get(driverType));
				utils.captureFullBrowserScreenShot("FullSS_" + testResult.getName() + "_thread" + threadId,
						drivers.get(driverType));
			}

		}

	}

	public Map<String, WebDriver> getDriverfromResult(ITestResult testResult) {
		Map<String, WebDriver> driverList = new HashMap<String, WebDriver>();
		AppDriver appDriver = getAppDriver(testResult);
		if (appDriver != null) {
			if (appDriver.hasDriver())
				driverList.put("primary", appDriver.getDriver());
			if (appDriver.hasSecondaryDriver())
				driverList.put("secondary", appDriver.getSecondaryDriver());
		}

		return driverList;
	}

	protected AppDriver getAppDriver(ITestResult testResult) {
		Object currentClass = testResult.getInstance();
		if (currentClass instanceof AppDriver)
			return ((AppDriver) currentClass);
		else
			return null;
	}

	protected String[] getJiraTestCases(ITestResult testResult) {
		Annotation a = testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Jira.class);
		String val = (String) testResult.getAttribute("JiraIds");
		if (a != null)
			return ((Jira) a).TC();
		else if (val != null)
			return val.split(",");
		else
			return null;
	}

	protected boolean jiraUpdate() {
		if (Boolean.valueOf(System.getProperty("jira", "false").toLowerCase(Locale.ENGLISH))) {
			if (!zephyrStarted) {
				zephyrStarted = true;
				ZephyrUtils.initZephyr(getTestCycleId());
			}
			return true;
		} else
			return false;

	}

	protected boolean testInProgressEnabled() {
		return Boolean.valueOf(System.getProperty("jenkins_tip", "false").toLowerCase(Locale.ENGLISH));
	}

	protected String getTestCycleId() {
		String testCycleId = System.getProperty("testCycleId", "1118");
		return testCycleId;
	}

	public void skipTest(String message) {
		throw new SkipException(SKIP_EXCEPTION_MESSAGE + message);
	}

	public void skipTest() {
		skipTest(" Note: No additional skip message was provided.\n");
	}

	protected boolean isExpectedSkip(ITestResult testResult) {
		Throwable thr = testResult.getThrowable();
		boolean flag = false;
		if (thr.getMessage().startsWith(SKIP_EXCEPTION_MESSAGE)) {
			flag = true;
		} else {
			for (String methodDependentUpon : testResult.getMethod().getMethodsDependedUpon()) {
				if (skippedMethods.contains(methodDependentUpon)) {
					flag = true;
					break;
				}
			}
		}
		if (flag) {
			String className = testResult.getMethod().getConstructorOrMethod().getMethod().getDeclaringClass()
					.getName();
			skippedMethods.add(className + "." + testResult.getMethod().getMethodName());
		}
		return flag;
	}

	@AfterClass(alwaysRun = true)
	public void afterClass() {
		logger.info("Stopping BaseDrivers");
		stopDriver();
		baseDriverHelper.stopServer();
	}

	/***
	 * TODO : needed when using log capturing public void printLogs() { try{ Logs
	 * logs = getDriver().manage().logs(); LogEntries logEntries =
	 * logs.get(LogType.DRIVER);
	 * 
	 * for (LogEntry logEntry : logEntries) {
	 * System.out.println(logEntry.getMessage()); } }catch(Exception e){
	 * e.printStackTrace(); } }
	 ***/
	public void stopDriver() {
		logger.info("Stopping driver..");
		baseDriverHelper.stopDriver();
	}

	protected void stopSecondaryDriver() {
		baseDriverHelper.stopSecondaryDriver();
	}

	protected void stopPrimaryDriver() {
		baseDriverHelper.stopPrimaryDriver();
	}

	public void setDriver(WebDriver driver) {
		baseDriverHelper.setDriver(driver);
	}

	public void setSecondaryDriver(WebDriver driver) {
		baseDriverHelper.setSecondaryDriver(driver);
	}

	public String getJSErrors(WebDriver driver) {
		LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
		StringBuffer error = new StringBuffer();
		for (LogEntry entry : logEntries) {
			error.append(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
		}
		return error.toString();
	}

	/*
	 * Test In Progress plugin methods
	 * 
	 */
	private void sendTestTree(Map<String, ArrayList<String>> classMap, ITestContext context) {
		String xmlTestName = context.getCurrentXmlTest().getName();
		// String parentName = suiteName + ":" + xmlTestName;
		String runId = getRunId(context);
		Iterator<Entry<String, ArrayList<String>>> it = classMap.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, ArrayList<String>> entry = it.next();
			String className = entry.getKey();

			String clssTreedIdName = runId + ":" + className;

			String classTestId = getTestId(clssTreedIdName);
			ArrayList<String> methods = entry.getValue();
			int classChilds = methods.size();
			try {
				messageSender.testTree(classTestId, className, getTestId(runId), true);

				for (String method : methods) {
					String methodKey = method + "(" + className + ")";

					String mthdTreedIdName = runId + ":" + methodKey;
					String mthdTestId = getTestId(mthdTreedIdName);

					messageSender.testTree(mthdTestId, methodKey, classTestId, false);// deprecated , className, 1,
																						// runId
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // deprecated xmlTestName, classChilds, runId
		}
	}

	private String getTestId(String key) {
		String test = testKIds.get(key);
		if (test == null) {
			test = Long.toString(atomicLong.incrementAndGet());
			testKIds.put(key, test);
		}
		return test;
	}

	private String getIdForMethod(ITestContext context, ITestNGMethod testMethod) {
		String methodKey = getMessageSenderNameForMethod(testMethod);
		String testMethodContextKey = getRunId(context) + ":" + methodKey;
		String mthdTestId = getTestId(testMethodContextKey);

		return mthdTestId;
	}

	private String getMessageSenderNameForMethod(ITestNGMethod testMethod) {
		ConstructorOrMethod consMethod = testMethod.getConstructorOrMethod();
		String methodName = consMethod.getName();
		String className = consMethod.getDeclaringClass().getName();

		String methodKey = methodName + "(" + className + ")";
		return methodKey;
	}

	/**
	 * Gets the run id based on the suite and current xml test name.
	 * 
	 * @param context
	 * @return
	 */
	private String getRunId(ITestContext context) {
		String suiteName = context.getSuite().getName();
		String xmlTestName = context.getCurrentXmlTest().getName();

		if ((xmlTestName == null) || ("".equalsIgnoreCase(xmlTestName))) {
			xmlTestName = "Testng xml test";
		}
		String parentName = suiteName + "-" + xmlTestName;

		return parentName;
	}

	private String getRunId(ITestResult result) {
		return getRunId(result.getTestContext());
	}

	private String getTrace(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}

	private Map<String, ArrayList<String>> processTestContext(ITestContext context) {

		Map<String, ArrayList<String>> classMap = new HashMap<String, ArrayList<String>>();
		Collection<ITestNGMethod> testMethods = Arrays.asList(context.getAllTestMethods());

		for (ITestNGMethod testMethod : testMethods) {
			ConstructorOrMethod consMethod = testMethod.getConstructorOrMethod();
			String methodName = consMethod.getName();
			String className = consMethod.getDeclaringClass().getName();
			ArrayList<String> methodList;
			if (!classMap.containsKey(className)) {
				methodList = new ArrayList<String>();
			} else {
				methodList = classMap.get(className);
			}
			methodList.add(methodName);
			classMap.put(className, methodList);
		}
		return classMap;
	}

	public void setJiraTestCaseId(String ids) {
		ITestResult tr = Reporter.getCurrentTestResult();
		tr.setAttribute("JiraIds", ids);
	}
}