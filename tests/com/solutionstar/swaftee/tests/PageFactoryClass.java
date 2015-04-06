package com.solutionstar.swaftee.tests;

import org.openqa.selenium.WebDriver;

import com.solutionstar.swaftee.webdriverbase.AppPage;

public class PageFactoryClass extends AppPage{
	
	public PageFactoryClass(WebDriver driver)
	{
		super(driver);
	}
	
	public boolean isDriverNotNull()
	{
		return this.driver != null ? true : false ;
	}

}
