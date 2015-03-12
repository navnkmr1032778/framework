package com.solutionstar.swaftee.webdriverhelpers;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.jsourcerer.webdriver.jserrorcollector.JavaScriptError;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.ProxyServer;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import com.solutionstar.swaftee.CustomExceptions.MyCoreExceptions;
import com.solutionstar.swaftee.config.WebDriverConfig;
import com.solutionstar.swaftee.constants.WebDriverConstants;
import com.solutionstar.swaftee.utils.CommonUtils;
import com.solutionstar.swaftee.webdriverFactory.AppDriver;

public class BaseDriverHelper {
	
	CommonUtils commonUtils = new CommonUtils();
	WebDriver driver = null;
	WebDriver secondaryDriver = null;	
	ProxyServer proxyServer = null;
	
	Logger logger = getLogger(this.getClass());
	
		public void startServer() throws InterruptedException
		{
				if(proxyServer !=  null)
					return;
				proxyServer = new ProxyServer(0); //port number equals to zero starts the server in dynamic port
			  	try {
		       	proxyServer.start();
	//	       	proxyServer.
		       
		      // Start the server in specified host and port - TODO
	//	      Map<String, String> options = new HashMap<String, String>();
	//	      options.put("httpProxy", "127.0.0.1" + ":" + "3000");
	//	      proxyServer.setOptions(options);
		       	
		       } catch (Exception e) {
		         String error = "Error while starting server.. " + e.getStackTrace();
		         logger.error(error);
		       }
		}
	
	   public void startDriver() throws Exception
	   {
		 	    if(driver != null)
			    	return;
			    String browserName = getBrowserName("primary");
			    logger.info("browserName -- "+ browserName);
			    DesiredCapabilities cap = createDriverCapabilities(browserName);		
				if (cap == null)
					throw new MyCoreExceptions("Capabilities return as Null");
				
				driver = setWebDriver(cap);
		 
		}

	   public WebDriver setWebDriver(DesiredCapabilities cap) throws Exception
	   {
		   if(WebDriverConfig.usingProxyServer())
			   createProxy(cap);
//		   setLoggingPref(cap);
		   if(WebDriverConfig.usingGrid())
			{
			   cap = setRemoteDriverCapabilities(cap.getBrowserName());
				driver = setRemoteWebDriver(cap);
			}
			else
				driver = startBrowser(cap);
		    
			return driver;
	   }

	   /*** TODO: customize the method
	   public void setLoggingPref(DesiredCapabilities cap)
	   {
		   LoggingPreferences logs = new LoggingPreferences();
		   logs.enable(LogType.BROWSER, Level.ALL);
		   logs.enable(LogType.CLIENT, Level.ALL);
		   logs.enable(LogType.DRIVER, Level.ALL);
		   logs.enable(LogType.PERFORMANCE, Level.ALL);
		   logs.enable(LogType.PROFILER, Level.ALL);
		   logs.enable(LogType.SERVER, Level.ALL);
		   cap.setCapability(CapabilityType.LOGGING_PREFS, logs);
	   }
	   ***/
	   
	   public void startSecondaryDriver() throws Exception
	   {
	  	    if(secondaryDriver != null)
		    	return;			    
		    String browserName = getBrowserName("secondary");
		    DesiredCapabilities cap = createDriverCapabilities(browserName);		
			if (cap == null)
				throw new MyCoreExceptions("Capabilities return as Null");
			logger.info("browserName -- "+ browserName);
			secondaryDriver = setWebDriver(cap);
				
	   }
	     
	   private String getBrowserName(String driverTypeStr) throws MyCoreExceptions
	   {
		   String browserName = WebDriverConstants.DEFAULT_BROWSER_NAME;
		   try{
			    switch(WebDriverConstants.DriverTypes.valueOf(driverTypeStr.toUpperCase()))
			    {
				    case PRIMARY   	: 	browserName = System.getProperty("webdriver.browser", WebDriverConstants.DEFAULT_BROWSER_NAME) ;  // Setting the default browser if no browser name is specified
				    				   	break;
				    case SECONDARY 	: 	browserName = System.getProperty("webdriver.secondary.browser", WebDriverConstants.DEFAULT_BROWSER_NAME) ;
					 				 	break;
				    default			: 	browserName = WebDriverConstants.DEFAULT_BROWSER_NAME;						
			    }
				browserName = browserName.toLowerCase() ;
				browserName =  WebDriverConstants.DRIVER_METHOD.containsKey(browserName) ? browserName : WebDriverConstants.DEFAULT_BROWSER_NAME;					
		   }catch(Exception e){
			   throw new MyCoreExceptions("Exception while assiging browserName");
		   }
			return browserName;
	   }
	   
