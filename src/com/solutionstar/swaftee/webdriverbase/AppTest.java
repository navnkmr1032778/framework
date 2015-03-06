package com.solutionstar.swaftee.webdriverbase;


import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Listeners;
import com.solutionstar.swaftee.webdriverFactory.AppDriver;

@Listeners(AppDriver.class)
public class AppTest extends AppDriver 
{
	  @BeforeSuite
	  public void beforeSuite() {
	  }

	  @AfterSuite
	  public void AfterSuite() {
	  }

}
