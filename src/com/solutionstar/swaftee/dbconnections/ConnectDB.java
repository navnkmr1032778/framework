package com.solutionstar.swaftee.dbconnections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.solutionstar.swaftee.CustomExceptions.MyCoreExceptions;
import com.solutionstar.swaftee.utils.CommonProperties;
import com.solutionstar.swaftee.utils.CommonUtils;

public class ConnectDB extends DatabaseConnection 
{
	Connection con = null;
	String dbClassName,dbUrl ="";
	String dbServerName=null,hostName,port,database,userName,passwd;
	CommonUtils utils;
	String env="";

	public ConnectDB(String dbConfigurationFile)
	{
		CommonProperties props = CommonProperties.getInstance();
		utils = new CommonUtils();
		try {
			props.load(dbConfigurationFile);
			dbServerName = props.get("db_server_name");
			hostName = props.get("db_host_name");
			port = props.get("db_port");
			database = props.get("db_name");
			userName = props.get("user_name");
			passwd = props.get("password");
			dbClassName=DB_CLASS_NAMES.get(dbServerName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ConnectDB(Connection con)
	{
		this.con = con;
	}
	
	public String constructConnectionString(String hostName,String port,String database)
	{
		String dbUrl="";
		switch(dbServerName)
		{
		case "sqlserver":
			dbUrl = "jdbc:"+dbServerName+"://" + hostName + ":" + port + ";databaseName=" + database+";" ;
			break;
		case "postgresql":
			dbUrl = "jdbc:"+dbServerName+"://" + hostName + ":" + port + "/" + database ;
			break;
		default:
			dbUrl = "jdbc:"+dbServerName+"://" + hostName + ":" + port + ";databaseName=" + database+";" ;
			break;
				
		}
		return dbUrl;
	}
	
	public Connection establishConnection() throws MyCoreExceptions
	{
		try{
			Class.forName(dbClassName);
			if(port.length()==0)
			{
				port=DEFAULT_PORTS.get(dbServerName);
			}
			dbUrl=constructConnectionString(hostName,port,database);

			con = DriverManager.getConnection(dbUrl, 
					generateProperty( userName , passwd));

			return con;	

		}catch(Exception e){
			throw new MyCoreExceptions("Exception while establishing db connection.."+ExceptionUtils.getFullStackTrace(e));
		}
	}

	@Override
	public Connection establishConnection(String hostName, String port, String database, String userName, String passwd) throws MyCoreExceptions 
	{	
		try{
			Class.forName(dbClassName);

			dbUrl=constructConnectionString(hostName,port,database);

			con = DriverManager.getConnection(dbUrl, 
					generateProperty( userName , passwd));

		}catch(Exception e){
			throw new MyCoreExceptions("Exception while establishing db connection.."+e.getLocalizedMessage());
		}
		return con;	
	}

	@Override
	public Connection establishConnection(String hostName, String database, String userName, String passwd) throws MyCoreExceptions {
		try{
			Class.forName(dbClassName);
			
			port=DEFAULT_PORTS.get(dbServerName);
			dbUrl=constructConnectionString(hostName,port,database);
			
			con = DriverManager.getConnection(dbUrl, 
					generateProperty(userName, passwd));

		}catch(Exception e){
			throw new MyCoreExceptions("Exception while establishing db connection.."+e.getLocalizedMessage());
		}
		return con;		}

	@Override
	public Connection establishConnection(String hostName, String port, String database) throws MyCoreExceptions 
	{
		try{
			Class.forName(dbClassName);
			dbUrl=constructConnectionString(hostName,port,database);

			con = DriverManager.getConnection(dbUrl, 
					generateProperty("", ""));

		}catch(Exception e){
			throw new MyCoreExceptions("Exception while establishing db connection.."+e.getLocalizedMessage());
		}
		return con;	
	}

	@Override
	public Connection establishConnection(String hostName, String database) throws MyCoreExceptions 
	{
		try{
			Class.forName(dbClassName);

			port=DEFAULT_PORTS.get(dbServerName);
			dbUrl=constructConnectionString(hostName,port,database);

			con = DriverManager.getConnection(dbUrl,generateProperty("", ""));

		}catch(Exception e){
			throw new MyCoreExceptions("Exception while establishing db connection.."+e.getLocalizedMessage());
		}
		return con;	
	}

	Properties generateProperty(String userName, String password)
	{
		Properties props = null;
		try{
			props = new Properties();	
			props.setProperty("user", userName);
			props.setProperty("password", password);
		}catch(Exception e){
			e.printStackTrace();
		}
		return props;
	}

	@Override
	public List<String[]> executeQueryForArray(String query) throws MyCoreExceptions
	{
		List<String[]> resultHash = null;
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			int numberOfColumns = rsmd.getColumnCount();

			resultHash = new ArrayList<String[]>();
			while ( rs.next() ) 
			{
				resultHash.add(constructStringArray(rs, numberOfColumns));
			}
			rs.close();
			stmt.close();
		}catch(Exception e){
			throw new MyCoreExceptions("Exception while executing the query in db connection.."+e.getLocalizedMessage());
		}
		return resultHash;
	}

	@Override
	public List<HashMap<String, String>> executeQueryForHash(String query) throws MyCoreExceptions
	{
		List<HashMap<String, String>> resultHash = null;
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			int numberOfColumns = rsmd.getColumnCount();

			resultHash = new ArrayList<HashMap<String, String>>();
			while ( rs.next() ) 
			{
				resultHash.add(constructStringArray(rs,rsmd, numberOfColumns));
			}
			rs.close();
			stmt.close();
		}catch(Exception e){
			throw new MyCoreExceptions("Exception while executing the query in db connection.."+e.getLocalizedMessage());
		}
		return resultHash;
	}
	
	
	public HashMap<String, String> executeQueryForHashMap(String query) throws MyCoreExceptions
	{
		HashMap<String, String> resultHash = null;
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			resultHash = new HashMap<String,String>();
			while ( rs.next() ) 
			{
				resultHash.put(rs.getString(1), rs.getString(2));
			}
			rs.close();
			stmt.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new MyCoreExceptions("Exception while executing the query in db connection.."+e.getLocalizedMessage());
		}
		return resultHash;
	}

