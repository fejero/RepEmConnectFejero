/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.fejero.emconnect.mailclient;

import java.io.Serializable;

/**
 *
 * @author Rastislav
 */
public class AccountSettings implements Serializable{
    
    private String userName;
    private String userPwd;
    private String smtpServer;
    private String imapServer;
    private String dwnFolder;
    private int smtpPort;
    private int imapPort;
    private int storeMails;
    private String author;

    public AccountSettings(String userName, String userPwd) {
        this.userName = userName;
        this.userPwd = userPwd;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public String getImapServer() {
        return imapServer;
    }

    public void setImapServer(String imapServer) {
        this.imapServer = imapServer;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public int getImapPort() {
        return imapPort;
    }

    public void setImapPort(int imapPort) {
        this.imapPort = imapPort;
    }

    public int getStoreMails() {
        return storeMails;
    }

    public void setStoreMails(int storeMails) {
        this.storeMails = storeMails;
    }

    public String getDwnFolder() {
        return dwnFolder;
    }

    public void setDwnFolder(String dwnFolder) {
        this.dwnFolder = dwnFolder;
    }
}
