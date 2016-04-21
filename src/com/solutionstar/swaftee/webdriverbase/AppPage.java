package com.solutionstar.swaftee.webdriverbase;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
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
import com.solutionstar.swaftee.utils.CommonUtils;
import com.solutionstar.swaftee.webdriverhelpers.BaseDriverHelper;

public class AppPage extends TestListenerAdapter 
{
	protected static Logger logger = LoggerFactory.getLogger(AppPage.class.getName());
	protected WebDriver driver;
	JavascriptExecutor javaScriptExecutor; 
	public static boolean mobileEmulationExecution=false;
	BaseDriverHelper baseDriverHelper = new BaseDriverHelper();

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
		String windowSize = System.getProperty("windowSize","");
		//android does not supports maximizeWindow;
		if(windowSize.equals("") && !baseDriverHelper.ismobile())
			maximizeWindow();
		if(baseDriverHelper.ismobile() && !baseDriverHelper.getEmulationDeviceName().equals("noEmul"))
			mobileEmulationExecution=true;
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

	public void waitForURLToChange(String url)
	{
		final String currentURL = url;
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(WebDriverConstants.WAIT_ONE_MIN, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		wait.until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver driver) 
			{
				return !getCurrentUrl().equals(currentURL);
			}
				});
		return;
	}
	
	/**
	 * 
	 * @param urlText
	 * @param timeout in seconds
	 */
	public void waitForURLContainingText(String urlText, int timeout)
	{
		final String expectedURL = urlText;
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(timeout, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		wait.until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver driver) 
			{
				return getCurrentUrl().contains(expectedURL);
			}
				});
		return;
	}
	
	public Set<Cookie> getCookies()
	{
		return this.driver.manage().getCookies();
	}
	
	public HashMap<String,String> getCookiesHash()
	{
		Set<Cookie> cookies = getCookies();
		HashMap<String, String> cookieHash = new HashMap<String, String>();
		for (Cookie c : cookies)
		{
			cookieHash.put(c.getName(), c.getValue());
		}
		return cookieHash;
	}
	
	public void deleteCookies() 
	{
		this.driver.manage().deleteAllCookies();
	}

	public String pageSource() 
	{
		return this.driver.getPageSource();
	}

	public JavascriptExecutor getJavaScriptExecutor()
	{
		if( javaScriptExecutor == null)
			javaScriptExecutor = (JavascriptExecutor) driver;
		return javaScriptExecutor;
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

	public boolean isElementPresentAndDisplayed(By xpath)
	{
		try
		{
			return isElementPresentAndDisplayed(this.driver.findElement(xpath));
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
				new WebDriverWait(driver, WebDriverConstants.WAIT_FOR_VISIBILITY_TIMEOUT_IN_SEC);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public void waitForVisible(WebElement element, Integer timeout) 
	{
		WebDriverWait wait =
				new WebDriverWait(driver, timeout);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public void waitForVisible(By locator) 
	{
		WebDriverWait wait =
				new WebDriverWait(driver,WebDriverConstants.WAIT_FOR_VISIBILITY_TIMEOUT_IN_SEC);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public void waitForElementToBeEnabled(WebElement e)
	{
		final WebElement web = e;
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(WebDriverConstants.WAIT_ONE_MIN, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		wait.until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver driver) 
			{
				return web.isEnabled();
			}
				});
		return;
	}
	
	public void waitForElementToBeEnabled(By locator)
	{
		final By loc = locator;
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(WebDriverConstants.WAIT_ONE_MIN, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		wait.until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver driver) 
			{
				return driver.findElement(locator).isEnabled();
			}
				});
		return;
	}
	

	public void waitForElementToContainText(WebElement e, String text)
	{
		waitForElementToBeEnabled(e);
		if(isElementPresentAndDisplayed(e))
		{
			final String innerText = text;
			final WebElement element = e;
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(WebDriverConstants.WAIT_ONE_MIN, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
			wait.until(new ExpectedCondition<Boolean>() 
			{
				public Boolean apply(WebDriver driver) 
				{
					return element.getText().contains(innerText);
				}
			});
		}
		return;
	}
	
	public void waitForElementToContainText(By locator, String text)
	{
		waitForElementToBeEnabled(locator);
		if(isElementPresentAndDisplayed(locator))
		{
			final String innerText = text;
			final By loc = locator;
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(WebDriverConstants.WAIT_ONE_MIN, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
			wait.until(new ExpectedCondition<Boolean>() 
			{
				public Boolean apply(WebDriver driver) 
				{
					return driver.findElement(loc).getText().contains(innerText);
				}
			});
		}
		return;
	}
	
	
	public void waitForPageLoadComplete() 
	{
		waitForPageLoad(WebDriverConstants.MAX_TIMEOUT_PAGE_LOAD);
		waitForAJaxCompletion();
		return;
	}

	public void waitForPageLoadComplete(Integer timeout) 
	{
		waitForPageLoad(timeout);
		return;
	}

	public void clearAndType(WebElement element, String text) 
	{		
		element.clear();
		element.sendKeys(text);
	}

	public void setTextUsingJS(WebElement element,String text)
	{
		getJavaScriptExecutor().executeScript("arguments[0].value=arguments[1]", element, text);
	}
	
	public void hoverOverElementUsingJS(WebElement element)
	{
		String js = "var eventObj=document.createEvent('MouseEvent');eventObj.initMouseEvent('mouseover', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, false, 0, null);arguments[0].dispatchEvent(eventObj);"; 
		getJavaScriptExecutor().executeScript(js,element);
		sleep(1000);
	}
	
	public void clearAttrValueUsingElementID(String elementId)
	{
		String query = "document.getElementById('"+elementId+"').value = ''";
		getJavaScriptExecutor().executeScript(query);
	}
	
	public void clearFirstElementAttrValueUsingElementName(String elementName)
	{
		String query = "var eleList = document.getElementsByName('"+elementName+"'); eleList[0].value = ''";
		getJavaScriptExecutor().executeScript(query);
	}
	public void takeScreenShot() 
	{
		waitForPageLoadComplete();
		waitForAJaxCompletion();
		String fileName=WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT_BASE;
		if(baseDriverHelper.getIsDryRun()==true && baseDriverHelper.getBaseDirLocation()!=null)
		{
			fileName=baseDriverHelper.getBaseDirLocation();
		}
		else if(baseDriverHelper.getCompareImages()==true && baseDriverHelper.getCurrentDirLocation()!=null)
		{
			fileName=baseDriverHelper.getCurrentDirLocation();
		}
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try 
		{
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			FileUtils.copyFile(scrFile, new File(fileName+stackTraceElements[2].getMethodName()+".png"));	//Files.move(src,dest);
		} 
		catch (IOException e) 
		{
			logger.info("Error While taking Screen Shot");
			e.printStackTrace();
		}
	}

	public void takeScreenShot(String fileName) 
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

	public void takeScreenShot(String fileName, WebElement element) 
	{
		try 
		{
			scrolltoElement(element);
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage  fullImg = ImageIO.read(scrFile);

			Point point = element.getLocation();
			int eleWidth = element.getSize().getWidth();
			int eleHeight = element.getSize().getHeight();

			if(fullImg.getHeight()>point.getY() && fullImg.getWidth()>point.getX())
			{
				BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
				ImageIO.write(eleScreenshot, "png", scrFile);
				FileUtils.copyFile(scrFile, new File(fileName));
			}
			else
			{
				logger.info("web element size is greate than image");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			logger.error("Error While taking Screen Shot");
		}
	}

	public void takeScreenShot(WebElement element)
	{
		try 
		{
			String fileName= WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT_BASE;
			BaseDriverHelper baseDriverHelper=new BaseDriverHelper();
			if(baseDriverHelper.getIsDryRun()==true && baseDriverHelper.getBaseDirLocation()!=null)
			{
				fileName=baseDriverHelper.getBaseDirLocation();
			}
			else if(baseDriverHelper.getCompareImages()==true && baseDriverHelper.getCurrentDirLocation()!=null)
			{
				fileName=baseDriverHelper.getCurrentDirLocation();
			}
			takeScreenShot(fileName,element);
		} 
		catch (Exception e) 
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

	public void selectDropDownContainingText(WebElement element, String value)
	{
		Select select = new Select(element);
		List<String> allOptions = getAllSelectOptions(element);
		for(String s: allOptions)
		{
			if(s.contains(value))
			{
				select.selectByVisibleText(s);
				break;
			}
		} 
	}
	
	public void selectSilverLightDropDown(WebElement element, String value) throws Exception
	{
		List<WebElement> options = _getAllOptionsFromSilverLightDropDown(element);
		for(WebElement option : options)
		{
			
			if(option.getText().equals(value))
			{
				scrolltoElement(option);
				option.click();
				sleep(500);
				return;
			}
		}
		throw new Exception("Value not found exception");
	}
	
	private List<WebElement> _getAllOptionsFromSilverLightDropDown(WebElement element)
	{
		String inputId = element.getAttribute("id");
		String wrapperId = inputId.replaceFirst("_Input$", "");
		String dropDownId = wrapperId + "_DropDown";
		element.click();
		waitForVisible(By.id(dropDownId));
		sleep(500); //for the animation to end
		List<WebElement> options = driver.findElement(By.id(dropDownId)).findElements(By.tagName("li"));
		return options;
	}
	
	public List<String> getAllOptionsFromSilverLightDropDown(WebElement element)
	{
		List<String> optionText = new ArrayList<String>();
		List<WebElement> options = _getAllOptionsFromSilverLightDropDown(element);
		for(WebElement option : options)
		{
			optionText.add(option.getText());
		}
		return optionText;
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
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(timeout, TimeUnit.SECONDS).pollingEvery(3, TimeUnit.SECONDS).ignoring(NoSuchElementException.class,WebDriverException.class);
		wait.until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver driver) 
			{
				return (getJavaScriptExecutor()).executeScript("return document.readyState").equals(
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
		CommonUtils utils = new CommonUtils();
		if(utils.isNumeric(date))
		{
			date = utils.getFutureDate(Integer.parseInt(date));
		}
		getJavaScriptExecutor().executeScript( "arguments[0].removeAttribute('readonly','readonly')",element);
		sleep(500);
		element.clear();
		element.sendKeys(date);
		element.sendKeys(Keys.TAB);
		sleep(500);
	}

	public void scrollDown(String xVal, String yVal) 
	{
		getJavaScriptExecutor().executeScript("scroll("+ xVal +", "+  yVal+");");
	}

	public void maximizeWindow()
	{
		driver.manage().window().maximize();
	}

	public void windowResize(int hight, int width)
	{
		Dimension di = new Dimension(width, hight);
		driver.manage().window().setSize(di);
	}
	
	/***
	 * This Method will get the current screen resolution and set that resolution to browser window
	 */
	public void maximizeWindowToFullScreen()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();  
		int Width = (int) toolkit.getScreenSize().getWidth();  
		int Height = (int) toolkit.getScreenSize().getHeight();  
		Dimension screenResolution = new Dimension(Width, Height);  
		logger.info("Setting the screen resolution as Height = " + Height + " and Width = "+ Width);
		driver.manage().window().setSize(screenResolution);  

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
	
	public String getVisibleTextOfElement(WebElement elem)
	{
		String visibleText= (String) getJavaScriptExecutor().executeScript("var clone = $(arguments[0]).clone();"
				+ "clone.appendTo('body').find(':hidden').remove();"
				+ "var text = clone.text();"
				+ "clone.remove(); return text;", elem);
		visibleText=visibleText.replaceAll("\\s+", " ");
		return visibleText;
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
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(WebDriverConstants.WAIT_TWO_MIN, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		wait.until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver driver) 
			{
				return !getWindowHandles().contains(window);
			}
				});
		return;
	}
	
	public void waitForNewWindow(int winCount)
	{
		final int count = winCount;
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(WebDriverConstants.WAIT_ONE_MIN, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		wait.until(new ExpectedCondition<Boolean>() 
				{
			public Boolean apply(WebDriver driver) 
			{
				return getWindowHandles().size()>count;
			}
				});
		return;
	}

	public boolean switchToNextWindowClosingCurrent()
	{
		boolean switchSuccess = false;
		List<String>windows = new ArrayList<String>(getWindowHandles());
		String currentWindow = getWindowHandle();
		if(windows.size()==1)
		{
			return true;
		}
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
		String handle;
		for(int index=0; index<windows.size();index++)
		{
			handle = windows.get(index);
			this.driver.switchTo().window(handle);
			if(!currentWindow.equals(handle))
			{
				this.driver.close();
			}
		}
		this.driver.switchTo().window(currentWindow);
		return switchSuccess;
	}

	public boolean switchToNextWindow()
	{
		boolean switchSuccess = false;
		if(getWindowHandles().size()==1)
		{
			logger.info("One window present..Waiting for new window to open");
			waitForNewWindow(1);
		}
		List<String>windows = new ArrayList<String>(getWindowHandles());
		String currentWindow = getWindowHandle();
		int count = windows.size();
		for(int index=0; index<count;index++)
		{
			if(currentWindow.equals(windows.get(index)))
			{
				if(index==count-1)
				{
					logger.info("switchToNextWindow() - Current window is last window..Switch not possible");
					break;
				}
				switchSuccess = switchToNthWindow(index+1);  
				break;
			}
		}
		return switchSuccess;
	}

	public boolean switchToNextWindow(int currentHandleCount)
	{
		boolean switchSuccess = false;
		if(getWindowHandles().size()==currentHandleCount)
		{
			logger.info("Waiting for new window to open");
			waitForNewWindow(currentHandleCount);
		}
		List<String> windows = new ArrayList<String>(getWindowHandles());
		String currentWindow = getWindowHandle();
		int count = windows.size();
		for(int index=0; index<count;index++)
		{
			if(currentWindow.equals(windows.get(index)))
			{
				if(index==count-1)
				{
					logger.info("switchToNextWindow() - Current window is last window..Switch not possible");
					break;
				}
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

	public void switchToWindowClosingCurrent(String windowHandle)
	{
		this.driver.close();
		switchToWindow(windowHandle);
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
		sleep(500);
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

	public void switchToWindowClosingOthers(String handle)
	{
		List<String>windows = new ArrayList<String>(getWindowHandles());

		for(String window : windows)
		{
			this.driver.switchTo().window(window);
			if(!window.equals(handle))
				this.driver.close();
		}

		this.driver.switchTo().window(handle);
	}

	public String getValueUsingJavaScript(String by, String ele)
	{
		String val = null;
		try{
			switch (JavaScriptSelector.valueOf(by.toUpperCase())) 
			{
			case ID:
				val = (String) getJavaScriptExecutor().executeScript("return document.getElementById('"+ele+"').value");
				break;
			case CLASS:
				val = (String) getJavaScriptExecutor().executeScript("return document.getElementsByClassName('"+ele+"').value");
				break;
			case TAGNAME:
				val = (String) getJavaScriptExecutor().executeScript("return document.getElementsByTagName('"+ele+"').value");
				break;
			case NAME:
				val = (String) getJavaScriptExecutor().executeScript("return document.getElementsByName('"+ele+"').value");
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
			switch (JavaScriptSelector.valueOf(by.toUpperCase())) 
			{
			case ID:
				System.out.println(("document.getElementById('"+ele+"').value = \""+ val + "\"" ));
				getJavaScriptExecutor().executeScript("document.getElementById('"+ele+"').value = \""+ val + "\"" );
				break;
			case CLASS:
				getJavaScriptExecutor().executeScript("document.getElementsByClassName('"+ele+"').value = \""+ val + "\"" );
				break;
			case TAGNAME:
				getJavaScriptExecutor().executeScript("document.getElementsByTagName('"+ele+"').value = \""+ val + "\"" );
				break;
			case NAME:
				getJavaScriptExecutor().executeScript("document.getElementsByName('"+ele+"').value = \""+ val + "\"" );
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
		monkeyPatch();
	}

	public void closeWindow()
	{
		this.driver.close();
		sleep(500);
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

	public boolean hasSelectOption(WebElement drpDown, String value) {
		return getAllSelectOptions(drpDown).contains(value);
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



	public void switchToFrame(WebElement frame)
	{
		this.driver.switchTo().frame(frame);
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

	public void clickOnLinkWithText(String linkText)
	{
		By locator = By.xpath("//a[text()='" + linkText+"']");
		if(isElementPresent(locator))
		{
			driver.findElement(locator).click();
		}
	}

	/**
	 * check HTML validity of element
	 * @param locator
	 */
	public boolean checkValidityOfElement(WebElement e)
	{
		boolean res=(boolean)getJavaScriptExecutor().executeScript("return arguments[0].checkValidity()",e);
		logger.info("check validity: "+res);
		return res;
	}
	
	public void waitForElementToDisappear(By locator) 
	{	
		WebDriverWait wait = new WebDriverWait(this.driver,WebDriverConstants.WAIT_TWO_MIN);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	public void waitForElementToDisappear(WebElement e)
	{
		WebDriverWait wait = new WebDriverWait(this.driver,WebDriverConstants.WAIT_TWO_MIN);
		if(isElementPresent(e))
			wait.until(invisibilityOfElementLocated(e));
	}
	
	public void waitForElementToDisappear(WebElement e,int timeOut)
	{
		WebDriverWait wait = new WebDriverWait(this.driver,timeOut);
		if(isElementPresent(e))
			wait.until(invisibilityOfElementLocated(e));
	}

	public ExpectedCondition<Boolean> invisibilityOfElementLocated(final WebElement element) {
		return new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				try {
					return !(element.isDisplayed());
				} catch (NoSuchElementException e) {
					// Returns true because the element is not present in DOM. The
					// try block checks if the element is present but is invisible.
					return true;
				} catch (StaleElementReferenceException e) {
					// Returns true because stale element reference implies that element
					// is no longer visible.
					return true;
				}
			}
		};
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
		try
		{
			WebElement element = this.driver.findElement(By.xpath(locator));

			scrolltoElement(element);
		}
		catch(Exception ex)
		{
			logger.info("exception in scroll to element: "+ExceptionUtils.getFullStackTrace(ex));
		}
	}

	/**
	 * Scroll to the particular element in the page
	 * @param locator
	 */
	public void scrolltoElement(WebElement element) 
	{
		getJavaScriptExecutor().executeScript("arguments[0].scrollIntoView(false)",element);
		sleep(500);
	}

	public void scrollTopToElement(WebElement element) 
	{
		getJavaScriptExecutor().executeScript("arguments[0].scrollIntoView(true)",element);
		sleep(500);
	}

	public void rightClick(By locator)
	{
		WebElement elementToRightClick = this.driver.findElement(locator);
		Actions clicker = new Actions(this.driver);
		clicker.contextClick(elementToRightClick).perform();
	}

	public boolean waitForAlert()
	{
		try 
		{
			WebDriverWait wait = new WebDriverWait(this.driver, WebDriverConstants.WAIT_FOR_VISIBILITY_TIMEOUT_IN_SEC);
			wait.until(ExpectedConditions.alertIsPresent() );
			return true;
		} 
		catch (Exception e) 
		{
			return false;
		}
	}

	public boolean isAlertPresent() 
	{ 
		try 
		{ 
			driver.switchTo().alert(); 
			return true; 
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace();
			logger.info("Error while checking if alert is present"+ e.getMessage());
			return false; 
		} 
	} 

	public Alert switchToAlert() throws Exception
	{
		Alert alert = driver.switchTo().alert();
		return alert;
	}

	public String getAlertText() throws Exception
	{
		Alert alert = driver.switchTo().alert();
		return alert.getText();
	}

	public void dismissAlertIfPresent(boolean shouldWait)
	{
		boolean dismissed = false;
		if(shouldWait)
		{
			if(waitForAlert())
			{
				Alert alert  = this.driver.switchTo().alert();
				alert.accept();
				dismissed = true;
			}
		}
		else
		{
			//Arbitrary wait for alert to appear
			sleep(100);
			if(isAlertPresent())
			{
				try
				{
					driver.switchTo().alert().accept();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					logger.info("Error in dismissing alert.."+e.getMessage());
				}
				dismissed = true;
			}
		}
		if(!dismissed)
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

	public Object executeScript(String script)
	{
		return ((JavascriptExecutor) this.driver).executeScript(script);
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
		try
		{
			ExpectedCondition<Boolean> isLoadingFalse = new ExpectedCondition<Boolean>()
			{
				public Boolean apply(WebDriver driver)
				{
					String ajaxCount = (String) ((JavascriptExecutor) driver)
							.executeScript("return '' + XMLHttpRequest.prototype.ajaxCount");
					if (ajaxCount != null && ajaxCount.equals("undefined"))
					{
						monkeyPatch();
						return true;
					}
					if (ajaxCount != null && Double.parseDouble(ajaxCount) > 0.0d)
					{
						return false;
					}
					else
					{
						return true;
					}
				}
			};
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
					.withTimeout(WebDriverConstants.WAIT_ONE_MIN, TimeUnit.SECONDS).pollingEvery(500, TimeUnit.MILLISECONDS)
					.ignoring(NoSuchElementException.class);
			wait.until(isLoadingFalse);
		}
		catch (Exception e)
		{
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
	}

	public void monkeyPatch()
	{	
/*
 * The actual source code for monkey patch, the compressed version while executing
(function(xhr) {
	xhr.ajaxCount = 0;
	function incrementAjaxCount() {
		xhr.ajaxCount++;
		console.log('Ajax count when triggering ajax send: ' + xhr.ajaxCount);
	}
	function decrementAjaxCount() {
		if(xhr.ajaxCount > 0)
			xhr.ajaxCount--;
		console.log('Ajax count when resolving ajax send: ' + xhr.ajaxCount);
	}
	var send = xhr.send;
	xhr.send = function(data) {
		this.addEventListener('readystatechange', function() { 
			if(this != null && this.readyState == XMLHttpRequest.DONE) {
				decrementAjaxCount();
			}
		}, false);
		incrementAjaxCount();
		return send.apply(this, arguments);
	};
	var abort = xhr.abort;
	xhr.abort = function(data) {
		decrementAjaxCount();
		return abort.apply(this, arguments);
	};
	return xhr;
})(XMLHttpRequest.prototype);
*/
		String ajaxCount = (String) ((JavascriptExecutor) driver)
				.executeScript("return '' + XMLHttpRequest.prototype.ajaxCount");
		if (ajaxCount != null && ajaxCount.equals("undefined"))
		{
			getJavaScriptExecutor().executeScript(
					"!function(t){function n(){t.ajaxCount++,console.log(\"Ajax count when triggering ajax send: \"+t.ajaxCount)}function a(){t.ajaxCount>0&&t.ajaxCount--,console.log(\"Ajax count when resolving ajax send: \"+t.ajaxCount)}t.ajaxCount=0;var e=t.send;t.send=function(t){return this.addEventListener(\"readystatechange\",function(){null!=this&&this.readyState==XMLHttpRequest.DONE&&a()},!1),n(),e.apply(this,arguments)};var o=t.abort;return t.abort=function(t){return a(),o.apply(this,arguments)},t}(XMLHttpRequest.prototype);");
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
		this.driver.navigate().back();
	}

	/**
	 * Assert text <code>s</code> in the HTML source
	 *
	 * @param s The text string that should be in the HTML source
	 */
	public boolean isPageContainsText(String s)
	{
		return pageSource().contains(s);
	}

	public void assertText(String s) 
	{
		Assert.assertTrue(pageSource().contains(s),"Expect text '"+s+"' in html source but not found.");
	}

	/**
	 * Assert text <code>s</code> in the HTML source
	 *
	 * @param s The text string that should be in the HTML source
	 */
	public void assertTextNotPresent(String s) 
	{
		Assert.assertTrue(!(pageSource().contains(s)),"Expect text '"+s+"' in html source is found.");
	}

	/**
	 * Assert a title equals exactly a string specified by <code>s</code>.
	 *
	 * @param s The title
	 */
	public void assertTitle(String s) 
	{
		Assert.assertEquals(s, this.driver.getTitle(), "Expect HTML title '" + s + "' but got '" + this.driver.getTitle() + "'.");
	}

	public long getIndexofWebElementMatchingString(List<WebElement> list, String match)
	{
		int index = -1;
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i).getText().trim().equals(match))
			{
				index = i;
				break;
			}
		}
		return index;
	}

	public void waitUntilValueAttributeForElement(WebElement e, String value)
	{
		WebDriverWait wait =
				new WebDriverWait(driver,WebDriverConstants.WAIT_FOR_VISIBILITY_TIMEOUT_IN_SEC);
		wait.until(ExpectedConditions.textToBePresentInElementValue(e, value));
	}

	public String getAbsolutePath(String filePath)
	{
		String absolutePath = null;
		try{
			File file = new File(filePath);
			absolutePath = file.getAbsolutePath();
		}catch(Exception e){
			e.printStackTrace();
		}
		return absolutePath;
	}

	public boolean hoverOnElement(WebElement element)
	{
		try{
			Actions builder = new Actions(this.driver); 
			Actions hoverOverRegistrar = builder.moveToElement(element);
			hoverOverRegistrar.perform();
			Thread.sleep(500);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	/***
	 * Verify that the given attribute is present inside the webelement
	 * @param element - WebElement
	 * @param attribute - attribute text
	 * @return boolean
	 */

	public boolean isAttribtuePresent(WebElement element, String attribute)
	{
		Boolean result = false;
		try {
			String value = element.getAttribute(attribute);
			if (value != null){
				result = true;
			}
		} catch (Exception e) {}

		return result;
	}

	public String getAttributeValue(WebElement element, String attribute)
	{
		try{
			if(isAttribtuePresent(element, attribute))
			{
				return element.getAttribute(attribute);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}

	public void gotoURL(String url)
	{
		this.driver.get(url);
		waitForAJaxCompletion();
	}
	
	public void gotoURLInNewWindow(String url)
	{
		getJavaScriptExecutor().executeScript("window.open('"+url+"','_blank');");
	}

	public boolean hasEditableFields(WebElement element) {
		boolean flag = (Boolean)(getJavaScriptExecutor().executeScript("var inputElements = arguments[0].getElementsByTagName('input');" + 
				"	var textareaElements = arguments[0].getElementsByTagName('textarea');" + 
				"	var selectElements = arguments[0].getElementsByTagName('select');" + 
				"	var flag = false;" +  
				"	for(var index = 0; !flag && index < inputElements.length; index++) {" + 
				"		if(inputElements[index].type == 'hidden') {" + 
				"			continue;" + 
				"		}" + 
				"		if(!inputElements[index].disabled) {" + 
				"			flag = true;" + 
				"		}" + 
				"	}" + 
				"	for(var index = 0; !flag && index < textareaElements.length; index++) {" + 
				"		if(!textareaElements[index].disabled) {" + 
				"			flag = true;" + 
				"		}" + 
				"	}" + 
				"	for(var index = 0; !flag && index < selectElements.length; index++) {" + 
				"		if(!selectElements[index].disabled) {" + 
				"			flag = true;" + 
				"		}" + 
				"	}" + 
				"	return flag;", element));
		return flag;
	}
	
	public void elementHighlighter(WebElement element)
	{
		scrolltoElement(element);
		getJavaScriptExecutor().executeScript("arguments[0].setAttribute(\"style\", \"border: 5px solid red;\");", element);
	}
	
	public void waitImplicitly()
	{
		driver.manage().timeouts().implicitlyWait(WebDriverConstants.WAIT_HALF_MIN, TimeUnit.SECONDS);
	}
	
	public void waitImplicitly(int timeOutInSeconds)
	{
		driver.manage().timeouts().implicitlyWait(timeOutInSeconds, TimeUnit.SECONDS);
	}
	public void scrollElementToUserView(WebElement elem)
	{
		getJavaScriptExecutor().executeScript("window.scrollTo(" + (elem.getLocation().x-500) + "," +(elem.getLocation().y-500) + ");");
	}

	public void scrollHorizontallyTo(WebElement elem,WebElement container) 
	{
		getJavaScriptExecutor().executeScript("$(arguments[0],arguments[1])[0].scrollIntoView(false)",elem,container);
	}


}
