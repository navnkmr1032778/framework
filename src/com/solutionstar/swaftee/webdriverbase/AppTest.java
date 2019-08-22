package com.solutionstar.swaftee.webdriverbase;


import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.solutionstar.swaftee.constants.WebDriverConstants;
import com.solutionstar.swaftee.jira.Zephyr;
import com.solutionstar.swaftee.jira.ZephyrUtils;
import com.solutionstar.swaftee.utils.CommonUtils;
import com.solutionstar.swaftee.webdriverFactory.AppDriver;

import io.github.bonigarcia.wdm.DriverManagerType;
import io.github.bonigarcia.wdm.WebDriverManager;

@Listeners(AppDriver.class)
public class AppTest extends AppDriver 
{
	 CommonUtils utils = new CommonUtils();
	 @BeforeSuite(alwaysRun=true)
	  public void beforeSuite(ITestContext ctx) {
		  if(System.getProperty("webdriver.browser", WebDriverConstants.DEFAULT_BROWSER_NAME)=="chrome")
		  {
			  String workingDir = utils.getCurrentWorkingDirectory();
			  System.setProperty("wdm.targetPath", workingDir+"/resources/drivers/");
	   			WebDriverManager.chromedriver().clearCache();
	   			WebDriverManager.chromedriver().clearPreferences();
	   			WebDriverManager.chromedriver().setup();
		  }
		  logger.info("XML FileName : " +ctx.getCurrentXmlTest().getSuite().getFileName());
		  logger.info("Executing the Suite : " +ctx.getSuite().getName());
	  }

	  @AfterSuite(alwaysRun=true)
	  public void AfterSuite() {
		  if(jiraUpdate())
		  {
			  ZephyrUtils.updateExecutionStatusInJIRA();
			  Zephyr.generateHtmlReport();
		  }
	  }

}
