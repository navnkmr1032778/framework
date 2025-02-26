package com.solutionstar.swaftee.utils.dataarchive;

import java.util.*;

import org.apache.log4j.*;

/**
 * Data archive base class. All data archive classes should extend this class.
 * 
 * @author Allen Godfrey
 */
public class DataArchiveBase {

	/**
	 * Queue object for collecting commands
	 */
	protected List<String[]> list = new LinkedList<String[]>();

	/**
	 * logging object
	 */
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(DataArchiveBase.class);

	/**
	 * Add data to be archived.
	 * 
	 * @param data
	 * 
	 * @throws Exception
	 */
	public void addData(String[] data) throws Exception {
		list.add(data);
	}

	/**
	 * Clear/remove all data collected.
	 * 
	 * @throws Exception
	 */
	public void clearData() throws Exception {
		list.clear();
	}

}
