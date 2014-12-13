/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.fejero.emconnect.mailclient.outcomming;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import sk.fejero.emconnect.mailclient.EmailMessage;
import sk.fejero.emconnect.mailclient.AccountSettings;

/**
 *
 * @author Rastislav
 */
public class SmtpClient {

    private final Session session;
    private AccountSettings acc;

    public SmtpClient(final AccountSettings acc) {
        this.acc = acc;
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", acc.getSmtpServer());
        props.put("mail.smtp.port", acc.getSmtpPort());
        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(acc.getUserName(), acc.getUserPwd());
                    }
                });
    }
    
    public void sendMessage(EmailMessage email) throws MessagingException {
        Message message = new MimeMessage(session);
        String[] attachements = email.getAttachements();

        //Setting up header
        //Logger.getGlobal().log(Level.INFO, "sendMessage: setting up header");
        message.setFrom(new InternetAddress(acc.getAuthor()));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo()));
        if (email.getCc() != null) {
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(email.getCc()));
        }
        if (email.getSubject() != null) {
            message.setSubject(email.getSubject());
        }
        if(email.getSent() != null) {
            message.setSentDate(email.getSent());
        } else {
            message.setSentDate(new Date());
        }


        //Setting up content
        //Logger.getGlobal().log(Level.INFO, "sendMessage: setting up content");
        if (attachements != null) {
            BodyPart messageBodyPart = new MimeBodyPart();
            if(email.getContent() != null) {
                messageBodyPart.setText(email.getContent());
            }
            Multipart multipart = new MimeMultipart();
            //Adding default text content
            //Logger.getGlobal().log(Level.INFO, "sendMessage: adding default text content in multipart");
            multipart.addBodyPart(messageBodyPart);
            //Adding attachements
            //Logger.getGlobal().log(Level.INFO, "sendMessage: entering loop for adding attachements");
            for (String filename : attachements) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filename);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);
            }
            message.setContent(multipart);
        } else {
            //Logger.getGlobal().log(Level.INFO, "sendMessage: adding default text content");
            if (email.getContent() != null) {
                message.setText(email.getContent());
            }
        }

        //Sending message
        Transport.send(message);
    }

}
