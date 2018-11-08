/*
 * Copyright 2010, David George, Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.magneato.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SendMail {
// config.yml
	private String fromEmail;

	private String smtpServer;

	private String smtpPort;

	private String smtpUser;

	private String smtpPass;
	private final Log _logger = LogFactory.getLog(SendMail.class);

	/**
	 * Send an email message via SMTP. Configured via spring.
	 * 
	 * @param subject - Message Subject
	 * @param mesg - Message Body
	 * @param email - User's email address
	 */
	public boolean message(String subject, String mesg, String email) {
		Authenticator auth = null;

		// Get system properties
		Properties properties = new Properties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", smtpServer);
		properties.setProperty("mail.smtp.port", smtpPort);
		if (smtpUser != null && !smtpUser.isEmpty()) {
			properties.put("mail.smtp.auth", "true");
			auth = this.new SMTPAuthenticator(smtpUser, smtpPass);
		}
		
		Session session = Session.getDefaultInstance(properties, auth);
		session.setDebug(true);

		try {

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));
			message.setSubject(subject);
			message.setText(mesg);

			Transport.send(message);
		} catch (MessagingException mex) {
			_logger.warn("Error sending confirmation email to" + email + " because " + mex);
			return false;
		}
		
		return true;
	}

	/**
	 * Required for secure mail servers
	 * @author david
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		String user, pass;
		
		SMTPAuthenticator(String user, String pass) {
			this.user = user;
			this.pass = pass;
		}
		public PasswordAuthentication getPasswordAuthentication() {
			String username = user;
			String password = pass;
			return new PasswordAuthentication(username, password);
		}
	}
}
