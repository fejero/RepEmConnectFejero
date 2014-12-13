/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.fejero.emconnect.mailclient.incomming;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.DateTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import sk.fejero.emconnect.mailclient.AccountSettings;
import sk.fejero.emconnect.mailclient.EmailMessage;

/**
 *
 * @author Rastislav
 */
public class ImapClient {

    private final Session session;
    private final AccountSettings acc;
    private Store store;
    private Folder emailFolder;

    public ImapClient(final AccountSettings acc) {
        this.acc = acc;
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.starttls.enable", "true");
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.host", acc.getImapServer());
        props.put("mail.imaps.port", acc.getImapPort());
        this.session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(acc.getUserName(), acc.getUserPwd());
                    }
                });
    }

    public EmailMessage[] getMessages(String folder) throws NoSuchProviderException, MessagingException, IOException {
        //Store store = session.getStore("imaps");
        //store.connect(acc.getImapServer(), acc.getUserName(), acc.getUserPwd());
        Folder emailFolder = store.getFolder(folder);
        emailFolder.open(Folder.READ_ONLY);
        //Date date = new Date();
        long time= System.currentTimeMillis();
        Date date = new Date(time - (acc.getStoreMails()*1000));
        SearchTerm term = new ReceivedDateTerm(ComparisonTerm.LE, date);
        Message[] messages = emailFolder.search(term);
        EmailMessage[] emails = new EmailMessage[messages.length];
        EmailMessage email;
        Message message;
        for (int i = 0; i < messages.length; i++) {
            email = new EmailMessage();
            message = messages[i];
            email.setAuthor(InternetAddress.toString(message.getFrom()));
            email.setTo(Arrays.toString(message.getRecipients(Message.RecipientType.TO)));
            email.setReceived(message.getReceivedDate());
            email.setSent(message.getSentDate());
            email.setSubject(message.getSubject());
            email.setFolder(message.getFolder().getName());
            email.setRead(message.isSet(Flags.Flag.SEEN));
            if (message.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) message.getContent();
                Part p;
                String[] files = new String[mp.getCount() - 1];
                int fc = 0;
                for (int j = 0; j < mp.getCount(); j++) {
                    p = mp.getBodyPart(j);
                    if (p.isMimeType("text/plain")) {
                        email.setContent(p.getContent().toString());
                    }
                    if (p.isMimeType("message/rfc822")) {
                        email.setContent("\nNested message:\n" + p.getContent().toString());
                    }
                    if (p.getFileName() != null) {
                        files[fc++] = p.getFileName();
                        saveFile(p.getContent(), p.getFileName());
                    }
                }
                email.setAttachements(files);
            } else {
                email.setContent(message.getContent().toString());
            }
            emails[i] = email;
        }
        emailFolder.close(false);
        store.close();
        return emails;
    }

    public void openFolder(String name) throws MessagingException {
        emailFolder = store.getFolder(name);
        emailFolder.open(Folder.READ_ONLY);
    }

    public void closeFolder() throws MessagingException {
        emailFolder.close(false);
    }

    public Message[] getMessages() throws MessagingException {
        long time= System.currentTimeMillis();
        Date date = new Date(time - (acc.getStoreMails()*1000));
        SearchTerm term = new ReceivedDateTerm(ComparisonTerm.GE, date);
        return emailFolder.search(term);
    }

    public EmailMessage getMessage(Message message) throws MessagingException, IOException {
        EmailMessage email;
        email = new EmailMessage();
        email.setAuthor(((InternetAddress) message.getFrom()[0]).getAddress());
        email.setTo(Arrays.toString(message.getRecipients(Message.RecipientType.TO)));
        email.setReceived(message.getReceivedDate());
        email.setSent(message.getSentDate());
        email.setSubject(message.getSubject());
        email.setFolder(message.getFolder().getName());
        email.setRead(message.isSet(Flags.Flag.SEEN));
        if (message.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) message.getContent();
            Part p;
            String[] files = new String[mp.getCount() - 1];
            int fc = 0;
            for (int j = 0; j < mp.getCount(); j++) {
                p = mp.getBodyPart(j);
                if (p.isMimeType("text/plain")) {
                    email.setContent(p.getContent().toString());
                }
                if (p.isMimeType("message/rfc822")) {
                    email.setContent("\nNested message:\n" + p.getContent().toString());
                }
                if (p.getFileName() != null) {
                    files[fc++] = p.getFileName();
                    saveFile(p.getContent(), p.getFileName());
                }
            }
            email.setAttachements(files);
        } else {
            email.setContent(message.getContent().toString());
        }
        return email;
    }

    public void testConnection() throws NoSuchProviderException, MessagingException {
        Store lstore = session.getStore("imaps");
        lstore.connect(acc.getImapServer(), acc.getUserName(), acc.getUserPwd());
    }

    public void initStore() throws MessagingException {
        store = session.getStore("imaps");
        store.connect(acc.getImapServer(), acc.getUserName(), acc.getUserPwd());
    }

    public String getAuthor() {
        Folder emailFolder = null;
        String author = null;
        try {
            emailFolder = store.getFolder("Sent");
            emailFolder.open(Folder.READ_ONLY);
            Message message = emailFolder.getMessage(1);
            author = InternetAddress.toString(message.getFrom());
            emailFolder.close(false);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return author;
    }

    public void closeStore() throws MessagingException {
        store.close();
    }

    public EmailMessage[] getNewMessages(String folder) throws NoSuchProviderException, MessagingException, IOException {
        int newCount;
        //Store store = session.getStore("imaps");
        //store.connect(acc.getImapServer(), acc.getUserName(), acc.getUserPwd());
        Folder emailFolder = store.getFolder(folder);
        emailFolder.open(Folder.READ_ONLY);
        newCount = emailFolder.getNewMessageCount();
        if (newCount == 0) {
            return null;
        }
        Message[] messages = emailFolder.getMessages(1, newCount);
        EmailMessage[] emails = new EmailMessage[messages.length];
        EmailMessage email;
        Message message;
        for (int i = 0; i < messages.length; i++) {
            email = new EmailMessage();
            message = messages[i];
            email.setAuthor(Arrays.toString(message.getFrom()));
            email.setTo(Arrays.toString(message.getRecipients(Message.RecipientType.TO)));
            email.setReceived(message.getReceivedDate());
            email.setSent(message.getSentDate());
            email.setSubject(message.getSubject());
            email.setFolder(message.getFolder().getName());
            email.setRead(message.isSet(Flags.Flag.SEEN));
            if (message.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) message.getContent();
                Part p;
                String[] files = new String[mp.getCount() - 1];
                int fc = 0;
                for (int j = 0; j < mp.getCount(); j++) {
                    p = mp.getBodyPart(j);
                    if (p.isMimeType("text/plain")) {
                        email.setContent(p.getContent().toString());
                    }
                    if (p.isMimeType("message/rfc822")) {
                        email.setContent("\nNested message:\n" + p.getContent().toString());
                    }
                    if (p.getFileName() != null) {
                        files[fc++] = p.getFileName();
                        saveFile(p.getContent(), p.getFileName());
                    }
                }
                email.setAttachements(files);
            } else {
                email.setContent(message.getContent().toString());
            }
            emails[i] = email;
        }
        emailFolder.close(false);
        store.close();
        return emails;
    }

    public String[] getFoldersNames() throws MessagingException {
        Folder[] folders = getFolders();
        String[] names = new String[folders.length];
        for (int i = 0; i < folders.length; i++) {
            names[i] = folders[i].getName();
        }
        return names;
    }

    private Folder[] getFolders() throws MessagingException {
        //Store store = session.getStore("imaps");
        //store.connect(acc.getImapServer(), acc.getUserName(), acc.getUserPwd());
        Folder[] folders = store.getDefaultFolder().list();
        store.close();
        return folders;
    }

    public boolean renameFolder(String folder) throws MessagingException {
        boolean renamed;
        //Store store = session.getStore("imaps");
        //store.connect(acc.getImapServer(), acc.getUserName(), acc.getUserPwd());
        Folder emailFolder = store.getFolder(folder);
        renamed = emailFolder.renameTo(emailFolder);
        store.close();
        return renamed;
    }

    public boolean deleteFolder(String folder) throws NoSuchProviderException, MessagingException {
        boolean deleted;
        //Store store = session.getStore("imaps");
        //store.connect(acc.getImapServer(), acc.getUserName(), acc.getUserPwd());
        Folder emailFolder = store.getFolder(folder);
        deleted = emailFolder.delete(true);
        store.close();
        return deleted;
    }

    private void saveFile(Object data, String filename) throws IOException {
        FileOutputStream file = new FileOutputStream(acc.getDwnFolder() + filename);
        if(data instanceof String) {
            file.write(((String) data).getBytes());
        } else {
            InputStream x = (InputStream) data;
            int i;
            while ((i = x.read()) != -1) {
                file.write(i);
            }
        }
    }
}
