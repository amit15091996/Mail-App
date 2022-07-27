package com.eidiko.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import javax.swing.text.BadLocationException;

import org.springframework.stereotype.Component;

import com.eidiko.entity.MailDetails;

import lombok.extern.slf4j.Slf4j;

/**
 * @author sumanthk
 *
 */

@Slf4j
@Component
public class MailUtility {

	private static String host = "outlook.office365.com";

	private String port = "993";

	private String imapPort = "143";

//	@Value("${spring.mail.INBOX}")
	private String inboxFolder = "Inbox";

	public List<MailDetails> readEmail(String userName, String password, String subject, String mailbox)
			throws Exception {

		System.out.println("adding mail properties");
		List<MailDetails> listOfMailDetails = new ArrayList<MailDetails>();

		System.out.println("Username : " + userName);
		System.out.println("subject : " + subject);
		System.out.println("IMAP Port : " + imapPort);

		Properties properties = new Properties();
		properties.setProperty("mail.store.protocol", "imap");
		properties.setProperty("mail.imap.port", "143");
		properties.setProperty("mail.imap.starttls.enable", "true");

		Session session = Session.getInstance(properties, null);
		session.setDebug(false);

		Store store = session.getStore();

		store.connect(host, userName, password);

		System.out.println("Reading Mail Box :: " + userName);

		Folder inbox = store.getFolder(inboxFolder);
		inbox.open(Folder.READ_WRITE);

		Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
		System.out.println("Connected to " + mailbox + " successfully..");
		System.out.println("Total unread mails in inbox :" + messages.length);

		if (messages.length == 0) {
			System.out.println("No unread mails found.");
		}

		int mailCount = 0;
		if (messages != null && messages.length > 0) {
			for (int i = 0; i < messages.length; i++) {

				Message message = messages[i];

				System.out.println("Subject from mail: " + message.getSubject());
				System.out.println("Subject to read: " + subject);

				String messageSubject = message.getSubject();
				Address[] fromAddress = message.getFrom();
				String modifiedFromAddress = fromAddress[0].toString();
				System.out.println("From Address is: " + modifiedFromAddress.toUpperCase());

				System.out.println("messageSubject: " + messageSubject + "Size is::" + messageSubject.length());
				if ((!messageSubject.isEmpty()) && (messageSubject.toUpperCase().contains(subject.toUpperCase()))) {
					mailCount++;

					MailDetails mailDetails = new MailDetails();

					String contentType = message.getContentType();
//					System.out.println("contentType: " + contentType);
					String messageContent = null;

					if (contentType.contains("multipart")) {

						// content may contain attachments
						Multipart multiPart = (Multipart) message.getContent();
						MimeMultipart mimeMultiPart1 = (MimeMultipart) message.getContent();
						messageContent = getTextFromMimeMultipart(mimeMultiPart1);

					} 
					mailDetails.setBody(messageContent);
					System.out.println("Content " + messageContent);
					boolean multipartException = false;
					if (multipartException) {
						message.setFlag(Flags.Flag.SEEN, false);
					} else {
						listOfMailDetails.add(mailDetails);

						System.out.println("Mail Reading completed successfully");

						message.setFlag(Flags.Flag.SEEN, true);
					}
				} else {
					System.out.println("Mail subject or from address is null");
				}

			}

			System.out.println("Out of Loop " + mailCount);
			deleteMail();
		}
		return listOfMailDetails;
	}

	private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart)
			throws MessagingException, IOException, BadLocationException {
		String result = "";
		int count = mimeMultipart.getCount();

		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);

			if (bodyPart.isMimeType("text/plain")) {
				result = result + "\n" + bodyPart.getContent();
				break; // without break same text appears twice in my tests
			} else if (bodyPart.getContent() instanceof MimeMultipart) {
				result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
			}
		}

		return result;

	}

	private static void deleteMail() throws Exception {
		String username = "amitdewangan199@outlook.com";// change accordingly
		String password = "@mit1996";// change accordingly

		// 1) get the session object
		Properties properties = new Properties();
		properties.setProperty("mail.store.protocol", "imap");
		properties.setProperty("mail.imap.port", "143");
		properties.setProperty("mail.imap.starttls.enable", "true");

		Session session = Session.getInstance(properties, null);
		// 2) create the store object and connect to the current host
		Store store = session.getStore();
		store.connect(host, username, password);

		// 3) create the folder object and open it
		Folder folder = store.getFolder("Inbox");

		if (!folder.exists()) {
			System.out.println("inbox not found");
			System.exit(0);
		}

		folder.open(Folder.READ_WRITE);

		// 4) Get the message to delete
		Message[] msg = folder.getMessages();

		// System.out.println((messages.length+1)+" message found");
		for (int i = 0; i < msg.length; i++) {
			System.out.println("--------- " + (i + 1) + "------------");
			String from = InternetAddress.toString(msg[i].getFrom());

			if (from != null) {
				System.out.println("From: " + from);
			}

			String replyTo = InternetAddress.toString(msg[i].getReplyTo());
			if (replyTo != null) {
				System.out.println("Reply-to: " + replyTo);
			}

			String to = InternetAddress.toString(msg[i].getRecipients(Message.RecipientType.TO));

			if (to != null) {
				System.out.println("To: " + to);
			}
			String subject = msg[i].getSubject();
			if (subject != null) {
				System.out.println("Subject: " + subject);
			}
			Date sent = msg[i].getSentDate();
			if (sent != null) {
				System.out.println("Sent: " + sent);
			}

			msg[(msg.length)-1].setFlag(Flags.Flag.DELETED, true);
			
			System.out.println("Message Deleted ....."); 

			folder.close(true);
			store.close();
		}
	}
}
