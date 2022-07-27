package com.eidiko;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.swing.text.BadLocationException;

import com.eidiko.utility.MailUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MailApp {

	public static void main(String[] args) {
		System.out.println("In Main Class");
		MailUtility mailUtility = new MailUtility();
		try {
			mailUtility.readEmail("amitdewangan199@outlook.com", "@mit1996", "Sample Java Email", "mailbox");
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
