package sk.fejero.emconnect.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import javax.mail.MessagingException;

import sk.fejero.emconnect.EmailActivity;
import sk.fejero.emconnect.mailclient.AccountSettings;
import sk.fejero.emconnect.mailclient.EmailMessage;
import sk.fejero.emconnect.mailclient.incomming.ImapClient;
import sk.fejero.emconnect.mailclient.outcomming.SmtpClient;

/**
 * Created by Rastislav on 9.12.2014.
 */
public class SendMessageTask extends AsyncTask<EmailMessage, Integer , Boolean[]> {

    private EmailActivity activity;
    private SmtpClient client;
    private ProgressDialog progressDialog;
    private String[] error;

    public SendMessageTask(EmailActivity activity, SmtpClient client) {
        this.activity = activity;
        this.client = client;
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected Boolean[] doInBackground(EmailMessage... params) {
        Boolean[] sent;
        int numOfMsgs = params.length;
        sent = new Boolean[numOfMsgs];
        error = new String[numOfMsgs];
        for (int i = 0; i < numOfMsgs; i++) {
            try {
                sent[i] = false;
                if(activity.isOffline()) {
                    //TO-DO ulozit maily na odoslanie neskor
                } else {
                    client.sendMessage(params[i]);
                }
                sent[i] = true;
                publishProgress((int) ((i / (float) numOfMsgs) * 100));
            } catch (MessagingException e) {
                e.printStackTrace();
                error[i] = e.getMessage();
            }
        }
        return sent;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Sending messages...");
        progressDialog.setCancelable(false);
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean[] sent) {
        super.onPostExecute(sent);
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        StringBuilder message = new StringBuilder();
        if(activity.isOffline()) {
            message.append("Message/s saved for later sending.");
        } else {
            for(int i = 0; i < sent.length; i++){
                message.append("Message "+(i+1));
                if (sent[i]) {
                    message.append(" sent\n");
                } else {
                    message.append(error[i]);
                }
            }
        }
        Context context = activity.getApplicationContext();

        Toast toast;
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