	public String executeQueryForScalar(String query) throws MyCoreExceptions
	{
		try
		{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			String result=null;
			if( rs.next() ) 
			{
				result=rs.getString(1);
			}
			rs.close();
			stmt.close();
			return result;
		}
		catch(Exception ex)
		{
			throw new MyCoreExceptions("Exception while executing the query in db connection.. "+ExceptionUtils.getFullStackTrace(ex));
		}
	}
	
	public void executeInsertQuery(String query) throws MyCoreExceptions
	{
		try
		{
			Statement stmt = con.createStatement();
			stmt.executeUpdate(query);			
			stmt.close();			
		}
		catch(Exception ex)
		{
			throw new MyCoreExceptions("Exception while executing the query in db connection.. "+ExceptionUtils.getFullStackTrace(ex));
		}
	}

	private HashMap<String, String> constructStringArray(ResultSet row, ResultSetMetaData rsmd, int numberOfColumns)
	{
		HashMap<String, String> rowHash = new HashMap<String, String>(); 
		try{
			for(int i=1; i <= numberOfColumns; i++)
			{
				rowHash.put(rsmd.getColumnName(i), row.getString(i));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return rowHash;	
	}

	private String[] constructStringArray(ResultSet row, int columnLength)
	{
		String[] tmpArray = new String[columnLength];
		try{
			for(int i=0; i < columnLength; i++)
			{
				tmpArray[i] = row.getString(i+1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return tmpArray;
	}

	public void closeDBConnectionIfExits()
	{
		try {
			if(!con.isClosed())
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* todo */
	public List<String> executeQueryResultAsList(String query) {
		try
		{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			List<String> resultList=new ArrayList<String>();
			while( rs.next() ) 
			{
				for(int j=1;j<=numberOfColumns;j++)
				{
					resultList.add(rs.getString(j).trim());
				}
			}
			rs.close();
			stmt.close();
			return resultList;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
}
