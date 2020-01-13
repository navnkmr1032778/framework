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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solutionstar.swaftee.CustomExceptions.MyCoreExceptions;
import com.solutionstar.swaftee.tests.xv.wf.adminrelated.CreateIndividualAgent;
import com.solutionstar.swaftee.utils.CommonProperties;
import com.solutionstar.swaftee.utils.CommonUtils;

public class ConnectDB extends DatabaseConnection 
{
	protected static Logger logger = LoggerFactory.getLogger(ConnectDB.class);
    Connection con = null;
    String dbClassName,dbUrl ="";
    String dbServerName=null,hostName,port,database,userName,passwd,dbIntegrationSecurity;
    CommonUtils utils;
    String env="";
 
    public ConnectDB(String dbConfigurationFile)
    {
    	logger.info("0 Start of ConnectDB");
        CommonProperties props = CommonProperties.getInstance();
        logger.info("0 CommonProperties");
        utils = new CommonUtils();
        logger.info("0 Utils");
        try {
            props.load(dbConfigurationFile);
            dbServerName = props.get("db_server_name");
            dbIntegrationSecurity=props.get("db_IntegrationSecurity");
            hostName = props.get("db_host_name");
            port = props.get("db_port");
            database = props.get("db_name");
            userName = props.get("user_name");
            passwd = props.get("password");
            dbClassName=DB_CLASS_NAMES.get(dbServerName);
			logger.info("0 dbClassName in establishConnection "+dbClassName);
			logger.info("0 hostName in establishConnection "+hostName);
			logger.info("0 port in establishConnection "+port);
			logger.info("0 database in establishConnection "+database);
			logger.info("0 dbUrl in establishConnection "+dbUrl);
			logger.info("0 userName in establishConnection "+userName);
			logger.info("0 passwd in establishConnection "+passwd);
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
            if(dbIntegrationSecurity.equalsIgnoreCase("true"))
            {
            	dbUrl = dbUrl  + "integratedSecurity=true;";
            }           
            break;
        case "postgresql":
            dbUrl = "jdbc:"+dbServerName+"://" + hostName + ":" + port + "/" + database ;
            break;
        default:
            dbUrl = "jdbc:"+dbServerName+"://" + hostName + ":" + port + ";databaseName=" + database+";" ;
            break;
                
        }
        logger.info("dbUrl : "+dbUrl);
        return dbUrl;
    }
	
	public Connection establishConnection() throws MyCoreExceptions
	{
		try{
			logger.info("A");
			Class.forName(dbClassName);
			logger.info("B");
			if(port.length()==0)
			{
				logger.info("C");
				port=DEFAULT_PORTS.get(dbServerName);
				logger.info("D");
			}
			logger.info("E");
			dbUrl=constructConnectionString(hostName,port,database);
			logger.info("F");
			logger.info("1 hostName in establishConnection "+hostName);
			logger.info("1 port in establishConnection "+port);
			logger.info("1 database in establishConnection "+database);
			logger.info("1 dbUrl in establishConnection "+dbUrl);
			con = DriverManager.getConnection(dbUrl, 
					generateProperty( userName , passwd));
			return con;	

		}catch(Exception e){
			throw new MyCoreExceptions("Exception while establishing db connection.."+ExceptionUtils.getRootCauseStackTrace(e));
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
			logger.info("2 dbClassName in establishConnection "+dbClassName);
			logger.info("2 hostName in establishConnection "+hostName);
			logger.info("2 port in establishConnection "+port);
			logger.info("2 database in establishConnection "+database);
			logger.info("2 dbUrl in establishConnection "+dbUrl);
			logger.info("2 userName in establishConnection "+userName);
			logger.info("2 passwd in establishConnection "+passwd);

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
			logger.info("3 dbClassName in establishConnection "+dbClassName);
			logger.info("3 hostName in establishConnection "+hostName);
			logger.info("3 port in establishConnection "+port);
			logger.info("3 database in establishConnection "+database);
			logger.info("3 dbUrl in establishConnection "+dbUrl);
			logger.info("3 userName in establishConnection "+userName);
			logger.info("3 passwd in establishConnection "+passwd);
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
			logger.info("4 dbClassName in establishConnection "+dbClassName);
			logger.info("4 hostName in establishConnection "+hostName);
			logger.info("4 port in establishConnection "+port);
			logger.info("4 database in establishConnection "+database);
			logger.info("4 dbUrl in establishConnection "+dbUrl);
			logger.info("4 userName in establishConnection "+userName);
			logger.info("4 passwd in establishConnection "+passwd);

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
			throw new MyCoreExceptions("Exception while executing the query in db connection.. "+ExceptionUtils.getRootCauseStackTrace(ex));
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
			throw new MyCoreExceptions("Exception while executing the query in db connection.. "+ExceptionUtils.getRootCauseStackTrace(ex));
		}
	}
	
	public void executeUpdateQuery(String query) throws MyCoreExceptions
    {
        try
        {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);            
            stmt.close();            
        }
        catch(Exception ex)
        {
            throw new MyCoreExceptions("Exception while executing the query in db connection.. "+ExceptionUtils.getRootCauseStackTrace(ex));
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
