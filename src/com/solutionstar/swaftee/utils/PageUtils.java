package com.solutionstar.swaftee.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import com.google.common.base.Function;

public class PageUtils 
{
	public static WebElement fluentWait(final By locator, int timeout,WebDriver driver) 
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

	public static void fluentWaitforPageLoad(int timeout, WebDriver driver) 
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
	  
	  public void selectDateDatePicker(String id, WebElement element, String date,WebDriver driver) 
	  {
		    JavascriptExecutor js = (JavascriptExecutor) driver;
		    js.executeScript("document.getElementById('" + id + "').removeAttribute('readonly')");
		    element.sendKeys(date);
		    element.sendKeys(Keys.TAB);
	  }

	  public void scrollDown(WebDriver driver, String xVal, String yVal) 
	  {
		    JavascriptExecutor js = (JavascriptExecutor) driver;
		    js.executeScript("scroll("+ xVal +", "+  yVal+");");
	  }

	  public void dragAndDropElements(WebElement dragElem, WebElement dropElem,WebDriver driver) throws InterruptedException 
	  {
		    Actions builder = new Actions(driver);
		    Point p = dropElem.getLocation();
		    scrollDown(driver, String.valueOf(p.x), String.valueOf(p.y));
		    Action dragAndDrop2 = builder.dragAndDropBy(dragElem, p.x, 0).build();
		    dragAndDrop2.perform();
		    Thread.sleep(5000);
		    dragElem.click();
	  }
	  
	  public boolean switchToWindowUsingTitle(String title,WebDriver driver) throws InterruptedException 
	  {
		    String curWindow = driver.getWindowHandle();
		    Set<String> windows = driver.getWindowHandles();
		    if (!windows.isEmpty()) 
		    {
		      for (String windowId : windows) 
		      {
		        if (driver.switchTo().window(windowId).getTitle().equals(title)) 
		        {
		          return true;
		        } 
		        else 
		        {
		          driver.switchTo().window(curWindow);
		        }
		      }
		    }
		    return false;
	  }
}
