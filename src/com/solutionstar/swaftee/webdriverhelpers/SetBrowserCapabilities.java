package com.solutionstar.swaftee.webdriverhelpers;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import net.jsourcerer.webdriver.jserrorcollector.JavaScriptError;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solutionstar.swaftee.constants.WebDriverConstants;
import com.solutionstar.swaftee.utils.CommonUtils;

public class SetBrowserCapabilities {
	
	CommonUtils utils = new CommonUtils();
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public boolean isGridRun()
	{
		return Boolean.valueOf(System.getProperty("grid","false").toLowerCase(Locale.ENGLISH));
	}
	
	
	public DesiredCapabilities setChromeDriver( DesiredCapabilities cap)
   	{
   		try{
   			String workingDir = utils.getCurrentWorkingDirectory();
   			if(workingDir == null)
   			{
   				logger.info("Working directory is Null");
   				return null;
   			}
   			
   			if(!isGridRun())
   			{
   				File chromeDriver = utils.getBrowserExecutable((workingDir+WebDriverConstants.PATH_TO_BROWSER_EXECUTABLE), "chrome");
   	   		    
   	   			if(chromeDriver.getName().equals("tempfile"))
   	   			{
   	   				logger.warn("Unable to find executable file");
   	   				return null;
   	   			}
   	   			System.setProperty("webdriver.chrome.driver", chromeDriver.getAbsolutePath());
   			}
   			
            cap = DesiredCapabilities.chrome();
            LoggingPreferences loggingprefs = new LoggingPreferences();
            loggingprefs.enable(LogType.BROWSER, Level.ALL);
            cap.setCapability(CapabilityType.LOGGING_PREFS, loggingprefs);
            ChromeOptions options = new ChromeOptions();
			options.addArguments("--disable-extensions");
			options.addArguments("--session-override=true");
			String windowSize = System.getProperty("windowSize","");
			
			if(windowSize.matches("^\\d+,\\d+$"))
			{
				options.addArguments("--window-size=" + windowSize);
			}
			
			String emulDeviceName = System.getProperty("emulationDeviceName","windows");
			if(!emulDeviceName.equals("windows"))
			{
				Map<String, String> mobileEmulation = new HashMap<String, String>();
				mobileEmulation.put("deviceName", emulDeviceName);
				Map<String, Object> chromeOptions = new HashMap<String, Object>();
				chromeOptions.put("mobileEmulation", mobileEmulation);
				cap.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
			}
			else
			{
				cap.setCapability(ChromeOptions.CAPABILITY, options);
			}
			cap.setBrowserName("chrome");
   		}catch(Exception e){
   			e.printStackTrace();
   		}
   		return cap;
   	}
	
   	public DesiredCapabilities setFirefoxDriver(DesiredCapabilities cap)
   	{
   		try{
   			final FirefoxProfile profile = new FirefoxProfile();
            JavaScriptError.addExtension(profile);
            cap = DesiredCapabilities.firefox();
            cap.setCapability(FirefoxDriver.PROFILE, profile);
            cap.setBrowserName("firefox");
   		}catch(Exception e){
   			e.printStackTrace();
   		}
   		return cap;
   	}
   	
   	public DesiredCapabilities setIEDriver(DesiredCapabilities cap)
   	{
   		try{
   			String workingDir = utils.getCurrentWorkingDirectory();
   			if(workingDir == null)
   			{
   				logger.warn("Working directory is Null ");
   				return null;
   			}
   			
   			if(!isGridRun())
   			{
   				File ieDriver = utils.getBrowserExecutable((workingDir+WebDriverConstants.PATH_TO_BROWSER_EXECUTABLE), "IE");
   	   		    
   	   			if(ieDriver.getName().equals("tempfile"))
   	   			{
   	   				logger.info("Unable to find executable file");
   	   				return null;
   	   			}
   	   			System.setProperty("webdriver.ie.driver", ieDriver.getAbsolutePath());
   			}
   			
			cap = DesiredCapabilities.internetExplorer();
			cap.setCapability(InternetExplorerDriver.
					INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			cap.setBrowserName("InternetExplorer");
   		}catch(Exception e){
   			e.printStackTrace();
   		}
   		return cap;
   	}
   	
   	public DesiredCapabilities setPhomtomJsDriver( DesiredCapabilities cap)
   	{
   		try{
   			String workingDir = utils.getCurrentWorkingDirectory();
   			if(workingDir == null)
   			{
   				logger.warn("Working directory is Null ");
   				return null;
   			}
   			
   			if(!isGridRun())
   			{
   				File phantomDriver = utils.getBrowserExecutable((workingDir+WebDriverConstants.PATH_TO_BROWSER_EXECUTABLE), "phantomjs");
   	   			
   	   		    if(phantomDriver.getName().equals("tempfile"))
   	   			{
   	   				logger.info("Unable to find executable file");
   	   				return null;
   	   			} 		   
   				System.setProperty("phantomjs.binary.path", phantomDriver.getAbsolutePath());	
   			}
   			
   		    cap = DesiredCapabilities.phantomjs();
   		   
   		}catch(Exception e){
   			e.printStackTrace();
   		}
   		return cap;
   	}
}