	   private DesiredCapabilities createDriverCapabilities(String browserName)
	   {
		   DesiredCapabilities cap = null;			
		   try{
			    SetBrowserCapabilities setBrowserCapabilities = new SetBrowserCapabilities();
				Method setCapabilities = setBrowserCapabilities.getClass().getMethod(WebDriverConstants.DRIVER_METHOD.get(browserName),DesiredCapabilities.class);
				cap = (DesiredCapabilities) setCapabilities.invoke(setBrowserCapabilities, cap);

		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return cap;
	   }
	   
	   private WebDriver startBrowser(DesiredCapabilities cap)
	   {  
		   WebDriver driver = null;
		   try{
				switch (WebDriverConstants.BrowserNames.valueOf(cap.getBrowserName().replace(" ", "_").toUpperCase())) 
			    {
				     case CHROME:
				    	driver = new ChromeDriver(cap);
			   			break;
					case INTERNET_EXPLORER:
						driver = new InternetExplorerDriver();
			   			break;
					case FIREFOX:
						driver = new FirefoxDriver(cap);
			   			break;
					case PHANTOMJS:
						driver = new PhantomJSDriver(cap);
			   			break;
					default:
			            throw new IllegalArgumentException("Invalid Argument for browser name : " + cap.getBrowserName());
			    }
				
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return driver;
	   }
	   
	 
	   private DesiredCapabilities setRemoteDriverCapabilities(String browserName) throws Exception
	   {
		 	DesiredCapabilities capab = new DesiredCapabilities();
			capab.setBrowserName(browserName);
			if(System.getProperty("webdriver.browser.version") != null)
				capab.setVersion(System.getProperty("webdriver.browser.version"));
			capab.setPlatform(getOperatingSystem());
			
			return capab;
	   }
	
	   private Platform getOperatingSystem() 
	   {
			String os = System.getProperty("webdriver.platform.os",WebDriverConstants.DEFAULT_BROWSER_OS);
			switch(WebDriverConstants.OperatingSystem.valueOf(os.toUpperCase()))
			{
				case WINDOWS:
					return Platform.WINDOWS;
				case MAC:
					return Platform.MAC;
			}
			return null;
	   }
	   
	  public void startHar(String harName)
	  {
		  try{
			  proxyServer.newHar(harName);
//				proxyServer.newHar("windows.microsoft.com/");
//				proxyServer.newHar("google.com/");
		  }catch(Exception e){
			e.printStackTrace();
		  }
	  }
	   
	   private DesiredCapabilities setDriverCapabilities(String browserName) throws Exception
	   {
		   DesiredCapabilities cap = null;
		   SetBrowserCapabilities setBrowserCapabilities = new SetBrowserCapabilities();
		   browserName =  WebDriverConstants.DRIVER_METHOD.containsKey(browserName) ? browserName : "chrome";
	
		   Method setCapabilities = setBrowserCapabilities.getClass().getMethod(WebDriverConstants.DRIVER_METHOD.get(browserName),DesiredCapabilities.class);
		   return (DesiredCapabilities) setCapabilities.invoke(setBrowserCapabilities, cap);
	   }
	   
	   private WebDriver setRemoteWebDriver(DesiredCapabilities cap) throws Exception
	   {
			return new RemoteWebDriver(new URL("http://"+ WebDriverConfig.getWebDriverProperty("gridserver") +":"+ WebDriverConfig.getWebDriverProperty("gridserverport") +"/wd/hub"),cap);
	   }   
	   
	   
	    private Proxy createProxyObject() 
	    {
	    	Proxy proxy = null;
	    	try {
	    		logger.info("-------------------------------proxy server - "+ proxyServer.getPort());
	    		proxy = proxyServer.seleniumProxy();
//	    		proxy.setSslProxy("trustAllSSLCertificates");
//	    	    proxy.setHttpProxy("localhost:"+proxyServer.getPort());
//		        set server properties.
//		        proxyServer.setCaptureHeaders(true);// capture headers
//		        proxyServer.setCaptureContent(true);// capture content.

	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        return proxy;
	      }
	    
	    private void createProxy(DesiredCapabilities cap)
	    {
	    	try{
	    		Proxy proxy = createProxyObject();
				if (proxy != null)
				      cap.setCapability(CapabilityType.PROXY, proxy);
				else
					logger.info("Proxy object is null");
		    	
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
	    
	    public void stopServer()
	    {
	    	try{
	    		proxyServer.stop();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
	    
	    public void stopDriver() 
	    {
	    	 if(getDriver() != null) 
	 	    {
	 	      getDriver().quit();
	 	      setDriver(null);
	 	    }
	 	    if(getSecondaryDriver() != null)
	 	    {
	 	      getSecondaryDriver().quit();
	 	      setSecondaryDriver(null);	    	
	 	    }
	    }
	    
	    public WebDriver getDriver()
	    {
	    	return this.driver;
	    }
	    
	    public WebDriver getSecondaryDriver()
	    {
	    	return this.secondaryDriver;
	    }
	    
	    public void setDriver(Object obj)
	    {
	    	this.driver = (WebDriver) obj;
	    }
	    
	    public void setSecondaryDriver(Object obj)
	    {
	    	this.secondaryDriver = (WebDriver) obj;
	    }
	    
	    private void printCapabilities(Capabilities capabilities)
	    {
	        Map<String, ?> map = capabilities.asMap();
	        for (Entry<String, ?> entry : map.entrySet()) {
	          String key = entry.getKey();
	          Object value = entry.getValue();
	          logger.info("\t\tkey is " + key + "\t\tvalue is " + value);
	        }
	    }

		public Logger getLogger(Class<?> className) 
		{
			Logger logger = null;
			try{
				logger = LoggerFactory.getLogger(className);				
			}catch(Exception e){
				e.printStackTrace();
			}
			return logger;
		}	
		
		public String getPrimaryWinhandle() throws MyCoreExceptions
		{
			try{
				if(this.driver == null)
					throw new MyCoreExceptions("Unable to get the winhandle as the driver is set as null");
				return this.driver.getWindowHandle();
			}catch(Exception e){
				e.printStackTrace();
				throw new MyCoreExceptions("Exception occured... "+ e.getStackTrace());
				
			}
		}
		
		public String getSecondaryWinhandle() throws MyCoreExceptions
		{
			try{
				if(this.secondaryDriver == null)
					throw new MyCoreExceptions("Unable to get the winhandle as the driver is set as null");
				return this.secondaryDriver.getWindowHandle();
			}catch(Exception e){
				throw new MyCoreExceptions("Exception occured... "+ e.getStackTrace());
			}
		}
		
		public WebDriver getDriverfromResult(ITestResult testResult)
		{
				Object currentClass = testResult.getInstance();
			    return ((AppDriver) currentClass).getDriver();
		}
		public void ExtractChromeJSLogs(WebDriver driver) 
		{
			 	logger.info("JS Errors");
		        LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
		        for (LogEntry entry : logEntries) 
		        	logger.info(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
		}

		public void ExtractJSLogs(WebDriver driver, String driverType) throws MyCoreExceptions 
		{
				ExtractDriverJSErrors(driver,getBrowserName(driverType));
		}

		private void ExtractDriverJSErrors(WebDriver driver, String browserName) 
		{
			switch (WebDriverConstants.BrowserNames.valueOf(browserName.toUpperCase()))
		    {
			    case CHROME:
			        ExtractChromeJSLogs(driver);
		   			break;
				case FIREFOX:
					ExtractFFJSLogs(driver);
		   			break;
			}
		}

		private void ExtractFFJSLogs(WebDriver driver) 
		{
			final List<JavaScriptError> jsErrors = JavaScriptError.readErrors(driver);
			logger.info(jsErrors.toString());
		}
		
		public Har getHar()
		{
			try{
				logger.info("No of headers captured - "+ proxyServer.getHar().getLog().getEntries().size()); 
				Har har = proxyServer.getHar();
				String fileName = "resources/testdata/har-"+commonUtils.getCurrentTimeString()+".json";
//				String fileName = "resources/testdata/har"+System.currentTimeMillis()+".json";
				har.writeTo(new File(fileName));
				return har;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
}
