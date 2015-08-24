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

public class connectDB extends DatabaseConnection 
{
	static Connection con = null;
	String dbClassName,dbUrl ="";
	String dbServerName=null,hostName,port,database,userName,passwd;
	CommonUtils utils;


	public connectDB()
	{
		CommonProperties props = CommonProperties.getInstance();
		utils = new CommonUtils();
		try {
			props.load("./conf/databaseconfiguration.properties");
			dbServerName = props.get("db_server_name");
			hostName = props.get("db_host_name");
			port = props.get("db_port");
			database = props.get("db_name");
			userName = props.get("user_name");
			passwd = props.get("password");
			if(dbServerName.equals("sqlserver"))
			{
				dbClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver";
			}
			else if(dbServerName.equals("postgresql"))
			{
				dbClassName="org.postgresql.Driver";
			}
			else
			{

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String constructConnectionString(String hostName,String port,String database)
	{
		String dbUrl="";
		if(dbServerName.equals("sqlserver"))
		{
			dbUrl = "jdbc:"+dbServerName+"://" + hostName + ":" + port + ";databaseName=" + database+";" ;
		}
		else if(dbServerName.equals("postgresql"))
		{
			dbUrl = "jdbc:"+dbServerName+"://" + hostName + ":" + port + "/" + database+";" ;
		}
		else
		{
			
		}
		return dbUrl;
	}
	
	public String getPortNumber()
	{
		if(dbServerName.equals("sqlserver"))
		{
			return "1433";
		}
		else if(dbServerName.equals("postgresql"))
		{
			return "5432" ;
		}
		else
		{
			return "";
		}
	}
	
	public Connection establishConnection() throws MyCoreExceptions
	{
		try{
			Class.forName(dbClassName);

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
			
			port=getPortNumber();
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

			port=getPortNumber();
			dbUrl=constructConnectionString(hostName,port,database);

			con = DriverManager.getConnection(dbUrl, 
					generateProperty("", ""));

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
	public HashMap<Integer, String[]> executeQueryForArray(String query) throws MyCoreExceptions
	{
		HashMap<Integer, String[]> resultHash = null;
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			int numberOfColumns = rsmd.getColumnCount();

			resultHash = new HashMap<Integer, String[]>();
			int i =1;
			while ( rs.next() ) 
			{
				resultHash.put(i++, constructStringArray(rs, numberOfColumns));
			}
			rs.close();
			stmt.close();
		}catch(Exception e){
			throw new MyCoreExceptions("Exception while executing the query in db connection.."+e.getLocalizedMessage());
		}
		return resultHash;
	}

	@Override
	public HashMap<Integer, HashMap<String, String>> executeQueryForHash(String query) throws MyCoreExceptions
	{
		HashMap<Integer, HashMap<String, String>> resultHash = null;
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			int numberOfColumns = rsmd.getColumnCount();

			resultHash = new HashMap<Integer, HashMap<String, String>>();
			int i =1;
			while ( rs.next() ) 
			{
				resultHash.put(i++, constructStringArray(rs,rsmd, numberOfColumns));
			}
			rs.close();
			stmt.close();
		}catch(Exception e){
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
			ResultSetMetaData rsmd = rs.getMetaData();
			String result=null;
			int numberOfColumns = rsmd.getColumnCount();

			int i =1;
			while ( rs.next() ) 
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

	private HashMap<String, String> constructStringArray(ResultSet row, ResultSetMetaData rsmd, int numberOfColumns)
	{
		HashMap<String, String> rowHash = new HashMap<String, String>(); 
		try{
			for(int i=1; i <= numberOfColumns; i++)
			{
				rowHash.put(rsmd.getColumnName(i), row.getArray(i).toString());
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
				tmpArray[i] = row.getArray(i+1).toString();
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

	public List<String> executeQuery(String query) {
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
					resultList.add(rs.getString(j).toLowerCase().trim());
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
