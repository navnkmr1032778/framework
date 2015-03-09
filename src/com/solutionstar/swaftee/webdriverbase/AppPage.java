package com.solutionstar.swaftee.webdriverbase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestListenerAdapter;

import com.google.common.base.Function;
import com.solutionstar.swaftee.constants.WebDriverConstants;

public class AppPage extends TestListenerAdapter 
{
	 protected static Logger logger = LoggerFactory.getLogger(AppPage.class.getName());
	 WebDriver driver;
	 
	 enum ByTypes{
		  INDEX, VALUE, TEXT
	 } 
	 
	 public AppPage(WebDriver driver)
	 {
		 this.driver = driver;
		 PageFactory.initElements(driver, this);
	 }
	
	 public void get(String url) 
	 {
		this.driver.get(url);
	 }
	
	 public String getCurrentUrl() 
	 {
		return this.driver.getCurrentUrl();
	 }
	 
	 public void deleteCookies() 
	 {
	    this.driver.manage().deleteAllCookies();
	 }

	 public String pageSource() 
	 {
	    return this.driver.getPageSource();
	 }
	
	 public boolean isElementPresent(By locator) 
	 {
	    return this.driver.findElements(locator).size() == 0? false : true;
	 }
	 public boolean isElementPresent(WebElement element) 
	 {
		 try
		 {
			 element.getAttribute("innerHTML");
		 }
		 catch(Exception ex)
		 {
			 return false;
		 }
	    return true;
	 }
	
	 public boolean isElementPresentAndDisplayed(WebElement element) 
	 {
		 try
		 {
			 return isElementPresent(element) && element.isDisplayed();
		 }
		 catch(Exception ex)
		 {
			 return false;
		 }
     }
	 
	 public Boolean isElementPresentInContainer(WebElement container, final By locator) 
	 {
		    Boolean isElementPresent = false;
		    if (container != null && container.findElements(locator).size() > 0)
		      isElementPresent = true;
		    return isElementPresent;
	 }
	 public void waitForVisible(WebElement element) 
	 {
		    WebDriverWait wait =
		        new WebDriverWait(driver,WebDriverConstants.WAIT_FOR_VISIBILITY_TIMEOUT_IN_SEC);
		    wait.until(ExpectedConditions.visibilityOf(element));
     }
	 
	 public void waitForPageLoadComplete() 
	 {
		  waitForPageLoad(WebDriverConstants.MAX_TIMEOUT_PAGE_LOAD);
		  return;
	 }
	 public void clearAndType(WebElement element, String text) 
	 {
		  element.clear();
		  element.sendKeys(text);
	 }
	  
	 public void screenShot(String fileName) 
	 {
	      File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	      try 
	      {
	        FileUtils.copyFile(scrFile, new File(fileName));
	      } 
	      catch (IOException e) 
	      {
	    	  e.printStackTrace();
	    	  logger.error("Error While taking Screen Shot");
	      }
	 }
	 
	 public void selectDropdown(WebElement element, String by, String value) 
	 {
		  Select select = new Select(element);
		  switch (ByTypes.valueOf(by.toLowerCase())) 
		  {
		      case INDEX:
		        select.selectByIndex(Integer.parseInt(value));
		        break;
		      case VALUE:
		        select.selectByValue(value);
		        break;
		      case TEXT:
		        select.selectByVisibleText(value);
		        break;

		  }
	 }
	  
	 public WebElement fluentWaitByLocator(final By locator, int timeout) 
	 {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(timeout, TimeUnit.SECONDS).pollingEvery(3, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		    WebElement element = wait.until(new Function<WebDriver, WebElement>() 
		    {
		      public WebElement apply(WebDriver driver) 
		      {
		        return driver.findElement(locator);
		      }
		    }); 
			return element;
	 }

	public void waitForPageLoad(int timeout) 
	{
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(timeout, TimeUnit.SECONDS).pollingEvery(3, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		    wait.until(new ExpectedCondition<Boolean>() 
		    {
		      public Boolean apply(WebDriver driver) 
		      {
		        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals(
		            "complete");
		      }
		    });
			return;
	}
	
	public boolean verifyDropDownElements(WebElement drpdown, List<String> listExpected) 
	{
		    Select selectCity = new Select(drpdown);
		    List<WebElement> list = selectCity.getOptions();
		    List<String> listNames = new ArrayList<String>(list.size());
		    for (WebElement w : list) 
		    	 listNames.add(w.getText());
		    return listNames.containsAll(listExpected);
	}
		  
	public void selectDateDatePicker(String id, WebElement element, String date) 
	{
		    JavascriptExecutor js = (JavascriptExecutor) driver;
		    js.executeScript("document.getElementById('" + id + "').removeAttribute('readonly')");
		    element.sendKeys(date);
		    element.sendKeys(Keys.TAB);
	}

	public void scrollDown(String xVal, String yVal) 
	{
		    JavascriptExecutor js = (JavascriptExecutor) driver;
		    js.executeScript("scroll("+ xVal +", "+  yVal+");");
	}

	public void dragAndDropElements(WebElement dragElem, WebElement dropElem) throws InterruptedException 
	{
		    Actions builder = new Actions(driver);
		    Point p = dropElem.getLocation();
		    scrollDown(String.valueOf(p.x), String.valueOf(p.y));
		    Action dragAndDrop2 = builder.dragAndDropBy(dragElem, p.x, 0).build();
		    dragAndDrop2.perform();
		    Thread.sleep(5000);
		    dragElem.click();
	}
		  
	public boolean switchToWindowUsingTitle(String title) throws InterruptedException 
	{
		    String curWindow = this.driver.getWindowHandle();
		    Set<String> windows = this.driver.getWindowHandles();
		    if (!windows.isEmpty()) 
		    {
		      for (String windowId : windows) 
		      {
		        if (this.driver.switchTo().window(windowId).getTitle().equals(title)) 
		        {
		          return true;
		        } 
		        else 
		        {
		          this.driver.switchTo().window(curWindow);
		        }
		      }
		    }
		    return false;
  	}
}
