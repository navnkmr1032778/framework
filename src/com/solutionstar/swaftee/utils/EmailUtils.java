package com.solutionstar.swaftee.utils;

import java.io.IOException;
import java.net.URI;
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

import microsoft.exchange.webservices.data.BasePropertySet;
import microsoft.exchange.webservices.data.ExchangeCredentials;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.ExchangeVersion;
import microsoft.exchange.webservices.data.FindItemsResults;
import microsoft.exchange.webservices.data.InternetMessageHeader;
import microsoft.exchange.webservices.data.InternetMessageHeaderCollection;
import microsoft.exchange.webservices.data.Item;
import microsoft.exchange.webservices.data.ItemSchema;
import microsoft.exchange.webservices.data.ItemView;
import microsoft.exchange.webservices.data.PropertySet;
import microsoft.exchange.webservices.data.ServiceLocalException;
import microsoft.exchange.webservices.data.WebCredentials;
import microsoft.exchange.webservices.data.WellKnownFolderName;

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
	
	public HashMap<String, String> getLastXomeMailfrom(String userName,String passWord,String fromEmail) throws Exception
	{
		return getLastExchangeMailfrom(userName,passWord,fromEmail,WebDriverConstants.SOLUTIONSTAR_IMAP_HOST,WebDriverConstants.SOLUTIONSTAR_DOMAIN_NAME);
	}
	
	private HashMap<String, String> getLastExchangeMailfrom(String userName, String passWord, String fromEmail,
			String exchangeServer, String domainName) throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, String> result = new HashMap<String, String>();
		ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
	    ExchangeCredentials credentials = new WebCredentials(userName,passWord,domainName );
	    service.setCredentials(credentials);
	    service.setUrl(new URI("https://"+ exchangeServer +"/ews/exchange.asmx"));

	    ItemView view = new ItemView (10);
	    FindItemsResults findResults = service.findItems(WellKnownFolderName.Inbox, view);

	    for(Object i : findResults.getItems())
	    {
	      Item item = (Item)i;
	      item.load(new PropertySet(BasePropertySet.FirstClassProperties, ItemSchema.MimeContent));
	      String sender = getReturnPath(item);
	      if(sender.equalsIgnoreCase(fromEmail))
	      {
		      result.put("Subject", item.getSubject());
	          result.put("ReceivedDate", item.getDateTimeReceived().toString());
	          result.put("From", sender);
	          result.put("Body", item.getBody().toString());
	          return result;
	      }
	    }
		return null;
	}

	private String getReturnPath(Item item)
	{
		try 
		{
			  InternetMessageHeaderCollection inhead = item.getInternetMessageHeaders();
			  for(InternetMessageHeader head : inhead)
		      {
		    	  if(head.getName().equalsIgnoreCase(WebDriverConstants.SENDER_INTERNET_HEADER))
		    			  return head.getValue();
		      }
		      return null;
		} 
		catch (ServiceLocalException e) 
		{
			e.printStackTrace();
			return null;
		}
	    
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
			if(folder!=null)folder.close(false);
		    if(store!=null)store.close();
		    throw ex;
		    
		}
		return null;
		
	}


	
}
