package sk.fejero.emconnect.management;

import java.util.ArrayList;
import java.util.List;

import sk.fejero.emconnect.mailclient.EmailMessage;

/**
 * Created by fejero on 5.11.2014.
 */
public class ContainerManagement {

    private List<EmailMessage> inboxMessageList = new ArrayList<EmailMessage>();
    private List<EmailMessage> sentMessageList = new ArrayList<EmailMessage>();
    private List<EmailMessage> trashMessageList = new ArrayList<EmailMessage>();
    private List<EmailMessage> spamMessageList = new ArrayList<EmailMessage>();
    private List<EmailMessage> conceptMessageList = new ArrayList<EmailMessage>();
    //private List<Account> accountList = new ArrayList<Account>();
    //private Account currentAccount;
    private EmailMessage tempMessage=null;

    public void setInboxMessageList(List<EmailMessage> inboxMessageList) {
        this.inboxMessageList = inboxMessageList;
    }

    public void setSentMessageList(List<EmailMessage> sentMessageList) {
        this.sentMessageList = sentMessageList;
    }

    public void setTrashMessageList(List<EmailMessage> trashMessageList) {
        this.trashMessageList = trashMessageList;
    }

    public void setTempMessage(EmailMessage message){

        this.tempMessage = message;
        //Log.i("Set temp", tempMessage.getAddress());
    }

    public EmailMessage getTempMessage(){
        //Log.i("Get temp", tempMessage.getAddress());
        return tempMessage;
    }

    public void addTrashMessage(EmailMessage trashMessage){
        trashMessageList.add(trashMessage);
    }



    public boolean removeTrashInboxMessage(int i){
        if(i<trashMessageList.size()) {
            trashMessageList.remove(i);
            return true;
        }
        else{
            return false;
        }

    }

    public void addSpamMessage(EmailMessage spamMessage){
        spamMessageList.add(spamMessage);
    }

    public boolean removeSpamMessage(int i){
        if(i<spamMessageList.size()) {
            spamMessageList.remove(i);
            return true;
        }
        else{
            return false;
        }
    }

    public void addConceptMessage(EmailMessage conceptmessage){
        conceptMessageList.add(conceptmessage);
    }

    public boolean removeConceptMessage(int i){
        if(i<conceptMessageList.size()) {
            conceptMessageList.remove(i);
            return true;
        }
        else{
            return false;
        }
    }

    public void addInboxMessage(EmailMessage inboxMessage){
        inboxMessageList.add(inboxMessage);
    }

    public boolean removeInboxMessage(int i){
        if(i<inboxMessageList.size()) {
            inboxMessageList.remove(i);
            return true;
        }
        else{
            return false;
        }

    }

    public void addSentMessage(EmailMessage sentMessage){
        sentMessageList.add(sentMessage);
    }

    public boolean removeSentMessage(int i){
        if(i<sentMessageList.size()) {
            sentMessageList.remove(i);
            return true;
        }
        else{
            return false;
        }

    }

    /*public void addAccount(Account account){
        accountList.add(account);
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public boolean removeAccount(Account a){
        return accountList.remove(a);

    }*/

    public List<EmailMessage> getInboxMessageList() {
        return inboxMessageList;
    }

    public List<EmailMessage> getSentMessageList() {
        return sentMessageList;
    }

    public List<EmailMessage> getTrashMessageList() {
        return trashMessageList;
    }

    public List<EmailMessage> getConceptMessageList() {
        return conceptMessageList;
    }

    public List<EmailMessage> getSpamMessageList() {
        return spamMessageList;
    }
}
