package com.solutionstar.swaftee.webdriverbase;

import java.io.*;

import org.testng.*;
import org.testng.annotations.*;

import com.solutionstar.swaftee.docker.*;
import com.solutionstar.swaftee.jira.*;
import com.solutionstar.swaftee.utils.*;
import com.solutionstar.swaftee.webdriverFactory.*;

@Listeners(AppDriver.class)
public class AppTest extends AppDriver {

	CommonUtils utils = new CommonUtils();

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite(ITestContext ctx) {
		if (Boolean.parseBoolean(System.getProperty("userdocker", "true"))) {
			File fi = null;
			try {
				fi = new File("output.txt");
				if (fi.delete()) {
					System.out.println("file deleted successfully");
				}
				@SuppressWarnings("unused")
				startDocker s = new startDocker();
				// s.startFile();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// setDriverExecutable();
		logger.info("XML FileName : " + ctx.getCurrentXmlTest().getSuite().getFileName());
		logger.info("Executing the Suite : " + ctx.getSuite().getName());
	}

	@AfterSuite(alwaysRun = true)
	public void AfterSuite() {
		if (jiraUpdate()) {
			ZephyrUtils.updateExecutionStatusInJIRA();
			Zephyr.generateHtmlReport();
			if (Boolean.parseBoolean(System.getProperty("userdocker", "false"))) {
				try {
					stopDocker d = new stopDocker();
					d.stopFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
