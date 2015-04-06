package com.solutionstar.swaftee.dbconnections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import com.solutionstar.swaftee.CustomExceptions.MyCoreExceptions;

public class PostgresDB extends DatabaseConnection 
{
	static Connection con = null;
	
	@Override
	public Connection establishConnection(String hostName, String port, String database, String userName, String passwd) throws MyCoreExceptions 
	{	
		try{
			Class.forName("org.postgresql.Driver");
		
			String dbUrl = "jdbc:postgresql://" + hostName + ":" + port + "/" + database ;
			System.out.println("dburl.." + dbUrl );
	        
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
			Class.forName("org.postgresql.Driver");
		
			String dbUrl = "jdbc:postgresql://" + hostName + ":5432/" + database ;

			System.out.println("dburl.." + dbUrl );
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
			Class.forName("org.postgresql.Driver");
		
			String dbUrl = "jdbc:postgresql://" + hostName + ":" + port + "/" + database ;
			
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
			Class.forName("org.postgresql.Driver");
		
			String dbUrl = "jdbc:postgresql://" + hostName + ":5432/" + database ;

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
}
