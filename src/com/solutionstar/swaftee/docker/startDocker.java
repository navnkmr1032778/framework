package com.solutionstar.swaftee.docker;

import java.io.*;
import java.util.*;

public class startDocker {

	public void startFile() throws IOException, InterruptedException {

//		runtime.exec("cmd /c start dockerUp.bat", null,
//				new File(new File(System.getProperty("user.dir")).getParent() + "/swaftee/resources/docker"));
//
//		String f = new File(System.getProperty("user.dir")).getParent() + "/swaftee/resources/docker/output.txt";

//		try {
//		String[] env = { "PATH=/bin/zsh:/usr/bin/" };
//
//		Process proc = Runtime.getRuntime().exec("sh /Users/guestuser/Downloads/framework/resources/docker/dockerUp.sh",
//				env);
//
//		StringBuffer output = new StringBuffer();
//		BufferedReader reader1 = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//		String line = "";
//		while ((line = reader1.readLine()) != null) {
//			output.append(line + "\n");
//		}
//		System.out.println("### " + output);
//		proc.waitFor();
//		ProcessBuilder processBuilder = new ProcessBuilder("zsh",
//				"/Users/guestuser/Downloads/framework/resources/docker/dockerUp.sh");
//		processBuilder.directory(new File("/Users/guestuser/Downloads/framework/resources/docker"));
//		String path = System.getenv("PATH");
//		processBuilder.environment().put("PATH", "/usr/local/bin");
//		processBuilder.environment().put("PATH", path + ":/usr/local/bin");
//		Process process = processBuilder.start();

//		int exitValue = process.waitFor();
//		if (exitValue != 0) {
//			// check for errors
//			new BufferedInputStream(process.getErrorStream());
//			throw new RuntimeException("execution of script failed!");
//		}
		boolean flag = false;

		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec("/Users/guestuser/Downloads/framework/resources/docker/dockerUp.sh");
		proc.waitFor();

		StringBuffer output = new StringBuffer();
		BufferedReader reader1 = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String line = "";
		while ((line = reader1.readLine()) != null) {
			output.append(line + "\n");
		}
		System.out.println("### " + output);
		String f = "/Users/guestuser/Downloads/framework/resources/docker/output.txt";

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

				if (currentLine.contains("registered to the hub and ready to use")) {
					System.out.println("found my text");
					flag = true;// 14th seconds
					break;
				}

				currentLine = reader.readLine();
			}
			reader.close();

		}

		// Assert.assertTrue(flag);
		try {
			runtime.exec("sh scale.sh", null, new File("/Users/guestuser/Downloads/framework/resources/docker/"));
		} catch (Exception e) {
		}
		Thread.sleep(1500);

	}

}
