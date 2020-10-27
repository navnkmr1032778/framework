package com.solutionstar.swaftee.webdriverbase;

import java.io.File;

import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.solutionstar.swaftee.docker.startDocker;
import com.solutionstar.swaftee.docker.stopDocker;
import com.solutionstar.swaftee.jira.Zephyr;
import com.solutionstar.swaftee.jira.ZephyrUtils;
import com.solutionstar.swaftee.utils.CommonUtils;
import com.solutionstar.swaftee.webdriverFactory.AppDriver;

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
