package com.eidiko.entity;

import java.io.File;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.List;

import javax.mail.Message;

import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author sumanthk
 *
 */

@Data
@Component
public class MailDetails {

	private String from;

	private String referenceNo;

	private String actiontaken;

	private String to;

	private String subject;

	private Timestamp sentDate;

	private Timestamp receivedDate;
	private Timestamp Email_read_date;

	private List<File> attachements;

	private List<String> cc;

	private String body;

	private InputStream emlInputStream;

	private Message message;
	private String emailType;

}
