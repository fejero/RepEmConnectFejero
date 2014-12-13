package sk.fejero.emconnect.management;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import sk.fejero.emconnect.mailclient.EmailMessage;


/**
 * Created by fejero on 6.11.2014.
 */
public class DataLoader {

    //private ContainerManagement cm;
    private String folder;

    public DataLoader(String folder) {
        this.folder = folder;
    }

    public void loadAllFolders(ContainerManagement cm) {
        loadInbox(cm);
        loadSent(cm);
        loadTrash(cm);
    }

    /*public void loadAccounts(ContainerManagement cm){
        cm.getAccountList().clear();
        Account account;

        account = new Account("vladimir.fejercak@gmail.com", AccountType.GMAIL);
        cm.addAccount(account);

        account = new Account("vladimir.fejercak@student.tuke.sk",AccountType.TUKE);
        cm.addAccount(account);
    }

    public Account loadCurrentAccount(ContainerManagement cm) {
        return cm.getAccountList().get(0);
    }*/

    private List<EmailMessage> readEmails(String dir) {
        List<EmailMessage> messages = null;
        try {
            FileInputStream filein = new FileInputStream(folder+dir+".cmm");
            ObjectInputStream oin = new ObjectInputStream(filein);
            Object input =  oin.readObject();
            if(input instanceof List) {
                messages = (List<EmailMessage>)input;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public void loadInbox(ContainerManagement cm){
        List<EmailMessage> messages;
        messages = readEmails("inbox");
        if(messages != null) {
            cm.setInboxMessageList(messages);
        }
    }

    public void loadSpam(ContainerManagement cm){
        /*List<EmailMessage> messages;
        messages = readEmails("spam");
        if(messages != null) {
            cm.set(messages);
        }*/
    }

    public void loadTrash(ContainerManagement cm){
        List<EmailMessage> messages;
        messages = readEmails("trash");
        if(messages != null) {
            cm.setTrashMessageList(messages);
        }

    }

    public void loadSent(ContainerManagement cm){
        List<EmailMessage> messages;
        messages = readEmails("sent");
        if(messages != null) {
            cm.setSentMessageList(messages);
        }
    }

    public void loadConcepts(ContainerManagement cm){
        /*cm.getConceptMessageList().clear();
        for (int i = 0; i < 1; i++) {
            ConceptMessage im = new ConceptMessage(new Date(), "fejero@fejero.com", "Concept", "", "Hello, I would like to bla bla bla...");
            cm.addConceptMessage(im);
        }*/
    }
}
