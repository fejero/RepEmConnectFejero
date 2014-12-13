package sk.fejero.emconnect.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Date;

import sk.fejero.emconnect.EmailActivity;
import sk.fejero.emconnect.R;
import sk.fejero.emconnect.asynctasks.SendMessageTask;
import sk.fejero.emconnect.mailclient.EmailMessage;
import sk.fejero.emconnect.mailclient.outcomming.SmtpClient;
import sk.fejero.emconnect.management.ContainerManagement;
import sk.fejero.emconnect.management.DataLoader;

/**
 * Created by fejero on 23.10.2014.
 */
public class NewMessageSectionFragment extends Fragment {

    private EditText addressEditText;
    private EditText ccEditText;
    private EditText subjectEditText;
    private EditText contentEditText;
    private ImageButton emailSendButton;
    private ImageButton emailSaveButton;
    private ImageButton attachementButton;
    private DataLoader loader;
    private ContainerManagement cm;
    private SmtpClient smtpClient;

    //private NewMessageModel model;


    public NewMessageSectionFragment() {

    }

    public void loadModel(DataLoader loader, ContainerManagement cm, SmtpClient smtpClient){
        //this.model = model;
        this.loader = loader;
        this.cm = cm;
        this.smtpClient = smtpClient;
    }

    public void setTempMessage(EmailMessage message) {
        Log.i("Message", "Vykonava sa zapis");
        //EditText x = (EditText)rootView.findViewById(R.id.address_edit_text);
        String add = message.getTo();
        if(add != null) {
            addressEditText.setHint(add);
        }
        String sub = message.getSubject();
        if(sub != null) {
            subjectEditText.setHint(sub);
        }
        String con = message.getContent();
        if(con != null) {
            contentEditText.setHint(con);
        }
        cm.setTempMessage(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_message, container, false);
        Log.i("New Message", "New message section started");
        addressEditText = (EditText)rootView.findViewById(R.id.address_edit_text);
        ccEditText = (EditText)rootView.findViewById(R.id.cc_edit_text);
        subjectEditText = (EditText)rootView.findViewById(R.id.subject_edit_text);
        contentEditText = (EditText)rootView.findViewById(R.id.content_edit_text);
        emailSendButton = (ImageButton)rootView.findViewById(R.id.email_send_button);
        emailSaveButton = (ImageButton)rootView.findViewById(R.id.email_save_button);
        attachementButton = (ImageButton)rootView.findViewById(R.id.attachements_button);

                addressEditText.setHint("");


                subjectEditText.setHint("");


                contentEditText.setHint("");

        emailSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailMessage message = new EmailMessage();
                message.setTo(addressEditText.getText().toString());
                message.setSubject(subjectEditText.getText().toString());
                message.setCc(ccEditText.getText().toString());
                message.setContent(contentEditText.getText().toString());
                new SendMessageTask((EmailActivity)NewMessageSectionFragment.this.getActivity(), smtpClient).execute(message);
            }
        });

        attachementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //model.loadAttachement();
            }
        });

        emailSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailMessage message = new EmailMessage();
                message.setTo(addressEditText.getText().toString());
                message.setSubject(subjectEditText.getText().toString());
                message.setCc(ccEditText.getText().toString());
                message.setContent(contentEditText.getText().toString());
                message.setSent(new Date());
                cm.addConceptMessage(message);
            }
        });

        return rootView;
    }
}
