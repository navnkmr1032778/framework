package com.solutionstar.swaftee.utils;

import java.util.*;

import org.apache.commons.lang3.*;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.*;

public class NadaEMailService {
	public static final String NADA_EMAIL_DOMAIN = "@clrmail.com";
	private static final String INBOX_MESSAGE_KEY_NAME = "msgs";
	private static final String EMAIL_ID_ROUTE_PARAM = "email-id";
	private static final String MESSAGE_ID_ROUTE_PARAM = "message-id";
	private static final String NADA_EMAIL_INBOX_API = "https://getnada.com/api/v1/inboxes/{email-id}";
	private static final String NADA_EMAIL_MESSAGE_API = "https://getnada.com/api/v1/messages/{message-id}";
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final int EMAIL_CHARS_LENGTH = 10;

	private String emailId;

	@SuppressWarnings("deprecation")
	private void generateEmailId() {
		this.emailId = RandomStringUtils.randomAlphanumeric(EMAIL_CHARS_LENGTH).toLowerCase().concat(NADA_EMAIL_DOMAIN);
	}

	// generates a random email for the first time.
	// call reset for a new random email
	public String getEmailId() {
		if (Objects.isNull(this.emailId)) {
			this.generateEmailId();
		}
		return this.emailId;
	}

	public void setEmailId(String mail) {
		this.emailId = mail;
	}

	// to re-generate a new random email id
	public void reset() {
		this.emailId = null;
	}

	public List<InboxEmail> getInbox() {
		try {
			String msgs = Unirest.get(NADA_EMAIL_INBOX_API).routeParam(EMAIL_ID_ROUTE_PARAM, this.getEmailId()).asJson()
					.getBody().getObject().getJSONArray(INBOX_MESSAGE_KEY_NAME).toString();
			return MAPPER.readValue(msgs, new TypeReference<List<InboxEmail>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<InboxEmail> getInboxByEMail(String mail) {
		try {
			System.out.println(Unirest.get(NADA_EMAIL_INBOX_API).routeParam(EMAIL_ID_ROUTE_PARAM, mail).asJson()
					.getBody().toString());
			String msgs = Unirest.get(NADA_EMAIL_INBOX_API).routeParam(EMAIL_ID_ROUTE_PARAM, mail).asJson().getBody()
					.getObject().getJSONArray(INBOX_MESSAGE_KEY_NAME).toString();
			return MAPPER.readValue(msgs, new TypeReference<List<InboxEmail>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public EmailMessage getMessageById(final String messageId) {
		String msgs;
		try {
			msgs = Unirest.get(NADA_EMAIL_MESSAGE_API).routeParam(MESSAGE_ID_ROUTE_PARAM, messageId).asJson().getBody()
					.getObject().toString();
			return MAPPER.readValue(msgs, EmailMessage.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public EmailMessage getMessageWithSubjectStartsWith(List<InboxEmail> inbox, final String emailSubject) {
		return inbox.stream().filter(ie -> ie.getSubject().startsWith(emailSubject)).findFirst()
				.map(InboxEmail::getMessageId).map(this::getMessageById).orElseThrow(IllegalArgumentException::new);
	}

	public EmailMessage getMessageWithSubjectContainsWith(List<InboxEmail> inbox, final String emailSubject) {
		return inbox.stream().filter(ie -> ie.getSubject().contains(emailSubject)).findFirst()
				.map(InboxEmail::getMessageId).map(this::getMessageById).orElseThrow(IllegalArgumentException::new);
	}
}
