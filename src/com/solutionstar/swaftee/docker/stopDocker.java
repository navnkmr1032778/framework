package com.solutionstar.swaftee.docker;

import java.io.*;
import java.util.*;

import org.testng.*;

public class stopDocker {

	public void stopFile() throws IOException, InterruptedException {

		boolean flag = false;
		Runtime runtime = Runtime.getRuntime();
		runtime.exec("cmd /c start dockerDown.bat", null,
				new File(new File(System.getProperty("user.dir")).getParent() + "/swaftee/resources/docker"));

		String f = System.getProperty("user.dir") + "/resources/docker/output.txt";

		Calendar cal = Calendar.getInstance();// 2:44 15th second
		cal.add(Calendar.SECOND, 45);// 2:44 45seconds
		long stopnow = cal.getTimeInMillis();
		Thread.sleep(3000);

		while (System.currentTimeMillis() < stopnow) {
			if (flag) {
				break;
			}

			BufferedReader reader = new BufferedReader(new FileReader(f));
			String currentLine = reader.readLine();
			while (currentLine != null && !flag)

			{

				if (currentLine.contains("selenium-hub exited")) {
					System.out.println("found my text");
					flag = true;// 14th seconds
					break;
				}

				currentLine = reader.readLine();
			}
			reader.close();

		}

		Assert.assertTrue(flag);

	}

}
