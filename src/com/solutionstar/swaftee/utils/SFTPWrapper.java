package com.solutionstar.swaftee.utils;

import java.io.BufferedReader;
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
	private String touchFilename = null;
	private ChannelSftp channelSftp = null;
	private Session session = null;
	private ChannelExec channelExec = null;

	protected static Logger logger = LoggerFactory.getLogger(SFTPWrapper.class);

	public SFTPWrapper(String domain, int port, String username, String password, String baseDirectory)
	{
		this.domain = domain;
		this.password = password;
		this.username = username;
		this.port = port;
		this.baseDirectory = baseDirectory;
	}
	
	public String getBaseDirectory()
	{
		return baseDirectory;
	}

	public void setBaseDirectory(String baseDirectory)
	{
		this.baseDirectory = baseDirectory;
	}
	
	public String getTouchFilename()
	{
		return touchFilename;
	}

	public void startSession() throws JSchException
	{
		if (session == null || !hasSession())
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
		if (hasSession())
		{
			session.disconnect();
			session = null;
		}
	}

	public boolean hasSession()
	{
		if(session == null)
			return false;
		return session.isConnected();
	}
	
	public boolean hasChannelExec()
	{
		if(channelExec == null)
			return false;
		return channelExec.isConnected();
	}

	public void startExecSession() throws JSchException
	{
		if(!hasChannelExec())
		{
			startSession();
			channelExec = (ChannelExec) session.openChannel("exec");
		}
	}

	public void endExecSession() throws JSchException
	{
		if (hasChannelExec())
		{
			channelExec.disconnect();
			channelExec = null;
		}
		endSession();
	}

	private String execCommand(String command, boolean forceClose) throws Exception
	{
		StringBuilder result = new StringBuilder();
		startExecSession();
		BufferedReader in=new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
		channelExec.setCommand(command);
		channelExec.connect();
		String msg=null;
		while((msg=in.readLine())!=null)
		{
		  result.append(msg);
		}
		int exitStatus = channelExec.getExitStatus();
		channelExec.disconnect();
		if(forceClose)
			endExecSession();
		System.out.println(result.toString());
		logExitStatus(command, exitStatus);
		return result.toString();
	}

	public void createNewTouchFile() throws Exception
	{
		createNewTouchFile(false);
	}
	
	public String createNewTouchFile(boolean forceClose) throws Exception
	{
		deleteTouchFile(forceClose);
		updateTouchFile(forceClose);
		return touchFilename;
	}

	public void updateTouchFile(boolean forceClose) throws Exception
	{
		if (touchFilename == null)
		{
			touchFilename = "" + System.currentTimeMillis();
		}
		execCommand("cd " + baseDirectory + ";touch " + touchFilename, forceClose);
	}

	public void deleteTouchFile(boolean forceClose) throws Exception
	{
		if (touchFilename != null)
		{
			execCommand("cd " + baseDirectory + ";rm " + touchFilename, forceClose);
			touchFilename = null;
		}
	}

	private void logExitStatus(String command, int exitStatus) throws Exception
	{
		if (exitStatus < 0)
		{
			logger.info("[User Log] SFTP command '" + command + "' execution done, but exit status not set!");
		}
		else if (exitStatus > 0)
		{
			throw new Exception("[User Exception] SFTP command '" + command + "' execution done, but with error! Error code: " + exitStatus);
		}
		else
		{
			logger.info("[User Log] SFTP command '" + command + "' execution done successfully!");
		}
	}

	public void createTarFile() throws Exception
	{
		execCommand("cd " + baseDirectory + ";tar -cf " + touchFilename + ".tar --newer ./" + touchFilename + " *.csv", true);
		execCommand("cd " + baseDirectory + ";tar -cf " + touchFilename + ".tar.gz -z "+ touchFilename + ".tar", true);
	}
}
