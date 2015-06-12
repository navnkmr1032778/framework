package com.solutionstar.swaftee.webdriverbase;


import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Listeners;

import com.solutionstar.swaftee.webdriverFactory.MobileDriver;

@Listeners(MobileDriver.class)
public class MobileTest extends MobileDriver 
{
	  @BeforeSuite
	  public void beforeSuite() {
	  }

	  @AfterSuite
	  public void AfterSuite() {
	  }

}
