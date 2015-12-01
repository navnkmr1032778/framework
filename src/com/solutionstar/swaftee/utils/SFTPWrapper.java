package com.solutionstar.swaftee.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SFTPWrapper
{
	private String domain = "";
	private String username = "";
	private String password = "";
	private int port = 22;
	private String baseDirectory = "";

	ChannelSftp channelSftp = null;

	public String getBaseDirectory()
	{
		return baseDirectory;
	}

	public void setBaseDirectory(String baseDirectory)
	{
		this.baseDirectory = baseDirectory;
	}

	Session session = null;
	ChannelExec channelExec = null;

	private String touchFilename = null;

	protected static Logger logger = LoggerFactory.getLogger(SFTPWrapper.class);

	public SFTPWrapper(String domain, int port, String username, String password, String baseDirectory)
	{
		this.domain = domain;
		this.password = password;
		this.username = username;
		this.port = port;
		this.baseDirectory = baseDirectory;
	}

	public void startSession() throws JSchException
	{
		if (session == null)
		{
			JSch jsch = new JSch();
			session = jsch.getSession(username, domain, port);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
		}
	}

	public void endSession()
	{
		if (session != null)
		{
			session.disconnect();
		}
	}

	public boolean hasSession()
	{
		return session.isConnected();
	}

	public void startExecSession() throws JSchException
	{
		startSession();
		channelExec = (ChannelExec) session.openChannel("exec");
	}

	public void endExecSession() throws JSchException
	{
		if (channelExec != null)
		{
			channelExec.disconnect();
		}
		endSession();
	}

	public void createTouchFile() throws JSchException, IOException
	{
		startExecSession();
		InputStream in = channelExec.getInputStream();
		touchFilename = "" + System.currentTimeMillis();
		channelExec.setCommand("cd " + baseDirectory + ";touch " + touchFilename);
		channelExec.connect();
		endExecSession();
	}

	public void updateTouchFile() throws JSchException, IOException
	{
		if (touchFilename != null)
		{

		}
		else
		{
			createTouchFile();
		}
	}

	public void deleteTouchFile() throws Exception
	{
		if (touchFilename != null)
		{
			startExecSession();
			channelExec.setCommand("cd " + baseDirectory + ";rm " + touchFilename);
			channelExec.connect();
			int exitStatus = channelExec.getExitStatus();
			endExecSession();
			touchFilename = null;
			if (exitStatus < 0)
			{
				System.out.println("Done, but exit status not set!");
			}
			else if (exitStatus > 0)
			{
				throw new Exception("Done, but with error!");
			}
			else
			{
				System.out.println("Done!");
			}
		}
	}
}
