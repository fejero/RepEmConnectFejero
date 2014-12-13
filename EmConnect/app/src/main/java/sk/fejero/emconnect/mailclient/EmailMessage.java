/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.fejero.emconnect.mailclient;

import java.util.Date;

/**
 *
 * @author Rastislav
 */
public class EmailMessage {
    
    private String author;
    private String to;
    private String cc;
    private Date sent;
    private Date received;
    private String subject;
    private String content;
    private String folder;
    private String[] attachements;
    private boolean read;

    public EmailMessage() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getAttachements() {
        return attachements;
    }

    public void setAttachements(String[] attachements) {
        this.attachements = attachements;
    }

    public Date getSent() {
        return sent;
    }

    public void setSent(Date sent) {
        this.sent = sent;
    }

    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
    
    public void clear() {
        attachements = null;
        author = null;
        cc = null;
        content = null;
        received = null;
        sent = null;
        subject = null;
        to = null;
    }
}
