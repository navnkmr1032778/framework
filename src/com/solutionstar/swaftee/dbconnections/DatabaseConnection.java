package com.solutionstar.swaftee.dbconnections;

import java.sql.*;
import java.util.*;

import com.solutionstar.swaftee.CustomExceptions.*;

public abstract class DatabaseConnection {

	public static final Map<String, String> DEFAULT_PORTS;
	static {
		Map<String, String> tmp = new LinkedHashMap<String, String>();
		tmp.put("sqlserver", "1433");
		tmp.put("postgresql", "5432");
		DEFAULT_PORTS = Collections.unmodifiableMap(tmp);
	}

	public static final Map<String, String> DB_CLASS_NAMES;
	static {
		Map<String, String> tmp = new LinkedHashMap<String, String>();
		tmp.put("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		tmp.put("postgresql", "org.postgresql.Driver");
		DB_CLASS_NAMES = Collections.unmodifiableMap(tmp);
	}

	abstract Connection establishConnection(String hostName, String port, String database, String userName,
			String passwd) throws MyCoreExceptions;

	abstract Connection establishConnection(String hostName, String database, String userName, String passwd)
			throws MyCoreExceptions;

	abstract Connection establishConnection(String hostName, String port, String database) throws MyCoreExceptions;

	abstract Connection establishConnection(String hostName, String database) throws MyCoreExceptions;

	abstract List<String[]> executeQueryForArray(String query) throws MyCoreExceptions;

	abstract List<HashMap<String, String>> executeQueryForHash(String query) throws MyCoreExceptions;

	abstract void closeDBConnectionIfExits();

}
