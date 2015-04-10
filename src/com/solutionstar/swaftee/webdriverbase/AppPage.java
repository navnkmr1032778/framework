package com.solutionstar.swaftee.webdriverbase;

import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
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
import org.testng.Assert;
import org.testng.TestListenerAdapter;

import com.google.common.base.Function;
import com.solutionstar.swaftee.constants.WebDriverConstants;

public class AppPage extends TestListenerAdapter 
{
	 protected static Logger logger = LoggerFactory.getLogger(AppPage.class.getName());
	 protected WebDriver driver;
	 
	 enum ByTypes{
		  INDEX, VALUE, TEXT
	 } 
	 
	 enum JavaScriptSelector{
		  ID, CLASS, NAME, TAGNAME
	 } 
	 
	 public AppPage(WebDriver driver)
	 {
		 this.driver = driver;
		 waitForPageLoadComplete();
		 PageFactory.initElements(driver, this);
	 }
	 
	 public WebDriver getDriver()
	 {
		 return this.driver;
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
		  switch (ByTypes.valueOf(by.toUpperCase())) 
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
		    
		    return getAllSelectOptions(drpdown).containsAll(listExpected);
	}
		  
	public void selectDateDatePicker(WebElement element, String date) 
	{
		    JavascriptExecutor js = (JavascriptExecutor) driver;
		    js.executeScript( "arguments[0].removeAttribute('readonly','readonly')",element);
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
		  
	public Set<String> getWindowHandles()
	{
		return this.driver.getWindowHandles();	
	}
	
	public String getWindowHandle()
	{
		return this.driver.getWindowHandle();
	}
 
	public void waitForWindowToClose(String windowId)
	{
		final String window = windowId;
		int timeout = 10;
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(timeout, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
	    wait.until(new ExpectedCondition<Boolean>() 
	    {
	      public Boolean apply(WebDriver driver) 
	      {
	        return !getWindowHandles().contains(window);
	      }
	    });
		return;
	}
	
	public boolean switchToNextWindowClosingCurrent()
	{
		boolean switchSuccess = false;
		List<String>windows = new ArrayList<String>(getWindowHandles());
		String currentWindow = getWindowHandle();
		for(int index=0; index<windows.size();index++)
		{
			if(currentWindow.equals(windows.get(index)))
			{
				this.driver.close(); 
				//Pass index, since the next window's index would've reduced by 1 
				switchSuccess = switchToNthWindow(index);  
				break;
			}
		}
		return switchSuccess;
	}
	
	public boolean closeAllWindowsExceptCurrent()
	{
		boolean switchSuccess = false;
		List<String>windows = new ArrayList<String>(getWindowHandles());
		String currentWindow = getWindowHandle();
		for(int index=0; index<windows.size();index++)
		{
			if(currentWindow.equals(windows.get(index)))
			{
				switchSuccess = switchToNthWindowClosingOthers(index, true);
				break;
			}
		}
		return switchSuccess;
	}
	
	public boolean switchToNextWindow()
	{
		boolean switchSuccess = false;
		List<String>windows = new ArrayList<String>(getWindowHandles());
		String currentWindow = getWindowHandle();
		for(int index=0; index<windows.size();index++)
		{
			if(currentWindow.equals(windows.get(index)))
			{
				switchSuccess = switchToNthWindow(index+1);  
				break;
			}
		}
		return switchSuccess;
	}
	
	public boolean switchToPreviousWindow()
	{
		return switchToPreviousWindowClosingCurrent(false);
	}
	
	public boolean switchToPreviousWindowClosingCurrent(boolean close)
	{
		boolean switchSuccess = false;
		List<String>windows = new ArrayList<String>(getWindowHandles());
		String currentWindow = getWindowHandle();
		for(int index=0; index<windows.size();index++)
		{
			if(currentWindow.equals(windows.get(index)))
			{
				if(close)
					this.driver.close();
				switchSuccess = switchToNthWindow(index-1);  
				break;
			}
		}
		return switchSuccess;
	}
	public boolean switchToLastWindowClosingOthers()
	{
		List<String>windows = new ArrayList<String>(getWindowHandles());
		return switchToNthWindowClosingOthers(windows.size(), true);
	}
	
	/**
	 * Switch to corresponding nth window and close other open windows if needed
	 * @param n - index of window to switch to(assuming 0 as start index)
	 * @param close - True if other windows have to be closed
	 * @return
	 */
	public boolean switchToNthWindowClosingOthers(int n, boolean close)
	{
		boolean switchSuccess = false;
		List<String>windows = new ArrayList<String>(getWindowHandles());
		if(windows.size()>=n)
		{
			if(close)
			{
				for (int index=0;index<windows.size();index++)
				{
					switchToWindow(windows.get(index));
					if(index!=n)
					{
						this.driver.close();
					}
				}
			}
			switchToWindow(windows.get(n));
			switchSuccess = true;
		}
		return switchSuccess;
	}
	
	public boolean switchToNthWindow(int n)
	{
		return switchToNthWindowClosingOthers(n, false);
	}
	
	public void switchToWindow(String windowHandle)
	{
    	this.driver.switchTo().window(windowHandle);
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
	
	public String getvalueUsingJavaScript(String by, String ele)
	{
		String val = null;
		try{
			JavascriptExecutor js = (JavascriptExecutor) this.driver;
			switch (JavaScriptSelector.valueOf(by.toUpperCase())) 
			{
			      case ID:
						val = (String) js.executeScript("document.getElementById('"+ele+"').value");
						break;
			      case CLASS:
						val = (String) js.executeScript("document.getElementsByClassName('"+ele+"').value");
						break;
			      case TAGNAME:
						val = (String) js.executeScript("document.getElementsByTagName('"+ele+"').value");
						break;
			      case NAME:
						val = (String) js.executeScript("document.getElementsByName('"+ele+"').value");
						break;					
			}
			return val;
		}catch(Exception e){
			e.printStackTrace();
		}
		return val;

	}
	
	public void setvalueUsingJavaScript(String by, String ele, String val)
	{
		try{
			JavascriptExecutor js = (JavascriptExecutor) this.driver;
			switch (JavaScriptSelector.valueOf(by.toUpperCase())) 
			{
			      case ID:
						js.executeScript("document.getElementById('"+ele+"').value = \""+ val + "\"" );
						break;
			      case CLASS:
						js.executeScript("document.getElementsByClassName('"+ele+"').value = \""+ val + "\"" );
						break;
			      case TAGNAME:
						js.executeScript("document.getElementsByTagName('"+ele+"').value = \""+ val + "\"" );
						break;
			      case NAME:
						js.executeScript("document.getElementsByName('"+ele+"').value = \""+ val + "\"" );
						break;					
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
     * Simulates the user clicking the "Refresh" button on their browser.
     */
    public void refresh() 
    {    
        this.driver.navigate().refresh();   
    }
    
    
    public List<String> getAllSelectOptions(WebElement drpdown)
    {
    	 Select s = new Select(drpdown);
		    List<WebElement> list = s.getOptions();
		    List<String> listNames = new ArrayList<String>(list.size());
		    for (WebElement w : list) 
		    	 listNames.add(w.getText());
		    
		    return listNames;
    }
    
    
    public void waitUntilDropdownIsLoaded(WebElement drpdown, final List<String> defaultOptions)
    {
    	
    	try {

    		final WebElement dropdown = drpdown;
 		    ExpectedCondition<Boolean> isLoadingFalse = new
 				   ExpectedCondition<Boolean>() {
 			   public Boolean apply(WebDriver driver) 
 			   {
 				   return (!getAllSelectOptions(dropdown).isEmpty() && getAllSelectOptions(dropdown).size()!=defaultOptions.size() && !defaultOptions.containsAll(getAllSelectOptions(dropdown)));
 			   }
 		   };
 		   Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(WebDriverConstants.WAIT_ONE_MIN, TimeUnit.SECONDS).pollingEvery(2, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		   wait.until(isLoadingFalse);
	   } 
	   catch (Exception e) 
	   {
		   logger.error(e.getMessage());
	   }
    }
    
    protected Boolean validateURL(String url)
    {
    	try
    	{
    		new URL(url);
    	}
    	catch (MalformedURLException e) 
    	{
    		return false;
    	}
    	return true;
    }
    
    public void switchToDefaultContent() 
    {
    	this.driver.switchTo().defaultContent();
    }
    
    public void switchToFrame(String frameId) 
    {
		this.driver.switchTo().frame(frameId);
	}
    
    public void switchToFrame(int index) 
    {
		this.driver.switchTo().frame(index);
	}
    
    /**
     * Gets the value of an element attribute.
     *
     * @param attributeLocator an element locator followed by an attribute
     *
     * @return the value of the specified attribute
     */
    public String getAttribute(WebElement element, String attributeLocator)
    {
    	return element.getAttribute(attributeLocator);
    }
    
    public void enterInput(WebElement element, String value) 
    {
    	String attr=null;
    	waitForVisible(element);
        if((attr=getAttribute(element, "type"))!=null && !attr.equalsIgnoreCase("file"))
        	element.clear();
        element.sendKeys(value);     
    }
       
    /**
     * Check for link with the text
     * @param link text
     */
    public boolean isLinkPresent(String link)
    {
    	String locator = "//a[text()='" + link+"']";
		return isElementPresent(By.xpath(locator));
	}
    
    public void waitForElementToDisappear(By locator) 
    {	
    	WebDriverWait wait = new WebDriverWait(this.driver,WebDriverConstants.WAIT_ONE_MIN);
    	wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    /**
    *
    * @return the name of invoking Class
    */
   public String getPageName() 
   {	
	   String fullClassName = getClass().getName();
	   int i = fullClassName.lastIndexOf(".");
	   String className = fullClassName.substring(i+1);
	   return className;
   }
    
   /**
    * Causes the currently executing thread to sleep (temporarily cease execution)
    * for the specified number of milliseconds.
    *
    * @param millis delay in milliseconds
    */
   public void sleep(long millis) 
   {    
       try 
       {
    	   Thread.sleep(millis);
       }
       catch(Exception e)
       { 
    	   logger.error(e.getMessage()); 
       }
   }
   
   /**
    * Scroll to the particular element in the page
    * @param locator
    */
   public void scrolltoElement(String locator) 
   {
	   Point scroll = this.driver.findElement(By.xpath(locator)).getLocation();
	   JavascriptExecutor js = (JavascriptExecutor) this.driver;
	   js.executeScript("javascript:window.scrollBy("+scroll.getX()+","+scroll.getY()+")");
   }
   
   public void rightClick(By locator)
   {
	   WebElement elementToRightClick = this.driver.findElement(locator);
	   Actions clicker = new Actions(this.driver);
	   clicker.contextClick(elementToRightClick).perform();
   }
   
   public boolean isAlertPresent()
   {
	   try 
	   {
		   WebDriverWait wait = new WebDriverWait(this.driver, WebDriverConstants.WAIT_FOR_VISIBILITY_TIMEOUT_IN_SEC);
		   wait.until(ExpectedConditions.alertIsPresent());
		   return true;
	   } 
	   catch (Exception e) 
	   {
		   return false;
	   }
   }
   
   public void dismissAlertIfPresent()
   {
	   if(isAlertPresent())
	   {
		   Alert alert  = this.driver.switchTo().alert();
		   alert.accept();
	   }
	   else
	   {
		   logger.error("FAIL: dismissAlertIfPresent() - No alert to dismiss");
	   }
   }
   public String getSelectedLabel(WebElement element)
   {
	   WebElement option = new Select(element).getFirstSelectedOption();
	   return option.getText();

   }
   
   /**
    * Gets option value (value attribute) for selected option in the specified select element.
    * @param <SelectElement>
    *
    * @return The text of the element
    */
   public <SelectElement> String getSelectedValue(WebElement element) 
   {    
       return (String)(new Select(element).getFirstSelectedOption()).getText();
       
   }
   
   /**
    * @param <SelectElement>
    * @return "value" attribute of selection option. 
    * getSelectedValue() returns the text not the value attribute
    */
   public <SelectElement> String getSelectedOptionValue(WebElement element) 
   {
       return (String)(new Select(element).getFirstSelectedOption()).getAttribute("value");    
   }
   
   /**
    * Get page title.
    *
    * @return The page title
    */
   public String getTitle() 
   { 
	   return this.driver.getTitle(); 
   }
   
   public String getTextForElementIfPresent(By locator)
   {
	   String text = null;
	   if(isElementPresent(locator))
	   {
		   text = this.driver.findElement(locator).getText();
	   }
	   return text;
   }
   
   public void executeScript(String script)
   {
	   ((JavascriptExecutor) this.driver).executeScript(script);
   }
   
   /**
    * Focus the element identified by <code>locator</code>.
    *
    * @param element
    */
   public void focus(WebElement element) 
   {
       if("input".equals(element.getTagName())){
           element.sendKeys("");
       }
       else{
           new Actions(this.driver).moveToElement(element).perform();
           
       }
   }
      
   public void waitForAJaxCompletion()
   {    
	   try {

		   ExpectedCondition<Boolean> isLoadingFalse = new
				   ExpectedCondition<Boolean>() {

			   public Boolean apply(WebDriver driver) {
				   
				   boolean ajaxCallBack = Boolean.parseBoolean(((JavascriptExecutor)
						   driver).executeScript("return Sys.WebForms.PageRequestManager.getInstance().get_isInAsyncPostBack();").toString());
				   Object obj = ((JavascriptExecutor)
						   driver).executeScript("return !window.ajaxActive");

				   Object jQueryActive = ((JavascriptExecutor)
						   driver).executeScript("return jQuery.active;");

				   if (obj != null && obj.toString().equals("true") && !ajaxCallBack &&
						   jQueryActive.toString().equals("0"))
				   {    
					   return Boolean.valueOf(true);
				   }
				   else
				   {
					   return false;
				   }
			   }

		   };

		   Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(WebDriverConstants.WAIT_ONE_MIN, TimeUnit.SECONDS).pollingEvery(3, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		   wait.until(isLoadingFalse);
	   } 
	   catch (Exception e) 
	   {
		   logger.error(e.getMessage());
	   }
   }
   
   
   public void uploadFile(WebElement element, String fileName)
   {
	  element.sendKeys(fileName);
   }
   
   /**
    * This method will set any parameter string to the system's clipboard.
    */
	public static void setClipboardData(String string) {
		//StringSelection is a class that can be used for copy and paste operations.
		   StringSelection stringSelection = new StringSelection(string);
		   Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		}
	
   /**
    * Simulates the user clicking the "back" button on their browser.
    */
   public void goBack() 
   {
       ((Navigation) this.driver).back();    
   }
   
	/**
    * Assert text <code>s</code> in the HTML source
    *
    * @param s The text string that should be in the HTML source
    */
   public void assertText(String s) 
   {
	   Assert.assertTrue(pageSource().contains(s),"Expect text '"+s+"' in html source but not found.");
   }
   
   /**
    * Assert a title equals exactly a string specified by <code>s</code>.
    *
    * @param s The title
    */
   public void assertTitle(String s) 
   {
        //WebDriverWait wait = new WebDriverWait(this.driver, Long.parseLong(timeout));
       // wait.until(ExpectedConditions.titleIs(s));
       Assert.assertEquals(s, this.driver.getTitle(), "Expect HTML title '" + s + "' but got '" + this.driver.getTitle() + "'.");
   }

   public long getIndexofWebElementMatchingString(List<WebElement> list, String match)
   {
	   int index = -1;
	   for(int i=0;i<list.size();i++)
	   {
		   if(list.get(i).getText().equals(match))
		   {
			   index = i;
			   break;
		   }
	   }
	return index;
   }
   
}
