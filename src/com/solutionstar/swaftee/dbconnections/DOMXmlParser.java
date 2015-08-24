package com.solutionstar.swaftee.dbconnections;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DOMXmlParser {
	
	private String fileName,data;
	DocumentBuilderFactory dbFactory;
	DocumentBuilder dBuilder;
	Document doc;
	
	public DOMXmlParser(String data) throws ParserConfigurationException, SAXException, IOException
	{
        dbFactory = DocumentBuilderFactory.newInstance();
        dBuilder = dbFactory.newDocumentBuilder();
        InputSource inputSource=new InputSource();
        inputSource.setCharacterStream(new StringReader(data));
        doc = dBuilder.parse(inputSource);
        doc.getDocumentElement().normalize();
	}
	
	public File xmlDataToFile(String data)
	{
		try
		{
			fileName=fileName+".txt";
			File file = new File(fileName);
			if (!file.exists()) 
			{
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.close();
			return file;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	public String getValueByTagName(String tagName)
	{
		try
		{ 
			NodeList nodeList=doc.getElementsByTagName(tagName);
			int length=nodeList.getLength();
			String value=nodeList.item(0).getTextContent();
			return value.trim();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	public HashMap<String,String> getChildNodesValues(String tagName)
	{
		try
		{
			NodeList nodeList=doc.getElementsByTagName(tagName);
			nodeList=nodeList.item(0).getChildNodes();
			HashMap<String,String> childValues=new HashMap<String,String>();
			if(nodeList.getLength()>0)
			{
				for(int i=0;i<nodeList.getLength();i++)
				{
					Node node=nodeList.item(i);
					String key=node.getNodeName();
					String value=node.getTextContent();
					childValues.put(key, value);
				}
			}
			else
			{
				String value=doc.getElementsByTagName(tagName).item(0).getNodeValue();
				childValues.put(tagName, value);
			}
			return childValues;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
}