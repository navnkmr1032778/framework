package com.solutionstar.swaftee.constants;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class WebDriverConstants {
	
	public static String PATH_TO_BROWSER_EXECUTABLE = "/resources/drivers/";
	public static String PATH_TO_BROWSER_SCREENSHOT = "resources/screenshot/";
	public static String PATH_TO_TEST_DATA_FILE = "/resources/testdata/";
	public static String WINDOWS_PATH_TO_TEST_DATA_DIR = "\\resources\\testdata\\";
	
	public static String DEFAULT_BROWSER_NAME = "chrome";
	public static int WAIT_FOR_VISIBILITY_TIMEOUT_IN_SEC = 10;
	public static int WAIT_ONE_MIN = 60;
	public static int MAX_TIMEOUT_PAGE_LOAD = 40;
	final public static String PROXY_SERVER  = "proxyserver.enabled";
	final public static String GRID_SERVER  = "grid.enabled";
	public static final String DEFAULT_BROWSER_OS = "windows";
	
	public enum OperatingSystem{
		WINDOWS, MAC
	}
	
	public enum DriverTypes{
		PRIMARY, SECONDARY
    } 
	
	public enum BrowserNames{
		CHROME, FIREFOX, INTERNET_EXPLORER, PHANTOMJS
    } 
    
	public static final Map<String, String> DRIVER_METHOD;
	static {
	  Map<String, String> tmp = new LinkedHashMap<String, String>();
	  tmp.put("ie", "setIEDriver");
	  tmp.put("firefox", "setFirefoxDriver");
	  tmp.put("chrome", "setChromeDriver");
	  tmp.put("phantomjs", "setPhomtomJsDriver");
	  DRIVER_METHOD = Collections.unmodifiableMap(tmp);
	}

	public static final Map<String, String> WINDOWS_DRIVERS;
	static {
	  Map<String, String> tmp = new LinkedHashMap<String, String>();
	  tmp.put("chrome", "https://github.com/sheltonpaul89/WebDrivers/raw/master/chromedriver_win32.zip");
	  tmp.put("phantomjs", "https://github.com/sheltonpaul89/WebDrivers/raw/master/phantomjs_win32.zip");
	  tmp.put("ie", "https://github.com/sheltonpaul89/WebDrivers/raw/master/IEDriverServer.zip");
	  WINDOWS_DRIVERS = Collections.unmodifiableMap(tmp);
	}
	
	public static final Map<String, String> MAC_DRIVERS;
	static {
	  Map<String, String> tmp = new LinkedHashMap<String, String>();
	  tmp.put("chrome", "http://chromedriver.storage.googleapis.com/2.14/chromedriver_mac32.zip");
	  tmp.put("phantomjs", "https://github.com/sheltonpaul89/WebDrivers/raw/master/phantomjs_mac.zip");
		
	  MAC_DRIVERS = Collections.unmodifiableMap(tmp);
	}

	public static Map<String, String> getDiverDownloadMapping(String osName) 
	{
		if(osName.contains("mac"))
			return MAC_DRIVERS;
		else
			return WINDOWS_DRIVERS;
	}
}
