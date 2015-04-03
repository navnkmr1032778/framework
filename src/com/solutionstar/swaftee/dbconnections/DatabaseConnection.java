package com.solutionstar.swaftee.dbconnections;

import java.sql.Connection;
import java.util.HashMap;

import com.solutionstar.swaftee.CustomExceptions.MyCoreExceptions;

public abstract class DatabaseConnection {
	
	abstract Connection establishConnection(String hostName, String port, String database, String userName, String passwd) throws MyCoreExceptions;
	
	abstract Connection establishConnection(String hostName, String database, String userName, String passwd) throws MyCoreExceptions;

	abstract Connection establishConnection(String hostName, String port, String database) throws MyCoreExceptions;
	
	abstract Connection establishConnection(String hostName, String database) throws MyCoreExceptions;
	
	abstract HashMap<Integer, String[]> executeQueryForArray(String query) throws MyCoreExceptions;
	
	abstract HashMap<Integer, HashMap<String, String>> executeQueryForHash(String query) throws MyCoreExceptions;
	
	abstract void closeDBConnectionIfExits(); 
	
}
