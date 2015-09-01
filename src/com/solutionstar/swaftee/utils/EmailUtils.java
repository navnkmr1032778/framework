package com.solutionstar.swaftee.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solutionstar.swaftee.constants.WebDriverConstants;

public class EmailUtils {
	protected static Logger logger = LoggerFactory.getLogger(EmailUtils.class.getName());
	
	private String getResponseBodyText(Part p) throws MessagingException, IOException 
	{
		if (p.isMimeType("text/*")) {
		String s = (String)p.getContent();
		return s;
		}
		
		if (p.isMimeType("multipart/alternative")) {
		// prefer html text over plain text
		Multipart mp = (Multipart)p.getContent();
		String text = null;
		for (int i = 0; i < mp.getCount(); i++) {
		    Part bp = mp.getBodyPart(i);
		    if (bp.isMimeType("text/plain")) {
		        if (text == null)
		            text = getResponseBodyText(bp);
		        continue;
		    } else if (bp.isMimeType("text/html")) {
		        String s = getResponseBodyText(bp);
		        if (s != null)
		            return s;
		    } else {
		        return getResponseBodyText(bp);
		    }
		}
		return text;
		} else if (p.isMimeType("multipart/*")) {
		Multipart mp = (Multipart)p.getContent();
		for (int i = 0; i < mp.getCount(); i++) {
		    String s = getResponseBodyText(mp.getBodyPart(i));
		    if (s != null)
		        return s;
		}
		}
		
		return null;
		}
	
	public HashMap<String, String> getLastGmailfrom(String userName,String passWord,String fromEmail) throws Exception
	{
		return getLastmailfromImap(userName,passWord,fromEmail,WebDriverConstants.GMAIL_IMAP_HOST);
	}
	
	
	private HashMap<String, String> getLastmailfromImap(String userName, String passWord, String fromEmail,
			String imapHost) throws Exception 
	{
		HashMap<String, String> result = new HashMap<String, String>();
		Store store =null;
	    Folder folder = null;
		try
		{
			 	Properties props = new Properties();
			    props.setProperty("mail.store.protocol", WebDriverConstants.IMAP_PROTOCOL);
			    Session session = Session.getDefaultInstance(props, null);
			    store = session.getStore();
			    store.connect(imapHost , userName, passWord);
			    folder = store.getFolder("INBOX");
			    folder.open(Folder.READ_ONLY);
			    int totalCnt = folder.getMessageCount();
			    for (int emailCnt=0; emailCnt< WebDriverConstants.EMAIL_PULL_LIMIT && totalCnt-emailCnt>0; emailCnt++)
			    {
			    	Message msg = folder.getMessage(totalCnt-emailCnt);
			        if(msg.getFrom()[0].toString().toLowerCase().contains(fromEmail.toLowerCase()))
			        {
			        	result.put("Subject", msg.getSubject());
			        	result.put("ReceivedDate", msg.getSentDate().toString());
			        	result.put("From", msg.getFrom()[0].toString());
			        	result.put("Body", getResponseBodyText(msg));
			        	return result;
			        }
		        }
			    folder.close(false);
			    store.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			folder.close(false);
		    store.close();
		    throw ex;
		}
		return null;
		
	}


	
}
