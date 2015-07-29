package com.solutionstar.swaftee.webdriverbase;


import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import com.solutionstar.swaftee.jira.ZephyrUtils;
import com.solutionstar.swaftee.webdriverFactory.AppDriver;

@Listeners(AppDriver.class)
public class AppTest extends AppDriver 
{
	  @BeforeSuite(alwaysRun=true)
	  public void beforeSuite() {
	  }

	  @AfterSuite(alwaysRun=true)
	  public void AfterSuite() {
		  if(jiraUpdate())
			  ZephyrUtils.updateExecutionStatusInJIRA();
	  }

}
