package com.solutionstar.swaftee.docker;

import java.io.IOException;

public class A {

	public static void main(String[] args) throws InterruptedException, IOException {

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
		ProcessBuilder processBuilder = new ProcessBuilder("zsh",
				"/Users/guestuser/Downloads/framework/resources/docker/dockerUp.sh");
//		processBuilder.directory(new File("/Users/guestuser/Downloads/framework/resources/docker"));
//		String path = System.getenv("PATH");
		processBuilder.environment().put("PATH", "/usr/local/bin");
//		processBuilder.environment().put("PATH", path + ":/usr/local/bin");
		Process process = processBuilder.start();

//		int exitValue = process.waitFor();
//		if (exitValue != 0) {
//			// check for errors
//			new BufferedInputStream(process.getErrorStream());
//			throw new RuntimeException("execution of script failed!");
//		}

	}

}
