package com.solutionstar.swaftee.dbconnections;

import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DOMXmlParser {
	
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
	
	public String getFirstValueByTagName(String tagName)
	{
		try
		{
			NodeList nodeList=doc.getElementsByTagName(tagName);
			String value=nodeList.item(0).getTextContent();
			return value.trim();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	public HashMap<String,List<String>> getChildNodeValuesOfFirstElement(String tagName)
	{
		try
		{
			
			NodeList nodeList=doc.getElementsByTagName(tagName);
			nodeList=nodeList.item(0).getChildNodes();
			HashMap<String,List<String>> childValues=new HashMap<String,List<String>>();
			
			if(nodeList.getLength()>0)
			{
				for(int i=0;i<nodeList.getLength();i++)
				{
					Node node=nodeList.item(i);
					String key=node.getNodeName();
					String value=node.getTextContent();
					if(childValues.containsKey(key))
					{
						childValues.get(key).add(value);
					}
					else
					{
						childValues.put(key,Arrays.asList(value));
					}
				}
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